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

        // Initialize table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Price", "Stock", "Category"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Title:"));
        titleSearchField = new JTextField(15);
        searchPanel.add(titleSearchField);

        searchPanel.add(new JLabel("Category:"));
        categorySearchBox = new JComboBox<>(); // Do not immediately set to "All"
        searchPanel.add(categorySearchBox);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Book");
        updateButton = new JButton("Update Book");
        deleteButton = new JButton("Delete Book");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCategories(); // Load category list and set default value
        loadBooks(); // Load all books data
    }

    private void loadCategories() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT category FROM books");
            ResultSet rs = pstmt.executeQuery();

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("All");

            while (rs.next()) {
                model.addElement(rs.getString("category"));
            }

            categorySearchBox.setModel(model);
            categorySearchBox.setSelectedItem("All");

        } catch (SQLException e) {
            e.printStackTrace();
            showCustomErrorDialog("Failed to load categories: " + e.getMessage());
        }
    }

    private void loadBooks(String title, String category) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");

            if (!title.isEmpty()) {
                queryBuilder.append(" AND title LIKE ?");
            }
            if (!"All".equals(category)) {
                queryBuilder.append(" AND category = ?");
            }

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            int parameterIndex = 1;
            if (!title.isEmpty()) {
                pstmt.setString(parameterIndex++, "%" + title + "%");
            }
            if (!"All".equals(category)) {
                pstmt.setString(parameterIndex++, category);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // Clear existing data
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
            showCustomErrorDialog("Failed to load books: " + e.getMessage());
        }
    }

    private void loadBooks() {
        loadBooks("", "All");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if ("Add Book".equals(actionCommand)) {
            showAddBookDialog();
        } else if ("Update Book".equals(actionCommand)) {
            updateBook();
        } else if ("Delete Book".equals(actionCommand)) {
            deleteBook();
        } else if ("Search".equals(actionCommand)) {
            performSearch();
        }
    }

    private void showCustomMessageDialog(String message, String title, int messageType) {
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, messageType, null, options, options[0]);
    }

    private void showCustomErrorDialog(String message) {
        showCustomMessageDialog(message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showAddBookDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddBookDialog dialog = new AddBookDialog(frame);
        dialog.setVisible(true);
        loadBooks(); // Refresh table
        loadCategories(); // Update dropdown box data
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            showCustomMessageDialog("Please select a book to delete!", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        // Define the options for the confirm dialog explicitly as English strings
        Object[] options = {"Yes", "No"};

        int confirm = JOptionPane.showOptionDialog(
                null,
                "Are you sure you want to delete the selected book?",
                "Confirm Delete",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, // No custom icon
                options, // The options array
                options[1] // Default option is "No"
        );

        if (confirm == JOptionPane.YES_OPTION) { // Check if "Yes" was selected
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id=?")) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                showCustomMessageDialog("Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks(); // Refresh table
                loadCategories(); // Update dropdown box data

            } catch (SQLException ex) {
                ex.printStackTrace();
                showCustomErrorDialog("Failed to delete book: " + ex.getMessage());
            }
        }
    }

    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            showCustomMessageDialog("Please select a book to update!", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        if (!isBookIdValid(bookId)) {
            showCustomMessageDialog("Invalid book ID: " + bookId, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UpdateBookDialog dialog = new UpdateBookDialog(frame, bookId);
        dialog.setVisible(true);
        loadBooks(); // Refresh table
        loadCategories(); // Update dropdown box data
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
