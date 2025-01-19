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

        // Create book management panel (temporarily do not pass in shoppingCartPanel)
        BookManagementPanel bookManagementPanel = null;

        // Create shopping cart panel and set booksManagementPanel to null
        ShoppingCartPanel shoppingCartPanel = new ShoppingCartPanel(mainFrame, userIdentifier, null);

        // Update book management panel to include reference to shoppingCartPanel
        bookManagementPanel = new BookManagementPanel(shoppingCartPanel);

        // Add shopping cart panel
        tabbedPane.addTab("Shopping Cart", shoppingCartPanel);

        // Add system management panel
        tabbedPane.addTab("System Management", createSystemManagementPanel());

        // Add book browsing panel
        tabbedPane.addTab("Book Browsing", bookManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSystemManagementPanel() {
        return new SystemManagementPanel(mainFrame, userIdentifier);
    }
}
