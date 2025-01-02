package Users;

import services.Reservation;
import services.ReservationsManagers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Represents a Customer user with a username, password, and travel history.
 */
public class Customer implements User {
    private final String username;
    private final String password;
    private final Integer id;
    private ArrayList<Reservation> travelHistory;

    /**
     * Constructs a Customer object with the specified username, password, and ID.
     *
     * @param username The username of the customer.
     * @param password The password of the customer.
     * @param id The unique ID of the customer.
     */
    public Customer(String username, String password, Integer id) {
        travelHistory = new ArrayList<>();
        this.username = username;
        this.password = password;
        this.id = id;
    }

    /**
     * Loads the customer's travel history from the reservations file.
     *
     * @throws FileNotFoundException If the reservations file is not found.
     */
    public void loadTravelHistory() throws FileNotFoundException {
        travelHistory = new ArrayList<>();
        File file = new File("services/reservations.txt");
        Scanner scn = new Scanner(file);
        ReservationsManagers.loadReservations();
        while (scn.hasNextLine()) {
            String line = scn.nextLine();
            String[] lineArray = line.split(",");
            if (Objects.equals(lineArray[5], String.valueOf(this.getID()))) {
                travelHistory.add(ReservationsManagers.getReservation(Integer.parseInt(lineArray[0])));
            }
        }
    }

    /**
     * Gets the customer's travel history.
     *
     * @return A list of reservations associated with the customer.
     */
    public ArrayList<Reservation> getTravelHistory() {
        try {
            loadTravelHistory();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return travelHistory;
    }

    /**
     * Gets the username of the customer.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the customer.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the unique ID of the customer.
     *
     * @return The customer ID.
     */
    @Override
    public int getID() {
        return id;
    }
}
