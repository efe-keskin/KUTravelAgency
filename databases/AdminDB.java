package databases;
import Users.Admin;
import Users.User;
import constants.Constants;

import java.io.*;
import java.util.HashMap;

public class AdminDB {
    private static HashMap<String, Admin> adminDB = new HashMap<>();
    static {
        loadAdmin();

    }
    // Load users from the file into the HashMap
    public static void loadAdmin() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.ADMIN_DB_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    adminDB.put(parts[0], new Admin(parts[0], parts[1])); // Add username and Users.Customer to HashMap
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading admin database: " + e.getMessage());
        }
    }


    // Retrieve a user's password
    public static String getAdminPass(String username) {
        return adminDB.get(username).getPassword();
    }
    public static Boolean hasAdmin(String username){
        if(adminDB.containsKey(username)){
            return true;
        }
        return false;
    }

    public static User getAdmin(String username) {
        return adminDB.get(username);
    }
}
