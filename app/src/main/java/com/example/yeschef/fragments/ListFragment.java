package com.example.yeschef.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.yeschef.R;
import com.example.yeschef.models.ShoppingItem;
import com.example.yeschef.utils.ShoppingListStorage;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ImageButton addTaskButton = view.findViewById(R.id.addTaskButton);
        LinearLayout taskContainer = view.findViewById(R.id.taskContainer);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskView(LayoutInflater.from(getContext()), taskContainer, true, null);
            }
        });

        ShoppingListStorage storage = new ShoppingListStorage(requireContext());
        List<ShoppingItem> savedItems = storage.loadShoppingList();

        taskContainer.removeAllViews();

        // Load saved items into the task container
        for (ShoppingItem item : savedItems) {
            addTaskView(LayoutInflater.from(getContext()), taskContainer, false, item.getName()); // Pass the saved text
        }

        return view;
    }

    private void updateStorage() {
        LinearLayout container = getView().findViewById(R.id.taskContainer);
        List<ShoppingItem> shoppingItems = new ArrayList<>();

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            TextInputEditText input = child.findViewById(R.id.item_task_input);
            if (input != null && !input.getText().toString().trim().isEmpty()) {
                shoppingItems.add(new ShoppingItem(input.getText().toString().trim()));
            }
        }

        ShoppingListStorage storage = new ShoppingListStorage(requireContext());
        storage.saveShoppingList(shoppingItems);
    }

    public void addTaskView(LayoutInflater inflater, ViewGroup taskContainer, boolean useAnimations, @Nullable String prefilledText) {
        // Inflate a new task using item_task.xml
        View newTask = inflater.inflate(R.layout.item_task, taskContainer, false);

        TextInputEditText taskInput = newTask.findViewById(R.id.item_task_input);
        ImageButton checkTaskButton = newTask.findViewById(R.id.checkmarkButton);
        ImageButton deleteTaskButton = newTask.findViewById(R.id.deleteTaskButton);

        // Set the hint for the task input
        taskInput.setHint("New Item");

        // If prefilledText is provided, set it
        if (prefilledText != null) {
            taskInput.setText(prefilledText);
        }

        taskInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update the storage whenever the text changes
                updateStorage();
            }
        });

        if (useAnimations) {
            // Animation for adding a new task
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(300);
            newTask.startAnimation(fadeIn);
        }

        // Check task listener
        checkTaskButton.setOnClickListener(v -> {
            if (taskInput.getText() != null && taskInput.getText().length() > 0) {
                taskInput.setPaintFlags(taskInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                taskInput.setTextColor(getResources().getColor(android.R.color.darker_gray));

                // Move the task to the bottom of the list
                taskContainer.removeView(newTask);
                taskContainer.addView(newTask);
            }
        });

        // Delete task listener
        deleteTaskButton.setOnClickListener(v -> {
            if (useAnimations) {
                // Animation for deleting a task
                ScaleAnimation scaleDown = new ScaleAnimation(
                        1.0f, 0.0f, 1.0f, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleDown.setDuration(300);
                scaleDown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        taskContainer.removeView(newTask);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                newTask.startAnimation(scaleDown);
            } else {
                // Remove task without animation
                taskContainer.removeView(newTask);
            }
        });

        // Add the new task view to the top of the task container
        taskContainer.addView(newTask, 0); // Adding at position 0 makes it the first one
    }
}
