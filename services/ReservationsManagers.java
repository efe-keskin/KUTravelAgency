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
import reservationlogs.Logger;

/**
 * ReservationsManager handles creating, reading, updating, and saving
 * Reservation objects to a local file for persistence.
 */
public class ReservationsManagers {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
        // Clear the map before loading fresh
        reservations.clear();

        if (!Files.exists(Paths.get(FILE_PATH))) {
            System.out.println("No existing reservations file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            Set<Integer> processedIds = new HashSet<>();  // Track processed IDs

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 6) {
                    System.out.println("Skipping malformed reservation line: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0]);

                // Skip if we've already processed this ID
                if (processedIds.contains(id)) {
                    System.out.println("Skipping duplicate reservation ID: " + id);
                    continue;
                }

                processedIds.add(id);

                int packageId = Integer.parseInt(parts[1]);
                boolean status = parts[2].equalsIgnoreCase("confirmed");
                String userId = parts[5];

                Package pck = PackageManager.retrievePackage(packageId);
                Customer user = CustomerDB.retrieveCustomer(userId);

                Reservation res = new Reservation(id, pck, user);
                res.setStatus(status);

                reservations.put(id, res);

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
        loadReservations(); // Get fresh state

        int id = generateId();
        Reservation newRes = new Reservation(id, pck, user);
        newRes.setStatus(true);


        reservations.put(id, newRes);
        saveReservations();
        Logger.logHotelreservation(App.user.getUsername(),pck.getHotel().getName(),pck.getHotel().getCity(),pck.getHotel().getRoomType(),pck.getHotelStart().format(formatter),pck.getDateEnd().format(formatter));
        Logger.logTaxireservation(App.user.getUsername(), pck.getTaxi().getCity(),pck.getTaxi().getTaxiType(),pck.getTaxiTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
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



    public static void ReservationCancellation(int id, String keyword) throws FileNotFoundException {
        loadReservations(); // Reload to get fresh state
        Reservation res = reservations.get(id);

        if (res == null || !res.isStatus()) {
            throw new IllegalArgumentException("Reservation " + id + " not found or already cancelled");
        }

        Package pck = res.getRelatedPackage();
        if (pck == null) {
            throw new IllegalStateException("Package not found for reservation " + id);
        }

        try {
            // First mark as cancelled and save to ensure state is updated
            res.setStatus(false);
            saveReservations();

            // Now handle all the cancellations
            // Cancel hotel bookings
            Hotel hotel = pck.getHotel();
            for (LocalDate date = pck.getHotelStart(); !date.isAfter(pck.getDateEnd()); date = date.plusDays(1)) {
                hotel.cancelBook(date);
            }

            // Cancel flight booking
            Flight flight = pck.getFlight();
            LocalDate flightDate = flight.isDayChange() ? pck.getDateStart().minusDays(1) : pck.getDateStart();
            flight.cancelBook(flightDate);

            // Cancel taxi bookings
            Taxi taxi = pck.getTaxi();
            double distanceKm = hotel.getDistanceToAirport();
            int travelTimeMinutes = (int) Math.ceil((distanceKm / 60.0) * 60);

            LocalDateTime taxiPickupTime = pck.getTaxiTime();
            LocalDateTime taxiEndTime = taxiPickupTime.plusMinutes(travelTimeMinutes);

            LocalDateTime currentTime = taxiPickupTime;
            while (!currentTime.isAfter(taxiEndTime)) {
                taxi.cancelBook(currentTime);
                currentTime = currentTime.plusMinutes(2);
            }

            // Process refund
            int refundAmount;
            switch (keyword.toLowerCase()) {
                case "immediate":
                case "far":
                    refundAmount = pck.getDiscountedPrice();
                    break;
                case "inter":
                    refundAmount = (int) (pck.getDiscountedPrice() * 0.85);
                    break;
                case "close":
                    refundAmount = (int) (pck.getDiscountedPrice() * 0.70);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid cancellation type: " + keyword);
            }

            // Update customer history and process refund
            Customer customer = res.getCustomer();
            customer.loadTravelHistory();
            Vendor.moneyReturn(res.getId(), refundAmount);
            saveReservations();
            Logger.logCancellation(App.user.getUsername(),"Package",String.valueOf(res.getId()),res.toString());

        } catch (Exception e) {
            System.err.println("Error during cancellation of reservation " + id + ": " + e.getMessage());
            throw e;
        }
    }

    public static String cancellationInitiator(Reservation res) throws FileNotFoundException {
        if (res == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        Flight flight = res.getRelatedPackage().getFlight();
        if (flight == null) {
            throw new IllegalStateException("Flight not found in package");
        }

        // Calculate departure date/time
        LocalDate departureDate = flight.isDayChange() ?
                res.getDateStart().minusDays(1) : res.getDateStart();
        LocalDateTime departureDateTime = LocalDateTime.of(departureDate, flight.getDepartureTime());
        LocalDateTime now = LocalDateTime.now();

        // Calculate hours between now and departure
        long hoursBetween = ChronoUnit.HOURS.between(now, departureDateTime);

        // Determine cancellation type
        String cancellationType;
        if (App.isAdmin) {
            cancellationType = "immediate";
        } else if (hoursBetween > 72) {
            cancellationType = "far";
        } else if (hoursBetween >= 48) {
            cancellationType = "inter";
        } else {
            cancellationType = "close";
        }

        // Process cancellation
        ReservationCancellation(res.getId(), cancellationType);
        return cancellationType;
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
