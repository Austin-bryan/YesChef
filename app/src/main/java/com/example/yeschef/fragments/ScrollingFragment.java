package com.example.yeschef.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.util.Log;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.yeschef.R;
import com.example.yeschef.models.Recipe;
import com.example.yeschef.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class ScrollingFragment extends Fragment {

    private GridLayout recipeContainer;
    private SearchView searchView;
    private ImageButton delRecipeButton;
    private int recipeCount = 0; // Start with 0 recipes

    // For deletion of recipes
    private boolean isDeleteMode = false;
    private final Map<View, Integer> selectedRecipes = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling, container, false);

        // Initialize the GridLayout, SearchView, and Button
        recipeContainer = view.findViewById(R.id.recipe_container);
        searchView = view.findViewById(R.id.searchView);
        delRecipeButton = view.findViewById(R.id.delRecipeButton);

        Map<Integer, Recipe> loadedRecipeMap = new HashMap<>();
        loadedRecipeMap = JsonUtils.loadRecipeMapFromJson(requireContext(), "recipes.json");

        for (Map.Entry<Integer, Recipe> entry : loadedRecipeMap.entrySet()) {
            Integer recipeId = entry.getKey();
            Recipe recipe = entry.getValue();

            addRecipeItem(recipeId, recipe);

            // For deletion
            delRecipeButton.setOnClickListener(v -> {
                    toggleDeleteMode();
            });
        }

        return view;
    }

    private void addRecipeItem(int recipeId, Recipe recipe) {
        // Inflate the recipe item layout
        View recipeItemView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_item, recipeContainer, false);

