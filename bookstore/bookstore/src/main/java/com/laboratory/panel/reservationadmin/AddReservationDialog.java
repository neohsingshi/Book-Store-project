package com.laboratory.panel.reservationadmin;

import com.laboratory.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddReservationDialog extends JDialog implements ActionListener {
    private JComboBox<String> userIdBox; // Stores user identifiers, such as name or ID number
    private JComboBox<Integer> equipmentIdBox;
    private JSpinner reservationTimeSpinner;
    private JComboBox<String> statusBox;
    private JTextArea notesArea;
    private JSpinner durationSpinner;

    private Map<String, Integer> userMap = new HashMap<>(); // Mapping user identifiers to user IDs

    public AddReservationDialog(JFrame owner) {
        super(owner, "Add Appointment", true);
        setResizable(false); // Disable resizing
        initUI();
        loadUsers(); // Loading user information
        loadEquipments(); // Loading device information
    }

    private void initUI() {
        Font globalFont = new Font("Arial", Font.PLAIN, 14);

        // Initializing components
        userIdBox = new JComboBox<>();
        equipmentIdBox = new JComboBox<>();

        reservationTimeSpinner = new JSpinner(new SpinnerDateModel());
        reservationTimeSpinner.setEditor(new JSpinner.DateEditor(reservationTimeSpinner, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DefaultEditor) reservationTimeSpinner.getEditor()).getTextField().setEditable(false);

        statusBox = new JComboBox<>(new String[]{"To be confirmed", "confirmed", "done", "Cancelled"});
        notesArea = new JTextArea(5, 20);
        notesArea.setFont(globalFont);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setEditable(false);

        // Creating and setting up panels
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // user selectable area
        JPanel userSelectionPanel = createTitledPanel("user-selected", new GridLayout(2, 2, 10, 10));
        userSelectionPanel.add(new JLabel("subscribers:", SwingConstants.RIGHT));
        userSelectionPanel.add(userIdBox);
        userSelectionPanel.add(new JLabel("Device ID:", SwingConstants.RIGHT));
        userSelectionPanel.add(equipmentIdBox);

        // Reservation Details Area
        JPanel reservationDetailsPanel = createTitledPanel("Reservation Details", new GridLayout(4, 2, 10, 10));
        reservationDetailsPanel.add(new JLabel("Reservation Time:", SwingConstants.RIGHT));
        reservationDetailsPanel.add(reservationTimeSpinner);
        reservationDetailsPanel.add(new JLabel("Length of appointment (hours):", SwingConstants.RIGHT));
        reservationDetailsPanel.add(durationSpinner);
        reservationDetailsPanel.add(new JLabel("state of affairs:", SwingConstants.RIGHT));
        reservationDetailsPanel.add(statusBox);
        reservationDetailsPanel.add(new JLabel("note:", SwingConstants.RIGHT));
        reservationDetailsPanel.add(new JScrollPane(notesArea));

        // button area
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("add");;
        JButton cancelButton = new JButton("cancel");
        addButton.addActionListener(this);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add to main panel
        addComponent(panel, gbc, userSelectionPanel, 0, 0, 2, 1);
        addComponent(panel, gbc, reservationDetailsPanel, 0, 1, 2, 1);
        addComponent(panel, gbc, buttonPanel, 0, 2, 2, 1, 1.0, 1.0);

        add(panel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createTitledPanel(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private JButton createButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setMinimumSize(new Dimension(80, 30));
        button.setPreferredSize(new Dimension(80, 30));
        return button;
    }

    private void addComponent(JPanel panel, GridBagConstraints gbc, Component component, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        panel.add(component, gbc);
    }

    private void addComponent(JPanel panel, GridBagConstraints gbc, Component component, int gridx, int gridy, int gridwidth, int gridheight) {
        addComponent(panel, gbc, component, gridx, gridy, gridwidth, gridheight, 0, 0);
    }

    private void loadUsers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM users");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String userName = rs.getString("name");
                int userId = rs.getInt("id");
                userMap.put(userName, userId);
                userIdBox.addItem(userName);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load user：" + e.getMessage(), "incorrect", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEquipments() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM equipments");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                equipmentIdBox.addItem(rs.getInt("id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load device：" + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("add".equals(e.getActionCommand())) {
            // input validation
            if (userIdBox.getSelectedItem() == null ||
                    equipmentIdBox.getSelectedItem() == null ||
                    reservationTimeSpinner.getValue() == null ||
                    statusBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please make sure all required fields are filled in！", "input error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String selectedUserName = (String) userIdBox.getSelectedItem();
                Integer userId = userMap.get(selectedUserName);
                if (userId == null) {
                    JOptionPane.showMessageDialog(this, "The user provided does not exist, please select a valid user.", "input error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Integer equipmentId = (Integer) equipmentIdBox.getSelectedItem();
                Date reservationTime = (Date) reservationTimeSpinner.getValue();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                // Check that the appointment is within the permitted time slot
                if (!isWithinAllowedTime(reservationTime)) {
                    JOptionPane.showMessageDialog(this, "Appointment time is not within the permitted time period（9:00-21:00).", "timing error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Check for conflicting appointments
                if (isConflictingReservation(conn, equipmentId, reservationTime, durationSpinner.getValue())) {
                    JOptionPane.showMessageDialog(this, "There is already a booking for this time slot, please choose another time.", "Booking Conflicts", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String status = (String) statusBox.getSelectedItem();
                String notes = notesArea.getText().trim();
                int duration = (Integer) durationSpinner.getValue();

                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO reservations (user_id, equipment_id, reservation_time, duration, status, notes) VALUES (?, ?, ?, ?, ?, ?)");
                pstmt.setInt(1, userId);
                pstmt.setInt(2, equipmentId);
                pstmt.setTimestamp(3, new java.sql.Timestamp(reservationTime.getTime()));
                pstmt.setInt(4, duration);
                pstmt.setString(5, status);
                pstmt.setString(6, notes);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment added successfully！", "The operation was successful.", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close dialogue box
                } else {
                    JOptionPane.showMessageDialog(this, "Appointment addition failed, please try again later.", "failure of an operation", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add appointment：" + ex.getMessage(), "SQL incorrect", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isWithinAllowedTime(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timeStr = sdf.format(time);
        return !(timeStr.compareTo("09:00") < 0) && !(timeStr.compareTo("21:00") > 0);
    }

    private boolean isConflictingReservation(Connection conn, Integer equipmentId, Date startTime, Object duration) {
        try {
            int durationInt = (int) duration;
            long endTimeMillis = startTime.getTime() + (long) durationInt * 60 * 60 * 1000;
            Date endTime = new Date(endTimeMillis);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM reservations WHERE equipment_id = ? AND reservation_time <= ? AND DATE_ADD(reservation_time, INTERVAL duration HOUR) >= ?");
            pstmt.setInt(1, equipmentId);
            pstmt.setTimestamp(2, new java.sql.Timestamp(endTime.getTime()));
            pstmt.setTimestamp(3, new java.sql.Timestamp(startTime.getTime()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
