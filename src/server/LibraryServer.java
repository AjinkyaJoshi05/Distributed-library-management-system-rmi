package server;

import common.LibraryService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main application class to launch the RMI Library Server.
 * Creates the RMI Registry dynamically on port 1099 and binds "LibraryService".
 */
public class LibraryServer {

    public static final int RMI_PORT = 1099;
    public static final String SERVICE_NAME = "LibraryService";

    public static void main(String[] args) {
        try {
            System.out.println("==================================================");
            System.out.println("     Starting Distributed Library Management Server");
            System.out.println("==================================================");

            // 1. Create the remote service object
            LibraryService libraryService = new LibraryServiceImpl();

            // 2. Start/create the RMI Registry programmatically on port 1099
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
                System.out.println("[INFO] RMI Registry created on port " + RMI_PORT + ".");
            } catch (Exception e) {
                // If registry is already running, locate it
                registry = LocateRegistry.getRegistry(RMI_PORT);
                System.out.println("[INFO] Existing RMI Registry located on port " + RMI_PORT + ".");
            }

            // 3. Bind remote service object to the registry
            registry.rebind(SERVICE_NAME, libraryService);

            System.out.println("[SUCCESS] Service bound as '" + SERVICE_NAME + "' in RMI Registry.");
            System.out.println("==================================================");
            System.out.println(" Server is running. Waiting for client requests...");
            System.out.println(" Press Ctrl+C to stop the server.");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("[FATAL ERROR] Server startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
