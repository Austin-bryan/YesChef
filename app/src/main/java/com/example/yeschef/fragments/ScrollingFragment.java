package com.example.yeschef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yeschef.R;
import com.example.yeschef.models.FilterParams;
import com.example.yeschef.models.Recipe;
import com.example.yeschef.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScrollingFragment extends Fragment implements FilterBottomSheet.FilterCallback {

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
        populateRecipes("", null);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Trigger filtering when the user submits the query
                populateRecipes(query, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Trigger filtering as the user types
                populateRecipes(newText, null);
                return true;
            }
        });

        ImageButton filterButton = view.findViewById(R.id.filter_button);

        // Set an OnClickListener to open the filter panel
        filterButton.setOnClickListener(v -> {
            // Open the filter panel
            FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
            filterBottomSheet.setFilterCallback(this);
            filterBottomSheet.show(getParentFragmentManager(), "FilterBottomSheet");
        });

        return view;
    }

    private void populateRecipes(String filter, FilterParams filterParams) {
        // Clear the existing GridLayout
        recipeContainer.removeAllViews();

        // Filter and add recipes that match the filter
        for (Map.Entry<Integer, Recipe> entry : loadedRecipeMap.entrySet()) {
            Recipe recipe = entry.getValue();

            // Apply filtering logic
            if (!recipe.getTitle().toLowerCase().contains(filter.toLowerCase())) continue;
            if (filterParams != null) {
                if (!filterParams.descriptionTags.isEmpty() && !containsAllTags(filterParams.descriptionTags, recipe.getDescription())) continue;
                if (!filterParams.servingSizeParam.IsValid(recipe.getServingSize())) continue;
                if (!filterParams.calorieParam.IsValid(recipe.getCal())) continue;
                if (!filterParams.proteinParam.IsValid(recipe.getProtein())) continue;
                if (!filterParams.difficulty.isEmpty() && !filterParams.difficulty.contains(recipe.getDifficultyLevel().toString().toLowerCase())) continue;
                if (!filterParams.mealtime.isEmpty() && !filterParams.mealtime.contains(recipe.getMealTime().toString().toLowerCase())) continue;
                if (!filterParams.dietaryOptions.isEmpty() && !matchesDietaryOptions(recipe, filterParams.dietaryOptions)) continue;
                if (!filterParams.ingredients.isEmpty() && !containsAllFilteredStrings(filterParams.ingredients, recipe.getIngredients())) continue;
                if (!filterParams.directions.isEmpty() && !containsAllFilteredStrings(filterParams.directions, recipe.getDirections())) continue;
            }

            addRecipeItem(entry.getKey(), recipe);
        }
    }

    private boolean containsAllTags(List<String> filter, String description) {
        if (filter == null || description == null)
            return false;

        String lowerDescription = description.toLowerCase();

        for (String tag : filter)
            if (!lowerDescription.contains(tag.toLowerCase()))
                return false; // If this, then there was a tag missing

        return true; // Returning here means all tags were found
    }

    private boolean containsAllFilteredStrings(List<String> filter, List<String> recipe) {
        // Convert the recipe list to lowercase for case-insensitive comparison
        List<String> recipeLower = recipe.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // Check if all items in the filter are present in the recipe
        for (String ingredient : filter) {
            if (!recipeLower.contains(ingredient.toLowerCase())) {
                return false; // Return false if any ingredient is missing
            }
        }

        return true; // Return true if all filter ingredients are found
    }
    
    private boolean matchesDietaryOptions(Recipe recipe, List<String> options) {
        if (options.contains("vegetarian") && !recipe.getIsVegetarian())
            return false;
        if (options.contains("gluten-free") && !recipe.getIsGlutenFree())
            return false;
        if (options.contains("sugar-free") && !recipe.getIsSugarFree())
            return false;
        return true;
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

            RecipeDetailsBottomSheet bottomSheet = new RecipeDetailsBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "RecipeDetailsBottomSheet");

//            AddFragment recipeDetailsFragment = new AddFragment();
//
//            Bundle bundle = new Bundle();
//            bundle.putString("recipeTitle", recipe.getTitle());
//            bundle.putString("recipeDescription", recipe.getDescription());
//            bundle.putInt("servingSize", recipe.getServingSize());
//            bundle.putInt("calories", recipe.getCal());
//            bundle.putInt("protein", recipe.getProtein());
//            bundle.putInt("difficulty", recipe.getDifficultyLevel().ordinal());
//            bundle.putInt("mealtime", recipe.getMealTime().ordinal());
//
//            bundle.putBoolean("isVegetarian", recipe.getIsVegetarian());
//            bundle.putBoolean("isGlutenFree", recipe.getIsGlutenFree());
//            bundle.putBoolean("isSugarFree", recipe.getIsSugarFree());
//
//            bundle.putString("image", recipe.getImage().toString());
//
//            bundle.putStringArrayList("ingredients", recipe.getIngredients());
//            bundle.putStringArrayList("directions", recipe.getDirections());
//
//            recipeDetailsFragment.setArguments(bundle);
//
//            // Replace the current fragment
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main, recipeDetailsFragment) // Replace with the ID of your container
//                    .addToBackStack(null) // Add to back stack for "Back" navigation
//                    .commit();
        });

        // Add the item to the GridLayout
        recipeContainer.addView(recipeItemView);
    }

    @Override
    public void onFilterApplied(FilterParams filterParams) {
        populateRecipes(searchView.getQuery().toString(), filterParams);
    }
}
