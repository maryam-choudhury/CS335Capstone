package mindful_consumption_week5;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userID;  // This is actually the username
    private String name;
    private String email;
    private List<String> dietaryPreferences;
    private Pantry pantry;
    private List<Notifications> notifications; // Stores user notifications
    private boolean notificationsEnabled; // Store notifications preference separately


    public User(String username, String name, String email, List<String> dietaryPreferences, boolean notificationsEnabled) {
        this.userID = username;
        this.name = name;
        this.email = email;
        this.dietaryPreferences = new ArrayList<>(dietaryPreferences);
        this.notifications = new ArrayList<>(); // Initialize notifications as an empty list
        this.notificationsEnabled = notificationsEnabled; // Store boolean separately from notifications param
        this.pantry = new Pantry(); 
    }

    //  Allows notifications to be added manually, could be helpful for testing
    // Prob needs to be removed later
    public void addNotification(Notifications notification) {
        notifications.add(notification);
    }
    public void setNotifications(List<Notifications> loadedNotifications) {
        this.notifications = new ArrayList<>(loadedNotifications);
    }

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled; 
    }

    // Prevents duplicate notifications 
    public void checkPantryNotifications() {
        List<Notifications> newNotifications = pantry.generateNotifications();

        for (Notifications notification : newNotifications) {
            if (!notifications.contains(notification)) { // Avoid duplicates
                notifications.add(notification);
            }
        }
    }

    public Pantry getPantry() {
        return pantry;
    }

    public void encryptData() {
        // TODO: Implement encryption in future updates
    }

 
    public String getUserID() {
        return userID;
    }

    public List<String> getDietaryPreferences() {
        return dietaryPreferences;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}

