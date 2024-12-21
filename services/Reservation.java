package services;

import Users.Admin;
import Users.Customer;
import Users.User;
import core.App;
import products.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int id;
    private String type; //package or custom
    private List<Product> productsList;
    private Package relatedPackage;
    private boolean status; //confirmed or cancelled
    private int totalCost;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private ArrayList<String> presentableDatalist;
    private User customer;


    public Reservation(int id,Package pck,User customer){
        this.id = id;
        this.relatedPackage = pck;
        this.dateStart = pck.getDateStart();
        this.dateEnd = pck.getDateEnd();
        this.totalCost = pck.getTotalCost();
        this.customer = customer;

    }
    //reservation with package for customer by admin
    public Reservation(int id, String type, Package pck, boolean status, int totalCost, LocalDate dateStart, LocalDate dateEnd,Customer customer){
        this.id = id;
        this.type = type;
        this.productsList = pck.getProductsList();
        this.relatedPackage = pck;
        this.status = status;
        this.totalCost = totalCost;
        this.dateStart = pck.getDateStart();
        this.dateEnd = pck.getDateEnd();
        if(App.user instanceof Customer){
            this.customer = App.user;
        } else if (App.user instanceof Admin) {
            this.customer = customer;
        }
    }
    //reservation with package by customer
    public Reservation(int id, String type, Package pck, boolean status, int totalCost){
        this.id = id;
        this.type = type;
        this.relatedPackage = pck;
        this.productsList = productsList;
        this.status = status;
        this.totalCost = totalCost;
        this.dateStart = pck.getDateStart();
        this.dateEnd = pck.getDateEnd();
        if(App.user instanceof Customer){
            this.customer = App.user;
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String products = "";
        if (productsList != null) {
            for (Product product : productsList) {
                if (!products.isEmpty()) {
                    products += "; ";
                }
                products += product.toString(); // Assuming Product has a proper toString method
            }
        }


        return String.join(",",
                String.valueOf(id),
                type != null ? type : "",
                products,
                relatedPackage != null ? relatedPackage.toString() : "", // Assuming Package has a toString method
                status ? "confirmed" : "cancelled",
                String.valueOf(totalCost),
                dateStart != null ? dateStart.format(formatter) : "",
                dateEnd != null ? dateEnd.format(formatter) : "",
                customer != null ? customer.toString() : "" // Assuming User has a toString method
        );
    }

    public ArrayList<String> getPresentableDatalist() {
        presentableDatalist = new ArrayList<String>();
        presentableDatalist.add(String.valueOf(id));
        presentableDatalist.add(dateStart.toString());
        presentableDatalist.add(dateEnd.toString());
        presentableDatalist.add(type);
        presentableDatalist.add(String.valueOf(totalCost));
        presentableDatalist.add(status ? "Confirmed":"Canceled");
        for(Product product: productsList){
            presentableDatalist.add(product.toString());
        }

        return presentableDatalist;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Package getRelatedPackage() {
        return relatedPackage;
    }

    public User getCustomer() {
        return customer;
    }

}
