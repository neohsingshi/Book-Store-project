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
        super(owner, "Updated books", true);
        this.bookId = bookId;
        initUI();
        loadBookDetails(bookId);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new GridLayout(5, 2));

        add(new JLabel("book name:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("stock:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("form:"));
        categoryBox = new JComboBox<>(new String[]{"fiction", "sci-fi", "histories", "programmer", "else"});
        add(categoryBox);

        JButton okButton = new JButton("ok");
        okButton.addActionListener(this::onOK);
        add(okButton);

        JButton cancelButton = new JButton("cancel");
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
            JOptionPane.showMessageDialog(this, "Failed to load book details：" + ex.getMessage(), "incorrect", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Book Update Successful！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "update failure：" + ex.getMessage(), "incorrect", JOptionPane.ERROR_MESSAGE);
        } finally {
            dispose();
        }
    }
}
