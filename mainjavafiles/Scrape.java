import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Scrape {
	public static void main(String[] args) {
        try {
            //url
            String url = "https://www.budgetbytes.com/asparagus-soup/";
            
            //fetch the HTML content
            Document doc = Jsoup.connect(url).get();

            //extract title
            String title = doc.select("h1.entry-title").text();
            System.out.println("Recipe Title: " + title);

            //extract ingredients
            Elements ingredients = doc.select("li.wprm-recipe-ingredient");
            System.out.println("\nIngredients:");
            for (Element ingredient : ingredients) {
                System.out.println("- " + ingredient.text());
            }

            //extract instructions
            Elements instructions = doc.select("div.wprm-recipe-instruction-text");
            System.out.println("\nInstructions:");
            for (Element step : instructions) {
                System.out.println(step.text());
            }

        } catch (IOException e) {
            System.err.println("Error scraping the recipe: " + e.getMessage());
        }
    }

}
