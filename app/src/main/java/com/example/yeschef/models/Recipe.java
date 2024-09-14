package com.example.yeschef.models;

import java.util.List;

public class Recipe {
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servingSize;
    private int prepTime;  // in minutes
    private int cookTime;  // in minutes
    private String description;
    private List<NutritionFact> nutritionInfo;
    private List<String> tags;

    public Recipe(String name, List<Ingredient> ingredients, List<Step> steps, int servingSize,
                  int prepTime, int cookTime, String description, List<NutritionFact> nutritionInfo, List<String> tags) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servingSize = servingSize;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.description = description;
        this.nutritionInfo = nutritionInfo;
        this.tags = tags;
    }

    // Method to calculate total time
    public int getTotalTime() {
        return prepTime + cookTime;
    }

    // Getters
    public String getName() { return name; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<Step> getSteps() { return steps; }
    public int getServingSize() { return servingSize; }
    public String getDescription() { return description; }
    public List<NutritionFact> getNutritionInfo() { return nutritionInfo; }
    public List<String> getTags() { return tags; }

    // toString method for displaying the recipe
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Recipe: " + name + "\n");

        result.append("Serving Size: ").append(servingSize).append("\n");
        result.append("Prep Time: ").append(prepTime).append(" minutes\n");
        result.append("Cook Time: ").append(cookTime).append(" minutes\n");
        result.append("Total Time: ").append(getTotalTime()).append(" minutes\n");
        result.append("Description: ").append(description).append("\n\n");

        result.append("Ingredients:\n");
        for (Ingredient ingredient : ingredients) {
            result.append("- ").append(ingredient.toString()).append("\n");
        }

        result.append("\nSteps:\n");
        for (Step step : steps) {
            result.append(step.toString()).append("\n");
        }

        result.append("\nNutrition Info:\n");
        for (NutritionFact nutrition : nutritionInfo) {
            result.append(nutrition.toString()).append("\n");
        }

        result.append("\nTags: ").append(String.join(", ", tags)).append("\n");

        return result.toString();
    }
}