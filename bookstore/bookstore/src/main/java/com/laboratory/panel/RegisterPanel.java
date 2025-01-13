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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPanel extends JPanel {
    private JTextField nameField;
    private JComboBox<String> identityComboBox;
    private JTextField identifierField; // Identification number
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton; // Added a back button
    private JLabel messageLabel;
    private MainFrame mainFrame;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.decode("#f0f4f8")); // Set the background color

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Increase component spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font globalFont = new Font("Microsoft YaHei", Font.PLAIN, 16); // Use Microsoft Yahei font
        Font titleFont = globalFont.deriveFont(Font.BOLD, 24);

        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.decode("#333333"));

        JLabel nameLabel = createStyledLabel("name:");
        nameField = createStyledTextField();

        JLabel identityLabel = createStyledLabel("identity:");
        String[] identities = {"user", "administrator"}; // Admin has been added here
        identityComboBox = createStyledComboBox(identities);

        JLabel identifierLabel = createStyledLabel("Identification number:"); // Identification number label
        identifierField = createStyledTextField();           // Identification number input box

        JLabel phoneLabel = createStyledLabel("Phone:");
        phoneField = createStyledTextField();

        JLabel emailLabel = createStyledLabel("mailbox:");
        emailField = createStyledTextField();

        JLabel passwordLabel = createStyledLabel("password:");
        passwordField = createStyledPasswordField();

        JLabel confirmPasswordLabel = createStyledLabel("Confirm your password:");
        confirmPasswordField = createStyledPasswordField();

        registerButton = createStyledButton("Sign up for an account");
        backButton = createStyledButton("Go back to sign in"); // Create a back button
        messageLabel = createStyledMessageLabel("");

        // Add components to the panel with increased height
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        int row = 1;
        addRow(gbc, nameLabel, nameField, row++);
        addRow(gbc, identityLabel, identityComboBox, row++);
        addRow(gbc, identifierLabel, identifierField, row++); // Add the ID number line
        addRow(gbc, phoneLabel, phoneField, row++);
        addRow(gbc, emailLabel, emailField, row++);
        addRow(gbc, passwordLabel, passwordField, row++);
        addRow(gbc, confirmPasswordLabel, confirmPasswordField, row++);

        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        gbc.gridy = row++;
        add(backButton, gbc); // Add a back button

        gbc.gridy = row;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);

        // Add action listener to the register button
        registerButton.addActionListener(new RegisterButtonListener());

        // Add action listener to the back button
        backButton.addActionListener(e -> {
            clearFields(); // Clear all data in the input box
            CardLayout cl = (CardLayout)(getParent().getLayout());
            if (cl != null) {
                cl.show(getParent(), "LOGIN_PANEL"); // Let's say the login panel is named "LOGIN_PANEL"
            }
        });

        // Set different colors for buttons
        registerButton.setBackground(Color.decode("#FF9800"));
        backButton.setBackground(Color.decode("#3F51B5"));
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return comboBox;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private JLabel createStyledMessageLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        label.setForeground(Color.RED);
        label.setVisible(true); // Make sure the message label is visible
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

    private boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validatePhone(String phone) {
        String phoneRegex = "^\\d{11}$"; // Assuming a simple 11-digit number for China
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearMessage(); // Clear any previous messages

            String name = nameField.getText().trim();
            String identity = (String) identityComboBox.getSelectedItem();
            String identifier = identifierField.getText().trim(); // Get an identity number
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (name.isEmpty() || identity == null || identifier.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                setMessage("All fields are mandatory!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                setMessage("The password entered twice is inconsistent!");
                return;
            }

            if (!validatePhone(phone)) {
                setMessage("Please enter a valid mobile phone number!");
                return;
            }

            if (!validateEmail(email)) {
                setMessage("Please enter a valid email address!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Check if ID号 or name already exists in database
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM users WHERE identifier = ? OR name = ?")) {
                    checkStmt.setString(1, identifier);
                    checkStmt.setString(2, name);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            setMessage("The ID number or name already exists, please select a different ID number or name.");
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
                    pstmt.setString(6, password); // Use plaintext passwords directly

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Registration Successful!", "prompt", JOptionPane.INFORMATION_MESSAGE);
                        clearFields(); // Clear the input box
                        CardLayout cl = (CardLayout)(getParent().getLayout());
                        if (cl != null) {
                            cl.show(getParent(), "LOGIN_PANEL"); // Let's say the login panel is named "LOGIN_PANEL"
                        }
                    } else {
                        setMessage("Registration failed, please try again.");
                    }
                }
            } catch (SQLException ex) {
                setMessage("Database error, please try again later.");
                ex.printStackTrace();
            }

            messageLabel.repaint(); // Force refresh of the interface
        }

        private void setMessage(final String message) {
            SwingUtilities.invokeLater(() -> {
                messageLabel.setText(message);
                messageLabel.setVisible(true); // Make sure the message label is visible
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        identityComboBox.setSelectedIndex(0);
        identifierField.setText(""); // Clear the ID number entry box
        phoneField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void clearMessage() {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setText("");
            messageLabel.setVisible(false); // Hide message labels
        });
    }
}
