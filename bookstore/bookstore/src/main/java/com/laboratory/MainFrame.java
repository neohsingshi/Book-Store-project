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
    private String userIdentifier; // 用户标识符（例如学号或教工号）

    // 定义面板名称常量
    private static final String LOGIN_PANEL = "LOGIN_PANEL";
    private static final String REGISTER_PANEL = "REGISTER_PANEL";
    private static final String ADMIN_MAIN_PANEL = "ADMIN_MAIN_PANEL";
    private static final String USER_MAIN_PANEL = "USER_MAIN_PANEL";
    private static final String SYSTEM_MANAGEMENT_PANEL = "SYSTEM_MANAGEMENT_PANEL"; // 新增系统管理面板标识

    // 使用 Map 来存储面板
    private final Map<String, JPanel> panelMap = new HashMap<>();

    // 构造函数初始化主窗口和布局管理器
    public MainFrame() {
        setTitle("图书商店");  // 设置窗口标题
        setSize(800, 600);  // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 关闭时退出程序

        // 初始化CardLayout和容器，用于管理不同面板
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // 添加登录面板到容器中
        LoginPanel loginPanel = new LoginPanel(this);
        panelMap.put(LOGIN_PANEL, loginPanel);
        cards.add(loginPanel, LOGIN_PANEL);

        // 添加注册面板到容器中
        RegisterPanel registerPanel = new RegisterPanel(this);  // 将 this 作为参数传递
        panelMap.put(REGISTER_PANEL, registerPanel);
        cards.add(registerPanel, REGISTER_PANEL);

        setContentPane(cards);  // 设置内容面板
        cardLayout.show(cards, LOGIN_PANEL);  // 设置初始显示的面板为登录面板
    }

    // 显示系统管理面板的方法
    public void showSystemManagementPanel(String userIdentifier) {
        // 创建系统管理面板实例时传递 userIdentifier
        SystemManagementPanel systemManagementPanel = new SystemManagementPanel(this, userIdentifier);
        panelMap.put(SYSTEM_MANAGEMENT_PANEL, systemManagementPanel);
        cards.add(systemManagementPanel, SYSTEM_MANAGEMENT_PANEL);
        cardLayout.show(cards, SYSTEM_MANAGEMENT_PANEL);
    }

    // 退出系统的方法
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(null, "确定要退出系统吗？", "确认退出", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void showMainPanel(String role, String userIdentifier) {
        this.userIdentifier = userIdentifier; // 确保在此处正确设置了 userIdentifier
        System.out.println("Setting Main Frame User Identifier: " + this.userIdentifier); // 调试信息

        String panelName;
        switch (role) {
            case "管理员":
                panelName = ADMIN_MAIN_PANEL;
                break;
            default:
                panelName = USER_MAIN_PANEL;
                break;
        }

        // 检查是否已存在该面板，如果不存在则创建并添加
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

        cardLayout.show(cards, panelName);  // 切换到相应的主界面
    }

    // 主方法，启动应用程序
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);  // 创建并显示主窗口
        });
    }
}
