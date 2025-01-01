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

        // 初始化表格
        tableModel = new DefaultTableModel(new Object[]{"ID", "书名", "价格", "库存"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // 添加按钮
        JButton addButton = new JButton("加入购物车");
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
                    JOptionPane.showMessageDialog(null, "请选择一本书！");
                }
            }
        });
        add(addButton, BorderLayout.SOUTH);

        // 设置定时器，每5秒刷新一次数据
        Timer timer = new Timer(5000, new ActionListener() { // 每5秒刷新一次
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBooks();
            }
        });
        timer.setInitialDelay(0); // 立即开始第一次刷新
        timer.start();

        // 初始加载书籍数据
        loadBooks();
    }

    void loadBooks() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, title, price, stock FROM books")) {

            ResultSet rs = pstmt.executeQuery();

            // 清空表格中的旧数据
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
            JOptionPane.showMessageDialog(null, "加载书籍失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBookSelected(int bookId, String title, double price) {
        if (shoppingCartPanel != null) {
            shoppingCartPanel.addToCart(bookId, title, price);
        }
    }
}
