/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClientInterface;

import javax.swing.*;

/**
 * UI class represents the graphical user interface for the client application.
 * It includes buttons, labels, text fields, and other components necessary for
 * user interaction.
 *
 * @author Kasun Eranda - 12216898
 */
public class UI {

    private static final int BTN_WIDTH = 150;
    private static final int BTN_HEIGHT = 40;

    private static final int LBL_WIDTH = 100;
    private static final int LBL_HEIGHT = 40;

    private static final int TXT_WIDTH = 100;
    private static final int TXT_HEIGHT = 40;

    // Components for UI
    public static JTextArea ta;
    public static JFrame f = new JFrame(); // Creating an instance of JFrame
    public static JButton upldBtn, setBtn, clrBrdBtn, offLdTskBtn, authBtn;
    public static JTextField host, port, username;
    public static JComboBox cb;

    /**
     * Initializes the user interface by setting up the frame, buttons, labels,
     * text fields, and other components.
     */
    public void initUI() {
        f.setSize(1000, 700); // Set the frame size (400 width and 500 height)

        // Set up Buttons
        setBtn = createButton("SET", 500, 60);
        authBtn = createButton("authenticate", setBtn.getX() + 150, 60);
        clrBrdBtn = createButton("Clear Board", authBtn.getX() + 150, 60);
        upldBtn = createButton("Upload", authBtn.getX(), 120);
        offLdTskBtn = createButton("OffLoadTask", clrBrdBtn.getX(), 120);

        // Set up Combo box
        String taskList[] = {"Generate Fibonacci sequence", "Search Perfect Number", "Factorise number"};
        cb = createComboBox(taskList, 120, 120);

        enableButton(false);

        // Set up Labels
        createLabel("Server host", 10, 10);
        createLabel("Username", 500, 10);
        createLabel("Server port", 10, 60);
        createLabel("Task List", 10, 120);
        createLabel("Results Board", setBtn.getX() - 50, 200);

        // Set up Text fields
        host = createTextField((10 + LBL_WIDTH + 10), 10);
        port = createTextField(host.getX(), 60);
        username = createTextField(host.getX() + 500, 10);

        // Set up Textarea
        ta = createTextArea(10, 300);

        f.setLayout(null); // Using no layout managers
        f.setVisible(true); // Making the frame visible
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Window close
    }

    /**
     * Creates a JButton with the specified label and position.
     *
     * @param label The label for the button.
     * @param x The x-coordinate for the button.
     * @param y The y-coordinate for the button.
     * @return The created JButton.
     */
    private static JButton createButton(String label, int x, int y) {
        JButton button = new JButton(label);
        button.setBounds(x, y, UI.BTN_WIDTH, UI.BTN_HEIGHT);
        button.setFocusable(false);
        attachToUI(button);
        return button;
    }

    /**
     * Creates a JLabel with the specified text and position.
     *
     * @param text The text for the label.
     * @param x The x-coordinate for the label.
     * @param y The y-coordinate for the label.
     */
    private static void createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, LBL_WIDTH, LBL_HEIGHT);
        attachToUI(label);
    }

    /**
     * Creates a JTextField and adds it to the specified JFrame at the specified
     * position.
     *
     * @param x The x-coordinate for the text field.
     * @param y The y-coordinate for the text field.
     * @return The created JTextField.
     */
    private static JTextField createTextField(int x, int y) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, TXT_WIDTH, TXT_HEIGHT);
        attachToUI(textField);
        return textField;
    }

    /**
     * Creates a JComboBox with the specified items and adds it to the specified
     * JFrame at the specified position.
     *
     * @param items The array of items for the combo box.
     * @param x The x-coordinate for the combo box.
     * @param y The y-coordinate for the combo box.
     * @return The created JComboBox.
     */
    private static JComboBox createComboBox(String[] items, int x, int y) {
        JComboBox comboBox = new JComboBox(items);
        comboBox.setBounds(x, y, 500, 40);
        attachToUI(comboBox);
        return comboBox;
    }

    /**
     * Creates a JTextArea and adds it to the specified JFrame at the specified
     * position.
     *
     * @param x The x-coordinate for the text area.
     * @param y The y-coordinate for the text area.
     * @return The created JTextArea.
     */
    private static JTextArea createTextArea(int x, int y) {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(x, y, 950, 480);
        attachToUI(textArea);
        return textArea;
    }

    /**
     * Enables or disables buttons based on the specified parameter.
     *
     * @param enabled True to enable buttons, false to disable.
     */
    public static void enableButton(boolean enabled) {

        if (ClientInterface.isIsAuthenticated()) {
            clrBrdBtn.setEnabled(enabled);
            upldBtn.setEnabled(enabled);
            offLdTskBtn.setEnabled(enabled);
            if (enabled) {
                cb.setSelectedIndex(0);
            } else {
                cb.setSelectedIndex(-1);
            }
            cb.setEnabled(enabled);
            authBtn.setEnabled(!enabled);
        } else {
            clrBrdBtn.setEnabled(false);
            upldBtn.setEnabled(false);
            offLdTskBtn.setEnabled(false);
            cb.setEnabled(false);
            authBtn.setEnabled(true);
        }
        setBtn.setEnabled(!enabled);

    }

    /**
     * Appends a message to the JTextArea and prints it to the console.
     *
     * @param message The message to be appended.
     */
    public static void message(String message) {
        ta.append(message + "\n");
        System.out.println(message);
    }

    /**
     * Gets user input as an integer using JOptionPane.
     *
     * @param inputMessage The message to be displayed in the input dialog.
     * @return The user input as an integer.
     */
    public static int getUserInput(String inputMessage) {
        String input;
        do {
            input = JOptionPane.showInputDialog(f, inputMessage, "Please enter number", 3);
        } while (!isValidInteger(input));

        return Integer.parseInt(input);
    }

    /**
     * Checks if the input string is a valid integer.
     *
     * @param input The input string to be validated.
     * @return True if the input is a valid integer, false otherwise.
     */
    private static boolean isValidInteger(String input) {
        return input != null && !input.isEmpty() && input.matches("-?\\d+");
    }

    private static void attachToUI(JComponent component) {
        f.add(component);
    }
}
