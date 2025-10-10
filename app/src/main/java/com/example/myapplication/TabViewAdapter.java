package com.example.myapplication;

import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabViewAdapter extends FragmentStateAdapter {

    public TabViewAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:{
                Home Home = new Home();
                return Home;
            }
            case  1:{
                Workout workout = new Workout();
                return workout;
            }
            case 2:{
                Calories calories = new Calories();
                return calories;
            } case 3:{
                Profile profile = new Profile();
                return  profile;
            }
            default:{
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
