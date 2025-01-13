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
        loadEquipments(); // Load device list and set defaults
        loadReservations(); // Load all reservation data
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Initializing Forms
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "user ID", "Device ID", "Reservation time", "state of affairs"}, 0);
        reservationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        // search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("user ID:"));
        userSearchField = new JTextField(15);
        searchPanel.add(userSearchField);

        searchPanel.add(new JLabel("Device ID:"));
        equipmentSearchBox = new JComboBox<>();
        searchPanel.add(equipmentSearchBox);

        searchPanel.add(new JLabel("state of affairs:"));
        statusSearchBox = new JComboBox<>(new String[]{"full", "To be confirmed", "confirmed", "done", "Cancelled"});
        searchPanel.add(statusSearchBox);

        searchButton = new JButton("look for sth.");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // pushbutton panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Appointment");
        updateButton = new JButton("Update Appointment");
        deleteButton = new JButton("Delete Appointment");

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
            equipmentIds.add(0); // Reserved for subsequent logical judgment

            while (rs.next()) {
                equipmentIds.add(rs.getInt("id"));
            }

            // Create an array containing "All" and set it to the JComboBox
            String[] equipmentOptions = equipmentIds.stream()
                    .sorted()
                    .map(id -> id == 0 ? "full" : String.valueOf(id))
                    .toArray(String[]::new);

            equipmentSearchBox.setModel(new DefaultComboBoxModel<>(equipmentOptions));
            equipmentSearchBox.setSelectedItem("full"); // "All" is selected by default

        } catch (SQLException e) {
            handleException("Failed to load device", e);
        }
    }

    private void loadReservations(String userId, Integer equipmentId, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM reservations WHERE 1=1");

            if (!userId.isEmpty()) {
                queryBuilder.append(" AND user_id LIKE ?");
            }
            if (equipmentId != null) { // Add filter only if equipmentId is not null
                queryBuilder.append(" AND equipment_id = ?");
            }
            if (!"full".equals(status)) {
                queryBuilder.append(" AND status = ?");
            }

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());
            int parameterIndex = 1;

            if (!userId.isEmpty()) {
                pstmt.setString(parameterIndex++, "%" + userId + "%");
            }
            if (equipmentId != null) { // Set parameter value only if equipmentId is not null
                pstmt.setInt(parameterIndex++, equipmentId);
            }
            if (!"full".equals(status)) {
                pstmt.setString(parameterIndex++, status);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // Emptying existing data
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
            handleException("Failed to load reservation", e);
        }
    }

    private void loadReservations() {
        loadReservations("", null, "full"); // Use null to indicate that the device ID is not restricted
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case "Add Appointment":
                showAddReservationDialog();
                break;
            case "Update AppointmentUpdate Appointment":
                updateReservation();
                break;
            case "Delete Appointment":
                deleteReservation();
                break;
            case "look for sth.":
                performSearch();
                break;
        }
    }

    private void showAddReservationDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddReservationDialog dialog = new AddReservationDialog(frame);
        dialog.setVisible(true);
        loadReservations(); // Refresh Form
    }

    private void deleteReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select the appointments to be deleted！");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected appointments？", "Confirm deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            executeDatabaseOperation("DELETE FROM reservations WHERE id=?", id);
            JOptionPane.showMessageDialog(null, "Appointment deleted successfully！");
            loadReservations(); // Refresh Form
        }
    }

    private void updateReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select the appointment to update！");
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);

        if (!isReservationIdValid(reservationId)) {
            JOptionPane.showMessageDialog(null, "Invalid Reservation ID：" + reservationId);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UpdateReservationDialog dialog = new UpdateReservationDialog(frame, reservationId);
        dialog.setVisible(true);
        loadReservations(); // Refresh Form
    }

    private boolean isReservationIdValid(int reservationId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM reservations WHERE id=?")) {

            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException ex) {
            handleException("Error while verifying reservation ID", ex);
            return false;
        }
    }

    private void performSearch() {
        String userId = userSearchField.getText().trim();
        String selectedEquipment = (String) equipmentSearchBox.getSelectedItem();
        Integer equipmentId = "full".equals(selectedEquipment) ? null : Integer.parseInt(selectedEquipment); // If "all" then use null to not limit device IDs
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
            handleException("Database operation failed", e);
        }
    }
}
