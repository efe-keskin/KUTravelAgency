package gui;


import Users.Admin;
import constants.Constants;
import core.App;

import javax.swing.*;
import java.awt.*;

public class MenuGUI extends JFrame {
public MenuGUI(){
    super("KU Travel App Menu");
    setSize(Constants.MENUFRAME_SIZE);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    setLayout(null);
    getContentPane().setBackground(Constants.PRIMARY_COLOR);

  addGuiComponent();
}
private void addGuiComponent(){

    //Label for customer menu
    JLabel menuLabel = new JLabel("Main Menu");
    menuLabel.setFont(new Font("SANS",Font.PLAIN,30));
    menuLabel.setHorizontalAlignment(SwingConstants.LEFT);
    menuLabel.setVerticalAlignment(SwingConstants.NORTH);
    menuLabel.setForeground(Constants.SECONDARY_COLOR);
    menuLabel.setBounds(30,20,menuLabel.getPreferredSize().width+10,menuLabel.getPreferredSize().height);
    //Label for menu
    JLabel adminLabel = new JLabel("Welcome Admin " + App.user.getUsername());
    adminLabel.setFont(new Font("SANS",Font.PLAIN,30));
    adminLabel.setHorizontalAlignment(SwingConstants.LEFT);
    adminLabel.setVerticalAlignment(SwingConstants.NORTH);
    adminLabel.setForeground(Constants.SECONDARY_COLOR);
    adminLabel.setBounds(30,20,menuLabel.getPreferredSize().width+500,menuLabel.getPreferredSize().height);



    //menuLabel.setBorder(BorderFactory.createLineBorder(Color.RED));

    if(App.isAdmin){
        {getContentPane().add(adminLabel);}
    }
    else{getContentPane().add(menuLabel);

    }





}

}




