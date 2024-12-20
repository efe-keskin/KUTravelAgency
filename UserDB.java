import java.io.*;
import java.util.HashMap;

public class UserDB {

    private static HashMap<String, String> userDB = new HashMap<>();
    static {loadUsers();}



    // Add a user and save it to the file
    public static void addUser(String username, String password) {
        userDB.put(username, password);
        saveToFile();
    }

    // Load users from the file into the HashMap
    public static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.DB_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userDB.put(parts[0], parts[1]); // Add username and password to HashMap
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }

    // Save all users to the file
    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.DB_PATH))) {
            for (String username : userDB.keySet()) {
                writer.write(username + ":" + userDB.get(username));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving user database: " + e.getMessage());
        }
    }

    // Retrieve a user's password
    public static String getUser(String username) {
        return userDB.get(username);
    }
}
