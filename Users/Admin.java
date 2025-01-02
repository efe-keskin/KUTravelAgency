package Users;

/**
 * Represents an Admin user with a username and password.
 */
public class Admin implements User {
    private final String username;
    private final String password;

    /**
     * Constructs an Admin object with the specified username and password.
     *
     * @param username The username of the admin.
     * @param password The password of the admin.
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username of the admin.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the ID of the admin. Always returns 0 as admins do not have specific IDs.
     *
     * @return The admin ID (always 0).
     */
    @Override
    public int getID() {
        return 0;
    }

    /**
     * Gets the password of the admin.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }
}