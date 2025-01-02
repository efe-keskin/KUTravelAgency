package gui;

import constants.Constants;
import custom.LoginErrors;
import custom.CustomPasswordField;
import custom.CustomTextField;
import databases.AdminDB;
import databases.CustomerDB;
import reservationlogs.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterGUI extends JFrame implements ActionListener, FocusListener {
    private LoginErrors usernameLoginErrors, usernameLoginErrors1, passwordLoginErrors, confirmPasswordLoginErrors;
    private CustomTextField usernameField;
    private CustomPasswordField passwordField,confirmPasswordField;
    public RegisterGUI(){
        super("KU Travel Agency Register");
        setSize(Constants.LOGINFRAME_SIZE);
        setLocationRelativeTo(null); // centers the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Constants.PRIMARY_COLOR);
        addGuiComponents();
    }
    private void addGuiComponents(){
        JLabel registerLabel = new JLabel("Register");
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setForeground(Constants.SECONDARY_COLOR);
        registerLabel.setBounds(0,0, Constants.REGISTER_LABEL_SIZE.width, Constants.REGISTER_LABEL_SIZE.height);
        //username field
        usernameField = new CustomTextField("Enter username",30);
        usernameField.setBackground(Constants.SECONDARY_COLOR);
        usernameField.setForeground(Color.WHITE);
        usernameField.setBounds(50,registerLabel.getY()+100, Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height);

        usernameField.addFocusListener(this);
        // username error label
        usernameLoginErrors = new LoginErrors("Invalid username: Can't be less than 6 characters");
        usernameLoginErrors.setBounds(50,usernameField.getY()+50, Constants.TEXTFIELD_SIZE.width,25);
        usernameLoginErrors1 = new LoginErrors("Invalid username: Username in use");
        usernameLoginErrors1.setBounds(50,usernameField.getY()+50, Constants.TEXTFIELD_SIZE.width,25);

        // password field
        passwordField = new CustomPasswordField("Enter Password",30);
        passwordField.setBounds(50,usernameField.getY()+100, Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height);
        passwordField.setBackground(Constants.SECONDARY_COLOR);
        passwordField.setForeground(Color.WHITE);
        passwordField.addFocusListener(this);
        // password error label
        passwordLoginErrors = new LoginErrors("Invalid: Size > 6, At Least 1 Upper and Lower Case Letter, 1 Special Char, 1 Number");
        passwordLoginErrors.setBounds(50,passwordField.getY()+50, Constants.TEXTFIELD_SIZE.width,25);
        // confirm password field
        confirmPasswordField =new CustomPasswordField("Confirm Password",30);
        confirmPasswordField.setBackground(Constants.SECONDARY_COLOR);
        confirmPasswordField.setForeground(Color.WHITE);
        confirmPasswordField.setBounds(50,
                passwordField.getY()+100,
                Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height);
        confirmPasswordField.addFocusListener(this);

        // confirm password error label
        confirmPasswordLoginErrors = new LoginErrors("Invalid: Passwords don't match");
        confirmPasswordLoginErrors.setBounds(50,confirmPasswordField.getY()+50, Constants.TEXTFIELD_SIZE.width,25);


        // register button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(50,confirmPasswordField.getY()+100, Constants.TEXTFIELD_SIZE.width, Constants.TEXTFIELD_SIZE.height);
        registerButton.addActionListener( this);
        registerButton.setBackground(Constants.BUTTON_COLOR);
        registerButton.setForeground(Color.WHITE);



        // register --> login
        JLabel loginLabel = new JLabel("Already a user? Login Here");
        loginLabel.setBounds((Constants.LOGINFRAME_SIZE.width-loginLabel.getPreferredSize().width)/2,registerButton.getY()+100,loginLabel.getPreferredSize().width+10,loginLabel.getPreferredSize().height);
        loginLabel.setForeground(Constants.SECONDARY_COLOR);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginGUI().setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }
        });




        getContentPane().add(registerLabel);

        getContentPane().add(usernameField);
        getContentPane().add(usernameLoginErrors);
        getContentPane().add(usernameLoginErrors1);


        getContentPane().add(passwordField);
        getContentPane().add(passwordLoginErrors);

        getContentPane().add(confirmPasswordField);
        getContentPane().add(confirmPasswordLoginErrors);


        getContentPane().add(registerButton);
        getContentPane().add(loginLabel);

    }


    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        Object fieldSource = e.getSource();
        if(fieldSource == usernameField){
            // valid username has to be greater or equal to 6
            if(usernameField.getText().length() < 6 || usernameField.isHasPlaceHolder()){
                usernameLoginErrors.setVisible(true);
            }
            else if(AdminDB.hasAdmin(usernameField.getText()) || CustomerDB.hasCustomer(usernameField.getText())){
                usernameLoginErrors.setVisible(false);
                usernameLoginErrors1.setVisible(true);
            }
            else{
                usernameLoginErrors.setVisible(false);
                usernameLoginErrors1.setVisible(false);
            }
        } else if (fieldSource == passwordField) {
            // if password isn't 6 char, has 1 upper case, 1 special, 1 number
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        Pattern p = Pattern.compile(passwordRegex);
        Matcher m = p.matcher(passwordField.getText());
        if(!m.find()) {
            passwordLoginErrors.setVisible(true);}
        else {
            passwordLoginErrors.setVisible(false);}
        }
        else if(fieldSource == confirmPasswordField){
            // if passwords don't match
            if(!passwordField.getText().equals(confirmPasswordField.getText())){
                confirmPasswordLoginErrors.setVisible(true);
            }
            else{
                confirmPasswordLoginErrors.setVisible(false);}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command =e.getActionCommand();
        if(command.equals("Register")){
            boolean isValid = !usernameLoginErrors.isVisible() && !passwordLoginErrors.isVisible() && !confirmPasswordLoginErrors.isVisible()
                     && !usernameField.isHasPlaceHolder() && !passwordField.isHasPlaceHolder() && !confirmPasswordField.isHasPlaceHolder();
            // result dialog
            JDialog resultDialog = new JDialog();
            resultDialog.setSize(Constants.RESULT_DIALOG_SIZE);
            resultDialog.setLocationRelativeTo(this);
            resultDialog.setModal(true);

            // result label
            JLabel resultLabel = new JLabel();
            resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
            resultDialog.add(resultLabel);
            resultDialog.getContentPane().setBackground(Constants.PRIMARY_COLOR);
            resultLabel.setForeground(Constants.SECONDARY_COLOR);

            if(isValid){
                String username = usernameField.getText();
                String password = passwordField.getText();
                CustomerDB.addCustomer(username,password);

                // show a dialog that the user has been added to the UserDB
                resultLabel.setText("Account Registered!");
                Logger.logUserRegistration(usernameField.getText(),"Customer");

                // take user back to the login gui (after closing dialog window)
                resultDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        dispose();
                        new LoginGUI().setVisible(true);
                    }
                });

                } else{
                //show an error label
                resultLabel.setText("Invalid Credentials");

            }
            resultDialog.setVisible(true);
            }
        }
    }

