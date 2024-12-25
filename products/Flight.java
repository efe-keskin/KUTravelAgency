package products;

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

    // New boolean indicating if the flight crosses midnight
    private boolean dayChange;

    // 1) A map to store availability: date -> capacity
    private HashMap<LocalDate, Integer> availableDates;

    // Constructor for direct flights
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
        this.stopoverCity = null; // No stopover for direct flights
        this.finalArrivalCity = null; // No final arrival city for direct flights
        this.leg1DepartureTime = null;
        this.leg1ArrivalTime = null;
        this.leg2DepartureTime = null;
        this.leg2ArrivalTime = null;
        this.flightID = String.valueOf(id); // ensure flightID is consistent with int ID

        // Determine if this flight ends after midnight compared to its departure
        // (arrivalTime < departureTime means it rolled into the next day)
        this.dayChange = this.arrivalTime.isBefore(this.departureTime);

        // Initialize the availability map to avoid NullPointerExceptions
        this.availableDates = new HashMap<>();
    }

    // Constructor for stopover flights
    public Flight( String airline, String departureCity, String stopoverCity,
                  String finalArrivalCity, String leg1DepartureTime, String leg1ArrivalTime,
                  String leg2DepartureTime, String leg2ArrivalTime, int availableCount,
                  String ticketClass, double price,int id) {
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

        // Leg 1 times
        this.leg1DepartureTime = LocalTime.parse(leg1DepartureTime, formatter);
        this.leg1ArrivalTime   = LocalTime.parse(leg1ArrivalTime,   formatter);

        // Leg 2 times
        this.leg2DepartureTime = LocalTime.parse(leg2DepartureTime, formatter);
        this.leg2ArrivalTime   = LocalTime.parse(leg2ArrivalTime,   formatter);

        // Overall departure and arrival times for convenience
        this.departureTime = this.leg1DepartureTime;
        this.arrivalTime   = this.leg2ArrivalTime;

        // For multi-leg flights, you could either check each leg
        // or just check the overall departure vs. overall arrival:
        this.dayChange = this.arrivalTime.isBefore(this.departureTime);

        // Initialize the availability map
        this.availableDates = new HashMap<>();
    }

    /**
     * Retrieves the Flight object from TravelParser's dictionary by ID.
     */
    public static Flight retrieveFlight(int id) {
        return TravelParser.getFlightsDict().get(id);
    }

    public static ArrayList<Flight> selectByCity(String city,String depCity) {
        // Create a list to store all matching flights
        ArrayList<Flight> flightsInCity = new ArrayList<>();

        // Iterate through all flights in the TravelParser's dictionary
        for (Flight flight : TravelParser.getFlightsDict().values()) {
            // Compare city names, ignoring case to make the search more robust
            if (flight.getArrivalCity().equalsIgnoreCase(city)&&flight.getDepartureCity().equalsIgnoreCase(depCity)) {
                flightsInCity.add(flight);
            }
        }

        return flightsInCity;
    }

    /**
     * 2) Reads 'flightavailability.txt' to populate the availableDates map
     *    with (date -> capacity) for this flight's ID.
     */
    public void flightAvailabilityParser() throws FileNotFoundException {
        File file = new File("products/flightavailability.txt");
        Scanner reader = new Scanner(file);
        // Clear old data to avoid duplicates
        this.availableDates.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] dataArray = line.split(",");
            // Expect at least 3 parts: id, date, capacity
            if (dataArray.length < 3) {
                continue; // skip malformed lines
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDate date = LocalDate.parse(dataArray[1], formatter);
            int capacity = Integer.parseInt(dataArray[2]);

            // Only load for this specific flight
            if (lineId == this.getId()) {
                this.availableDates.put(date, capacity);
            }
        }
        reader.close();
    }

    /**
     * 3) Rewrites 'flightavailability.txt' to reflect updated capacities
     *    for all flights, including this one.
     */
    public void updateFile() throws FileNotFoundException {
        File file = new File("products/flightavailability.txt");
        List<String> lines = new ArrayList<>();

        // 1) Read all lines from the original file
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
        }

        // 2) Prepare for parsing and rewriting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> updatedLines = new ArrayList<>();
        // Track which dates we've updated to avoid duplicating
        Set<LocalDate> updatedDates = new HashSet<>();

        // 3) Loop through existing lines
        for (String line : lines) {
            String[] dataArray = line.split(",");
            if (dataArray.length < 3) {
                updatedLines.add(line);
                continue;
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDate lineDate = LocalDate.parse(dataArray[1], formatter);
            int lineCapacity = Integer.parseInt(dataArray[2]);

            // If this line belongs to our flight & date is in our map, replace it
            if (lineId == this.getId() && availableDates.containsKey(lineDate)) {
                int newCapacity = availableDates.get(lineDate);
                updatedLines.add(this.getId() + "," + lineDate.format(formatter) + "," + newCapacity);
                updatedDates.add(lineDate);
            } else {
                // Otherwise, keep the existing line
                updatedLines.add(line);
            }
        }

        // 4) Add lines for any date in availableDates not in the original file
        for (LocalDate date : availableDates.keySet()) {
            if (!updatedDates.contains(date)) {
                int newCapacity = availableDates.get(date);
                updatedLines.add(this.getId() + "," + date.format(formatter) + "," + newCapacity);
            }
        }

        // 5) Rewrite the entire file with updated lines
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.println(updatedLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 4) Books a seat for the specified date.
     *    - If the date doesn't exist, set a default capacity, then decrement.
     *    - If the date exists & capacity > 0, decrement.
     *    - updateFile() afterward to persist changes.
     */
    public void book(LocalDate date) {
        try {
            // 1) Ensure availability is loaded
            if (availableDates == null || availableDates.isEmpty()) {
                flightAvailabilityParser();
            }

            // 2) Check if the date is in our map
            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                if (currentCap > 0) {
                    availableDates.put(date, currentCap - 1);
                } else {
                    System.out.println("No more available seats on " + date);
                    return;
                }
            } else {
                // 3) If date not present, add a default capacity & decrement
                int defaultCapacity = getAvailableCount();  // choose a default
                availableDates.put(date, defaultCapacity - 1);
            }

            // 4) Persist changes
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

    /**
     * A boolean property that returns true if the flight spans into the next day.
     */
    public boolean isDayChange() {
        return dayChange;
    }

    /**
     * Override getId so that we can align with flight availability file (IDs).
     */
    public int getId() {
        return Integer.parseInt(flightID);
    }
    public int getAvailabilityForDate(LocalDate date) {
        // Ensure the availability data is loaded
        if (availableDates == null || availableDates.isEmpty()) {
            try {
                flightAvailabilityParser();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Return capacity if present; otherwise the default
        return availableDates.getOrDefault(date, getAvailableCount());
    }
    public static ArrayList<Flight> availableSeatsListMaker(LocalDate dateStart,ArrayList<Flight> arrayList){
        ArrayList<Flight> newFlightsList = new ArrayList<Flight>();
        for(Flight flight : arrayList) {
            if (flight.isDayChange()) {
                if(flight.getAvailabilityForDate(dateStart.minusDays(1))>0){
                    newFlightsList.add(flight);
                }
            }
            else{
                if(flight.getAvailabilityForDate(dateStart)>0){
                    newFlightsList.add(flight);
                }
            }
        }
        return newFlightsList;
    }


    public String getDuration() {
        // 1) Create a LocalDateTime for the departure (arbitrary date, e.g. Jan 1, 2000).
        LocalDateTime departureDateTime = LocalDateTime.of(
                LocalDate.of(2000, 1, 1),
                this.departureTime
        );

        // 2) Create a LocalDateTime for the arrival.
        //    If the flight crosses midnight (dayChange == true), shift arrival to Jan 2, 2000.
        LocalDateTime arrivalDateTime = this.dayChange
                ? LocalDateTime.of(LocalDate.of(2000, 1, 2), this.arrivalTime)
                : LocalDateTime.of(LocalDate.of(2000, 1, 1), this.arrivalTime);

        // 3) Calculate the Duration
        Duration duration = Duration.between(departureDateTime, arrivalDateTime);

        // 4) Convert the Duration to hours/minutes
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        // 5) Return a simple string format: e.g. "2h 45m"
        return hours + "h " + minutes + "m";
    }
}
