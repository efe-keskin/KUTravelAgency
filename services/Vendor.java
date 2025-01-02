package services;

import Users.Customer;
import products.Flight;
import products.Hotel;
import products.Taxi;
import reservationlogs.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles package selling and transaction management for travel bookings.
 */
public class Vendor {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Books a complete travel package including hotel, flight, and taxi for an existing reservation.
     *
     * @param res The reservation containing package details
     * @param cst The customer making the booking
     * @throws IOException If there's an error writing transaction data
     */
    public static void packageSeller(Reservation res, Customer cst) throws IOException {
        Package pck = res.getRelatedPackage();
        Hotel hotel = pck.getHotel();
        for (LocalDate date = res.getRelatedPackage().getHotelStart(); !date.isAfter(res.getDateEnd()); date = date.plusDays(1)) {
            hotel.book(date);
        }

        Flight flight = pck.getFlight();
        flight.book(res.getRelatedPackage().getFlight().isDayChange() ? res.getDateStart().minusDays(1):res.getDateStart());

        Taxi taxi = pck.getTaxi();
        double distanceKm = hotel.getDistanceToAirport();
        int travelTimeMinutes = (int) Math.ceil((distanceKm / 60.0) * 60);
        LocalDateTime taxiArrivalTime = res.getRelatedPackage().getTaxiTime().plusMinutes(travelTimeMinutes);

        LocalDateTime bookingTime = res.getRelatedPackage().getTaxiTime();
        while (!bookingTime.isAfter(taxiArrivalTime)) {
            taxi.book(bookingTime);
            bookingTime = bookingTime.plusMinutes(2);
        }

        createTransaction(res, cst);
    }

    /**
     * Creates a new reservation and books a complete travel package.
     *
     * @param pck The travel package to book
     * @param cst The customer making the booking
     * @param taxiTime The requested taxi pickup time
     * @param hotelStartDate The first day of hotel stay
     * @param dateStart The start date of the trip
     * @param dateEnd The end date of the trip
     * @throws IOException If there's an error writing transaction data
     */
    public static void packageSeller(Package pck, Customer cst, LocalDateTime taxiTime,
                                     LocalDate hotelStartDate, LocalDate dateStart, LocalDate dateEnd) throws IOException {
        Hotel hotel = pck.getHotel();
        for (LocalDate date = hotelStartDate; !date.isAfter(dateEnd); date = date.plusDays(1)) {
            hotel.book(date);
        }

        Flight flight = pck.getFlight();
        flight.book(dateStart);

        Taxi taxi = pck.getTaxi();
        double distanceKm = hotel.getDistanceToAirport();
        int travelTimeMinutes = (int) Math.ceil((distanceKm / 60.0) * 60);
        LocalDateTime taxiArrivalTime = taxiTime.plusMinutes(travelTimeMinutes);

        LocalDateTime bookingTime = taxiTime;
        while (!bookingTime.isAfter(taxiArrivalTime)) {
            taxi.book(bookingTime);
            bookingTime = bookingTime.plusMinutes(2);
        }

        Reservation newRes = ReservationsManagers.makeReservation(pck, cst);
        createTransaction(newRes, cst);
        Logger.logPayment(cst.getUsername(),"Package",newRes.toString()+" Reservation ID "+newRes.getId(),newRes.getRelatedPackage().getDiscountedPrice());
    }

    /**
     * Creates a transaction record for a package purchase.
     *
     * @param res The reservation associated with the transaction
     * @param cst The customer making the purchase
     * @throws IOException If there's an error writing to the transactions file
     */
    public static void createTransaction(Reservation res, Customer cst) throws IOException {
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

    /**
     * Records a refund transaction for a reservation.
     *
     * @param resID The ID of the reservation being refunded
     * @param amount The amount to be refunded
     */
    static void moneyReturn(int resID, int amount) {
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