package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Student in the Library System.
 * Note: Must implement Serializable to be passed between Client and Server in Java RMI.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String name;
    private List<String> borrowedBookIds; // List of book IDs borrowed by this student

    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.borrowedBookIds = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBorrowedBookIds() {
        return borrowedBookIds;
    }

    public void addBorrowedBook(String bookId) {
        if (!borrowedBookIds.contains(bookId)) {
            borrowedBookIds.add(bookId);
        }
    }

    public void removeBorrowedBook(String bookId) {
        borrowedBookIds.remove(bookId);
    }

    @Override
    public String toString() {
        return String.format("Student ID: %s | Name: %s | Borrowed Books Count: %d",
                studentId, name, borrowedBookIds.size());
    }
}
