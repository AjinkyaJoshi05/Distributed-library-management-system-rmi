package client;

import common.Book;
import common.LibraryService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/**
 * Client application for the Distributed Library Management System.
 * Connects to RMI Registry, looks up LibraryService stub, and presents a console UI.
 */
public class LibraryClient {

    private static final String REGISTRY_HOST = "localhost";
    private static final int REGISTRY_PORT = 1099;
    private static final String SERVICE_NAME = "LibraryService";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("==================================================");
            System.out.println("      Distributed Library Management System");
            System.out.println("                   (Java RMI)");
            System.out.println("==================================================");
            System.out.println("Connecting to RMI Registry at " + REGISTRY_HOST + ":" + REGISTRY_PORT + "...");

            // 1. Locate the RMI Registry
            Registry registry = LocateRegistry.getRegistry(REGISTRY_HOST, REGISTRY_PORT);

            // 2. Look up the Remote Object stub by service name
            LibraryService libraryService = (LibraryService) registry.lookup(SERVICE_NAME);

            System.out.println("[SUCCESS] Successfully connected to Library Service!");

            // Main Console Menu Loop
            boolean running = true;
            while (running) {
                printMainMenu();
                System.out.print("Select Role / Option (1-3): ");
                String roleChoice = scanner.nextLine().trim();

                switch (roleChoice) {
                    case "1":
                        handleStudentMenu(scanner, libraryService);
                        break;
                    case "2":
                        handleAdminMenu(scanner, libraryService);
                        break;
                    case "3":
                        running = false;
                        System.out.println("\nThank you for using the Library System. Goodbye!");
                        break;
                    default:
                        System.out.println("[INVALID] Please select a valid option (1-3).");
                }
            }

        } catch (Exception e) {
            System.err.println("\n[RMI ERROR] Could not connect to RMI Server.");
            System.err.println("Make sure 'LibraryServer' is started before launching the client.");
            System.err.println("Error Details: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void printMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("                  MAIN MENU");
        System.out.println("==================================================");
        System.out.println("1. Student Portal");
        System.out.println("2. Admin Portal");
        System.out.println("3. Exit System");
        System.out.println("==================================================");
    }

    // --- Student Portal Menu ---

    private static void handleStudentMenu(Scanner scanner, LibraryService libraryService) {
        boolean studentLoop = true;
        while (studentLoop) {
            System.out.println("\n--- STUDENT PORTAL ---");
            System.out.println("1. View All Books");
            System.out.println("2. Search for a Book");
            System.out.println("3. Check Book Availability");
            System.out.println("4. Borrow a Book");
            System.out.println("5. Return a Book");
            System.out.println("6. View My Borrowed Books");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter option (1-7): ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        displayBooks("ALL BOOKS IN LIBRARY", libraryService.getAllBooks());
                        break;
                    case "2":
                        System.out.print("Enter search keyword (Title/Author/Category/ID): ");
                        String query = scanner.nextLine().trim();
                        displayBooks("SEARCH RESULTS", libraryService.searchBook(query));
                        break;
                    case "3":
                        System.out.print("Enter Book ID to check availability: ");
                        String bookIdCheck = scanner.nextLine().trim();
                        boolean available = libraryService.checkAvailability(bookIdCheck);
                        if (available) {
                            System.out.println("[STATUS] Book ID '" + bookIdCheck + "' is AVAILABLE.");
                        } else {
                            System.out.println("[STATUS] Book ID '" + bookIdCheck + "' is NOT available (either borrowed or non-existent).");
                        }
                        break;
                    case "4":
                        System.out.print("Enter Book ID to borrow: ");
                        String borrowBookId = scanner.nextLine().trim();
                        System.out.print("Enter your Student ID (e.g., BT23CSE046): ");
                        String studentId = scanner.nextLine().trim();
                        System.out.print("Enter your Name: ");
                        String studentName = scanner.nextLine().trim();

                        String borrowResult = libraryService.borrowBook(borrowBookId, studentId, studentName);
                        System.out.println("\n" + borrowResult);
                        break;
                    case "5":
                        System.out.print("Enter Book ID to return: ");
                        String returnBookId = scanner.nextLine().trim();
                        System.out.print("Enter your Student ID: ");
                        String retStudentId = scanner.nextLine().trim();

                        String returnResult = libraryService.returnBook(returnBookId, retStudentId);
                        System.out.println("\n" + returnResult);
                        break;
                    case "6":
                        System.out.print("Enter your Student ID: ");
                        String viewStudentId = scanner.nextLine().trim();
                        displayBooks("BOOKS BORROWED BY " + viewStudentId, libraryService.getBorrowedBooksByStudent(viewStudentId));
                        break;
                    case "7":
                        studentLoop = false;
                        break;
                    default:
                        System.out.println("[INVALID] Please select a valid student menu option (1-7).");
                }
            } catch (Exception e) {
                System.err.println("[RMI EXCEPTION] Error executing remote request: " + e.getMessage());
            }
        }
    }

    // --- Admin Portal Menu ---

    private static void handleAdminMenu(Scanner scanner, LibraryService libraryService) {
        boolean adminLoop = true;
        while (adminLoop) {
            System.out.println("\n--- ADMIN PORTAL ---");
            System.out.println("1. Add a New Book");
            System.out.println("2. Remove a Book");
            System.out.println("3. View All Books");
            System.out.println("4. View Currently Borrowed Books");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter option (1-5): ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter Book ID (e.g., B106): ");
                        String newId = scanner.nextLine().trim();
                        System.out.print("Enter Book Title: ");
                        String title = scanner.nextLine().trim();
                        System.out.print("Enter Author Name: ");
                        String author = scanner.nextLine().trim();
                        System.out.print("Enter Category: ");
                        String category = scanner.nextLine().trim();

                        String addResult = libraryService.addBook(newId, title, author, category);
                        System.out.println("\n" + addResult);
                        break;
                    case "2":
                        System.out.print("Enter Book ID to remove: ");
                        String removeId = scanner.nextLine().trim();

                        String removeResult = libraryService.removeBook(removeId);
                        System.out.println("\n" + removeResult);
                        break;
                    case "3":
                        displayBooks("ALL BOOKS IN LIBRARY", libraryService.getAllBooks());
                        break;
                    case "4":
                        displayBooks("CURRENTLY BORROWED BOOKS", libraryService.getCurrentlyBorrowedBooks());
                        break;
                    case "5":
                        adminLoop = false;
                        break;
                    default:
                        System.out.println("[INVALID] Please select a valid admin menu option (1-5).");
                }
            } catch (Exception e) {
                System.err.println("[RMI EXCEPTION] Error executing admin remote request: " + e.getMessage());
            }
        }
    }

    private static void displayBooks(String title, List<Book> books) {
        System.out.println("\n--------------------------------------------------");
        System.out.println(" " + title);
        System.out.println("--------------------------------------------------");
        if (books == null || books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            for (Book b : books) {
                System.out.println(b);
            }
        }
        System.out.println("--------------------------------------------------");
    }
}
