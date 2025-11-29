package com.example.myapplication;

import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Home extends Fragment {

    ArrayList barArrayList;
    LinearLayout calorieBox;
    CircularProgressBar stepProgressBar,calorieProgressBar;
    TextView currentCalorie,maxCalorie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getParentFragmentManager().setFragmentResultListener("calorieUpdate", this,
                (requestKey, bundle) -> {
                    int consumed = bundle.getInt("consumed", 0);
                    int max = bundle.getInt("max", 2000);

                    calorieProgressBar.setProgressMax(max);
                    calorieProgressBar.setProgress(consumed);

                    currentCalorie.setText(String.valueOf(consumed));
                    maxCalorie.setText(String.valueOf(max));
                }
        );


        BarChart barChart = view.findViewById(R.id.monthlyBarChart);
        LinearLayout calorieBox = view.findViewById(R.id.calorieBox);
        CheckBox dailyWorkout = view.findViewById(R.id.dailyWorkoutLog);
        currentCalorie = view.findViewById(R.id.currentCalorie);
        maxCalorie=view.findViewById(R.id.maxCalorie);

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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("workoutLogs").child(userId);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (isChecked) {
                ref.child(today).setValue(true);
            } else{
                ref.child(today).removeValue();
            }

            updateChartFromFirebase();
        });

        barArrayList = new ArrayList<>();

        stepProgressBar = view.findViewById(R.id.stepProgressBar);

        stepProgressBar.setProgressBarColorStart(Color.RED);
        stepProgressBar.setProgressBarColorEnd(Color.GREEN);
        stepProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.LEFT_TO_RIGHT);

        stepProgressBar.setProgress(100f);

        calorieProgressBar = view.findViewById(R.id.calorieProgressBar);

        calorieProgressBar.setProgressBarColorStart(Color.RED);
        calorieProgressBar.setProgressBarColorEnd(Color.GREEN);
        calorieProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.LEFT_TO_RIGHT);

        calorieBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ViewPager2 viewPager = requireActivity().findViewById(R.id.viewpager2);
                viewPager.setCurrentItem(2, true);
            }
        });


        return view;
    }

    private void updateChartFromFirebase(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("workoutLogs").child(userId);

       ref.get().addOnCompleteListener(task->{
           barArrayList.clear();
           int count =0;

           for(DataSnapshot snap: task.getResult().getChildren()){
               Boolean trained = snap.getValue(Boolean.class);
               if(trained!=null && trained) count++;
           }

           barArrayList.add(new BarEntry(0,count));

           BarChart barChart = getView().findViewById(R.id.monthlyBarChart);
           BarDataSet set = new BarDataSet(barArrayList, "Workouts this month");
           set.setColors(ColorTemplate.COLORFUL_COLORS);

           barChart.setData(new BarData(set));
           barChart.invalidate();
       });
    }



}
