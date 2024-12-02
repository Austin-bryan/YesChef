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

        recipeCount = 0;

        // Initialize the GridLayout, SearchView, and Button
        recipeContainer = view.findViewById(R.id.recipe_container);
        searchView      = view.findViewById(R.id.searchView);
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
        View recipeItemView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_item, recipeContainer, false);

        // Set text and image
        TextView recipeTitleTextView = recipeItemView.findViewById(R.id.recipe_name);
        recipeTitleTextView.setText(recipe.getTitle());

        ImageButton recipeImageButton = recipeItemView.findViewById(R.id.recipe_image);
        recipeImageButton.setImageURI(recipe.getImage());

        // Calculate row and column
        int columnCount = recipeContainer.getColumnCount();
        int row = recipeCount / columnCount;
        int col = recipeCount % columnCount;

        // Set GridLayout.LayoutParams
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row, 1); // Row span of 1
        params.columnSpec = GridLayout.spec(col, 1); // Column span of 1
        params.setMargins(10, 10, 10, 10); // Margins

        recipeItemView.setLayoutParams(params);
        recipeContainer.addView(recipeItemView);

        // Force layout recalculation
        recipeContainer.invalidate();
        recipeContainer.requestLayout();

        // Increment recipe count
        recipeCount++;
    }

}
