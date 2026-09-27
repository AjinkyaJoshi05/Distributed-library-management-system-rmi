package client;

import common.Book;
import common.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Clean and simple Java Swing GUI for the RMI Distributed Library System.
 * Connects to the RMI Registry and delegates all library operations to the remote LibraryService stub.
 */
public class LibraryClientGUI extends JFrame {

    private static final String REGISTRY_HOST = "localhost";
    private static final int REGISTRY_PORT = 1099;
    private static final String SERVICE_NAME = "LibraryService";

    // Remote RMI Stub Reference
    private LibraryService libraryService;

    // UI Components
    private JLabel statusLabel;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField searchField;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public LibraryClientGUI() {
        setTitle("Distributed Library Management System (Java RMI GUI)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Initialize UI Layout
        initComponents();

        // Connect to RMI Registry
        connectToRMI();

        // Load Initial Book List via RMI
        refreshBooksTable();
    }

    /**
     * Step 1 & 2: Connect to RMI Registry and lookup LibraryService stub.
     */
    private void connectToRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry(REGISTRY_HOST, REGISTRY_PORT);
            libraryService = (LibraryService) registry.lookup(SERVICE_NAME);
            statusLabel.setText("Status: Connected to RMI Server at " + REGISTRY_HOST + ":" + REGISTRY_PORT);
            statusLabel.setForeground(new Color(0, 128, 0)); // Green color
        } catch (Exception e) {
            statusLabel.setText("Status: Unable to connect to RMI Server (" + e.getMessage() + ")");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to RMI Server.\nPlease ensure LibraryServer is running on port 1099.",
                    "RMI Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 1. Top Panel: Header & RMI Connection Status
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        JLabel titleLabel = new JLabel("Distributed Library Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        statusLabel = new JLabel("Status: Connecting...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        topPanel.add(statusLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // 2. Center Panel: Student Details, Search Bar & JTable
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Student Info Panel & Search Panel Combined
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel studentInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        studentInfoPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField("STU101", 10);
        studentInfoPanel.add(studentIdField);

        studentInfoPanel.add(new JLabel("Student Name:"));
        studentNameField = new JTextField("Alice", 12);
        studentInfoPanel.add(studentNameField);

        JButton adminGuiBtn = new JButton("Open Admin Portal");
        adminGuiBtn.addActionListener(e -> new LibraryAdminGUI(libraryService).setVisible(true));
        studentInfoPanel.add(adminGuiBtn);

        inputPanel.add(studentInfoPanel);

        // Search Bar Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Search Book:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn);

        JButton refreshBtn = new JButton("View All / Refresh");
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            refreshBooksTable();
        });
        searchPanel.add(refreshBtn);

        inputPanel.add(searchPanel);
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // Books Table
        String[] columnNames = {"Book ID", "Title", "Author", "Category", "Availability", "Borrowed By"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table rows uneditable
            }
        };
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Panel: Action Buttons (Borrow, Return, My Borrowed Books)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton borrowBtn = new JButton("Borrow Selected Book");
        borrowBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        borrowBtn.addActionListener(e -> performBorrow());
        actionPanel.add(borrowBtn);

        JButton returnBtn = new JButton("Return Selected Book");
        returnBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        returnBtn.addActionListener(e -> performReturn());
        actionPanel.add(returnBtn);

        JButton myBooksBtn = new JButton("My Borrowed Books");
        myBooksBtn.addActionListener(e -> performViewMyBooks());
        actionPanel.add(myBooksBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        actionPanel.add(exitBtn);

        add(actionPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes table by calling remote getAllBooks() over RMI.
     */
    private void refreshBooksTable() {
        if (libraryService == null) return;
        try {
            List<Book> books = libraryService.getAllBooks();
            updateTableData(books);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI Error fetching books: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Searches books by calling remote searchBook() over RMI.
     */
    private void performSearch() {
        if (libraryService == null) return;
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            refreshBooksTable();
            return;
        }
        try {
            List<Book> books = libraryService.searchBook(query);
            updateTableData(books);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI Error searching books: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Borrows selected book by calling remote borrowBook() over RMI.
     */
    private void performBorrow() {
        if (libraryService == null) return;

        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to borrow.", "No Book Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();

        if (studentId.isEmpty() || studentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Student ID and Student Name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // RMI Remote Call
            String response = libraryService.borrowBook(bookId, studentId, studentName);
            
            // Display exact server response
            JOptionPane.showMessageDialog(this, response, "Borrow Result", 
                    response.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            // Refresh table to show updated availability from server
            refreshBooksTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI Exception: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns selected book by calling remote returnBook() over RMI.
     */
    private void performReturn() {
        if (libraryService == null) return;

        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to return.", "No Book Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentId = studentIdField.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Student ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // RMI Remote Call
            String response = libraryService.returnBook(bookId, studentId);

            // Display exact server response
            JOptionPane.showMessageDialog(this, response, "Return Result",
                    response.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            // Refresh table to show updated availability from server
            refreshBooksTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI Exception: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fetches borrowed books for current student by calling remote getBorrowedBooksByStudent() over RMI.
     */
    private void performViewMyBooks() {
        if (libraryService == null) return;

        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Student ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Book> borrowedBooks = libraryService.getBorrowedBooksByStudent(studentId);
            if (borrowedBooks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No books currently borrowed by Student ID: " + studentId, "My Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder sb = new StringBuilder("Books borrowed by " + studentId + ":\n\n");
                for (Book b : borrowedBooks) {
                    sb.append("• [").append(b.getBookId()).append("] ").append(b.getTitle()).append(" (Author: ").append(b.getAuthor()).append(")\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "My Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI Error: " + e.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableData(List<Book> books) {
        tableModel.setRowCount(0);
        if (books != null) {
            for (Book b : books) {
                tableModel.addRow(new Object[]{
                        b.getBookId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getCategory(),
                        b.isAvailable() ? "Available" : "Borrowed",
                        b.getBorrowedByStudentId() == null ? "-" : b.getBorrowedByStudentId()
                });
            }
        }
    }

    public static void main(String[] args) {
        // Launch Swing GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LibraryClientGUI gui = new LibraryClientGUI();
            gui.setVisible(true);
        });
    }
}
