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
        super(owner, "Add Book", true);
        initUI();
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
        categoryBox = new JComboBox<>(new String[]{"Novel", "Science Fiction", "History", "Programming", "Other"}); // Example categories
        add(categoryBox);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this::onOK);
        add(okButton);

        JButton cancelButton = new JButton("Cancel");
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

                // Ensure the message dialog and button text are in English
                Object[] options = {"OK"};
                int result = JOptionPane.showOptionDialog(
                        this,
                        "Book added successfully!",
                        "Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null, // no custom icon
                        options, // option buttons
                        options[0] // default button
                );
                if (result == JOptionPane.OK_OPTION) {
                    dispose(); // Close the dialog after confirmation
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Ensure error messages are also in English
            JOptionPane.showMessageDialog(this, "Add failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
