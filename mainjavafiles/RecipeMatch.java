public class RecipeMatch {  
    Recipe recipe;
    double matchPercentage;

    public RecipeMatch(Recipe recipe, double matchPercentage) {
        this.recipe = recipe;
        this.matchPercentage = matchPercentage;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }
}
