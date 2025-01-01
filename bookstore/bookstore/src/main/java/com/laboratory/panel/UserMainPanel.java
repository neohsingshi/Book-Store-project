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

        // 创建书籍管理面板（暂时不传入shoppingCartPanel）
        BookManagementPanel bookManagementPanel = null;

        // 创建购物车面板，并将 booksManagementPanel 设置为null
        ShoppingCartPanel shoppingCartPanel = new ShoppingCartPanel(mainFrame, userIdentifier, null);

        // 更新书籍管理面板以包含对 shoppingCartPanel 的引用
        bookManagementPanel = new BookManagementPanel(shoppingCartPanel);

        // 添加购物车面板
        tabbedPane.addTab("购物车", shoppingCartPanel);

        // 添加系统管理面板
        tabbedPane.addTab("系统管理", createSystemManagementPanel());

        // 添加书籍管理面板
        tabbedPane.addTab("书籍浏览", bookManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSystemManagementPanel() {
        return new SystemManagementPanel(mainFrame, userIdentifier);
    }
}
