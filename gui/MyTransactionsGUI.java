package gui;

import Users.Customer;
import services.Reservation;
import services.ReservationsManagers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MyTransactionsGUI extends JFrame {
    public MyTransactionsGUI(Customer cst) throws FileNotFoundException {
        File file = new File("services/transactions.txt");
        this.setSize(1000,1000);
        Scanner scn = new Scanner(file);
        JPanel mainPanel = new JPanel(new BorderLayout());
        DefaultTableModel dtm = new DefaultTableModel();
        JTable transactionsTable = new JTable(dtm);
        transactionsTable.setRowHeight(30);
        JScrollPane jScrollPane = new JScrollPane(transactionsTable);
        mainPanel.add(jScrollPane,BorderLayout.CENTER);
        this.add(mainPanel);
        dtm.addColumn("Transaction Date");
        dtm.addColumn("Amount($)");
        dtm.addColumn("Reservation Number");
        dtm.addColumn("From City");
        dtm.addColumn("To City");
        dtm.addColumn("Transaction Type");
        //fill the table
        while (scn.hasNextLine()){
            String line = scn.nextLine();
            String[] linSep = line.split(",");
            String[] row = new String[6];
            if(linSep[3].equals(String.valueOf(cst.getID()))){
                row[0] = linSep[0];
                row[1] = linSep[1];
                row[2] = linSep[2];
                Reservation res = ReservationsManagers.getReservation(Integer.parseInt(linSep[2]));
                row[3] = res.getRelatedPackage().getFlight().getDepartureCity();
                row[4] = res.getRelatedPackage().getFlight().getArrivalCity();
                row[5] = linSep[4];
                dtm.addRow(row);
            }
        }

    }
}
