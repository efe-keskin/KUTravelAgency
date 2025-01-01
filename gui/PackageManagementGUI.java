package gui;

import services.Package;
import services.PackageManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class PackageManagementGUI extends JFrame {
    private JTable packageTable;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Font font = new Font("Arial", Font.PLAIN, 14);
    public PackageManagementGUI(){
        setTitle("Package Manager");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400,800);
        mainPanel = new JPanel(new BorderLayout());
        packageTable = new JTable();
        jScrollPane = new JScrollPane(packageTable);
        mainPanel.add(jScrollPane,BorderLayout.CENTER);
        packageTable.setFont(font);



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
        PackageManager.packageDictGenerator();
        for (int id : PackageManager.packageDict.keySet()) {
            if (!Objects.equals(PackageManager.retrievePackage(id).getType(), "not offered")) {
                Object[] rowData = new Object[12];
                rowData[0] = String.valueOf(id);
                rowData[1] = PackageManager.retrievePackage(id).getFlight().getDepartureCity();
                rowData[2] = PackageManager.retrievePackage(id).getFlight().getArrivalCity();
                rowData[3] = PackageManager.retrievePackage(id).getFlight().getAirline();
                rowData[4] = PackageManager.retrievePackage(id).getFlight().getTicketClass();
                rowData[5] = PackageManager.retrievePackage(id).getHotel().getName();
                rowData[6] = PackageManager.retrievePackage(id).getTaxi().getTaxiType();
                rowData[7] = String.valueOf(PackageManager.retrievePackage(id).getTotalCost());
                if (Objects.equals(PackageManager.retrievePackage(id).getType(), "offered")) {
                    rowData[8] = String.valueOf(PackageManager.retrievePackage(id).getDiscountedPrice());
                    System.out.println(PackageManager.retrievePackage(id).getDiscountedPrice());
                }
                else{
                    rowData[8] = "no discount";
                }
                rowData[9] = PackageManager.retrievePackage(id).getDateStart().format(DATE_FORMATTER);
                rowData[10] = PackageManager.retrievePackage(id).getDateEnd().format(DATE_FORMATTER);


                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                JButton editButton = new JButton("Edit");
                editButton.setFont(font);
                JButton deleteButton = new JButton("Delete");
                deleteButton.setFont(font);
                editButton.addActionListener(e -> {
                    new PackageEditorGUI(PackageManager.retrievePackage(id)).setVisible(true);
                    System.out.println("Edit clicked for ID: " + id);
                });

                deleteButton.addActionListener(e -> {
                    PackageManager.deletePackage(id);
                    System.out.println("Delete clicked for ID: " + id);
                });

                buttonPanel.add(editButton);
                buttonPanel.add(deleteButton);
                rowData[11] = buttonPanel;

                tableModel.addRow(rowData);



                packageTable.setRowHeight(60);
                packageTable.setModel(tableModel);
                packageTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                TableColumn actionColumn = packageTable.getColumnModel().getColumn(11);
                actionColumn.setCellRenderer(new ButtonRenderer());
                actionColumn.setCellEditor(new ButtonEditor());

                TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
                packageTable.setRowSorter(sorter);
                List<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
                sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                sorter.setSortKeys(sortKeys);

                add(mainPanel);
            }
        }
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        refreshButton.setFont(font);
        backButton.setFont(font);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        refreshButton.addActionListener(e -> {
            refreshPackageList();
        });
        backButton.addActionListener(e -> {
            backReturn();
        });


    }
    public void refreshPackageList(){
        dispose();
        new PackageManagementGUI().setVisible(true);
    }

    public void backReturn(){
        dispose();
        new AdminGUI().setVisible(true);
    }
    class ButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return (JPanel) value;
        }
    }

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
            PackageManagementGUI gui = new PackageManagementGUI();
            gui.setVisible(true);
        });
    }
}