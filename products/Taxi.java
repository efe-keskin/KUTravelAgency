package products;

import services.TravelParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Taxi extends Product {
    private int id;
    private String city;
    private String taxiType;
    private double baseFare;
    private double perKmRate;

    /**
     *  We now store availability for specific date-times (instead of just dates).
     *  Key: LocalDateTime
     *  Value: capacity (int)
     */
    private HashMap<LocalDateTime, Integer> availableDateTimes;

    public Taxi(String city, String taxiType, int availableCount, double baseFare, double perKmRate, int id) {
        super(availableCount);
        this.city = city;
        this.taxiType = taxiType;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.id = id;

        // Initialize to avoid NullPointerExceptions
        this.availableDateTimes = new HashMap<>();
    }

    /**
     * Retrieves the Taxi object from TravelParser's dictionary by ID.
     */
    public static Taxi retrieveTaxi(int id) {
        return TravelParser.getTaxisDict().get(id);
    }

    /**
     * Reads 'taxiavailability.txt' and populates the availableDateTimes map
     * with (dateTime -> capacity) entries for this taxi's ID.
     * Updated to parse date and time using yyyy-MM-dd HH:mm:ss
     */
    public void taxiAvailabilityParser() throws FileNotFoundException {
        File file = new File("products/taxiavailability.txt");
        Scanner reader = new Scanner(file);

        // Clear out any existing data to avoid duplicates
        this.availableDateTimes.clear();

        // Updated formatter: includes year, month, day, hour, minute, second
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] dataArray = line.split(",");
            // Make sure the line has at least 3 parts: id, dateTime, capacity
            if (dataArray.length < 3) {
                continue; // skip malformed lines
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDateTime dateTime = LocalDateTime.parse(dataArray[1], formatter);
            int capacity = Integer.parseInt(dataArray[2]);

            // Only load data for this particular taxi
            if (lineId == this.id) {
                this.availableDateTimes.put(dateTime, capacity);
            }
        }
        reader.close();
    }

    /**
     * Generates a list of Taxis that have capacity > 0 at the given startDateTime.
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
     * Calculates taxi price based on the hotel's distance to airport.
     */
    public double taxiPriceCalculator(Hotel hotel) {
        // baseFare + (airportDistance * perKmRate)
        return this.baseFare + (hotel.getDistanceToAirport() * this.perKmRate);
    }

    /**
     * Rewrites 'taxiavailability.txt' to reflect the updated availability
     * for all taxis, including this one, using dateTime in yyyy-MM-dd HH:mm:ss format.
     */
    public void updateFile() throws FileNotFoundException {
        File file = new File("products/taxiavailability.txt");
        List<String> lines = new ArrayList<>();

        // 1) Read all lines from the original file
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
        }

        // Prepare for parsing and rewriting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> updatedLines = new ArrayList<>();
        // We'll track which LocalDateTimes we've updated so we don't add duplicates
        Set<LocalDateTime> updatedDateTimes = new HashSet<>();

        // 2) Loop through existing lines
        for (String line : lines) {
            String[] dataArray = line.split(",");
            if (dataArray.length < 3) {
                // If line is malformed, just keep it as-is or skip
                updatedLines.add(line);
                continue;
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDateTime lineDateTime = LocalDateTime.parse(dataArray[1], formatter);
            int lineCapacity = Integer.parseInt(dataArray[2]);

            // If this line belongs to our taxi and dateTime is in our map, replace it
            if (lineId == this.id && availableDateTimes.containsKey(lineDateTime)) {
                int newCapacity = availableDateTimes.get(lineDateTime);
                updatedLines.add(this.id + "," + lineDateTime.format(formatter) + "," + newCapacity);
                updatedDateTimes.add(lineDateTime);
            } else {
                // Otherwise, keep the existing line
                updatedLines.add(line);
            }
        }

        // 3) Add lines for any dateTime in availableDateTimes not found in the original file
        for (LocalDateTime dateTime : availableDateTimes.keySet()) {
            if (!updatedDateTimes.contains(dateTime)) {
                int newCapacity = availableDateTimes.get(dateTime);
                updatedLines.add(this.id + "," + dateTime.format(formatter) + "," + newCapacity);
            }
        }

        // 4) Rewrite the entire file with updated lines
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.println(updatedLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Books a taxi on a specific dateTime.
     * - Loads availability if not already loaded.
     * - Checks if the dateTime has capacity > 0; if so, decrement.
     * - If dateTime doesn't exist, sets a default capacity, then decrements.
     * - Updates the file at the end.
     */
    public void book(LocalDateTime dateTime) {
        try {
            // Ensure availableDateTimes is loaded
            if (availableDateTimes == null || availableDateTimes.isEmpty()) {
                taxiAvailabilityParser();
            }

            // Check if the dateTime exists in the map
            if (availableDateTimes.containsKey(dateTime)) {
                int currentCap = availableDateTimes.get(dateTime);
                if (currentCap > 0) {
                    availableDateTimes.put(dateTime, currentCap - 1);
                } else {
                    System.out.println("No more available taxis on " + dateTime);
                    return;
                }
            } else {
                // If the dateTime doesn't exist, add a default capacity
                int defaultCapacity = getAvailableCount(); // or any desired default
                availableDateTimes.put(dateTime, defaultCapacity - 1);
            }

            // Update the file
            updateFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void cancelBook(LocalDateTime dateTime) {
        try {
            // Ensure availableDateTimes is loaded
            if (availableDateTimes == null || availableDateTimes.isEmpty()) {
                taxiAvailabilityParser();
            }

            // Check if the dateTime exists in the map
            if (availableDateTimes.containsKey(dateTime)) {
                int currentCap = availableDateTimes.get(dateTime);
                int maxCapacity = getAvailableCount();
                // Ensure we don't exceed the maximum number of taxis
                if (currentCap < maxCapacity) {
                    availableDateTimes.put(dateTime, currentCap + 1);
                    System.out.println("Taxi booking cancelled successfully for " + dateTime);
                } else {
                    System.out.println("Cannot cancel booking - taxi capacity already at maximum for " + dateTime);
                    return;
                }
            } else {
                // If the dateTime doesn't exist, can't cancel a non-existent booking
                System.out.println("No taxi booking record found for " + dateTime);
                return;
            }

            // Update the file
            updateFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    // --- Getters and other methods ---

    public String getCity() {
        return city;
    }

    public String getTaxiType() {
        return taxiType;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public double getPerKmRate() {
        return perKmRate;
    }

    @Override
    public String toString() {
        return taxiType + " " + city;
    }

    public int getId() {
        return id;
    }

    /**
     * Select Taxis by city name.
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
     * Get the availability for a specific dateTime. If dateTime not in map, return default capacity.
     */
    public int getAvailabilityForDateTime(LocalDateTime dateTime) {
        // Ensure the availability data is loaded
        if (availableDateTimes == null || availableDateTimes.isEmpty()) {
            try {
                taxiAvailabilityParser();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Return capacity if present; otherwise the default
        return availableDateTimes.getOrDefault(dateTime, getAvailableCount());
    }
}
