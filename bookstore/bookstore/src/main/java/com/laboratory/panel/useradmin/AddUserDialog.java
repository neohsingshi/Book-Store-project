package com.laboratory.panel.useradmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddUserDialog extends JDialog implements ActionListener {
    private JTextField nameField, identifierField, phoneField, emailField, ageField;
    private JComboBox<String> genderBox, identityBox;
    private JButton addButton, cancelButton;

    public AddUserDialog(JFrame owner) {
        super(owner, "添加用户", true);
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
        addButton = new JButton("添加");
        cancelButton = new JButton("取消");

        addButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("添加".equals(e.getActionCommand())) {
            addUser();
            dispose(); // 关闭对话框
        } else if ("取消".equals(e.getActionCommand())) {
            dispose(); // 关闭对话框
        }
    }

    private void addUser() {
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
        String password = "defaultpassword"; // 默认密码

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (name, gender, age, identity, identifier, phone, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setInt(3, age);
            pstmt.setString(4, identity);
            pstmt.setString(5, identifier);
            pstmt.setString(6, phone);
            pstmt.setString(7, email);
            pstmt.setString(8, password);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "用户添加成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "用户添加失败：" + ex.getMessage());
        }
    }
}
