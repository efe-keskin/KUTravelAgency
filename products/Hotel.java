package products;

import services.TravelParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Scanner;


public class Hotel extends Product{
    private int id;
    private String name;
    private String city;
    private String roomType;
    private int pricePerNight;
    private int distanceToAirport;
    private HashMap<LocalDate,Integer> availableDates;


    public Hotel(String name,String city,String roomType, int availableCount, int pricePerNight, int distanceToAirport,int id) {
        super(availableCount);
        this.name = name;
        this.city = city;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.distanceToAirport = distanceToAirport;
        this.availableDates = new HashMap<>();
        this.id = id;

    }
    public static Hotel retrieveHotel(int id){
        return TravelParser.getHotelsDict().get(id);

    }
    public void hotelAvailabilityParser() throws FileNotFoundException {
        File file = new File("products/hotelavailability.txt");
        Scanner reader = new Scanner(file);
        this.availableDates.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            String[] dataArray = data.split(",");
            int id = Integer.parseInt(dataArray[0]);
            LocalDate date = LocalDate.parse(dataArray[1], formatter);
            int capacity = Integer.parseInt(dataArray[2]);
            if (id == this.id) {
                this.availableDates.put(date, capacity);
            }


        }
        reader.close();
    }
    public void updateFile() throws FileNotFoundException {
        // 1) Read all lines from the original file
        File file = new File("products/hotelavailability.txt");
        java.util.List<String> lines = new java.util.ArrayList<>();
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
        }

        // 2) Prepare for parsing and rewriting
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        java.util.List<String> updatedLines = new java.util.ArrayList<>();
        // We'll track which LocalDates we’ve already updated so we don't add duplicates later
        java.util.Set<java.time.LocalDate> updatedDates = new java.util.HashSet<>();

        // 3) Loop through existing lines
        for (String line : lines) {
            String[] dataArray = line.split(",");
            int lineId = Integer.parseInt(dataArray[0]);
            java.time.LocalDate lineDate = java.time.LocalDate.parse(dataArray[1], formatter);
            int lineCapacity = Integer.parseInt(dataArray[2]);

            // If this line belongs to the same hotel (id) and date is in our availableDates, we replace it
            if (lineId == this.id && availableDates.containsKey(lineDate)) {
                int newCapacity = availableDates.get(lineDate);
                updatedLines.add(this.id + "," + lineDate.format(formatter) + "," + newCapacity);
                updatedDates.add(lineDate);
            } else {
                // Otherwise, keep the existing line
                updatedLines.add(line);
            }
        }

        // 4) Add lines for any date in availableDates that didn’t appear in the original file
        for (LocalDate date : availableDates.keySet()) {
            if (!updatedDates.contains(date)) {
                int newCapacity = availableDates.get(date);
                updatedLines.add(this.id + "," + date.format(formatter) + "," + newCapacity);
            }
        }

        // 5) Rewrite the entire file with updated lines
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.println(updatedLine);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    public void book(LocalDate date) {
        try {
            // 1) Ensure availableDates is loaded
            if (availableDates == null || availableDates.isEmpty()) {
                hotelAvailabilityParser();
            }

            // 2) Check if the date exists in the dictionary
            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                // 3) Check if there's at least 1 available room
                if (currentCap > 0) {
                    availableDates.put(date, currentCap - 1);
                } else {
                    // No rooms left, just return or handle accordingly
                    System.out.println("No more available rooms on " + date);
                    return;
                }
            } else {
                // 4) If this date doesn't exist, add a default capacity and deduct 1
                int defaultCapacity = getAvailableCount(); // can be any default capacity you want
                availableDates.put(date, defaultCapacity - 1);
            }

            // 5) Update the file to reflect the new availability
            updateFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }







    @Override
    public String toString() {
        return name + " " + city + " " + roomType;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public int getDistanceToAirport() {
        return distanceToAirport;
    }
    public int getId(){
        return id;
    }

}
