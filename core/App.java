package core;

import Users.User;
import gui.LoginGUI;
import gui.MenuGUI;
import gui.RegisterGUI;

import javax.swing.*;

public class App {
    public static User user;
    public static boolean loggedIn = false;
    public static boolean isAdmin = false;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(loggedIn){new MenuGUI().setVisible(true);}
                else{
                    new LoginGUI().setVisible(true);
                    new RegisterGUI().setVisible(false);
                }

            }
        });
    }
}
