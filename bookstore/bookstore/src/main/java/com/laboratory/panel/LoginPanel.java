package com.laboratory.panel;

import com.laboratory.MainFrame;
import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private MainFrame mainFrame;  // 引用主窗口实例，用于切换面板

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.decode("#f0f4f8"));  // 设置背景色

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font globalFont = new Font("Microsoft YaHei", Font.PLAIN, 16);  // 设置全局字体

        // 初始化UI组件并设置样式
        JLabel titleLabel = new JLabel("图书商店", SwingConstants.CENTER);
        titleLabel.setFont(globalFont.deriveFont(Font.BOLD, 24));
        titleLabel.setForeground(Color.decode("#333333"));

        JLabel usernameLabel = new JLabel("身份号:");
        usernameLabel.setFont(globalFont);
        usernameField = new JTextField(20);
        usernameField.setFont(globalFont);

        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(globalFont);
        passwordField = new JPasswordField(20);
        passwordField.setFont(globalFont);

        JButton loginButton = new JButton("登录");
        loginButton.setFont(globalFont);
        loginButton.setBackground(Color.decode("#4CAF50"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        JButton registerButton = new JButton("注册");
        registerButton.setFont(globalFont);
        registerButton.setBackground(Color.decode("#FF9800"));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(globalFont);
        messageLabel.setForeground(Color.RED);

        // 将组件添加到面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(usernameLabel, gbc);

        gbc.gridy = 2;
        add(usernameField, gbc);

        gbc.gridy = 3;
        add(passwordLabel, gbc);

        gbc.gridy = 4;
        add(passwordField, gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        gbc.gridy = 6;
        add(registerButton, gbc);

        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);

        // 为按钮添加事件监听器
        loginButton.addActionListener(new LoginButtonListener());
        registerButton.addActionListener(e -> {
            CardLayout cl = (CardLayout)(getParent().getLayout());
            if (cl != null) {
                cl.show(getParent(), "REGISTER_PANEL");
            }
        });
    }

    // 登录按钮点击事件处理器
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String identifier = usernameField.getText().trim();
            char[] passwordChars = passwordField.getPassword();  // 获取密码输入
            String password = new String(passwordChars);

            if (identifier.isEmpty() || password.isEmpty()) {
                messageLabel.setText("身份号或密码不能为空！");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                // 查询用户的 identifier 和 password
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT password, identity FROM users WHERE identifier = ?")) {

                    pstmt.setString(1, identifier);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String storedPassword = rs.getString("password");
                            String role = rs.getString("identity");  // 获取用户身份（角色）

                            // 直接比较明文密码
                            if (password.equals(storedPassword)) {  // 注意：不要在生产环境中这样做！
                                JOptionPane.showMessageDialog(null, "登录成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                mainFrame.showMainPanel(role, identifier);  // 使用原始角色字符串进行切换，并传递用户标识符
                            } else {
                                messageLabel.setText("身份号或密码错误！");
                            }
                        } else {
                            messageLabel.setText("身份号或密码错误！");
                        }
                    }
                }
            } catch (SQLException ex) {
                messageLabel.setText("数据库错误，请稍后再试。");
                ex.printStackTrace();
            }
        }
    }
}
