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
    private BookManagementPanel bookManagementPanel; // 添加对 BookManagementPanel 的引用

    public ShoppingCartPanel(MainFrame mainFrame, String userIdentifier, BookManagementPanel bookManagementPanel) {
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        this.bookManagementPanel = bookManagementPanel;
        setLayout(new BorderLayout());

        // 初始化表格
        tableModel = new DefaultTableModel(new Object[]{"书名", "价格", "数量", "小计"}, 0);
        cartTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        // 总价标签
        totalPriceLabel = new JLabel("总价: 0.00 元");
        add(totalPriceLabel, BorderLayout.SOUTH);

        // 结账按钮
        checkoutButton = new JButton("结账");
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
        SwingUtilities.invokeLater(() -> { // 确保UI更新在EDT中执行
            tableModel.setRowCount(0); // 清空现有数据
            double totalPrice = 0.0;

            for (CartItem item : cartItems.values()) {
                double lineTotal = item.price * item.quantity;
                tableModel.addRow(new Object[]{item.title, item.price, item.quantity, lineTotal});
                totalPrice += lineTotal;
            }

            totalPriceLabel.setText(String.format("总价: %.2f 元", totalPrice));
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("结账".equals(e.getActionCommand())) {
            performCheckout();
        }
    }

    private void performCheckout() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 开始事务

            // 检查每项商品的库存是否充足
            for (CartItem item : cartItems.values()) {
                if (!checkStockAndValidity(conn, item.bookId, item.quantity)) {
                    JOptionPane.showMessageDialog(null, "书籍ID：" + item.bookId + " 库存不足或无效！");
                    conn.rollback(); // 回滚事务
                    return; // 停止结账流程
                }
            }

            // 更新库存并生成订单记录
            for (CartItem item : cartItems.values()) {
                updateStock(conn, item.bookId, item.quantity);
                generateOrderRecord(conn, item.bookId, item.quantity);
            }

            conn.commit(); // 提交事务
            JOptionPane.showMessageDialog(null, "结账成功！");
            clearCart();

            // 触发书籍管理面板的数据刷新
            if (bookManagementPanel != null) {
                bookManagementPanel.loadBooks();
            }
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "结账失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复默认提交模式
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

            return rs.next(); // 如果查询返回结果，则表示库存充足且书籍ID有效
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
                throw new SQLException("书籍ID：" + bookId + " 库存不足或无效");
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
