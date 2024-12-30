package services;

import Users.Customer;
import core.App;
import databases.CustomerDB; // or wherever your user lookup code is
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Format;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import products.*;

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

    // Date format used in Reservation#toString
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Loads reservation data from reservations.txt into the in-memory 'reservations' map.
     * Each line in the file is assumed to look like:
     * <pre>
     *  ID,PACKAGE_ID,STATUS,DATE_START,DATE_END,USER_ID
     * </pre>
     * where:
     * <ul>
     *   <li>ID is an int</li>
     *   <li>PACKAGE_ID is an int</li>
     *   <li>STATUS is either "confirmed" or "cancelled"</li>
     *   <li>DATE_START and DATE_END are YYYY-MM-DD or empty strings</li>
     *   <li>USER_ID is a string that identifies the user (e.g., email, username, etc.)</li>
     * </ul>
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
                if (line.trim().isEmpty()) {
                    continue;
                }

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
                String userId = parts[5];

                // Retrieve actual Package and User objects
                Package pck = PackageManager.retrievePackage(packageId);
                Customer user = CustomerDB.retrieveCustomer(userId);

                // Build the Reservation
                Reservation res = new Reservation(id, pck, user);
                res.setStatus(status);


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
            for (Reservation res : reservations.values()) {
                int id = res.getId();
                int packageId = (res.getRelatedPackage() != null)
                        ? res.getRelatedPackage().getId()
                        : 0;
                String status = res.isStatus() ? "confirmed" : "cancelled";

                String dateStartStr = (res.getDateStart() == null)
                        ? ""
                        : res.getDateStart().format(DATE_FORMATTER);

                String dateEndStr = (res.getDateEnd() == null)
                        ? ""
                        : res.getDateEnd().format(DATE_FORMATTER);

                String userId = (res.getCustomer() != null)
                        ? String.valueOf(res.getCustomer().getID())
                        : "customer is null";

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
     * @throws FileNotFoundException if the file is not found when loading
     */
    public static Reservation makeReservation(Package pck, Customer user) throws FileNotFoundException {
        CustomerDB.loadCustomers(); // Ensure customers are loaded
        loadReservations();

        int id = generateId();
        Reservation newRes = new Reservation(id, pck, user);
        newRes.setStatus(true);
        reservations.put(id, newRes);

        saveReservations();
        user.loadTravelHistory();
        return newRes;
    }


    /**
     * Fetch a reservation by ID (if it exists).
     *
     * @param id the reservation ID
     * @return the Reservation object or null if not found
     */
    public static Reservation getReservation(int id) {
        loadReservations();
        return reservations.get(id);
    }


    public static void ReservationCancellation (int id, String keyword) throws FileNotFoundException {
        loadReservations();
        Reservation res = reservations.get(id);
        if (res != null) {
            Package pck = res.getRelatedPackage();
            Hotel hotel = pck.getHotel();
            for (LocalDate date = pck.getHotelStart(); !date.isAfter(pck.getDateEnd()); date = date.plusDays(1)) {
                hotel.cancelBook(date);
            }

            Flight flight = pck.getFlight();
            flight.cancelBook(!flight.isDayChange()?pck.getDateStart():pck.getDateStart().minusDays(1));

            Taxi taxi = pck.getTaxi();

            double distanceKm = hotel.getDistanceToAirport();
            int travelTimeMinutes = (int) Math.ceil((distanceKm / 60.0) * 60);
            LocalDateTime taxiArrivalTime = pck.getTaxiTime().plusMinutes(travelTimeMinutes);

            LocalDateTime bookingTime = pck.getTaxiTime();
            while (!bookingTime.isAfter(taxiArrivalTime)) {
                taxi.cancelBook(bookingTime);
                bookingTime = bookingTime.plusMinutes(2);
            }
            res.setStatus(false);
            saveReservations();
            Customer customer = reservations.get(id).getCustomer();
            customer.loadTravelHistory();
            int amount = pck.getDiscountedPrice();
            if(keyword.equals("immediate")) {
               amount = pck.getDiscountedPrice();
            }
            else if(keyword.equals("far")){
                amount = pck.getDiscountedPrice();
            }
            else if (keyword.equals("inter")) {
                amount = (int) (pck.getDiscountedPrice()*0.85);
            }
            else if (keyword.equals("close")){
                amount = (int) (pck.getDiscountedPrice()*0.70);
            }


            Vendor.moneyReturn(res.getId(), amount);




        }
        else{System.out.println("Reservation couldn't be found");}


    }




    public static String cancellationInitiator(Reservation res) throws FileNotFoundException {
        LocalTime departureTime =  res.getRelatedPackage().getFlight().getDepartureTime();
        LocalDate departureDate = !res.getRelatedPackage().getFlight().isDayChange() ? res.getDateStart():res.getDateStart().minusDays(1);
        LocalDateTime departureDateTime = LocalDateTime.of(departureDate,departureTime);
        LocalDateTime now = LocalDateTime.now();
        long hoursBetween = ChronoUnit.HOURS.between(now,departureDateTime);
        System.out.println("Departure DateTime: " + departureDateTime);
        System.out.println("Current DateTime: " + now);
        System.out.println(hoursBetween);
        if(App.isAdmin){
            ReservationCancellation(res.getId(),"immediate");
            return "immediate";
        }
        else if (
                hoursBetween>72 //more than 72h before departure

        ) {
            ReservationCancellation(res.getId(),"far");
            return "far";

        } else if (
                hoursBetween>=48//48-72h
        ) {
            ReservationCancellation(res.getId(),"inter");
            return "inter";
        } else {
            ReservationCancellation(res.getId(),"close");
            return "close";
        }


    }
    public static Collection<Reservation> getAllReservations() {
        loadReservations();
        return reservations.values();
    }

    /**
     * A helper method to generate a unique integer ID.
     */
    private static int generateId() {
        // nextId is updated as we load from file,
        // so it's always at least one above the highest ID encountered.
        return nextId++;
    }
}
