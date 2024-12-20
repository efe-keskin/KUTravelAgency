package Users;

import reservations.Reservation;

import java.util.ArrayList;
import java.util.List;

public class Customer implements User{
    private final String username;
    private final String password;
    private final Integer id;
    private final List<Reservation> reservations;

    public Customer(String username, String password, Integer id){
        reservations = new ArrayList<Reservation>();
        this.username = username;
        this.password = password;
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getId() {
        return id;
    }
}
