package com.laboratory;

import com.laboratory.panel.AdminMainPanel;
import com.laboratory.panel.LoginPanel;
import com.laboratory.panel.RegisterPanel;
import com.laboratory.panel.UserMainPanel;
import com.laboratory.panel.system.SystemManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cards;
    private String userIdentifier; // User identifier (e.g., student ID or staff ID)

    // Define panel name constants
    private static final String LOGIN_PANEL = "LOGIN_PANEL";
    private static final String REGISTER_PANEL = "REGISTER_PANEL";
    private static final String ADMIN_MAIN_PANEL = "ADMIN_MAIN_PANEL";
    private static final String USER_MAIN_PANEL = "USER_MAIN_PANEL";
    private static final String SYSTEM_MANAGEMENT_PANEL = "SYSTEM_MANAGEMENT_PANEL"; // Add system management panel identifier

    // Use Map to store panels
    private final Map<String, JPanel> panelMap = new HashMap<>();

    // Constructor initializes the main window and layout manager
    public MainFrame() {
        setTitle("Bookstore");  // Set window title
        setSize(800, 600);  // Set window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program on close

        // Initialize CardLayout and container for managing different panels
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Add login panel to the container
        LoginPanel loginPanel = new LoginPanel(this);
        panelMap.put(LOGIN_PANEL, loginPanel);
        cards.add(loginPanel, LOGIN_PANEL);

        // Add registration panel to the container
        RegisterPanel registerPanel = new RegisterPanel(this);  // Pass this as a parameter
        panelMap.put(REGISTER_PANEL, registerPanel);
        cards.add(registerPanel, REGISTER_PANEL);

        setContentPane(cards);  // Set content pane
        cardLayout.show(cards, LOGIN_PANEL);  // Set the initially displayed panel to login panel
    }

    // Method to display the system management panel
    public void showSystemManagementPanel(String userIdentifier) {
        // Pass userIdentifier when creating an instance of SystemManagementPanel
        SystemManagementPanel systemManagementPanel = new SystemManagementPanel(this, userIdentifier);
        panelMap.put(SYSTEM_MANAGEMENT_PANEL, systemManagementPanel);
        cards.add(systemManagementPanel, SYSTEM_MANAGEMENT_PANEL);
        cardLayout.show(cards, SYSTEM_MANAGEMENT_PANEL);
    }

    // Method to exit the system
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit the system?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Displays the main panel based on the user's role.
     */
    public void showMainPanel(String role, String userIdentifier) {
        this.userIdentifier = userIdentifier; // Ensure userIdentifier is correctly set here
        System.out.println("Setting Main Frame User Identifier: " + this.userIdentifier); // Debug information
        System.out.println("Logged in user role: " + role); // Debug information

        String panelName;
        switch (role) {
            case "Admin":  // Ensure this matches the exact string stored in the database
                panelName = ADMIN_MAIN_PANEL;
                break;
            default:
                panelName = USER_MAIN_PANEL;
                break;
        }

        // Check if the panel already exists, if not create and add it
        if (!panelMap.containsKey(panelName)) {
            switch (panelName) {
                case ADMIN_MAIN_PANEL:
                    AdminMainPanel adminMainPanel = new AdminMainPanel(this, userIdentifier);
                    panelMap.put(panelName, adminMainPanel);
                    cards.add(adminMainPanel, panelName);
                    break;
                case USER_MAIN_PANEL:
                    UserMainPanel userMainPanel = new UserMainPanel(this, userIdentifier);
                    panelMap.put(panelName, userMainPanel);
                    cards.add(userMainPanel, panelName);
                    break;
            }
        }

        cardLayout.show(cards, panelName);  // Switch to the corresponding main interface
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);  // Create and display the main window
        });
    }
}
