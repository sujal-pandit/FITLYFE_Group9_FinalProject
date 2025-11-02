package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Workout extends Fragment {

    ArrayList<Workout_Modal> workoutList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recWorkoutView);

        workoutList.add(new Workout_Modal(R.drawable.pullup,"Pull Up","180 Calories"));
        workoutList.add(new Workout_Modal(R.drawable.pushup,"Push Up","200 Calories"));
        workoutList.add(new Workout_Modal(R.drawable.squat,"Squats","400 Calories"));

        Workout_Adapter adapter=new Workout_Adapter(getContext(), workoutList);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}