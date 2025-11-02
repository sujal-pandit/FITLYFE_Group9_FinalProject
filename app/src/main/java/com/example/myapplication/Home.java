package com.example.myapplication;

import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

public class Home extends Fragment {

    ArrayList barArrayList;
    CircularProgressBar stepProgressBar,calorieProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // === Bar Chart Setup ===
        BarChart barChart = view.findViewById(R.id.monthlyBarChart);
        barArrayList = new ArrayList<>();
        getData();

        BarDataSet barDataSet = new BarDataSet(barArrayList, "Workout History (Monthly)");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        String[] labels = {"Jan", "Feb", "Mar", "Apr", "May"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(labels.length);


        stepProgressBar = view.findViewById(R.id.stepProgressBar);

        stepProgressBar.setProgressBarColorStart(Color.RED);
        stepProgressBar.setProgressBarColorEnd(Color.GREEN);
        stepProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.LEFT_TO_RIGHT);

        stepProgressBar.setProgress(100f);

        calorieProgressBar = view.findViewById(R.id.calorieProgressBar);

        calorieProgressBar.setProgressBarColorStart(Color.RED);
        calorieProgressBar.setProgressBarColorEnd(Color.GREEN);
        calorieProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.LEFT_TO_RIGHT);

        calorieProgressBar.setProgress(100f);

        return view;
    }

    private void getData()
    {
        barArrayList = new ArrayList();
        barArrayList.add(new BarEntry(0f,10));
        barArrayList.add(new BarEntry(1f,20));
        barArrayList.add(new BarEntry(2f,30));
        barArrayList.add(new BarEntry(3f,40));
        barArrayList.add(new BarEntry(4f,50));
    }


}
