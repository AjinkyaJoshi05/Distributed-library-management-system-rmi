package common;

import java.io.Serializable;

/**
 * Represents a Book in the Library System.
 * Note: Must implement Serializable to be passed between Client and Server in Java RMI.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bookId;
    private String title;
    private String author;
    private String category;
    private boolean isAvailable;
    private String borrowedByStudentId; // Tracks which student currently borrowed this book (null if available)

    public Book(String bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isAvailable = true;
        this.borrowedByStudentId = null;
    }

    // Getters and Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getBorrowedByStudentId() {
        return borrowedByStudentId;
    }

    public void setBorrowedByStudentId(String borrowedByStudentId) {
        this.borrowedByStudentId = borrowedByStudentId;
    }

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : ("Borrowed by Student ID: " + borrowedByStudentId);
        return String.format("[%s] Title: \"%s\" | Author: %s | Category: %s | Status: %s",
                bookId, title, author, category, status);
    }
}
