package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CalorieAdapter extends RecyclerView.Adapter<CalorieAdapter.ViewHolder> {

    private List<CalorieItem> calorieList;

    public CalorieAdapter(List<CalorieItem> calorieList) {
        this.calorieList = calorieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calorie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalorieItem item = calorieList.get(position);
        holder.foodName.setText(item.getFoodName());
        holder.calorieAmount.setText(item.getCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return calorieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, calorieAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            calorieAmount = itemView.findViewById(R.id.calorieAmount);
        }
    }
}

