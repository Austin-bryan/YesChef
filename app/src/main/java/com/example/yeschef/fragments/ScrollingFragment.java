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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling, container, false);

        // Initialize the GridLayout, SearchView, and Button
        recipeContainer = view.findViewById(R.id.recipe_container);
        searchView = view.findViewById(R.id.searchView);
        addRecipeButton = view.findViewById(R.id.addRecipeButton);

        Map<Integer, Recipe> loadedRecipeMap = new HashMap<>();
        loadedRecipeMap = JsonUtils.loadRecipeMapFromJson(requireContext(), "recipes.json");

        for (Map.Entry<Integer, Recipe> entry : loadedRecipeMap.entrySet()) {
            Integer recipeId = entry.getKey();
            Recipe recipe = entry.getValue();

            addRecipeItem(recipeId, recipe);
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

        // Set the OnClickListener for the recipe item view
        recipeImageButton.setOnClickListener(v -> {
            // Create an instance of the RecipeDetailsFragment
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

}
