import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


class Recipe {
    private String name;
    private List<String> ingredients;
    private String instructions;

    public Recipe(String name, List<String> ingredients, String instructions) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
    
    public String getInstructions() {
        return instructions;
    }

    
    public static List<Recipe> loadHardcodedRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Caprese Sandwich", Arrays.asList("Bread", "Tomato", "Mozzarella", "Basil", "Olive Oil"), "1. Assemble the sandwich"));
        recipes.add(new Recipe("Macaroni and Cheese", Arrays.asList("Macaroni", "Cheese", "Milk", "Butter"), "1. Boil the macaroni  2. Stir in cheese, milk, and butter"));
        return recipes;
    }
    
    
    public static List<Recipe> loadScrapedRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        List<String> urls = Arrays.asList(
            "https://www.budgetbytes.com/asparagus-soup/",
            "https://www.budgetbytes.com/easy-orange-chicken/",
            "https://www.budgetbytes.com/chicken-noodle-soup/",
            "https://www.budgetbytes.com/cacio-e-pepe/",
            "https://www.budgetbytes.com/glazed-pork-chops/",
            "https://www.budgetbytes.com/cheesy-skillet-ravioli/",
            "https://www.budgetbytes.com/shrimp-scampi/",
            "https://www.budgetbytes.com/street-corn-salad/",
            "https://www.budgetbytes.com/chicken-parmesan/",
            "https://www.budgetbytes.com/lemony-chickpea-soup/",
            "https://www.budgetbytes.com/chicken-stir-fry/",
            "https://www.budgetbytes.com/oatmeal-cookies/",
            "https://www.budgetbytes.com/garlic-butter-baked-cod/",
            "https://www.budgetbytes.com/creamy-chicken-and-gnocchi/",
            "https://www.budgetbytes.com/saucy-white-beans-with-spinach/",
            "https://www.budgetbytes.com/guacamole-salad/",
            "https://www.budgetbytes.com/cowboy-caviar/",
            "https://www.budgetbytes.com/one-pot-veggie-rice-bowl/",
            "https://www.budgetbytes.com/one-pot-creamy-mushroom-pasta/",
            "https://www.budgetbytes.com/curried-red-lentil-and-pumpkin-soup/"
        );

        for (String url : urls) {
            try {
                Document doc = Jsoup.connect(url).get();
                String title = doc.select("h1.entry-title").text();
                Elements ingredients = doc.select("li.wprm-recipe-ingredient");
                Elements instructions = doc.select("div.wprm-recipe-instruction-text");

                List<String> ingredientList = new ArrayList<>();
                for (Element ingredient : ingredients) {
                    ingredientList.add(ingredient.text());
                }

                StringBuilder instructionText = new StringBuilder();
                for (Element step : instructions) {
                    instructionText.append(step.text()).append("\n");
                }

                recipes.add(new Recipe(title, ingredientList, instructionText.toString()));
            } catch (IOException e) {
                System.err.println("Error scraping the recipe from " + url + ": " + e.getMessage());
            }
        }
        return recipes;
    }
    
 
}


