package Contract;

import java.io.Serializable;

/**
 * This class represents a container to hold the class file of a compute task that is to be transferred to the server.
 */
public class CFile implements Serializable {

    // The file name
    String fname;
    // The byte array holding the class file
    byte[] fbyte;

    /**
     * Constructs a CFile object with the specified file name and byte array.
     *
     * @param fname The name of the file.
     * @param fbyte The byte array holding the class file.
     */
    public CFile(String fname, byte[] fbyte) {
        this.fname = fname;
        this.fbyte = fbyte;
    }

    /**
     * Gets the file name.
     *
     * @return The name of the file.
     */
    public String getFname() {
        return fname;
    }

    /**
     * Sets the file name.
     *
     * @param fname The name of the file.
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * Gets the byte array holding the class file.
     *
     * @return The byte array of the class file.
     */
    public byte[] getFbyte() {
        return fbyte;
    }

    /**
     * Sets the byte array holding the class file.
     *
     * @param fbyte The byte array of the class file.
     */
    public void setFbyte(byte[] fbyte) {
        this.fbyte = fbyte;
    }
}
