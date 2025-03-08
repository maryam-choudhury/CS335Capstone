package mindful_consumption_week5;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;


public class Pantry {
    private List<FoodItem> items;

    public Pantry() {
        this.items = new ArrayList<FoodItem>();
    }

    public void addFoodItem(FoodItem item) {
        items.add(item);
    }

    public void removeFooditem(FoodItem item) {
        items.remove(item);
    }
    
    public List<FoodItem> getItems() { 
        return items; 
    }

    public List<FoodItem> getExpiringSoon() { 
        LocalDate today = LocalDate.now();
        List<FoodItem> expiringSoon = new ArrayList<>();
        for (FoodItem item : items) {
            if (item.getExpirationDate().isBefore(today.plusDays(5))) { //adds days to the current date
                expiringSoon.add(item);
            }
        }
        return expiringSoon;
    }
    
    public List<Notifications> generateNotifications() {
        List<Notifications> notifications = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (FoodItem item : items) {
            if (item.getExpirationDate().isBefore(today.plusDays(5))) {
                notifications.add(new Notifications("Warning: " + item.getName() + " is expiring soon!"));
            }
            if (item.getRipeness() == 5) {
                notifications.add(new Notifications("Alert: " + item.getName() + " is overripe and should be used soon!"));
            }
        }
        return notifications;
    }
    public void updateRipenessLevels() {
        for (FoodItem item : items) {
            item.increaseRipeness();
        }
    }

}
