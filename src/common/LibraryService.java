package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface defining all operations exposed by the Remote Library Service.
 * Must extend java.rmi.Remote, and every method must throw RemoteException.
 */
public interface LibraryService extends Remote {

    // --- Student Operations ---
    List<Book> getAllBooks() throws RemoteException;
    
    List<Book> searchBook(String query) throws RemoteException;
    
    boolean checkAvailability(String bookId) throws RemoteException;
    
    String borrowBook(String bookId, String studentId, String studentName) throws RemoteException;
    
    String returnBook(String bookId, String studentId) throws RemoteException;
    
    List<Book> getBorrowedBooksByStudent(String studentId) throws RemoteException;

    // --- Admin Operations ---
    String addBook(String bookId, String title, String author, String category) throws RemoteException;
    
    String removeBook(String bookId) throws RemoteException;
    
    List<Book> getCurrentlyBorrowedBooks() throws RemoteException;
}
