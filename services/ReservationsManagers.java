package services;

import Users.Customer;
import Users.User;
import core.App;
import databases.CustomerDB;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ReservationsManagers {
    protected static HashMap<Integer, Reservation> reservationsDict;
    private static Integer newID;

    //Gives reservations their ids and makes them
    public static void reservationsDictGenerator() {
        try {
            File file = new File("services/reservations.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineSep = (line.split(","));
                int id = Integer.parseInt(line.split(",")[0]); // 0 is assumed the reservation id
                Customer cust = CustomerDB.retrieveCustomer((lineSep[4]));//4 is assumed the customer id
                Reservation res = new Reservation(id, new Package(Integer.parseInt(lineSep[1]), //1 is assumed hotel id
                        Integer.parseInt(lineSep[2]),Integer.parseInt(lineSep[3])),cust );//2 is assumed flight id, 3 is assumed taxi id
                reservationsDict.put(id, res);
            }
            scanner.close();
        } catch (FileNotFoundException f) {
            System.out.println(f);
        }

    }

    public static void idGenerator() {
        if (reservationsDict == null) {
            reservationsDictGenerator();
        } else {
            int lastID = 0;
            for (int id : reservationsDict.keySet()) {
                lastID = id;
            }
            newID = lastID + 1;
        }


    }
    public static void makeReservation(Package pck, User user){
    idGenerator();
    Reservation newRes = new Reservation(newID,pck,user);
    newRes.setStatus(true);
    reservationsDict.put(newID,newRes);
    updateReservationsFile();

    }

    private static void updateReservationsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("services/reservations.txt"))) {
            for (int id : reservationsDict.keySet()) {
                Reservation res = reservationsDict.get(id);
                writer.write(id + "," +res.getRelatedPackage()+ ","
                        +res.getCustomer().getID()+"," +res.getRelatedPackage().getId()
                        +","+res);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating reservations file: " + e.getMessage());
        }
    }



}











