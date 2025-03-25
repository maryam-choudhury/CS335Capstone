import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Driver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // international format

    public static void main(String[] args) {
    	
    	
    	List<Recipe> recipes = new ArrayList<>();
        recipes.addAll(Recipe.loadHardcodedRecipes()); //load all scraped and hardcoded recipes
        recipes.addAll(Recipe.loadScrapedRecipes());
        
        
        List<User> users = CSV.loadUsersFromCSV("users.csv"); // Load users from existing CSV
        CSV.loadNotificationsFromCSV("notifications.csv", users); // Load notifications 
        
        // Ensuring Jennie Kim is hard coded to CSV for persistence
        if (!CSV.userExists("JennieKim", users)) {
            User testUser = new User("JennieKim", "Jennie Kim", "jennie@email.com", new ArrayList<>(), true);
            users.add(testUser);
            CSV.exportUsersToCSV(users, "users.csv");
        }

        while (true) { 
            User currentUser = null; // Reset currentUser for new login

            while (currentUser == null) {  
                System.out.print("Enter your username: ");
                String username = scanner.nextLine().trim();

                for (User user : users) {
                    if (user.getUserID().trim().equalsIgnoreCase(username.trim())) { 
                        currentUser = user;
                        break;
                    }
                }

                if (currentUser == null) {
                    System.out.println("Username not found. Please try again.");
                }
            }

            System.out.println("Welcome, " + currentUser.getName() + "! You are now logged in.");

            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("1. View Pantry");
                System.out.println("2. Add Food Item");
                System.out.println("3. View Notifications");
                System.out.println("4. View My Recipes"); // Updated to list all recipes
                System.out.println("5. View Suggested Recipes"); // Updated to sort and notify
                System.out.println("6. Add a Recipe");
                System.out.println("7. Logout");
                
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewPantry(currentUser);
                        break;
                    case 2:
                        addFoodItem(currentUser);
                        break;
                    case 3:
                        checkExpiringFoodItems(currentUser); //added this line to fix the notification system
                        viewNotifications(currentUser);
                        break;
                    case 4:
                        viewAllRecipes(recipes);
                        break;
                    case 5:
                        viewSuggestedRecipes(currentUser, recipes);
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        CSV.exportNotificationsToCSV(users, "notifications.csv"); 
                        break;
                }

                if (choice == 6) {
                    break;
                }
            }
        }
    }

    private static void viewPantry(User user) {
        System.out.println("\nYour Pantry:");
        if (!user.getPantry().getItems().isEmpty()) {
            for (FoodItem item : user.getPantry().getItems()) {
                System.out.println(item);
            }
        } else {
            System.out.println("Your pantry is empty.");
        }
    }

    private static void addFoodItem(User user) {
        System.out.print("Enter food name: ");
        String name = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        System.out.print("Enter expiration date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();
        LocalDate expirationDate = LocalDate.parse(dateInput, dateFormatter);

        FoodItem item = new FoodItem(name, quantity, expirationDate, category, 0);
        user.getPantry().addFoodItem(item);
        System.out.println("Food item added successfully!");
    }

    
    
    
    private static void viewNotifications(User user) {
        System.out.println("\nYour Notifications:");
        List<Notifications> notifications = user.getNotifications();
        if (notifications.isEmpty()) {
            System.out.println("No notifications available.");  
        } else {
            for (Notifications notification : notifications) {
                System.out.println("- " + notification.getMessage());
            }
        }
    }

    
    
   
    
    
    
    private static void checkExpiringFoodItems(User user) {
        List<Notifications> newNotifications = user.getPantry().generateNotifications();

        for (Notifications notification : newNotifications) {
            if (!user.getNotifications().contains(notification)) {
                user.addNotification(notification);
            }
        }
        CSV.exportNotificationsToCSV(Collections.singletonList(user), "notifications.csv");
    }
    
    
  
  
    
    private static void viewAllRecipes(List<Recipe> recipes) {
        System.out.println("\nAll Available Recipes:");
        for (Recipe recipe : recipes) {
                System.out.println("- " + recipe.getName());
                //System.out.println("  Ingredients: " + String.join(", ", recipe.getIngredients())); these lines of code print out the instructions and ingredients of the recipes but we don't need to implement this rn
                //System.out.println("  Instructions: " + recipe.getInstructions() + "\n");
        }
    }
    
    
    
    private static void viewSuggestedRecipes(User user, List<Recipe> recipes) {
        System.out.println("\nSuggested Recipes (sorted by highest ingredient match):");
        List<RecipeMatch> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            int matchCount = 0;
            for (String ingredient : recipe.getIngredients()) {
                if (user.getPantry().hasIngredient(ingredient)) {
                    matchCount++;
                }
            }
            double matchPercentage = (double) matchCount / recipe.getIngredients().size();
            if (matchPercentage >= 0.6) { // 60% match threshold
                matches.add(new RecipeMatch(recipe, matchPercentage));
                
                // Generate a notification for high-match recipes
                String message = "You have most ingredients for " + recipe.getName() + "!";
                Notifications newNotification = new Notifications(message);
                if (!user.getNotifications().contains(newNotification)) { // Avoids duplicate notifications
                    user.addNotification(newNotification);
                }
            }
        }
        
        // Sort recipes in descending order based on percentage matching pantry
        matches.sort((a, b) -> Double.compare(b.matchPercentage, a.matchPercentage));
        for (RecipeMatch match : matches) {
            System.out.println("- " + match.recipe.getName() + " (" + (int)(match.matchPercentage * 100) + "% match)");
        }
        
        CSV.exportNotificationsToCSV(Collections.singletonList(user), "notifications.csv");
    }
}




