package com.example.yeschef.models;

import java.util.List;

public class FilterParams {
    public String description;

    public NumericalParam servingSizeParam;
    public NumericalParam calorieParam;
    public NumericalParam proteinParam;

    public List<String> difficulty;
    public List<String> mealtime;
    public List<String> dietaryOptions;
    public List<String> ingredients;
    public List<String> directions;

    // Constructor
    public FilterParams(
            String description,
            NumericalParam servingSizeParam, NumericalParam calorieParam, NumericalParam proteinParam,
            List<String> difficulty, List<String> mealtime, List<String> dietaryOptions,
            List<String> ingredients, List<String> directions) {
        this.description = description;

        this.servingSizeParam = servingSizeParam;
        this.calorieParam = calorieParam;
        this.proteinParam = proteinParam;

        this.difficulty = difficulty;
        this.mealtime = mealtime;
        this.dietaryOptions = dietaryOptions;

        this.ingredients = ingredients;
        this.directions = directions;
    }
}