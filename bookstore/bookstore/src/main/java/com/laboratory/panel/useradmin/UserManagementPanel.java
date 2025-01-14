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

        // Initialize table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Gender", "Age", "Identity", "ID Number", "Phone", "Email"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Name:"));
        nameSearchField = new JTextField(15);
        searchPanel.add(nameSearchField);

        searchPanel.add(new JLabel("Gender:"));
        genderSearchBox = new JComboBox<>(new String[]{"All", "Male", "Female"});
        searchPanel.add(genderSearchBox);

        searchPanel.add(new JLabel("Identity:"));
        identitySearchBox = new JComboBox<>(new String[]{"All", "Teacher", "Undergraduate", "Graduate", "External Personnel", "Administrator"});
        searchPanel.add(identitySearchBox);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add User");
        updateButton = new JButton("Update User");
        deleteButton = new JButton("Delete User");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadUsers(); // Load all user data
    }

    // The loadUsers method with parameters for search conditions
    private void loadUsers(String name, String gender, String identity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM users WHERE 1=1");

            if (!name.isEmpty()) {
                queryBuilder.append(" AND name LIKE ?");
            }
            if (!"All".equals(gender)) {
                queryBuilder.append(" AND gender = ?");
            }
            if (!"All".equals(identity)) {
                queryBuilder.append(" AND identity = ?");
            }

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            int parameterIndex = 1;
            if (!name.isEmpty()) {
                pstmt.setString(parameterIndex++, "%" + name + "%");
            }
            if (!"All".equals(gender)) {
                pstmt.setString(parameterIndex++, gender);
            }
            if (!"All".equals(identity)) {
                pstmt.setString(parameterIndex++, identity);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // Clear current data
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
            JOptionPane.showMessageDialog(null, "Failed to load users: " + e.getMessage());
        }
    }

    // The default loadUsers method to load all users
    private void loadUsers() {
        // Call the loadUsers method with default values
        loadUsers("", "All", "All");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if ("Add User".equals(actionCommand)) {
            showAddUserDialog();
        } else if ("Update User".equals(actionCommand)) {
            updateUser();
        } else if ("Delete User".equals(actionCommand)) {
            deleteUser();
        } else if ("Search".equals(actionCommand)) {
            performSearch();
        }
    }

    private void showAddUserDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddUserDialog dialog = new AddUserDialog(frame);
        dialog.setVisible(true);
        loadUsers(); // Refresh the table
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to delete!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id=?")) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "User deleted successfully!");
                loadUsers(); // Refresh the table

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to delete user: " + ex.getMessage());
            }
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to update!");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UpdateUserDialog dialog = new UpdateUserDialog(frame, userId);
        dialog.setVisible(true);
        loadUsers(); // Refresh the table
    }

    private void performSearch() {
        String name = nameSearchField.getText().trim();
        String gender = (String) genderSearchBox.getSelectedItem();
        String identity = (String) identitySearchBox.getSelectedItem();
        loadUsers(name, gender, identity);
    }
}
