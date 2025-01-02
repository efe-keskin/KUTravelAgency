package Users;

/**
 * Represents a user in the system with basic identification methods.
 */
public interface User {
    /**
     * Gets the username of the user.
     *
     * @return The user's username as a String
     */
    String getUsername();

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user's ID as an integer
     */
    int getID();
}