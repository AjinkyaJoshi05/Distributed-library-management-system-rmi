package client;

import common.Book;
import common.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Clean and simple Admin Java Swing GUI for Library Management.
 * Delegates all admin operations (Add, Remove, View Borrowed) to the remote LibraryService stub.
 */
public class LibraryAdminGUI extends JFrame {

    private final LibraryService libraryService;

    // UI Components
    private JTextField bookIdField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField categoryField;
    private JTable adminBooksTable;
    private DefaultTableModel adminTableModel;

    public LibraryAdminGUI(LibraryService libraryService) {
        this.libraryService = libraryService;

        setTitle("Library Admin Portal (Java RMI GUI)");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        initComponents();
        refreshAdminTable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 1. Top Panel: Header
        JLabel headerLabel = new JLabel("Library Admin Management Portal", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(headerLabel, BorderLayout.NORTH);

        // 2. West Panel: Add Book Form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Book"));

        formPanel.add(new JLabel("Book ID:"));
        bookIdField = new JTextField();
        formPanel.add(bookIdField);

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        formPanel.add(authorField);

        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        formPanel.add(categoryField);

        JButton addBtn = new JButton("Add Book");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBtn.addActionListener(e -> performAddBook());
        formPanel.add(addBtn);

        JButton clearBtn = new JButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());
        formPanel.add(clearBtn);

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        leftContainer.add(formPanel, BorderLayout.NORTH);
        add(leftContainer, BorderLayout.WEST);

        // 3. Center Panel: Books Table & Action Toolbar
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));

        String[] cols = {"Book ID", "Title", "Author", "Category", "Status", "Borrowed By"};
        adminTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        adminBooksTable = new JTable(adminTableModel);
        adminBooksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(adminBooksTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton viewAllBtn = new JButton("View All Books");
        viewAllBtn.addActionListener(e -> refreshAdminTable(false));
        toolbar.add(viewAllBtn);

        JButton viewBorrowedBtn = new JButton("View Currently Borrowed");
        viewBorrowedBtn.addActionListener(e -> refreshAdminTable(true));
        toolbar.add(viewBorrowedBtn);

        JButton removeBtn = new JButton("Remove Selected Book");
        removeBtn.setForeground(Color.RED);
        removeBtn.addActionListener(e -> performRemoveBook());
        toolbar.add(removeBtn);

        centerPanel.add(toolbar, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void performAddBook() {
        if (libraryService == null) return;

        String id = bookIdField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryField.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all book fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // RMI Remote Call
            String res = libraryService.addBook(id, title, author, category);
            JOptionPane.showMessageDialog(this, res, "Admin Operation",
                    res.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            if (res.startsWith("SUCCESS")) {
                clearForm();
                refreshAdminTable(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI Error: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRemoveBook() {
        if (libraryService == null) return;

        int selectedRow = adminBooksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) adminTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove book ID: " + bookId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // RMI Remote Call
                String res = libraryService.removeBook(bookId);
                JOptionPane.showMessageDialog(this, res, "Remove Result",
                        res.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                refreshAdminTable(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "RMI Error: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAdminTable(boolean onlyBorrowed) {
        if (libraryService == null) return;
        try {
            List<Book> books = onlyBorrowed ? libraryService.getCurrentlyBorrowedBooks() : libraryService.getAllBooks();
            adminTableModel.setRowCount(0);
            if (books != null) {
                for (Book b : books) {
                    adminTableModel.addRow(new Object[]{
                            b.getBookId(),
                            b.getTitle(),
                            b.getAuthor(),
                            b.getCategory(),
                            b.isAvailable() ? "Available" : "Borrowed",
                            b.getBorrowedByStudentId() == null ? "-" : b.getBorrowedByStudentId()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI Error fetching data: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        bookIdField.setText("");
        titleField.setText("");
        authorField.setText("");
        categoryField.setText("");
    }
}
