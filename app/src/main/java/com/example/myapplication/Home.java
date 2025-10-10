package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;

public class Home extends Fragment {

    ArrayList barArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        BarChart barChart = view.findViewById(R.id.monthlyBarChart);
        barArrayList = new ArrayList<>();
        getData();
        BarDataSet barDataSet = new BarDataSet(barArrayList,"Workout History(Monthly)");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // XAxis labels
        String[] labels = new String[]{"Jan", "Feb", "Mar", "Apr", "May"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(labels.length);

        //setting colorful bar dataset
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        //setting text color
        barDataSet.setValueTextColor(Color.BLACK);

        //setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
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
