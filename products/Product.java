package products;

public class Product {
    private int availableCount;
    public Product(int availableCount){
        this.availableCount = availableCount;
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
