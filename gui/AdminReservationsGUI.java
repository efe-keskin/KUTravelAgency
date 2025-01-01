package gui;

import services.PackageManager;
import services.Reservation;
import services.ReservationsManagers;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminReservationsGUI extends JFrame {
    private JTable reservationTable;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final Font font = new Font("Arial", Font.PLAIN, 14);

    public AdminReservationsGUI() {
        setTitle("Admin Reservation Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 800);

        mainPanel = new JPanel(new BorderLayout());

        //top panel for search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        // Search components
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchField.setFont(font);
        searchButton.setFont(font);
        searchPanel.add(new JLabel("Search by Username: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Button components
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        refreshButton.setFont(font);
        backButton.setFont(font);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Table setup
        reservationTable = new JTable();
        scrollPane = new JScrollPane(reservationTable);
        reservationTable.setFont(font);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 13;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 13 ? JPanel.class : String.class;
            }
        };


        tableModel.addColumn("Reservation ID");
        tableModel.addColumn("User ID");
        tableModel.addColumn("Username");
        tableModel.addColumn("From City");
        tableModel.addColumn("To City");
        tableModel.addColumn("Hotel Name");
        tableModel.addColumn("Airline");
        tableModel.addColumn("Flight Class");
        tableModel.addColumn("Taxi Type");
        tableModel.addColumn("Taxi Time");
        tableModel.addColumn("Start Date");
        tableModel.addColumn("End Date");
        tableModel.addColumn("Price ($)");
        tableModel.addColumn("Actions");

        populateTable("");

        reservationTable.setRowHeight(60);
        reservationTable.setModel(tableModel);
        reservationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        //button column
        TableColumn actionColumn = reservationTable.getColumnModel().getColumn(13);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());

        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Make Reservation Button
        JButton makeReservationButton = new JButton("Make Reservation");
        makeReservationButton.setFont(font);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(makeReservationButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        refreshButton.addActionListener(e -> refreshReservationList());
        backButton.addActionListener(e -> backReturn());
        searchButton.addActionListener(e -> searchReservations());
        makeReservationButton.addActionListener(e -> {
            new CustomerChooserGUI().setVisible(true);
        });

        add(mainPanel);
    }

    private void populateTable(String searchUsername) {
        tableModel.setRowCount(0); // Clear existing rows

        for (Reservation reservation : ReservationsManagers.getAllReservations()) {
            if (reservation.isStatus()) {  // Only show active reservations
                String username = reservation.getCustomer().getUsername();

                // If search is empty or username matches search
                if (searchUsername.isEmpty() || username.toLowerCase().contains(searchUsername.toLowerCase())) {
                    Object[] rowData = new Object[14];
                    rowData[0] = String.valueOf(reservation.getId());
                    rowData[1] = String.valueOf(reservation.getCustomer().getID());
                    rowData[2] = username;
                    rowData[3] = reservation.getRelatedPackage().getFlight().getDepartureCity();
                    rowData[4] = reservation.getRelatedPackage().getFlight().getArrivalCity();
                    rowData[5] = reservation.getRelatedPackage().getHotel().getName();
                    rowData[6] = reservation.getRelatedPackage().getFlight().getAirline();
                    rowData[7] = reservation.getRelatedPackage().getFlight().getTicketClass();
                    rowData[8] = reservation.getRelatedPackage().getTaxi().getTaxiType();
                    rowData[9] = reservation.getRelatedPackage().getTaxiTime().format(TIME_FORMATTER);
                    rowData[10] = reservation.getRelatedPackage().getDateStart().format(DATE_FORMATTER);
                    rowData[11] = reservation.getRelatedPackage().getDateEnd().format(DATE_FORMATTER);
                    rowData[12] = String.valueOf(reservation.getRelatedPackage().getDiscountedPrice());

                    // Create button panel
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                    JButton cancelButton = new JButton("Cancel");
                    JButton editButton = new JButton("Edit");
                    cancelButton.setFont(font);
                    editButton.setFont(font);

                    cancelButton.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to cancel this reservation?",
                                "Confirm Cancellation",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                // Get the reservation ID from the current row
                                int row = reservationTable.getEditingRow();
                                String reservationId = (String) tableModel.getValueAt(row, 0);

                                // Perform cancellation
                                ReservationsManagers.cancellationInitiator(ReservationsManagers.getReservation(Integer.parseInt(reservationId)));

                                // Instead of calling refreshReservationList, remove just this row
                                tableModel.removeRow(row);

                                JOptionPane.showMessageDialog(this,
                                        "Reservation cancelled successfully",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);

                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(this,
                                        "Error canceling reservation: " + ex.getMessage(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });

                    editButton.addActionListener(e -> {
                        int row = reservationTable.getEditingRow();
                        String reservationId = (String) tableModel.getValueAt(row, 0);
                        services.Reservation thisRes = ReservationsManagers.getReservation(Integer.parseInt(reservationId));
                        services.Package current = ReservationsManagers.getReservation(Integer.parseInt(reservationId)).getRelatedPackage();
                        new PackageEditorGUI(current,true,thisRes,this).setVisible(true);

                    });

                    buttonPanel.add(editButton);
                    buttonPanel.add(cancelButton);
                    rowData[13] = buttonPanel;

                    tableModel.addRow(rowData);
                }
            }
        }
    }

    private void searchReservations() {
        String searchTerm = searchField.getText().trim();
        populateTable(searchTerm);
    }

    public void refreshReservationList() {
        searchField.setText("");
        populateTable("");
    }

    private void backReturn() {
        dispose();
        new AdminGUI().setVisible(true);
    }

    // Button renderer for the action column
    class ButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return (JPanel) value;
        }
    }

    // Button editor for the action column
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            table.setRowSelectionInterval(row, row);  // Force row selection
            panel = (JPanel) value;
            return panel;
        }


        @Override
        public Object getCellEditorValue() {
            return panel;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminReservationsGUI gui = new AdminReservationsGUI();
            gui.setVisible(true);
        });
    }
}