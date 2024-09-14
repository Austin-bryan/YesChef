package com.example.yeschef.models;

public class Step {
    private int stepNumber;
    private String description;

    public Step(int stepNumber, String description) {
        this.stepNumber = stepNumber;
        this.description = description;
    }

    // Getters
    public int getStepNumber() { return stepNumber; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "Step " + stepNumber + ": " + description;
    }
}
