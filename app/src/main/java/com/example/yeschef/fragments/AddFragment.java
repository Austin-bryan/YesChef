package com.example.yeschef.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.yeschef.R;
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
    private LinearLayout directionsContainer; // Added directionsContainer as a member variable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize containers for dynamic content
        LinearLayout ingredientsContainer = view.findViewById(R.id.ingredients_container);
        directionsContainer = view.findViewById(R.id.directions_container); // Initialize directionsContainer

        // Set up labels
        TextView ingredientsLabel = view.findViewById(R.id.ingredients_label);
        TextView directionsLabel = view.findViewById(R.id.directions_label);
        int ingredientsColor = ((ColorDrawable) ingredientsLabel.getBackground()).getColor();
        int directionsColor = ((ColorDrawable) directionsLabel.getBackground()).getColor();

        // Setup basic text fields
        setHintText(view, R.id.meal_title, "Meal Title");
        setHintText(view, R.id.meal_description, "Description");
        setHintText(view, R.id.meal_nutrition, "Nutrition");

        initializeImageAdders(view);
        initializeCategorySelection(view);

        // Restore saved state or initialize with one item
        if (savedInstanceState != null) {
            ingredientsList = savedInstanceState.getStringArrayList(KEY_INGREDIENTS_LIST);
            directionsList = savedInstanceState.getStringArrayList(KEY_DIRECTIONS_LIST);
            if (ingredientsList != null) {
                for (String ingredient : ingredientsList) {
                    addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, null, ingredient);
                }
            }
            if (directionsList != null) {
                for (String direction : directionsList) {
                    addNewItem(directionsContainer, "Step(s)", directionsColor, null, direction);
                }
            }
        } else {
            ingredientsList = new ArrayList<>();
            directionsList = new ArrayList<>();
            addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, directionsContainer);
            addNewItem(directionsContainer, "Step(s)", directionsColor, null);
        }

        // Set click listeners for toggling visibility
        ingredientsLabel.setOnClickListener(v -> toggleContainerVisibility(ingredientsContainer));
        directionsLabel.setOnClickListener(v -> toggleContainerVisibility(directionsContainer));

        // Add buttons for adding new items
        addButtonListeners(view, ingredientsContainer, directionsContainer, ingredientsColor, directionsColor);

        return view;
    }

    private void addButtonListeners(View view, LinearLayout ingredientsContainer, LinearLayout directionsContainer, int ingredientsColor, int directionsColor) {
        ImageButton addIngredientButton = view.findViewById(R.id.add_ingredient_button);
        ImageButton addDirectionButton = view.findViewById(R.id.add_direction_button);

        addIngredientButton.setOnClickListener(v -> addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, directionsContainer));
        addDirectionButton.setOnClickListener(v -> addNewItem(directionsContainer, "Step(s)", directionsColor, null));
    }

    private void initializeCategorySelection(View view) {
        Button pickCategoryButton = view.findViewById(R.id.pick_category_button);
        categoryButtonsContainer = view.findViewById(R.id.category_buttons_container);

        pickCategoryButton.setOnClickListener(v -> toggleCategoryButtons());

        view.findViewById(R.id.breakfast_button).setOnClickListener(v -> selectCategory("Breakfast", pickCategoryButton));
        view.findViewById(R.id.lunch_button).setOnClickListener(v -> selectCategory("Lunch", pickCategoryButton));
        view.findViewById(R.id.dinner_button).setOnClickListener(v -> selectCategory("Dinner", pickCategoryButton));
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
        if (container.getVisibility() == View.VISIBLE) {
            container.animate()
                    .alpha(0f)
                    .translationY(-30)
                    .setDuration(300)
                    .withEndAction(() -> container.setVisibility(View.GONE))
                    .start();
        } else {
            container.setVisibility(View.VISIBLE);
            container.setAlpha(0f);
            container.setTranslationY(-30);
            container.animate()
                    .alpha(1f)
                    .translationY(0)
                    .setDuration(300)
                    .start();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_INGREDIENTS_LIST, new ArrayList<>(ingredientsList));
        outState.putStringArrayList(KEY_DIRECTIONS_LIST, new ArrayList<>(directionsList));
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

    private void addNewItem(LinearLayout itemListContainer, String hintText, int labelColor, LinearLayout otherContainer) {
        addNewItem(itemListContainer, hintText, labelColor, otherContainer, null);
    }

    private void addNewItem(LinearLayout itemListContainer, String hintText, int labelColor, LinearLayout otherContainer, String existingText) {
        View newItem = getLayoutInflater().inflate(R.layout.item_list, itemListContainer, false);
        newItem.setAlpha(0f);
        newItem.setTranslationY(-30);
        newItem.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        TextInputEditText inputField = newItem.findViewById(R.id.ingredient_step_input);

        // Use stepCounter to set the hint for the step number
        if (itemListContainer == directionsContainer) {
            inputField.setHint("Step " + stepCounter++); // Increment step counter for each new direction
        } else {
            inputField.setHint(hintText);
        }

        if (existingText != null) {
            inputField.setText(existingText);
        }

        ImageButton addRemoveButton = newItem.findViewById(R.id.add_remove_button);
        View leftRectangle = newItem.findViewById(R.id.left_rectangle);

        addRemoveButton.setOnClickListener(v -> {
            if (inputField.getText().toString().isEmpty()) {
                // Remove the item if the input field is empty
                itemListContainer.removeView(newItem);
                if (otherContainer != null && itemListContainer.getChildCount() == 0) {
                    addNewItem(itemListContainer, hintText, labelColor, otherContainer);
                }
            } else {
                // Add a new empty item to the list
                addNewItem(itemListContainer, hintText, labelColor, otherContainer);
            }
        });

        // Set the background color of the rectangle
        leftRectangle.setBackgroundColor(labelColor);
        itemListContainer.addView(newItem);
    }
}
