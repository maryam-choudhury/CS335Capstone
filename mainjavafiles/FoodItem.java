import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FoodItem {
    private String name;
    private int quantity;
    private LocalDate expirationDate;
    private String category;
    private int ripeness;

    // Constructor from fields
    public FoodItem(String name, int quantity, LocalDate expirationDate, String category, int ripeness) {
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.category = category;
        this.ripeness = ripeness;
    }

    // Alternate constructor from String expirationDate (for CSV load)
    public FoodItem(String name, int quantity, String expirationDate, String category, int ripeness) {
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.category = category;
        this.ripeness = ripeness;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getCategory() {
        return category;
    }

    public int getRipeness() {
        return ripeness;
    }

    // stringify, prep for CSV export
    @Override
    public String toString() {
        return name + "," + quantity + "," + expirationDate + "," + category + "," + ripeness;
    }

    // Used by Pantry to check for expiring items
    public boolean isExpiringSoon() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now().plusDays(3));
    }
}
