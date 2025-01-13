package com.laboratory.panel.system;

import com.laboratory.MainFrame;

import javax.swing.*;
import java.awt.*;

public class SystemManagementPanel extends JPanel {
    private JButton changePasswordButton;
    private JButton logoutButton;
    private MainFrame mainFrame;
    private String userIdentifier; // User identifier (e.g., student or faculty number)

    // The constructor takes a MainFrame instance and a user identifier as arguments.
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
        gbc.anchor = GridBagConstraints.CENTER; // Add this line to ensure that the component is centered

        // Change Password button
        changePasswordButton = new JButton("change your password");
        changePasswordButton.setFont(new Font(null, Font.BOLD, 14)); // Use the system default font
        changePasswordButton.setBackground(Color.LIGHT_GRAY);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        // Exit system button
        logoutButton = new JButton("Log out of the system");
        logoutButton.setFont(new Font(null, Font.BOLD, 14)); // Use the system default font
        logoutButton.setBackground(Color.LIGHT_GRAY);
        logoutButton.addActionListener(e -> mainFrame.logout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // explicit setting gridwidth
        gbc.weightx = 1.0;
        add(changePasswordButton, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1; // explicit setting gridwidth
        add(logoutButton, gbc);
    }

    public void showChangePasswordDialog() {
        // Make sure the userIdentifier is not null here.
        System.out.println("User Identifier: " + userIdentifier); // Debugging Information
        ChangePasswordDialog dialog = new ChangePasswordDialog(mainFrame, "change your password", true, userIdentifier);
        dialog.setVisible(true);
    }
}
