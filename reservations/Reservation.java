package reservations;

import products.Product;

import java.time.LocalDate;
import java.util.List;

public class Reservation {
    private String id;
    private String type; //package or custom
    private List<Product> productsList;
    private boolean Status; //confirmed or cancelled
    private int totalCost;
    private LocalDate dateStart;
    private LocalDate dateEnd;
}
