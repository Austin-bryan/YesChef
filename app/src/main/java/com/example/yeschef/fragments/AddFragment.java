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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.yeschef.R;
import com.example.yeschef.models.Recipe;
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
    private LinearLayout categoryButtonsContainer;

    private int stepCounter = 1; // Counter for steps
    private LinearLayout directionsContainer;
    private LinearLayout ingredientsContainer;
    private boolean isAnimating = false;
    private Spinner mealTimeSpinner;
    private EditText recipeTitleInput;
    private EditText descriptionInput;
    private EditText caloriesInput;
    private EditText proteinInput;
    private Button saveButton;


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

        initializeImageAdders(view);

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
            addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, directionsContainer, null);
            addNewItem(directionsContainer, "Step(s)", directionsColor, ingredientsContainer, null);
        }

        // Set up the meal time spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.mealtime_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTimeSpinner.setAdapter(adapter);

        // Set click listeners for toggling visibility
        ingredientsLabel.setOnClickListener(v -> toggleContainerVisibility(ingredientsContainer));
        directionsLabel.setOnClickListener(v -> toggleContainerVisibility(directionsContainer));

        // Add buttons for adding new items
        addButtonListeners(view, ingredientsContainer, directionsContainer, ingredientsColor, directionsColor);

        // Add save button click listener
        Button saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> onSaveClick());

        return view;

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
//        if (container.getVisibility() == View.VISIBLE) {
//            container.animate()
//                    .alpha(0f)
//                    .translationY(-30)
//                    .setDuration(300)
//                    .withEndAction(() -> container.setVisibility(View.GONE))
//                    .start();
//        } else {
//            container.setVisibility(View.VISIBLE);
//            container.setAlpha(0f);
//            container.setTranslationY(-30);
//            container.animate()
//                    .alpha(1f)
//                    .translationY(0)
//                    .setDuration(300)
//                    .start();
//        }
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

        // Parse numeric inputs (with default values if empty)
        int calories = caloriesStr.isEmpty() ? 0 : Integer.parseInt(caloriesStr);
        int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);

        // Set user input to the Recipe object
        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setCal(calories);
        recipe.setProtein(protein);
        recipe.setMealTime(mealTime);


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

        // Log the recipe details to test the save functionality
        Log.e("RecipeTest", recipe.toString());
    }

    private void initializeImageAdders(View view) {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (clickedButton != null) {
                    clickedButton.setImageURI(imageUri);
                    clickedButton.clearColorFilter();
                    clickedButton.setPadding(0, 0, 0, 0);
                }
            }
        });

        GridLayout gridLayout = view.findViewById(R.id.image_add_grid);
        int[] imageAddIds = {R.id.image_add1, R.id.image_add2, R.id.image_add3, R.id.image_add4};

        for (int imageAddId : imageAddIds) {
            RelativeLayout relativeLayout = gridLayout.findViewById(imageAddId);
            ImageButton imageAdd = relativeLayout.findViewById(R.id.add_photo);
            initializeImageAdder(imageAdd);
        }
    }
    private void initializeImageAdder(ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            clickedButton = imageButton;
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
                // Only set text if the TextView is not null
                stepText.setText(String.valueOf(i));  // Update the step number based on its position in the list
            } else {
                // Optional: Log or handle the case where the TextView is missing
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
                .alpha(0f)  // Fade out
                .translationY(-30)  // Translate up
                .setDuration(300)  // Animation duration
                .setInterpolator(new AccelerateDecelerateInterpolator())  // Smooth animation
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
        // Loop through all items after the one being deleted
        for (int i = deletedIndex + 1; i < itemListContainer.getChildCount(); i++) {
            View item = itemListContainer.getChildAt(i);

            // Apply translation animation only to items below the deleted one
            item.animate()
                .translationYBy(-item.getHeight())  // Move down by the height of the deleted item
                .setDuration(300)  // Set the duration for the animation
                .setInterpolator(new AccelerateDecelerateInterpolator())  // Smooth animation
                .withEndAction(() -> {
                    // Reset translation after the animation completes
                    item.setTranslationY(0);
                })
                .start();
        }
    }
    private void playSlideContainerAnimation(LinearLayout itemListContainer, View itemView, int direction) {
        itemListContainer.animate()
            .translationYBy(direction * itemView.getHeight())  // Move down by the height of the deleted item
            .setDuration(300)  // Set the duration for the animation
            .setInterpolator(new AccelerateDecelerateInterpolator())  // Smooth animation
            .withEndAction(() -> {
                // Reset translation after the animation completes
                itemListContainer.setTranslationY(0);
            })
            .start();
    }
}