//        String fileName = "recipes.json";
//        Recipe recipe = JsonUtils.readJsonFromFile(requireContext(), fileName);

        TextView recipeTitleTextView = recipeItemView.findViewById(R.id.recipe_name);
        recipeTitleTextView.setText(recipe.getTitle());

        ImageButton recipeImageButton = recipeItemView.findViewById(R.id.recipe_image);
        Log.d("Test", recipe.getImage().toString());
        recipeImageButton.setImageURI(recipe.getImage());

        recipeImageButton.setOnClickListener(v -> {
        // Set the OnClickListener for the recipe item view
        if (isDeleteMode) {
            // Get reference to the CardView (the parent of the recipe item)
            CardView recipeCardView = recipeItemView.findViewById(R.id.recipe_card_view);

            // Check if the recipe item is already selected
            boolean isSelected = selectedRecipes.containsKey(recipeItemView);

            if (isSelected) {
                // Deselect and reset background to default (no highlight)
                recipeCardView.setCardBackgroundColor(getResources().getColor(android.R.color.white)); // Remove border
                selectedRecipes.remove(recipeItemView); // Unselect recipe
            } else {
                // Select and apply border (highlight)
                recipeCardView.setCardBackgroundColor(getResources().getColor(R.color.pink)); // Apply pink border
                selectedRecipes.put(recipeItemView, recipeId); // Select recipe for deletion
            }
        } else { // Non-delete mode: show recipe details fragment
                AddFragment recipeDetailsFragment = new AddFragment();

                // Pass any necessary data to the new fragment using a Bundle

                Log.d("Test", recipe.getServingSize());
                Log.d("Test", String.valueOf(recipe.getCal()));

                Bundle bundle = new Bundle();
                bundle.putString("recipeTitle", recipe.getTitle());
                bundle.putString("recipeDescription", recipe.getDescription());
                bundle.putString("servingSize", recipe.getServingSize());
                bundle.putInt("calories", recipe.getCal());
                bundle.putInt("protein", recipe.getProtein());
                bundle.putInt("difficulty", recipe.getDifficultyLevel().ordinal());
                bundle.putInt("mealtime", recipe.getMealTime().ordinal());

                bundle.putBoolean("isVegetarian", recipe.getIsVegetarian());
                bundle.putBoolean("isGlutenFree", recipe.getIsGlutenFree());
                bundle.putBoolean("isSugarFree", recipe.getIsSugarFree());

                bundle.putString("image", recipe.getImage().toString());

                bundle.putStringArrayList("ingredients", recipe.getIngredients());
                bundle.putStringArrayList("directions", recipe.getDirections());

                recipeDetailsFragment.setArguments(bundle);

                // Replace the current fragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, recipeDetailsFragment) // Replace with the ID of your container
                        .addToBackStack(null) // Add to back stack for "Back" navigation
                        .commit();
            }
        });

        // Calculate the row and column for placement
        int columnCount = recipeContainer.getColumnCount();
        int row = recipeCount / columnCount;
        int col = recipeCount % columnCount;

        // Set the GridLayout.LayoutParams for the new item
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(col);
        params.setMargins(10, 10, 10, 10); // Add margins

        recipeItemView.setLayoutParams(params);
        recipeContainer.addView(recipeItemView);

        // Increment recipe count
        recipeCount++;
    }

    private void deleteSelectedRecipes() {
        // Check if any recipes are selected
        if (selectedRecipes.isEmpty()) {
            return; // If no recipes are selected, do nothing
        }

        // Iterate through the selected recipes and remove them
        for (Map.Entry<View, Integer> entry : selectedRecipes.entrySet()) {
            View recipeItemView = entry.getKey(); // View representing the recipe item
            Integer recipeId = entry.getValue(); // The ID of the recipe to be deleted

            // Remove the recipe from the UI
            recipeContainer.removeView(recipeItemView); // This will remove the view from the container

            // Optionally, you can also remove the recipe from the data source (e.g., map or JSON file)
            Map<Integer, Recipe> loadedRecipeMap = JsonUtils.loadRecipeMapFromJson(requireContext(), "recipes.json");

            // Remove the recipe from the map using the recipe ID
            loadedRecipeMap.remove(recipeId);

            // Save the updated map back to the JSON file
            JsonUtils.saveRecipeMapToJson(requireContext(), loadedRecipeMap, "recipes.json");
        }

        // After deletion, exit delete mode and reset the icon
        isDeleteMode = false;
        delRecipeButton.setImageResource(R.drawable.ic_delete); // Reset the delete button icon
    }

    private void toggleDeleteMode() {
        if (isDeleteMode) {
            // We're in delete mode already. Show the confirmation dialog if recipes are selected.
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Recipes")
                    .setMessage("Are you sure you want to delete the selected recipes?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete selected recipes
                        deleteSelectedRecipes();
                        dialog.dismiss();
                        // Reset the delete mode and icon
                        isDeleteMode = false;
                        delRecipeButton.setImageResource(R.drawable.ic_delete);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                        // Exit delete mode if the user cancels
                        isDeleteMode = false;
                        delRecipeButton.setImageResource(R.drawable.ic_delete);
                        clearSelection(); // Optional: Clear any selection overlays
                    })
                    .show();
        } else {
            // Enter delete mode
            isDeleteMode = true;
            delRecipeButton.setImageResource(R.drawable.ic_del_forever);

            // Show selection overlays for all recipes
            for (int i = 0; i < recipeContainer.getChildCount(); i++) {
                View recipeItem = recipeContainer.getChildAt(i);
                recipeItem.findViewById(R.id.selection_overlay).setVisibility(View.VISIBLE);
            }
        }
    }

    // Method to clear the selection overlays and reset selections
    private void clearSelection() {
        for (int i = 0; i < recipeContainer.getChildCount(); i++) {
            View recipeItem = recipeContainer.getChildAt(i);
            recipeItem.findViewById(R.id.selection_overlay).setVisibility(View.GONE);
        }
        selectedRecipes.clear(); // Clear the selected recipes map
    }

    /*private void rebuildRecipeMap(Map<Integer, Recipe> oldMap) {
        Map<Integer, Recipe> newMap = new HashMap<>();
        int newId = 1; // Starting ID value

        // Iterate through the old map and assign new IDs
        for (Map.Entry<Integer, Recipe> entry : oldMap.entrySet()) {
            Recipe recipe = entry.getValue();
            newMap.put(newId++, recipe); // Add to new map with new IDs
        }

        // Update your recipeMap with the re-indexed map
        //recipeMap = newMap;
    }*/

}




