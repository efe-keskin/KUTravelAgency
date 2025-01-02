package gui;

import Users.Customer;
import core.App;
import databases.CustomerDB;
import services.Reservation;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * GUI for searching, selecting, and managing customers. Allows sorting by total spending
 * and provides options to view customer details or add new customers.
 */
public class CustomerSearchGUI extends JFrame {

    private JTable customerTable;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private TableRowSorter<TableModel> sorter;
    private static final Font font = new Font("Arial", Font.PLAIN, 14);

    /**
     * Constructs the CustomerSearchGUI and initializes its components.
     */
    public CustomerSearchGUI() {
        setTitle("Customer Selection");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 600);

        mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchField.setFont(font);
        searchButton.setFont(font);
        searchPanel.add(new JLabel("Search by Username: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        refreshButton.setFont(font);
        backButton.setFont(font);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;
                    case 3: return Integer.class;
                    case 4: return JPanel.class;
                    default: return String.class;
                }
            }
        };

        tableModel.addColumn("User ID");
        tableModel.addColumn("Username");
        tableModel.addColumn("Password");
        tableModel.addColumn("Total Spending($)");
        tableModel.addColumn("Actions");

        customerTable = new JTable(tableModel);
        scrollPane = new JScrollPane(customerTable);
        customerTable.setFont(font);

        sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);
        populateTable("");

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);

        customerTable.setRowHeight(40);
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumn actionColumn = customerTable.getColumnModel().getColumn(4);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addCustomerButton = new JButton("Add New Customer");
        addCustomerButton.setFont(font);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(addCustomerButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshCustomerList());
        backButton.addActionListener(e -> dispose());
        searchButton.addActionListener(e -> searchCustomers());
        addCustomerButton.addActionListener(e -> addNewCustomer());

        add(mainPanel);
    }

    /**
     * Populates the table with customer data, optionally filtering by username.
     *
     * @param searchUsername the username to filter by, or empty for all customers
     */
    private void populateTable(String searchUsername) {
        tableModel.setRowCount(0);
        CustomerDB.loadCustomers();

        for (Customer cst : CustomerDB.getAllCustomers()) {
            String username = cst.getUsername();
            String password = cst.getPassword();
            Integer id = cst.getID();
            Integer moneySpent = 0;

            for (Reservation reservation : cst.getTravelHistory()) {
                if (reservation.isStatus()) {
                    moneySpent += reservation.getRelatedPackage().getDiscountedPrice();
                }
            }

            if (searchUsername.isEmpty() || username.toLowerCase().contains(searchUsername.toLowerCase())) {
                Object[] rowData = new Object[5];
                rowData[0] = id;
                rowData[1] = username;
                rowData[2] = password;
                rowData[3] = moneySpent;

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JButton selectButton = new JButton("Select");
                selectButton.setFont(font);

                final Customer customer = CustomerDB.getCustomer(username);
                selectButton.addActionListener(e -> {
                    dispose();
                    new CustomerDetailsGUI(customer).setVisible(true);
                });

                buttonPanel.add(selectButton);
                rowData[4] = buttonPanel;

                tableModel.addRow(rowData);
            }
        }
    }

    /**
     * Adds a new customer to the database after user input.
     */
    private void addNewCustomer() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                CustomerDB.addCustomer(username, password);
                refreshCustomerList();
            } else {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Searches for customers based on the entered username.
     */
    private void searchCustomers() {
        String searchTerm = searchField.getText().trim();
        populateTable(searchTerm);
    }

    /**
     * Refreshes the customer list by clearing search filters.
     */
    private void refreshCustomerList() {
        searchField.setText("");
        populateTable("");
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

    /**
     * Main method for testing the CustomerSearchGUI.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerSearchGUI gui = new CustomerSearchGUI();
            gui.setVisible(true);
        });
    }
}
