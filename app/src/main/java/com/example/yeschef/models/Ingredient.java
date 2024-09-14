package com.example.yeschef.models;

public class Ingredient {
    private String name;
    private String quantity; // Can include units, e.g., "1 cup"

    public Ingredient(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    // Getters
    public String getName() { return name; }
    public String getQuantity() { return quantity; }

    @Override
    public String toString() {
        return quantity + " of " + name;
    }
}
