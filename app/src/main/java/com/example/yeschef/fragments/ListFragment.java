package com.example.yeschef.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.yeschef.R;
import com.google.android.material.textfield.TextInputEditText;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ImageButton addTaskButton = view.findViewById(R.id.addTaskButton);
        LinearLayout taskContainer = view.findViewById(R.id.taskContainer);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate a new task using item_task.xml
                LayoutInflater taskInflater = LayoutInflater.from(getContext());
                View newTask = taskInflater.inflate(R.layout.item_task, taskContainer, false);

                TextInputEditText taskInput = newTask.findViewById(R.id.item_task_input);
                ImageButton checkTaskButton = newTask.findViewById(R.id.checkmarkButton);
                ImageButton deleteTaskButton = newTask.findViewById(R.id.deleteTaskButton);

                // Set the hint for the task input
                taskInput.setHint("New Item");

                // Animation for adding a new task
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(300);
                newTask.startAnimation(fadeIn);

                // Check task listener
                checkTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (taskInput.getText() != null && taskInput.getText().length() > 0) {
                            taskInput.setPaintFlags(taskInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            taskInput.setTextColor(getResources().getColor(android.R.color.darker_gray));

                            // Move the task to the bottom of the list
                            taskContainer.removeView(newTask);
                            taskContainer.addView(newTask);
                        }
                    }
                });

                // Clear all tasks listener
                ImageButton clearTasksButton = view.findViewById(R.id.clear_button);


                clearTasksButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v){
                        new AlertDialog.Builder(getContext())
                                .setTitle("Clear Recipe")
                                .setMessage("Are you sure you want to clear all fields?")
                                .setPositiveButton("Clear", (dialog, which) -> taskContainer.removeAllViews())
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .show();


                    }

                });

                // Delete task listener
                deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                });

                // Add the new task view to the top of the task container
                taskContainer.addView(newTask, 0); // Adding at position 0 makes it the first one
            }
        });

        return view;
    }
}
