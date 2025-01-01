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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPanel extends JPanel {
    private JTextField nameField;
    private JComboBox<String> identityComboBox;
    private JTextField identifierField; // 身份号
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton; // 新增返回按钮
    private JLabel messageLabel;
    private MainFrame mainFrame;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.decode("#f0f4f8")); // 设置背景色

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // 增加组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font globalFont = new Font("Microsoft YaHei", Font.PLAIN, 16); // 使用微软雅黑字体
        Font titleFont = globalFont.deriveFont(Font.BOLD, 24);

        JLabel titleLabel = new JLabel("用户注册", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.decode("#333333"));

        JLabel nameLabel = createStyledLabel("姓名:");
        nameField = createStyledTextField();

        JLabel identityLabel = createStyledLabel("身份:");
        String[] identities = {"用户", "管理员"}; // 在这里添加了"管理员"
        identityComboBox = createStyledComboBox(identities);

        JLabel identifierLabel = createStyledLabel("身份号:"); // 身份号标签
        identifierField = createStyledTextField();           // 身份号输入框

        JLabel phoneLabel = createStyledLabel("电话:");
        phoneField = createStyledTextField();

        JLabel emailLabel = createStyledLabel("邮箱:");
        emailField = createStyledTextField();

        JLabel passwordLabel = createStyledLabel("密码:");
        passwordField = createStyledPasswordField();

        JLabel confirmPasswordLabel = createStyledLabel("确认密码:");
        confirmPasswordField = createStyledPasswordField();

        registerButton = createStyledButton("注册账号");
        backButton = createStyledButton("返回登录"); // 创建返回按钮
        messageLabel = createStyledMessageLabel("");

        // Add components to the panel with increased height
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        int row = 1;
        addRow(gbc, nameLabel, nameField, row++);
        addRow(gbc, identityLabel, identityComboBox, row++);
        addRow(gbc, identifierLabel, identifierField, row++); // 添加ID号行
        addRow(gbc, phoneLabel, phoneField, row++);
        addRow(gbc, emailLabel, emailField, row++);
        addRow(gbc, passwordLabel, passwordField, row++);
        addRow(gbc, confirmPasswordLabel, confirmPasswordField, row++);

        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        gbc.gridy = row++;
        add(backButton, gbc); // 添加返回按钮

        gbc.gridy = row;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);

        // Add action listener to the register button
        registerButton.addActionListener(new RegisterButtonListener());

        // Add action listener to the back button
        backButton.addActionListener(e -> {
            clearFields(); // 清空输入框的所有数据
            CardLayout cl = (CardLayout)(getParent().getLayout());
            if (cl != null) {
                cl.show(getParent(), "LOGIN_PANEL"); // 假设登录面板名为"LOGIN_PANEL"
            }
        });

        // Set different colors for buttons
        registerButton.setBackground(Color.decode("#FF9800"));
        backButton.setBackground(Color.decode("#3F51B5"));
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return comboBox;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private JLabel createStyledMessageLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        label.setForeground(Color.RED);
        label.setVisible(true); // 确保消息标签是可见的
        return label;
    }

    private void addRow(GridBagConstraints gbc, Component leftComponent, Component rightComponent, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        add(leftComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        add(rightComponent, gbc);
    }

    private boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validatePhone(String phone) {
        String phoneRegex = "^\\d{11}$"; // Assuming a simple 11-digit number for China
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearMessage(); // 清除之前的任何消息

            String name = nameField.getText().trim();
            String identity = (String) identityComboBox.getSelectedItem();
            String identifier = identifierField.getText().trim(); // 获取身份号
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (name.isEmpty() || identity == null || identifier.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                setMessage("所有字段都是必填项！");
                return;
            }

            if (!password.equals(confirmPassword)) {
                setMessage("两次输入的密码不一致！");
                return;
            }

            if (!validatePhone(phone)) {
                setMessage("请输入有效的手机号码！");
                return;
            }

            if (!validateEmail(email)) {
                setMessage("请输入有效的电子邮件地址！");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Check if ID号 or name already exists in database
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM users WHERE identifier = ? OR name = ?")) {
                    checkStmt.setString(1, identifier);
                    checkStmt.setString(2, name);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            setMessage("该身份号或姓名已存在，请选择其他ID号或姓名。");
                            return;
                        }
                    }
                }

                // Insert new user into database without hashing the password
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO users (name, identity, identifier, phone, email, password) VALUES (?, ?, ?, ?, ?, ?)")) {

                    pstmt.setString(1, name);
                    pstmt.setString(2, identity);
                    pstmt.setString(3, identifier);
                    pstmt.setString(4, phone);
                    pstmt.setString(5, email);
                    pstmt.setString(6, password); // 直接使用明文密码

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "注册成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        clearFields(); // 清空输入框
                        CardLayout cl = (CardLayout)(getParent().getLayout());
                        if (cl != null) {
                            cl.show(getParent(), "LOGIN_PANEL"); // 假设登录面板名为"LOGIN_PANEL"
                        }
                    } else {
                        setMessage("注册失败，请重试。");
                    }
                }
            } catch (SQLException ex) {
                setMessage("数据库错误，请稍后再试。");
                ex.printStackTrace();
            }

            messageLabel.repaint(); // 强制刷新界面
        }

        private void setMessage(final String message) {
            SwingUtilities.invokeLater(() -> {
                messageLabel.setText(message);
                messageLabel.setVisible(true); // 确保消息标签是可见的
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        identityComboBox.setSelectedIndex(0);
        identifierField.setText(""); // 清空身份号输入框
        phoneField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void clearMessage() {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setText("");
            messageLabel.setVisible(false); // 隐藏消息标签
        });
    }
}
