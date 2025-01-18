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
        super(owner, "Update Book", true);
        this.bookId = bookId;
        initUI();
        loadBookDetails(bookId);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Title:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("Stock:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("Category:"));
        categoryBox = new JComboBox<>(new String[]{"Novel", "Science Fiction", "History", "Programming", "Other"});
        add(categoryBox);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this::onOK);
        add(okButton);

        JButton cancelButton = new JButton("Cancel");
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
            // Ensure error messages are also in English
            Object[] options = {"OK"};
            JOptionPane.showOptionDialog(
                    this,
                    "Failed to load book details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, // no custom icon
                    options, // option buttons
                    options[0] // default button
            );
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

                // Ensure the message dialog and button text are in English
                Object[] options = {"OK"};
                JOptionPane.showOptionDialog(
                        this,
                        "Book updated successfully!",
                        "Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null, // no custom icon
                        options, // option buttons
                        options[0] // default button
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Ensure error messages are also in English
            Object[] options = {"OK"};
            JOptionPane.showOptionDialog(
                    this,
                    "Update failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, // no custom icon
                    options, // option buttons
                    options[0] // default button
            );
        } finally {
            dispose();
        }
    }
}
