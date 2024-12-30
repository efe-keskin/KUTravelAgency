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

public class Reservation {
    private int id;
    private Package relatedPackage;
    private boolean status = true; //confirmed or cancelled
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private Customer customer;


    public Reservation(int id,Package pck,Customer customer){
        this.id = id;
        this.relatedPackage = pck;
        this.customer = customer;

        //dates will calculated based on package data
        dateEnd = pck.getDateEnd();
        dateStart = pck.getDateStart();

    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return String.join(",",
                String.valueOf(id),
                relatedPackage != null ? relatedPackage.toString() : "", // Assuming Package has a proper toString method
                status ? "confirmed" : "cancelled",
                dateStart != null ? dateStart.format(formatter) : "", // Format start date
                dateEnd != null ? dateEnd.format(formatter) : "", // Format end date
                customer != null ? customer.toString() : "" // Assuming User has a toString method
        );
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public Package getRelatedPackage() {
        return relatedPackage;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public int getId() {
        return id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }
}
