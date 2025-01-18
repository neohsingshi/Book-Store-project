package com.laboratory.panel.system;

import com.laboratory.MainFrame;

import javax.swing.*;
import java.awt.*;

public class SystemManagementPanel extends JPanel {
    private JButton changePasswordButton;
    private JButton logoutButton;
    private MainFrame mainFrame;
    private String userIdentifier; // User identifier (e.g., student ID or staff ID)

    // Constructor needs to receive a MainFrame instance and user identifier as parameters
    public SystemManagementPanel(MainFrame mainFrame, String userIdentifier) {
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER; // Ensure components are centered

        // Change password button
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font(null, Font.BOLD, 14)); // Use system default font
        changePasswordButton.setBackground(Color.LIGHT_GRAY);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font(null, Font.BOLD, 14)); // Use system default font
        logoutButton.setBackground(Color.LIGHT_GRAY);
        logoutButton.addActionListener(e -> mainFrame.logout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // Explicitly set gridwidth
        gbc.weightx = 1.0;
        add(changePasswordButton, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1; // Explicitly set gridwidth
        add(logoutButton, gbc);
    }

    public void showChangePasswordDialog() {
        // Ensure userIdentifier is not empty here
        System.out.println("User Identifier: " + userIdentifier); // Debug information
        ChangePasswordDialog dialog = new ChangePasswordDialog(mainFrame, "Change Password", true, userIdentifier);
        dialog.setVisible(true);
    }
}
