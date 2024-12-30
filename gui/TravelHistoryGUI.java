package gui;

import Users.Customer;
import services.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TravelHistoryGUI extends JFrame {
    private JTable historyTable;
    private JPanel historyPanel;
    private JScrollPane historyScrollPane;
    public ArrayList<Reservation> travelHistory;
    DateTimeFormatter formatterDate = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatterTime = java.time.format.DateTimeFormatter.ofPattern("H:mm");
    DateTimeFormatter formatterDateTime = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");
    public TravelHistoryGUI(Customer customer) throws FileNotFoundException {
        setTitle("Travel History #" + customer.getID());
        travelHistory = customer.getTravelHistory();
        setSize(1000,1000);
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("Reservation ID");
        dtm.addColumn("From City");
        dtm.addColumn("To City");
        dtm.addColumn("Hotel Name");
        dtm.addColumn("Airline");
        dtm.addColumn("Flight Class");
        dtm.addColumn("Taxi Type");
        dtm.addColumn("Taxi Departure Time");
        dtm.addColumn("Start Date");
        dtm.addColumn("End Date");
        dtm.addColumn("Price($)");
        dtm.addColumn("Status");
        historyTable = new JTable(dtm);
        historyTable.setRowHeight(30);
        historyPanel = new JPanel(new BorderLayout());
        historyScrollPane = new JScrollPane(historyTable);
        historyPanel.add(historyScrollPane,BorderLayout.CENTER);
        this.add(historyPanel);

        for(Reservation reservation : travelHistory) {
            String[] row = new String[12];
            row[0] = String.valueOf(reservation.getId());
            row[1] = reservation.getRelatedPackage().getFlight().getDepartureCity();
            row[2] = reservation.getRelatedPackage().getFlight().getArrivalCity();
            row[3] = reservation.getRelatedPackage().getHotel().getName();
            row[4] = reservation.getRelatedPackage().getFlight().getAirline();
            row[5] = reservation.getRelatedPackage().getFlight().getTicketClass();
            row[6] = reservation.getRelatedPackage().getTaxi().getTaxiType();
            row[7] = reservation.getRelatedPackage().getTaxiTime().format(formatterDateTime).split(" ")[1];
            row[8] = reservation.getRelatedPackage().getDateStart().format(formatterDate);
            row[9] = reservation.getRelatedPackage().getDateEnd().format(formatterDate);
            row[10] = String.valueOf(reservation.getRelatedPackage().getDiscountedPrice());
            row[11] = reservation.isStatus() ? "Confirmed": "Cancelled";
            dtm.addRow(row);
        }
    }

}
