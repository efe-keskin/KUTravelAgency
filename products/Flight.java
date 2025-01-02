package products;

import core.App;
import reservationlogs.Logger;
import services.TravelParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Represents a flight product with details such as airline, departure/arrival times,
 * ticket class, and availability management.
 */
public class Flight extends Product {
    private String flightID;
    private String airline;
    private String departureCity;
    private String arrivalCity;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String ticketClass;
    private double price;
    private String stopoverCity;
    private String finalArrivalCity;
    private LocalTime leg1DepartureTime;
    private LocalTime leg1ArrivalTime;
    private LocalTime leg2DepartureTime;
    private LocalTime leg2ArrivalTime;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
    private boolean dayChange; // Indicates if the flight crosses midnight
    private HashMap<LocalDate, Integer> availableDates; // Availability map: date -> capacity

    /**
     * Constructor for direct flights.
     *
     * @param airline         The airline operating the flight.
     * @param departureCity   The city of departure.
     * @param arrivalCity     The destination city.
     * @param availableCount  Initial seat availability.
     * @param departureTime   Departure time in HH:mm format.
     * @param arrivalTime     Arrival time in HH:mm format.
     * @param ticketClass     The ticket class (e.g., Economy, Business).
     * @param price           The price of the ticket.
     * @param id              Unique flight ID.
     */
    public Flight(String airline, String departureCity, String arrivalCity,
                  int availableCount, String departureTime, String arrivalTime,
                  String ticketClass, double price, int id) {
        super(availableCount);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        this.airline = airline;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = LocalTime.parse(departureTime, formatter);
        this.arrivalTime = LocalTime.parse(arrivalTime, formatter);
        this.ticketClass = ticketClass;
        this.price = price;
        this.stopoverCity = null;
        this.finalArrivalCity = null;
        this.leg1DepartureTime = null;
        this.leg1ArrivalTime = null;
        this.leg2DepartureTime = null;
        this.leg2ArrivalTime = null;
        this.flightID = String.valueOf(id);
        this.dayChange = this.arrivalTime.isBefore(this.departureTime);
        this.availableDates = new HashMap<>();
    }

    /**
     * Constructor for flights with a stopover.
     *
     * @param airline            The airline operating the flight.
     * @param departureCity      The city of departure.
     * @param stopoverCity       The city where the flight stops.
     * @param finalArrivalCity   The final destination city.
     * @param leg1DepartureTime  Departure time for the first leg.
     * @param leg1ArrivalTime    Arrival time for the first leg.
     * @param leg2DepartureTime  Departure time for the second leg.
     * @param leg2ArrivalTime    Arrival time for the second leg.
     * @param availableCount     Initial seat availability.
     * @param ticketClass        The ticket class.
     * @param price              The price of the ticket.
     * @param id                 Unique flight ID.
     */
    public Flight(String airline, String departureCity, String stopoverCity,
                  String finalArrivalCity, String leg1DepartureTime, String leg1ArrivalTime,
                  String leg2DepartureTime, String leg2ArrivalTime, int availableCount,
                  String ticketClass, double price, int id) {
        super(availableCount);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        this.flightID = String.valueOf(id);
        this.airline = airline;
        this.departureCity = departureCity;
        this.stopoverCity = stopoverCity;
        this.finalArrivalCity = finalArrivalCity;
        this.ticketClass = ticketClass;
        this.price = price;
        this.arrivalCity = finalArrivalCity;
        this.leg1DepartureTime = LocalTime.parse(leg1DepartureTime, formatter);
        this.leg1ArrivalTime = LocalTime.parse(leg1ArrivalTime, formatter);
        this.leg2DepartureTime = LocalTime.parse(leg2DepartureTime, formatter);
        this.leg2ArrivalTime = LocalTime.parse(leg2ArrivalTime, formatter);
        this.departureTime = this.leg1DepartureTime;
        this.arrivalTime = this.leg2ArrivalTime;
        this.dayChange = this.arrivalTime.isBefore(this.departureTime);
        this.availableDates = new HashMap<>();
    }

    /**
     * Retrieves the flight object by ID from TravelParser.
     *
     * @param id Flight ID.
     * @return The corresponding Flight object.
     */
    public static Flight retrieveFlight(int id) {
        return TravelParser.getFlightsDict().get(id);
    }

    /**
     * Filters flights by departure and arrival cities.
     *
     * @param city    Arrival city.
     * @param depCity Departure city.
     * @return List of matching flights.
     */
    public static ArrayList<Flight> selectByCity(String city, String depCity) {
        ArrayList<Flight> flightsInCity = new ArrayList<>();
        for (Flight flight : TravelParser.getFlightsDict().values()) {
            if (flight.getArrivalCity().equalsIgnoreCase(city) && flight.getDepartureCity().equalsIgnoreCase(depCity)) {
                flightsInCity.add(flight);
            }
        }
        return flightsInCity;
    }

