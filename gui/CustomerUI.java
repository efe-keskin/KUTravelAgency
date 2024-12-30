package gui;

import Users.Customer;
import constants.Constants;
import core.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

public class CustomerUI extends JFrame{
    private JPanel CustomerPanel;
    private JButton bookTravelPackagesButton;
    private JButton myTravelHistoryButton;
    private JButton editReservationsButton;
    private JButton makeACustomTravelButton;
    private JButton myPaymentsButton;
    private JLabel customerPanelLabel;


    public CustomerUI() {
            super("Customer Panel");
            setSize(Constants.MENUFRAME_SIZE);
            setLocationRelativeTo(null); // Centers the JFrame
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setResizable(false);

            setContentPane(CustomerPanel); // Use the IntelliJ-designed CustomerPanel


            // Button action for "Make a Custom Travel"
            makeACustomTravelButton.addActionListener(e -> {
                dispose();
                new PackageMakerGUI().setVisible(true);
            });
        bookTravelPackagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TravelSelectorGUI().setVisible(true);
            }
        });
        myTravelHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    new TravelHistoryGUI((Customer)App.user).setVisible(true);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        myPaymentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    new MyTransactionsGUI((Customer) App.user).setVisible(true);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        editReservationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new EditReservationsUI().setVisible(true);
            }
        });
    }


    }

