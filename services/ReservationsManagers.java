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
                Package p = PackageManager.retrievePackage(Integer.parseInt(lineSep[1]));
                Customer cust = CustomerDB.retrieveCustomer((lineSep[2]));//2 is assumed the customer id, 1 is assumed package id
                Reservation res = new Reservation(id,p,cust);
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
                writer.write(id + "," +res.getRelatedPackage().getId()+","+res.getCustomer().getID()+"," +res);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating reservations file: " + e.getMessage());
        }
    }



}











