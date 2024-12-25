package gui;

import constants.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminGUI extends JFrame {
    private JLabel firstLabel;
    private JButton createEditDeletePackagesButton;
    private JButton allReservationsButton;
    private JButton cancelReservationsButton;
    private JButton createReservationsButton;
    private JButton customerSearchButton;
    private JPanel AdminPanel;
    private JLabel reservationsLabel;
    private JLabel activePackagesLabel;
    private JLabel usersLabel;

    public AdminGUI() {
        super("Admin Panel");
        setSize(Constants.MENUFRAME_SIZE);
        setLocationRelativeTo(null); // centers the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setContentPane(AdminPanel);
        createEditDeletePackagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new PackageMakerGUI().setVisible(true);
            }
        });
    }



}
