package Users;

import services.Reservation;

import java.util.ArrayList;
import java.util.List;

public class Customer implements User{
    private final String username;
    private final String password;
    private final Integer id;
    private final List<Reservation> travelHistory;

    public Customer(String username, String password, Integer id){
        travelHistory = new ArrayList<Reservation>();
        this.username = username;
        this.password = password;
        this.id = id;
    }



    public void makeReservation(){

    }
public void addReservation(Reservation reservation){
        travelHistory.add(reservation);
}
public List<Reservation> getTravelHistory(){
        return travelHistory;
}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getID() {
        return id;
    }
}
