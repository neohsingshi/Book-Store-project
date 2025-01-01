package com.laboratory.panel;

import com.laboratory.MainFrame;
import com.laboratory.panel.bookadmin.BookManagementPanel;
import com.laboratory.panel.system.SystemManagementPanel;
import com.laboratory.panel.useradmin.UserManagementPanel;

import javax.swing.*;
import java.awt.*;

public class AdminMainPanel extends JPanel {
    private MainFrame mainFrame;
    private String userIdentifier; // 用户标识符（例如学号或教工号）

    // 构造函数需要接收 MainFrame 实例和用户标识符作为参数
    public AdminMainPanel(MainFrame mainFrame, String userIdentifier) {
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        setLayout(new BorderLayout());

        // 创建一个选项卡面板，用于分隔不同的管理功能
        JTabbedPane tabbedPane = new JTabbedPane();

        // 用户管理
        tabbedPane.addTab("用户管理", new UserManagementPanel());

        // 书籍管理
        tabbedPane.addTab("书籍管理", new BookManagementPanel());

        // 系统管理
        tabbedPane.addTab("系统管理", new SystemManagementPanel(mainFrame, userIdentifier)); // 传递 mainFrame 和 userIdentifier 给 SystemManagementPanel



        add(tabbedPane, BorderLayout.CENTER);
    }
}
