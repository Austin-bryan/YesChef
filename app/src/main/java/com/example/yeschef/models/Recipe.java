package com.example.yeschef.models;

import java.util.List;

public class Recipe {
    private String recipeTitle;
    private int cal;
    //private int rating; 1-10, every even number being a .5 star
    private int protein;
    private String servingSize;
    private String description;
    private boolean isVegetarian;
    private boolean isSugarFree;
    private boolean isGlutenFree;
    private List<String> ingredients;
    private List<String> directions;


    //MealTime enumeration
    public enum MealTime {
        BREAKFAST,
        LUNCH,
        DINNER
    }

    private MealTime mealTime;
    //TO:DO Difficulty level enumeration

    public Recipe(String name, List<String> ingredients, String servingSize,
                  int cal, int protein, String description, List<String> directions,
                  MealTime mealTime, boolean isVegetarian, boolean isSugarFree, boolean isGlutenFree) {
        this.recipeTitle = name;
        this.ingredients = ingredients;
        this.directions = directions;
        this.servingSize = servingSize;
        this.cal = cal;
        this.protein = protein;
        this.description = description;
        this.mealTime = mealTime;
        this.isVegetarian = isVegetarian;
        this.isSugarFree = isSugarFree;
        this.isGlutenFree = isGlutenFree;

    }



    // Getters
    public String getTitle() { return recipeTitle; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getDirections() { return directions; }
    public String getServingSize() { return servingSize; }
    public String getDescription() { return description; }
    public int getCal() { return cal; }
    public int getProtein() { return protein; }
    public MealTime getMealTime() { return mealTime; }

//Setters
    public void setTitle(String recipeTitle) { this.recipeTitle = recipeTitle; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public void setDirections(List<String> directions) { this.ingredients = directions; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }
    public void setDescription(String description) { this.description = description; }
    public void setCal(int cal) { this.cal = cal; }
    public void setProtein(int protein) { this.protein = protein; }
    public void setMealTime(MealTime mealTime) { this.mealTime = mealTime; }

   //toString displays the recipe
   @Override
   public String toString() {
       return "Recipe Title: " + recipeTitle + "\n" +
               "Meal Time: " + mealTime + "\n" +
               "Calories: " + cal + "\n" +
               "Protein: " + protein + "g\n" +
               "Description: " + description + "\n" +
               "Ingredients: \n" + formatIngredients() + "\n" +
               "Directions: \n" + formatDirections();
    }

    //formatter functions for our lists
    private String formatIngredients() {
        StringBuilder sb = new StringBuilder();
        for (String ingredient : ingredients) {
            sb.append("- ").append(ingredient).append("\n");
        }
        return sb.toString();
    }

    private String formatDirections() {
        StringBuilder sb = new StringBuilder();
        int stepNumber = 1;
        for (String direction : directions) {
            sb.append(stepNumber++).append(". ").append(direction).append("\n");
        }
        return sb.toString();
    }


}