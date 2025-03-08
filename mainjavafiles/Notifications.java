package mindful_consumption_week5;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// class represents a single notif about food status
public class Notifications {
	private String message;
	private LocalDate dateGenerated;
	
	// notification w/ message constructor
	public Notifications(String message) {
		this.message = message;
		this.dateGenerated = LocalDate.now();
	}
	
	// Constructor to load notifications from CSV (diff than one above!)
    public Notifications(String message, String date) {
        this.message = message;
        this.dateGenerated = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE); 
    }
	
	// getter for message
    public String getMessage() {
        return message;
    }

    public LocalDate getDateGenerated() {
        return dateGenerated;
    }

    @Override
	// stringify
	public String toString() {
		return dateGenerated + ": " + message;
	}
}