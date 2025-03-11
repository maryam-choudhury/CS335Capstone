package mindful_consumption;

import java.util.List;

public class Recipe {
    private String name;
    private List<FoodItem> ingredients;
    private int prepTime; // in minutes
    private String instructions;

    public Recipe(String name, List<FoodItem> ingredients, int prepTime, String instructions) {
        this.name = name;
        this.ingredients = ingredients;
        this.prepTime = prepTime;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public List<FoodItem> getIngredients() {
        return ingredients;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public String getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recipe: ").append(name).append("\n");
        sb.append("Prep Time: ").append(prepTime).append(" minutes\n");
        sb.append("Ingredients:\n");
        for (FoodItem item : ingredients) {
            sb.append(" - ").append(item.getName()).append(" (").append(item.getQuantity()).append(")\n");
        }
        sb.append("Instructions: ").append(instructions).append("\n");
        return sb.toString();
    }
}
