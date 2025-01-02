package services;

import products.Flight;
import products.Hotel;
import products.Product;
import products.Taxi;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Manages travel packages including creation, modification, and persistence of package data.
 * Handles package operations such as creating, editing, and retrieving travel packages.
 */
public class PackageManager {
    public static HashMap<Integer,Package> packageDict;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static int newID=400000;

    /**
     * Loads package data from file into packageDict HashMap.
     */
    public static void packageDictGenerator() {
        packageDict = new HashMap<Integer,Package>();
        try {
            File file = new File("services/packages.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] lineSep = line.split(",");

                if (lineSep.length < 8) {
                    continue;
                }
                int id = Integer.parseInt(lineSep[0]);

                Package aPackage = new Package(lineSep[1],Integer.parseInt(lineSep[2]),Integer.parseInt(lineSep[3])
                        ,Integer.parseInt(lineSep[4]),LocalDate.parse(lineSep[7],formatter),LocalDate.parse(lineSep[8],formatter),LocalDateTime.parse(lineSep[9],dateTimeFormatter),id);

                aPackage.setDiscountedPrice(Integer.parseInt(lineSep[6]));
                packageDict.put(id, aPackage);
            }
            scanner.close();
        } catch (FileNotFoundException f) {
            System.out.println(f);
        }
    }

    /**
     * Marks a package as "not offered" instead of physically deleting it.
     * @param packageId ID of the package to delete
     */
    public static void deletePackage(int packageId) {
        if (packageDict == null) {
            packageDict = new HashMap<>();
            packageDictGenerator();
        }

        Package toDelete = packageDict.get(packageId);

        if (toDelete == null) {
            System.out.println("No package with ID " + packageId + " found.");
            return;
        }

        toDelete.setType("not offered");
        updatePackagesFile();
        System.out.println("Package " + packageId + " is now marked as 'not offered'.");
    }

    /**
     * Returns all packages that are currently being offered.
     * @return HashMap of active packages with their IDs as keys
     */
    public static HashMap<Integer,Package> getActivePackages(){
        packageDictGenerator();
        HashMap<Integer,Package> returnDict = new HashMap<Integer,Package>();
        for(Package p: packageDict.values()) {
            if(!p.getType().equals("not offered")){
                returnDict.put(p.getId(),p);
            }
        }
        return returnDict;
    }

    /**
     * @return The next available package ID
     */
    public static int getNewID(){
        return newID;
    }

    /**
     * Retrieves a specific package by its ID.
     * @param packageId ID of the package to retrieve
     * @return The requested Package object or null if not found
     */
    public static Package retrievePackage(int packageId) {
        packageDictGenerator();
        return packageDict.get(packageId);
    }

    /**
     * Generates the next available package ID.
     */
    public static void idGenerator() {
        if (packageDict == null|| packageDict.isEmpty()) {
            packageDictGenerator();
        }
        int lastID = 400000;
        lastID = lastID+ packageDict.size();
        newID = lastID + 1;
    }

    /**
     * Creates a new package with the specified components.
     * @param type Package type
     * @param hotelID Hotel component ID
     * @param flightID Flight component ID
     * @param taxiID Taxi component ID
     * @param dateStart Start date of the package
     * @param dateEnd End date of the package
     * @param taxiTime Scheduled taxi pickup time
     * @return The newly created Package object
     */
    public static Package makePackage(String type, int hotelID, int flightID, int taxiID, LocalDate dateStart, LocalDate dateEnd, LocalDateTime taxiTime){
        idGenerator();
        Package newPack = new Package(type,hotelID,flightID,taxiID,dateStart,dateEnd,taxiTime,newID);
        newPack.setId(newID);
        packageDict.put(newID,newPack);
        updatePackagesFile();
        return newPack;
    }

    /**
     * Creates a copy of an existing package with a new ID.
     * @param pck Package to duplicate
     * @return The duplicated Package object
     */
    public static Package duplicatePackage(Package pck){
        idGenerator();
        Package newPack = new Package(pck);
        newPack.setId(newID);
        packageDict.put(newID,newPack);
        updatePackagesFile();
        return newPack;
    }

    /**
     * Updates the packages.txt file with current package data.
     */
    public static void updatePackagesFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("services/packages.txt"))) {
            for (int id : packageDict.keySet()) {
                Package pck = packageDict.get(id);
                writer.write(id + "," + pck.getType() + "," + pck.getHotel().getId() + ","
                        + pck.getFlight().getId() + "," + pck.getTaxi().getId() + ","
                        + pck.getTotalCost() + "," + pck.getDiscountedPrice() + "," + pck.getDateStart().format(formatter) + ","
                        + pck.getDateEnd().format(formatter) + "," + pck.getTaxiTime().format(dateTimeFormatter));
                writer.newLine();
            }
            System.out.println("Packages successfully written to file.");
        } catch (IOException e) {
            System.out.println("Error updating packages file: " + e.getMessage());
        }
    }

    /**
     * Creates a new package based on an existing one with updated components.
     * @param packageId ID of the package to edit
     * @param newHotelId New hotel ID (optional)
     * @param newFlightId New flight ID (optional)
     * @param newTaxiId New taxi ID (optional)
     * @param newDateStart New start date (optional)
     * @param newDateEnd New end date (optional)
     * @return The newly created Package object with updated components
     */
    public static Package editPackage(int packageId, Integer newHotelId, Integer newFlightId, Integer newTaxiId,
                                      LocalDate newDateStart, LocalDate newDateEnd) {
        PackageManager.packageDictGenerator();
        Package currentPackage = packageDict.get(packageId);
        if (currentPackage == null) {
            System.out.println("Package not found: " + packageId);
            return null;
        }

        packageDict.get(packageId).setType("not offered");
        updatePackagesFile();

        int hotelId = newHotelId != null ? newHotelId : currentPackage.getHotel().getId();
        int flightId = newFlightId != null ? newFlightId : currentPackage.getFlight().getId();
        int taxiId = newTaxiId != null ? newTaxiId : currentPackage.getTaxi().getId();
        LocalDate dateStart = newDateStart != null ? newDateStart : currentPackage.getDateStart();
        LocalDate dateEnd = newDateEnd != null ? newDateEnd : currentPackage.getDateEnd();
        LocalDateTime taxiTime = LocalDateTime.of(dateStart, Flight.retrieveFlight(flightId).getArrivalTime());

        Package newPackage = makePackage("offered", hotelId, flightId, taxiId, dateStart, dateEnd, taxiTime);
        updatePackagesFile();

        return newPackage;
    }
}