package databases;

import Users.Customer;
import constants.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Manages the customer database, including loading, retrieval, and modification of customer data.
 */
public class CustomerDB {

    private static HashMap<String, Customer> customerDB = new HashMap<>();
    private static HashMap<String, Customer> customerIDDB = new HashMap<>();
    private static int uniqueID = 600000;
    private static int size;

    static {
        loadCustomers();
        uniqueID += customerDB.size();
    }

    /**
     * Retrieves a customer by their unique ID.
     *
     * @param id the unique ID of the customer
     * @return the Customer object or null if not found
     */
    public static Customer retrieveCustomer(String id) {
        loadCustomers();
        return customerIDDB.get(id);
    }

    /**
     * Adds a new customer to the database and saves it to the file.
     *
     * @param username the username of the customer
     * @param password the password of the customer
     */
    public static void addCustomer(String username, String password) {
        loadCustomers();
        Customer newCustomer = new Customer(username, password, uniqueID++);
        customerDB.put(username, newCustomer);
        saveToFile();
    }

    /**
     * Loads customer data from the file into the database.
     */
    public static void loadCustomers() {
        if (!customerDB.isEmpty() && !customerIDDB.isEmpty()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.DB_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    customerDB.put(parts[0], new Customer(parts[0], parts[1], Integer.valueOf(parts[2])));
                    customerIDDB.put(parts[2], new Customer(parts[0], parts[1], Integer.valueOf(parts[2])));
                }
            }
            size = customerDB.size();
        } catch (IOException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }

    /**
     * Saves all customer data to the file.
     */
    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.DB_PATH))) {
            for (String username : customerDB.keySet()) {
                writer.write(username + ":" + customerDB.get(username).getPassword() + ":" + customerDB.get(username).getID());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving customer database: " + e.getMessage());
        }
    }

    /**
     * Retrieves the password for a given customer username.
     *
     * @param username the username of the customer
     * @return the password of the customer or null if not found
     */
    public static String getCustomerPass(String username) {
        loadCustomers();
        Customer customer = customerDB.get(username);
        return customer != null ? customer.getPassword() : null;
    }

    /**
     * Retrieves the Customer object for a given username.
     *
     * @param username the username of the customer
     * @return the Customer object or null if not found
     */
    public static Customer getCustomer(String username) {
        loadCustomers();
        return customerDB.get(username);
    }

    /**
     * Checks if a customer exists in the database.
     *
     * @param username the username of the customer
     * @return true if the customer exists, false otherwise
     */
    public static Boolean hasCustomer(String username) {
        loadCustomers();
        return customerDB.containsKey(username);
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return a collection of all Customer objects
     */
    public static Collection<Customer> getAllCustomers() {
        loadCustomers();
        return customerDB.values();
    }

    /**
     * Retrieves the total number of customers in the database.
     *
     * @return the number of customers
     */
    public static int getSize() {
        loadCustomers();
        return size;
    }
}
