package mindful_consumption;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Driver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // international format

    public static void main(String Args[]) {
        List<User> users = CSV.loadUsersFromCSV("users.csv"); // Load users from existing CSV
        CSV.loadNotificationsFromCSV("notifications.csv", users); // Load notifications 

        while (true) { 
            User currentUser = null; // Reset currentUser for new login

            while (currentUser == null) {  
                System.out.print("Are you a new user? (yes/no): ");
                String isNewUser = scanner.nextLine().trim().toLowerCase();

                if (isNewUser.equals("no")) {
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine().trim();

                    for (User user : users) {
                        if (user.getUserID().trim().equalsIgnoreCase(username.trim())) { // remove unwanted spaces
                            currentUser = user;
                            break;
                        }
                    }

                    if (currentUser == null) {
                        System.out.println("Username not found. Please try again.");
                    }

                } else if (isNewUser.equals("yes")) {
                    List<String> existingUsernames = new ArrayList<>();
                    for (User user : users) {
                        existingUsernames.add(user.getUserID()); // Store usernames
                    }

                    String username;
                    while (true) {
                        System.out.print("Choose a username: ");
                        username = scanner.nextLine().trim();

                        if (!existingUsernames.contains(username)) {
                            break;
                        } else {
                            System.out.println("This username is unavailable. Please choose another one.");
                        }
                    }

                    System.out.print("Enter your name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter your email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter dietary preferences (comma-separated): ");
                    List<String> dietaryPreferences = Arrays.asList(scanner.nextLine().split(","));

                    boolean notifications = false;
                    while (true) {
                        System.out.print("Enable notifications? (yes/no): ");
                        String notificationInput = scanner.nextLine().trim().toLowerCase();
                        if (notificationInput.equals("yes")) {
                            notifications = true;
                            break;
                        } else if (notificationInput.equals("no")) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                        }
                    }

                    // Create new user 
                    // initialize notifications
                    User newUser = new User(username, name, email, dietaryPreferences, notifications);
                    users.add(newUser);
                    System.out.println("Account created successfully! Your username is: " + newUser.getUserID());

                    // Save new user data
                    CSV.exportUsersToCSV(users, "users.csv");
                    System.out.println("Please log in with your new username.");
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }

            System.out.println("Welcome, " + currentUser.getName() + "! You are now logged in.");

            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("1. View Pantry");
                System.out.println("2. Add Food Item");
                System.out.println("3. View Notifications");
                System.out.println("4. Logout");

                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("\nYour Pantry:");
                        if (currentUser.getPantry() != null) {
                            for (FoodItem item : currentUser.getPantry().getItems()) {
                                System.out.println(item);
                            }
                        } else {
                            System.out.println("(TODO: Implement pantry retrieval)");
                        }
                        break;

                    case 2:
                        if (currentUser.getPantry() != null) {
                            addFoodItem(currentUser.getPantry());
                        } else {
                            System.out.println("(TODO: Implement addFoodItem)");
                        }
                        break;

                    case 3:
                        System.out.println("\nYour Notifications:");
                        if (!currentUser.getNotifications().isEmpty()) {
                            for (Notifications notification : currentUser.getNotifications()) {
                                System.out.println(notification);
                            }
                        } else {
                            System.out.println("You have no notifications.");
                        }
                        break;

                    case 4:
                        System.out.println("Logging out...");

                        // Save notifications before logout
                        CSV.exportNotificationsToCSV(users, "notifications.csv"); 
                        break; // Exit the menu loop when logging out
                }

                if (choice == 4) {
                    break; // Breaks out of the menu loop and restarts login
                }
            }
         // Example: Create some FoodItems
            FoodItem apple = new FoodItem("Apple", 10, LocalDate.now().plusDays(3), "Fruit", 3);
            FoodItem banana = new FoodItem("Banana", 5, LocalDate.now().plusDays(2), "Fruit", 4);
            
            // Create a User and add FoodItems to their pantry
            User user = new User("jane_doe", "Jane Doe", "jane.doe@example.com", Arrays.asList("Vegetarian"), true);
            Pantry pantry = user.getPantry();
            pantry.addFoodItem(apple);
            pantry.addFoodItem(banana);
            
            // Generate notifications based on pantry items
            user.checkPantryNotifications();
            
            // Display notifications (Optional)
            System.out.println("User Notifications:");
            for (Notifications notification : user.getNotifications()) {
                System.out.println(notification);
            }

            // -------------------- New Code for PB&J Recipe --------------------

            // Creating some FoodItems for the PB&J sandwich
            FoodItem bread = new FoodItem("Bread", 2, LocalDate.now().plusDays(7), "Bakery", 2);
            FoodItem peanutButter = new FoodItem("Peanut Butter", 1, LocalDate.now().plusMonths(3), "Spread", 1);
            FoodItem jelly = new FoodItem("Jelly", 1, LocalDate.now().plusMonths(1), "Spread", 1);

            // Creating the PB&J recipe
            Recipe pbjSandwich = new Recipe(
                "Peanut Butter and Jelly Sandwich",
                Arrays.asList(bread, peanutButter, jelly),
                5, // 5 minutes prep time
                "1. Spread peanut butter on one slice of bread.\n" +
                "2. Spread jelly on the other slice.\n" +
                "3. Press the slices together.\n" +
                "4. Cut in half and enjoy!"
            );

            // Display the recipe details
            System.out.println(pbjSandwich);
            
            // Example: Add PB&J sandwich ingredients to the user's pantry
            Pantry pantry1 = currentUser.getPantry(); 
            pantry1.addFoodItem(bread);
            pantry1.addFoodItem(peanutButter);
            pantry1.addFoodItem(jelly);
            
            // Display Pantry contents (Optional)
            System.out.println("\nPantry Contents:");
            for (FoodItem item : pantry1.getItems()) {
                System.out.println(item);
            }
            
            // Example: Generate notifications for user based on pantry items
            user.checkPantryNotifications();
            
            // Display notifications (Optional)
            System.out.println("\nUser Notifications:");
            for (Notifications notification : user.getNotifications()) {
                System.out.println(notification);
            }
        }
        
        
    }

    private static void addFoodItem(Pantry pantry1) {
        System.out.print("Enter food name: ");
        String name = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter category (Fruit, Vegetable, Pantry Goods, Dairy, Protein, Spices): ");
        String category = scanner.nextLine();

        LocalDate expirationDate = null;
        while (expirationDate == null) {
            System.out.print("Enter expiration date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();
            try {
                expirationDate = LocalDate.parse(dateInput, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        int ripeness = -1; 
        while (ripeness < 0 || ripeness > 5) {  // Add logic for foods that do not get ripe
            System.out.print("Enter ripeness level (0-5): ");
            if (scanner.hasNextInt()) {
                ripeness = scanner.nextInt();
            } else {
                scanner.next();
            }
        }
        scanner.nextLine();

        FoodItem item = new FoodItem(name, quantity, expirationDate, category, ripeness);
        pantry1.addFoodItem(item);
        System.out.println("Food item added successfully!");
        
     // Ask the user if they want to see a recipe suggestion
        System.out.print("Would you like to see a recipe using the newly added ingredients? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes")) {
            showRecipe();
        }
    }

    private static void showRecipe() {
        // Creating some FoodItems for the PB&J sandwich
        FoodItem bread = new FoodItem("Bread", 2, LocalDate.now().plusDays(7), "Bakery", 2);
        FoodItem peanutButter = new FoodItem("Peanut Butter", 1, LocalDate.now().plusMonths(3), "Spread", 1);
        FoodItem jelly = new FoodItem("Jelly", 1, LocalDate.now().plusMonths(1), "Spread", 1);

        // Creating the PB&J recipe
        Recipe pbjSandwich = new Recipe(
            "Peanut Butter and Jelly Sandwich",
            Arrays.asList(bread, peanutButter, jelly),
            5, // 5 minutes prep time
            "1. Spread peanut butter on one slice of bread.\n" +
            "2. Spread jelly on the other slice.\n" +
            "3. Press the slices together.\n" +
            "4. Cut in half and enjoy!"
        );

        // Display the recipe details
        System.out.println(pbjSandwich);
    }
    }






