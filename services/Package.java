package services;

import products.Flight;
import products.Hotel;
import products.Product;
import products.Taxi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Package {
    private int id;
    private int totalCost;
    private String type; //offered or custom
    private Hotel hotel;
    private Flight flight;
    private Taxi taxi;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private LocalDate hotelStart;
    private long daysInHotel;


    public Package(String type, int hotelID, int flightID, int taxiID,
                   LocalDate dateStart, LocalDate dateEnd) {

        this.hotel = Hotel.retrieveHotel(hotelID);
        this.flight = Flight.retrieveFlight(flightID);
        this.taxi = Taxi.retrieveTaxi(taxiID);
        this.dateStart = dateStart;

        // Adjust the start date for the hotel if the flight causes a day change
        if (flight.isDayChange()) {
            this.hotelStart = dateStart.plusDays(1);
        } else {
            this.hotelStart = dateStart;
        }

        this.dateEnd = dateEnd;

        // Calculate how many days the user will stay in the hotel
        long daysInHotel = ChronoUnit.DAYS.between(hotelStart, dateEnd);
        this.daysInHotel = daysInHotel;
        if (daysInHotel <= 0) {
            throw new IllegalArgumentException("End date must be after start date for hotel stay.");
        }

        // Multiply the number of days by the hotel's price per night
        this.totalCost = (int) (hotel.getPricePerNight() * daysInHotel);
    }


    @Override
    public String toString() {
        return this.hotel.getId()+","+this.flight.getId()+","+this.taxi.getId();
    }



    public int getTotalCost() {
        return totalCost;
    }

    public int getId() {
        return id;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Flight getFlight() {
        return flight;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public LocalDate getHotelStart() {
        return hotelStart;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public long getDaysInHotel() {
        return daysInHotel;
    }
}
