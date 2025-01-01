package com.laboratory.panel.useradmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateUserDialog extends JDialog implements ActionListener {
    private JTextField nameField, identifierField, phoneField, emailField, ageField;
    private JComboBox<String> genderBox, identityBox;
    private JButton updateButton, cancelButton;
    private int userId;

    public UpdateUserDialog(JFrame owner, int userId) {
        super(owner, "更新用户", true);
        this.userId = userId;
        setLayout(new BorderLayout());

        // 初始化表单
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(7, 2));

        formPanel.add(new JLabel("姓名:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("性别:"));
        genderBox = new JComboBox<>(new String[]{"男", "女"});
        formPanel.add(genderBox);

        formPanel.add(new JLabel("年龄:"));
        ageField = new JTextField();
        formPanel.add(ageField);

        formPanel.add(new JLabel("身份:"));
        identityBox = new JComboBox<>(new String[]{"教师", "本科生", "研究生", "校外人员", "管理员"});
        formPanel.add(identityBox);

        formPanel.add(new JLabel("ID号:"));
        identifierField = new JTextField();
        identifierField.setEditable(false); // 禁止编辑ID号
        formPanel.add(identifierField);

        formPanel.add(new JLabel("电话:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("邮箱:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("更新");
        cancelButton = new JButton("取消");

        updateButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadUserDetails(); // 加载用户详情

        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    private void loadUserDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE id = ?")) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                genderBox.setSelectedItem(rs.getString("gender"));
                ageField.setText(String.valueOf(rs.getInt("age")));
                identityBox.setSelectedItem(rs.getString("identity"));
                identifierField.setText(rs.getString("identifier"));
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载用户失败：" + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("更新".equals(e.getActionCommand())) {
            updateUser();
            dispose(); // 关闭对话框
        } else if ("取消".equals(e.getActionCommand())) {
            dispose(); // 关闭对话框
        }
    }

    private void updateUser() {
        String name = nameField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的年龄！");
            return;
        }
        String identity = (String) identityBox.getSelectedItem();
        String identifier = identifierField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE users SET name=?, gender=?, age=?, identity=?, identifier=?, phone=?, email=? WHERE id=?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setInt(3, age);
            pstmt.setString(4, identity);
            pstmt.setString(5, identifier);
            pstmt.setString(6, phone);
            pstmt.setString(7, email);
            pstmt.setInt(8, userId);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "用户信息更新成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "用户信息更新失败：" + ex.getMessage());
        }
    }
}
