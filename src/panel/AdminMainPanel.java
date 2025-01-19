package com.laboratory.panel;

import com.laboratory.MainFrame;
import com.laboratory.panel.bookadmin.BookManagementPanel;
import com.laboratory.panel.system.SystemManagementPanel;
import com.laboratory.panel.useradmin.UserManagementPanel;

import javax.swing.*;
import java.awt.*;

public class AdminMainPanel extends JPanel {
    private MainFrame mainFrame;
    private String userIdentifier; // User identifier (e.g., student ID or staff number)

    // Constructor needs to receive a MainFrame instance and a user identifier as parameters
    public AdminMainPanel(MainFrame mainFrame, String userIdentifier) {
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        setLayout(new BorderLayout());

        // Create a tabbed pane for separating different management functions
        JTabbedPane tabbedPane = new JTabbedPane();

        // User Management
        tabbedPane.addTab("User Management", new UserManagementPanel());

        // Book Management
        tabbedPane.addTab("Book Management", new BookManagementPanel());

        // System Management
        tabbedPane.addTab("System Management", new SystemManagementPanel(mainFrame, userIdentifier)); // Pass mainFrame and userIdentifier to SystemManagementPanel

        add(tabbedPane, BorderLayout.CENTER);
    }
}
