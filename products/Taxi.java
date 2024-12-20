package products;

public class Taxi extends Product {
    private String city;
    private String taxiType;
    private double baseFare;
    private double perKmRate;

    public Taxi( String city, String taxiType,int availableCount, double baseFare, double perKmRate) {
        super(availableCount);
        this.city = city;
        this.taxiType = taxiType;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
    }


    public String getCity() {
        return city;
    }

    public String getTaxiType() {
        return taxiType;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public double getPerKmRate() {
        return perKmRate;
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "city='" + city + '\'' +
                ", taxiType='" + taxiType + '\'' +
                ", availableCount=" + getAvailableCount() +
                ", baseFare=" + baseFare +
                ", perKmRate=" + perKmRate +
                '}';
    }
}