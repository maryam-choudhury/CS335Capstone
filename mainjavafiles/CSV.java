import java.io.*;
import java.util.*;

public class CSV {

    // Load users from CSV 
	public static List<User> loadUsersFromCSV(String filename) {
	    List<User> users = new ArrayList<>();
	    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	        br.readLine(); // skip header
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] parts = line.split(",", -1);
	            String userID = parts[0].trim();
	            String name = parts[1].trim();
	            String email = parts[2].trim();
	            List<String> dietaryPreferences = List.of(parts[3].trim().split(";"));
	            boolean notificationsEnabled = Boolean.parseBoolean(parts[4].trim());
	            int matchThreshold = parts.length >= 6 ? Integer.parseInt(parts[5].trim()) : 60;

	            // Allergies are in column 7 (index 6), semicolon-separated
	            List<String> allergies = parts.length >= 7 && !parts[6].isBlank()
	                ? List.of(parts[6].split(";"))
	                : new ArrayList<>();

	            // Substitutions are in column 8 (index 7), format: ingredient:sub1|sub2;
	            Map<String, List<String>> substitutions = new HashMap<>();
	            if (parts.length >= 8 && !parts[7].isBlank()) {
	                String[] substPairs = parts[7].split(";");
	                for (String pair : substPairs) {
	                    String[] kv = pair.split(":");
	                    if (kv.length == 2) {
	                        substitutions.put(kv[0].trim(), List.of(kv[1].split("\\|")));
	                    }
	                }
	            }

	            // Build user object
	            User user = new User(userID, name, email, new ArrayList<>(dietaryPreferences), notificationsEnabled, matchThreshold);
	            user.setAllergies(new ArrayList<>(allergies));
	            user.setSubstitutions(substitutions);

	            users.add(user);
	        }
	        System.out.println("Users successfully loaded from " + filename);
	    } catch (IOException e) {
	        System.err.println("Error reading " + filename + ": " + e.getMessage());
	    }
	    return users;
	}


    // Save users (including dietary prefs) to CSV
    public static void exportUsersToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        	writer.println("Username,Name,Email,DietaryPreferences,NotificationsEnabled,MatchThreshold,Allergies,Substitutions");
            for (User user : users) {
            	String allergyStr = String.join(";", user.getAllergies());

            	StringBuilder substitutionStr = new StringBuilder();
            	for (Map.Entry<String, List<String>> entry : user.getSubstitutions().entrySet()) {
            	    substitutionStr.append(entry.getKey()).append(":").append(String.join("|", entry.getValue())).append(";");
            	}

            	writer.println(user.getUserID() + "," + user.getName() + "," + user.getEmail() + "," +
            	               String.join(";", user.getDietaryPreferences()) + "," + user.isNotificationsEnabled() +
            	               "," + user.getMatchThreshold() + "," + allergyStr + "," + substitutionStr.toString());

            }
        } catch (IOException e) {
            System.err.println("Error writing to " + filename + ": " + e.getMessage());
        }
    }

    // Check if a user exists
    public static boolean userExists(String username, List<User> users) {
        for (User user : users) {
            if (user.getUserID().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    // Load pantry items
    public static void loadPantriesFromCSV(String filename, List<User> users) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                String userID = parts[0].trim();
                String name = parts[1].trim();
                int quantity = Integer.parseInt(parts[2].trim());
                String date = parts[3].trim();
                String category = parts[4].trim();
                int ripeness = Integer.parseInt(parts[5].trim());

                for (User user : users) {
                    if (user.getUserID().equalsIgnoreCase(userID)) {
                        user.getPantry().addFoodItem(new FoodItem(name, quantity, date, category, ripeness));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading pantry CSV: " + e.getMessage());
        }
    }

    // Export pantry items
    public static void exportPantriesToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username,Name,Quantity,ExpirationDate,Category,Ripeness");
            for (User user : users) {
                for (FoodItem item : user.getPantry().getItems()) {
                    writer.println(user.getUserID() + "," + item.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing pantry CSV: " + e.getMessage());
        }
    }

    // Load notifications
    public static void loadNotificationsFromCSV(String filename, List<User> users) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                String userID = parts[0].trim();
                String message = parts[1].trim();

                for (User user : users) {
                    if (user.getUserID().equalsIgnoreCase(userID)) {
                        user.addNotification(new Notifications(message));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading notifications CSV: " + e.getMessage());
        }
    }

    // Export notifications
    public static void exportNotificationsToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username,Notification");
            for (User user : users) {
                for (Notifications n : user.getNotifications()) {
                    writer.println(user.getUserID() + "," + n.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing notifications CSV: " + e.getMessage());
        }
    }

    //  Load shopping lists
    public static void loadShoppingListsFromCSV(String filename, List<User> users) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                String userID = parts[0].trim();
                String item = parts[1].trim();

                for (User user : users) {
                    if (user.getUserID().equalsIgnoreCase(userID)) {
                        user.addToShoppingList(item);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading shopping list CSV: " + e.getMessage());
        }
    }

    // Export shopping lists
    public static void exportShoppingListsToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username,Item");
            for (User user : users) {
                for (String item : user.getShoppingList()) {
                    writer.println(user.getUserID() + "," + item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing shopping list CSV: " + e.getMessage());
        }
    }
}



