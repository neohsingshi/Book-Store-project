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

public class RegisterPanel extends JPanel {
    private JTextField nameField;
    private JComboBox<String> identityComboBox;
    private JTextField identifierField; // ID number field
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton; // Back button
    private JLabel messageLabel;
    private MainFrame mainFrame;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.decode("#f0f4f8")); // Set background color

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Add component spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font globalFont = new Font("Arial", Font.PLAIN, 16); // Changed font to Arial for English text
        Font titleFont = globalFont.deriveFont(Font.BOLD, 24);

        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.decode("#333333"));

        JLabel nameLabel = createStyledLabel("Name:");
        nameField = createStyledTextField();

        JLabel identityLabel = createStyledLabel("Identity:");
        String[] identities = {"User", "Admin"}; // Identity options
        identityComboBox = createStyledComboBox(identities);

        JLabel identifierLabel = createStyledLabel("ID Number:"); // ID number label
        identifierField = createStyledTextField();           // ID number input field

        JLabel phoneLabel = createStyledLabel("Phone:");
        phoneField = createStyledTextField();

        JLabel emailLabel = createStyledLabel("Email:");
        emailField = createStyledTextField();

        JLabel passwordLabel = createStyledLabel("Password:");
        passwordField = createStyledPasswordField();

        JLabel confirmPasswordLabel = createStyledLabel("Confirm Password:");
        confirmPasswordField = createStyledPasswordField();

        registerButton = createStyledButton("Register Account");
        backButton = createStyledButton("Back to Login"); // Create back button
        messageLabel = createStyledMessageLabel("");

        // Add components to the panel with increased height
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        int row = 1;
        addRow(gbc, nameLabel, nameField, row++);
        addRow(gbc, identityLabel, identityComboBox, row++);
        addRow(gbc, identifierLabel, identifierField, row++); // Add ID number row
        addRow(gbc, phoneLabel, phoneField, row++);
        addRow(gbc, emailLabel, emailField, row++);
        addRow(gbc, passwordLabel, passwordField, row++);
        addRow(gbc, confirmPasswordLabel, confirmPasswordField, row++);

        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        gbc.gridy = row++;
        add(backButton, gbc); // Add back button

        gbc.gridy = row;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);

        // Add action listener to the register button
        registerButton.addActionListener(new RegisterButtonListener());

        // Add action listener to the back button
        backButton.addActionListener(e -> {
            clearFields(); // Clear all data in text fields
            CardLayout cl = (CardLayout)(getParent().getLayout());
            if (cl != null) {
                cl.show(getParent(), "LOGIN_PANEL"); // Assume login panel name is "LOGIN_PANEL"
            }
        });

        // Set different colors for buttons
        registerButton.setBackground(Color.decode("#FF9800"));
        backButton.setBackground(Color.decode("#3F51B5"));
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16)); // Changed font to Arial for English text
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 16)); // Changed font to Arial for English text
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16)); // Changed font to Arial for English text
        return comboBox;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16)); // Changed font to Arial for English text
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16)); // Changed font to Arial for English text
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private JLabel createStyledMessageLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14)); // Changed font to Arial for English text
        label.setForeground(Color.RED);
        label.setVisible(true); // Ensure message label is visible
        return label;
    }

    private void addRow(GridBagConstraints gbc, Component leftComponent, Component rightComponent, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        add(leftComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        add(rightComponent, gbc);
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearMessage(); // Clear any previous messages

            String name = nameField.getText().trim();
            String identity = (String) identityComboBox.getSelectedItem();
            String identifier = identifierField.getText().trim(); // Get ID number
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (name.isEmpty() || identity == null || identifier.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                setMessage("All fields are required!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                setMessage("Passwords do not match!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Check if ID number or name already exists in database
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM users WHERE identifier = ? OR name = ?")) {
                    checkStmt.setString(1, identifier);
                    checkStmt.setString(2, name);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            setMessage("This ID number or name already exists, please choose a different ID number or name.");
                            return;
                        }
                    }
                }

                // Insert new user into database without hashing the password
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO users (name, identity, identifier, phone, email, password) VALUES (?, ?, ?, ?, ?, ?)")) {

                    pstmt.setString(1, name);
                    pstmt.setString(2, identity);
                    pstmt.setString(3, identifier);
                    pstmt.setString(4, phone);
                    pstmt.setString(5, email);
                    pstmt.setString(6, password); // Use plain text password directly

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        Object[] options = {"OK"};
                        int result = JOptionPane.showOptionDialog(
                                null,
                                "Registration successful!",
                                "Info",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null, // No custom icon
                                options, // The options array
                                options[0] // Default option
                        );
                        clearFields(); // Clear text fields
                        CardLayout cl = (CardLayout)(getParent().getLayout());
                        if (cl != null) {
                            cl.show(getParent(), "LOGIN_PANEL"); // Assume login panel name is "LOGIN_PANEL"
                        }
                    } else {
                        setMessage("Registration failed, please try again.");
                    }
                }
            } catch (SQLException ex) {
                setMessage("Database error, please try again later.");
                ex.printStackTrace();
            }

            messageLabel.repaint(); // Force refresh interface
        }

        private void setMessage(final String message) {
            SwingUtilities.invokeLater(() -> {
                messageLabel.setText(message);
                messageLabel.setVisible(true); // Ensure message label is visible
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        identityComboBox.setSelectedIndex(0);
        identifierField.setText(""); // Clear ID number input field
        phoneField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void clearMessage() {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setText("");
            messageLabel.setVisible(false); // Hide message label
        });
    }
}
