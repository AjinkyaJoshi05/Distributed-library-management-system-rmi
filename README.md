# Distributed Library Management System Using Java RMI

A final-year B.Tech Computer Science Distributed Computing Systems (DCS) lab project demonstrating **Java Remote Method Invocation (RMI)**.

---

## 📌 Project Overview

This project implements a **Distributed Library Management System** using pure Java RMI. It enables multiple concurrent clients (Students and Admins) to communicate with a central remote library server to perform operations like viewing, searching, borrowing, returning, adding, and removing books.

The primary objective is to demonstrate core Distributed Computing Systems concepts without relying on modern web frameworks (like Spring Boot, REST, or DB ORMs), preserving architectural clarity and simplicity for academic evaluation and viva defense.

---

## 🏗️ System Architecture

The application follows the classic 3-tier Java RMI Architecture:

```text
+-----------------------+              +-----------------------+
|  Client A (Student)   |              |   Client B (Admin)    |
+-----------------------+              +-----------------------+
            |                                      |
            +------------------+-------------------+
                               |
                               |  Java RMI Protocols (JRMP)
                               v
                  +-------------------------+
                  |      RMI Registry       |  (Port 1099)
                  |  Name: "LibraryService" |
                  +-------------------------+
                               |
                               v
                  +-------------------------+
                  |    Remote Interface     |  LibraryService.java
                  +-------------------------+
                               |
                               v
                  +-------------------------+
                  |      Remote Server      |  LibraryServiceImpl.java
                  +-------------------------+
                               |
                               v
                  +-------------------------+
                  |   In-Memory Storage     |  LibraryData.java
                  | (HashMap / ArrayList)   |  (Thread-Safe Synchronized)
                  +-------------------------+
```

---

## 📁 Project Structure

```text
Java_RMI/
│
├── src/
│   ├── common/                # Shared Interface & Data Models (Passed over RMI)
│   │   ├── Book.java          # Book Model (Implements Serializable)
│   │   ├── Student.java       # Student Model (Implements Serializable)
│   │   └── LibraryService.java# Remote Interface (Extends java.rmi.Remote)
│   │
│   ├── server/                # Server Side Implementation
│   │   ├── LibraryData.java   # In-Memory Storage & Synchronized Operations
│   │   ├── LibraryServiceImpl.java # Remote Service Implementation (Extends UnicastRemoteObject)
│   │   └── LibraryServer.java # Server Entry Point (Creates RMI Registry & Binds Service)
│   │
│   └── client/                # Client Side Application
│       ├── LibraryClient.java # Interactive Console Menu Client
│       └── AutomatedTest.java # Script to automatically verify RMI features
│
├── bin/                       # Compiled Bytecode (.class files)
├── README.md                  # Detailed Project Documentation
└── VIVA_NOTES.md              # Comprehensive Viva Questions & Answers
```

---

## ⚙️ Key Technical Features

1. **Pure Java RMI Communication**: Remote interfaces and stubs handle communication across JVM boundaries.
2. **Dynamic RMI Registry**: The server programmatically starts the RMI registry on port `1099` using `LocateRegistry.createRegistry(1099)`.
3. **Object Serialization**: `Book` and `Student` classes implement `java.io.Serializable` so that object states can be transmitted across the network.
4. **Thread Synchronization & Concurrency**: The shared state in `LibraryData.java` uses `synchronized` methods. If two clients attempt to borrow the same book at the exact same moment, one client succeeds while the second client receives an error message, preventing race conditions.
5. **Robust Error Handling**: Gracefully handles non-existent books, duplicate book IDs, double-borrowing attempts, invalid returns, and network connectivity failures via `RemoteException`.

---

## 🚀 How to Compile and Run

### Prerequisites
- **Java Development Kit (JDK 17 or newer)** installed and added to `PATH`.

### Step 1: Open Terminal in Project Root
Navigate to the root directory `Java_RMI/`.

### Step 2: Compile the Source Code
Run the following command to compile all source files into the `bin/` output directory:
```bash
javac -d bin src/common/*.java src/server/*.java src/client/*.java
```

---

### Step 3: Start the RMI Server
Open a command prompt/terminal and run:
```bash
java -cp bin server.LibraryServer
```
*Expected Output:*
```text
==================================================
     Starting Distributed Library Management Server
==================================================
[INFO] RMI Registry created on port 1099.
[SUCCESS] Service bound as 'LibraryService' in RMI Registry.
==================================================
 Server is running. Waiting for client requests...
 Press Ctrl+C to stop the server.
==================================================
```

---

### Step 4: Launch the Client Application

#### Option A: Launch Java Swing GUI Client (Recommended)
Open one or more separate terminal windows and run:
```bash
java -cp bin client.LibraryClientGUI
```

#### Option B: Launch Console Client
```bash
java -cp bin client.LibraryClient
```


---

### Step 5: (Optional) Run Automated Verification Suite
You can run the automated verification test script to test all remote methods and concurrency without manual console entry:
```bash
java -cp bin client.AutomatedTest
```

---

## 🧪 Sample Test Scenarios

| Test Case | Steps | Expected Result |
| :--- | :--- | :--- |
| **1. View Books** | Client chooses Student Portal -> `1` | Displays initial seeded books (B101 to B105). |
| **2. Search Book** | Client chooses Student Portal -> `2` -> Search `Operating System` | Returns book `B102` (Operating System Concepts). |
| **3. Borrow Book** | Client A borrows `B101` with Student ID `STU01` | Server marks `B101` as borrowed and updates status. |
| **4. Concurrent Borrow** | Client B attempts to borrow `B101` with Student ID `STU02` | Server returns `ERROR: Book B101 is already borrowed by STU01.` |
| **5. Return Book** | Client A returns `B101` with Student ID `STU01` | Status resets to Available. |
| **6. Add Book** | Admin adds `B106` | Book `B106` is instantly visible to all student clients. |
| **7. Remove Book** | Admin removes `B106` | Book `B106` is removed from system. |

---

## 📊 Limitations & Future Enhancements

- **In-Memory Storage**: Current data is stored in RAM and resets when the server stops. Persistent storage (e.g., MySQL or SQLite) can be added.
- **Security**: Basic student ID input is used. Future scope includes authentication and password hashing.
