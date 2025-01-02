package services;

import products.Flight;
import products.Hotel;
import products.Taxi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Parses travel-related data from CSV files and creates product objects for hotels, flights, and taxis.
 */
public class TravelParser {
    private static HashMap<Integer, Hotel> hotelsDict;
    private static HashMap<Integer, Flight> flightsDict;
    private static HashMap<Integer, Taxi> taxisDict;

    /**
     * Parses hotel data from the hotels dataset CSV file and populates the hotels dictionary.
     */
    public static void parseHotels() {
        HashMap<Integer, Hotel> hotelDict = new HashMap<>();
        int uniqueId = 100000;

        try {
            File file = new File("datasets/FinalKU_Travel_Agency_Dataset_Hotels.csv");
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()) {
                myReader.nextLine();
            }
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataArray = data.split(",");

                if (dataArray.length >= 6) {
                    String name = dataArray[0];
                    String city = dataArray[1];
                    String roomType = dataArray[2];
                    int availableCount = Integer.parseInt(dataArray[3]);
                    double pricePerNight = Double.parseDouble(dataArray[4]);
                    double distanceToAirport = Double.parseDouble(dataArray[5]);

                    Hotel hotel = new Hotel(name, city, roomType, availableCount, pricePerNight, distanceToAirport, uniqueId);
                    hotelDict.put(uniqueId, hotel);

                    uniqueId++;
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e);
        }
        hotelsDict = hotelDict;
    }

    /**
     * Parses flight data from the flights dataset CSV file and populates the flights dictionary.
     */
    public static void parseFlights() {
        HashMap<Integer, Flight> flightDict = new HashMap<>();
        int uniqueId = 200000;

        try {
            File file = new File("datasets/FinalKU_Travel_Agency_Dataset_Flights.csv");
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()) {
                myReader.nextLine();
            }
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataArray = data.split(",");

                if (dataArray.length >= 9) {
                    String flightID = dataArray[0];
                    String airline = dataArray[1];
                    String departureCity = dataArray[2];
                    String ticketClass = dataArray[6];
                    double price = Double.parseDouble(dataArray[7]);
                    int availableSeats = Integer.parseInt(dataArray[8]);

                    if (dataArray[3].isEmpty()) {
                        String stopoverCity = dataArray[9];
                        String finalArrivalCity = dataArray[10];
                        String leg1DepartureTime = dataArray[11];
                        String leg1ArrivalTime = dataArray[12];
                        String leg2DepartureTime = dataArray[13];
                        String leg2ArrivalTime = dataArray[14];

                        Flight flight = new Flight(
                                airline,
                                departureCity,
                                stopoverCity,
                                finalArrivalCity,
                                leg1DepartureTime,
                                leg1ArrivalTime,
                                leg2DepartureTime,
                                leg2ArrivalTime,
                                availableSeats,
                                ticketClass,
                                price, uniqueId
                        );
                        flightDict.put(uniqueId, flight);
                    } else {
                        String arrivalCity = dataArray[3];
                        String departureTime = dataArray[4];
                        String arrivalTime = dataArray[5];

                        Flight flight = new Flight(
                                airline,
                                departureCity,
                                arrivalCity,
                                availableSeats,
                                departureTime,
                                arrivalTime,
                                ticketClass,
                                price, uniqueId
                        );
                        flightDict.put(uniqueId, flight);
                    }

                    uniqueId++;
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e);
        }
        flightsDict = flightDict;
    }

    /**
     * Parses taxi data from the taxis dataset CSV file and populates the taxis dictionary.
     */
    public static void parseTaxis() {
        HashMap<Integer, Taxi> taxiDict = new HashMap<>();
        int uniqueId = 300000;

        try {
            File file = new File("datasets/FinalKU_Travel_Agency_Dataset_Taxis.csv");
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()) {
                myReader.nextLine();
            }
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataArray = data.split(",");

                if (dataArray.length == 5) {
                    String city = dataArray[0];
                    String taxiType = dataArray[1];
                    int availableTaxis = Integer.parseInt(dataArray[2]);
                    double baseFare = Double.parseDouble(dataArray[3]);
                    double perKmRate = Double.parseDouble(dataArray[4]);

                    Taxi taxi = new Taxi(city, taxiType, availableTaxis, baseFare, perKmRate, uniqueId);
                    taxiDict.put(uniqueId, taxi);

                    uniqueId++;
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e);
        }
        taxisDict = taxiDict;
    }

    /**
     * Retrieves the hotels dictionary, parsing data if not already loaded.
     *
     * @return A map of hotel IDs to hotel objects.
     */
    public static Map<Integer, Hotel> getHotelsDict() {
        if (hotelsDict == null) {
            parseHotels();
        }
        return hotelsDict;
    }

    /**
     * Retrieves the flights dictionary, parsing data if not already loaded.
     *
     * @return A map of flight IDs to flight objects.
     */
    public static Map<Integer, Flight> getFlightsDict() {
        if (flightsDict == null) {
            parseFlights();
        }
        return flightsDict;
    }

    /**
     * Retrieves the taxis dictionary, parsing data if not already loaded.
     *
     * @return A map of taxi IDs to taxi objects.
     */
    public static Map<Integer, Taxi> getTaxisDict() {
        if (taxisDict == null) {
            parseTaxis();
        }
        return taxisDict;
    }
}
