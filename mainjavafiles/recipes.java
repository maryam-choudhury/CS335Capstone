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

    // Returns a few hardcoded (non URL) recipes for fallback 
    public static List<Recipe> loadHardcodedRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Caprese Sandwich", Arrays.asList("Bread", "Tomato", "Mozzarella", "Basil", "Olive Oil"),
                "1. Layer tomato and mozzarella on bread.\n2. Add basil and olive oil.\n3. Serve."));
        recipes.add(new Recipe("Macaroni and Cheese", Arrays.asList("Macaroni", "Cheese", "Milk", "Butter"),
                "1. Cook macaroni.\n2. Melt cheese with milk and butter.\n3. Combine."));
        return recipes;
    }

    // Scrapes curated and dynamically loaded recipes based on dietary preference
    public static List<Recipe> loadScrapedRecipes(List<String> dietaryPreferences) {
        List<Recipe> recipes = new ArrayList<>();

        // Curated URLs = ones we've hardcoded in via URL
        List<String> mustHaveUrls = Arrays.asList(
            "https://www.budgetbytes.com/asparagus-soup/",
            "https://www.budgetbytes.com/one-pot-veggie-rice-bowl/",
            "https://www.budgetbytes.com/saucy-white-beans-with-spinach/"
        );

        for (String url : mustHaveUrls) {
            Recipe recipe = scrapeRecipe(url);
            if (recipe != null) {
                recipes.add(recipe);
                System.out.println("Loaded curated recipe: " + recipe.getName());
            }
        }

        // Determine which recipe category page to scrape based on preff
        List<String> baseUrls = new ArrayList<>();
        if (dietaryPreferences.contains("vegan")) {
            baseUrls.add("https://www.budgetbytes.com/category/recipes/vegetarian/vegan/");
        } else if (dietaryPreferences.contains("vegetarian")) {
            baseUrls.add("https://www.budgetbytes.com/category/recipes/vegetarian/");
        } else {
            baseUrls.add("https://www.budgetbytes.com/");
        }

        // Scrape recipe links from category page -> later expand to more categ.
        for (String categoryUrl : baseUrls) {
            try {
                Document categoryDoc = Jsoup.connect(categoryUrl).get();

                // Had to change jsoup selector 
                Elements recipeLinks = categoryDoc.select("div.archive-post-listing article a");

                int count = 0;
                for (Element link : recipeLinks) {
                    String url = link.attr("href");
                    if (!url.contains("category")) {
                        Recipe recipe = scrapeRecipe(url);
                        if (recipe != null) {
                            recipes.add(recipe);
                            System.out.println("Dynamically scraped recipe: " + recipe.getName());
                            count++;
                        }
                    }
                    if (count >= 10) break; // Scrape limit for performance (was too slow prev)
                }
            } catch (IOException e) {
                System.err.println("Error scraping category: " + e.getMessage());
            }
        }

        return recipes;
    }

    //  Extracts a single recipe from URL
    private static Recipe scrapeRecipe(String url) {
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

            return new Recipe(title, ingredientList, instructionText.toString());
        } catch (IOException e) {
            System.err.println("Error scraping the recipe from " + url + ": " + e.getMessage());
            return null;
        }
    }
}


