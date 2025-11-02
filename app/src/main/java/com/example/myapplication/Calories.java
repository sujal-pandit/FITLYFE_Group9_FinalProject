package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class Calories extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calories, container, false);
        MaterialButton changeCalorieLimit = view.findViewById(R.id.changeCalorieLimit);
        MaterialButton addCalorie = view.findViewById(R.id.addCalorie);
        ProgressBar calorieProgressBar = view.findViewById(R.id.calorieProgressBar);
        TextView calorieProgressText = view.findViewById(R.id.calorieProgressText);

         RecyclerView calorieListView;
        CalorieAdapter adapter;
        List<CalorieItem> calorieItems;

        calorieProgressBar.setMax(2000);
        calorieProgressBar.setProgress(400);

        calorieProgressText.setText(calorieProgressBar.getProgress()+"/"+calorieProgressBar.getMax());

        addCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Edit in Progress", Toast.LENGTH_SHORT).show();
            }
        });

        calorieListView = view.findViewById(R.id.CalorieList);
        calorieListView.setLayoutManager(new LinearLayoutManager(getContext()));


        calorieItems = new ArrayList<>();
        calorieItems.add(new CalorieItem("Breakfast - Eggs & Toast", 350));
        calorieItems.add(new CalorieItem("Lunch - Chicken Salad", 450));
        calorieItems.add(new CalorieItem("Snack - Protein Bar", 200));

        adapter = new CalorieAdapter(calorieItems);
        calorieListView.setAdapter(adapter);


        return view;
    }
}