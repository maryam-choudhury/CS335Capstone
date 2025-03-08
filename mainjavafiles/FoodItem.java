package mindful_consumption_week5;
import java.time.LocalDate;

public class FoodItem {
	private String name;
	private int quantity;
	private LocalDate expirationDate;
	private String category;
	private int ripeness;
	
	public FoodItem(String name, int quantity, LocalDate expirationDate, String category, int ripeness) {
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.category = category;
        this.ripeness = Math.max(0, Math.min(ripeness, 5)); // ripeness btwn 0-5
        
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
		
	}
    
    // method to increase ripeness
    public void increaseRipeness() {
    	if (ripeness < 5) {
    		ripeness ++;
    	}
    }
    
    public String getName() { 
    	return name; 
    	}
  
    public int getQuantity() { 
    	return quantity; 
    }
    
    // Getter for ripeness
    public int getRipeness() {
        return ripeness;
    }
    
    // stringify, prep for CSV export
    @Override
    public String toString() {
        return name + "," + quantity + "," + expirationDate + "," + category + "," + ripeness;
    }

}