package services;

import products.Flight;
import products.Hotel;
import products.Product;
import products.Taxi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Represents a travel package consisting of a hotel stay, a flight, and a taxi service.
 * It calculates the total cost of the package based on the components and their durations.
 */
public class Package {
    private int id;
    private int totalCost;
    private int discountedPrice;
    private String type; // Offered or custom package type
    private Hotel hotel;
    private Flight flight;
    private Taxi taxi;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private LocalDate hotelStart;
    private LocalDateTime taxiTime;
    private long daysInHotel;

    /**
     * Creates a new Package object by copying the details of another package.
     *
     * @param pck The package to copy.
     */
    public Package(Package pck) {
        this.hotel = pck.getHotel();
        this.flight = pck.getFlight();
        this.taxiTime = pck.getTaxiTime();
        this.taxi = pck.getTaxi();
        this.dateEnd = pck.getDateEnd();
        this.totalCost = pck.getTotalCost();
        this.discountedPrice = pck.getDiscountedPrice();
        this.daysInHotel = pck.getDaysInHotel();
        this.dateStart = pck.getDateStart();
        this.hotelStart = pck.getHotelStart();
        this.type = pck.getType();
        this.id = pck.getId();
    }

    /**
     * Constructs a new Package object with specified details.
     *
     * @param type The type of the package (offered or custom).
     * @param hotelID ID of the hotel included in the package.
     * @param flightID ID of the flight included in the package.
     * @param taxiID ID of the taxi service included in the package.
     * @param dateStart Start date of the package.
     * @param dateEnd End date of the package.
     * @param taxiTime Time of the taxi service.
     * @param id Unique identifier for the package.
     */
    public Package(String type, int hotelID, int flightID, int taxiID,
                   LocalDate dateStart, LocalDate dateEnd, LocalDateTime taxiTime, int id) {

        this.hotel = Hotel.retrieveHotel(hotelID);
        this.flight = Flight.retrieveFlight(flightID);
        this.taxi = Taxi.retrieveTaxi(taxiID);
        this.dateStart = dateStart;
        this.type = type;
        this.taxiTime = taxiTime;
        this.id = id;

        this.hotelStart = dateStart;
        this.dateEnd = dateEnd;

        // Calculate the number of days for the hotel stay
        long daysInHotel = ChronoUnit.DAYS.between(hotelStart, dateEnd);
        this.daysInHotel = daysInHotel;
        if (daysInHotel <= 0) {
            throw new IllegalArgumentException("End date must be after start date for hotel stay.");
        }

        // Calculate total cost
        this.totalCost = (int) (hotel.getPricePerNight() * daysInHotel
                + flight.getPrice()
                + (taxi.getBaseFare() + (taxi.getPerKmRate() * hotel.getDistanceToAirport())));
        setDiscountedPrice(totalCost);
    }

    /**
     * Returns a string representation of the package.
     *
     * @return A string containing hotel name, airline, and taxi type.
     */
    @Override
    public String toString() {
        return this.hotel.getName() + "," + this.flight.getAirline() + "," + this.taxi.getTaxiType();
    }

    // Getters and Setters with Javadoc comments

    /**
     * Sets the hotel included in the package.
     *
     * @param hotel The hotel to set.
     */
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    /**
     * Sets the taxi service included in the package.
     *
     * @param taxi The taxi to set.
     */
    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    /**
     * Sets the flight included in the package.
     *
     * @param flight The flight to set.
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    /**
     * Gets the total cost of the package.
     *
     * @return The total cost of the package.
     */
    public int getTotalCost() {
        return totalCost;
    }

    /**
     * Gets the unique ID of the package.
     *
     * @return The package ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the hotel included in the package.
     *
     * @return The hotel object.
     */
    public Hotel getHotel() {
        return hotel;
    }

    /**
     * Gets the flight included in the package.
     *
     * @return The flight object.
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * Gets the taxi service included in the package.
     *
     * @return The taxi object.
     */
    public Taxi getTaxi() {
        return taxi;
    }

    /**
     * Gets the start date of the hotel stay.
     *
     * @return The start date.
     */
    public LocalDate getHotelStart() {
        return hotelStart;
    }

    /**
     * Gets the type of the package (offered or custom).
     *
     * @return The type of the package.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the start date of the package.
     *
     * @return The start date.
     */
    public LocalDate getDateStart() {
        return dateStart;
    }

    /**
     * Gets the end date of the package.
     *
     * @return The end date.
     */
    public LocalDate getDateEnd() {
        return dateEnd;
    }

    /**
     * Gets the number of days the user will stay in the hotel.
     *
     * @return The number of days in the hotel.
     */
    public long getDaysInHotel() {
        return daysInHotel;
    }

    /**
     * Sets the ID of the package.
     *
     * @param id The ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the type of the package.
     *
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the scheduled time of the taxi service.
     *
     * @return The taxi service time.
     */
    public LocalDateTime getTaxiTime() {
        return taxiTime;
    }

    /**
     * Sets the scheduled time of the taxi service.
     *
     * @param taxiTime The taxi time to set.
     */
    public void setTaxiTime(LocalDateTime taxiTime) {
        this.taxiTime = taxiTime;
    }

    /**
     * Sets the start date of the package.
     *
     * @param startDate The start date to set.
     */
    public void setDateStart(LocalDate startDate) {
        this.dateStart = startDate;
    }

    /**
     * Sets the end date of the package.
     *
     * @param endDate The end date to set.
     */
    public void setDateEnd(LocalDate endDate) {
        this.dateEnd = endDate;
    }

    /**
     * Gets the discounted price of the package.
     *
     * @return The discounted price.
     */
    public int getDiscountedPrice() {
        return discountedPrice;
    }

    /**
     * Sets the discounted price of the package.
     *
     * @param discountedPrice The discounted price to set.
     */
    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
