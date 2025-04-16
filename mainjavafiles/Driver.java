// Original imports preserved
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Driver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // international format

    public static void main(String[] args) {
        List<User> users = CSV.loadUsersFromCSV("users.csv"); // Load users from existing CSV
        CSV.loadNotificationsFromCSV("notifications.csv", users); // Load notifications 
        CSV.loadPantriesFromCSV("pantry.csv", users); // Accidentally got rid of pantry reload when transferring, adding back
        CSV.loadShoppingListsFromCSV("shoppinglist.csv", users); //  Shopping list load 

        while (true) {
            User currentUser = null;

            // Login or register 
            System.out.print("Are you a new user? (yes/no): ");
            String isNewUser = scanner.nextLine().trim().toLowerCase();
            if (isNewUser.equals("yes")) {
                System.out.print("Enter a username (no spaces): ");
                String username = scanner.nextLine().trim();

                while (CSV.userExists(username, users)) {
                    System.out.println("Username already exists. Try another: ");
                    username = scanner.nextLine().trim();
                }

                System.out.print("Enter your full name: ");
                String fullName = scanner.nextLine();

                System.out.print("Enter your email: ");
                String email = scanner.nextLine();

                System.out.print("Enter dietary preference (vegan, vegetarian, none): ");
                String pref = scanner.nextLine().trim().toLowerCase();
                if (!List.of("vegan", "vegetarian", "none").contains(pref)) {
                    pref = "none";
                }

                List<String> preferences = new ArrayList<>();
                preferences.add(pref);

                User newUser = new User(username, fullName, email, preferences, true);
                users.add(newUser);
                CSV.exportUsersToCSV(users, "users.csv");
                currentUser = newUser;

                System.out.println("New user created and logged in: " + fullName);
            } else {
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
            }

            // Dietary preference prompt - check that it's the same for later filters
            String savedPreference = currentUser.getDietaryPreferences().isEmpty() ||
                                     currentUser.getDietaryPreferences().get(0).isBlank()
                                     ? "none"
                                     : currentUser.getDietaryPreferences().get(0);

            System.out.println("\nYour current dietary preference is set to: " + savedPreference.toUpperCase());
            System.out.println("This affects which recipes we show you.");
            System.out.print("Would you like to keep using this preference? (yes to keep, no to change): ");
            String keepPref = scanner.nextLine().trim().toLowerCase();

            List<String> sessionPreferences = new ArrayList<>();
            if (keepPref.equals("no")) {
                String newPref;
                while (true) {
                    System.out.println("Options: 'vegan', 'vegetarian', or 'none'");
                    System.out.print("Enter your new preference: ");
                    newPref = scanner.nextLine().trim().toLowerCase();
                    if (newPref.equals("vegan") || newPref.equals("vegetarian") || newPref.equals("none")) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please enter exactly 'vegan', 'vegetarian', or 'none'.");
                    }
                }
                sessionPreferences.add(newPref);
                currentUser.getDietaryPreferences().clear();
                currentUser.getDietaryPreferences().add(newPref);
                CSV.exportUsersToCSV(users, "users.csv"); //  Persist preference 
            } else {
                sessionPreferences = new ArrayList<>(currentUser.getDietaryPreferences());
            }

            // Load recipes based on preference
            List<Recipe> recipes = new ArrayList<>();
            recipes.addAll(Recipe.loadHardcodedRecipes());
            recipes.addAll(Recipe.loadScrapedRecipes(sessionPreferences));

            System.out.println("Welcome, " + currentUser.getName() + "! You are now logged in.");

            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\nChoose an option:");
                System.out.println("1. View Pantry");
                System.out.println("2. Add Food Item");
                System.out.println("3. View Notifications");
                System.out.println("4. View My Recipes");
                System.out.println("5. View Suggested Recipes");
                System.out.println("6. Add a Recipe");
                System.out.println("7. Logout");
                System.out.println("8. View/Add to Shopping List");

                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear newline

                switch (choice) {
                    case 1:
                        viewPantry(currentUser);
                        break;
                    case 2:
                        addFoodItem(currentUser);
                        break;
                    case 3:
                        checkExpiringFoodItems(currentUser);
                        viewNotifications(currentUser);
                        break;
                    case 4:
                        viewAllRecipes(recipes);
                        break;
                    case 5:
                        viewSuggestedRecipes(currentUser, recipes);
                        break;
                    case 6:
                        System.out.println("Feature coming soon...");
                        break;
                    case 7:
                        System.out.println("Logging out...");

                        //  Persist user data post logout
                        CSV.exportUsersToCSV(users, "users.csv");
                        CSV.exportNotificationsToCSV(users, "notifications.csv");
                        CSV.exportPantriesToCSV(users, "pantry.csv");
                        CSV.exportShoppingListsToCSV(users, "shoppinglist.csv");

                        loggedIn = false; // back to login
                        break;
                    case 8:
                        manageShoppingList(currentUser);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }
    //  View user's pantry
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

    // Add new food item to  pantry
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

    // View user notifications
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

    // Food items expiring? Generate new notifications
    private static void checkExpiringFoodItems(User user) {
        List<Notifications> newNotifications = user.getPantry().generateNotifications();

        for (Notifications notification : newNotifications) {
            if (!user.getNotifications().contains(notification)) {
                user.addNotification(notification);
            }
        }

        CSV.exportNotificationsToCSV(Collections.singletonList(user), "notifications.csv");
    }

    // View all available recipes (regardless of content in user's pantry)
    private static void viewAllRecipes(List<Recipe> recipes) {
        System.out.println("\nAll Available Recipes:");
        for (Recipe recipe : recipes) {
            System.out.println("- " + recipe.getName());
        }
    }

    // View recipe suggestions based on user's pantry
    private static void viewSuggestedRecipes(User user, List<Recipe> recipes) {
        // Show the user's current match threshold
        System.out.println("\nYour current minimum ingredient match percentage is: " + user.getMatchThreshold() + "%");

        // Ask if they want to change it
        System.out.print("Would you like to change your match percentage for this session? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        int threshold = user.getMatchThreshold(); // default to stored value

        if (response.equals("yes")) {
            System.out.println("\nChoose your new minimum ingredient match percentage:");
            System.out.println("1. 10%");
            System.out.println("2. 30%");
            System.out.println("3. 50%");
            System.out.println("4. 60%");
            System.out.println("5. 80%");
            System.out.println("6. 100%");
            System.out.print("Enter the number corresponding to your choice: ");

            int matchChoice = scanner.nextInt();
            scanner.nextLine(); // Clear newline

            switch (matchChoice) {
                case 1: threshold = 10; break;
                case 2: threshold = 30; break;
                case 3: threshold = 50; break;
                case 4: threshold = 60; break;
                case 5: threshold = 80; break;
                case 6: threshold = 100; break;
                default:
                    System.out.println("Invalid choice. Keeping previous value: " + threshold + "%");
            }

            // Persist updated threshold to user object
            user.setMatchThreshold(threshold);
        }

        double thresholdDecimal = threshold / 100.0;

        // Match logic based on pantry
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

            if (matchPercentage >= thresholdDecimal) {
                matches.add(new RecipeMatch(recipe, matchPercentage));

                String message = "You have " + (int)(matchPercentage * 100) + "% of ingredients for " + recipe.getName() + "!";
                Notifications newNotification = new Notifications(message);
                if (!user.getNotifications().contains(newNotification)) {
                    user.addNotification(newNotification);
                }
            }
        }

        matches.sort((a, b) -> Double.compare(b.matchPercentage, a.matchPercentage));

        for (RecipeMatch match : matches) {
            System.out.println("- " + match.recipe.getName() + " (" + (int)(match.matchPercentage * 100) + "% match)");
        }

        CSV.exportNotificationsToCSV(Collections.singletonList(user), "notifications.csv");
    }


    // View or modify user's shopping list
    private static void manageShoppingList(User user) {
        while (true) {
            System.out.println("\nYour Shopping List:");
            List<String> list = user.getShoppingList();
            if (list.isEmpty()) {
                System.out.println("Your shopping list is empty.");
            } else {
                for (String item : list) {
                    System.out.println("- " + item);
                }
            }

            System.out.println("\n1. Add Item");
            System.out.println("2. Remove Item");
            System.out.println("3. Back to Menu");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            if (choice == 1) {
                System.out.print("Enter item to add: ");
                String item = scanner.nextLine();
                user.addToShoppingList(item);
            } else if (choice == 2) {
                System.out.print("Enter item to remove: ");
                String item = scanner.nextLine();
                user.removeFromShoppingList(item);
            } else if (choice == 3) {
                break;
            }
        }
    }
}

