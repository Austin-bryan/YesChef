package com.example.yeschef.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.example.yeschef.R;
import com.google.android.material.textfield.TextInputEditText;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Button addTaskButton = view.findViewById(R.id.addTaskButton);
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
                taskInput.setHint("New Task");

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

                // Delete task listener
                deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        taskContainer.removeView(newTask);
                    }
                });

                // Add the new task view to the top of the task container
                taskContainer.addView(newTask, 0); // Adding at position 0 makes it the first one
            }
        });

        return view;
    }
}
