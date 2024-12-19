import custom.ErrorLabel;
import custom.PasswordFieldCustom;
import custom.TextFieldCustom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterGUI extends JFrame implements ActionListener, FocusListener {
    private ErrorLabel usernameErrorLabel, passwordErrorLabel,confirmPasswordErrorLabel, emailErrorLabel;
    private TextFieldCustom usernameField,emailField;
    private PasswordFieldCustom passwordField,confirmPasswordField;
    public RegisterGUI(){
        super("KU Travel Agency Register");
        setSize(Constants.FRAME_SIZE);
        setLocationRelativeTo(null); // centers the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        addGuiComponents();
    }
    private void addGuiComponents(){
        JLabel registerLabel = new JLabel("Register");
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setBounds(0,0,Constants.REGISTER_LABEL_SIZE.width,Constants.REGISTER_LABEL_SIZE.height);
        //username field
        usernameField = new TextFieldCustom("Enter username",30);
        usernameField.setBounds(50,registerLabel.getY()+100,Constants.TEXTFIELD_SIZE.width,Constants.TEXTFIELD_SIZE.height);

        usernameField.addFocusListener(this);
        // username error label
        usernameErrorLabel = new ErrorLabel("Invalid username: Can't be less than 6 characters");
        usernameErrorLabel.setBounds(50,usernameField.getY()+50,Constants.TEXTFIELD_SIZE.width,25);

        // password field
        passwordField = new PasswordFieldCustom("Enter Password",30);
        passwordField.setBounds(50,usernameField.getY()+100,Constants.TEXTFIELD_SIZE.width,Constants.TEXTFIELD_SIZE.height);
        passwordField.addFocusListener(this);
        // password error label
        passwordErrorLabel = new ErrorLabel("Invalid: Size > 6, At Least 1 Upper and Lower Case Letter, 1 Special Char, 1 Number");
        passwordErrorLabel.setBounds(50,passwordField.getY()+50,Constants.TEXTFIELD_SIZE.width,25);
        // confirm password field
        confirmPasswordField =new PasswordFieldCustom("Confirm Password",30);
        confirmPasswordField.setBounds(50,
                passwordField.getY()+100,
                Constants.TEXTFIELD_SIZE.width,Constants.TEXTFIELD_SIZE.height);
        confirmPasswordField.addFocusListener(this);

        // confirm password error label
        confirmPasswordErrorLabel = new ErrorLabel("Invalid: Passwords don't match");
        confirmPasswordErrorLabel.setBounds(50,confirmPasswordField.getY()+50,Constants.TEXTFIELD_SIZE.width,25);
        // email field
        emailField = new TextFieldCustom("Enter E-mail",30);
        emailField.setBounds(50,confirmPasswordField.getY()+100,Constants.TEXTFIELD_SIZE.width,Constants.TEXTFIELD_SIZE.height);
        emailField.addFocusListener(this);
        // email field error label
        emailErrorLabel = new ErrorLabel("Invalid: This is not a proper e-mail");
        emailErrorLabel.setBounds(50,emailField.getY()+50,Constants.TEXTFIELD_SIZE.width,25);


        // register button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(50,emailField.getY()+100,Constants.TEXTFIELD_SIZE.width,Constants.TEXTFIELD_SIZE.height);
        registerButton.addActionListener( this);



        // register --> login
        JLabel loginLabel = new JLabel("Already a user? Login Here");
        loginLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        loginLabel.setBounds((Constants.FRAME_SIZE.width-loginLabel.getPreferredSize().width)/2,registerButton.getY()+100,loginLabel.getPreferredSize().width+10,loginLabel.getPreferredSize().height);

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
        getContentPane().add(usernameErrorLabel);

        getContentPane().add(passwordField);
        getContentPane().add(passwordErrorLabel);

        getContentPane().add(confirmPasswordField);
        getContentPane().add(confirmPasswordErrorLabel);

        getContentPane().add(emailField);
        getContentPane().add(emailErrorLabel);

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
                usernameErrorLabel.setVisible(true);
            } else{
                usernameErrorLabel.setVisible(false);
            }
        } else if (fieldSource == passwordField) {
            // if password isn't 6 char, has 1 upper case, 1 special, 1 number
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        Pattern p = Pattern.compile(passwordRegex);
        Matcher m = p.matcher(passwordField.getText());
        if(!m.find()) {passwordErrorLabel.setVisible(true);}
        else {passwordErrorLabel.setVisible(false);}
        }
        else if(fieldSource == confirmPasswordField){
            // if passwords don't match
            if(!passwordField.getText().equals(confirmPasswordField.getText())){
                confirmPasswordErrorLabel.setVisible(true);
            }
            else{confirmPasswordErrorLabel.setVisible(false);}
        }
        else if(fieldSource == emailField){
            // checks email if its in valid format
            String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
            Pattern p = Pattern.compile(emailRegex);
            Matcher m = p.matcher(emailField.getText());
            if(!m.find()) {emailErrorLabel.setVisible(true);}
            else{ emailErrorLabel.setVisible(false);}

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command =e.getActionCommand();
        if(command.equals("Register")){
            boolean isValid = !usernameErrorLabel.isVisible() && !passwordErrorLabel.isVisible() && !confirmPasswordErrorLabel.isVisible()
                    && !emailErrorLabel.isVisible() && !usernameField.isHasPlaceHolder() && !passwordField.isHasPlaceHolder() && !confirmPasswordField.isHasPlaceHolder() && !emailField.isHasPlaceHolder();
            // result dialog
            JDialog resultDialog = new JDialog();
            resultDialog.setSize(Constants.RESULT_DIALOG_SIZE);
            resultDialog.setLocationRelativeTo(this);
            resultDialog.setModal(true);

            // result label
            JLabel resultLabel = new JLabel();
            resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
            resultDialog.add(resultLabel);

            if(isValid){
                String username = usernameField.getText();
                String password = passwordField.getText();
                UserDB.addUser(username,password);

                // show a dialog that the user has been added to the UserDB
                resultLabel.setText("Account Registered!");

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

