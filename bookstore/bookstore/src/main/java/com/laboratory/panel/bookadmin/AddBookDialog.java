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
        super(owner, "Add a book", true);
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new GridLayout(5, 2));

        add(new JLabel("stock:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("prices:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("property or cash held in reserve:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("form:"));
        categoryBox = new JComboBox<>(new String[]{"fiction", "sci-fi", "histories", "programmer", "else"}); // 示例类别
        add(categoryBox);

        JButton okButton = new JButton("ok");
        okButton.addActionListener(this::onOK);
        add(okButton);

        JButton cancelButton = new JButton("cancel");
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
                JOptionPane.showMessageDialog(this, "Book added successfully！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Add Failure：" + ex.getMessage(), "incorrect", JOptionPane.ERROR_MESSAGE);
        } finally {
            dispose();
        }
    }
}
