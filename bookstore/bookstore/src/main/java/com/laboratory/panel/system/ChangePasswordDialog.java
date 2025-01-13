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
    private String userIdentifier; // User identifier (e.g., student or faculty number)

    public ChangePasswordDialog(MainFrame mainFrame, String title, boolean modal, String userIdentifier) {
        super((JFrame)null, title, modal);
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        initializeComponents();
        setLocationRelativeTo(null); // Center the dialog on screen
        setResizable(false); // Prohibit users from resizing dialog boxes
        pack(); // Resize the dialog according to the optimal size of the component
        setMinimumSize(new Dimension(300, 200)); // Setting the minimum size
        setPreferredSize(new Dimension(400, 250)); // Setting the preferred size
        pack(); // Re-adjustment to ensure optimal size
    }

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel oldPasswordLabel = new JLabel("old password:");
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

        JLabel newPasswordLabel = new JLabel("new password:");
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

        JLabel confirmPasswordLabel = new JLabel("Confirm new password:");
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

        JButton saveButton = new JButton("preservation");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    if (updatePassword()) {
                        JOptionPane.showMessageDialog(ChangePasswordDialog.this, "Password changed successfully！");
                        dispose(); // Close dialog box
                    } else {
                        messageLabel.setText("Password change failed, please try again later！");
                    }
                } else {
                    messageLabel.setText("Password change failed, please check the input！");
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

        // Checks if all fields are non-null
        if (oldPassword.length == 0 || newPassword.length == 0 || confirmPassword.length == 0) {
            messageLabel.setText("All fields cannot be null！");
            return false;
        }

        // Verify that the new password is the same
        if (!new String(newPassword).equals(new String(confirmPassword))) {
            messageLabel.setText("Inconsistency between the new password and the confirmation password！");
            return false;
        }

        // Verify that the old password is correct
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE identifier = ?")) {

            pstmt.setString(1, userIdentifier);
            System.out.println("Executing query: SELECT password FROM users WHERE identifier = '" + userIdentifier + "'");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Direct comparison of plaintext passwords
                    String storedPassword = rs.getString("password");
                    System.out.println("Stored Password: " + storedPassword);
                    System.out.println("Old Password Input: " + new String(oldPassword));

                    if (new String(oldPassword).equals(storedPassword)) {
                        return true;
                    } else {
                        messageLabel.setText("Old password error！");
                        System.out.println("Password mismatch.");
                        return false;
                    }
                } else {
                    messageLabel.setText("The user does not exist！");
                    System.out.println("User not found for identifier: " + userIdentifier);
                    return false;
                }
            }
        } catch (SQLException ex) {
            messageLabel.setText("Database error, please try again later。");
            ex.printStackTrace();
            System.out.println("Database error occurred: " + ex.getMessage());
            return false;
        }
    }

    private boolean updatePassword() {
        char[] newPassword = newPasswordField.getPassword();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE identifier = ?");
            pstmt.setString(1, new String(newPassword)); // Storing plaintext passwords
            pstmt.setString(2, userIdentifier);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
