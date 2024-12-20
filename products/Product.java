package products;

public class Product {
    private static int id = 0;
    private int thisID;
    private int availableCount;
    public Product(int availableCount){
        this.availableCount = availableCount;
        id++;
        this.thisID = id;
    }
    public int getID(){
        return thisID;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    void bookProduct(){
        if(availableCount >0){
            availableCount--;

        }

    }
}
