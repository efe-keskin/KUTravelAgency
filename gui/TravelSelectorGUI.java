package gui;

import Users.Customer;
import core.App;
import services.PackageManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class TravelSelectorGUI extends JFrame {
    private JTable packageTable;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Font font = new Font("Arial", Font.PLAIN, 14);

    public TravelSelectorGUI() {
        setTitle("Travel Selector");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 800);

        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout());
        packageTable = new JTable();
        jScrollPane = new JScrollPane(packageTable);
        mainPanel.add(jScrollPane, BorderLayout.CENTER);
        packageTable.setFont(font);

        // Create table model
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
        tableModel.addColumn("PackageID");
        tableModel.addColumn("From City");
        tableModel.addColumn("To City");
        tableModel.addColumn("Airline");
        tableModel.addColumn("Flight Class");
        tableModel.addColumn("Hotel Name");
        tableModel.addColumn("Taxi Type");
        tableModel.addColumn("Price ($)");
        tableModel.addColumn("Discounted Price ($)");
        tableModel.addColumn("Start Date");
        tableModel.addColumn("End Date");
        tableModel.addColumn("Actions");

        // Load packages
        PackageManager.packageDictGenerator();
        for (int id : PackageManager.packageDict.keySet()) {
            if (Objects.equals(PackageManager.retrievePackage(id).getType(), "offered")) {
                Object[] rowData = new Object[12];
                rowData[0] = String.valueOf(id);
                rowData[1] = PackageManager.retrievePackage(id).getFlight().getDepartureCity();
                rowData[2] = PackageManager.retrievePackage(id).getFlight().getArrivalCity();
                rowData[3] = PackageManager.retrievePackage(id).getFlight().getAirline();
                rowData[4] = PackageManager.retrievePackage(id).getFlight().getTicketClass();
                rowData[5] = PackageManager.retrievePackage(id).getHotel().getName();
                rowData[6] = PackageManager.retrievePackage(id).getTaxi().getTaxiType();
                rowData[7] = String.valueOf(PackageManager.retrievePackage(id).getTotalCost());
                rowData[8] = String.valueOf(PackageManager.retrievePackage(id).getDiscountedPrice());
                rowData[9] = PackageManager.retrievePackage(id).getDateStart().format(DATE_FORMATTER);
                rowData[10] = PackageManager.retrievePackage(id).getDateEnd().format(DATE_FORMATTER);

                // Create Details button
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                JButton detailsButton = new JButton("Details");
                detailsButton.setFont(font);

                detailsButton.addActionListener(e -> {
              //      new PackageDetailsGUI(packageId).setVisible(true);
                    new PaymentGUI((Customer)App.user,PackageManager.retrievePackage(id)).setVisible(true);
                });

                buttonPanel.add(detailsButton);
                rowData[11] = buttonPanel;

                tableModel.addRow(rowData);
            }
        }

        // Configure table
        packageTable.setRowHeight(60);
        packageTable.setModel(tableModel);
        packageTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumn actionColumn = packageTable.getColumnModel().getColumn(11);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        packageTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // back button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.setFont(font);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        backButton.addActionListener(e -> backReturn());

        add(mainPanel);
    }

    public void backReturn() {
        dispose();
        if(!App.isAdmin){
        new CustomerUI().setVisible(true);
    }
        else{new AdminGUI().setVisible(true);}}

    // Button renderer for the Actions column
    class ButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return (JPanel) value;
        }
    }

    // Button editor for the Actions column
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
            TravelSelectorGUI gui = new TravelSelectorGUI();
            gui.setVisible(true);
        });
    }
}