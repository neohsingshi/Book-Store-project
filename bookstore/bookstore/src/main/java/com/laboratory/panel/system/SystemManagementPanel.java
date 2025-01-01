package com.laboratory.panel.system;

import com.laboratory.MainFrame;

import javax.swing.*;
import java.awt.*;

public class SystemManagementPanel extends JPanel {
    private JButton changePasswordButton;
    private JButton logoutButton;
    private MainFrame mainFrame;
    private String userIdentifier; // 用户标识符（例如学号或教工号）

    // 构造函数需要接收 MainFrame 实例和用户标识符作为参数
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
        gbc.anchor = GridBagConstraints.CENTER; // 添加这一行以确保组件居中

        // 修改密码按钮
        changePasswordButton = new JButton("修改密码");
        changePasswordButton.setFont(new Font(null, Font.BOLD, 14)); // 使用系统默认字体
        changePasswordButton.setBackground(Color.LIGHT_GRAY);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        // 退出系统按钮
        logoutButton = new JButton("退出系统");
        logoutButton.setFont(new Font(null, Font.BOLD, 14)); // 使用系统默认字体
        logoutButton.setBackground(Color.LIGHT_GRAY);
        logoutButton.addActionListener(e -> mainFrame.logout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // 明确设置 gridwidth
        gbc.weightx = 1.0;
        add(changePasswordButton, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1; // 明确设置 gridwidth
        add(logoutButton, gbc);
    }

    public void showChangePasswordDialog() {
        // 确保 userIdentifier 在这里不是空的
        System.out.println("User Identifier: " + userIdentifier); // 调试信息
        ChangePasswordDialog dialog = new ChangePasswordDialog(mainFrame, "修改密码", true, userIdentifier);
        dialog.setVisible(true);
    }
}
