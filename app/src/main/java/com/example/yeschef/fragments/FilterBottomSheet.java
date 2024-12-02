package com.example.yeschef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yeschef.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    // Callback interface to send data back to the fragment
    public interface FilterCallback {
        void onFilterApplied(String filterOption, int calorieAmount);
    }

    private FilterCallback filterCallback;

    public void setFilterCallback(FilterCallback callback) {
        this.filterCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_side_panel, container, false);

        Spinner calorieFilterSpinner = view.findViewById(R.id.calorie_filter_spinner);
        EditText calorieFilterInput = view.findViewById(R.id.calorie_filter_input);
        Button applyFilterButton = view.findViewById(R.id.apply_filter_button);
        Button cancelFilterButton = view.findViewById(R.id.cancel_filter_button);

        // Handle "Apply" button click
        applyFilterButton.setOnClickListener(v -> {
            if (filterCallback != null) {
                String filterOption = calorieFilterSpinner.getSelectedItem().toString();
                String calorieInput = calorieFilterInput.getText().toString();
                if (!calorieInput.isEmpty()) {
                    int calorieAmount = Integer.parseInt(calorieInput);
                    filterCallback.onFilterApplied(filterOption, calorieAmount);
                }
            }
            dismiss(); // Close the bottom sheet
        });

        // Handle "Cancel" button click
        cancelFilterButton.setOnClickListener(v -> dismiss());

        return view;
    }
}
