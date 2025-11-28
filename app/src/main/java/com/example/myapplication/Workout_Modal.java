package com.example.myapplication;

public class Workout_Modal {

    String img;

    String name,calories;

    public Workout_Modal(){}

    public Workout_Modal(String img, String name, String calories){
        this.img=img;
        this.name=name;
        this.calories=calories;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
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

    public void setCalories(String calories) {
        this.calories = calories;
    }

}
