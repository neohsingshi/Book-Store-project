package com.laboratory.panel.useradmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateUserDialog extends JDialog implements ActionListener {
    private JTextField nameField, identifierField, phoneField, emailField, ageField;
    private JComboBox<String> genderBox, identityBox;
    private JButton updateButton, cancelButton;
    private int userId;

    public UpdateUserDialog(JFrame owner, int userId) {
        super(owner, "Update User", true);
        this.userId = userId;
        setLayout(new BorderLayout());

        // Initialize form with English labels
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(7, 2));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[]{"Male", "Female"});
        formPanel.add(genderBox);

        formPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        formPanel.add(ageField);

        formPanel.add(new JLabel("Identity:"));
        // Only allow 'User' and 'Admin' as options for identity
        identityBox = new JComboBox<>(new String[]{"User", "Admin"});
        formPanel.add(identityBox);

        formPanel.add(new JLabel("ID Number:"));
        identifierField = new JTextField();
        identifierField.setEditable(false); // Make ID number non-editable
        formPanel.add(identifierField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        add(formPanel, BorderLayout.CENTER);

        // Button panel with English button text
        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("Update");
        cancelButton = new JButton("Cancel");

        updateButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadUserDetails(); // Load user details

        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    private void loadUserDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE id = ?")) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                genderBox.setSelectedItem(rs.getString("gender"));
                ageField.setText(String.valueOf(rs.getInt("age")));

                // Ensure the loaded identity is either "User" or "Admin"
                String loadedIdentity = rs.getString("identity");
                if ("User".equals(loadedIdentity) || "Admin".equals(loadedIdentity)) {
                    identityBox.setSelectedItem(loadedIdentity);
                } else {
                    // If the loaded identity is not "User" or "Admin", select "User" by default
                    identityBox.setSelectedIndex(0);
                }

                identifierField.setText(rs.getString("identifier"));
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showMessageDialog("Failed to load user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Update".equals(e.getActionCommand())) {
            updateUser();
            dispose(); // Close the dialog
        } else if ("Cancel".equals(e.getActionCommand())) {
            dispose(); // Close the dialog
        }
    }

    private void updateUser() {
        String name = nameField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            showMessageDialog("Please enter a valid age!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String identity = (String) identityBox.getSelectedItem();
        String identifier = identifierField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE users SET name=?, gender=?, age=?, identity=?, identifier=?, phone=?, email=? WHERE id=?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setInt(3, age);
            pstmt.setString(4, identity);
            pstmt.setString(5, identifier);
            pstmt.setString(6, phone);
            pstmt.setString(7, email);
            pstmt.setInt(8, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showMessageDialog("User information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessageDialog("No changes were made.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showMessageDialog("Failed to update user information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessageDialog(String message, String title, int messageType) {
        Object[] options = {"OK"}; // Use "OK" instead of "ok"
        JOptionPane.showOptionDialog(null,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                messageType,
                null, // Do not use a custom Icon
                options, // Button text array
                options[0]); // Default button to focus
    }
}
