package gui;

import services.Reservation;
import services.ReservationsManagers;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EditReservationsUI extends JFrame {
    private JTable reservationTable;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final Font font = new Font("Arial", Font.PLAIN, 14);

    public EditReservationsUI() {
        setTitle("Reservation Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 800);

        mainPanel = new JPanel(new BorderLayout());
        reservationTable = new JTable();
        scrollPane = new JScrollPane(reservationTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        reservationTable.setFont(font);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 11; // Make only the Actions column editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 11 ? JPanel.class : String.class;
            }
        };

        // Add columns
        tableModel.addColumn("Reservation ID");
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

        // Populate table with reservations
        for (Reservation reservation : ReservationsManagers.getAllReservations()) {
            if (reservation.isStatus()) {  // Only show active reservations
                Object[] rowData = new Object[12];
                rowData[0] = String.valueOf(reservation.getId());
                rowData[1] = reservation.getRelatedPackage().getFlight().getDepartureCity();
                rowData[2] = reservation.getRelatedPackage().getFlight().getArrivalCity();
                rowData[3] = reservation.getRelatedPackage().getHotel().getName();
                rowData[4] = reservation.getRelatedPackage().getFlight().getAirline();
                rowData[5] = reservation.getRelatedPackage().getFlight().getTicketClass();
                rowData[6] = reservation.getRelatedPackage().getTaxi().getTaxiType();
                rowData[7] = reservation.getRelatedPackage().getTaxiTime().format(TIME_FORMATTER);
                rowData[8] = reservation.getRelatedPackage().getDateStart().format(DATE_FORMATTER);
                rowData[9] = reservation.getRelatedPackage().getDateEnd().format(DATE_FORMATTER);
                rowData[10] = String.valueOf(reservation.getRelatedPackage().getDiscountedPrice());

                // Create cancel button panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setFont(font);

                int reservationId = reservation.getId();
                cancelButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to cancel this reservation?",
                            "Confirm Cancellation",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            ReservationsManagers.cancellationInitiator(reservation);
                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        refreshReservationList();
                    }
                });

                buttonPanel.add(cancelButton);
                rowData[11] = buttonPanel;

                tableModel.addRow(rowData);
            }
        }

        reservationTable.setRowHeight(60);
        reservationTable.setModel(tableModel);
        reservationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Set up the button column
        TableColumn actionColumn = reservationTable.getColumnModel().getColumn(11);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());

        // Add refresh and back buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        refreshButton.setFont(font);
        backButton.setFont(font);

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        refreshButton.addActionListener(e -> refreshReservationList());
        backButton.addActionListener(e -> backReturn());

        add(mainPanel);
    }

    private void refreshReservationList() {
        dispose();
        new EditReservationsUI().setVisible(true);
    }

    private void backReturn() {
        dispose();
        new CustomerUI().setVisible(true);
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
            EditReservationsUI gui = new EditReservationsUI();
            gui.setVisible(true);
        });
    }
}