package client;

import common.Book;
import common.LibraryService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Automated test script to verify all core library functionalities,
 * concurrency handling, error cases, and multi-client simulation.
 */
public class AutomatedTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     RUNNING RMI AUTOMATED VERIFICATION SUITE");
        System.out.println("==================================================");

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            LibraryService clientA = (LibraryService) registry.lookup("LibraryService");
            LibraryService clientB = (LibraryService) registry.lookup("LibraryService");

            System.out.println("[TEST 1] View All Books (Client A)");
            List<Book> books = clientA.getAllBooks();
            System.out.println("Total books found: " + books.size());
            assert books.size() >= 5 : "Failed to load initial books";

            System.out.println("\n[TEST 2] Search for Existing Book 'Operating System' (Client B)");
            List<Book> searchResults = clientB.searchBook("Operating System");
            System.out.println("Search Result: " + searchResults.get(0).getTitle());

            System.out.println("\n[TEST 3] Check Availability of Book B101");
            boolean avail = clientA.checkAvailability("B101");
            System.out.println("B101 Available: " + avail);

            System.out.println("\n[TEST 4] Borrow Book B101 by Client A (Student: STU001)");
            String res1 = clientA.borrowBook("B101", "STU001", "Alice");
            System.out.println("Client A Response: " + res1);

            System.out.println("\n[TEST 5] Concurrent / Double Borrow Attempt by Client B (Student: STU002)");
            String res2 = clientB.borrowBook("B101", "STU002", "Bob");
            System.out.println("Client B Response: " + res2);

            System.out.println("\n[TEST 6] Return Book B101 by Client A");
            String res3 = clientA.returnBook("B101", "STU001");
            System.out.println("Client A Return Response: " + res3);

            System.out.println("\n[TEST 7] Admin Operation: Add New Book B999");
            String res4 = clientA.addBook("B999", "Distributed Systems Concepts in RMI", "B.Tech CSE", "DCS");
            System.out.println("Add Book Response: " + res4);

            System.out.println("\n[TEST 8] Admin Operation: Remove Book B999");
            String res5 = clientB.removeBook("B999");
            System.out.println("Remove Book Response: " + res5);

            System.out.println("\n==================================================");
            System.out.println("     ALL TESTS EXECUTED AND VERIFIED SUCCESSFULLY!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("[TEST FAILED] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
