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

public class PackageManager {
public static HashMap<Integer,Package> packageDict;
private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
private static int newID=400000;
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
    public static void deletePackage(int packageId) {
        // Make sure packageDict is loaded
        if (packageDict == null) {
            packageDict = new HashMap<>();
            packageDictGenerator();
        }

        // Retrieve the package
        Package toDelete = packageDict.get(packageId);

        if (toDelete == null) {
            System.out.println("No package with ID " + packageId + " found.");
            return;
        }

        // Set its type to "not offered"
        toDelete.setType("not offered");

        // Rewrite the packages file to persist changes
        updatePackagesFile();

        System.out.println("Package " + packageId + " is now marked as 'not offered'.");
    }
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
    public static Package retrievePackage(int packageId) {
        // Make sure packageDict is initialized

            packageDictGenerator();  // loads from packages.txt


        // Retrieve the package from the dictionary
        return packageDict.get(packageId);
    }
    public static void idGenerator() {
        if (packageDict == null|| packageDict.isEmpty()) {
            packageDictGenerator();
        }
            int lastID = 400000;
            lastID = lastID+ packageDict.size();
            newID = lastID + 1;

    }


    public static Package makePackage(String type, int hotelID, int flightID, int taxiID, LocalDate dateStart, LocalDate dateEnd, LocalDateTime taxiTime){
        idGenerator();
        Package newPack = new Package(type,hotelID,flightID,taxiID,dateStart,dateEnd,taxiTime,newID);
        newPack.setId(newID);
        packageDict.put(newID,newPack);
        updatePackagesFile();
        return newPack;

    }

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


    public static Package editPackage(int packageId, Integer newHotelId, Integer newFlightId, Integer newTaxiId,
                                      LocalDate newDateStart, LocalDate newDateEnd) {
        PackageManager.packageDictGenerator();
        Package currentPackage = packageDict.get(packageId);
        if (currentPackage == null) {
            System.out.println("Package not found: " + packageId);
            return null;
        }

        // Mark the current package as "not offered" before creating the new one
        packageDict.get(packageId).setType("not offered");
        updatePackagesFile();
        // Use existing values if new ones aren't provided
        int hotelId = newHotelId != null ? newHotelId : currentPackage.getHotel().getId();
        int flightId = newFlightId != null ? newFlightId : currentPackage.getFlight().getId();
        int taxiId = newTaxiId != null ? newTaxiId : currentPackage.getTaxi().getId();
        LocalDate dateStart = newDateStart != null ? newDateStart : currentPackage.getDateStart();
        LocalDate dateEnd = newDateEnd != null ? newDateEnd : currentPackage.getDateEnd();
        LocalDateTime taxiTime = LocalDateTime.of(dateStart, Flight.retrieveFlight(flightId).getArrivalTime());

        // Create the new package with updated components
        Package newPackage = makePackage("offered", hotelId, flightId, taxiId, dateStart, dateEnd, taxiTime);

        // Save the updated package details
        updatePackagesFile();

        return newPackage;
    }

}
