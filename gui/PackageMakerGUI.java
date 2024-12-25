package gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import core.App;
import products.Hotel;
import products.Flight;
import products.Taxi;
import services.Package;
import services.PackageManager;

public class PackageMakerGUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextField sourceCity;
    private JTextField destinationCity;
    private JTable resultsTable;
    private LocalDate startDate;
    private LocalDate endDate;
    private Hotel selectedHotel;
    private Flight selectedFlight;
    DateTimeFormatter formatterDate = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatterTime = java.time.format.DateTimeFormatter.ofPattern("H:mm");
    public PackageMakerGUI() {
        setTitle("Travel Package Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        createInitialPanel();
        createHotelSelectionPanel();
        createFlightSelectionPanel();
        createTaxiSelectionPanel();

    }
/**
 * This method
 * **/
    private void createInitialPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Source City
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Source City:"), gbc);
        gbc.gridx = 1;
        sourceCity = new JTextField(20);
        panel.add(sourceCity, gbc);

        // Destination City
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Destination City:"), gbc);
        gbc.gridx = 1;
        destinationCity = new JTextField(20);
        panel.add(destinationCity, gbc);

        // Date Selection
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField startDateField = new JTextField(20);
        panel.add(startDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField endDateField = new JTextField(20);
        panel.add(endDateField, gbc);

        // Search Button
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton searchButton = new JButton("Search Hotels");
        searchButton.addActionListener(e -> {
            try {
                startDate = LocalDate.parse(startDateField.getText());
                endDate = LocalDate.parse(endDateField.getText());
                searchHotels(destinationCity.getText());
                cardLayout.show(mainPanel, "hotelSelection");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid dates in YYYY-MM-DD format",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(searchButton, gbc);

        mainPanel.add(panel, "initial");
    }


    private void createHotelSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select Hotel");
        selectButton.addActionListener(e -> {
            int selectedRow = resultsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int hotelId = (int) resultsTable.getValueAt(selectedRow, 0);
                selectedHotel = currentHotels.get(selectedRow);
                searchFlights(sourceCity.getText(), destinationCity.getText());
                cardLayout.show(mainPanel, "flightSelection");
            }
        });
        buttonPanel.add(selectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "hotelSelection");
    }

    private void createTaxiSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable taxiTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(taxiTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select Taxi");
        selectButton.addActionListener(e -> {
            int selectedRow = taxiTable.getSelectedRow();
            if (selectedRow >= 0) {
                int taxiId = (int) taxiTable.getValueAt(selectedRow, 0);
                selectedTaxi = currentTaxis.get(selectedRow);
                createPackage();
            }
        });
        buttonPanel.add(selectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "taxiSelection");
    }

    private void searchTaxis(String city) {
        ArrayList<Taxi> taxisInCity = Taxi.selectByCity(city);
        currentTaxis = Taxi.availableCarsListMaker(startDate, taxisInCity);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Taxi ID");
        model.addColumn("Type");
        model.addColumn("Base Fare($)");
        model.addColumn("Per Km Rate($/km)");
        model.addColumn("Total Price($)");

        for (Taxi taxi : currentTaxis) {
            model.addRow(new Object[]{
                    taxi.getId(),
                    taxi.getTaxiType(),
                    taxi.getBaseFare(),
                    taxi.getPerKmRate(),
                    taxi.taxiPriceCalculator(selectedHotel)
            });
        }

        ((JTable)((JScrollPane)((JPanel)mainPanel.getComponent(3))
                .getComponent(0)).getViewport().getView()).setModel(model);
    }
    private ArrayList<Flight> currentFlights;
    private ArrayList<Taxi> currentTaxis;
    private Taxi selectedTaxi;
    private void createFlightSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable flightTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(flightTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select Flight");
        selectButton.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedFlight = currentFlights.get(selectedRow);
                searchTaxis(destinationCity.getText());
                cardLayout.show(mainPanel, "taxiSelection");
            }
        });
        buttonPanel.add(selectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "flightSelection");
    }

    private ArrayList<Hotel> currentHotels; // Add this as class field

    private void searchHotels(String city) {
        ArrayList<Hotel> hotelsInCity = Hotel.selectByCity(city);
        currentHotels = Hotel.availableRoomsListMaker(startDate, endDate, hotelsInCity);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Hotel ID");
        model.addColumn("Hotel Name");
        model.addColumn("Room Type");
        model.addColumn("Price/Night($)");
        model.addColumn("Distance to Airport(km)");

        for (Hotel hotel : currentHotels) {
            model.addRow(new Object[]{
                    hotel.getId(),
                    hotel.getName(),
                    hotel.getRoomType(),
                    hotel.getPricePerNight(),
                    hotel.getDistanceToAirport()
            });
        }

        resultsTable.setModel(model);
        resultsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void searchFlights(String source, String destination) {
        ArrayList<Flight> flights = Flight.selectByCity(destination, source);
        currentFlights = Flight.availableSeatsListMaker(startDate, flights);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Flight ID");
        model.addColumn("Price($)");
        model.addColumn("Duration");
        model.addColumn("Departure Date");
        model.addColumn("Departure Time");
        model.addColumn("Arrival Time");

        for (Flight flight : currentFlights) {
            model.addRow(new Object[]{
                    flight.getId(),
                    flight.getPrice(),
                    flight.getDuration(),
                    flight.isDayChange() ? startDate.minusDays(1).format(formatterDate) : startDate.format(formatterDate),
                    flight.getDepartureTime().format(formatterTime),
                    flight.getArrivalTime().format(formatterTime)
            });
        }

        ((JTable)((JScrollPane)((JPanel)mainPanel.getComponent(2))
                .getComponent(0)).getViewport().getView()).setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PackageMakerGUI gui = new PackageMakerGUI();
            gui.setVisible(true);
        });
    }
    private void createPackage() {
        try {
            Package travelPackage = PackageManager.makePackage(
                    App.isAdmin?"offered":"custom",
                    selectedHotel.getId(),
                    selectedFlight.getId(),
                    selectedTaxi.getId(),
                    startDate,
                    endDate
            );

            JOptionPane.showMessageDialog(this,
                    "Package created successfully!\nTotal Cost: $" + travelPackage.getTotalCost(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating package: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
