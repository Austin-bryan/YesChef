package com.example.yeschef;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final ScrollingFragment scrollingFragment = new ScrollingFragment();
    private int selectedItemId = R.id.recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        ScrollingFragment scrollingFragment = new ScrollingFragment();
        ListFragment listFragment = new ListFragment();
        AddFragment addFragment = new AddFragment();

        // Commit the default fragment
        commitFragment(scrollingFragment, false);

        // Add listener for navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Get the index of the current and newly selected items
                int currentIndex = getItemIndexById(bottomNavigationView, selectedItemId);
                int newIndex = getItemIndexById(bottomNavigationView, item.getItemId());

                boolean leftToRight = newIndex > currentIndex; // Determine the direction based on index comparison

                // Assign the selected fragment
                if (item.getItemId() == R.id.recipes) {
                    selectedFragment = scrollingFragment;
                } else if (item.getItemId() == R.id.add) {
                    selectedFragment = addFragment;
                } else if (item.getItemId() == R.id.list) {
                    selectedFragment = listFragment;
                }

                // Commit the fragment with the determined animation direction
                commitFragment(selectedFragment, leftToRight);

                // Update the selected item ID
                selectedItemId = item.getItemId();

                return true;
            }
        });
    }

    // Method to get the index of the selected item in the BottomNavigationView
    private int getItemIndexById(BottomNavigationView bottomNavigationView, int itemId) {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == itemId) {
                return i;
            }
        }
        return -1; // Item not found
    }

    // Method to commit a fragment with appropriate sliding animations
    private void commitFragment(Fragment fragment, boolean leftToRight) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (leftToRight) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        transaction.replace(R.id.container, fragment).commit();
    }
}