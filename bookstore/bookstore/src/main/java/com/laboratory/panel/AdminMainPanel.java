package com.laboratory.panel;

import com.laboratory.MainFrame;
import com.laboratory.panel.bookadmin.BookManagementPanel;
import com.laboratory.panel.system.SystemManagementPanel;
import com.laboratory.panel.useradmin.UserManagementPanel;

import javax.swing.*;
import java.awt.*;

public class AdminMainPanel extends JPanel {
    private MainFrame mainFrame;
    private String userIdentifier; // User identifier (e.g., student ID or faculty ID)

    // The constructor needs to receive a MainFrame instance and a user identifier as parameters
    public AdminMainPanel(MainFrame mainFrame, String userIdentifier) {
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        setLayout(new BorderLayout());

        // Create a tab panel that separates the different management functions
        JTabbedPane tabbedPane = new JTabbedPane();

        // User management
        tabbedPane.addTab("User management", new UserManagementPanel());

        // Book management
        tabbedPane.addTab("Book management", new BookManagementPanel());

        // System administration
        tabbedPane.addTab("System administration", new SystemManagementPanel(mainFrame, userIdentifier)); // 传递 mainFrame 和 userIdentifier 给 SystemManagementPanel



        add(tabbedPane, BorderLayout.CENTER);
    }
}
