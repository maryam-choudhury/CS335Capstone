//package mindful_consumption_week5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class CSV {

    public static void exportNotificationsToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username,Date,Message"); // CSV header

            for (User user : users) {
                for (Notifications notification : user.getNotifications()) {
                    writer.println(user.getUserID() + "," + notification.getDateGenerated() + "," + notification.getMessage());
                }
            }
            System.out.println("Notifications successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

   
        public static boolean userExists(String username, List<User> users) {
            for (User user : users) {
                if (user.getUserID().equalsIgnoreCase(username)) {
                    return true;
                }
            }
            return false;
        }
    
    public static void loadNotificationsFromCSV(String filename, List<User> users) {
        File file = new File(filename);
        if (!file.exists()) {
            return; //  Silent return without printing error when file is missing
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { 
                    firstLine = false;
                    continue; 
                }

                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue; // Skip invalid lines

                String username = parts[0].trim();
                String date = parts[1].trim();
                String message = parts[2].trim();

                for (User user : users) {
                    if (user.getUserID().trim().equalsIgnoreCase(username)) {
                        user.addNotification(new Notifications(message, date)); // Load saved notification
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading notifications CSV: " + e.getMessage());
        }
    }

    //  Exports users to CSV
    public static void exportUsersToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username,Name,Email,DietaryPreferences,NotificationsEnabled"); // CSV Header

            for (User user : users) {
                writer.println(user.getUserID() + "," + user.getName() + "," + user.getEmail() + "," +
                               String.join(";", user.getDietaryPreferences()) + "," + user.isNotificationsEnabled());
            }

            System.out.println("Users successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
    
    public static void exportPantriesToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username,Name,Quantity,ExpirationDate,Category,Ripeness"); // header
            for (User user : users) {
                for (FoodItem item : user.getPantry().getItems()) {
                    writer.println(user.getUserID() + "," + item.toString());
                }
            }
            System.out.println("Pantries successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing pantry CSV: " + e.getMessage());
        }
    }
    public static void exportShoppingListsToCSV(List<User> users, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username,Item"); // Header
            for (User user : users) {
                for (String item : user.getShoppingList()) {
                    writer.println(user.getUserID() + "," + item);
                }
            }
            System.out.println("Shopping lists successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing shopping list CSV: " + e.getMessage());
        }
    }


    
    public static void loadPantriesFromCSV(String filename, List<User> users) {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 6);
                if (parts.length < 6) continue;

                String username = parts[0].trim();
                String name = parts[1].trim();
                int quantity = Integer.parseInt(parts[2].trim());
                LocalDate expirationDate = LocalDate.parse(parts[3].trim());
                String category = parts[4].trim();
                int ripeness = Integer.parseInt(parts[5].trim());

                for (User user : users) {
                    if (user.getUserID().equalsIgnoreCase(username)) {
                        FoodItem item = new FoodItem(name, quantity, expirationDate, category, ripeness);
                        user.getPantry().addFoodItem(item);
                        break;
                    }
                }
            }

            System.out.println("Pantries successfully loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error reading pantry CSV: " + e.getMessage());
        }
    }


    public static List<User> loadUsersFromCSV(String filename) {
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { 
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 5);
                if (parts.length < 5) continue; // Skip invalid lines

                String username = parts[0].trim(); // Trim username 
                String name = parts[1].trim();
                String email = parts[2].trim();
                List<String> dietaryPreferences = List.of(parts[3].trim().split(";"));
                boolean notificationsEnabled = Boolean.parseBoolean(parts[4].trim());

                User user = new User(username, name, email, dietaryPreferences, notificationsEnabled);
                users.add(user);
            }

            System.out.println("Users successfully loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error reading user CSV: " + e.getMessage());
        }

        return users;
    }
    
    public static void loadShoppingListsFromCSV(String filename, List<User> users) {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String username = parts[0].trim();
                String item = parts[1].trim();

                for (User user : users) {
                    if (user.getUserID().equalsIgnoreCase(username)) {
                        user.addToShoppingList(item);
                        break;
                    }
                }
            }

            System.out.println("Shopping lists successfully loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error reading shopping list CSV: " + e.getMessage());
        }
    }


}


