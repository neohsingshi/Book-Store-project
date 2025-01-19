package com.laboratory.panel;

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

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private MainFrame mainFrame;  // Reference to the main window instance for panel switching

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.decode("#f0f4f8"));  // Set background color

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font globalFont = new Font("Arial", Font.PLAIN, 16);  // Set global font to a common English font

        // Initialize UI components and set styles
        JLabel titleLabel = new JLabel("Bookstore", SwingConstants.CENTER);
        titleLabel.setFont(globalFont.deriveFont(Font.BOLD, 24));
        titleLabel.setForeground(Color.decode("#333333"));

        JLabel usernameLabel = new JLabel("Identifier:");
        usernameLabel.setFont(globalFont);
        usernameField = new JTextField(20);
        usernameField.setFont(globalFont);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(globalFont);
        passwordField = new JPasswordField(20);
        passwordField.setFont(globalFont);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(globalFont);
        loginButton.setBackground(Color.decode("#4CAF50"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(globalFont);
        registerButton.setBackground(Color.decode("#FF9800"));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(globalFont);
        messageLabel.setForeground(Color.RED);

        // Add components to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(usernameLabel, gbc);

        gbc.gridy = 2;
        add(usernameField, gbc);

        gbc.gridy = 3;
        add(passwordLabel, gbc);

        gbc.gridy = 4;
        add(passwordField, gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        gbc.gridy = 6;
        add(registerButton, gbc);

        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);

        // Add event listeners to buttons
        loginButton.addActionListener(new LoginButtonListener());
        registerButton.addActionListener(e -> {
            CardLayout cl = (CardLayout)(getParent().getLayout());
            if (cl != null) {
                cl.show(getParent(), "REGISTER_PANEL");
            }
        });
    }

    // Login button click event handler
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String identifier = usernameField.getText().trim();
            char[] passwordChars = passwordField.getPassword();  // Get password input
            String password = new String(passwordChars);

            if (identifier.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Identifier or password cannot be empty!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Query the user's identifier and password
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT password, identity FROM users WHERE identifier = ?")) {

                    pstmt.setString(1, identifier);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String storedPassword = rs.getString("password");
                            String role = rs.getString("identity");  // Get user identity (role)

                            // Directly compare plain text passwords
                            if (password.equals(storedPassword)) {  // Note: Do not do this in production environments!
                                showCustomMessageDialog("Login successful!", "Information");
                                mainFrame.showMainPanel(role, identifier);  // Switch using the original role string and pass the user identifier
                            } else {
                                messageLabel.setText("Incorrect identifier or password!");
                            }
                        } else {
                            messageLabel.setText("Incorrect identifier or password!");
                        }
                    }
                }
            } catch (SQLException ex) {
                messageLabel.setText("Database error, please try again later.");
                ex.printStackTrace();
            }
        }
    }

    private void showCustomMessageDialog(String message, String title) {
        // Create an array with one option: "OK"
        Object[] options = {"OK"};
        // Show a dialog with custom options and ensure that it uses English for "OK"
        JOptionPane.showOptionDialog(null, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
    }
}
