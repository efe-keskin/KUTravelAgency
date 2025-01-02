package gui;

import constants.Constants;
import core.App;
import custom.CustomPasswordField;
import custom.CustomTextField;
import databases.AdminDB;
import databases.CustomerDB;
import reservationlogs.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * GUI for logging into the KU Travel App. Provides fields for username and password,
 * and supports login for both customers and administrators.
 */
public class LoginGUI extends JFrame implements ActionListener {

    private CustomTextField usernameField;
    private CustomPasswordField passwordField;

    /**
     * Constructs the LoginGUI and initializes its components.
     */
    public LoginGUI() {
        super("KU Travel App Login");
        setSize(Constants.LOGINFRAME_SIZE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Constants.PRIMARY_COLOR);
        addGuiComponent();
    }

    /**
     * Adds GUI components including username and password fields, login button, and register label.
     */
    private void addGuiComponent() {
        usernameField = new CustomTextField("Enter Username", 30);
        usernameField.setBackground(Constants.SECONDARY_COLOR);
        usernameField.setForeground(Color.WHITE);
        usernameField.setBounds(50, 100, Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height);

        passwordField = new CustomPasswordField("Enter Password", 30);
        passwordField.setBackground(Constants.SECONDARY_COLOR);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBounds(50, usernameField.getY() + 100, Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Constants.BUTTON_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(50, passwordField.getY() + 100, Constants.BUTTON_SIZE.width, Constants.BUTTON_SIZE.height);
        loginButton.addActionListener(this);

        JLabel registerLabel = new JLabel("Not registered? Click Here!");
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setBackground(Constants.SECONDARY_COLOR);
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setBounds(
                (Constants.LOGINFRAME_SIZE.width - registerLabel.getPreferredSize().width) / 2,
                loginButton.getY() + 100,
                registerLabel.getPreferredSize().width + 10,
                registerLabel.getPreferredSize().height
        );
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterGUI().setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                registerLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerLabel.setForeground(Constants.SECONDARY_COLOR);
            }
        });

        getContentPane().add(usernameField);
        getContentPane().add(passwordField);
        getContentPane().add(loginButton);
        getContentPane().add(registerLabel);
    }

    /**
     * Handles the login button action. Validates user credentials and navigates to the appropriate UI.
     *
     * @param e the action event triggered by the login button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("Login")) {
            JDialog resultDialog = new JDialog();
            resultDialog.setPreferredSize(Constants.RESULT_DIALOG_SIZE);
            resultDialog.pack();
            resultDialog.setLocationRelativeTo(this);
            resultDialog.setModal(true);

            JLabel resultLabel = new JLabel();
            resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
            resultDialog.add(resultLabel);
            resultDialog.getContentPane().setBackground(Constants.BUTTON_COLOR);
            resultLabel.setForeground(Constants.SECONDARY_COLOR);

            String username = usernameField.getText();
            String password = passwordField.getText();

            String customerPass = CustomerDB.getCustomerPass(username);
            String adminPass = AdminDB.getAdminPass(username);

            if (customerPass != null) {
                if (password.equals(customerPass)) {
                    resultLabel.setText("Login Successful!");
                    App.loggedIn = true;
                    App.user = CustomerDB.getCustomer(username);
                    dispose();
                    Logger.logUserLogin(App.user.getUsername());
                    new CustomerUI().setVisible(true);
                } else {
                    resultLabel.setText("Invalid Password!");
                }
            } else if (adminPass != null) {
                if (password.equals(adminPass)) {
                    App.loggedIn = true;
                    App.isAdmin = true;
                    App.user = AdminDB.getAdmin(username);
                    resultLabel.setText("Admin Login Successful!");
                    dispose();
                    new AdminGUI().setVisible(true);
                } else {
                    resultLabel.setText("Invalid Password!");
                }
            } else {
                resultLabel.setText("Invalid Username!");
            }

            resultDialog.setVisible(true);
        }
    }
}
