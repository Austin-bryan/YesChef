package com.example.yeschef.models;

import java.util.List;

public class Recipe {
    private String recipeTitle;
    private int cal;
    //private int rating; //1-10, every even number being a .5 star
    private int protein;
    private String servingSize;
    private String description;
    private boolean IsVegetarian;
    private boolean IsSugarFree;
    private boolean IsGlutenFree;
    private List<String> ingredients;
    private List<String> directions;


    public Recipe(String name, List<String> ingredients, String servingSize,
                  int cal, int protein, String description, List<String> directions) {
        this.recipeTitle = name;
        this.ingredients = ingredients;
        this.directions = directions;
        this.servingSize = servingSize;
        this.cal = cal;
        this.protein = protein;
        this.description = description;
        this.directions = directions;

    }



    // Getters
    public String getTitle() { return recipeTitle; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getDirections() { return directions; }
    public String getServingSize() { return servingSize; }
    public String getDescription() { return description; }
   //TO:DO finish getters 
    //TO:DO Setters

    // TO:DO -- toString method for displaying the recipe
   // @Override
   // public String toString() {

    //}
}