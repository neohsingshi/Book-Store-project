package com.laboratory.panel;

import com.laboratory.MainFrame;
import com.laboratory.panel.shop.BookManagementPanel;
import com.laboratory.panel.shop.ShoppingCartPanel;
import com.laboratory.panel.system.SystemManagementPanel;

import javax.swing.*;
import java.awt.*;

public class UserMainPanel extends JPanel {
    private MainFrame mainFrame;
    private String userIdentifier;

    public UserMainPanel(MainFrame mainFrame, String userIdentifier) {
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Create a book admin panel (don't pass in shoppingCartPanel for the time being)
        BookManagementPanel bookManagementPanel = null;

        // Create a shopping cart panel and set the booksManagementPanel to null
        ShoppingCartPanel shoppingCartPanel = new ShoppingCartPanel(mainFrame, userIdentifier, null);

        // Update the book admin panel to include references to shoppingCartPanel
        bookManagementPanel = new BookManagementPanel(shoppingCartPanel);

        // Add a shopping cart panel
        tabbedPane.addTab("Shopping cart", shoppingCartPanel);

        // Add a system admin panel
        tabbedPane.addTab("System administration", createSystemManagementPanel());

        // Add a book admin panel
        tabbedPane.addTab("Book browsing", bookManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSystemManagementPanel() {
        return new SystemManagementPanel(mainFrame, userIdentifier);
    }
}
