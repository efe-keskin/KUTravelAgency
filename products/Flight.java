package products;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Flight extends Product{
    private String flightID;
    private String airline;
    private String departureCity;
    private String arrivalCity;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String ticketClass;
    private int price;
    private String stopoverCity;
    private String finalArrivalCity;
    private LocalTime leg1DepartureTime;
    private LocalTime leg1ArrivalTime;
    private LocalTime leg2DepartureTime;
    private LocalTime leg2ArrivalTime;
    public static HashMap<Integer,Flight> flightsDict;
    // Constructor for direct flights
    public Flight(String flightID, String airline, String departureCity, String arrivalCity,
                  int availableCount, String departureTime, String arrivalTime, String ticketClass, int price) {
        super(availableCount);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        this.flightID = flightID;
        this.airline = airline;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = LocalTime.parse(departureTime, formatter);
        this.arrivalTime = LocalTime.parse(arrivalTime, formatter);
        this.ticketClass = ticketClass;
        this.price = price;
        this.stopoverCity = null; // No stopover for direct flights
        this.finalArrivalCity = null; // No final arrival city for direct flights
        this.leg1DepartureTime = null;
        this.leg1ArrivalTime = null;
        this.leg2DepartureTime = null;
        this.leg2ArrivalTime = null;
    }

    // Constructor for stopover flights
    public Flight(String flightID, String airline, String departureCity, String stopoverCity, String finalArrivalCity,
                  String leg1DepartureTime, String leg1ArrivalTime, String leg2DepartureTime, String leg2ArrivalTime,
                  int availableCount, String ticketClass, int price) {
        super(availableCount);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        this.flightID = flightID;
        this.airline = airline;
        this.departureCity = departureCity;
        this.stopoverCity = stopoverCity;
        this.finalArrivalCity = finalArrivalCity;
        this.ticketClass = ticketClass;
        this.price = price;
        this.arrivalCity = finalArrivalCity;

        // Leg 1 times
        this.leg1DepartureTime = LocalTime.parse(leg1DepartureTime, formatter);
        this.leg1ArrivalTime = LocalTime.parse(leg1ArrivalTime, formatter);

        // Leg 2 times
        this.leg2DepartureTime = LocalTime.parse(leg2DepartureTime, formatter);
        this.leg2ArrivalTime = LocalTime.parse(leg2ArrivalTime, formatter);

        // overall departure and arrival times for convenience
        this.departureTime = this.leg1DepartureTime;
        this.arrivalTime = this.leg2ArrivalTime;
    }
    public static Flight retrieveFlight(int id){
        return flightsDict.get(id);
    }
    @Override
    public String toString() {

        return flightID + " " + airline + " " + departureCity + " " + arrivalCity ;
    }

    public String getFlightID() {
        return flightID;
    }

    public String getAirline() {
        return airline;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public String getTicketClass() {
        return ticketClass;
    }

    public int getPrice() {
        return price;
    }

    public String getStopoverCity() {
        return stopoverCity;
    }

    public String getFinalArrivalCity() {
        return finalArrivalCity;
    }

    public LocalTime getLeg1ArrivalTime() {
        return leg1ArrivalTime;
    }

    public LocalTime getLeg2DepartureTime() {
        return leg2DepartureTime;
    }

    public LocalTime getLeg1DepartureTime() {
        return leg1DepartureTime;
    }

    public LocalTime getLeg2ArrivalTime() {
        return leg2ArrivalTime;
    }
}
