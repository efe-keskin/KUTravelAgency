package databases;


import Users.Customer;
import constants.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class CustomerDB {

    private static HashMap<String, Customer> customerDB = new HashMap<>();
    private static HashMap<String, Customer> customerIDDB = new HashMap<>();
    private static int uniqueID=600000;
    private static int size;
    static {
        loadCustomers();
    uniqueID += customerDB.size();

    }
public static Customer retrieveCustomer(String id){
        loadCustomers();
        if(customerIDDB.size()>0){
            loadCustomers();
            return customerIDDB.get(id);
                }
    else{
        loadCustomers();
        return customerIDDB.get(id);

        }
}




    // Add a user and save it to the file
    public static void addCustomer(String username, String password) {
        loadCustomers();
        Customer newCustomer = new Customer(username,password,uniqueID++);
        customerDB.put(username, newCustomer);
        saveToFile();
    }

    // Load users from the file into the HashMap
    public static void loadCustomers() {
        if (!customerDB.isEmpty()&&!customerIDDB.isEmpty()) {
            return; // Avoid reloading if already populated
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.DB_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    customerDB.put(parts[0], new Customer(parts[0], parts[1], Integer.valueOf(parts[2])));
                    customerIDDB.put(parts[2],new Customer(parts[0],parts[1],Integer.valueOf(parts[2])));
                }
            }
            size = customerDB.size();
        } catch (IOException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }


    // Save all users to the file
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

    // Retrieve a user's password
    public static String getCustomerPass(String username) {
        loadCustomers();
        if(customerDB.get(username) == null){return null;}
        return customerDB.get(username).getPassword();
    }
    public static Customer getCustomer(String username){
        loadCustomers();
        return customerDB.get(username);
    }
    public static Boolean hasCustomer(String username){
        loadCustomers();
        if(customerDB.containsKey(username)){
            return true;
        }
        return false;
    }
public static Collection<Customer> getAllCustomers(){
        loadCustomers();
        return customerDB.values();

}


    public static int getSize() {
        loadCustomers();
        return size;
    }
}
