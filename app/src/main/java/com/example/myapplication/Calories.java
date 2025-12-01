package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class Calories extends Fragment {

    RecyclerView calorieListView;
    CalorieAdapter adapter;
    ProgressBar calorieProgressBar;
    TextView calorieProgressText;
    List<CalorieItem> calorieItems = new ArrayList<>();
    String todayKey;
    int dailyCalorieLimit = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calories, container, false);


        getParentFragmentManager().setFragmentResultListener("requestCalorieData", this,
                (key, bundle) -> {
                    loadCaloriesFromFirebase(); // will send calorieUpdate
                }
        );

        MaterialButton changeCalorieLimit = view.findViewById(R.id.changeCalorieLimit);
        MaterialButton addCalorie = view.findViewById(R.id.addCalorie);
        calorieProgressBar = view.findViewById(R.id.calorieProgressBar);
        calorieProgressText = view.findViewById(R.id.calorieProgressText);

        calorieListView = view.findViewById(R.id.CalorieList);
        calorieListView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CalorieAdapter(calorieItems);
        calorieListView.setAdapter(adapter);

        calorieProgressBar.setMax(dailyCalorieLimit);
        todayKey = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        loadDailyCalorieLimit();
        loadCaloriesFromFirebase();
        sendCalorieUpdateToHome();

        addCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCalorieDialog();
            }
        });

        changeCalorieLimit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showChangeCalorieLimitDialog();
            }
        });

        return view;
    }

    private void loadCaloriesFromFirebase(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("calories").child(todayKey);

        ref.get().addOnSuccessListener(snapshot -> {
            calorieItems.clear();
            int totalCalories = 0;

            for (DataSnapshot ds : snapshot.getChildren()) {
                String foodName = ds.getKey();
                int calories = ds.getValue(Integer.class);
                calorieItems.add(new CalorieItem(foodName, calories));
                totalCalories += calories;
            }

            adapter.notifyDataSetChanged();

            calorieProgressBar.setProgress(totalCalories);
            calorieProgressText.setText(totalCalories + "/" + calorieProgressBar.getMax());

            sendCalorieUpdateToHome();
    });}

    private void showAddCalorieDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_calorie, null);

        EditText mealNameInput = dialogView.findViewById(R.id.mealNameInput);
        EditText calorieInput = dialogView.findViewById(R.id.calorieInput);
        MaterialButton saveBtn = dialogView.findViewById(R.id.saveBtn);
        MaterialButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mealName = mealNameInput.getText().toString().trim();
                String caloriesStr = calorieInput.getText().toString().trim();

                if (!mealName.isEmpty() && !caloriesStr.isEmpty()) {
                    int calories = Integer.parseInt(caloriesStr);
                    saveCalorieToFirebase(mealName, calories);
                    dialog.dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveCalorieToFirebase(String mealName, int calories) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("calories")
                .child(todayKey);

        ref.child(mealName).setValue(calories)
                .addOnSuccessListener(aVoid -> loadCaloriesFromFirebase())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add meal", Toast.LENGTH_SHORT).show()
                );
    }

    private void showChangeCalorieLimitDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_change_calorie_limit, null);

        EditText limitInput = dialogView.findViewById(R.id.calorieLimitInput);
        MaterialButton saveBtn = dialogView.findViewById(R.id.saveBtn);
        MaterialButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        saveBtn.setOnClickListener(v -> {
            String limitStr = limitInput.getText().toString().trim();
            if (!limitStr.isEmpty()) {
                dailyCalorieLimit = Integer.parseInt(limitStr);
                saveDailyCalorieLimit(dailyCalorieLimit);
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void saveDailyCalorieLimit(int limit) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("calorieLimit");

        ref.setValue(limit).addOnSuccessListener(aVoid -> {
            calorieProgressBar.setMax(limit);

            int totalCalories = 0;
            for (CalorieItem item : calorieItems) {
                totalCalories += item.getCalories();
            }
            calorieProgressBar.setProgress(totalCalories);

            calorieProgressText.setText(calorieProgressBar.getProgress() + "/" + limit);
        });
        sendCalorieUpdateToHome();
    }

    private void loadDailyCalorieLimit(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("calorieLimit");

        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                dailyCalorieLimit = snapshot.getValue(Integer.class);
            }else{
                dailyCalorieLimit=2000;
            }
            calorieProgressBar.setMax(dailyCalorieLimit);
            calorieProgressText.setText(calorieProgressBar.getProgress() + "/" + dailyCalorieLimit);
        });
    }

    private void sendCalorieUpdateToHome() {
        int consumed = 0;
        for (CalorieItem item : calorieItems) {
            consumed += item.getCalories();
        }

        Bundle result = new Bundle();
        result.putInt("consumed", consumed);
        result.putInt("max", dailyCalorieLimit);

        getParentFragmentManager().setFragmentResult("calorieUpdate", result);
    }



}