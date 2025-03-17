import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Recipe {
    private String name;
    private List<String> ingredients;

    public Recipe(String name, List<String> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public static List<Recipe> loadHardcodedRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Caprese Sandwich", Arrays.asList("Bread", "Tomato", "Mozzarella", "Basil", "Olive Oil")));
        recipes.add(new Recipe("Macaroni and Cheese", Arrays.asList("Macaroni", "Cheese", "Milk", "Butter")));
        return recipes;
    }
}

