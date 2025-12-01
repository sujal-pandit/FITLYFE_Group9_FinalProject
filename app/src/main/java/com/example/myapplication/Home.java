package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Home extends Fragment implements SensorEventListener {

    ArrayList barArrayList;
    LinearLayout calorieBox, stepBox;
    CircularProgressBar stepProgressBar, calorieProgressBar;
    TextView currentCalorie, maxCalorie;
    TextView tvStepCountLabel;


    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean hasStepSensor = false;
    private float initialStepCount = -1f;

    // Step goal & calories
    private int stepGoal = 10000;              // default goal
    private static final float CALORIES_PER_STEP = 0.04f; // avg calories burned per step
    private int stepCalories = 0;              // burned calories from steps (not added to intake)

    // food calories from Calories fragment
    private int foodCalories = 0;
    private int maxCalories = 2000;            // daily limit from Calories fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // listen to calorie updates from Calories fragment
        getParentFragmentManager().setFragmentResultListener("calorieUpdate", this,
                (requestKey, bundle) -> {
                    foodCalories = bundle.getInt("consumed", 0);
                    maxCalories = bundle.getInt("max", 2000);

                    calorieProgressBar.setProgressMax(maxCalories);
                    updateCalorieMeter();
                }
        );

        BarChart barChart = view.findViewById(R.id.monthlyBarChart);
        calorieBox = view.findViewById(R.id.calorieBox);
        stepBox = view.findViewById(R.id.stepBox);
        CheckBox dailyWorkout = view.findViewById(R.id.dailyWorkoutLog);
        currentCalorie = view.findViewById(R.id.currentCalorie);
        maxCalorie = view.findViewById(R.id.maxCalorie);
        tvStepCountLabel = view.findViewById(R.id.tvStepCountLabel);

        //chart setup
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(months.length);

        barChart.getAxisLeft().setAxisMinimum(0f); // start at 0
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(true);

        dailyWorkout.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("workoutLogs")
                    .child(userId);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (isChecked) {
                ref.child(today).setValue(true);
            } else {
                ref.child(today).removeValue();
            }

            updateChartFromFirebase();
        });

        barArrayList = new ArrayList<>();

        // step progress
        stepProgressBar = view.findViewById(R.id.stepProgressBar);
        stepProgressBar.setProgressBarColorStart(Color.RED);
        stepProgressBar.setProgressBarColorEnd(Color.GREEN);
        stepProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.LEFT_TO_RIGHT);
        stepProgressBar.setProgressMax(stepGoal);
        stepProgressBar.setProgress(0f);

        if (tvStepCountLabel != null) {
            tvStepCountLabel.setText("0/" + stepGoal);
        }

        // calorie progress
        calorieProgressBar = view.findViewById(R.id.calorieProgressBar);
        calorieProgressBar.setProgressBarColorStart(Color.RED);
        calorieProgressBar.setProgressBarColorEnd(Color.GREEN);
        calorieProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.LEFT_TO_RIGHT);
        calorieProgressBar.setProgressMax(maxCalories);
        updateCalorieMeter();

        // listener for calorie card to redirect it to Calories tab
        calorieBox.setOnClickListener(v1 -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewpager2);
            viewPager.setCurrentItem(2, true);
        });

        // step card for adjusting step target
        stepBox.setOnClickListener(v12 -> showStepGoalDialog());

        // step sensor
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            hasStepSensor = true;
        } else {
            hasStepSensor = false;
            if (tvStepCountLabel != null) {
                tvStepCountLabel.setText("No step sensor");
            }
        }

        loadStepGoal();
        updateChartFromFirebase();
        return view;
    }

    // load step goal from Firebase
    private void loadStepGoal() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("stepGoal")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Integer value = snapshot.getValue(Integer.class);
                        if (value != null && value > 0) {
                            stepGoal = value;
                        }
                    }
                    stepProgressBar.setProgressMax(stepGoal);
                    tvStepCountLabel.setText("0/" + stepGoal);
                });
    }

    // change daily step goal
    private void showStepGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set Daily Step Goal");

        final TextInputEditText input = new TextInputEditText(getContext());
        input.setHint("Enter steps (e.g. 8000)");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String text = input.getText() != null ? input.getText().toString().trim() : "";
            if (!text.isEmpty()) {
                int newGoal = Integer.parseInt(text);
                if (newGoal <= 0) return;

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("users")
                        .child(uid)
                        .child("stepGoal")
                        .setValue(newGoal);

                stepGoal = newGoal;
                stepProgressBar.setProgressMax(stepGoal);
                tvStepCountLabel.setText("0/" + stepGoal);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasStepSensor && sensorManager != null && stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        if (hasStepSensor && sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_STEP_COUNTER) return;

        float totalStepsSinceBoot = event.values[0];

        if (initialStepCount < 0) {
            initialStepCount = totalStepsSinceBoot;  // first reading
        }

        float stepsTodayFloat = totalStepsSinceBoot - initialStepCount;
        if (stepsTodayFloat < 0) stepsTodayFloat = 0;

        int stepsToday = (int) stepsTodayFloat;

        // step circle
        if (stepProgressBar != null) {
            stepProgressBar.setProgressMax(stepGoal);
            stepProgressBar.setProgress(Math.min(stepsToday, stepGoal));
        }

        if (tvStepCountLabel != null) {
            tvStepCountLabel.setText(stepsToday + "/" + stepGoal);
        }

        // calories from steps (kept for future use, but NOT added to intake meter)
        stepCalories = (int) (stepsToday * CALORIES_PER_STEP);

        // DO NOT modify food calorie meter here
        // updateCalorieMeter();  // not needed, foodCalories comes from Calories fragment
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // update intake calorie meter (FOOD ONLY)
    private void updateCalorieMeter() {
        if (calorieProgressBar == null || currentCalorie == null || maxCalorie == null) return;

        int totalFood = foodCalories;

        if (maxCalories <= 0) {
            maxCalories = 2000;
        }

        calorieProgressBar.setProgressMax(maxCalories);
        calorieProgressBar.setProgress(Math.min(totalFood, maxCalories));

        currentCalorie.setText(String.valueOf(totalFood));
        maxCalorie.setText(String.valueOf(maxCalories));
    }

    private void updateChartFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("workoutLogs")
                .child(userId);

        ref.get().addOnCompleteListener(task -> {
            barArrayList.clear();
            int count = 0;

            for (DataSnapshot snap : task.getResult().getChildren()) {
                Boolean trained = snap.getValue(Boolean.class);
                if (trained != null && trained) count++;
            }

            int currentMonthIndex = new Date().getMonth();
            barArrayList.add(new BarEntry(currentMonthIndex, count));

            BarChart barChart = getView().findViewById(R.id.monthlyBarChart);
            BarDataSet set = new BarDataSet(barArrayList, "Workouts this month");
            set.setColors(ColorTemplate.COLORFUL_COLORS);

            barChart.setData(new BarData(set));
            barChart.invalidate();
        });
    }
}
