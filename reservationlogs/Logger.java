package reservationlogs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

/**
 * Logger class for recording events such as user actions, package operations,
 * and reservations in the application.
 *
 * Logs are stored in the "logs/application_logs.txt" file.
 */
public class Logger {
    private static final String LOG_FILE_PATH = "logs/application_logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Static block to ensure the logs directory exists
    static {
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }
    }

    /**
     * Logs the creation of a package.
     *
     * @param packageID ID of the package created.
     * @param createdBy User who created the package.
     * @param details Additional details about the package.
     */
    public static void logPackageCreation(String packageID, String createdBy, String details) {
        String logMessage = "PACKAGE CREATION - Package ID: " + packageID +
                ", Created by: " + createdBy +
                ", Details: " + details;
        writeLog(logMessage);
    }

    /**
     * Logs the modification of a package.
     *
     * @param packageName Name of the package modified.
     * @param modifiedBy User who modified the package.
     * @param changes Details of the modifications made.
     */
    public static void logPackageModification(String packageName, String modifiedBy, String changes) {
        String logMessage = "PACKAGE MODIFICATION - Package: " + packageName +
                ", Modified by: " + modifiedBy +
                ", Changes: " + changes;
        writeLog(logMessage);
    }

    /**
     * Logs the registration of a new user.
     *
     * @param username Username of the registered user.
     * @param userType Type of the user (e.g., admin, customer).
     */
    public static void logUserRegistration(String username, String userType) {
        String logMessage = "USER REGISTRATION - Username: " + username +
                ", Type: " + userType;
        writeLog(logMessage);
    }

    /**
     * Logs a user login event.
     *
     * @param username Username of the user who logged in.
     */
    public static void logUserLogin(String username) {
        String logMessage = "USER LOGIN - Username: " + username;
        writeLog(logMessage);
    }

    /**
     * Logs a flight reservation.
     *
     * @param username Username of the user making the reservation.
     * @param flightDetails Details of the flight.
     * @param departure Departure location.
     * @param arrival Arrival location.
     * @param ticketClass Class of the ticket (e.g., economy, business).
     */
    public static void logFlightreservation(String username, String flightDetails,
                                            String departure, String arrival, String ticketClass) {
        String logMessage = "FLIGHT reservation - User: " + username +
                ", Flight: " + flightDetails +
                ", From: " + departure +
                ", To: " + arrival +
                ", Class: " + ticketClass;
        writeLog(logMessage);
    }

    /**
     * Logs a hotel reservation.
     *
     * @param username Username of the user making the reservation.
     * @param hotelName Name of the hotel.
     * @param city City where the hotel is located.
     * @param roomType Type of the room reserved.
     * @param checkIn Check-in date.
     * @param checkOut Check-out date.
     */
    public static void logHotelreservation(String username, String hotelName,
                                           String city, String roomType, String checkIn, String checkOut) {
        String logMessage = "HOTEL reservation - User: " + username +
                ", Hotel: " + hotelName +
                ", City: " + city +
                ", Room: " + roomType +
                ", Check-in: " + checkIn +
                ", Check-out: " + checkOut;
        writeLog(logMessage);
    }

    /**
     * Logs a taxi reservation.
     *
     * @param username Username of the user making the reservation.
     * @param city City where the taxi is reserved.
     * @param taxiType Type of taxi (e.g., sedan, SUV).
     * @param dateTime Date and time of the reservation.
     */
    public static void logTaxireservation(String username, String city,
                                          String taxiType, String dateTime) {
        String logMessage = "TAXI reservation - User: " + username +
                ", City: " + city +
                ", Type: " + taxiType +
                ", DateTime: " + dateTime;
        writeLog(logMessage);
    }

    /**
     * Logs the cancellation of a reservation.
     *
     * @param username Username of the user cancelling the reservation.
     * @param reservationType Type of the reservation (e.g., flight, hotel, taxi).
     * @param reservationId ID of the reservation being cancelled.
     * @param details Additional details about the cancellation.
     */
    public static void logCancellation(String username, String reservationType,
                                       String reservationId, String details) {
        String logMessage = "CANCELLATION - User: " + username +
                ", Type: " + reservationType +
                ", reservation ID: " + reservationId +
                ", Details: " + details;
        writeLog(logMessage);
    }

    /**
     * Logs a payment event.
     *
     * @param username Username of the user making the payment.
     * @param reservationType Type of reservation being paid for.
     * @param reservationId ID of the reservation.
     * @param amount Payment amount.
     */
    public static void logPayment(String username, String reservationType,
                                  String reservationId, double amount) {
        String logMessage = "PAYMENT - User: " + username +
                ", Type: " + reservationType +
                ", reservation ID: " + reservationId +
                ", Amount: " + amount;
        writeLog(logMessage);
    }

    /**
     * Writes a log message to the log file with a timestamp.
     *
     * @param message The message to log.
     */
    private static void writeLog(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            String logEntry = "[" + timestamp + "] " + message + "\n";
            writer.write(logEntry);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
