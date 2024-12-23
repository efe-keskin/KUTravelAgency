package products;

import services.TravelParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Taxi extends Product {
    private int id;
    private String city;
    private String taxiType;
    private double baseFare;
    private double perKmRate;

    // 1) A map for storing dates and availability (similar to hotels)
    private HashMap<LocalDate, Integer> availableDates;

    public Taxi(String city, String taxiType, int availableCount, double baseFare, double perKmRate, int id) {
        super(availableCount);
        this.city = city;
        this.taxiType = taxiType;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.id = id;

        // Initialize the map to avoid NullPointerExceptions
        this.availableDates = new HashMap<>();
    }

    /**
     * Retrieves the Taxi object from TravelParser's dictionary by ID.
     */
    public static Taxi retrieveTaxi(int id) {
        return TravelParser.getTaxisDict().get(id);
    }

    /**
     * 2) Reads 'taxiavailability.txt' and populates the availableDates map
     *    with (date -> capacity) entries for this taxi's ID.
     */
    public void taxiAvailabilityParser() throws FileNotFoundException {
        File file = new File("products/taxiavailability.txt");
        Scanner reader = new Scanner(file);

        // Clear out any existing data to avoid duplicates
        this.availableDates.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] dataArray = line.split(",");
            // Make sure the line has at least 3 parts: id, date, capacity
            if (dataArray.length < 3) {
                continue; // skip malformed lines
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDate date = LocalDate.parse(dataArray[1], formatter);
            int capacity = Integer.parseInt(dataArray[2]);

            // Only load data for this particular taxi
            if (lineId == this.id) {
                this.availableDates.put(date, capacity);
            }
        }
        reader.close();
    }

    /**
     * 3) Rewrites 'taxiavailability.txt' to reflect the updated availability
     *    for all taxis, including this one.
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> updatedLines = new ArrayList<>();
        // We'll track which LocalDates we’ve updated so we don't add duplicates
        Set<LocalDate> updatedDates = new HashSet<>();

        // 2) Loop through existing lines
        for (String line : lines) {
            String[] dataArray = line.split(",");
            if (dataArray.length < 3) {
                // If line is malformed, just skip or keep it as is
                updatedLines.add(line);
                continue;
            }

            int lineId = Integer.parseInt(dataArray[0]);
            LocalDate lineDate = LocalDate.parse(dataArray[1], formatter);
            int lineCapacity = Integer.parseInt(dataArray[2]);

            // If this line belongs to our taxi and date is in our map, replace it
            if (lineId == this.id && availableDates.containsKey(lineDate)) {
                int newCapacity = availableDates.get(lineDate);
                updatedLines.add(this.id + "," + lineDate.format(formatter) + "," + newCapacity);
                updatedDates.add(lineDate);
            } else {
                // Otherwise, keep the existing line
                updatedLines.add(line);
            }
        }

        // 3) Add lines for any date in availableDates not found in original file
        for (LocalDate date : availableDates.keySet()) {
            if (!updatedDates.contains(date)) {
                int newCapacity = availableDates.get(date);
                updatedLines.add(this.id + "," + date.format(formatter) + "," + newCapacity);
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
     * 4) Books a taxi on a specific date (similar to hotel bookings).
     *    - Loads availability if not already loaded.
     *    - Checks if the date has capacity > 0; if so, decrement.
     *    - If date doesn't exist, sets a default capacity, then decrements.
     *    - Updates the file at the end.
     */
    public void book(LocalDate date) {
        try {
            // Ensure availableDates is loaded
            if (availableDates == null || availableDates.isEmpty()) {
                taxiAvailabilityParser();
            }

            // Check if the date exists in the dictionary
            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                // If there's capacity, decrement
                if (currentCap > 0) {
                    availableDates.put(date, currentCap - 1);
                } else {
                    System.out.println("No more available taxis on " + date);
                    return;
                }
            } else {
                // If the date doesn't exist, add a default capacity
                int defaultCapacity = getAvailableCount(); // adjust this value as needed
                availableDates.put(date, defaultCapacity - 1);
            }

            // Finally, update the file to reflect the changes
            updateFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // -- Getters & Setters & toString -- //

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
}
