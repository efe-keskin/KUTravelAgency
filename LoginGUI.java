import custom.PasswordFieldCustom;
import custom.TextFieldCustom;

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
        setSize(Constants.FRAME_SIZE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Constants.PRIMARY_COLOR);

        addGuiComponent();
    }
    private void addGuiComponent(){
        JLabel loginImage = CustomTools.loadImage(Constants.LOGIN_IMAGE_PATH);
        loginImage.setBounds((Constants.FRAME_SIZE.width - loginImage.getPreferredSize().width)/2,
                25,Constants.LOGIN_IMAGE_SIZE.width,Constants.LOGIN_IMAGE_SIZE.height
        );

        // username field
        usernameField = new TextFieldCustom("Enter Username",30);

        usernameField.setBackground(Constants.SECONDARY_COLOR);
        usernameField.setForeground(Color.WHITE);
        usernameField.setBounds(
                50,
                loginImage.getY()+315,
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
                Constants.BUTTON_SIZE.width,Constants.BUTTON_SIZE.height

        );
        loginButton.addActionListener(this);

        // login -> register label
        JLabel registerLabel = new JLabel("Not registered? Click Here!");
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setBackground(Constants.SECONDARY_COLOR);
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setBounds( (Constants.FRAME_SIZE.width-registerLabel.getPreferredSize().width)/2,
                loginButton.getY()+100, registerLabel.getPreferredSize().width+10,registerLabel.getPreferredSize().height );
        //to debug
        registerLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
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
        getContentPane().add(loginImage);
        getContentPane().add(usernameField);
        getContentPane().add(passwordField);
        getContentPane().add(loginButton);
        getContentPane().add(registerLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equalsIgnoreCase("Login")){
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
            resultDialog.getContentPane().setBackground(Constants.PRIMARY_COLOR);
            resultLabel.setForeground(Constants.SECONDARY_COLOR);

            // retrieve entered credentials
            String username = usernameField.getText();
            String password = passwordField.getText();

            // validate credentials in UserDB
            if(UserDB.getUser(username) !=null){
                // checks password
                String validPass = UserDB.getUser(username);
                if(password.equals(validPass)){
                    //display a success dialog
                    resultLabel.setText("Login Successful!");
                }
                else{
                    // display an incorrect password dialog
                    resultLabel.setText("Invalid Password");
                }
            }
            else{
                // display an incorrect username dialog
                resultLabel.setText("Invalid Username");

            }
            resultDialog.setVisible(true);




        }
    }
}
