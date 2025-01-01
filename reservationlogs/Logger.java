package reservationlogs;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class Logger {
    private static final String LOG_FILE_PATH = "logs/application_logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }
    }

    public static void logPackageCreation(String packageID, String createdBy, String details) {
        String logMessage = "PACKAGE CREATION - Package ID: " + packageID +
                ", Created by: " + createdBy +
                ", Details: " + details;
        writeLog(logMessage);
    }

    public static void logPackageModification(String packageName, String modifiedBy, String changes) {
        String logMessage = "PACKAGE MODIFICATION - Package: " + packageName +
                ", Modified by: " + modifiedBy +
                ", Changes: " + changes;
        writeLog(logMessage);
    }

    public static void logUserRegistration(String username, String userType) {
        String logMessage = "USER REGISTRATION - Username: " + username +
                ", Type: " + userType;
        writeLog(logMessage);
    }

    public static void logUserLogin(String username) {
        String logMessage = "USER LOGIN - Username: " + username;
        writeLog(logMessage);
    }

    public static void logFlightreservation(String username, String flightDetails,
                                        String departure, String arrival, String ticketClass) {
        String logMessage = "FLIGHT reservation - User: " + username +
                ", Flight: " + flightDetails +
                ", From: " + departure +
                ", To: " + arrival +
                ", Class: " + ticketClass;
        writeLog(logMessage);
    }

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

    public static void logTaxireservation(String username, String city,
                                      String taxiType, String dateTime) {
        String logMessage = "TAXI reservation - User: " + username +
                ", City: " + city +
                ", Type: " + taxiType +
                ", DateTime: " + dateTime;
        writeLog(logMessage);
    }

    public static void logCancellation(String username, String reservationType,
                                       String reservationId, String details) {
        String logMessage = "CANCELLATION - User: " + username +
                ", Type: " + reservationType +
                ", reservation ID: " + reservationId +
                ", Details: " + details;
        writeLog(logMessage);
    }

    public static void logPayment(String username, String reservationType,
                                  String reservationId, double amount) {
        String logMessage = "PAYMENT - User: " + username +
                ", Type: " + reservationType +
                ", reservation ID: " + reservationId +
                ", Amount: " + amount;
        writeLog(logMessage);
    }

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