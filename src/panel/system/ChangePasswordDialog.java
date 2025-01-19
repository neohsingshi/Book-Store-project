package com.laboratory.panel.system;

import com.laboratory.MainFrame;
import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;
    private MainFrame mainFrame;
    private String userIdentifier; // User identifier (e.g., student ID or staff ID)

    public ChangePasswordDialog(MainFrame mainFrame, String title, boolean modal, String userIdentifier) {
        super((JFrame)null, title, modal);
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        initializeComponents();
        setLocationRelativeTo(null); // Center the dialog on screen
        setResizable(false); // Prevent users from resizing the dialog
        pack(); // Adjust dialog size based on component's preferred sizes
        setMinimumSize(new Dimension(300, 200)); // Set minimum size
        setPreferredSize(new Dimension(400, 250)); // Set preferred size
        pack(); // Re-adjust to ensure optimal size
    }

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel oldPasswordLabel = new JLabel("Old Password:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(oldPasswordLabel, gbc);

        oldPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(oldPasswordField, gbc);
        gbc.gridwidth = 1;

        JLabel newPasswordLabel = new JLabel("New Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(newPasswordLabel, gbc);

        newPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(newPasswordField, gbc);
        gbc.gridwidth = 1;

        JLabel confirmPasswordLabel = new JLabel("Confirm New Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(confirmPasswordField, gbc);
        gbc.gridwidth = 1;

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    if (updatePassword()) {
                        JOptionPane.showMessageDialog(ChangePasswordDialog.this, "Password changed successfully!");
                        dispose(); // Close the dialog
                    } else {
                        messageLabel.setText("Failed to change password, please try again later!");
                    }
                } else {
                    messageLabel.setText("Password change failed, please check your input!");
                }
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(messageLabel, gbc);
    }

    private boolean validateInput() {
        char[] oldPassword = oldPasswordField.getPassword();
        char[] newPassword = newPasswordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        // Check that all fields are non-empty
        if (oldPassword.length == 0 || newPassword.length == 0 || confirmPassword.length == 0) {
            messageLabel.setText("All fields must be filled out!");
            return false;
        }

        // Verify that the new passwords match
        if (!new String(newPassword).equals(new String(confirmPassword))) {
            messageLabel.setText("New password and confirmation do not match!");
            return false;
        }

        // Verify that the old password is correct
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE identifier = ?")) {

            pstmt.setString(1, userIdentifier);
            System.out.println("Executing query: SELECT password FROM users WHERE identifier = '" + userIdentifier + "'");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Directly compare plain text passwords
                    String storedPassword = rs.getString("password");
                    System.out.println("Stored Password: " + storedPassword);
                    System.out.println("Old Password Input: " + new String(oldPassword));

                    if (new String(oldPassword).equals(storedPassword)) {
                        return true;
                    } else {
                        messageLabel.setText("Incorrect old password!");
                        System.out.println("Password mismatch.");
                        return false;
                    }
                } else {
                    messageLabel.setText("User does not exist!");
                    System.out.println("User not found for identifier: " + userIdentifier);
                    return false;
                }
            }
        } catch (SQLException ex) {
            messageLabel.setText("Database error, please try again later.");
            ex.printStackTrace();
            System.out.println("Database error occurred: " + ex.getMessage());
            return false;
        }
    }

    private boolean updatePassword() {
        char[] newPassword = newPasswordField.getPassword();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE identifier = ?");
            pstmt.setString(1, new String(newPassword)); // Store plain text password
            pstmt.setString(2, userIdentifier);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
