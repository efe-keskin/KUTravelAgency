package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

/**
 * GUI for displaying admin logs from a file in a scrollable text area.
 */
public class AdminLogGUI extends JFrame {

    /**
     * Constructs the AdminLogGUI and loads logs into a text area.
     */
    public AdminLogGUI() {
        super("Admin Logs");

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);

        try {
            Path logFilePath = Paths.get("logs/application_logs.txt");
            for (String line : Files.readAllLines(logFilePath)) {
                textArea.append(line + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading logs from file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    /**
     * Main method for testing the AdminLogGUI.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLogGUI gui = new AdminLogGUI();
            gui.setVisible(true);
        });
    }
}
