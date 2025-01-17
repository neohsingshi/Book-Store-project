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
    private String userIdentifier; // User identifier (e.g., student ID or faculty ID)

    // Defines the panel name constant
    private static final String LOGIN_PANEL = "LOGIN_PANEL";
    private static final String REGISTER_PANEL = "REGISTER_PANEL";
    private static final String ADMIN_MAIN_PANEL = "ADMIN_MAIN_PANEL";
    private static final String USER_MAIN_PANEL = "USER_MAIN_PANEL";
    private static final String SYSTEM_MANAGEMENT_PANEL = "SYSTEM_MANAGEMENT_PANEL"; // Added the system management panel logo

    // Use Map to store panels
    private final Map<String, JPanel> panelMap = new HashMap<>();

    // The constructor initializes the main window and layout manager
    public MainFrame() {
        setTitle("bookshop");  // Set the window title
        setSize(800, 600);  // Set the window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit the program when closing

        // Initialize the CardLayout and container to manage different panels
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Add the login panel to the container
        LoginPanel loginPanel = new LoginPanel(this);
        panelMap.put(LOGIN_PANEL, loginPanel);
        cards.add(loginPanel, LOGIN_PANEL);

        //  Add the registration panel to the container
        RegisterPanel registerPanel = new RegisterPanel(this);  // Pass this as a parameter
        panelMap.put(REGISTER_PANEL, registerPanel);
        cards.add(registerPanel, REGISTER_PANEL);

        setContentPane(cards);  // Set up the content panel
        cardLayout.show(cards, LOGIN_PANEL);  // Set the initial display panel to the login panel
    }

    // How to display the system management panel
    public void showSystemManagementPanel(String userIdentifier) {
        // Pass the userIdentifier when you create an instance of the System Administration Panel
        SystemManagementPanel systemManagementPanel = new SystemManagementPanel(this, userIdentifier);
        panelMap.put(SYSTEM_MANAGEMENT_PANEL, systemManagementPanel);
        cards.add(systemManagementPanel, SYSTEM_MANAGEMENT_PANEL);
        cardLayout.show(cards, SYSTEM_MANAGEMENT_PANEL);
    }

    // How to exit the system
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out of the system?", "Confirm the exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void showMainPanel(String role, String userIdentifier) {
        this.userIdentifier = userIdentifier; // Make sure the userIdentifier is set correctly here
        System.out.println("Setting Main Frame User Identifier: " + this.userIdentifier); // Debugging information

        String panelName;
        switch (role) {
            case "administrator":
                panelName = ADMIN_MAIN_PANEL;
                break;
            default:
                panelName = USER_MAIN_PANEL;
                break;
        }

        // Check if the panel already exists, and if it doesn't, create and add it
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

    // The main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);  // Create and display the main window
        });
    }
}
