package databases;


import Users.Customer;
import constants.Constants;

import java.io.*;
import java.util.HashMap;


public class CustomerDB {

    private static HashMap<String, Customer> customerDB = new HashMap<>();
    private static int uniqueID;
    static {
        loadCustomers();
    uniqueID = customerDB.size();

    }



    // Add a user and save it to the file
    public static void addCustomer(String username, String password) {
        Customer newCustomer = new Customer(username,password,uniqueID++);
        customerDB.put(username, newCustomer);
        saveToFile();
    }

    // Load users from the file into the HashMap
    public static void loadCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.DB_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    customerDB.put(parts[0], new Customer(parts[0], parts[1],Integer.valueOf(parts[2]))); // Add username and Users.Customer to HashMap
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }

    // Save all users to the file
    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.DB_PATH))) {
            for (String username : customerDB.keySet()) {
                writer.write(username + ":" + customerDB.get(username).getPassword() + ":" + customerDB.get(username).getId());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving customer database: " + e.getMessage());
        }
    }

    // Retrieve a user's password
    public static String getCustomer(String username) {
        if(customerDB.get(username) == null){return null;}
        return customerDB.get(username).getPassword();
    }
    public static Boolean hasCustomer(String username){
        if(customerDB.containsKey(username)){
            return true;
        }
        return false;
    }
}
