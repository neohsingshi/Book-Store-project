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
    private JComboBox<String> userIdBox; // 存储用户标识符，如姓名或ID号
    private JComboBox<Integer> equipmentIdBox;
    private JSpinner reservationTimeSpinner;
    private JComboBox<String> statusBox;
    private JTextArea notesArea;
    private JSpinner durationSpinner;

    private Map<String, Integer> userMap = new HashMap<>(); // 映射用户标识符到用户ID

    public AddReservationDialog(JFrame owner) {
        super(owner, "添加预约", true);
        setResizable(false); // 禁止调整大小
        initUI();
        loadUsers(); // 加载用户信息
        loadEquipments(); // 加载设备信息
    }

    private void initUI() {
        Font globalFont = new Font("Arial", Font.PLAIN, 14);

        // 初始化组件
        userIdBox = new JComboBox<>();
        equipmentIdBox = new JComboBox<>();

        reservationTimeSpinner = new JSpinner(new SpinnerDateModel());
        reservationTimeSpinner.setEditor(new JSpinner.DateEditor(reservationTimeSpinner, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DefaultEditor) reservationTimeSpinner.getEditor()).getTextField().setEditable(false);

        statusBox = new JComboBox<>(new String[]{"待确认", "已确认", "已完成", "已取消"});
        notesArea = new JTextArea(5, 20);
        notesArea.setFont(globalFont);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setEditable(false);

        // 创建并设置面板
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户选择区
        JPanel userSelectionPanel = createTitledPanel("用户选择", new GridLayout(2, 2, 10, 10));
        userSelectionPanel.add(new JLabel("用户:", SwingConstants.RIGHT));
        userSelectionPanel.add(userIdBox);
        userSelectionPanel.add(new JLabel("设备ID:", SwingConstants.RIGHT));
        userSelectionPanel.add(equipmentIdBox);

        // 预约详情区
        JPanel reservationDetailsPanel = createTitledPanel("预约详情", new GridLayout(4, 2, 10, 10));
        reservationDetailsPanel.add(new JLabel("预约时间:", SwingConstants.RIGHT));
        reservationDetailsPanel.add(reservationTimeSpinner);
        reservationDetailsPanel.add(new JLabel("预约时长（小时）:", SwingConstants.RIGHT));
        reservationDetailsPanel.add(durationSpinner);
        reservationDetailsPanel.add(new JLabel("状态:", SwingConstants.RIGHT));
        reservationDetailsPanel.add(statusBox);
        reservationDetailsPanel.add(new JLabel("备注:", SwingConstants.RIGHT));
        reservationDetailsPanel.add(new JScrollPane(notesArea));

        // 按钮区
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("添加");;
        JButton cancelButton = new JButton("取消");
        addButton.addActionListener(this);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // 添加到主面板
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
            JOptionPane.showMessageDialog(null, "加载用户失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "加载设备失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("添加".equals(e.getActionCommand())) {
            // 输入验证
            if (userIdBox.getSelectedItem() == null ||
                    equipmentIdBox.getSelectedItem() == null ||
                    reservationTimeSpinner.getValue() == null ||
                    statusBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "请确保所有必填项均已填写！", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String selectedUserName = (String) userIdBox.getSelectedItem();
                Integer userId = userMap.get(selectedUserName);
                if (userId == null) {
                    JOptionPane.showMessageDialog(this, "提供的用户不存在，请选择有效的用户。", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Integer equipmentId = (Integer) equipmentIdBox.getSelectedItem();
                Date reservationTime = (Date) reservationTimeSpinner.getValue();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                // 检查预约时间是否在允许的时间段内
                if (!isWithinAllowedTime(reservationTime)) {
                    JOptionPane.showMessageDialog(this, "预约时间不在允许的时间段内（9:00-21:00）。", "时间错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 检查是否有冲突的预约
                if (isConflictingReservation(conn, equipmentId, reservationTime, durationSpinner.getValue())) {
                    JOptionPane.showMessageDialog(this, "该时间段内已有预约，请选择其他时间。", "预约冲突", JOptionPane.WARNING_MESSAGE);
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
                    JOptionPane.showMessageDialog(this, "预约添加成功！", "操作成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭对话框
                } else {
                    JOptionPane.showMessageDialog(this, "预约添加失败，请稍后再试。", "操作失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "预约添加失败：" + ex.getMessage(), "SQL 错误", JOptionPane.ERROR_MESSAGE);
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
