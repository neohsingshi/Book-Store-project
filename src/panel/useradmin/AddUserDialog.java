package com.laboratory.panel.useradmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddUserDialog extends JDialog implements ActionListener {
    private JTextField nameField, identifierField, phoneField, emailField, ageField;
    private JComboBox<String> genderBox, identityBox;
    private JButton addButton, cancelButton;

    public AddUserDialog(JFrame owner) {
        super(owner, "Add User", true);
        setLayout(new BorderLayout());

        // Initialize form panel with labels and input fields
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
        formPanel.add(identifierField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        add(formPanel, BorderLayout.CENTER);

        // Initialize button panel with buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");

        addButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Add".equals(e.getActionCommand())) {
            addUser();
            dispose(); // Close the dialog after adding user
        } else if ("Cancel".equals(e.getActionCommand())) {
            dispose(); // Close the dialog on cancel
        }
    }

    private void addUser() {
        String name = nameField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String identity = (String) identityBox.getSelectedItem();
        String identifier = identifierField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String password = "defaultpassword"; // Default password

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (name, gender, age, identity, identifier, phone, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setInt(3, age);
            pstmt.setString(4, identity);
            pstmt.setString(5, identifier);
            pstmt.setString(6, phone);
            pstmt.setString(7, email);
            pstmt.setString(8, password);

            pstmt.executeUpdate();

            // Show success message with "ok" button
            Object[] options = {"ok"};
            JOptionPane.showOptionDialog(null,
                    "User added successfully!",
                    "Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, // Do not use a custom Icon
                    options, // Button text array
                    options[0]); // Default button to focus
        } catch (SQLException ex) {
            ex.printStackTrace();
            Object[] options = {"ok"};
            JOptionPane.showOptionDialog(null,
                    "Failed to add user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, // Do not use a custom Icon
                    options, // Button text array
                    options[0]); // Default button to focus
        }
    }
}
