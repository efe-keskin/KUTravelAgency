package services;

import products.Hotel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TravelParser {
//creates objects of products from the corresponding lines in .csv files

    public static Hotel parseHotel(){
        try {
            File file = new File("datasets/FinalKU_Travel_Agency_Dataset_Hotels.csv");
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()){
                String data = myReader.nextLine();
                String[] dataArray = data.split(",");
                String name = dataArray[0];
                String city = dataArray[1];
                String roomType = dataArray[2];
                int availableCount = Integer.parseInt(dataArray[3]);
                int pricePerNight =  Integer.parseInt((dataArray[4]));
                int distanceToAirpot = Integer.parseInt((dataArray[5]));
                Hotel hotel = new Hotel(name,city,roomType,availableCount,pricePerNight,distanceToAirpot);
                return hotel;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred."+ e);
        }
        return null;

    }




}
