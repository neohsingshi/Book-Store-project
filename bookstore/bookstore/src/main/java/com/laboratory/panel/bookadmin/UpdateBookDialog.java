package com.laboratory.panel.bookadmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateBookDialog extends JDialog {
    private JTextField titleField;
    private JTextField priceField;
    private JTextField stockField;
    private JComboBox<String> categoryBox;
    private int bookId;

    public UpdateBookDialog(JFrame owner, int bookId) {
        super(owner, "更新书籍", true);
        this.bookId = bookId;
        initUI();
        loadBookDetails(bookId);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new GridLayout(5, 2));

        add(new JLabel("书名:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("价格:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("库存:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("类别:"));
        categoryBox = new JComboBox<>(new String[]{"小说", "科幻", "历史", "编程", "其他"});
        add(categoryBox);

        JButton okButton = new JButton("确定");
        okButton.addActionListener(this::onOK);
        add(okButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void loadBookDetails(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                titleField.setText(rs.getString("title"));
                priceField.setText(rs.getBigDecimal("price").toString());
                stockField.setText(Integer.toString(rs.getInt("stock")));
                categoryBox.setSelectedItem(rs.getString("category"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载书籍详情失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onOK(ActionEvent e) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE books SET title=?, price=?, stock=?, category=? WHERE id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, titleField.getText());
                pstmt.setDouble(2, Double.parseDouble(priceField.getText()));
                pstmt.setInt(3, Integer.parseInt(stockField.getText()));
                pstmt.setString(4, (String) categoryBox.getSelectedItem());
                pstmt.setInt(5, bookId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "书籍更新成功！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "更新失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            dispose();
        }
    }
}
