package services;

import products.Flight;
import products.Hotel;
import products.Taxi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TravelParser {
//creates objects of products from the corresponding lines in .csv files
private static HashMap<Integer,Hotel> hotelsDict;
private static HashMap<Integer,Flight> flightsDict;
private static HashMap<Integer, Taxi> taxisDict;
    public static void parseHotels() {
        HashMap<Integer, Hotel> hotelDict = new HashMap<>();
        int uniqueId = 100000; // Initialize the unique ID counter

        try {
            File file = new File("datasets/FinalKU_Travel_Agency_Dataset_Hotels.csv");
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataArray = data.split(",");

                if (dataArray.length >= 6) { // Ensure the array has enough elements to avoid errors
                    String name = dataArray[0];
                    String city = dataArray[1];
                    String roomType = dataArray[2];
                    int availableCount = Integer.parseInt(dataArray[3]);
                    int pricePerNight = Integer.parseInt(dataArray[4]);
                    int distanceToAirport = Integer.parseInt(dataArray[5]);

                    Hotel hotel = new Hotel(name, city, roomType, availableCount, pricePerNight, distanceToAirport);
                    hotelDict.put(uniqueId, hotel);

                    uniqueId++; // Increment the unique ID for the next hotel
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

    public static void parseFlights() {
        HashMap<Integer, Flight> flightDict = new HashMap<>();
        int uniqueId = 200000; // Initialize the unique ID counter

        try {
            File file = new File("datasets/FinalKU_Travel_Agency_Dataset_Flights.csv");
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataArray = data.split(",");

                if (dataArray.length >= 9) { // Ensure the array has enough elements to avoid errors
                    String flightID = dataArray[0];
                    String airline = dataArray[1];
                    String departureCity = dataArray[2];
                    String ticketClass = dataArray[6];
                    int price = Integer.parseInt(dataArray[7]);
                    int availableSeats = Integer.parseInt(dataArray[8]);

                    if (dataArray[3].isEmpty()) {
                        // Stopover flight
                        String stopoverCity = dataArray[9];
                        String finalArrivalCity = dataArray[10];
                        String leg1DepartureTime = dataArray[11];
                        String leg1ArrivalTime = dataArray[12];
                        String leg2DepartureTime = dataArray[13];
                        String leg2ArrivalTime = dataArray[14];

                        Flight flight = new Flight(
                                flightID,
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
                                price
                        );
                        flightDict.put(uniqueId, flight);
                    } else {
                        // Direct flight
                        String arrivalCity = dataArray[3];
                        String departureTime = dataArray[4];
                        String arrivalTime = dataArray[5];

                        Flight flight = new Flight(
                                flightID,
                                airline,
                                departureCity,
                                arrivalCity,
                                availableSeats,
                                departureTime,
                                arrivalTime,
                                ticketClass,
                                price
                        );
                        flightDict.put(uniqueId, flight);
                    }

                    uniqueId++; // Increment the unique ID for the next flight
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
    public static void parseTaxis() {
        HashMap<Integer, Taxi> taxiDict = new HashMap<>();
        int uniqueId = 300000; // Initialize the unique ID counter

        try {
            File file = new File("datasets/FinalKU_Travel_Agency_Dataset_Taxis.csv");
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataArray = data.split(",");

                if (dataArray.length == 5) { // Ensure the array has exactly 5 elements
                    String city = dataArray[0];
                    String taxiType = dataArray[1];
                    int availableTaxis = Integer.parseInt(dataArray[2]);
                    double baseFare = Double.parseDouble(dataArray[3]);
                    double perKmRate = Double.parseDouble(dataArray[4]);

                    Taxi taxi = new Taxi(city, taxiType, availableTaxis, baseFare, perKmRate);
                    taxiDict.put(uniqueId, taxi);

                    uniqueId++; // Increment the unique ID for the next taxi
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

    public static Map<Integer, Hotel> getHotelsDict() {
        if (hotelsDict == null) {
            parseHotels();
        }
        return hotelsDict;
    }

    public static Map<Integer, Flight> getFlightsDict() {
        if (flightsDict == null) {
            parseFlights();
        }
        return flightsDict;
    }

    public static Map<Integer, Taxi> getTaxisDict() {
        if (taxisDict == null) {
            parseTaxis();
        }
        return taxisDict;
    }

}
