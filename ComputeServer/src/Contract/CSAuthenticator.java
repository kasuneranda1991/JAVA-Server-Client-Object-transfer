/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Contract;

import java.io.Serializable;

/**
 * CSAuthenticator class represents a singleton instance for user authentication
 * in a client-server application. It provides methods for setting and
 * retrieving user information, generating a verification string, and handling
 * authentication. This class implements the Serializable interface for object
 * serialization.
 *
 * @author kasun
 */
public class CSAuthenticator implements Serializable {

    // Default key length for the verification string
    private static final int DEFAULT_KEY_LENGTH = 128;

    // Plain text username
    private String PlainStringUserName;

    // Cipher text username
    private String CipherUserName;

    // Verification string used for authentication
    private static String VerificationString;

    // Session key for secure communication
    private String SessionKey;

    // Singleton instance of CSAuthenticator
    private static CSAuthenticator authtenticator;

    // Private constructor to enforce singleton pattern
    private CSAuthenticator() {
    }

    /**
     * Returns the singleton instance of CSAuthenticator. If the instance does
     * not exist, it creates a new one and initializes the verification string.
     *
     * @return The singleton instance of CSAuthenticator
     */
    public static CSAuthenticator getInstance() {
        if (authtenticator == null) {
            authtenticator = new CSAuthenticator();
            VerificationString = getBase64String(DEFAULT_KEY_LENGTH);
            return authtenticator;
        }
        return authtenticator;
    }

    /**
     * Gets the plain text username.
     *
     * @return The plain text username
     */
    public String getPlainStringUserName() {
        return PlainStringUserName;
    }

    /**
     * Gets the cipher text username.
     *
     * @return The cipher text username
     */
    public String getCipherUserName() {
        return CipherUserName;
    }

    /**
     * Sets the cipher text username.
     *
     * @param CipherUserName The cipher text username to set
     */
    public void setCipherUserName(String CipherUserName) {
        this.CipherUserName = CipherUserName;
    }

    /**
     * Gets the verification string.
     *
     * @return The verification string
     */
    public String getVerificationString() {
        return VerificationString;
    }

    /**
     * Sets the verification string.
     *
     * @param VerificationString The verification string to set
     */
    public void setVerificationString(String VerificationString) {
        this.VerificationString = VerificationString;
    }

    /**
     * Gets the session key.
     *
     * @return The session key
     */
    public String getSessionKey() {
        return SessionKey;
    }

    /**
     * Sets the session key.
     *
     * @param SessionKey The session key to set
     */
    public void setSessionKey(String SessionKey) {
        this.SessionKey = SessionKey;
    }

    /**
     * Generates a random base64 string of the specified length.
     *
     * @param string_length The length of the base64 string to generate
     * @return The generated base64 string
     */
    private static String getBase64String(int string_length) {
        // The character pool to generate a random alpha-numeric string 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz"
                + "+/";
        StringBuilder sb = new StringBuilder(string_length);
        for (int i = 0; i < string_length; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Authenticates the user with the provided username. Sets the plain text
     * username and retrieves the verification string.
     *
     * @param username The username to authenticate
     * @return True if authentication is successful, false otherwise
     */
    public boolean authenticate(String username) {
        this.PlainStringUserName = username;
        this.authtenticator.getVerificationString();
        return true;
    }

    /**
     * Generates a log message containing information about the verification
     * string and session key.
     *
     * @return The log message
     */
    public String log() {
        StringBuilder sb = new StringBuilder();
        sb.append("The verification string in plain text: ")
                .append(getVerificationString()).append("\n")
                .append("The verification string cipher text: ")
                .append("VERIFICATION STRING CIPHER TEXT GOES HERE\n")
                .append("The session key in cipher text: ")
                .append("SESSION KEY CIPHER TEXT GOES HERE\n")
                .append("The session key in plain text: ")
                .append("PLAIN SESSION KEY GOES HERE\n")
                .append("The mutual authentication is done");
        return sb.toString();
    }

}
