package com.example.yeschef.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;  // Add this import statement
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.yeschef.R;
import com.example.yeschef.models.Recipe;
import com.example.yeschef.utils.JsonUtils;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private static final String KEY_INGREDIENTS_LIST = "ingredients_list";
    private static final String KEY_DIRECTIONS_LIST = "directions_list";

    private List<String> ingredientsList;
    private List<String> directionsList;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageButton clickedButton;
    private int clickedIndex;
    private LinearLayout categoryButtonsContainer;

    private int stepCounter = 1; // Counter for steps
    private LinearLayout directionsContainer;
    private LinearLayout ingredientsContainer;
    private boolean isAnimating = false;
    private Spinner mealTimeSpinner;
    private Spinner difficultySpinner;
    private EditText recipeTitleInput;
    private EditText descriptionInput;
    private ImageButton imageAdder;
    private EditText caloriesInput;
    private EditText proteinInput;
    private String[] images = new String[4];
    private Button saveButton;
    private boolean isVegetarian = false;
    private boolean isGlutenFree = false;
    private boolean isSugarFree = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize input fields
        View mealTitleView = view.findViewById(R.id.meal_title);
        recipeTitleInput = mealTitleView.findViewById(R.id.ingredient_step_input); // Adjust as necessary

        View mealDescriptionView = view.findViewById(R.id.meal_description);
        descriptionInput = mealDescriptionView.findViewById(R.id.ingredient_step_input); // Adjust as necessary

        caloriesInput = view.findViewById(R.id.calories_input);
        proteinInput = view.findViewById(R.id.protein_input);
        mealTimeSpinner = view.findViewById(R.id.spinner1);
        difficultySpinner = view.findViewById(R.id.difficulty_spinner);
        imageAdder = view.findViewById(R.id.image_add);

        // Initialize containers for dynamic content
        ingredientsContainer = view.findViewById(R.id.ingredients_container);
        directionsContainer = view.findViewById(R.id.directions_container); // Initialize directionsContainer

        // Set up labels and their colors
        RelativeLayout ingredientsLabel = view.findViewById(R.id.ingredients_label);
        RelativeLayout directionsLabel = view.findViewById(R.id.directions_label);
        int ingredientsColor = ((ColorDrawable) ingredientsLabel.getBackground()).getColor();
        int directionsColor = ((ColorDrawable) directionsLabel.getBackground()).getColor();

        // Setup basic text fields
        setHintText(view, R.id.meal_title, "Meal Title");
        setHintText(view, R.id.meal_description, "Description");

        // Set visibility for remove buttons
        ImageButton removeButton1 = mealTitleView.findViewById(R.id.remove_button);
        removeButton1.setVisibility(View.GONE);
        ImageButton removeButton2 = mealDescriptionView.findViewById(R.id.remove_button);
        removeButton2.setVisibility(View.GONE);

        ingredientsLabel.setClickable(false);
        directionsLabel.setClickable(false);

        initializeGalleryLauncher(view);
        initializeImageAdder();

        // Initialize lists for ingredients and directions
        if (savedInstanceState != null) {
            // Restore saved lists
            ingredientsList = savedInstanceState.getStringArrayList(KEY_INGREDIENTS_LIST);
            directionsList = savedInstanceState.getStringArrayList(KEY_DIRECTIONS_LIST);

            // Populate ingredients
            if (ingredientsList != null) {
                for (String ingredient : ingredientsList) {
                    addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, directionsContainer, ingredient);
                }
            }

            // Populate directions
            if (directionsList != null) {
                for (String direction : directionsList) {
                    addNewItem(directionsContainer, "Step(s)", directionsColor, ingredientsContainer, direction);
                }
            }
        } else {
            // Initialize empty lists
            ingredientsList = new ArrayList<>();
            directionsList = new ArrayList<>();
        }

        // Set up the meal time spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.mealtime_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTimeSpinner.setAdapter(adapter);

        // Spinner for difficulty level
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);

        // Set click listeners for toggling visibility
        ingredientsLabel.setOnClickListener(v -> toggleContainerVisibility(ingredientsContainer));
        directionsLabel.setOnClickListener(v -> toggleContainerVisibility(directionsContainer));

        // Add buttons for adding new items
        addButtonListeners(view, ingredientsContainer, directionsContainer, ingredientsColor, directionsColor);

        // Add buttons for dietary options
        Button vegetarianButton = view.findViewById(R.id.option_vegetarian);
        Button glutenFreeButton = view.findViewById(R.id.option_gluten_free);
        Button sugarFreeButton = view.findViewById(R.id.option_sugar_free);

        //Add click listeners
        // Set click listeners to toggle booleans
        vegetarianButton.setOnClickListener(v -> {
            isVegetarian = !isVegetarian;
            vegetarianButton.setSelected(isVegetarian);
        });

        glutenFreeButton.setOnClickListener(v -> {
            isGlutenFree = !isGlutenFree;
            glutenFreeButton.setSelected(isGlutenFree);
        });

        sugarFreeButton.setOnClickListener(v -> {
            isSugarFree = !isSugarFree;
            sugarFreeButton.setSelected(isSugarFree);
        });

        // Add save button click listener
        ImageButton saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            onSaveClick();
            new AlertDialog.Builder(getContext())
                    .setTitle("Recipe Saved")
                    .setMessage("The " + (recipeTitleInput.getText().toString().isEmpty() ?
                            "untitled" : recipeTitleInput.getText().toString()) + " recipe has been saved." +
                            "\nIt is safe to clear this page.")
                    .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
                    .show();
            });

        // Add clear button click listener
        ImageButton clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> {
            // Create an AlertDialog builder
            new AlertDialog.Builder(getContext())
                    .setTitle("Clear Recipe")
                    .setMessage("Are you sure you want to clear all fields?")
                    .setPositiveButton("Clear", (dialog, which) -> onClearClick())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        updateIngredientsAndDirectionsViews();

        return view;
    }

    // reset UI method
    private void resetFields() {

        EditText servingSizeInput = requireView().findViewById(R.id.serving_size_input);
        View caloriesView = getView().findViewById(R.id.calories_input);
        View proteinView = getView().findViewById(R.id.protein_input);

        // Reset dietary option Chips to unselected state
        ChipGroup dietaryOptionsGroup = requireView().findViewById(R.id.dietary_options_group);
        dietaryOptionsGroup.clearCheck();

        recipeTitleInput.setText("");     // Clear the meal title
        descriptionInput.setText("");     // Clear the meal description

        // Clear serving size
        servingSizeInput.setText("");

        if (caloriesView instanceof TextView) {
            ((TextView) caloriesView).setText("");
        }
        if (proteinView instanceof TextView) {
            ((TextView) proteinView).setText("");
        }

        Spinner mealTimeSpinner = getView().findViewById(R.id.spinner1);
        mealTimeSpinner.setSelection(0);
        Spinner difficultySpinner = getView().findViewById(R.id.difficulty_spinner);
        difficultySpinner.setSelection(0);

        Button vegetarianButton = getView().findViewById(R.id.option_vegetarian);
        Button glutenFreeButton = getView().findViewById(R.id.option_gluten_free);
        Button sugarFreeButton = getView().findViewById(R.id.option_sugar_free);

        updateIngredientsAndDirectionsViews();
    }
    private void updateIngredientsAndDirectionsViews() {
        while (ingredientsContainer.getChildCount() > 1) { // Keep the header (assume header is the first child)
            ingredientsContainer.removeViewAt(1); // Remove all but the header
        }

        // Clear existing input views in directions container
        while (directionsContainer.getChildCount() > 1) { // Keep the header (assume header is the first child)
            directionsContainer.removeViewAt(1); // Remove all but the header
        }
    }
    private void addButtonListeners(View view, LinearLayout ingredientsContainer, LinearLayout directionsContainer, int ingredientsColor, int directionsColor) {
        ImageButton addIngredientButton = view.findViewById(R.id.add_ingredient_button);
        ImageButton addDirectionButton = view.findViewById(R.id.add_direction_button);

        addIngredientButton.setOnClickListener(v -> addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, directionsContainer));
        addDirectionButton.setOnClickListener(v -> addNewItem(directionsContainer, "Step(s)", directionsColor, null));
    }

    private void toggleCategoryButtons() {
        if (categoryButtonsContainer.getVisibility() == View.GONE) {
            categoryButtonsContainer.setVisibility(View.VISIBLE);
            slideDown(categoryButtonsContainer);
        } else {
            slideUp(categoryButtonsContainer, () -> categoryButtonsContainer.setVisibility(View.GONE));
        }
    }

    private void slideDown(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, -view.getHeight(), 0);
        animation.setDuration(300);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(animation);
        view.setTranslationY(0);
    }
    private void slideUp(View view, Runnable endAction) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animation.setDuration(300);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                endAction.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(animation);
    }

    private void selectCategory(String category, Button pickCategoryButton) {
        pickCategoryButton.setText(category);
        categoryButtonsContainer.setVisibility(View.GONE);
    }
    private void toggleContainerVisibility(final LinearLayout container) {
        // Logic for toggling visibility
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_INGREDIENTS_LIST, new ArrayList<>(ingredientsList));
        outState.putStringArrayList(KEY_DIRECTIONS_LIST, new ArrayList<>(directionsList));
    }
    private void onSaveClick() {
        // Create a new Recipe object
        Recipe recipe = new Recipe();

        // Retrieve input from UI components
        String title = recipeTitleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String caloriesStr = caloriesInput.getText().toString();
        String proteinStr = proteinInput.getText().toString();
        String mealTimeStr = mealTimeSpinner.getSelectedItem().toString();
        String difficultyLevelStr = difficultySpinner.getSelectedItem().toString();

        // Convert mealTimeStr to MealTime enum
        Recipe.MealTime mealTime;
        switch (mealTimeStr) {
            case "Breakfast":
                mealTime = Recipe.MealTime.BREAKFAST;
                break;
            case "Lunch":
                mealTime = Recipe.MealTime.LUNCH;
                break;
            case "Dinner":
                mealTime = Recipe.MealTime.DINNER;
                break;
            default:
                mealTime = Recipe.MealTime.ANYTIME;
                break;
        }

        // Convert Difficulty Level string to Difficulty Level enum
        Recipe.DifficultyLevel difficultyLevel;
        switch (difficultyLevelStr) {
            case "Medium":
                difficultyLevel = Recipe.DifficultyLevel.MEDIUM;
                break;
            case "Hard":
                difficultyLevel = Recipe.DifficultyLevel.HARD;
                break;
            default:
                difficultyLevel = Recipe.DifficultyLevel.EASY;
                break;
        }

        // Parse numeric inputs (with default values if empty)
        int calories = caloriesStr.isEmpty() ? 0 : Integer.parseInt(caloriesStr);
        int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);

        // Set user input to the Recipe object
        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setCal(calories);
        recipe.setProtein(protein);
        recipe.setMealTime(mealTime);
        recipe.setDifficultyLevel(difficultyLevel);

        //Retrieve the ingredients
        ingredientsList.clear();
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View child = ingredientsContainer.getChildAt(i);
            TextInputEditText inputField = child.findViewById(R.id.ingredient_step_input);
            if (inputField != null) {
                String ingredientText = inputField.getText().toString();
                if (!ingredientText.isEmpty()) {
                    ingredientsList.add(ingredientText);
                    Log.d("OnSaveClick", "Ingredient added: " + ingredientText); // Log added ingredient
                }
            }
        }

        // Retrieve the directions
        directionsList.clear();
        for (int i = 0; i < directionsContainer.getChildCount(); i++) {
            View child = directionsContainer.getChildAt(i);
            TextInputEditText inputField = child.findViewById(R.id.ingredient_step_input);
            if (inputField != null) {
                String directionText = inputField.getText().toString();
                if (!directionText.isEmpty()) {
                    directionsList.add(directionText);
                    Log.d("OnSaveClick", "Direction added: " + directionText); // Log added direction
                }
            }
        }

        recipe.setIngredients(ingredientsList);
        recipe.setDirections(directionsList);

        recipe.setVegetarian(isVegetarian);
        recipe.setGlutenFree(isGlutenFree);
        recipe.setSugarFree(isSugarFree);
        recipe.setImages(images);

        JsonUtils.writeJsonToFile(requireContext(), recipe,"recipe.json");
        JsonUtils.logJson(recipe);

        // Log the recipe details to test the save functionality
        Log.e("RecipeTest", recipe.toString());

        // Use the file name recipe.json
        String fileName = "recipe.json";

        // Write the Recipe object to JSON file
        JsonUtils.writeJsonToFile(requireContext(), recipe, fileName);

        // Read the Recipe object back from the JSON file
        Recipe restoredRecipe = JsonUtils.readJsonFromFile(requireContext(), fileName);

        Log.e("RestoreTest", restoredRecipe.toString());
    }
    private void onClearClick() {
        resetFields();
    }

    private void initializeGalleryLauncher(View view) {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (!(result.getResultCode() == RESULT_OK && result.getData() != null))
                return;
            Uri imageUri = result.getData().getData();

            imageAdder.clearColorFilter();
            imageAdder.setColorFilter(null);
            imageAdder.setImageURI(imageUri);
            imageAdder.setPadding(0, 0, 0, 0);
        });
    }
    private void initializeImageAdder() {
        imageAdder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            galleryLauncher.launch(intent);
        });
    }

    private void setHintText(View view, int id, String text) {
        View titleItem = view.findViewById(id);
        TextInputEditText editText = titleItem.findViewById(R.id.ingredient_step_input);
        editText.setHint(text);
    }
    private void updateStepNumbers(LinearLayout itemListContainer) {
        for (int i = 0; i < itemListContainer.getChildCount(); i++) {
            View stepItem = itemListContainer.getChildAt(i);
            TextView stepText = stepItem.findViewById(R.id.circle_text);  // Text inside the circle

            if (stepText != null) {
                stepText.setText(String.valueOf(i));  // Update the step number based on its position in the list
            } else {
                Log.e("TestCount", "TextView 'circle_text' is null in step item at index " + i);
            }
        }
    }
    private void addNewItem(LinearLayout itemListContainer, String hintText, int labelColor, LinearLayout otherContainer) {
        addNewItem(itemListContainer, hintText, labelColor, otherContainer, null);
    }
    private void addNewItem(LinearLayout itemListContainer, String hintText, int labelColor, LinearLayout otherContainer, String existingText) {
        if (isAnimating) return;
        isAnimating = true;

        // Inflate the item_list layout
        View newItem = getLayoutInflater().inflate(R.layout.item_list, itemListContainer, false);
        newItem.setAlpha(0f);
        newItem.setTranslationY(-30);

        // Animate the appearance of the new item
        newItem.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> { isAnimating = false; })
                .start();

        // Set up the input field and hint
        TextInputEditText inputField = newItem.findViewById(R.id.ingredient_step_input);
        inputField.setHint(hintText); // Set the hint based on context

        if (existingText != null) {
            inputField.setText(existingText); // Pre-fill with existing text if provided
        }

        // Check if the item is being added to the directions container
        if (itemListContainer == directionsContainer) {
            FrameLayout stepFrame = newItem.findViewById(R.id.step_frame);
            stepFrame.setVisibility(View.VISIBLE); // Show step frame for directions
            new Handler().postDelayed(() -> updateStepNumbers(itemListContainer), 50);
        }

        // Set up the remove button
        ImageButton addRemoveButton = newItem.findViewById(R.id.remove_button);
        addRemoveButton.setOnClickListener(v -> {
            if (isAnimating) return;
            isAnimating = true;

            // Remove animation logic here if needed
            playDeleteAnimation(newItem, itemListContainer);
            playSlideDownAnimation(itemListContainer, itemListContainer.indexOfChild(newItem));

            if (itemListContainer == ingredientsContainer) {
                playSlideContainerAnimation(directionsContainer, newItem, -1);
            }
            if (otherContainer != null && itemListContainer.getChildCount() == 0) {
                addNewItem(itemListContainer, hintText, labelColor, otherContainer);
            }

            itemListContainer.removeView(newItem); // Remove the item from the container
            isAnimating = false; // Reset animation state
        });

        // Set the background color of the rectangle
        View leftRectangle = newItem.findViewById(R.id.left_rectangle);
        leftRectangle.setBackgroundColor(labelColor);

        // Add the new item to the container
        itemListContainer.addView(newItem);
    }

    private void playDeleteAnimation(View itemView, LinearLayout itemListContainer) {
        itemView.animate()
                .alpha(0f)
                .translationY(-30)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    // This block will run after the animation ends
                    itemListContainer.removeView(itemView);  // Remove the view from the container
                    if (itemListContainer == directionsContainer) {
                        updateStepNumbers(itemListContainer);
                    }
                    isAnimating = false;
                })
                .start();
    }

    private void playSlideDownAnimation(LinearLayout itemListContainer, int deletedIndex) {
        for (int i = deletedIndex + 1; i < itemListContainer.getChildCount(); i++) {
            View item = itemListContainer.getChildAt(i);

            item.animate()
                    .translationYBy(-item.getHeight())
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        item.setTranslationY(0);
                    })
                    .start();
        }
    }

    private void playSlideContainerAnimation(LinearLayout itemListContainer, View itemView, int direction) {
        itemListContainer.animate()
                .translationYBy(direction * itemView.getHeight())
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    itemListContainer.setTranslationY(0);
                })
                .start();
    }
}

