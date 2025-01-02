package databases;

import Users.Admin;
import Users.User;
import constants.Constants;

import java.io.*;
import java.util.HashMap;

/**
 * Manages the admin database, including loading, retrieval, and validation.
 */
public class AdminDB {

    private static HashMap<String, Admin> adminDB = new HashMap<>();

    static {
        loadAdmin();
    }

    /**
     * Loads admin data from a file into the database.
     */
    public static void loadAdmin() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.ADMIN_DB_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0] != null && !parts[0].isEmpty()
                        && parts[1] != null && !parts[1].isEmpty()) {
                    adminDB.put(parts[0], new Admin(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading admin database: " + e.getMessage());
        }
    }

    /**
     * Retrieves the password for a given admin username.
     *
     * @param username the admin username
     * @return the admin password or null if not found
     */
    public static String getAdminPass(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        Admin admin = adminDB.get(username);
        return admin != null ? admin.getPassword() : null;
    }

    /**
     * Checks if an admin exists in the database.
     *
     * @param username the admin username
     * @return true if the admin exists, false otherwise
     */
    public static Boolean hasAdmin(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return adminDB.containsKey(username);
    }

    /**
     * Retrieves the Admin object for a given username.
     *
     * @param username the admin username
     * @return the Admin object or null if not found
     */
    public static User getAdmin(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        return adminDB.get(username);
    }
}