    /**
     * Loads flight availability from a file.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    public void flightAvailabilityParser() throws FileNotFoundException {
        File file = new File("products/flightavailability.txt");
        Scanner reader = new Scanner(file);
        this.availableDates.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] dataArray = line.split(",");
            if (dataArray.length < 3) continue;

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDate date = LocalDate.parse(dataArray[1], formatter);
            int capacity = Integer.parseInt(dataArray[2]);

            if (lineId == this.getId()) {
                this.availableDates.put(date, capacity);
            }
        }
        reader.close();
    }

    /**
     * Updates the availability file with current data.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    public void updateFile() throws FileNotFoundException {
        File file = new File("products/flightavailability.txt");
        List<String> lines = new ArrayList<>();
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> updatedLines = new ArrayList<>();
        Set<LocalDate> updatedDates = new HashSet<>();

        for (String line : lines) {
            String[] dataArray = line.split(",");
            if (dataArray.length < 3) {
                updatedLines.add(line);
                continue;
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDate lineDate = LocalDate.parse(dataArray[1], formatter);
            int lineCapacity = Integer.parseInt(dataArray[2]);

            if (lineId == this.getId() && availableDates.containsKey(lineDate)) {
                int newCapacity = availableDates.get(lineDate);
                updatedLines.add(this.getId() + "," + lineDate.format(formatter) + "," + newCapacity);
                updatedDates.add(lineDate);
            } else {
                updatedLines.add(line);
            }
        }

        for (LocalDate date : availableDates.keySet()) {
            if (!updatedDates.contains(date)) {
                int newCapacity = availableDates.get(date);
                updatedLines.add(this.getId() + "," + date.format(formatter) + "," + newCapacity);
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.println(updatedLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Books a seat for a given date.
     *
     * @param date Date of the flight.
     */
    public void book(LocalDate date) {
        try {
            if (availableDates == null || availableDates.isEmpty()) {
                flightAvailabilityParser();
            }

            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                if (currentCap > 0) {
                    availableDates.put(date, currentCap - 1);
                } else {
                    System.out.println("No more available seats on " + date);
                    return;
                }
            } else {
                int defaultCapacity = getAvailableCount();
                availableDates.put(date, defaultCapacity - 1);
            }

            updateFile();
            Logger.logFlightreservation(App.user.getUsername(), this.toString(), departureTime.format(formatter), arrivalTime.format(formatter), ticketClass);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels a booking for a given date.
     *
     * @param date Date of the flight.
     */
    public void cancelBook(LocalDate date) {
        try {
            if (availableDates == null || availableDates.isEmpty()) {
                flightAvailabilityParser();
            }

            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                int maxCapacity = getAvailableCount();
                if (currentCap < maxCapacity) {
                    availableDates.put(date, currentCap + 1);
                    System.out.println("Flight booking cancelled successfully for " + date);
                } else {
                    System.out.println("Cannot cancel booking - flight already at maximum capacity for " + date);
                    return;
                }
            } else {
                System.out.println("No flight booking record found for " + date);
                return;
            }

            updateFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return flightID + " " + airline + " " + departureCity + " " + arrivalCity;
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

    public double getPrice() {
        return price;
    }

    public String getStopoverCity() {
        return stopoverCity;
    }

    public String getFinalArrivalCity() {
        return finalArrivalCity;
    }

    public LocalTime getLeg1DepartureTime() {
        return leg1DepartureTime;
    }

    public LocalTime getLeg1ArrivalTime() {
        return leg1ArrivalTime;
    }

    public LocalTime getLeg2DepartureTime() {
        return leg2DepartureTime;
    }

    public LocalTime getLeg2ArrivalTime() {
        return leg2ArrivalTime;
    }

    public boolean isDayChange() {
        return dayChange;
    }

    public int getId() {
        return Integer.parseInt(flightID);
    }

    public int getAvailabilityForDate(LocalDate date) {
        if (availableDates == null || availableDates.isEmpty()) {
            try {
                flightAvailabilityParser();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return availableDates.getOrDefault(date, getAvailableCount());
    }

    public static ArrayList<Flight> availableSeatsListMaker(LocalDate dateStart, ArrayList<Flight> arrayList) {
        ArrayList<Flight> newFlightsList = new ArrayList<>();
        for (Flight flight : arrayList) {
            if (flight.isDayChange()) {
                if (flight.getAvailabilityForDate(dateStart.minusDays(1)) > 0) {
                    newFlightsList.add(flight);
                }
            } else {
                if (flight.getAvailabilityForDate(dateStart) > 0) {
                    newFlightsList.add(flight);
                }
            }
        }
        return newFlightsList;
    }

    public String getDuration() {
        LocalDateTime departureDateTime = LocalDateTime.of(LocalDate.of(2000, 1, 1), this.departureTime);
        LocalDateTime arrivalDateTime = this.dayChange
                ? LocalDateTime.of(LocalDate.of(2000, 1, 2), this.arrivalTime)
                : LocalDateTime.of(LocalDate.of(2000, 1, 1), this.arrivalTime);

        Duration duration = Duration.between(departureDateTime, arrivalDateTime);
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        return hours + "h " + minutes + "m";
    }
}
