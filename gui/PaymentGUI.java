package gui;

import Users.Customer;
import constants.Constants;
import services.Vendor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import services.Package;

public class PaymentGUI extends JFrame {
    private Package pck;
    private Customer cst;
    private LocalDateTime taxiTime;
    private LocalDate hotelStartDate;
    private LocalDate dateStart;
    private LocalDate dateEnd;

    private JPanel detailsPanel;
    private JPanel paymentPanel;
    private JButton confirmButton;
    private JButton cancelButton;

    public PaymentGUI(Customer cst, Package pck) {
        this.pck = pck;
        this.taxiTime = pck.getTaxiTime();
        this.cst = cst;
        this.hotelStartDate = pck.getHotelStart();
        this.dateStart = pck.getDateStart();
        this.dateEnd = pck.getDateEnd();

        setupFrame();
        initializeComponents();
        addComponents();
        addListeners();
    }

    private void setupFrame() {
        setTitle("Payment Confirmation");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Constants.PRIMARY_COLOR);
    }

    private void initializeComponents() {
        // Initialize details panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add package details
        addDetailsSection("Package Details");
        addDetailRow("Hotel:", (pck.getHotel().toString())+" Price per night: "+"$"+pck.getHotel().getPricePerNight()+" * "+pck.getDaysInHotel()+" = $"+ pck.getTotalCost());
        addDetailRow("Check-in:", hotelStartDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addDetailRow("Check-out:", dateEnd.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addDetailRow("Flight:", (pck.getFlight().toString())+" Price of the ticket: $"+(pck.getFlight().getPrice()));
        addDetailRow("Flight Date:", dateStart.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addDetailRow("Taxi:", (pck.getTaxi().toString())+" Estimated cost from the airport to the hotel: $"+pck.getTaxi().taxiPriceCalculator(pck.getHotel()));
        addDetailRow("Pickup Time:", taxiTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));

        // Add customer details
        addDetailsSection("Customer Information");
        addDetailRow("Name:", cst.getUsername());
        addDetailRow("ID:", String.valueOf(cst.getID()));

        // Add payment details
        addDetailsSection("Payment Details");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        addDetailRow("Total Amount:", currencyFormat.format(pck.getDiscountedPrice()));

        // Initialize payment panel
        paymentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        paymentPanel.setBackground(Constants.PRIMARY_COLOR);

        confirmButton = new JButton("Confirm Booking");
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(150, 40));

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(150, 40));
    }

    private void addComponents() {
        // Add scrollable details panel
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Add buttons panel
        paymentPanel.add(confirmButton);
        paymentPanel.add(cancelButton);
        add(paymentPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        confirmButton.addActionListener(e -> handleBooking());

        cancelButton.addActionListener(e -> {
            dispose();
        });
    }

    private void addDetailsSection(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        detailsPanel.add(titleLabel);
    }

    private void addDetailRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setPreferredSize(new Dimension(100, 20));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));

        row.add(labelComponent);
        row.add(valueComponent);
        detailsPanel.add(row);
    }

    private void handleBooking() {
        try {
            // Process the booking
            Vendor.packageSeller(pck, cst, taxiTime, hotelStartDate, dateStart, dateEnd);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed successfully!",
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error processing booking: " + ex.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


}