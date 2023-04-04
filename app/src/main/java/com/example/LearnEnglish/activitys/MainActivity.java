package com.example.LearnEnglish.activitys;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.LearnEnglish.fragments.CoursesFragment;
import com.example.LearnEnglish.fragments.HomeFragment;
import com.example.LearnEnglish.fragments.ProfileFragment;
import com.example.LearnEnglish.R;
import com.example.LearnEnglish.fragments.TestsFragment;
import com.example.LearnEnglish.adapters.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        // Получаем ссылку на ActionBar
//        ActionBar actionBar = getSupportActionBar();
//        // Устанавливаем цвет фона ActionBar
//        if (actionBar != null) {
//            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#454545")));
//        }

        DatabaseHelper databaseHelper;
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.create_db();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.tests:
                    replaceFragment(new TestsFragment());
                    break;
                case R.id.courses:
                    replaceFragment(new CoursesFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(new HomeFragment());
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_main, fragment);
        fragmentTransaction.commit();
    }
}