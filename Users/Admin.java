package Users;

public class Admin implements User{
    private final String username;
    private final String password;
    public Admin (String username, String password){
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    @Override
    public int getID() {
        return 0;
    }

    public String getPassword() {
        return password;
    }


    }

