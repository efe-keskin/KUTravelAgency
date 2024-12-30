package products;

import services.TravelParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Hotel extends Product{
    private int id;
    private String name;
    private String city;
    private String roomType;
    private double pricePerNight;
    private double distanceToAirport;
    private HashMap<LocalDate,Integer> availableDates;


    public Hotel(String name,String city,String roomType, int availableCount, double pricePerNight, double distanceToAirport,int id) {
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
    public static ArrayList<Hotel> selectByCity(String city) {
        TravelParser.parseHotels();
        // Create a list to store all matching hotels
        ArrayList<Hotel> hotelsInCity = new ArrayList<>();

        // Iterate through all hotels in the TravelParser's dictionary
        for (Hotel hotel : TravelParser.getHotelsDict().values()) {
            // Compare city names, ignoring case to make the search more robust
            if (hotel.getCity().equalsIgnoreCase(city)) {
                hotelsInCity.add(hotel);
            }
        }

        return hotelsInCity;
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
    public void cancelBook(LocalDate date) {
        try {
            // 1) Ensure availableDates is loaded
            if (availableDates == null || availableDates.isEmpty()) {
                hotelAvailabilityParser();
            }

            // 2) Check if the date exists in the dictionary
            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                // 3) Add 1 to available capacity
                int maxCapacity = getAvailableCount();
                // Ensure we don't exceed the maximum capacity
                if (currentCap < maxCapacity) {
                    availableDates.put(date, currentCap + 1);
                    System.out.println("Booking cancelled successfully for " + date);
                } else {
                    System.out.println("Cannot cancel booking - room capacity already at maximum for " + date);
                    return;
                }
            } else {
                // 4) If this date doesn't exist, something is wrong
                System.out.println("No booking record found for " + date);
                return;
            }

            // 5) Update the file to reflect the new availability
            updateFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Hotel> availableRoomsListMaker(LocalDate dateStart,
                                                  LocalDate dateEnd,
                                                  ArrayList<Hotel> hotelList)
    {
        ArrayList<Hotel> newHotelList = new ArrayList<>();

        // If dateEnd is before dateStart, just return an empty list
        if (dateEnd.isBefore(dateStart)) {
            return newHotelList;
        }

        // Check each hotel in the provided list
        for (Hotel hotel : hotelList) {
            boolean allDatesAvailable = true;

            // Loop from dateStart to dateEnd (inclusive)
            for (LocalDate d = dateStart; !d.isAfter(dateEnd); d = d.plusDays(1)) {
                // If the hotel has 0 or fewer rooms for any date in the range, it fails
                if (hotel.getAvailabilityForDate(d) <= 0) {
                    allDatesAvailable = false;
                    break; // No need to check further dates for this hotel
                }
            }

            // If the hotel had >0 availability on every date in the range, add it to the list
            if (allDatesAvailable) {
                newHotelList.add(hotel);
            }
        }

        return newHotelList;
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

    public double getPricePerNight() {
        return pricePerNight;
    }

    public double getDistanceToAirport() {
        return distanceToAirport;
    }
    public int getId(){
        return id;
    }
    public int getAvailabilityForDate(LocalDate date) {
        if (availableDates == null || availableDates.isEmpty()) {
            try {
                hotelAvailabilityParser();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return availableDates.getOrDefault(date, this.getAvailableCount());
    }
}
