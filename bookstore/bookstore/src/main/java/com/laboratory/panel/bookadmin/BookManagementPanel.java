package com.laboratory.panel.bookadmin;

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

public class BookManagementPanel extends JPanel implements ActionListener {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JTextField titleSearchField;
    private JComboBox<String> categorySearchBox;

    public BookManagementPanel() {
        setLayout(new BorderLayout());

        // 初始化表格
        tableModel = new DefaultTableModel(new Object[]{"ID", "书名", "价格", "库存", "类别"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // 搜索面板
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("书名:"));
        titleSearchField = new JTextField(15);
        searchPanel.add(titleSearchField);

        searchPanel.add(new JLabel("类别:"));
        categorySearchBox = new JComboBox<>(); // 不再立即设置为 "全部"
        searchPanel.add(categorySearchBox);

        searchButton = new JButton("搜索");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加书籍");
        updateButton = new JButton("更新书籍");
        deleteButton = new JButton("删除书籍");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCategories(); // 加载类别列表并设置默认值
        loadBooks(); // 加载所有书籍数据
    }

    private void loadCategories() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT category FROM books");
            ResultSet rs = pstmt.executeQuery();

            // 确保 "全部" 是第一个添加的元素
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("全部");

            while (rs.next()) {
                model.addElement(rs.getString("category"));
            }

            // 更新类别下拉框
            categorySearchBox.setModel(model);

            // 默认选择 "全部"
            categorySearchBox.setSelectedItem("全部");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载类别失败：" + e.getMessage());
        }
    }

    private void loadBooks(String title, String category) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");

            if (!title.isEmpty()) {
                queryBuilder.append(" AND title LIKE ?");
            }
            if (!"全部".equals(category)) {
                queryBuilder.append(" AND category = ?");
            }

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            int parameterIndex = 1;
            if (!title.isEmpty()) {
                pstmt.setString(parameterIndex++, "%" + title + "%");
            }
            if (!"全部".equals(category)) {
                pstmt.setString(parameterIndex++, category);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // 清空现有数据
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock"),
                        rs.getString("category")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载书籍失败：" + e.getMessage());
        }
    }

    private void loadBooks() {
        loadBooks("", "全部");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if ("添加书籍".equals(actionCommand)) {
            showAddBookDialog();
        } else if ("更新书籍".equals(actionCommand)) {
            updateBook();
        } else if ("删除书籍".equals(actionCommand)) {
            deleteBook();
        } else if ("搜索".equals(actionCommand)) {
            performSearch();
        }
    }

    private void showAddBookDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddBookDialog dialog = new AddBookDialog(frame);
        dialog.setVisible(true);
        loadBooks(); // 刷新表格
        loadCategories(); // 更新下拉框数据
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "请选择要删除的书籍！");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "确定要删除选中的书籍吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id=?")) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "书籍删除成功！");
                loadBooks(); // 刷新表格
                loadCategories(); // 更新下拉框数据

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "书籍删除失败：" + ex.getMessage());
            }
        }
    }

    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "请选择要更新的书籍！");
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        // 可选：验证 bookId 是否存在
        if (!isBookIdValid(bookId)) {
            JOptionPane.showMessageDialog(null, "无效的书籍ID：" + bookId);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UpdateBookDialog dialog = new UpdateBookDialog(frame, bookId);
        dialog.setVisible(true);
        loadBooks(); // 刷新表格
        loadCategories(); // 更新下拉框数据
    }

    private boolean isBookIdValid(int bookId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM books WHERE id=?")) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void performSearch() {
        String title = titleSearchField.getText().trim();
        String category = (String) categorySearchBox.getSelectedItem();
        loadBooks(title, category);
    }
}
