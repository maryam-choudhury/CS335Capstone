import java.util.ArrayList;
import java.util.List;

public class Pantry {
    private List<FoodItem> items;

    public Pantry() {
        this.items = new ArrayList<>();
    }

    public void addFoodItem(FoodItem item) {
        this.items.add(item);
    }

    public List<FoodItem> getItems() {
        return items;
    }

    // Updated logic so non-exact matches would still pass 
    public boolean hasIngredient(String ingredient) {
        String ing = ingredient.toLowerCase();
        for (FoodItem item : items) {
            String pantryName = item.getName().toLowerCase();

            // partial matches
            if (ing.contains(pantryName) || pantryName.contains(ing)) {
                return true;
            }
        }
        return false;
    }

    // Check if a food item already exists in the pantry
    public boolean containsItem(String itemName) {
        for (FoodItem item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    // List of notifications for items that are expiring soon
    public List<Notifications> generateNotifications() {
        List<Notifications> notifications = new ArrayList<>();
        for (FoodItem item : items) {
            if (item.isExpiringSoon()) { // added to FoodItem
                notifications.add(new Notifications("Your " + item.getName() + " is expiring soon!"));
            }
        }
        return notifications;
    }
}

