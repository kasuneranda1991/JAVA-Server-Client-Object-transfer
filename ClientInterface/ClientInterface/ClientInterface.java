/**
 * This package contains the client interface for interacting with a server to perform compute tasks.
 */
package ClientInterface;

import Contract.CFile;
import Contract.Factorization;
import Contract.Fibonacci;
import Contract.PerfectNumber;
import Contract.Task;
import java.net.*;
import java.io.*;

/**
 * The ClientInterface class represents a client application for interacting
 * with a server to perform compute tasks.
 * 
 * @author Kasun Eranda - 12216898
 */
public class ClientInterface {

    // Constants for file paths, classes, and default port
    private static final String BASE_CLASS_PATH = "Contract/";
    private static final String[] CLASSES = { "Fibonacci.class", "PerfectNumber.class", "Factorization.class" };
    private static final int DEFAULT_PORT = 8888;

    // Socket and file-related variables
    private static Socket socket = null;
    private static String taskfile;

    // Input and output streams
    private static ObjectInputStream in = null;
    private static ObjectOutputStream out = null;

    /**
     * The main method that initializes the UI and sets up listeners for various
     * actions.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        // Initialize the UI
        UI ui = new UI();
        ui.initUI();

        // Set up action listeners for UI buttons
        UI.setBtn.addActionListener(e -> establishConnection());
        UI.upldBtn.addActionListener(e -> uploadFile(UI.cb.getSelectedIndex()));
        UI.clrBrdBtn.addActionListener(e -> UI.ta.setText(null));
        UI.offLdTskBtn.addActionListener(e -> {
            initializeTask(UI.cb.getSelectedIndex());
        });
    }

    /**
     * Establishes a TCP connection to the server.
     */
    private static void establishConnection() {
        // Establish a TCP connection to the server
        connect();
        if (socket.isConnected()) {
            UI.message("TCP connection to the server is done!");
        }
    }

    /**
     * Reconnects to the server.
     */
    private static void reconnect() {
        // Reconnect to the server
        connect();
    }

    /**
     * Establishes a connection with the server using the specified host and port.
     */
    private static void connect() {
        try {
            // Get host and port details from UI
            String host = UI.t1.getText();
            int port = UI.t2.getText().isEmpty() ? DEFAULT_PORT : Integer.parseInt(UI.t2.getText());
            socket = new Socket(host, port);

            // If connected, enable UI buttons and set up streams
            if (socket.isConnected()) {
                UI.enableButton(true);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            }
        } catch (IOException e) {
            handleException("Error connection unsuccessful:", e);
        }
    }

    /**
     * Uploads a class file to the server based on the selected task.
     * 
     * @param index The index representing the selected task.
     */
    private static void uploadFile(int index) {
        // Reconnect to the server before uploading
        reconnect();
        try {
            if (out != null && socket != null && !socket.isClosed()) {
                // Determine the class file based on the selected task
                switch (index) {
                    case 1 ->
                        taskfile = BASE_CLASS_PATH + CLASSES[1];
                    case 2 ->
                        taskfile = BASE_CLASS_PATH + CLASSES[2];
                    default ->
                        taskfile = BASE_CLASS_PATH + CLASSES[0];
                }

                // Prepare the file for uploading
                File file = new File(taskfile);

                // Read the class file and send it to the server
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                        DataInputStream dis = new DataInputStream(bis)) {

                    byte[] byteArray = new byte[(int) file.length()];
                    dis.readFully(byteArray, 0, byteArray.length);
                    CFile cfile = new CFile(taskfile, byteArray);
                    out.writeObject(cfile);
                    out.flush();
                    UI.message("Uploading " + (cfile.getFname().split(BASE_CLASS_PATH)[1]) + " is done.");
                }
            } else {
                UI.message("Error: Not connected to the server. Please set the server details first.");
            }
        } catch (IOException e) {
            handleException("File upload error:", e);
        }
    }

    /**
     * Initializes and sends a compute task to the server based on the selected
     * task.
     * 
     * @param index The index representing the selected task.
     * @throws ClassNotFoundException If the class of the serialized object could
     *                                not be found.
     */
    private static void initializeTask(int index) {
        // Reconnect to the server before initializing a task
        reconnect();
        Object taskObject = createTaskObject(index);

        try {
            // Send the task to the server and receive the result
            out.writeObject(taskObject);
            out.flush();

            Object result = in.readObject();

            // Handle server response
            if (result instanceof Exception) {
                handleServerException((Exception) result);
            } else {
                Task processedTask = (Task) result;
                UI.message("\n" + processedTask.getResult());
                UI.message("-------------------------");
            }

        } catch (IOException | ClassNotFoundException e) {
            handleException("Error during task initialization:", e);
        } finally {
            // Close the socket connection
            closeSocket();
        }
    }

    /**
     * Creates a task object based on the selected task index.
     * 
     * @param index The index representing the selected task.
     * @return The task object.
     */
    private static Object createTaskObject(int index) {
        // Create a task object based on the selected task
        switch (index) {
            case 1 -> {
                return new PerfectNumber(UI.getUserInput("Perfect numbers up to"));
            }
            case 2 -> {
                return new Factorization(UI.getUserInput("The number to factorize"));
            }
            default -> {
                return new Fibonacci(UI.getUserInput("Number of sequence items"));
            }
        }
    }

    /**
     * Closes the socket and associated streams.
     */
    private static void closeSocket() {
        try {
            // Close the socket and associated streams
            if (socket != null) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            handleException("Error closing socket:", e);
        }
    }

    /**
     * Handles exceptions by logging the message and the exception details.
     * 
     * @param message The error message.
     * @param e       The exception.
     */
    private static void handleException(String message, Exception e) {
        // Handle and log exceptions
        UI.message(message + " " + e.getMessage());
    }

    /**
     * Handles server-specific exceptions.
     * 
     * @param exception The server exception.
     */
    private static void handleServerException(Exception exception) {
        // Handle server-specific exceptions
        if (exception instanceof ClassNotFoundException) {
            UI.message(
                    "Please upload the class file for this task " + (exception.getMessage()).substring(9) + ".class!");

        }
    }
}
