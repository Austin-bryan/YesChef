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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize containers for dynamic content
        ingredientsContainer = view.findViewById(R.id.ingredients_container);
        directionsContainer = view.findViewById(R.id.directions_container); // Initialize directionsContainer

        // Set up labels
        RelativeLayout ingredientsLabel = view.findViewById(R.id.ingredients_label);
        RelativeLayout directionsLabel = view.findViewById(R.id.directions_label);
        int ingredientsColor = ((ColorDrawable) ingredientsLabel.getBackground()).getColor();
        int directionsColor = ((ColorDrawable) directionsLabel.getBackground()).getColor();

        // Setup basic text fields
        setHintText(view, R.id.meal_title, "Meal Title");
        setHintText(view, R.id.meal_description, "Description");

        // Hide close buttons
        View mealTitleView = view.findViewById(R.id.meal_title);
        View mealDescriptionView = view.findViewById(R.id.meal_description);

        ImageButton removeButton1 = mealTitleView.findViewById(R.id.remove_button);
        removeButton1.setVisibility(View.GONE);

        ImageButton removeButton2 = mealDescriptionView.findViewById(R.id.remove_button);
        removeButton2.setVisibility(View.GONE);


        initializeImageAdders(view);
//        initializeCategorySelection(view);

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

        Spinner spinner = view.findViewById(R.id.spinner1);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.mealtime_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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
        if (isAnimating)
            return;
        isAnimating = true;

        View newItem = getLayoutInflater().inflate(R.layout.item_list, itemListContainer, false);
        newItem.setAlpha(0f);
        newItem.setTranslationY(-30);
        newItem.animate()
            .alpha(1f)
            .translationY(0)
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> { isAnimating = false; })
            .start();

        if (itemListContainer == ingredientsContainer) {
            directionsContainer.setTranslationY(-30);
            directionsContainer.animate()
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        }

        TextInputEditText inputField = newItem.findViewById(R.id.ingredient_step_input);
        FrameLayout stepFrame = newItem.findViewById(R.id.step_frame);

        // Use stepCounter to set the hint for the step number
        if (itemListContainer == directionsContainer) {
            stepFrame.setVisibility(View.VISIBLE);
            inputField.setHint("Step");

            // Add a delay before updating step numbers
            new Handler().postDelayed(() -> {
                updateStepNumbers(itemListContainer);
            }, 50);  // 500 ms delay
        } else {
            inputField.setHint(hintText);
        }

        if (existingText != null) {
            inputField.setText(existingText);
        }

        ImageButton addRemoveButton = newItem.findViewById(R.id.remove_button);
        View leftRectangle = newItem.findViewById(R.id.left_rectangle);

        addRemoveButton.setOnClickListener(v -> {
            if (isAnimating)
                return;
            isAnimating = true;

            playDeleteAnimation(newItem, itemListContainer);
            playSlideDownAnimation(itemListContainer, itemListContainer.indexOfChild(newItem));

            if (itemListContainer == ingredientsContainer)
                playSlideContainerAnimation(directionsContainer, newItem, -1);
            if (otherContainer != null && itemListContainer.getChildCount() == 0) {
                addNewItem(itemListContainer, hintText, labelColor, otherContainer);
            }
        });

        // Set the background color of the rectangle
        leftRectangle.setBackgroundColor(labelColor);
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
