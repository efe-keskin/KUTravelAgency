package services;

import products.Flight;
import products.Hotel;
import products.Product;
import products.Taxi;

import java.time.LocalDate;
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

public Package(String type,int hotelID,int flightID,int taxiID,LocalDate dateStart,LocalDate dateEnd){
    this.hotel = Hotel.retrieveHotel(hotelID);
    this.flight = Flight.retrieveFlight(flightID);
    this.taxi = Taxi.retrieveTaxi(taxiID);

}

    @Override
    public String toString() {
        return this.hotel.getID()+","+this.flight.getID()+","+this.taxi.getID();
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

    public String getType() {
        return type;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }
}
