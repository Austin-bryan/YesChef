package com.example.yeschef.fragments;

import static android.app.Activity.RESULT_OK;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
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

        initializeImageAdders(view);

        // Initialize with one item in each section
        addNewItem(ingredientsContainer, "Ingredient(s)", ingredientsColor, directionsContainer);
        addNewItem(directionsContainer, "Step(s)", directionsColor, null);

        return view;
    }
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageButton clickedButton;

    private void initializeImageAdders(View view) {
        // Register Activity Result Launcher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();

                if (clickedButton != null) {
                    // Set the image to the clicked button
                    clickedButton.setImageURI(imageUri);
                    clickedButton.setImageDrawable(null);  // Clear existing drawable first
                    clickedButton.setImageURI(imageUri);
                    clickedButton.clearColorFilter();
                    clickedButton.setColorFilter(Color.TRANSPARENT);
                    clickedButton.setPadding(0, 0, 0, 0);
                    Log.d("", "initializeImageAdders: ");
                }
                else {
                    Log.e("", "Null clickedbutton");
                }
            }
        });

        GridLayout gridLayout = view.findViewById(R.id.image_add_grid);

        int[] imageAddIds = {R.id.image_add1, R.id.image_add2, R.id.image_add3, R.id.image_add4};

        for (int i = 0; i < imageAddIds.length; i++) {
            RelativeLayout relativeLayout = gridLayout.findViewById(imageAddIds[i]);
            ImageButton imageAdd = relativeLayout.findViewById(R.id.add_photo);
            initializeImageAdder(imageAdd);
        }
    }

    private void initializeImageAdder(ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            // Store reference to the clicked button
            clickedButton = imageButton;
            galleryLauncher.launch(intent);
        });
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