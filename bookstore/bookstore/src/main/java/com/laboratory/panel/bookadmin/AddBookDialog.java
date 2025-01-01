package com.laboratory.panel.bookadmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddBookDialog extends JDialog {
    private JTextField titleField;
    private JTextField priceField;
    private JTextField stockField;
    private JComboBox<String> categoryBox;

    public AddBookDialog(JFrame owner) {
        super(owner, "添加书籍", true);
        initUI();
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
        categoryBox = new JComboBox<>(new String[]{"小说", "科幻", "历史", "编程", "其他"}); // 示例类别
        add(categoryBox);

        JButton okButton = new JButton("确定");
        okButton.addActionListener(this::onOK);
        add(okButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void onOK(ActionEvent e) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO books (title, price, stock, category) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, titleField.getText());
                pstmt.setDouble(2, Double.parseDouble(priceField.getText()));
                pstmt.setInt(3, Integer.parseInt(stockField.getText()));
                pstmt.setString(4, (String) categoryBox.getSelectedItem());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "书籍添加成功！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "添加失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            dispose();
        }
    }
}
