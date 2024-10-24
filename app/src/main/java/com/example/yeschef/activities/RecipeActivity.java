package com.example.yeschef.activities;

import android.os.Bundle;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yeschef.R;
import com.example.yeschef.models.Recipe;

public class RecipeActivity extends AppCompatActivity {

    private Spinner mealTimeSpinner;
    private EditText recipeTitleInput;
    private EditText descriptionInput;
    private EditText caloriesInput;
    private EditText proteinInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_add);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // Bind UI components
  /* mealTimeSpinner = findViewById(R.id.mealTimeSpinner);
    recipeTitleInput = findViewById(R.id.recipeTitleInput);
    descriptionInput = findViewById(R.id.descriptionInput);
    caloriesInput = findViewById(R.id.caloriesInput);
    proteinInput = findViewById(R.id.proteinInput);
    saveButton = findViewById(R.id.saveButton);

    // Handle save button click
        saveButton.setOnClickListener(v -> saveRecipe()); */

    private void saveRecipe() {
        // Get values from inputs
        String recipeTitle = recipeTitleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        int calories = Integer.parseInt(caloriesInput.getText().toString());
        int protein = Integer.parseInt(proteinInput.getText().toString());

        // Get selected meal time from Spinner
        String selectedMealTime = mealTimeSpinner.getSelectedItem().toString();
        Recipe.MealTime mealTime = Recipe.MealTime.BREAKFAST; // Default value

        switch (selectedMealTime) {
            case "Breakfast":
                mealTime = Recipe.MealTime.BREAKFAST;
                break;
            case "Lunch":
                mealTime = Recipe.MealTime.LUNCH;
                break;
            case "Dinner":
                mealTime = Recipe.MealTime.DINNER;
                break;
        }

        // Create a new Recipe object
       /* Recipe newRecipe = new Recipe(recipeTitle, mealTime);
        newRecipe.setDescription(description);
        newRecipe.setCal(calories);
        newRecipe.setProtein(protein);*/
    }
}