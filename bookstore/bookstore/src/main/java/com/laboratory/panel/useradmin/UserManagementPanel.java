package com.laboratory.panel.useradmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManagementPanel extends JPanel implements ActionListener {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JTextField nameSearchField;
    private JComboBox<String> genderSearchBox, identitySearchBox;

    public UserManagementPanel() {
        setLayout(new BorderLayout());

        // 初始化表格
        tableModel = new DefaultTableModel(new Object[]{"ID", "姓名", "性别", "年龄", "身份", "ID号", "电话", "邮箱"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // 搜索面板
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("姓名:"));
        nameSearchField = new JTextField(15);
        searchPanel.add(nameSearchField);

        searchPanel.add(new JLabel("性别:"));
        genderSearchBox = new JComboBox<>(new String[]{"全部", "男", "女"});
        searchPanel.add(genderSearchBox);

        searchPanel.add(new JLabel("身份:"));
        identitySearchBox = new JComboBox<>(new String[]{"全部", "教师", "本科生", "研究生", "校外人员", "管理员"});
        searchPanel.add(identitySearchBox);

        searchButton = new JButton("搜索");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加用户");
        updateButton = new JButton("更新用户");
        deleteButton = new JButton("删除用户");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadUsers(); // 加载所有用户数据
    }

    // 保持原有的 loadUsers 方法不变，它接受三个参数用于搜索条件
    private void loadUsers(String name, String gender, String identity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM users WHERE 1=1");

            if (!name.isEmpty()) {
                queryBuilder.append(" AND name LIKE ?");
            }
            if (!"全部".equals(gender)) {
                queryBuilder.append(" AND gender = ?");
            }
            if (!"全部".equals(identity)) {
                queryBuilder.append(" AND identity = ?");
            }

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            int parameterIndex = 1;
            if (!name.isEmpty()) {
                pstmt.setString(parameterIndex++, "%" + name + "%");
            }
            if (!"全部".equals(gender)) {
                pstmt.setString(parameterIndex++, gender);
            }
            if (!"全部".equals(identity)) {
                pstmt.setString(parameterIndex++, identity);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // 清空现有数据
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getInt("age"),
                        rs.getString("identity"),
                        rs.getString("identifier"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载用户失败：" + e.getMessage());
        }
    }

    // 添加一个新的无参 loadUsers 方法，用于加载所有用户
    private void loadUsers() {
        // 调用带参数的 loadUsers 方法，并传入默认值
        loadUsers("", "全部", "全部");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if ("添加用户".equals(actionCommand)) {
            showAddUserDialog();
        } else if ("更新用户".equals(actionCommand)) {
            updateUser();
        } else if ("删除用户".equals(actionCommand)) {
            deleteUser();
        } else if ("搜索".equals(actionCommand)) {
            performSearch();
        }
    }

    private void showAddUserDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddUserDialog dialog = new AddUserDialog(frame);
        dialog.setVisible(true);
        loadUsers(); // 刷新表格
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "请选择要删除的用户！");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "确定要删除选中的用户吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id=?")) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "用户删除成功！");
                loadUsers(); // 刷新表格

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "用户删除失败：" + ex.getMessage());
            }
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "请选择要更新的用户！");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UpdateUserDialog dialog = new UpdateUserDialog(frame, userId);
        dialog.setVisible(true);
        loadUsers(); // 刷新表格
    }

    private void performSearch() {
        String name = nameSearchField.getText().trim();
        String gender = (String) genderSearchBox.getSelectedItem();
        String identity = (String) identitySearchBox.getSelectedItem();
        loadUsers(name, gender, identity);
    }
}
