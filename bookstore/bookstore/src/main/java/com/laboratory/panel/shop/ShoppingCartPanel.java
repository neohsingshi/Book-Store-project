package com.laboratory.panel.shop;

import com.laboratory.MainFrame;
import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartPanel extends JPanel implements ActionListener {
    private DefaultTableModel tableModel;
    private JTable cartTable;
    private JButton checkoutButton;
    private JLabel totalPriceLabel;
    private Map<Integer, CartItem> cartItems = new HashMap<>();
    private MainFrame mainFrame;
    private String userIdentifier;
    private BookManagementPanel bookManagementPanel; // Adding a reference to BookManagementPanel

    public ShoppingCartPanel(MainFrame mainFrame, String userIdentifier, BookManagementPanel bookManagementPanel) {
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        this.bookManagementPanel = bookManagementPanel;
        setLayout(new BorderLayout());

        // Initializing Forms
        tableModel = new DefaultTableModel(new Object[]{"reputation as calligrapher", "prices", "quantities", "Subtotal"}, 0);
        cartTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        // Total price tag
        totalPriceLabel = new JLabel("total price: 0.00 RM");
        add(totalPriceLabel, BorderLayout.SOUTH);

        // Checkout button
        checkoutButton = new JButton("pay the bill");
        checkoutButton.addActionListener(this);
        add(checkoutButton, BorderLayout.NORTH);
    }

    public void addToCart(int bookId, String title, double price) {
        CartItem item = cartItems.getOrDefault(bookId, new CartItem(bookId, title, price, 0));
        item.quantity++;
        cartItems.put(bookId, item);

        updateCartDisplay();
    }

    private void updateCartDisplay() {
        SwingUtilities.invokeLater(() -> { // Ensure UI updates are performed in EDT
            tableModel.setRowCount(0); // Emptying existing data
            double totalPrice = 0.0;

            for (CartItem item : cartItems.values()) {
                double lineTotal = item.price * item.quantity;
                tableModel.addRow(new Object[]{item.title, item.price, item.quantity, lineTotal});
                totalPrice += lineTotal;
            }

            totalPriceLabel.setText(String.format("total price: %.2f RM", totalPrice));
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("pay the bill".equals(e.getActionCommand())) {
            performCheckout();
        }
    }

    private void performCheckout() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Commencement of business

            // Check that each item is adequately stocked
            for (CartItem item : cartItems.values()) {
                if (!checkStockAndValidity(conn, item.bookId, item.quantity)) {
                    JOptionPane.showMessageDialog(null, "Book ID：" + item.bookId + " Insufficient or ineffective inventory！");
                    conn.rollback(); // Rolling back transactions
                    return; // Stopping the closing process
                }
            }

            // Update inventory and generate order records
            for (CartItem item : cartItems.values()) {
                updateStock(conn, item.bookId, item.quantity);
                generateOrderRecord(conn, item.bookId, item.quantity);
            }

            conn.commit(); // Submission of transactions
            JOptionPane.showMessageDialog(null, "Checkout Successful！");
            clearCart();

            // Trigger a data refresh in the book management panel
            if (bookManagementPanel != null) {
                bookManagementPanel.loadBooks();
            }
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rolling back transactions
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Checkout Failure：" + ex.getMessage(), "incorrect", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore default commit mode
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private boolean checkStockAndValidity(Connection conn, int bookId, int quantity) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT stock FROM books WHERE id = ? AND stock >= ?")) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, quantity);

            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // If the query returns a result, the inventory is sufficient and the book ID is valid
        }
    }

    private void updateStock(Connection conn, int bookId, int quantity) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE books SET stock = stock - ? WHERE id = ? AND stock >= ?")) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, quantity);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Book ID：" + bookId + " Insufficient or ineffective inventory");
            }
        }
    }

    private void generateOrderRecord(Connection conn, int bookId, int quantity) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO orders (book_id, quantity, user_identifier) VALUES (?, ?, ?)")) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, userIdentifier);

            pstmt.executeUpdate();
        }
    }

    private void clearCart() {
        cartItems.clear();
        updateCartDisplay();
    }

    private static class CartItem {
        private final int bookId;
        private final String title;
        private final double price;
        private int quantity;

        public CartItem(int bookId, String title, double price, int quantity) {
            this.bookId = bookId;
            this.title = title;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
