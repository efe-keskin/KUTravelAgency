package gui;

import Users.Customer;
import constants.Constants;
import core.App;
import databases.CustomerDB;
import services.PackageManager;
import services.Reservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * GUI for displaying detailed information about a customer's travel history,
 * including reservations, spending statistics, and booking counts.
 */
public class CustomerDetailsGUI extends JFrame {

    private ArrayList<Reservation> resList;
    private int confirmedSize;
    private double moneySpent;
    private int hotelBookings = 0;
    private int flightBookings = 0;
    private int taxiBookings = 0;

    private JPanel panel1;
    private JTable travelTable;
    private JScrollPane dataScrollPane;
    private JPanel dataPanel;
    private JPanel infoPanel;
    private JPanel infoPanel1;
    private JPanel infoPanel2;
    private static final Font fontBig = new Font("Arial", Font.PLAIN, 20);
    private static final Font fontSmall = new Font("Arial", Font.PLAIN, 14);
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");

    /**
     * Constructs the CustomerDetailsGUI for a specific customer.
     *
     * @param customer the customer whose details are to be displayed
     */
    public CustomerDetailsGUI(Customer customer) {
        setTitle("Customer Details #" + customer.getID());
        setSize(1400, 800);

        panel1 = new JPanel(new BorderLayout());
        setContentPane(panel1);

        travelTable = new JTable();
        dataScrollPane = new JScrollPane(travelTable);
        dataPanel = new JPanel(new BorderLayout());
        infoPanel = new JPanel(new BorderLayout());
        infoPanel1 = new JPanel(new BorderLayout());

        panel1.setBackground(Color.decode("#F5EFE7"));
        infoPanel.setBackground(Color.decode("#F5EFE7"));
        dataPanel.setBackground(Color.decode("#F5EFE7"));
        dataScrollPane.setBackground(Color.decode("#F5EFE7"));

        try {
            customer.loadTravelHistory();
            resList = customer.getTravelHistory();

            DefaultTableModel dtm = new DefaultTableModel() {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
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
            dtm.addColumn("Discounted Price($)");
            dtm.addColumn("Discount Ratio");
            dtm.addColumn("Status");

            travelTable.setModel(dtm);
            travelTable.setRowHeight(30);
            JButton backButton = new JButton("Back");

            dataPanel.add(dataScrollPane, BorderLayout.SOUTH);
            dataPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
            panel1.add(dataPanel, BorderLayout.SOUTH);
            panel1.add(infoPanel, BorderLayout.NORTH);

            for (Reservation reservation : resList) {
                if (reservation.isStatus()) {
                    hotelBookings++;
                    flightBookings++;
                    taxiBookings++;
                    moneySpent += reservation.getRelatedPackage().getDiscountedPrice();
                }

                String[] row = new String[14];
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

                double totalCost = reservation.getRelatedPackage().getTotalCost();
                double discountedCost = reservation.getRelatedPackage().getDiscountedPrice();

                row[10] = df.format(totalCost);
                row[11] = df.format(discountedCost);

                double ratio = 100 - 100 * (discountedCost / totalCost);
                row[12] = df.format(ratio);
                row[13] = reservation.isStatus() ? "Confirmed" : "Cancelled";
                confirmedSize += reservation.isStatus() ? 1 : 0;

                dtm.addRow(row);
            }

            infoPanel.add(infoPanel1, BorderLayout.WEST);
            JTextPane custInfo = new JTextPane();
            custInfo.setFont(fontBig);
            custInfo.setText("Username: " + customer.getUsername()
                    + "\nID: " + customer.getID());

            infoPanel1.add(custInfo, BorderLayout.CENTER);
            infoPanel.setBorder(new EmptyBorder(30, 30, 30, 200));
            custInfo.setBackground(Color.decode("#D8C4B6"));
            custInfo.setEditable(false);
            panel1.add(backButton, BorderLayout.LINE_START);
            backButton.setBorder(new EmptyBorder(30, 30, 30, 30));
            backButton.setBackground(Color.decode("#3E5879"));
            backButton.setForeground(Color.decode("#F5EFE7"));
            backButton.addActionListener(e -> {
                dispose();
                if (App.isAdmin) {
                    new CustomerSearchGUI().setVisible(true);
                } else {
                    new CustomerUI().setVisible(true);
                }
            });

            infoPanel2 = new JPanel(new BorderLayout());
            infoPanel.add(infoPanel2, BorderLayout.EAST);
            JTextPane custInfo2 = new JTextPane();
            custInfo2.setEditable(false);
            custInfo2.setBorder(new EmptyBorder(10, 10, 10, 10));
            custInfo2.setFont(fontSmall);
            custInfo2.setBackground(Color.decode("#D8C4B6"));
            custInfo2.setText(
                    "Total Money Spent: $" + df.format(moneySpent)
                            + "     Average Spending Per Reservation: $"
                            + df.format(moneySpent / confirmedSize)
                            + "\n\nNumber of Reservations: " + resList.size()
                            + "     Number of Confirmed Reservations: " + confirmedSize
                            + "\n\nNumber of Cancelled Reservations: "
                            + (resList.size() - confirmedSize)
                            + "     Rate of Successful Reservations: %"
                            + df.format((confirmedSize / (double) resList.size()) * 100)
                            + "\n\nRate of Cancellation: %"
                            + df.format(((resList.size() - confirmedSize)
                            / (double) resList.size()) * 100)
                            + "     Number of Hotel Bookings: " + hotelBookings
                            + "\n\nNumber of Flight Bookings: " + flightBookings
                            + "     Number of Taxi Bookings: " + taxiBookings
            );

            infoPanel2.add(custInfo2, BorderLayout.EAST);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main method for testing the CustomerDetailsGUI.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Customer sevvalonna = CustomerDB.getCustomer("sevvalonna");
        new CustomerDetailsGUI(sevvalonna).setVisible(true);
    }
}
