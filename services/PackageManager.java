package services;

import Users.Customer;
import Users.User;
import databases.CustomerDB;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class PackageManager {
protected static HashMap<Integer,Package> packageDict;
private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
private static int newID;
    public static void packageDictGenerator() {
        try {
            File file = new File("services/packages.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineSep = (line.split(","));
                int id = Integer.parseInt(line.split(",")[0]); // 0 is assumed the pack id
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
        if (packageDict == null) {
            packageDictGenerator();
        } else {
            int lastID = 400000;
            for (int id : packageDict.keySet()) {
                lastID = id;
            }
            newID = lastID + 1;
        }
    }

    public static void makePackage(String type,int hotelID,int flightID,int taxiID,LocalDate dateStart,LocalDate dateEnd){
        idGenerator();
        Package newPack = new Package(type,hotelID,flightID,taxiID,dateStart,dateEnd);
        packageDict.put(newID,newPack);
        updatePackagesFile();

    }

    private static void updatePackagesFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("services/packages.txt"))) {
            for (int id : packageDict.keySet()) {
                Package pck = packageDict.get(id);
                writer.write(id + "," +pck.getType()+","+pck.getHotel().getId()+"," +pck.getFlight().getId()+
                        ","+pck.getTaxi().getId()+","+ pck.getTotalCost()+","+pck.getDateStart().format(formatter)+
                        ","+pck.getDateEnd().format(formatter));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating packages file: " + e.getMessage());
        }
    }
}
