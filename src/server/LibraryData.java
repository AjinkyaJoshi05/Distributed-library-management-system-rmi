package server;

import common.Book;
import common.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates in-memory data storage (HashMap & ArrayList) for Books and Students.
 * Handles thread-safety with synchronized methods to protect shared state from race conditions.
 */
public class LibraryData {
    // Shared state stored in memory using Java Collections
    private final Map<String, Book> booksMap;
    private final Map<String, Student> studentsMap;

    public LibraryData() {
        booksMap = new HashMap<>();
        studentsMap = new HashMap<>();
        seedInitialData();
    }

    /**
     * Seeds initial books into the system for demonstration and testing.
     */
    private void seedInitialData() {
        addBookInternal(new Book("B101", "Distributed Systems: Principles and Paradigms", "Andrew S. Tanenbaum", "Computer Science"));
        addBookInternal(new Book("B102", "Operating System Concepts", "Abraham Silberschatz", "Computer Science"));
        addBookInternal(new Book("B103", "Java Core Volume I - Fundamentals", "Cay S. Horstmann", "Programming"));
        addBookInternal(new Book("B104", "Computer Networking: A Top-Down Approach", "James Kurose", "Networking"));
        addBookInternal(new Book("B105", "Introduction to Algorithms", "Thomas H. Cormen", "Algorithms"));
    }

    private void addBookInternal(Book book) {
        booksMap.put(book.getBookId(), book);
    }

    // --- Thread-Safe Synchronized Public Methods ---

    public synchronized List<Book> getAllBooks() {
        return new ArrayList<>(booksMap.values());
    }

    public synchronized List<Book> searchBooks(String query) {
        List<Book> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Book book : booksMap.values()) {
            if (book.getBookId().toLowerCase().contains(lowerQuery) ||
                book.getTitle().toLowerCase().contains(lowerQuery) ||
                book.getAuthor().toLowerCase().contains(lowerQuery) ||
                book.getCategory().toLowerCase().contains(lowerQuery)) {
                result.add(book);
            }
        }
        return result;
    }

    public synchronized boolean checkAvailability(String bookId) {
        Book book = booksMap.get(bookId);
        return book != null && book.isAvailable();
    }

    public synchronized String borrowBook(String bookId, String studentId, String studentName) {
        Book book = booksMap.get(bookId);

        if (book == null) {
            return "ERROR: Book with ID '" + bookId + "' does not exist.";
        }

        if (!book.isAvailable()) {
            return "ERROR: Book '" + book.getTitle() + "' (ID: " + bookId + ") is already borrowed by Student ID: " + book.getBorrowedByStudentId() + ".";
        }

        // Get or register student
        Student student = studentsMap.computeIfAbsent(studentId, k -> new Student(studentId, studentName));
        student.setName(studentName); // Update name if provided

        // Update book state
        book.setAvailable(false);
        book.setBorrowedByStudentId(studentId);

        // Update student state
        student.addBorrowedBook(bookId);

        return "SUCCESS: Book '" + book.getTitle() + "' successfully borrowed by " + studentName + " (ID: " + studentId + ").";
    }

    public synchronized String returnBook(String bookId, String studentId) {
        Book book = booksMap.get(bookId);

        if (book == null) {
            return "ERROR: Book with ID '" + bookId + "' does not exist.";
        }

        if (book.isAvailable()) {
            return "ERROR: Book '" + book.getTitle() + "' is not currently borrowed by anyone.";
        }

        if (!studentId.equals(book.getBorrowedByStudentId())) {
            return "ERROR: Book '" + book.getTitle() + "' was borrowed by Student ID: " + book.getBorrowedByStudentId() + ", not " + studentId + ".";
        }

        // Update book state
        book.setAvailable(true);
        book.setBorrowedByStudentId(null);

        // Update student state
        Student student = studentsMap.get(studentId);
        if (student != null) {
            student.removeBorrowedBook(bookId);
        }

        return "SUCCESS: Book '" + book.getTitle() + "' successfully returned by Student ID: " + studentId + ".";
    }

    public synchronized List<Book> getBorrowedBooksByStudent(String studentId) {
        List<Book> borrowedList = new ArrayList<>();
        Student student = studentsMap.get(studentId);

        if (student != null) {
            for (String bId : student.getBorrowedBookIds()) {
                Book book = booksMap.get(bId);
                if (book != null) {
                    borrowedList.add(book);
                }
            }
        }
        return borrowedList;
    }

    public synchronized String addBook(String bookId, String title, String author, String category) {
        if (booksMap.containsKey(bookId)) {
            return "ERROR: Book with ID '" + bookId + "' already exists.";
        }

        Book newBook = new Book(bookId, title, author, category);
        booksMap.put(bookId, newBook);
        return "SUCCESS: Book '" + title + "' (ID: " + bookId + ") added successfully.";
    }

    public synchronized String removeBook(String bookId) {
        Book book = booksMap.get(bookId);

        if (book == null) {
            return "ERROR: Book with ID '" + bookId + "' does not exist.";
        }

        if (!book.isAvailable()) {
            return "ERROR: Cannot remove book '" + book.getTitle() + "' because it is currently borrowed by Student ID: " + book.getBorrowedByStudentId() + ".";
        }

        booksMap.remove(bookId);
        return "SUCCESS: Book '" + book.getTitle() + "' (ID: " + bookId + ") removed successfully.";
    }

    public synchronized List<Book> getCurrentlyBorrowedBooks() {
        List<Book> borrowedList = new ArrayList<>();
        for (Book book : booksMap.values()) {
            if (!book.isAvailable()) {
                borrowedList.add(book);
            }
        }
        return borrowedList;
    }
}
