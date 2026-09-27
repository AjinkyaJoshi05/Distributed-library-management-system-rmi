# Comprehensive Viva Preparation & Question Guide

This document contains clear, simple, and direct answers to potential questions asked during the B.Tech Computer Science Distributed Computing Systems (DCS) viva examination for the **Distributed Library Management System Using Java RMI**.

---

## Part A: Fundamental Distributed Systems & RMI Concepts

### 1. What is Distributed Computing?
**Answer:**
Distributed Computing is a model in which multiple independent computers or software components connected over a network communicate and coordinate their actions by passing messages to achieve a common goal, appearing to the user as a single coherent system.

### 2. What is Java RMI (Remote Method Invocation)?
**Answer:**
Java RMI is a Java API that allows an object running in one Java Virtual Machine (JVM) to invoke methods on an object running in another JVM (even on a different host machine across a network) as if it were a local method invocation.

### 3. Why use Java RMI instead of Socket Programming?
**Answer:**
- **Level of Abstraction:** Socket programming works at a low level where developers manually handle raw TCP/UDP streams, byte parsing, and application-level protocols.
- **Object-Oriented:** RMI provides high-level object-oriented remote method calls where parameters and return values (objects) are passed directly using Java serialization, hiding underlying network socket details.

### 4. What is the role of the RMI Registry?
**Answer:**
The RMI Registry is a bootstrap lookup service that maps human-readable string names to remote object references (stubs). 
1. The **Server** registers (binds) its remote object stub with a name (e.g., `"LibraryService"`).
2. The **Client** contacts the registry and looks up (`lookup()`) that name to obtain a reference (stub) to the remote object.

### 5. What is a Remote Interface in Java RMI?
**Answer:**
A Remote Interface defines the set of methods that can be called remotely by a client. In Java RMI:
1. It must extend `java.rmi.Remote`.
2. Every method declared in the interface must throw `java.rmi.RemoteException`.

### 6. What is `RemoteException` and why must it be handled?
**Answer:**
`RemoteException` is a checked exception that is thrown whenever a network communication failure, timeout, or RMI runtime error occurs during a remote invocation. It forces the client developer to gracefully handle distributed failures.

### 7. What is `UnicastRemoteObject`?
**Answer:**
`UnicastRemoteObject` is a superclass provided by Java RMI (`java.rmi.server.UnicastRemoteObject`). When a server class extends it:
1. It exports the object to listen for incoming client RMI calls on an anonymous or specified TCP port.
2. It handles low-level socket communication automatically.

### 8. What is a Stub (Proxy)?
**Answer:**
A stub is a client-side proxy object created by RMI that implements the remote interface. When the client calls a method on the stub:
1. The stub serializes (marshals) the arguments.
2. Sends the request across the network to the server.
3. Receives the serialized result (unmarshals) and returns it to the client caller.

### 9. What is Serialization in Java?
**Answer:**
Serialization is the process of converting a Java object's state into a sequence of bytes so it can be transmitted across a network or saved to a file. Classes passed as arguments or return values in RMI (e.g., `Book`, `Student`) must implement `java.io.Serializable`.

---

## Part B: Project Architecture & Implementation Questions

### 10. Can you explain the architecture of this project?
**Answer:**
The project uses a standard 3-tier RMI client-server architecture:
- **`common/` package:** Contains `LibraryService` (Remote Interface), `Book`, and `Student` (Serializable Data Models).
- **`server/` package:** `LibraryServer` starts the RMI Registry on port `1099` and binds `LibraryServiceImpl` (which extends `UnicastRemoteObject`). `LibraryData` stores the library books in memory.
- **`client/` package:** `LibraryClient` looks up `"LibraryService"` from the RMI registry and invokes remote methods based on user input from a console menu.

### 11. How does a Client connect to the Server in this project?
**Answer:**
```java
// 1. Locate registry on server machine (localhost:1099)
Registry registry = LocateRegistry.getRegistry("localhost", 1099);

// 2. Lookup the bound remote object
LibraryService libraryService = (LibraryService) registry.lookup("LibraryService");

// 3. Call remote methods directly
List<Book> books = libraryService.getAllBooks();
```

### 12. Where and how is the data stored in this system?
**Answer:**
The library data is stored in-memory inside `LibraryData.java` on the server using Java collection data structures:
- `HashMap<String, Book>` for fast O(1) lookup of books by ID.
- `HashMap<String, Student>` for student records.

### 13. How do multiple clients interact with the same data?
**Answer:**
Since Java RMI handles incoming requests concurrently in separate worker threads on the server, all remote requests interact with the **single shared instance** of `LibraryData` instantiated by `LibraryServiceImpl`.

### 14. How is Concurrency handled in this project? What prevents Race Conditions?
**Answer:**
Concurrency is handled using Java's `synchronized` keyword on all data modification methods in `LibraryData.java`.
- **Explanation:** When Client A and Client B try to borrow the same book `B101` simultaneously, Java locks the `LibraryData` object. Only one thread executes `borrowBook()` at a time. The first thread checks availability, marks `B101` as borrowed, and unlocks. The second thread then executes, sees `B101` is no longer available, and receives an error message.

### 15. What happens if the server goes down while a client is running?
**Answer:**
If the server crashes or network breaks, any subsequent method call made by `LibraryClient` will catch a `java.rmi.RemoteException` (or `ConnectException`), print a clear error message, and prevent the client app from crashing abruptly.

---

## Part C: Core Java Concepts Relevant to the Project

### 16. Interface vs Abstract Class vs Concrete Class
- **Interface (`LibraryService`):** Defines contract/methods without method bodies (until Java 8 default methods). Extends `Remote`.
- **Concrete Class (`LibraryServiceImpl`):** Provides the actual business logic implementation of all interface methods and extends `UnicastRemoteObject`.

### 17. `extends` vs `implements`
- `extends`: Used when a class inherits from a superclass (e.g., `LibraryServiceImpl extends UnicastRemoteObject`).
- `implements`: Used when a class realizes an interface contract (e.g., `implements LibraryService`).

### 18. Why use `synchronized` keyword?
**Answer:**
The `synchronized` keyword ensures that only one thread can execute a critical section of code at a time on a given object instance, providing mutual exclusion (mutex) and preventing data inconsistency due to race conditions in multi-threaded environments.

### 19. Why use `ArrayList` and `HashMap`?
- **`HashMap`:** Provides key-value pairing (e.g., `BookID -> Book Object`) allowing fast lookup in O(1) time.
- **`ArrayList`:** Used to dynamically return ordered collections of objects (e.g., list of borrowed books) back to the client.

---

## Part D: Java Swing GUI & Presentation Layer Questions

### 20. Why was Swing used?
**Answer:**
Swing provides a simple graphical interface while keeping the focus of the project on Java RMI. The GUI acts strictly as the client-side presentation layer and communicates with the server exclusively through the RMI remote interface.

### 21. Does the GUI communicate directly with `LibraryData`?
**Answer:**
No. The GUI communicates exclusively with the remote `LibraryService` interface via the RMI stub reference. `LibraryData` remains strictly on the server side behind the remote interface layer. The GUI contains zero business logic.

