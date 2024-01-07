package ComputeServer;

import Contract.CFile;
import Contract.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The ComputeServer class represents a server that listens for incoming connections
 * on a specified port and handles communication with clients by using threads.
 */
public class ComputeServer {

    // Constant for the server port
    public static final int SERVER_PORT = 8888;

    /**
     * The main method initializes a ServerSocket, listens for incoming connections,
     * and starts a new ConnectionHandler thread for each client.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String args[]) {
        try {
            // Create a server socket to listen for incoming connections
            ServerSocket listenSocket = new ServerSocket(SERVER_PORT);
            System.out.println("-----------------------------------");
            System.out.println("The Server is listening on port " + SERVER_PORT + " for object transfer...");
            System.out.println("-----------------------------------");

            // Accept incoming connections and start a new thread for each client
            while (true) {
                Socket clientSocket = listenSocket.accept();
                new ConnectionHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

/**
 * The ConnectionHandler class represents a thread that handles communication
 * with a specific client, including uploading class files and executing tasks.
 */
class ConnectionHandler extends Thread {

    // Base path for storing class files
    private static final String BASE_CLASS_PATH = "Contract/";

    // Object input and output streams for communication with the client
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    // Client socket
    private final Socket clientSocket;

    /**
     * Constructor to initialize input and output streams for a client connection.
     *
     * @param clientSocket The client socket.
     * @throws IOException If an I/O error occurs.
     */
    public ConnectionHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    /**
     * The run method handles communication with the client, including receiving
     * and processing objects, such as class files and tasks.
     */
    @Override
    public void run() {
        try {
            Object obj = in.readObject();

            // Check the type of object received and handle accordingly
            if (obj instanceof CFile cfile) {
                handleClassFileUpload(cfile);
            } else if (obj instanceof Task) {
                handleClientTask((Task) obj);
            }
        } catch (EOFException e) {
            handleException(e);
        } catch (IOException | ClassNotFoundException e) {
            handleException(e);
        } finally {
            closeConnection();
        }
    }

    /**
     * handle the upload of class files from the client.
     *
     * @param cfile The CFile object representing the class file.
     * @throws IOException If an I/O error occurs.
     */
    private void handleClassFileUpload(CFile cfile) throws IOException {
        try (FileOutputStream fo = new FileOutputStream(cfile.getFname());
             BufferedOutputStream bos = new BufferedOutputStream(fo)) {
            bos.write(cfile.getFbyte(), 0, cfile.getFbyte().length);
            consoleMessage("The class file of " + (cfile.getFname().split(BASE_CLASS_PATH)[1]) + " has been uploaded.");
        }
    }

    /**
     * handle the execution of client tasks.
     *
     * @param task The Task object representing the client task.
     * @throws IOException If an I/O error occurs.
     */
    private void handleClientTask(Task task) throws IOException {
        consoleMessage("Performing a client task of " + task.getClass());
        task.executeTask();
        out.writeObject(task);
        out.flush();
        consoleMessage("---------------------------------");
    }

    /**
     * handle exceptions during communication.
     *
     * @param e The exception that occurred.
     */
    private void handleException(Exception e) {
        try {
            consoleMessage("Error : " + e.getMessage());
            out.writeObject(e);
            out.flush();
        } catch (IOException ex) {
            // Ignore the exception during closing;
        }
    }

    /**
     * close the client connection.
     */
    private void closeConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * print a message to the console.
     *
     * @param message The message to be printed.
     */
    private void consoleMessage(String message) {
        System.out.println(message);
    }
}
