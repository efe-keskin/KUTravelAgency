package gui;

import Users.Customer;
import services.Reservation;
import services.ReservationsManagers;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.EventObject;

/**
 * GUI for managing reservations of a specific customer, including canceling reservations.
 */
public class EditReservationsUI extends JFrame {

    private JTable reservationTable;
    private JPanel mainPanel;
    private Customer cst;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final Font font = new Font("Arial", Font.PLAIN, 14);

    /**
     * Constructs the EditReservationsUI for a given customer.
     *
     * @param cst the customer whose reservations are being managed
     */
    public EditReservationsUI(Customer cst) {
        this.cst = cst;
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
                return column == 11;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 11 ? JPanel.class : String.class;
            }
        };

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

        populateTable();

        reservationTable.setRowHeight(60);
        reservationTable.setModel(tableModel);
        reservationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumn actionColumn = reservationTable.getColumnModel().getColumn(11);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());

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

    /**
     * Populates the reservation table with active reservations of the customer.
     */
    private void populateTable() {
        tableModel.setRowCount(0);
        for (Reservation reservation : cst.getTravelHistory()) {
            if (reservation.isStatus()) {
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

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setFont(font);

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
    }

    /**
     * Refreshes the reservation table.
     */
    private void refreshReservationList() {
        dispose();
        new EditReservationsUI(cst).setVisible(true);
    }

    /**
     * Returns to the customer UI.
     */
    private void backReturn() {
        dispose();
        new CustomerUI().setVisible(true);
    }

    /**
     * Custom button renderer for the action column.
     */
    class ButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return (JPanel) value;
        }
    }

    /**
     * Custom button editor for the action column.
     */
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
}