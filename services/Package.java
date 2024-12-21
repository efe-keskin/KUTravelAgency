package services;

import products.Flight;
import products.Hotel;
import products.Product;
import products.Taxi;

import java.time.LocalDate;
import java.util.ArrayList;

public class Package {
    private int id;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private ArrayList<Product> productsList;
    private int totalCost;
    private Hotel hotel;
    private Flight flight;
    private Taxi taxi;
public Package(int hotelID, int flightID,int taxiID){
    this.hotel = Hotel.retrieveHotel(hotelID);
    this.flight = Flight.retrieveFlight(flightID);
    this.taxi = Taxi.retrieveTaxi(taxiID);

}

    @Override
    public String toString() {
        return this.hotel.getID()+","+this.flight.getID()+","+this.taxi.getID();
    }

    public ArrayList<Product> getProductsList() {
        return productsList;
    }



    public LocalDate getDateStart() {
        return dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getId() {
        return id;
    }
}
