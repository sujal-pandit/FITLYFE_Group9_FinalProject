package com.example.myapplication;

import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    tabLayout = findViewById(R.id.tabLayout);
    viewPager2 = findViewById(R.id.viewpager2);
    //Home
    TabLayout.Tab firsttab = tabLayout.newTab();
        firsttab.setText("Home");
        firsttab.setIcon(R.drawable.ic_home);
        tabLayout.addTab(firsttab);

    //Workout
    TabLayout.Tab secondtab = tabLayout.newTab();
        secondtab.setText("Work Out");
        secondtab.setIcon(R.drawable.workout);
        tabLayout.addTab(secondtab);

    //Calories
    TabLayout.Tab thirdtab = tabLayout.newTab();
        thirdtab.setText("Calories");
        thirdtab.setIcon(R.drawable.calories);
        tabLayout.addTab(thirdtab);

    //Profile
    TabLayout.Tab forthtab = tabLayout.newTab();
        forthtab.setText("Profile");
        forthtab.setIcon(R.drawable.ic_profile);
        tabLayout.addTab(forthtab);

    //Adapter
    TabViewAdapter tabViewAdapter = new TabViewAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(tabViewAdapter);
    //Listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager2.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (tabLayout.getTabAt(position) != null) {
                    tabLayout.getTabAt(position).select();
                }
        }

        @Override
        public void onPageScrollStateChanged(int position) {
            super.onPageScrollStateChanged(position);
        }

    });
}
}