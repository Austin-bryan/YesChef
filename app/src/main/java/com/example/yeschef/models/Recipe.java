package com.example.yeschef.models;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

public class Recipe {
    private String recipeTitle;
    private int cal;
    //private int rating; 1-10, every even number being a .5 star
    private int protein;
    private String servingSize;
    private String description;
    private boolean isVegetarian = false;
    private boolean isSugarFree = false;
    private boolean isGlutenFree = false;
    private List<String> ingredients;
    private List<String> directions;


    //MealTime enumeration
    public enum MealTime {
        ANYTIME,
        BREAKFAST,
        LUNCH,
        DINNER
    }

    //Difficulty level enumeration
    public enum DifficultyLevel{
        EASY,
        MEDIUM,
        HARD
    }

    private MealTime mealTime;
    private DifficultyLevel difficultyLevel;

    public Recipe(String name, List<String> ingredients, String servingSize,
                  int cal, int protein, String description, List<String> directions,
                  MealTime mealTime, boolean isVegetarian, boolean isSugarFree, boolean isGlutenFree, DifficultyLevel difficultyLevel) {
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
        this.difficultyLevel = difficultyLevel;

    }
    public Recipe() {
        this.recipeTitle = "";
        this.servingSize = "";
        this.description = "";
        this.cal = protein = 0;
        this.ingredients = new LinkedList<String>();  // Initialize as empty list
        this.directions = new LinkedList<String>();
        this.mealTime = MealTime.ANYTIME;
        this.difficultyLevel = DifficultyLevel.EASY;


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
    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }

   //Setters
    public void setTitle(String recipeTitle) { this.recipeTitle = recipeTitle; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public void setDirections(List<String> directions) { this.directions = directions; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }
    public void setDescription(String description) { this.description = description; }
    public void setCal(int cal) { this.cal = cal; }
    public void setProtein(int protein) { this.protein = protein; }
    public void setMealTime(MealTime mealTime) { this.mealTime = mealTime; }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public void setVegetarian(boolean isVegetarian) { this.isVegetarian = isVegetarian; }
    public void setGlutenFree(boolean isGlutenFree) { this.isGlutenFree = isGlutenFree; }
    public void setSugarFree(boolean isSugarFree) { this.isSugarFree = isSugarFree; }

   //toString displays the recipe
   @Override
   public String toString() {
       return "Recipe Title: " + recipeTitle + "\n" +
               "Description: " + description + "\n" +
               "Meal Time: " + mealTime + "\n" +
               "Difficulty Level: " + difficultyLevel + "\n" +
               "Calories: " + cal + "\n" +
               "Protein: " + protein + "g\n" +
               "Vegetarian: " + formatBool(isVegetarian) + "\n" +
               "Gluten Free: " + formatBool(isGlutenFree) + "\n" +
               "Sugar Free: " + formatBool(isSugarFree) + "\n" +
               "Ingredients: \n" + formatIngredients() + "\n" +
               "Directions: \n" + formatDirections();
    }

    //formatter functions for booleans
    private String formatBool(boolean isSomething) {
      String answer;
        if (isSomething) {
            answer = "yes";
        } else {
            answer = "no";
        }
        return answer;
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

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this); // Converts the Recipe object to JSON string
    }

}