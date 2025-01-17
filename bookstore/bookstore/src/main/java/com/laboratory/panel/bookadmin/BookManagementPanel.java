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

        // Initialising the form
        tableModel = new DefaultTableModel(new Object[]{"ID", "reputation as calligrapher", "prices", "property or cash held in reserve", "form"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("reputation as calligrapher:"));
        titleSearchField = new JTextField(15);
        searchPanel.add(titleSearchField);

        searchPanel.add(new JLabel("form:"));
        categorySearchBox = new JComboBox<>(); // No longer immediately set to "All"
        searchPanel.add(categorySearchBox);

        searchButton = new JButton("search");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add a book");
        updateButton = new JButton("Updated books");
        deleteButton = new JButton("Delete books");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCategories(); // Load a list of categories and set default values
        loadBooks(); // Load all book data
    }

    private void loadCategories() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT category FROM books");
            ResultSet rs = pstmt.executeQuery();

            // Ensure that "all" is the first element added
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("all");

            while (rs.next()) {
                model.addElement(rs.getString("category"));
            }

            // Update category drop-down box
            categorySearchBox.setModel(model);

            // The default selection is "All"
            categorySearchBox.setSelectedItem("all");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load category：" + e.getMessage());
        }
    }

    private void loadBooks(String title, String category) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");

            if (!title.isEmpty()) {
                queryBuilder.append(" AND title LIKE ?");
            }
            if (!"all".equals(category)) {
                queryBuilder.append(" AND category = ?");
            }

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            int parameterIndex = 1;
            if (!title.isEmpty()) {
                pstmt.setString(parameterIndex++, "%" + title + "%");
            }
            if (!"all".equals(category)) {
                pstmt.setString(parameterIndex++, category);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // Emptying existing data
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
            JOptionPane.showMessageDialog(null, "Failed to load book：" + e.getMessage());
        }
    }

    private void loadBooks() {
        loadBooks("", "all");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if ("Add a book".equals(actionCommand)) {
            showAddBookDialog();
        } else if ("Updated books".equals(actionCommand)) {
            updateBook();
        } else if ("Delete books".equals(actionCommand)) {
            deleteBook();
        } else if ("search".equals(actionCommand)) {
            performSearch();
        }
    }

    private void showAddBookDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddBookDialog dialog = new AddBookDialog(frame);
        dialog.setVisible(true);
        loadBooks(); // Refresh Form
        loadCategories(); // Update drop-down box data
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select the books to be deleted！");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected books?？", "Confirm deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id=?")) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book deleted successfully！");
                loadBooks(); // Refresh Form
                loadCategories(); // Update drop-down box data

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Book Deletion Failure：" + ex.getMessage());
            }
        }
    }

    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select the books to be updated！");
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        // Optional: verify that the bookId exists
        if (!isBookIdValid(bookId)) {
            JOptionPane.showMessageDialog(null, "Invalid Book ID：" + bookId);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UpdateBookDialog dialog = new UpdateBookDialog(frame, bookId);
        dialog.setVisible(true);
        loadBooks(); // Refresh Form
        loadCategories(); // Update drop-down box data
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
