package products;

import java.util.HashMap;

public class Hotel extends Product{
    private String name;
    private String city;
    private String roomType;
    private int pricePerNight;
    private int distanceToAirport;
    public static HashMap<Integer,Hotel> hotelsDict;

    public Hotel(String name,String city,String roomType, int availableCount, int pricePerNight, int distanceToAirport) {
        super(availableCount);
        this.name = name;
        this.city = city;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.distanceToAirport = distanceToAirport;

    }
    public static Hotel retrieveHotel(int id){
        return hotelsDict.get(id);

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

    public int getPricePerNight() {
        return pricePerNight;
    }

    public int getDistanceToAirport() {
        return distanceToAirport;
    }
}
