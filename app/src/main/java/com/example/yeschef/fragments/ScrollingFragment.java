package com.example.yeschef.fragments;

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
    private ImageButton addRecipeButton;
    private int recipeCount = 0; // Start with 0 recipes
    private Map<Integer, Recipe> loadedRecipeMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling, container, false);

        recipeCount = 0;

        // Initialize the GridLayout, SearchView, and Button
        recipeContainer = view.findViewById(R.id.recipe_container);
        searchView      = view.findViewById(R.id.searchView);
        addRecipeButton = view.findViewById(R.id.addRecipeButton);
        loadedRecipeMap = JsonUtils.loadRecipeMapFromJson(requireContext(), "recipes.json");

        // Initially display all recipes
        populateRecipes("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Trigger filtering when the user submits the query
                populateRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Trigger filtering as the user types
                populateRecipes(newText);
                return true;
            }
        });

        ImageButton filterButton = view.findViewById(R.id.filter_button);

        // Set an OnClickListener to open the filter panel
        filterButton.setOnClickListener(v -> {
            // Open the filter panel
            FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
            filterBottomSheet.setFilterCallback((filterOption, calorieAmount) -> {
                // Apply the filter when the user clicks "Apply" in the panel
//                applyCalorieFilter(filterOption, calorieAmount);
            });
            filterBottomSheet.show(getParentFragmentManager(), "FilterBottomSheet");
        });

        return view;
    }

    private void populateRecipes(String filter) {
        // Clear the existing GridLayout
        recipeContainer.removeAllViews();

        // Filter and add recipes that match the filter
        for (Map.Entry<Integer, Recipe> entry : loadedRecipeMap.entrySet()) {
            Recipe recipe = entry.getValue();

            // Check if the recipe title contains the filter (case insensitive)
            if (recipe.getTitle().toLowerCase().contains(filter.toLowerCase())) {
                addRecipeItem(entry.getKey(), recipe);
            }
        }
    }

    private void addRecipeItem(int recipeId, Recipe recipe) {
        View recipeItemView = LayoutInflater.from(getContext())
                .inflate(R.layout.recipe_item, recipeContainer, false);

        TextView recipeTitleTextView = recipeItemView.findViewById(R.id.recipe_name);
        recipeTitleTextView.setText(recipe.getTitle());

        ImageButton recipeImageButton = recipeItemView.findViewById(R.id.recipe_image);
        recipeImageButton.setImageURI(recipe.getImage());

        // Set OnClickListener for each recipe item
        recipeImageButton.setOnClickListener(v -> {
            AddFragment recipeDetailsFragment = new AddFragment();

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
        });

        // Add the item to the GridLayout
        recipeContainer.addView(recipeItemView);
    }
}
