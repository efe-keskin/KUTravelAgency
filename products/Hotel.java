package products;

public class Hotel extends Product{
    private String name;
    private String city;
    private String roomType;
    private int pricePerNight;
    private int distanceToAirport;


    public Hotel(String name,String city,String roomType, int availableCount, int pricePerNight, int distanceToAirport) {
        super(availableCount);
        this.name = name;
        this.city = city;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.distanceToAirport = distanceToAirport;

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
