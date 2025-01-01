package com.laboratory.panel.reservationadmin;

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
import java.util.HashSet;
import java.util.Set;

public class ReservationManagementPanel extends JPanel implements ActionListener {
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JTextField userSearchField;
    private JComboBox<String> equipmentSearchBox;
    private JComboBox<String> statusSearchBox;

    public ReservationManagementPanel() {
        initializeComponents();
        loadEquipments(); // 加载设备列表并设置默认值
        loadReservations(); // 加载所有预约数据
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // 初始化表格
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "用户ID", "设备ID", "预约时间", "状态"}, 0);
        reservationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        // 搜索面板
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("用户ID:"));
        userSearchField = new JTextField(15);
        searchPanel.add(userSearchField);

        searchPanel.add(new JLabel("设备ID:"));
        equipmentSearchBox = new JComboBox<>();
        searchPanel.add(equipmentSearchBox);

        searchPanel.add(new JLabel("状态:"));
        statusSearchBox = new JComboBox<>(new String[]{"全部", "待确认", "已确认", "已完成", "已取消"});
        searchPanel.add(statusSearchBox);

        searchButton = new JButton("搜索");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加预约");
        updateButton = new JButton("更新预约");
        deleteButton = new JButton("删除预约");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEquipments() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM equipments");
            ResultSet rs = pstmt.executeQuery();

            Set<Integer> equipmentIds = new HashSet<>();
            equipmentIds.add(0); // 保留用于后续逻辑判断

            while (rs.next()) {
                equipmentIds.add(rs.getInt("id"));
            }

            // 创建一个包含 "全部" 的数组，并设置给 JComboBox
            String[] equipmentOptions = equipmentIds.stream()
                    .sorted()
                    .map(id -> id == 0 ? "全部" : String.valueOf(id))
                    .toArray(String[]::new);

            equipmentSearchBox.setModel(new DefaultComboBoxModel<>(equipmentOptions));
            equipmentSearchBox.setSelectedItem("全部"); // 默认选择 "全部"

        } catch (SQLException e) {
            handleException("加载设备失败", e);
        }
    }

    private void loadReservations(String userId, Integer equipmentId, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM reservations WHERE 1=1");

            if (!userId.isEmpty()) {
                queryBuilder.append(" AND user_id LIKE ?");
            }
            if (equipmentId != null) { // 只有当 equipmentId 不为 null 时才添加过滤条件
                queryBuilder.append(" AND equipment_id = ?");
            }
            if (!"全部".equals(status)) {
                queryBuilder.append(" AND status = ?");
            }

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());
            int parameterIndex = 1;

            if (!userId.isEmpty()) {
                pstmt.setString(parameterIndex++, "%" + userId + "%");
            }
            if (equipmentId != null) { // 只有当 equipmentId 不为 null 时才设置参数值
                pstmt.setInt(parameterIndex++, equipmentId);
            }
            if (!"全部".equals(status)) {
                pstmt.setString(parameterIndex++, status);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // 清空现有数据
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("equipment_id"),
                        rs.getTimestamp("reservation_time"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            handleException("加载预约失败", e);
        }
    }

    private void loadReservations() {
        loadReservations("", null, "全部"); // 使用 null 表示不限制设备ID
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case "添加预约":
                showAddReservationDialog();
                break;
            case "更新预约":
                updateReservation();
                break;
            case "删除预约":
                deleteReservation();
                break;
            case "搜索":
                performSearch();
                break;
        }
    }

    private void showAddReservationDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddReservationDialog dialog = new AddReservationDialog(frame);
        dialog.setVisible(true);
        loadReservations(); // 刷新表格
    }

    private void deleteReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "请选择要删除的预约！");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "确定要删除选中的预约吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            executeDatabaseOperation("DELETE FROM reservations WHERE id=?", id);
            JOptionPane.showMessageDialog(null, "预约删除成功！");
            loadReservations(); // 刷新表格
        }
    }

    private void updateReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "请选择要更新的预约！");
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);

        if (!isReservationIdValid(reservationId)) {
            JOptionPane.showMessageDialog(null, "无效的预约ID：" + reservationId);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UpdateReservationDialog dialog = new UpdateReservationDialog(frame, reservationId);
        dialog.setVisible(true);
        loadReservations(); // 刷新表格
    }

    private boolean isReservationIdValid(int reservationId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM reservations WHERE id=?")) {

            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException ex) {
            handleException("验证预约ID时出错", ex);
            return false;
        }
    }

    private void performSearch() {
        String userId = userSearchField.getText().trim();
        String selectedEquipment = (String) equipmentSearchBox.getSelectedItem();
        Integer equipmentId = "全部".equals(selectedEquipment) ? null : Integer.parseInt(selectedEquipment); // 如果是 "全部" 则使用 null 表示不限制设备ID
        String status = (String) statusSearchBox.getSelectedItem();
        loadReservations(userId, equipmentId, status);
    }

    private void handleException(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, message + "：" + e.getMessage());
    }

    private void executeDatabaseOperation(String sql, Object... params) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException("数据库操作失败", e);
        }
    }
}
