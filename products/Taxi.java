package products;

import services.TravelParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Represents a taxi service product with availability tracking and booking capabilities.
 */
public class Taxi extends Product {
    private int id;
    private String city;
    private String taxiType;
    private double baseFare;
    private double perKmRate;

    private HashMap<LocalDateTime, Integer> availableDateTimes;

    /**
     * Constructs a new Taxi with specified parameters.
     *
     * @param city City where the taxi operates
     * @param taxiType Type of taxi service
     * @param availableCount Total number of available taxis
     * @param baseFare Base fare for the taxi service
     * @param perKmRate Rate charged per kilometer
     * @param id Unique identifier for the taxi service
     */
    public Taxi(String city, String taxiType, int availableCount, double baseFare, double perKmRate, int id) {
        super(availableCount);
        this.city = city;
        this.taxiType = taxiType;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.id = id;
        this.availableDateTimes = new HashMap<>();
    }

    /**
     * Retrieves a Taxi object by its ID.
     *
     * @param id The unique identifier of the taxi
     * @return The corresponding Taxi object
     */
    public static Taxi retrieveTaxi(int id) {
        return TravelParser.getTaxisDict().get(id);
    }

    /**
     * Loads taxi availability data from file for this taxi's ID.
     *
     * @throws FileNotFoundException if the availability file cannot be found
     */
    public void taxiAvailabilityParser() throws FileNotFoundException {
        File file = new File("products/taxiavailability.txt");
        Scanner reader = new Scanner(file);

        this.availableDateTimes.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] dataArray = line.split(",");
            if (dataArray.length < 3) {
                continue;
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDateTime dateTime = LocalDateTime.parse(dataArray[1], formatter);
            int capacity = Integer.parseInt(dataArray[2]);

            if (lineId == this.id) {
                this.availableDateTimes.put(dateTime, capacity);
            }
        }
        reader.close();
    }

    /**
     * Returns a list of taxis available at a specific date and time.
     *
     * @param startDateTime The desired date and time
     * @param taxiList List of taxis to check availability from
     * @return List of available taxis
     */
    public static ArrayList<Taxi> availableCarsListMaker(LocalDateTime startDateTime,
                                                         ArrayList<Taxi> taxiList) {
        ArrayList<Taxi> newTaxiList = new ArrayList<>();

        for (Taxi taxi : taxiList) {
            if (taxi.getAvailabilityForDateTime(startDateTime) > 0) {
                newTaxiList.add(taxi);
            }
        }

        return newTaxiList;
    }

    /**
     * Calculates the total fare based on hotel location.
     *
     * @param hotel The hotel to calculate fare to/from
     * @return Total calculated fare
     */
    public double taxiPriceCalculator(Hotel hotel) {
        return this.baseFare + (hotel.getDistanceToAirport() * this.perKmRate);
    }

    /**
     * Updates the availability file with current availability data.
     *
     * @throws FileNotFoundException if the availability file cannot be found
     */
    public void updateFile() throws FileNotFoundException {
        File file = new File("products/taxiavailability.txt");
        List<String> lines = new ArrayList<>();

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> updatedLines = new ArrayList<>();
        Set<LocalDateTime> updatedDateTimes = new HashSet<>();

        for (String line : lines) {
            String[] dataArray = line.split(",");
            if (dataArray.length < 3) {
                updatedLines.add(line);
                continue;
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDateTime lineDateTime = LocalDateTime.parse(dataArray[1], formatter);
            int lineCapacity = Integer.parseInt(dataArray[2]);

            if (lineId == this.id && availableDateTimes.containsKey(lineDateTime)) {
                int newCapacity = availableDateTimes.get(lineDateTime);
                updatedLines.add(this.id + "," + lineDateTime.format(formatter) + "," + newCapacity);
                updatedDateTimes.add(lineDateTime);
            } else {
                updatedLines.add(line);
            }
        }

        for (LocalDateTime dateTime : availableDateTimes.keySet()) {
            if (!updatedDateTimes.contains(dateTime)) {
                int newCapacity = availableDateTimes.get(dateTime);
                updatedLines.add(this.id + "," + dateTime.format(formatter) + "," + newCapacity);
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
     * Books a taxi for a specific date and time.
     *
     * @param dateTime The date and time to book the taxi
     */
    public void book(LocalDateTime dateTime) {
        try {
            if (availableDateTimes == null || availableDateTimes.isEmpty()) {
                taxiAvailabilityParser();
            }

            if (availableDateTimes.containsKey(dateTime)) {
                int currentCap = availableDateTimes.get(dateTime);
                if (currentCap > 0) {
                    availableDateTimes.put(dateTime, currentCap - 1);
                } else {
                    System.out.println("No more available taxis on " + dateTime);
                    return;
                }
            } else {
                int defaultCapacity = getAvailableCount();
                availableDateTimes.put(dateTime, defaultCapacity - 1);
            }

            updateFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels a taxi booking for a specific date and time.
     *
     * @param dateTime The date and time of the booking to cancel
     */
    public void cancelBook(LocalDateTime dateTime) {
        try {
            if (availableDateTimes == null || availableDateTimes.isEmpty()) {
                taxiAvailabilityParser();
            }

            if (availableDateTimes.containsKey(dateTime)) {
                int currentCap = availableDateTimes.get(dateTime);
                int maxCapacity = getAvailableCount();
                if (currentCap < maxCapacity) {
                    availableDateTimes.put(dateTime, currentCap + 1);
                    System.out.println("Taxi booking cancelled successfully for " + dateTime);
                } else {
                    System.out.println("Cannot cancel booking - taxi capacity already at maximum for " + dateTime);
                    return;
                }
            } else {
                System.out.println("No taxi booking record found for " + dateTime);
                return;
            }

            updateFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the city where the taxi operates.
     *
     * @return The city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the type of taxi service.
     *
     * @return The taxi type
     */
    public String getTaxiType() {
        return taxiType;
    }

    /**
     * Returns the base fare for the taxi service.
     *
     * @return The base fare amount
     */
    public double getBaseFare() {
        return baseFare;
    }

    /**
     * Returns the per-kilometer rate.
     *
     * @return The rate per kilometer
     */
    public double getPerKmRate() {
        return perKmRate;
    }

    /**
     * Returns a string representation of the taxi.
     *
     * @return String in format "taxiType city"
     */
    @Override
    public String toString() {
        return taxiType + " " + city;
    }

    /**
     * Returns the unique identifier of the taxi.
     *
     * @return The taxi ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns a list of taxis operating in a specific city.
     *
     * @param city The city to search for
     * @return List of taxis in the specified city
     */
    public static ArrayList<Taxi> selectByCity(String city) {
        ArrayList<Taxi> taxisInCity = new ArrayList<>();

        for (Taxi taxi : TravelParser.getTaxisDict().values()) {
            if (taxi.getCity().equalsIgnoreCase(city)) {
                taxisInCity.add(taxi);
            }
        }

        return taxisInCity;
    }

    /**
     * Returns the availability for a specific date and time.
     *
     * @param dateTime The date and time to check
     * @return Number of available taxis
     */
    public int getAvailabilityForDateTime(LocalDateTime dateTime) {
        if (availableDateTimes == null || availableDateTimes.isEmpty()) {
            try {
                taxiAvailabilityParser();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return availableDateTimes.getOrDefault(dateTime, getAvailableCount());
    }
}