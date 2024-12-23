package services;

import Users.Customer;
import Users.User;
import databases.CustomerDB; // or wherever your user lookup code is
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReservationsManager handles creating, reading, updating, and saving
 * Reservation objects to a local file for persistence.
 */
public class ReservationsManagers {

    // Storage file path
    private static final String FILE_PATH = "services/reservations.txt";

    // A thread-safe way (if needed) to store all reservations by ID
    private static final Map<Integer, Reservation> reservations = new HashMap<>();

    // Next ID to assign. This will get updated after loading.
    private static int nextId = 500000;

    // Date format used in your Reservation#toString
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Loads reservation data from reservations.txt into the in-memory 'reservations' map.
     * Each line in the file is assumed to look like:
     *
     *   ID,PACKAGE_ID,STATUS,DATE_START,DATE_END,USER_ID
     *
     * where:
     *   - ID is an int
     *   - PACKAGE_ID is an int
     *   - STATUS is either "confirmed" or "cancelled"
     *   - DATE_START and DATE_END are YYYY-MM-DD or empty strings
     *   - USER_ID is a string that identifies the user (e.g., email, username, etc.)
     */
    public static void loadReservations() {
        reservations.clear(); // Clear the map before loading fresh

        if (!Files.exists(Paths.get(FILE_PATH))) {
            System.out.println("No existing reservations file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines (if any)
                if (line.trim().isEmpty()) continue;

                // Split CSV
                String[] parts = line.split(",");

                // Guard against malformed lines
                if (parts.length < 6) {
                    System.out.println("Skipping malformed reservation line: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                int packageId = Integer.parseInt(parts[1]);
                boolean status = parts[2].equalsIgnoreCase("confirmed");
                LocalDate dateStart = parts[3].isEmpty()
                        ? null
                        : LocalDate.parse(parts[3], DATE_FORMATTER);
                LocalDate dateEnd = parts[4].isEmpty()
                        ? null
                        : LocalDate.parse(parts[4], DATE_FORMATTER);
                String userId = parts[5];

                // Retrieve the actual Package and User objects
                Package pck = PackageManager.retrievePackage(packageId);
                // Depending on your design, maybe you have "CustomerDB.retrieveCustomer(userId)" or
                // "UserDB.retrieveUser(userId)". We'll assume 'CustomerDB' can also return a 'User'.
                User user = CustomerDB.retrieveCustomer(userId);

                // Build the Reservation
                Reservation res = new Reservation(id, pck, user);
                res.setStatus(status);

                // If you have setters for these dates in Reservation:
                //   res.setDateStart(dateStart);
                //   res.setDateEnd(dateEnd);

                reservations.put(id, res);

                // Keep track of the largest ID so we can set nextId properly
                if (id >= nextId) {
                    nextId = id + 1;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations file: " + e.getMessage());
        }
    }

    /**
     * Saves the in-memory reservations map to reservations.txt,
     * overwriting any existing contents.
     */
    public static void saveReservations() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            // For each reservation, we'll write out:
            //   ID,PACKAGE_ID,(confirmed|cancelled),DATE_START,DATE_END,USER_ID
            for (Reservation res : reservations.values()) {
                int id = res.getId();
                int packageId = (res.getRelatedPackage() != null)
                        ? res.getRelatedPackage().getId() : 0;
                String status = res.isStatus() ? "confirmed" : "cancelled";

                // If you're not using these fields, you can omit them or store them differently
                // We'll assume you eventually set them in Reservation.
                // If not set, we write an empty string.
                String dateStartStr = (res.getDateStart() == null)
                        ? ""
                        : res.getDateStart().format(DATE_FORMATTER);
                String dateEndStr = (res.getDateEnd() == null)
                        ? ""
                        : res.getDateEnd().format(DATE_FORMATTER);

                // If user is null, store an empty string or something
                String userId = (res.getCustomer() != null)
                        ? String.valueOf(res.getCustomer().getID()) // or getUsername(), etc.
                        : "";

                writer.write(String.join(",",
                        String.valueOf(id),
                        String.valueOf(packageId),
                        status,
                        dateStartStr,
                        dateEndStr,
                        userId
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving reservations file: " + e.getMessage());
        }
    }

    /**
     * Creates a new Reservation for the given Package and User, adds it to our in-memory map,
     * and immediately saves to file for persistence.
     *
     * @param pck  the Package being reserved
     * @param user the user (customer) making the reservation
     * @return the newly created Reservation object
     */
    public static Reservation makeReservation(Package pck, Customer user) {
        // If we've never loaded reservations, let's ensure we do so
        if (reservations.isEmpty()) {
            loadReservations(); // in case file data was never read
        }

        int id = generateId();
        Reservation newRes = new Reservation(id, pck, user);
        newRes.setStatus(true);
        newRes.setDateStart(pck.getDateStart());
        newRes.setDateEnd(pck.getDateEnd());
        reservations.put(id, newRes);
        user.addReservation(newRes);
        saveReservations(); // Immediately persist the changes
        return newRes;
    }

    /**
     * Fetch a reservation by ID (if it exists)
     */
    public static Reservation getReservation(int id) {
        return reservations.get(id);
    }

    /**
     * Cancel a reservation by ID (sets status to false/cancelled).
     * @return true if successfully cancelled, false if not found
     */
    public static boolean cancelReservation(int id) {
        Reservation res = reservations.get(id);
        if (res != null) {
            res.setStatus(false);
            saveReservations();
            return true;
        }
        return false;
    }

    /**
     * Retrieve all reservations currently loaded in memory.
     */
    public static Collection<Reservation> getAllReservations() {
        return reservations.values();
    }

    /**
     * A helper method to generate a unique integer ID.
     */
    private static int generateId() {
        // nextId is updated as we load from file, so it's always at least
        // one above the highest ID we've encountered.
        return nextId++;
    }
}
