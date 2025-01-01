package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import core.App;
import reservationlogs.Logger;
import services.*;
import products.Hotel;
import products.Flight;
import products.Taxi;
import services.Package;

public class PackageEditorGUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Package currentPackage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Hotel selectedHotel;
    private Flight selectedFlight;
    private Taxi selectedTaxi;
    private LocalDateTime taxiTime;
    private boolean discounted;
    private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("H:mm");
    private JTextField startDateField;
    private JTextField endDateField;
    private boolean isFromAdminReservations;
    private AdminReservationsGUI arg;
    private Reservation res;
    public PackageEditorGUI(Package pck, boolean isFromAdminReservations,Reservation res,AdminReservationsGUI arg) {
        currentPackage = pck;
        this.arg = arg;
        this.isFromAdminReservations = isFromAdminReservations;
        this.res =res;
        int packageId = pck.getId();
        startDate = currentPackage.getDateStart();
        endDate = currentPackage.getDateEnd();
        selectedHotel = currentPackage.getHotel();
        selectedFlight = currentPackage.getFlight();
        selectedTaxi = currentPackage.getTaxi();
        taxiTime = currentPackage.getTaxiTime();

        setTitle("Edit Travel Package #" + packageId);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        createMainPanel();
        createHotelSelectionPanel();
        createFlightSelectionPanel();
        createTaxiSelectionPanel();

        cardLayout.show(mainPanel, "main");
    }
    public PackageEditorGUI(Package pck) {
        this(pck, false, null,null);
    }
    private void createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current Package Info
        int row = 0;
        addLabelAndValue(panel, gbc, row++, "Current Hotel:", selectedHotel.getName());
        addLabelAndValue(panel, gbc, row++, "Current Flight:",
                String.format("%s to %s", selectedFlight.getDepartureCity(), selectedFlight.getArrivalCity()));
        addLabelAndValue(panel, gbc, row++, "Current Taxi:", selectedTaxi.getTaxiType());
        addLabelAndValue(panel, gbc, row++, "Start Date:", startDate.format(formatterDate));
        addLabelAndValue(panel, gbc, row++, "End Date:", endDate.format(formatterDate));

        // Date Selection
        row = addDateFields(panel, gbc, row);

        // Edit Buttons
        row = addEditButtons(panel, gbc, row);

        // Save Button
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        panel.add(saveButton, gbc);

        mainPanel.add(panel, "main");
    }

    private void addLabelAndValue(JPanel panel, GridBagConstraints gbc, int row,
                                  String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(value), gbc);
    }

    private int addDateFields(JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("New Start Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        startDateField = new JTextField(startDate.format(formatterDate), 20);
        panel.add(startDateField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("New End Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        endDateField = new JTextField(endDate.format(formatterDate), 20);
        panel.add(endDateField, gbc);

        return row + 1;
    }
    private void updateDates() throws DateTimeParseException {
        String startDateText = startDateField.getText().trim();
        String endDateText = endDateField.getText().trim();

        try {
            LocalDate newStartDate = LocalDate.parse(startDateText, formatterDate);
            LocalDate newEndDate = LocalDate.parse(endDateText, formatterDate);

            if (newEndDate.isBefore(newStartDate)) {
                throw new IllegalArgumentException("End date cannot be before start date");
            }

            startDate = newStartDate;
            endDate = newEndDate;
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Please enter dates in YYYY-MM-DD format", e.getParsedString(), e.getErrorIndex());
        }
    }

    private int addEditButtons(JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row++;

        JButton editHotelButton = new JButton("Edit Hotel");
        editHotelButton.addActionListener(e -> {
            try {
                updateDates();
                searchHotels(selectedFlight.getArrivalCity());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(editHotelButton, gbc);

        gbc.gridy = row++;
        JButton editFlightButton = new JButton("Edit Flight");
        editFlightButton.addActionListener(e -> {
            try {
                updateDates();
                searchFlights(selectedFlight.getDepartureCity(), selectedFlight.getArrivalCity());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(editFlightButton, gbc);

        gbc.gridy = row++;
        JButton editTaxiButton = new JButton("Edit Taxi");
        editTaxiButton.addActionListener(e -> {
            try {
                updateDates();
                LocalDate arrivalDate = selectedFlight.isDayChange() ?
                        startDate.plusDays(1) : startDate;
                LocalDateTime flightArrivalDateTime = LocalDateTime.of(
                        arrivalDate,
                        selectedFlight.getArrivalTime()
                );
                searchTaxis(selectedFlight.getArrivalCity(), flightArrivalDateTime);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(editTaxiButton, gbc);
        gbc.gridy = row++;
        JButton editPriceButton = new JButton("Add Discount");
        panel.add(editPriceButton,gbc);
        editPriceButton.addActionListener(e -> {
            String discountStr = JOptionPane.showInputDialog(
                    this,
                    "Enter discount percentage:",
                    "Add Discount",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (discountStr != null) {
                try {
                    int discount = Integer.parseInt(discountStr.trim());
                    if (discount < 0 || discount > 100) {
                        throw new IllegalArgumentException("Discount must be between 0 and 100.");
                    }
                    this.currentPackage.setDiscountedPrice((int) (currentPackage.getTotalCost() - discount * (0.01) *currentPackage.getTotalCost()));
                    discounted = true;

                    JOptionPane.showMessageDialog(this,
                            "Discount of " + discount + "% applied successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a valid integer for discount.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this,
                            ex.getMessage(),
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return row;
    }

    private void createHotelSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable hotelTable = new JTable();
        panel.add(new JScrollPane(hotelTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select Hotel");
        selectButton.addActionListener(e -> {
            int selectedRow = hotelTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedHotel = currentHotels.get(selectedRow);
                updateMainPanelInfo();
                cardLayout.show(mainPanel, "main");
            }
        });
        buttonPanel.add(selectButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "main"));
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(panel, "hotelSelection");
    }

    private void createFlightSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable flightTable = new JTable();
        panel.add(new JScrollPane(flightTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select Flight");
        selectButton.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedFlight = currentFlights.get(selectedRow);
                // Update taxi time based on new flight
                LocalDate arrivalDate = selectedFlight.isDayChange() ?
                        startDate.minusDays(1) : startDate;
                LocalDateTime flightArrivalDateTime = LocalDateTime.of(
                        arrivalDate,
                        selectedFlight.getArrivalTime()
                );
                searchTaxis(selectedFlight.getArrivalCity(), flightArrivalDateTime);
            }
        });
        buttonPanel.add(selectButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "main"));
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(panel, "flightSelection");
    }

    private void createTaxiSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable taxiTable = new JTable();
        panel.add(new JScrollPane(taxiTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select Taxi");
        selectButton.addActionListener(e -> {
            int selectedRow = taxiTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedTaxi = currentTaxis.get(selectedRow);
                updateMainPanelInfo();
                cardLayout.show(mainPanel, "main");
            }
        });
        buttonPanel.add(selectButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "main"));
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(panel, "taxiSelection");
    }

    private ArrayList<Hotel> currentHotels;
    private ArrayList<Flight> currentFlights;
    private ArrayList<Taxi> currentTaxis;

    private void searchHotels(String city) {
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

        ((JTable)((JScrollPane)((JPanel)mainPanel.getComponent(1))
                .getComponent(0)).getViewport().getView()).setModel(model);
        cardLayout.show(mainPanel, "hotelSelection");
    }

    private void searchFlights(String source, String destination) {
        ArrayList<Flight> flights = Flight.selectByCity(destination, source);
        currentFlights = Flight.availableSeatsListMaker(startDate, flights);

        if (currentFlights.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available flights found from " + source + " to " + destination,
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
                    flight.isDayChange() ? startDate.minusDays(1).format(formatterDate)
                            : startDate.format(formatterDate),
                    flight.getDepartureTime().format(formatterTime),
                    flight.getArrivalTime().format(formatterTime)
            });
        }

        ((JTable)((JScrollPane)((JPanel)mainPanel.getComponent(2))
                .getComponent(0)).getViewport().getView()).setModel(model);
        cardLayout.show(mainPanel, "flightSelection");
    }

    private void searchTaxis(String city, LocalDateTime taxiPickupDateTime) {
        ArrayList<Taxi> taxisInCity = Taxi.selectByCity(city);
        double distanceKm = selectedHotel.getDistanceToAirport();
        int travelTimeMinutes = (int) Math.ceil((distanceKm / 60.0) * 60);

        LocalDateTime taxiArrivalTime = taxiPickupDateTime.plusMinutes(travelTimeMinutes);
        taxiTime = taxiPickupDateTime;

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
                    "No available taxis found in " + city + " at " +
                            taxiPickupDateTime.format(formatterTime),
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

        ((JTable)((JScrollPane)((JPanel)mainPanel.getComponent(3))
                .getComponent(0)).getViewport().getView()).setModel(model);
        cardLayout.show(mainPanel, "taxiSelection");
    }

    private void updateMainPanelInfo() {
        JPanel mainPanel = (JPanel)this.mainPanel.getComponent(0);
        // Update hotel info
        ((JLabel)mainPanel.getComponent(1)).setText(selectedHotel.getName());
        // Update flight info
        ((JLabel)mainPanel.getComponent(3)).setText(
                String.format("%s to %s", selectedFlight.getDepartureCity(), selectedFlight.getArrivalCity()));
        // Update taxi info
        ((JLabel)mainPanel.getComponent(5)).setText(selectedTaxi.getTaxiType());
    }
    private void saveChanges() {
        try {
            updateDates();

            // Get IDs from selected components
            Integer newHotelId = selectedHotel != null ? selectedHotel.getId() : null;
            Integer newFlightId = selectedFlight != null ? selectedFlight.getId() : null;
            Integer newTaxiId = selectedTaxi != null ? selectedTaxi.getId() : null;

            // Convert dates if they've changed
            LocalDate newStartDate = startDate != null ? startDate : null;
            LocalDate newEndDate = endDate != null ? endDate : null;

            // Call the new edit method
            Package newPackage = PackageManager.editPackage(
                    currentPackage.getId(),
                    newHotelId,
                    newFlightId,
                    newTaxiId,
                    newStartDate,
                    newEndDate
            );

            // Handle discount
            if (discounted) {
                // Preserve the existing discounted price calculation
                int discountedPrice = currentPackage.getDiscountedPrice();
                newPackage.setDiscountedPrice(discountedPrice);
            } else {
                // If no discount applied, set discounted price equal to total cost
                newPackage.setDiscountedPrice(newPackage.getTotalCost());
            }

            // Save the updated package details to the file
            PackageManager.updatePackagesFile();

            int newCost = newPackage.getDiscountedPrice();

            JOptionPane.showMessageDialog(this,
                    "Package updated successfully!\nNew Total Cost: $" + newCost,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            if (isFromAdminReservations && res !=null) {
                services.Package newPack = PackageManager.retrievePackage(PackageManager.getNewID());
                Reservation newRes = ReservationsManagers.makeReservation(newPack, res.getCustomer());
                Vendor.packageSeller(newRes,res.getCustomer());
                ReservationsManagers.cancellationInitiator(res);
                arg.refreshReservationList();
            }
            Logger.logPackageModification(currentPackage.toString(), App.user.getUsername(),newPackage.toString());
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating package: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

}