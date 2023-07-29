package com.example.LearnEnglish.activitys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.adapters.WordAdapter;
import com.example.LearnEnglish.fragments.CoursesFragment;
import com.example.LearnEnglish.fragments.HomeFragment;
import com.example.LearnEnglish.fragments.ProfileFragment;
import com.example.LearnEnglish.R;
import com.example.LearnEnglish.fragments.TestsFragment;
import com.example.LearnEnglish.adapters.DatabaseHelper;
import com.example.LearnEnglish.models.Word;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnWordsChangeListener, ProfileFragment.OnWordsChangeListener{
    boolean firstOpen = true;
    ArrayList<Word> words;

    @Override
    public void onWordsChanged(ArrayList<Word> words) {
        Log.d("onWordsChanged", "SUCCESS");
        this.words = words;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        DatabaseHelper databaseHelper;
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.create_db();

        DatabaseAdapter db_adapter = new DatabaseAdapter(this);
        db_adapter.open();
        words = db_adapter.getWords();
        db_adapter.close();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    Log.d("ReplaceFragment", "SUCCESS");
                    Fragment homeFragment = new HomeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("words", words);
                    homeFragment.setArguments(bundle);
                    replaceFragment(homeFragment);
                    break;
                case R.id.tests:
                    Fragment testFragment = new TestsFragment();
                    Bundle bundleTest = new Bundle();
                    bundleTest.putParcelableArrayList("words", words);
                    testFragment.setArguments(bundleTest);
                    replaceFragment(testFragment);
                    break;
                case R.id.courses:
                    replaceFragment(new CoursesFragment());
                    break;
                case R.id.profile:
                    Bundle bundleProfile = new Bundle();
                    bundleProfile.putParcelableArrayList("words", words);
                    Fragment profileFragment = new ProfileFragment();
                    profileFragment.setArguments(bundleProfile);
                    replaceFragment(profileFragment);
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onStart() {
        if(firstOpen) {
            Fragment homeFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("words", words);
            homeFragment.setArguments(bundle);
            replaceFragment(homeFragment);
            firstOpen = false;
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_main, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        words = data.getParcelableArrayListExtra("words");
        Fragment testFragment = new TestsFragment();
        Bundle bundleTest = new Bundle();
        bundleTest.putParcelableArrayList("words", words);
        testFragment.setArguments(bundleTest);
        replaceFragment(testFragment);
    }
}