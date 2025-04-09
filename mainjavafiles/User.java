import java.util.ArrayList;
import java.util.List;

public class User {
    private String userID;
    private String name;
    private String email;
    private List<String> dietaryPreferences;
    private boolean notificationsEnabled;
    private Pantry pantry;
    private List<Notifications> notifications;
    private List<String> shoppingList;

    public User(String userID, String name, String email, List<String> dietaryPreferences, boolean notificationsEnabled) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.dietaryPreferences = dietaryPreferences;
        this.notificationsEnabled = notificationsEnabled;
        this.pantry = new Pantry(); //  initialize empty pantry
        this.notifications = new ArrayList<>(); // initialize empty notifications
        this.shoppingList = new ArrayList<>(); // initialize empty shopping list
    }

    // Accessor methods
    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getDietaryPreferences() {
        return dietaryPreferences;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public Pantry getPantry() {
        return pantry;
    }

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public List<String> getShoppingList() {
        return shoppingList;
    }

    public void addNotification(Notifications notification) {
        this.notifications.add(notification);
    }

    public void addToShoppingList(String item) {
        this.shoppingList.add(item);
    }

    public void removeFromShoppingList(String item) {
        this.shoppingList.remove(item);
    }
}


