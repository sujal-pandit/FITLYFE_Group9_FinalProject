package com.example.myapplication;

public class CalorieItem {
    private String foodName;
    private int calories;

    public CalorieItem(String foodName, int calories) {
        this.foodName = foodName;
        this.calories = calories;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getCalories() {
        return calories;
    }
}
