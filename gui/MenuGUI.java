package gui;


import Users.Customer;
import constants.Constants;
import core.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGUI extends JFrame implements ActionListener {
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

    //Travel history button for customer menu
    JButton travelHistoryButton = new JButton("Travel History");
    travelHistoryButton.setBackground(Constants.BUTTON_COLOR);
    travelHistoryButton.setForeground(Color.WHITE);
    travelHistoryButton.setBounds(30,menuLabel.getY()+100,menuLabel.getPreferredSize().width+10,menuLabel.getPreferredSize().height);
    travelHistoryButton.setActionCommand( "Travel History");
    travelHistoryButton.addActionListener(this);






    //Label for admin menu
    JLabel adminLabel = new JLabel("Welcome Admin " + App.user.getUsername());
    adminLabel.setFont(new Font("SANS",Font.PLAIN,30));
    adminLabel.setHorizontalAlignment(SwingConstants.LEFT);
    adminLabel.setVerticalAlignment(SwingConstants.NORTH);
    adminLabel.setForeground(Constants.SECONDARY_COLOR);
    adminLabel.setBounds(30,20,menuLabel.getPreferredSize().width+500,menuLabel.getPreferredSize().height);



    //menuLabel.setBorder(BorderFactory.createLineBorder(Color.RED));

    //Admin GUI
    if(App.isAdmin){
        {getContentPane().add(adminLabel);}
    }
    //Customer GUI
    else{
        getContentPane().add(menuLabel);
        getContentPane().add(travelHistoryButton);


    }





}

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command){
            case "Travel History":
                if (App.user instanceof Customer) { // Check if the user is a Customer
                    Customer customer = (Customer) App.user; // Downcast to Customer
                    try {
//                        String[][] data = new String[customer.getTravelHistory().size()][customer.getTravelHistory().get(0).getPresentableDatalist().size()];
//                        for (int i = 0; i < customer.getTravelHistory().size(); i++) {
//                            for (int j = 0; j < customer.getTravelHistory().get(i).getPresentableDatalist().size(); j++) {
//                                data[i][j] = customer.getTravelHistory().get(i).getPresentableDatalist().get(j);
//                            }
//                        }
//                        // Column Names
//                        String[] columnNames = {"ID", "Departure Date", "Arrival Date", "Reservation Type",
//                                "Total Cost", "Status", "Flight ID", "Airline", "Departure City", "Arrival City",
//                                "Taxi Type", "Taxi City", "Hotel Name", "Hotel City", "Room Type"};
//                        JTable j = new JTable(data, columnNames);
//                        j.setBounds(30, 40, 200, 300);
//                        getContentPane().add(j);
                    }catch (IndexOutOfBoundsException a){
                        JDialog errorDialog = new JDialog();
                        errorDialog.setTitle("Travel History not Found");
                        errorDialog.setLayout(new BorderLayout());
                        errorDialog.setModal(true);
                        // create label (display result)
                        JLabel errorLabel = new JLabel("You don't have any previous reservations", SwingConstants.CENTER);
                        errorLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                        errorDialog.add(errorLabel);
                        errorDialog.setLocationRelativeTo(this);
                        errorDialog.pack();
                        errorDialog.setVisible(true);

                    }
                    catch (Exception ar){
                    System.out.println(ar);
                    }
                    }



        }

    }
}




