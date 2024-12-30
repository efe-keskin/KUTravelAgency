package gui;

import constants.Constants;
import core.App;
import custom.PasswordFieldCustom;
import custom.TextFieldCustom;
import databases.AdminDB;
import databases.CustomerDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginGUI extends JFrame implements ActionListener {
    private TextFieldCustom usernameField;
    private PasswordFieldCustom passwordField;

    public LoginGUI(){
        super("KU Travel App Login");
        setSize(Constants.LOGINFRAME_SIZE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Constants.PRIMARY_COLOR);
        addGuiComponent();
    }
    private void addGuiComponent(){
        // username field
        usernameField = new TextFieldCustom("Enter Username",30);

        usernameField.setBackground(Constants.SECONDARY_COLOR);
        usernameField.setForeground(Color.WHITE);
        usernameField.setBounds(
                50,
                315,
                Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height
        );
        // password field
        passwordField = new PasswordFieldCustom("Enter Password",30);
        passwordField.setBackground(Constants.SECONDARY_COLOR);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBounds(
                50,
                usernameField.getY()+100,
                Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height
        );

        // login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Constants.BUTTON_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(
                50,
                passwordField.getY()+ 100,
                Constants.BUTTON_SIZE.width, Constants.BUTTON_SIZE.height

        );
        loginButton.addActionListener(this);

        // login -> register label
        JLabel registerLabel = new JLabel("Not registered? Click Here!");
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setBackground(Constants.SECONDARY_COLOR);
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setBounds( (Constants.LOGINFRAME_SIZE.width-registerLabel.getPreferredSize().width)/2,
                loginButton.getY()+100, registerLabel.getPreferredSize().width+10,registerLabel.getPreferredSize().height );
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
        // add to frame

        getContentPane().add(usernameField);
        getContentPane().add(passwordField);
        getContentPane().add(loginButton);
        getContentPane().add(registerLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("Login")) {
            // create dialog box
            JDialog resultDialog = new JDialog();
            resultDialog.setPreferredSize(Constants.RESULT_DIALOG_SIZE);
            resultDialog.pack();
            resultDialog.setLocationRelativeTo(this);
            resultDialog.setModal(true);
            // create label (display result)
            JLabel resultLabel = new JLabel();
            resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
            resultDialog.add(resultLabel);
            resultDialog.getContentPane().setBackground(Constants.BUTTON_COLOR);
            resultLabel.setForeground(Constants.SECONDARY_COLOR);

            // retrieve entered credentials
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Check if username exists in CustomerDB
            String customerPass = CustomerDB.getCustomerPass(username);
            // Check if username exists in AdminDB
            String adminPass = AdminDB.getAdminPass(username);

            if (customerPass != null) {
                // username exists in CustomerDB
                if (password.equals(customerPass)) {
                    // Customer password is correct
                    resultLabel.setText("Login Successful!");
                    App.loggedIn = true;
                    App.user = CustomerDB.getCustomer(username);
                    dispose();
                    new CustomerUI().setVisible(true);
                } else {
                    // Customer password is incorrect
                    resultLabel.setText("Invalid Password!");
                }
            } else if (adminPass != null) {
                // username exists in AdminDB
                if (password.equals(adminPass)) {
                    // Admin password is correct
                    App.loggedIn = true;
                    App.isAdmin = true;
                    App.user = AdminDB.getAdmin(username);
                    resultLabel.setText("Admin Login Successful!");
                    dispose();
                    new AdminGUI().setVisible(true);
                } else {
                    // Admin password is incorrect
                    resultLabel.setText("Invalid Password!");
                }
            } else {
                // username does not exist in either DB
                resultLabel.setText("Invalid Username!");
            }

            resultDialog.setVisible(true);
        }
    }

}
