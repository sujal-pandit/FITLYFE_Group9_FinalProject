package com.example.myapplication;

public class Workout_Modal {

    int img;

    String name,calories;

    public Workout_Modal(int img, String name,String calories){
        this.img=img;
        this.name=name;
        this.calories=calories;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String number) {
        this.calories = calories;
    }


}
