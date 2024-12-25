package core;

import Users.User;
import gui.AdminGUI;
import gui.LoginGUI;
import gui.MenuGUI;
import gui.RegisterGUI;

import javax.swing.*;

public class App {
    /************** Pledge of Honor ******************************************
     I hereby certify that I have completed this programming project on my own
     without any help from anyone else. The effort in the project thus belongs
     completely to me. I did not search for a solution, or I did not consult any
     program written by others or did not copy any program from other sources. I
     read and followed the guidelines provided in the project description.
     READ AND SIGN BY WRITING YOUR NAME SURNAME AND STUDENT ID
     SIGNATURE: <Muhammed Efe Keskin, 0083781>
     *************************************************************************/
    public static User user;
    public static boolean loggedIn = false;
    public static boolean isAdmin = false;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminGUI().setVisible(false);
                if(loggedIn){new MenuGUI().setVisible(true);}
                else{
                    new LoginGUI().setVisible(true);
                    new RegisterGUI().setVisible(false);
                }

            }
        });
    }
}
