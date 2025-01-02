package gui;

import Users.Customer;
import constants.Constants;
import core.App;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CustomerUI extends JFrame{
    private JPanel CustomerPanel;
    private JButton bookTravelPackagesButton;
    private JButton myTravelHistoryButton;
    private JButton editReservationsButton;
    private JButton makeACustomTravelButton;
    private JButton myPaymentsButton;
    private JLabel customerPanelLabel;
    private JPanel imagePane;
    private JButton logOutButton;
    private JButton myProfileButton;


    public CustomerUI() {
            super("Customer Panel");
            setSize(Constants.MENUFRAME_SIZE);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setResizable(false);
            setContentPane(CustomerPanel);

        try {
            imagePane.add(new JLabel(new ImageIcon(ImageIO.read(new File("images\\Adana2.jpg")))),BorderLayout.CENTER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


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
                new EditReservationsUI((Customer) App.user).setVisible(true);
            }
        });
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.loggedIn =false;
                App.user = null;
                App.isAdmin = false;
                dispose();
                new LoginGUI().setVisible(true);
            }
        });
        myProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new CustomerDetailsGUI((Customer) App.user).setVisible(true);
            }
        });
    }


    }

