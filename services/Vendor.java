package services;

import Users.Customer;
import Users.User;
import products.Flight;
import products.Hotel;
import products.Taxi;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Vendor {
private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //Sells the packages and keeps track in packages txt
    //Stores the customer info and package info in transactions txt
    //Makes reservations and adds them in the users
    public static void packageSeller(Package pck, Customer cst){
        Hotel hotel = pck.getHotel();
        LocalDate dateStart = pck.getDateStart();
        LocalDate hotelStartDate = pck.getHotelStart();
        LocalDate dateEnd = pck.getDateEnd();
        for (LocalDate date = hotelStartDate; !date.isAfter(dateEnd); date = date.plusDays(1))
        {
            hotel.book(date);
        }
        Flight flight = pck.getFlight();
        flight.book(dateStart);
        Taxi taxi = pck.getTaxi();
        if(!flight.isDayChange()){
            taxi.book(dateStart);
        }
        else{taxi.book(dateStart.plusDays(1));}
        ReservationsManagers.makeReservation(pck, cst);

    }
public static void paymentMaker(Reservation res,Customer cst) throws IOException {

    BufferedWriter writer = new BufferedWriter(new FileWriter("services/packages.txt"));
    LocalDate now = LocalDate.now();
    String date = now.format(DATE_FORMATTER);

    writer.write(String.join(",",date,String.valueOf(res.getRelatedPackage().getTotalCost()),
            String.valueOf(res.getId()),String.valueOf(cst.getID())));
}



}
