package Users;

public class Customer {
    private final String username;
    private final String password;
    private final Integer id;
    public Customer(String username, String password, Integer id){
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
