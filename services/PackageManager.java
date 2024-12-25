package services;

import products.Flight;
import products.Hotel;
import products.Product;
import products.Taxi;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PackageManager {
protected static HashMap<Integer,Package> packageDict;
private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                        ,Integer.parseInt(lineSep[4]),LocalDate.parse(lineSep[6],formatter),LocalDate.parse(lineSep[7],formatter));
                packageDict.put(id, aPackage);
            }
            scanner.close();
        } catch (FileNotFoundException f) {
            System.out.println(f);
        }

    }
    public static Package retrievePackage(int packageId) {
        // Make sure packageDict is initialized
        if (packageDict == null) {
            packageDict = new HashMap<>();
            packageDictGenerator();  // loads from packages.txt
        }

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


    public static Package makePackage(String type, int hotelID, int flightID, int taxiID, LocalDate dateStart, LocalDate dateEnd){
        idGenerator();
        Package newPack = new Package(type,hotelID,flightID,taxiID,dateStart,dateEnd);
        newPack.setId(newID);
        packageDict.put(newID,newPack);
        updatePackagesFile();
        return newPack;

    }

    private static void updatePackagesFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("services/packages.txt"))) {
            for (int id : packageDict.keySet()) {
                Package pck = packageDict.get(id);
                writer.write(id + "," + pck.getType() + "," + pck.getHotel().getId() + ","
                        + pck.getFlight().getId() + "," + pck.getTaxi().getId() + ","
                        + pck.getTotalCost() + "," + pck.getDateStart().format(formatter) + ","
                        + pck.getDateEnd().format(formatter));
                writer.newLine();
            }
            System.out.println("Packages successfully written to file.");
        } catch (IOException e) {
            System.out.println("Error updating packages file: " + e.getMessage());
        }
    }
    private static void appendPackageToFile(Package pck, int id) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("services/packages.txt", true))) { // 'true' enables append mode
            writer.write(id + "," + pck.getType() + "," + pck.getHotel().getId() + ","
                    + pck.getFlight().getId() + "," + pck.getTaxi().getId() + ","
                    + pck.getTotalCost() + "," + pck.getDateStart().format(formatter) + ","
                    + pck.getDateEnd().format(formatter));
            writer.newLine();
            System.out.println("Package successfully appended to file.");
        } catch (IOException e) {
            System.out.println("Error appending package to file: " + e.getMessage());
        }
    }

}
