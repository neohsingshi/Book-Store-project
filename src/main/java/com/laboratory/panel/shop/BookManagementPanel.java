package com.laboratory.panel.shop;

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

public class BookManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private ShoppingCartPanel shoppingCartPanel;

    public BookManagementPanel(ShoppingCartPanel shoppingCartPanel) {
        this.shoppingCartPanel = shoppingCartPanel;
        setLayout(new BorderLayout());

        // Initialize table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Price", "Stock"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add button
        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int bookId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    String title = tableModel.getValueAt(selectedRow, 1).toString();
                    double price = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());

                    onBookSelected(bookId, title, price);
                } else {
                    showCustomMessageDialog("Please select a book!", "Information");
                }
            }
        });
        add(addButton, BorderLayout.SOUTH);

        // Set up timer to refresh data every 5 seconds
        Timer timer = new Timer(5000, new ActionListener() { // Refresh every 5 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBooks();
            }
        });
        timer.setInitialDelay(0); // Start the first refresh immediately
        timer.start();

        // Initially load books data
        loadBooks();
    }

    private void showCustomMessageDialog(String message, String title) {
        // Create an array with one option: "OK"
        Object[] options = {"OK"};
        // Show a dialog with custom options and ensure that it uses English for "OK"
        JOptionPane.showOptionDialog(null, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
    }

    void loadBooks() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, title, price, stock FROM books")) {

            ResultSet rs = pstmt.executeQuery();

            // Clear old data from the table
            tableModel.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");

                tableModel.addRow(new Object[]{id, title, price, stock});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCustomMessageDialog("Failed to load books: " + ex.getMessage(), "Error");
        }
    }

    private void onBookSelected(int bookId, String title, double price) {
        if (shoppingCartPanel != null) {
            shoppingCartPanel.addToCart(bookId, title, price);
        }
    }
}
