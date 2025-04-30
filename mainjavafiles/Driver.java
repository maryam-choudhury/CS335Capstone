// Original imports preserved
import java.util.*;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

                	boolean userFound = false;
                	for (User user : users) {
                	    if (user.getUserID().trim().equalsIgnoreCase(username.trim())) {
                	        currentUser = user;
                	        userFound = true;
                	        break;
                	    }
                	}

                	if (!userFound) {
                	    System.out.println("New user detected. Let's create your profile.");

                	    System.out.print("Enter your full name: ");
                	    String name = scanner.nextLine();

                	    System.out.print("Enter your email: ");
                	    String email = scanner.nextLine();

                	    List<String> dietaryPreferences = new ArrayList<>(); // optional future use
                	    boolean notificationsEnabled = true;

                	    currentUser = new User(username, name, email, dietaryPreferences, notificationsEnabled);

                	    // Add starter pantry items
                	    addStarterPantryItems(currentUser.getPantry());

                	    users.add(currentUser);

                	    // Save new user to CSVs
                	    CSV.exportUsersToCSV(users, "users.csv");
                	    CSV.exportPantriesToCSV(users, "pantry.csv");

                	    // GUI popup
                	    javax.swing.JOptionPane.showMessageDialog(null,
                	        "Welcome to the Pantry App, " + name + "!\nWe've added starter spices and oils to your pantry.");
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
                System.out.println("7. Logout");  // Will need to be updated to show up at the end
                System.out.println("8. View/Add to Shopping List");
                System.out.println("9. Settings");


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
                        viewAllRecipes(recipes, currentUser);
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
                    case 9:
                        showSettingsMenu(currentUser, users);
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
    	String name = JOptionPane.showInputDialog(null, "Enter the name of your food:");

        int quantity = 0;
        boolean validQuantity = false;
        while (!validQuantity) {
            String quantityStr = JOptionPane.showInputDialog(null, "Enter the quantity:");
            if (quantityStr == null) return; // cancel
            try {
                quantity = Integer.parseInt(quantityStr);
                validQuantity = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid integer for quantity.");
            }
        }

        //Category dropdown
        String categoryString = "Pantry Goods, Vegetables, Fruits, Protein, Spices, Dairy Products";
        String[] categoryArray = categoryString.split(",");
        String category = (String) JOptionPane.showInputDialog(
            null,
            "Select a category:",
            "Category",
            JOptionPane.QUESTION_MESSAGE,
            null,
            categoryArray,
            categoryArray[0]
        );

        if (category == null) return; // user cancelled
        

  
        String dateInput = JOptionPane.showInputDialog(null, "Enter expiration date (YYYY-MM-DD):");
        if (dateInput == null) return;

        LocalDate expirationDate;
        try {
            expirationDate = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        FoodItem item = new FoodItem(name, quantity, expirationDate, category, 0);
        user.getPantry().addFoodItem(item);

        JOptionPane.showMessageDialog(null, "Food item added successfully!");
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
 // Prompts the user to view one.
 // And will ask the user if they want to see recipes that contain their allergens.
 // If no ->recipes that contain any of their allergens will be filtered out (fuzzy match)
 private static void viewAllRecipes(List<Recipe> recipes, User user) {
     System.out.print("Do you want to see recipes that contain your allergens? (yes/no): ");
     String allowAllergens = scanner.nextLine().trim().toLowerCase();
     boolean showAllergenRecipes = allowAllergens.equals("yes");

     Set<String> expandedAllergens = expandAllergens(user.getAllergies());

     System.out.println("\nAll Available Recipes:");
     for (Recipe recipe : recipes) {
         boolean containsAllergen = false;
         for (String ingredient : recipe.getIngredients()) {
             if (fuzzyMatch(ingredient, expandedAllergens)) {
                 containsAllergen = true;
                 break;
             }
         }
         if (!showAllergenRecipes && containsAllergen) continue;
         System.out.println("- " + recipe.getName());
     }

     System.out.print("\nEnter the name of a recipe to view, or type 'back': ");
     String selection = scanner.nextLine().trim();

     for (Recipe recipe : recipes) {
         if (recipe.getName().equalsIgnoreCase(selection)) {
             viewRecipeDetails(recipe, user);
             return;
         }
     }

     if (!selection.equalsIgnoreCase("back")) {
         System.out.println("Recipe not found.");
     }
 }



    // View recipe suggestions based on user's pantry and ingredient match threshold
// now asks the user if they want to see recipes that contain allergens

private static void viewSuggestedRecipes(User user, List<Recipe> recipes) {
  System.out.println("\nYour current minimum ingredient match percentage is: " + user.getMatchThreshold() + "%");
  System.out.print("Would you like to change your match percentage for this session? (yes/no): ");
  String response = scanner.nextLine().trim().toLowerCase();

  int threshold = user.getMatchThreshold();
  if (response.equals("yes")) {
      System.out.println("Choose your new minimum ingredient match percentage:");
      System.out.println("1. 10%  2. 30%  3. 50%  4. 60%  5. 80%  6. 100%");
      int choice = scanner.nextInt();
      scanner.nextLine(); // clear newline
      switch (choice) {
          case 1: threshold = 10; break;
          case 2: threshold = 30; break;
          case 3: threshold = 50; break;
          case 4: threshold = 60; break;
          case 5: threshold = 80; break;
          case 6: threshold = 100; break;
          default: System.out.println("Invalid choice. Using saved value.");
      }
      user.setMatchThreshold(threshold);
  }

  System.out.print("Do you want to see recipes that contain your allergens? (yes/no): ");
  String allowAllergens = scanner.nextLine().trim().toLowerCase();
  boolean showAllergenRecipes = allowAllergens.equals("yes");
  Set<String> expandedAllergens = expandAllergens(user.getAllergies());

  double thresholdDecimal = threshold / 100.0;
  List<RecipeMatch> matches = new ArrayList<>();

  for (Recipe recipe : recipes) {
      int matchCount = 0;
      for (String ingredient : recipe.getIngredients()) {
          if (user.getPantry().hasIngredient(ingredient)) {
              matchCount++;
          }
      }

      double matchPercent = (double) matchCount / recipe.getIngredients().size();
      if (matchPercent >= thresholdDecimal) {
          boolean containsAllergen = false;
          for (String ing : recipe.getIngredients()) {
              if (fuzzyMatch(ing, expandedAllergens)) {
                  containsAllergen = true;
                  break;
              }
          }
          if (!showAllergenRecipes && containsAllergen) continue;

          matches.add(new RecipeMatch(recipe, matchPercent));
          String note = "You have " + (int)(matchPercent * 100) + "% of ingredients for " + recipe.getName() + "!";
          if (!user.getNotifications().contains(new Notifications(note))) {
              user.addNotification(new Notifications(note));
          }
      }
  }

  matches.sort((a, b) -> Double.compare(b.matchPercentage, a.matchPercentage));

  if (matches.isEmpty()) {
      System.out.println("No recipes meet your match threshold.");
      return;
  }

  System.out.println("\nSuggested Recipes:");
  for (RecipeMatch match : matches) {
      System.out.println("- " + match.recipe.getName() + " (" + (int)(match.matchPercentage * 100) + "% match)");
  }

  System.out.print("\nEnter the name of a recipe to view, or 'back': ");
  String selection = scanner.nextLine().trim();

  for (RecipeMatch match : matches) {
      if (match.recipe.getName().equalsIgnoreCase(selection)) {
          viewRecipeDetails(match.recipe, user);
          return;
      }
  }

  if (!selection.equalsIgnoreCase("back")) {
      System.out.println("Recipe not found.");
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
    
    private static void addStarterPantryItems(Pantry pantry) {
        LocalDate futureDate = LocalDate.now().plusMonths(6); // expiration far in future
        pantry.addFoodItem(new FoodItem("Salt", 1, futureDate, "Spice", 0));
        pantry.addFoodItem(new FoodItem("Pepper", 1, futureDate, "Spice", 0));
        pantry.addFoodItem(new FoodItem("Olive Oil", 1, futureDate, "Oil", 0));
        pantry.addFoodItem(new FoodItem("Vegetable Oil", 1, futureDate, "Oil", 0));
        pantry.addFoodItem(new FoodItem("Cumin", 1, futureDate, "Spice", 0));
        pantry.addFoodItem(new FoodItem("Paprika", 1, futureDate, "Spice", 0));
    }
    
    private static void showSettingsMenu(User user, List<User> users) {
        while (true) {
            System.out.println("\n--- Settings ---");
            System.out.println("1. Dietary Preferences");
            System.out.println("2. Allergies");
            System.out.println("3. Common Substitutions");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    updateDietaryPreferences(user, users);
                    break;
                case 2:
                    updateAllergies(user, users);
                    break;
                case 3:
                    updateSubstitutions(user, users);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private static void updateAllergies(User user, List<User> users) {
        while (true) {
            System.out.println("\n--- Allergy Settings ---");
            System.out.println("1. View All Allergies");
            System.out.println("2. Add Allergy");
            System.out.println("3. Remove Allergy");
            System.out.println("4. Clear All Allergies");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Allergies: " + user.getAllergies());
                    break;
                case 2:
                    List<String> common = List.of("Peanuts", "Tree Nuts", "Dairy", "Eggs", "Shellfish", "Soy", "Wheat");
                    for (int i = 0; i < common.size(); i++) {
                        System.out.println((i + 1) + ". " + common.get(i));
                    }
                    System.out.println((common.size() + 1) + ". Other");
                    System.out.print("Enter comma-separated choices: ");
                    String[] choices = scanner.nextLine().split(",");
                    List<String> updated = new ArrayList<>(user.getAllergies());
                    for (String c : choices) {
                        int idx = Integer.parseInt(c.trim()) - 1;
                        if (idx >= 0 && idx < common.size()) {
                            updated.add(common.get(idx));
                        } else if (idx == common.size()) {
                            System.out.print("Enter custom allergen: ");
                            updated.add(scanner.nextLine().trim());
                        }
                    }
                    user.setAllergies(updated);
                    CSV.exportUsersToCSV(users, "users.csv");
                    break;
                case 3:
                    System.out.println("Current allergies: " + user.getAllergies());
                    System.out.print("Enter allergy to remove: ");
                    String toRemove = scanner.nextLine().trim();
                    user.getAllergies().removeIf(a -> a.equalsIgnoreCase(toRemove));
                    CSV.exportUsersToCSV(users, "users.csv");
                    break;
                case 4:
                    user.getAllergies().clear();
                    CSV.exportUsersToCSV(users, "users.csv");
                    break;
                case 5:
                    return;
            }
        }
    }
    
    private static void updateDietaryPreferences(User user, List<User> users) {
        System.out.println("Current dietary preference: " + user.getDietaryPreferences());

        System.out.print("Would you like to change it? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            String pref;
            while (true) {
                System.out.println("Options: 'vegan', 'vegetarian', or 'none'");
                System.out.print("Enter your new preference: ");
                pref = scanner.nextLine().trim().toLowerCase();
                if (pref.equals("vegan") || pref.equals("vegetarian") || pref.equals("none")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            }

            user.getDietaryPreferences().clear();
            user.getDietaryPreferences().add(pref);
            CSV.exportUsersToCSV(users, "users.csv");

            System.out.println("Dietary preference updated to: " + pref);
        }
    }

    private static void updateSubstitutions(User user, List<User> users) {
        while (true) {
            System.out.println("\n--- Substitution Settings ---");
            System.out.println("1. View All Substitutions");
            System.out.println("2. Add Substitution");
            System.out.println("3. Remove Substitution");
            System.out.println("4. Clear All Substitutions");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            Map<String, List<String>> subs = user.getSubstitutions();

            switch (choice) {
                case 1:
                    subs.forEach((k, v) -> System.out.println("- " + k + " → " + String.join(", ", v)));
                    break;
                case 2:
                    System.out.print("Enter ingredient to substitute: ");
                    String ing = scanner.nextLine().trim();
                    System.out.print("Enter substitutes (comma-separated): ");
                    String[] subsArr = scanner.nextLine().split(",");
                    subs.put(ing, List.of(subsArr));
                    user.setSubstitutions(subs);
                    CSV.exportUsersToCSV(users, "users.csv");
                    break;
                case 3:
                    System.out.print("Enter ingredient to remove from substitutions: ");
                    subs.remove(scanner.nextLine().trim());
                    user.setSubstitutions(subs);
                    CSV.exportUsersToCSV(users, "users.csv");
                    break;
                case 4:
                    subs.clear();
                    user.setSubstitutions(subs);
                    CSV.exportUsersToCSV(users, "users.csv");
                    break;
                case 5:
                    return;
            }
        }
    }
    
 // This function shows the full recipe.
 // And now shows a summary at the top of which ingredients match allergens,
 // and which ones have substitutions saved by the user.

 private static void viewRecipeDetails(Recipe recipe, User user) {
     System.out.println("\n--- " + recipe.getName() + " ---");

     List<String> ingredients = recipe.getIngredients();
     Set<String> expandedAllergens = expandAllergens(user.getAllergies());
     Map<String, List<String>> subs = user.getSubstitutions();

     List<String> matchedAllergens = new ArrayList<>();
     List<String> matchedSubstitutes = new ArrayList<>();

     for (String ing : ingredients) {
    	 // Fuzzy matching -> expand categories like "Tree Nuts" to actual ingredients like "walnut"
         if (fuzzyMatch(ing, expandedAllergens)) {
             matchedAllergens.add(ing);
         }
         for (String subKey : subs.keySet()) {
             if (fuzzyMatch(ing, List.of(subKey))) {
                 matchedSubstitutes.add(ing);
                 break;
             }
         }
     }

     if (!matchedAllergens.isEmpty()) {
         System.out.println("\n⚠ This recipe contains ingredients you're allergic to:");
         for (String a : matchedAllergens) System.out.println("- " + a);
     }

     if (!matchedSubstitutes.isEmpty()) {
    	    System.out.println("\n These ingredients have saved substitutions:");
    	    for (String s : matchedSubstitutes) {
    	        for (String subKey : subs.keySet()) {
    	            if (fuzzyMatch(s, List.of(subKey))) {
    	                System.out.println("- " + s + " → " + String.join(", ", subs.get(subKey)));
    	                break;
    	            }
    	        }
    	    }
    	}

     System.out.println("\nIngredients:");
     for (String ing : ingredients) {
         System.out.print("- " + ing);
         if (fuzzyMatch(ing, expandedAllergens)) System.out.print(" [⚠ Allergen]");
         for (String subKey : subs.keySet()) {
             if (fuzzyMatch(ing, List.of(subKey))) {
                 System.out.print(" [Substitute: " + String.join(", ", subs.get(subKey)) + "]");
                 break;
             }
         }
         System.out.println();
     }

     System.out.println("\nInstructions:");
     System.out.println(recipe.getInstructions());
 }



 // Should return true if any term contains the target
    private static boolean fuzzyMatch(String target, Collection<String> terms) {
        String normTarget = target.toLowerCase();
        for (String term : terms) {
            String normTerm = term.toLowerCase();
            if (normTarget.contains(normTerm) || normTerm.contains(normTarget)) {
                return true;
            }
        }
        return false;
    }
    
 // Returns a list of expanded ingredients for known allergen categories
    private static Set<String> expandAllergens(List<String> userAllergies) {
        Map<String, List<String>> allergenMap = new HashMap<>();
        allergenMap.put("Tree Nuts", List.of("almond", "walnut", "cashew", "pecan", "hazelnut", "pistachio", "macadamia"));
        allergenMap.put("Shellfish", List.of("shrimp", "lobster", "crab", "scallop", "clam", "oyster"));
        allergenMap.put("Dairy", List.of("milk", "cheese", "butter", "yogurt", "cream"));
        allergenMap.put("Eggs", List.of("egg", "egg whites", "egg yolk"));
        allergenMap.put("Soy", List.of("soybean", "tofu", "soy milk", "edamame"));
        allergenMap.put("Wheat", List.of("flour", "bread", "pasta", "gluten"));

        Set<String> expanded = new HashSet<>();

        for (String allergy : userAllergies) {
            expanded.add(allergy.toLowerCase());
            if (allergenMap.containsKey(allergy)) {
                expanded.addAll(allergenMap.get(allergy));
            }
        }

        return expanded;
    }


    

}

