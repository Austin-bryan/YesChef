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

        // Set the button click listener
        addRecipeButton.setOnClickListener(v -> addRecipeItem());

        return view;
    }

    private void addRecipeItem() {
        // Inflate the recipe item layout
        View recipeItemView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_item, recipeContainer, false);

        String fileName = "recipe.json";
        Recipe recipe = JsonUtils.readJsonFromFile(requireContext(), fileName);

        TextView recipeTitleTextView = recipeItemView.findViewById(R.id.recipe_name);
        recipeTitleTextView.setText(recipe.getTitle());

        ImageButton recipeImageButton = recipeItemView.findViewById(R.id.recipe_image);
        Log.d("Test", recipe.getImage().toString());
        recipeImageButton.setImageURI(recipe.getImage());

        // Set the OnClickListener for the recipe item view
        recipeImageButton.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putString("recipeId", recipe.getid()); // Assuming each Recipe has a unique ID
            Log.d("Test", "swag");
            Navigation.findNavController(v).navigate(R.id.action_scrollingFragment_to_addFragment);
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
