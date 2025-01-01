package gui;


import Users.Customer;
import core.App;
import databases.CustomerDB;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.EventObject;

public class CustomerSearchGUI extends JFrame {
    private JTable customerTable;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private static final Font font = new Font("Arial", Font.PLAIN, 14);

    public CustomerSearchGUI() {
        setTitle("Customer Selection");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 600);

        mainPanel = new JPanel(new BorderLayout());

        // Top panel for search and buttons
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
        customerTable = new JTable();
        scrollPane = new JScrollPane(customerTable);
        customerTable.setFont(font);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? JPanel.class : String.class;
            }
        };

        tableModel.addColumn("User ID");
        tableModel.addColumn("Username");
        tableModel.addColumn("Password");
        tableModel.addColumn("Actions");

        populateTable("");

        customerTable.setRowHeight(40);
        customerTable.setModel(tableModel);
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Button column
        TableColumn actionColumn = customerTable.getColumnModel().getColumn(3);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());

        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add New Customer Button
        JButton addCustomerButton = new JButton("Add New Customer");
        addCustomerButton.setFont(font);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(addCustomerButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        refreshButton.addActionListener(e -> refreshCustomerList());
        backButton.addActionListener(e -> dispose());
        searchButton.addActionListener(e -> searchCustomers());
        addCustomerButton.addActionListener(e -> addNewCustomer());

        add(mainPanel);
    }

    private void populateTable(String searchUsername) {
        tableModel.setRowCount(0); // Clear existing rows
        CustomerDB.loadCustomers();

        for (Customer cst : CustomerDB.getAllCustomers()) {
            String username = cst.getUsername();
            String password = cst.getPassword();
            Integer id = cst.getID();


            if (searchUsername.isEmpty() || username.toLowerCase().contains(searchUsername.toLowerCase())) {
                Object[] rowData = new Object[4];
                rowData[0] = String.valueOf(id);
                rowData[1] = username;
                rowData[2] = password;

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JButton selectButton = new JButton("Select");
                selectButton.setFont(font);

                final Customer customer = CustomerDB.getCustomer(username);
                selectButton.addActionListener(e -> {
                    dispose();
                    new CustomerDetailsGUI(customer).setVisible(true);

                });

                buttonPanel.add(selectButton);
                rowData[3] = buttonPanel;

                tableModel.addRow(rowData);
            }
        }
    }

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

    private void searchCustomers() {
        String searchTerm = searchField.getText().trim();
        populateTable(searchTerm);
    }

    private void refreshCustomerList() {
        searchField.setText("");
        populateTable("");
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
            CustomerChooserGUI gui = new CustomerChooserGUI();
            gui.setVisible(true);
        });
    }
}
