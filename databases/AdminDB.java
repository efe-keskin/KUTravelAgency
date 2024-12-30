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

    // Load admins from the file into the HashMap
    public static void loadAdmin() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.ADMIN_DB_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                // Check that we have 2 parts and both parts are non-null/non-empty
                if (parts.length == 2 && parts[0] != null && !parts[0].isEmpty()
                        && parts[1] != null && !parts[1].isEmpty()) {
                    adminDB.put(parts[0], new Admin(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading admin database: " + e.getMessage());
        }
    }

    // Retrieve an admin's password
    public static String getAdminPass(String username) {
        // Check if username is null or empty
        if (username == null || username.isEmpty()) {
            return null;
        }
        // Retrieve admin from HashMap and check if it's null
        Admin admin = adminDB.get(username);
        if (admin == null) {
            return null;
        }
        // Return password if admin is not null
        return admin.getPassword();
    }

    // Check if an admin exists in the DB
    public static Boolean hasAdmin(String username) {
        // Check if username is null or empty
        if (username == null || username.isEmpty()) {
            return false;
        }
        return adminDB.containsKey(username);
    }

    // Retrieve the Admin object
    public static User getAdmin(String username) {
        // Check if username is null or empty
        if (username == null || username.isEmpty()) {
            return null;
        }
        return adminDB.get(username);
    }
}
