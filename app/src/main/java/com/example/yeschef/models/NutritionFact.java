package com.example.yeschef.models;

public class NutritionFact {
    private String nutritionName;
    private double nutritionValue;
    private String nutritionType; // e.g., grams, calories, etc.

    public NutritionFact(String nutritionName, double nutritionValue, String nutritionType) {
        this.nutritionName = nutritionName;
        this.nutritionValue = nutritionValue;
        this.nutritionType = nutritionType;
    }

    @Override
    public String toString() {
        return nutritionName + " : " + nutritionValue + " " + nutritionType;
    }
}
