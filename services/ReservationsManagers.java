package services;

import Users.Customer;
import core.App;
import databases.CustomerDB;
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
 * Manages the creation, storage, and modification of hotel reservations.
 */
public class ReservationsManagers {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String FILE_PATH = "services/reservations.txt";
    private static final Map<Integer, Reservation> reservations = new HashMap<>();
    private static int nextId = 500000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Loads all reservations from storage file into memory.
     */
    public static void loadReservations() {
        reservations.clear();

        if (!Files.exists(Paths.get(FILE_PATH))) {
            System.out.println("No existing reservations file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            Set<Integer> processedIds = new HashSet<>();

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
     * Saves all reservations from memory to storage file.
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
     * Creates a new reservation and saves it to storage.
     * @param pck Package to reserve
     * @param user Customer making the reservation
     * @return New reservation
     */
    public static Reservation makeReservation(Package pck, Customer user) throws FileNotFoundException {
        loadReservations();

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
     * Retrieves a reservation by ID.
     * @param id Reservation ID
     * @return Reservation if found, null otherwise
     */
    public static Reservation getReservation(int id) {
        loadReservations();
        return reservations.get(id);
    }

    /**
     * Cancels a reservation and processes refund based on cancellation type.
     * @param id Reservation ID
     * @param keyword Cancellation type (immediate, far, inter, close)
     */
    public static void ReservationCancellation(int id, String keyword) throws FileNotFoundException {
        loadReservations();
        Reservation res = reservations.get(id);

        if (res == null || !res.isStatus()) {
            throw new IllegalArgumentException("Reservation " + id + " not found or already cancelled");
        }

        Package pck = res.getRelatedPackage();
        if (pck == null) {
            throw new IllegalStateException("Package not found for reservation " + id);
        }

        try {
            res.setStatus(false);
            saveReservations();

            Hotel hotel = pck.getHotel();
            for (LocalDate date = pck.getHotelStart(); !date.isAfter(pck.getDateEnd()); date = date.plusDays(1)) {
                hotel.cancelBook(date);
            }

            Flight flight = pck.getFlight();
            LocalDate flightDate = flight.isDayChange() ? pck.getDateStart().minusDays(1) : pck.getDateStart();
            flight.cancelBook(flightDate);

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

    /**
     * Determines cancellation type and initiates cancellation process.
     * @param res Reservation to cancel
     * @return Cancellation type applied
     */
    public static String cancellationInitiator(Reservation res) throws FileNotFoundException {
        if (res == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        Flight flight = res.getRelatedPackage().getFlight();
        if (flight == null) {
            throw new IllegalStateException("Flight not found in package");
        }

        LocalDate departureDate = flight.isDayChange() ?
                res.getDateStart().minusDays(1) : res.getDateStart();
        LocalDateTime departureDateTime = LocalDateTime.of(departureDate, flight.getDepartureTime());
        LocalDateTime now = LocalDateTime.now();

        long hoursBetween = ChronoUnit.HOURS.between(now, departureDateTime);

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

        ReservationCancellation(res.getId(), cancellationType);
        return cancellationType;
    }

    /**
     * Retrieves all reservations from storage.
     * @return Collection of all reservations
     */
    public static Collection<Reservation> getAllReservations() {
        loadReservations();
        return reservations.values();
    }

    /**
     * Generates a unique reservation ID.
     * @return New unique ID
     */
    private static int generateId() {
        return nextId++;
    }
}