package ComputeServer;

import Contract.CFile;
import Contract.CSAuthenticator;
import Contract.Task;
import Security.SecurityUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import javax.crypto.SecretKey;

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
    private static final String BASE_CLASS_PATH = "src/Contract/";

    // Object input and output streams for communication with the client
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    // Client socket
    private final Socket clientSocket;

    private static SecretKey sessionKey;
    
    // Authenticator
    private static CSAuthenticator authenticator = CSAuthenticator.getInstance();
    
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

            if (obj instanceof CSAuthenticator) { 
                handleAuthenticator((CSAuthenticator) obj);
            } else {
                // Descrypt encrypted string object by session key
                Object decryptedObject = SecurityUtil.SymDecryptObj((String)obj, sessionKey);
                
                // Check the type of object received and handle accordingly
                if (decryptedObject instanceof CFile cfile) {
                    // Log
                    System.out.println("The CFile encrypted String: " + (String)obj + "\n");
                    handleClassFileUpload(cfile);
                } else if (decryptedObject instanceof Task) {
                    // Log
                    System.out.println("The Task object encrypted String: " + (String)obj + "\n");
                    
                    handleClientTask((Task) decryptedObject);
                }
            }
        } catch (EOFException e) {
            handleException(e);
        } catch (IOException | ClassNotFoundException e) {
            handleException(e);
        } finally {
            closeConnection();
        }
    }
    
    private void handleAuthenticator(CSAuthenticator auth) throws IOException {
        String authUsername = auth.getPlainStringUserName();
        System.out.println("The mutual authentication of user \"" + authUsername + "\" is progressing!\n");
        
        HashMap privateKeys = SecurityUtil.ReadinKeys("src/Security/CentrePri.ser");
        HashMap publicKeys = SecurityUtil.ReadinKeys("src/Security/CentrePub.ser");      
        
        // Decrypt username with client public key (Step 2)
        String decryptedClientUsername = SecurityUtil.asyDecrypt(auth.getCipherUserName(), (PublicKey)publicKeys.get(authUsername));
        
        System.out.println(decryptedClientUsername);
 
        if (authUsername.equals(decryptedClientUsername)) {
            // Decrypt the verification string with Centre primary key (Step 3)
            String decryptedVerificationString = SecurityUtil.asyDecrypt(auth.getVerificationString(), (PrivateKey)privateKeys.get("CENTRE"));

            // Server Creates Session Key (Step 4)
            sessionKey = SecurityUtil.SecretKeyGen();

            // Encrypt server username with CENTRE primary key (E(“CENTRE”, Private(CENTRE)))
            String encryptedCipheredUserName = SecurityUtil.asyEncrypt("CENTRE", (PrivateKey)privateKeys.get("CENTRE"));
            
            // Encrypt verification String with Session Key (E(VerificationString, SessionKey))
            String encryptedVerificationString = SecurityUtil.SymEncryptObj(auth.getVerificationString(), sessionKey);

            // Encrypt Sessionkey with Client public key (E(SessionKey, Public(Stephen Smith))
            String encryptedSessionKey = SecurityUtil.EncryptSessionKey(sessionKey, (PublicKey)publicKeys.get(authUsername));
            
            // Sever creates an authenticator (Step 5)
            authenticator.authenticate(
                    "CENTRE", 
                    encryptedCipheredUserName, 
                    encryptedVerificationString, 
                    encryptedSessionKey
            );
            
            // Log data
            System.out.println("The verification string in cipher text: " + auth.getVerificationString() + "\n");
            System.out.println("The verification string in plain text: " + decryptedVerificationString + "\n");
            System.out.println("The session key in plain text: " + SecurityUtil.keytoB64String(sessionKey) + "\n");
            System.out.println("The session key in cipher text: " + encryptedSessionKey + "\n");
            
            // Send auth object back to the client
            out.writeObject(authenticator);
            out.flush();
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
        
        // Encrypt task object before send to the client
        String encriptedTaskString = SecurityUtil.SymEncryptObj(task, sessionKey);
        
        out.writeObject(encriptedTaskString);
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
