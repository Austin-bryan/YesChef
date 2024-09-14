package com.example.yeschef.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.yeschef.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        LinearLayout ingredientsContainer = view.findViewById(R.id.ingredients_container);
        LinearLayout directionsContainer = view.findViewById(R.id.directions_container);

        // Get the color of the ingredients and directions labels
        int ingredientsColor = ((ColorDrawable) view.findViewById(R.id.ingredients_label).getBackground()).getColor();
        int directionsColor = ((ColorDrawable) view.findViewById(R.id.directions_label).getBackground()).getColor();

        // Setup basic text fields with proper hints
        setHintText(view, R.id.meal_title, "Meal Title");
        setHintText(view, R.id.meal_description, "Description");
        setHintText(view, R.id.meal_nutrition, "Nutrition");

        // Initialize with one item in each section
        addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, directionsContainer);
        addNewItem(directionsContainer, "Step(s)", directionsColor, null);

        return view;
    }

    private void setHintText(View view, int id, String text) {
        View titleItem = view.findViewById(id);
        TextInputEditText editText = titleItem.findViewById(R.id.ingredient_step_input);
        editText.setHint(text);
    }

    // Method to add a new item to a section (either ingredients or directions)
    private void addNewItem(LinearLayout itemListContainer, String hintText, int labelColor, LinearLayout otherContainer) {
        // Inflate the item_list layout
        View newItem = getLayoutInflater().inflate(R.layout.item_list, itemListContainer, false);

        // Set initial alpha to 0 (for fade-in effect)
        newItem.setAlpha(0f);
        newItem.setTranslationY(-50);  // Start slightly above for slide-in

        // Animate the fade-in and slide-down effect
        newItem.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Find the input field and set its hint
        TextInputEditText inputField = newItem.findViewById(R.id.ingredient_step_input);
        inputField.setHint(hintText);

        // Find the add/remove button in the item
        ImageButton addRemoveButton = newItem.findViewById(R.id.add_remove_button);
        View leftRectangle = newItem.findViewById(R.id.left_rectangle);

        // Set the color of the button to match the label's color
        addRemoveButton.setColorFilter(labelColor);
        leftRectangle.setBackgroundColor(labelColor);

        // Set initial button state as "add"
        addRemoveButton.setImageResource(R.drawable.ic_add_circle);
        addRemoveButton.setTag("add");

        FrameLayout frameLayout = newItem.findViewById(R.id.circle_frame);  // Replace R.id.frame_layout with the actual ID of your FrameLayout
        boolean isDirections = "directions".equals(itemListContainer.getTag());

        if (isDirections) {
            frameLayout.setVisibility(View.VISIBLE);  // Set to VISIBLE
            TextView stepText = newItem.findViewById(R.id.circle_text);
            int stepNumber = itemListContainer.getChildCount();  // Get current step number
            stepText.setText(String.valueOf(stepNumber));
        } else {
            frameLayout.setVisibility(View.GONE);  // Hide it for non-directions
        }

        addRemoveButton.setVisibility(View.VISIBLE);

        // Set click listener to dynamically add or remove items
        addRemoveButton.setOnClickListener(v -> {
            String currentState = (String) addRemoveButton.getTag();

            if (currentState.equals("add")) {
                // Change to "remove" state
                addRemoveButton.setImageResource(R.drawable.ic_remove_outline);
                addRemoveButton.setTag("remove");

                // Animate the other container to slide down
                if (otherContainer != null) {
                    otherContainer.animate()
                            .translationYBy(addRemoveButton.getHeight())
                            .setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> {
                                // Reset translation to avoid jumps
                                otherContainer.setTranslationY(0);
                                addNewItem(itemListContainer, hintText, labelColor, otherContainer);
                            })
                            .start();
                } else {
                    addNewItem(itemListContainer, hintText, labelColor, otherContainer);
                }

            } else {
                // Slide and fade out the item
                newItem.animate()
                        .alpha(0f)
                        .translationY(-50)
                        .setDuration(300)
                        .withEndAction(() -> {
                            itemListContainer.removeView(newItem);
                            itemListContainer.requestLayout();  // Force layout recalculation
                            updateStepNumbers(itemListContainer);
                        })
                        .start();

                // Move up all items below the removed one
                int startIndex = itemListContainer.indexOfChild(newItem);
                int height = 0;
                for (int i = startIndex; i < itemListContainer.getChildCount(); i++) {
                    View item = itemListContainer.getChildAt(i);
                    height = item.getHeight();

                    // Slide each item up by the height of the removed item
                    item.animate()
                            .translationYBy(-height)
                            .setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> item.setTranslationY(0))  // Reset translation after animation
                            .start();
                }

                // Animate the other container to slide up
                if (otherContainer != null) {
                    otherContainer.animate()
                            .translationYBy(-height)
                            .setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> otherContainer.setTranslationY(0))  // Reset translation after animation
                            .start();
                }
            }
        });

        // Add the new item to the container
        itemListContainer.addView(newItem);

        // Ensure the other container is properly adjusted
        if (otherContainer != null) {
            otherContainer.requestLayout();
        }
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
                Log.e("AddFragment", "TextView 'circle_text' is null in step item at index " + i);
            }
        }
    }
}