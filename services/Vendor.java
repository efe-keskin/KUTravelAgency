package services;

import Users.Customer;
import products.Flight;
import products.Hotel;
import products.Taxi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vendor {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void packageSeller(Package pck, Customer cst, LocalDateTime taxiTime,
                                     LocalDate hotelStartDate, LocalDate dateStart, LocalDate dateEnd) throws IOException {
        // Book hotel
        Hotel hotel = pck.getHotel();
        for (LocalDate date = hotelStartDate; !date.isAfter(dateEnd); date = date.plusDays(1)) {
            hotel.book(date);
        }

        // Book flight
        Flight flight = pck.getFlight();
        flight.book(dateStart);

        // Book taxi
        Taxi taxi = pck.getTaxi();
        // Calculate travel duration based on hotel distance
        double distanceKm = hotel.getDistanceToAirport();
        int travelTimeMinutes = (int) Math.ceil((distanceKm / 60.0) * 60);
        LocalDateTime taxiArrivalTime = taxiTime.plusMinutes(travelTimeMinutes);

        // Book taxi for each 2-minute interval of the journey
        LocalDateTime bookingTime = taxiTime;
        while (!bookingTime.isAfter(taxiArrivalTime)) {
            taxi.book(bookingTime);
            bookingTime = bookingTime.plusMinutes(2);
        }

        // Create reservation and process payment
        Reservation newRes = ReservationsManagers.makeReservation(pck, cst);
        createTransaction(newRes, cst);
    }

    private static void createTransaction(Reservation res, Customer cst) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("services/transactions.txt", true))) {
            LocalDate now = LocalDate.now();
            String date = now.format(DATE_FORMATTER);

            writer.write(String.format("%s,%s,%s,%s,%s\n",
                    date,
                    res.getRelatedPackage().getDiscountedPrice(),
                    res.getId(),
                    cst.getID(),
                    "Purchase"

            ));
        }
    }
    static void moneyReturn(int resID, int amount){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("services/transactions.txt", true))) {
            LocalDate now = LocalDate.now();
            String date = now.format(DATE_FORMATTER);
            Reservation res = ReservationsManagers.getReservation(resID);
            Customer cst = res.getCustomer();

            writer.write(String.format("%s,%s,%s,%s,%s\n",
                    date,
                    amount,
                    res.getId(),
                    cst.getID(),
                    "Refund"

            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}