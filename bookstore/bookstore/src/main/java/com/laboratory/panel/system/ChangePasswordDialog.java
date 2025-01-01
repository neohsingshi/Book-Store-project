package com.laboratory.panel.system;

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

public class ChangePasswordDialog extends JDialog {
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;
    private MainFrame mainFrame;
    private String userIdentifier; // 用户标识符（例如学号或教工号）

    public ChangePasswordDialog(MainFrame mainFrame, String title, boolean modal, String userIdentifier) {
        super((JFrame)null, title, modal);
        this.mainFrame = mainFrame;
        this.userIdentifier = userIdentifier;
        initializeComponents();
        setLocationRelativeTo(null); // Center the dialog on screen
        setResizable(false); // 禁止用户调整对话框大小
        pack(); // 根据组件的最佳尺寸调整对话框大小
        setMinimumSize(new Dimension(300, 200)); // 设置最小尺寸
        setPreferredSize(new Dimension(400, 250)); // 设置首选尺寸
        pack(); // 再次调整以确保最佳尺寸
    }

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel oldPasswordLabel = new JLabel("旧密码:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(oldPasswordLabel, gbc);

        oldPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(oldPasswordField, gbc);
        gbc.gridwidth = 1;

        JLabel newPasswordLabel = new JLabel("新密码:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(newPasswordLabel, gbc);

        newPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(newPasswordField, gbc);
        gbc.gridwidth = 1;

        JLabel confirmPasswordLabel = new JLabel("确认新密码:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(confirmPasswordField, gbc);
        gbc.gridwidth = 1;

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    if (updatePassword()) {
                        JOptionPane.showMessageDialog(ChangePasswordDialog.this, "密码修改成功！");
                        dispose(); // 关闭对话框
                    } else {
                        messageLabel.setText("密码修改失败，请稍后再试！");
                    }
                } else {
                    messageLabel.setText("密码修改失败，请检查输入！");
                }
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(messageLabel, gbc);
    }

    private boolean validateInput() {
        char[] oldPassword = oldPasswordField.getPassword();
        char[] newPassword = newPasswordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        // 检查所有字段是否非空
        if (oldPassword.length == 0 || newPassword.length == 0 || confirmPassword.length == 0) {
            messageLabel.setText("所有字段不能为空！");
            return false;
        }

        // 验证新密码是否一致
        if (!new String(newPassword).equals(new String(confirmPassword))) {
            messageLabel.setText("新密码和确认密码不一致！");
            return false;
        }

        // 验证旧密码是否正确
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE identifier = ?")) {

            pstmt.setString(1, userIdentifier);
            System.out.println("Executing query: SELECT password FROM users WHERE identifier = '" + userIdentifier + "'");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 直接比较明文密码
                    String storedPassword = rs.getString("password");
                    System.out.println("Stored Password: " + storedPassword);
                    System.out.println("Old Password Input: " + new String(oldPassword));

                    if (new String(oldPassword).equals(storedPassword)) {
                        return true;
                    } else {
                        messageLabel.setText("旧密码错误！");
                        System.out.println("Password mismatch.");
                        return false;
                    }
                } else {
                    messageLabel.setText("用户不存在！");
                    System.out.println("User not found for identifier: " + userIdentifier);
                    return false;
                }
            }
        } catch (SQLException ex) {
            messageLabel.setText("数据库错误，请稍后再试。");
            ex.printStackTrace();
            System.out.println("Database error occurred: " + ex.getMessage());
            return false;
        }
    }

    private boolean updatePassword() {
        char[] newPassword = newPasswordField.getPassword();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE identifier = ?");
            pstmt.setString(1, new String(newPassword)); // 存储明文密码
            pstmt.setString(2, userIdentifier);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
