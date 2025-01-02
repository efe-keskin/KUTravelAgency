package services;

import Users.Admin;
import Users.Customer;
import Users.User;
import core.App;
import products.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a reservation made by a customer for a travel package.
 * Each reservation is associated with a specific package and has a status indicating whether it is confirmed or cancelled.
 */
public class Reservation {
    private int id;
    private Package relatedPackage;
    private boolean status = true; // true: confirmed, false: cancelled
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private Customer customer;

    /**
     * Constructs a new Reservation object.
     *
     * @param id The unique identifier for the reservation.
     * @param pck The package associated with the reservation.
     * @param customer The customer who made the reservation.
     */
    public Reservation(int id, Package pck, Customer customer) {
        this.id = id;
        this.relatedPackage = pck;
        this.customer = customer;

        // Dates are calculated based on the package details
        this.dateEnd = pck.getDateEnd();
        this.dateStart = pck.getDateStart();
    }

    /**
     * Returns a string representation of the reservation.
     *
     * @return A string containing reservation details such as ID, package info, status, dates, and customer username.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return String.join(",",
                String.valueOf(id),
                relatedPackage != null ? relatedPackage.toString() : "",
                status ? "confirmed" : "cancelled",
                dateStart != null ? dateStart.format(formatter) : "",
                dateEnd != null ? dateEnd.format(formatter) : "",
                customer != null ? customer.getUsername() : ""
        );
    }

    /**
     * Sets the status of the reservation.
     *
     * @param status true if confirmed, false if cancelled.
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Gets the package associated with the reservation.
     *
     * @return The related package.
     */
    public Package getRelatedPackage() {
        return relatedPackage;
    }

    /**
     * Gets the customer who made the reservation.
     *
     * @return The customer object.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Gets the end date of the reservation.
     *
     * @return The end date.
     */
    public LocalDate getDateEnd() {
        return dateEnd;
    }

    /**
     * Gets the start date of the reservation.
     *
     * @return The start date.
     */
    public LocalDate getDateStart() {
        return dateStart;
    }

    /**
     * Gets the unique ID of the reservation.
     *
     * @return The reservation ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the current status of the reservation.
     *
     * @return true if confirmed, false if cancelled.
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets the start date of the reservation.
     *
     * @param dateStart The start date to set.
     */
    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * Sets the end date of the reservation.
     *
     * @param dateEnd The end date to set.
     */
    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }
}