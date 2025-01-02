package gui;

import constants.Constants;
import databases.CustomerDB;
import services.PackageManager;
import services.ReservationsManagers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminGUI extends JFrame {
    private JLabel firstLabel;
    private JButton createEditDeletePackagesButton;
    private JButton reservationsManagementButton;
    private JButton customerSearchButton;
    private JPanel AdminPanel;
    private JLabel reservationsLabel;
    private JLabel activePackagesLabel;
    private JLabel usersLabel;
    private JButton editDeletePackagesButton;
    private JButton refreshButton;
    private JLabel userCount;
    private JLabel packageCount;
    private JLabel reservationsCount;
    private JButton logsButton;
    private static final Font font = new Font("Arial", Font.PLAIN, 14);
    public AdminGUI() {
        super("Admin Panel");
        setSize(Constants.MENUFRAME_SIZE);
        setLocationRelativeTo(null); // centers the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setContentPane(AdminPanel);
        CustomerDB.loadCustomers();
        userCount.setText(String.valueOf(CustomerDB.getSize()));
        reservationsCount.setText(String.valueOf(ReservationsManagers.getAllReservations().size()));
        PackageManager.packageDictGenerator();
        packageCount.setText(String.valueOf(PackageManager.getActivePackages().size()));
        createEditDeletePackagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new PackageMakerGUI().setVisible(true);
            }
        });
        editDeletePackagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editDeletePackagesFunction();
            }
        });



        reservationsManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AdminReservationsGUI().setVisible(true);
            }
        });
        customerSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    new CustomerSearchGUI().setVisible(true);
                });
            }
        });
        logsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminLogGUI().setVisible(true);
            }
        });
    }

    /**
     * Functions the edit delete button
     * */

    public void editDeletePackagesFunction(){
    if(!PackageManager.getActivePackages().isEmpty()) {
        dispose();
        new PackageManagementGUI().setVisible(true);
    }else{
        JOptionPane.showMessageDialog(this,"There are not any active packages at the moment.","Error",JOptionPane.INFORMATION_MESSAGE);
    }}


}
