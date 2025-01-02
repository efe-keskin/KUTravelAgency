package products;

/**
 * Represents a generic product with a count of available units.
 */
public class Product {
    private int availableCount;

    /**
     * Constructs a Product instance with the specified availability count.
     *
     * @param availableCount The initial number of available units.
     */
    public Product(int availableCount) {
        this.availableCount = availableCount;
    }

    /**
     * Retrieves the current number of available units for the product.
     *
     * @return The number of available units.
     */
    public int getAvailableCount() {
        return availableCount;
    }
}