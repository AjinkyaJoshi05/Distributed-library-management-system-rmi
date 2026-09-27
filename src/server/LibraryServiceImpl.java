package server;

import common.Book;
import common.LibraryService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Server-side implementation of the LibraryService remote interface.
 * Extends UnicastRemoteObject to enable RMI communication support.
 */
public class LibraryServiceImpl extends UnicastRemoteObject implements LibraryService {

    private final LibraryData libraryData;

    public LibraryServiceImpl() throws RemoteException {
        super(); // Exports the remote object on an anonymous port
        this.libraryData = new LibraryData();
    }

    @Override
    public List<Book> getAllBooks() throws RemoteException {
        System.out.println("[RMI LOG] Client requested: getAllBooks()");
        return libraryData.getAllBooks();
    }

    @Override
    public List<Book> searchBook(String query) throws RemoteException {
        System.out.println("[RMI LOG] Client requested: searchBook('" + query + "')");
        return libraryData.searchBooks(query);
    }

    @Override
    public boolean checkAvailability(String bookId) throws RemoteException {
        System.out.println("[RMI LOG] Client requested: checkAvailability('" + bookId + "')");
        return libraryData.checkAvailability(bookId);
    }

    @Override
    public String borrowBook(String bookId, String studentId, String studentName) throws RemoteException {
        System.out.println("[RMI LOG] Client requested: borrowBook(BookID: " + bookId + ", StudentID: " + studentId + ")");
        return libraryData.borrowBook(bookId, studentId, studentName);
    }

    @Override
    public String returnBook(String bookId, String studentId) throws RemoteException {
        System.out.println("[RMI LOG] Client requested: returnBook(BookID: " + bookId + ", StudentID: " + studentId + ")");
        return libraryData.returnBook(bookId, studentId);
    }

    @Override
    public List<Book> getBorrowedBooksByStudent(String studentId) throws RemoteException {
        System.out.println("[RMI LOG] Client requested: getBorrowedBooksByStudent('" + studentId + "')");
        return libraryData.getBorrowedBooksByStudent(studentId);
    }

    @Override
    public String addBook(String bookId, String title, String author, String category) throws RemoteException {
        System.out.println("[RMI LOG] Admin requested: addBook(BookID: " + bookId + ", Title: '" + title + "')");
        return libraryData.addBook(bookId, title, author, category);
    }

    @Override
    public String removeBook(String bookId) throws RemoteException {
        System.out.println("[RMI LOG] Admin requested: removeBook(BookID: " + bookId + ")");
        return libraryData.removeBook(bookId);
    }

    @Override
    public List<Book> getCurrentlyBorrowedBooks() throws RemoteException {
        System.out.println("[RMI LOG] Admin requested: getCurrentlyBorrowedBooks()");
        return libraryData.getCurrentlyBorrowedBooks();
    }
}
