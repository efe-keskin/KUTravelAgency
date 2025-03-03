package gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import Users.Customer;
import constants.Constants;
import core.App;
import products.Hotel;
import products.Flight;
import products.Taxi;
import reservationlogs.Logger;
import services.Package;
import services.PackageManager;
/**
 * GUI for creating and managing travel packages.
 */
public class PackageMakerGUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JComboBox sourceCity;
    private JComboBox destinationCity;
    private JTable resultsTable;
    private LocalDate startDate;
    private LocalDate endDate;
    private Hotel selectedHotel;
    private Flight selectedFlight;
    private LocalDateTime taxiTime;
    private boolean isReservationMaker;
    DateTimeFormatter formatterDate = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatterTime = java.time.format.DateTimeFormatter.ofPattern("H:mm");
    /**
     * Default constructor for the PackageMakerGUI.
     */
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
     * Creates the initial panel for selecting travel details.
     */
private void createInitialPanel() {
    String[] cityNames = {
            "Istanbul", "Paris", "Berlin", "Rome", "Amsterdam", "Madrid",
            "Vienna", "Prague", "Budapest", "Dublin", "Copenhagen", "Stockholm",
            "Helsinki", "Ankara", "Lisbon", "Brussels", "Zurich", "Oslo",
            "Athens", "Edinburgh", "Dubai"
    };
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Source City
    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(new JLabel("Source City:"), gbc);
    gbc.gridx = 1;
    sourceCity = new JComboBox<>(cityNames);
    panel.add(sourceCity, gbc);

    // Destination City
    gbc.gridx = 0; gbc.gridy = 1;
    panel.add(new JLabel("Destination City:"), gbc);
    gbc.gridx = 1;
    destinationCity = new JComboBox<>(cityNames);
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
    searchButton.setFont(Constants.font);
    searchButton.addActionListener(e -> {
        try {
            LocalDate today = LocalDate.now();
            LocalDate proposedStartDate = LocalDate.parse(startDateField.getText());
            LocalDate proposedEndDate = LocalDate.parse(endDateField.getText());

            // Validate dates are not in the past
            if (proposedStartDate.isBefore(today)) {
                JOptionPane.showMessageDialog(this,
                        "Start date cannot be in the past",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate end date is not before start date
            if (proposedEndDate.isBefore(proposedStartDate)) {
                JOptionPane.showMessageDialog(this,
                        "End date cannot be before start date",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If all validations pass, set the dates and proceed
            startDate = proposedStartDate;
            endDate = proposedEndDate;
            searchHotels((String) destinationCity.getSelectedItem());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid dates in YYYY-MM-DD format",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    });
    panel.add(searchButton, gbc);

    // Back Button
    gbc.gridx = 0; gbc.gridy = 5;
    JButton backButton = new JButton("Back");
    backButton.setFont(Constants.font);
    backButton.addActionListener(e -> {
        if(App.isAdmin){dispose(); new AdminGUI().setVisible(true);}
        else{dispose(); new CustomerUI().setVisible(true);}
    });
    panel.add(backButton, gbc);

    mainPanel.add(panel, "initial");
}

    /**
     * Creates the panel for selecting hotels.
     */
    private void createHotelSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        // Back Button
        JButton backButton = new JButton("Back to Search");
        backButton.setFont(Constants.font);
        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "initial");
        });
        buttonPanel.add(backButton);

        JButton selectButton = new JButton("Select Hotel");
        selectButton.setFont(Constants.font);
        selectButton.addActionListener(e -> {
            int selectedRow = resultsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int hotelId = (int) resultsTable.getValueAt(selectedRow, 0);
                selectedHotel = currentHotels.get(selectedRow);
                searchFlights((String)sourceCity.getSelectedItem(), (String) destinationCity.getSelectedItem());
            }
        });
        buttonPanel.add(selectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "hotelSelection");
    }

    /**
     * Creates the panel for selecting taxis.
     */
    private void createTaxiSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable taxiTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(taxiTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        // Back Button
        JButton backButton = new JButton("Back to Flights");
        backButton.setFont(Constants.font);
        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "flightSelection");
        });
        buttonPanel.add(backButton);

        JButton selectButton = new JButton("Select Taxi");
        selectButton.setFont(Constants.font);
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
    /**
     * Searches for taxis in the selected city.
     * @param city City for taxi search.
     * @param taxiPickupDateTime Pickup date and time for taxi.
     */
    void searchTaxis(String city, LocalDateTime taxiPickupDateTime) {
        ArrayList<Taxi> taxisInCity = Taxi.selectByCity(city);
        double distanceKm = selectedHotel.getDistanceToAirport();
        int travelTimeMinutes = (int) Math.ceil((distanceKm / 60.0) * 60);

        LocalDateTime taxiArrivalTime = taxiPickupDateTime.plusMinutes(travelTimeMinutes);
        taxiTime = taxiPickupDateTime;
        System.out.println(taxiTime);

        currentTaxis = new ArrayList<>();
        for (Taxi taxi : taxisInCity) {
            boolean hasAvailability = true;
            LocalDateTime checkTime = taxiPickupDateTime;

            while (!checkTime.isAfter(taxiArrivalTime)) {
                if (taxi.getAvailabilityForDateTime(checkTime) <= 0) {
                    hasAvailability = false;
                    break;
                }
                checkTime = checkTime.plusMinutes(2);
            }

            if (hasAvailability) {
                currentTaxis.add(taxi);
            }
        }
        if (currentTaxis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available taxis found in " + city + " at " + taxiPickupDateTime.format(formatterTime),
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
        model.addColumn("Journey Time(min)");

        for (Taxi taxi : currentTaxis) {
            model.addRow(new Object[]{
                    taxi.getId(),
                    taxi.getTaxiType(),
                    taxi.getBaseFare(),
                    taxi.getPerKmRate(),
                    taxi.taxiPriceCalculator(selectedHotel),
                    travelTimeMinutes
            });
        }

        JTable taxiTable = (JTable)((JScrollPane)((JPanel)mainPanel.getComponent(3))
                .getComponent(0)).getViewport().getView();
        taxiTable.setModel(model);
        cardLayout.show(mainPanel, "taxiSelection");
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

        // Back Button
        JButton backButton = new JButton("Back to Hotels");
        backButton.setFont(Constants.font);
        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "hotelSelection");
        });
        buttonPanel.add(backButton);

        JButton selectButton = new JButton("Select Flight");
        selectButton.setFont(Constants.font);
        selectButton.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedFlight = currentFlights.get(selectedRow);

                LocalDate arrivalDate = selectedFlight.isDayChange()
                        ? startDate.plusDays(1)
                        : startDate;

                LocalDateTime flightArrivalDateTime = LocalDateTime.of(
                        arrivalDate,
                        selectedFlight.getArrivalTime()
                );

                searchTaxis((String) destinationCity.getSelectedItem(), flightArrivalDateTime);
            }
        });

        buttonPanel.add(selectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "flightSelection");
    }

    private ArrayList<Hotel> currentHotels; // Add this as class field
    /**
     * Searches for hotels in the selected destination city.
     * @param city Destination city for hotel search.
     */
    void searchHotels(String city) {
        ArrayList<Hotel> hotelsInCity = Hotel.selectByCity(city);
        currentHotels = Hotel.availableRoomsListMaker(startDate, endDate, hotelsInCity);

        if (currentHotels.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available hotels found in " + city + " for selected dates",
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
        cardLayout.show(mainPanel, "hotelSelection");
    }
    /**
     * Searches for flights between source and destination cities.
     * @param source Source city.
     * @param destination Destination city.
     */
    void searchFlights(String source, String destination) {
        ArrayList<Flight> flights = Flight.selectByCity(destination, source);
        currentFlights = Flight.availableSeatsListMaker(startDate, flights);

        if (currentFlights.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available flights found from " + source + " to " + destination,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new PackageMakerGUI().setVisible(true);
            return;
        }
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Flight ID");
        model.addColumn("Airline");
        model.addColumn("Price($)");
        model.addColumn("Duration");
        model.addColumn("Departure Date");
        model.addColumn("Departure Time");
        model.addColumn("Arrival Time");

        for (Flight flight : currentFlights) {
            model.addRow(new Object[]{
                    flight.getId(),
                    flight.getAirline(),
                    flight.getPrice(),
                    flight.getDuration(),
                    flight.isDayChange() ? startDate.minusDays(1).format(formatterDate) : startDate.format(formatterDate),
                    flight.getDepartureTime().format(formatterTime),
                    flight.getArrivalTime().format(formatterTime)
            });
        }

        ((JTable)((JScrollPane)((JPanel)mainPanel.getComponent(2))
                .getComponent(0)).getViewport().getView()).setModel(model);
        cardLayout.show(mainPanel, "flightSelection");
    }

    /**
     * Constructor with reservation maker option.
     * @param isReservationMaker Specifies if the instance is for reservation creation.
     */
    public PackageMakerGUI(boolean isReservationMaker){
        this.isReservationMaker = isReservationMaker;
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
     * Creates the final travel package and logs the details.
     */
    private void createPackage() {
        try {
            Package travelPackage = PackageManager.makePackage(
                    App.isAdmin?"offered":"custom",
                    selectedHotel.getId(),
                    selectedFlight.getId(),
                    selectedTaxi.getId(),
                    startDate,
                    endDate,
                    taxiTime
            );
            travelPackage.setTaxiTime(taxiTime);
            JOptionPane.showMessageDialog(this,
                    "Package created successfully!\nTotal Cost: $" + travelPackage.getTotalCost(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            Logger.logPackageCreation(String.valueOf(travelPackage.getId()), App.user.getUsername(),travelPackage.toString());
            dispose();
            if(App.isAdmin){
                if(!isReservationMaker) {
                    new AdminGUI().setVisible(true);
                }
                else{new AdminGUI().setVisible(true);
                    new PaymentGUI((Customer) App.user,travelPackage).setVisible(true);}
            }
            else{
                new CustomerUI().setVisible(true);
                new PaymentGUI((Customer) App.user,travelPackage).setVisible(true);}
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating package: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
