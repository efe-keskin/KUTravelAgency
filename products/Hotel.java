package products;

import core.App;
import reservationlogs.Logger;
import services.TravelParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Represents a hotel with attributes such as name, city, room type, and availability.
 * Handles booking and cancellation operations with file-based persistence.
 */
public class Hotel extends Product {
    private int id;
    private String name;
    private String city;
    private String roomType;
    private double pricePerNight;
    private double distanceToAirport;
    private HashMap<LocalDate, Integer> availableDates;

    /**
     * Constructs a Hotel instance.
     *
     * @param name             The name of the hotel.
     * @param city             The city where the hotel is located.
     * @param roomType         The type of rooms available.
     * @param availableCount   Initial number of available rooms.
     * @param pricePerNight    Price per night for a room.
     * @param distanceToAirport Distance from the hotel to the nearest airport.
     * @param id               Unique identifier for the hotel.
     */
    public Hotel(String name, String city, String roomType, int availableCount,
                 double pricePerNight, double distanceToAirport, int id) {
        super(availableCount);
        this.name = name;
        this.city = city;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.distanceToAirport = distanceToAirport;
        this.availableDates = new HashMap<>();
        this.id = id;
    }

    /**
     * Retrieves a Hotel by its ID from the TravelParser.
     *
     * @param id The unique identifier of the hotel.
     * @return The corresponding Hotel object.
     */
    public static Hotel retrieveHotel(int id) {
        return TravelParser.getHotelsDict().get(id);
    }

    /**
     * Parses hotel availability data from a file and populates the availability map.
     *
     * @throws FileNotFoundException If the file is not found.
     */
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

    /**
     * Filters hotels by city.
     *
     * @param city The city to filter hotels by.
     * @return A list of hotels located in the specified city.
     */
    public static ArrayList<Hotel> selectByCity(String city) {
        TravelParser.parseHotels();
        ArrayList<Hotel> hotelsInCity = new ArrayList<>();
        for (Hotel hotel : TravelParser.getHotelsDict().values()) {
            if (hotel.getCity().equalsIgnoreCase(city)) {
                hotelsInCity.add(hotel);
            }
        }
        return hotelsInCity;
    }

    /**
     * Updates the hotel availability file with current data.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    public void updateFile() throws FileNotFoundException {
        File file = new File("products/hotelavailability.txt");
        java.util.List<String> lines = new java.util.ArrayList<>();

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.util.List<String> updatedLines = new java.util.ArrayList<>();
        java.util.Set<LocalDate> updatedDates = new java.util.HashSet<>();

        for (String line : lines) {
            String[] dataArray = line.split(",");
            int lineId = Integer.parseInt(dataArray[0]);
            LocalDate lineDate = LocalDate.parse(dataArray[1], formatter);
            int lineCapacity = Integer.parseInt(dataArray[2]);

            if (lineId == this.id && availableDates.containsKey(lineDate)) {
                int newCapacity = availableDates.get(lineDate);
                updatedLines.add(this.id + "," + lineDate.format(formatter) + "," + newCapacity);
                updatedDates.add(lineDate);
            } else {
                updatedLines.add(line);
            }
        }

        for (LocalDate date : availableDates.keySet()) {
            if (!updatedDates.contains(date)) {
                int newCapacity = availableDates.get(date);
                updatedLines.add(this.id + "," + date.format(formatter) + "," + newCapacity);
            }
        }

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.println(updatedLine);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Books a room for the specified date.
     *
     * @param date The date for which a room is to be booked.
     */
    public void book(LocalDate date) {
        try {
            if (availableDates == null || availableDates.isEmpty()) {
                hotelAvailabilityParser();
            }

            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                if (currentCap > 0) {
                    availableDates.put(date, currentCap - 1);
                } else {
                    System.out.println("No more available rooms on " + date);
                    return;
                }
            } else {
                int defaultCapacity = getAvailableCount();
                availableDates.put(date, defaultCapacity - 1);
            }

            updateFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels a booking for the specified date.
     *
     * @param date The date for which the booking is to be canceled.
     */
    public void cancelBook(LocalDate date) {
        try {
            if (availableDates == null || availableDates.isEmpty()) {
                hotelAvailabilityParser();
            }

            if (availableDates.containsKey(date)) {
                int currentCap = availableDates.get(date);
                int maxCapacity = getAvailableCount();
                if (currentCap < maxCapacity) {
                    availableDates.put(date, currentCap + 1);
                    System.out.println("Booking cancelled successfully for " + date);
                } else {
                    System.out.println("Cannot cancel booking - room capacity already at maximum for " + date);
                    return;
                }
            } else {
                System.out.println("No booking record found for " + date);
                return;
            }

            updateFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters hotels based on availability within a date range.
     *
     * @param dateStart Start date of the range.
     * @param dateEnd   End date of the range.
     * @param hotelList List of hotels to filter.
     * @return A list of hotels available for all dates in the range.
     */
    public static ArrayList<Hotel> availableRoomsListMaker(LocalDate dateStart, LocalDate dateEnd, ArrayList<Hotel> hotelList) {
        ArrayList<Hotel> newHotelList = new ArrayList<>();

        if (dateEnd.isBefore(dateStart)) {
            return newHotelList;
        }

        for (Hotel hotel : hotelList) {
            boolean allDatesAvailable = true;

            for (LocalDate d = dateStart; !d.isAfter(dateEnd); d = d.plusDays(1)) {
                if (hotel.getAvailabilityForDate(d) <= 0) {
                    allDatesAvailable = false;
                    break;
                }
            }

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

    public int getId() {
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