package com.example.LearnEnglish.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.adapters.TestPagerAdapter;
import com.example.LearnEnglish.fragments.HomeFragment;
import com.example.LearnEnglish.fragments.ResultsFragment;
import com.example.LearnEnglish.fragments.TestRUENFragment;
import com.example.LearnEnglish.models.Word;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity implements TestRUENFragment.OnFragmentAnswerListener {

    private ViewPager pager;
    private ViewPager pagerResults;
    private TabLayout tabLayout;
    private Button okButton;
    private ArrayList<String> answers;
    private ArrayList<Integer> positions;
    private int countFragments = 0;
    private int countAnswered = 0;
    ArrayList<Word> words;
    boolean isEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        pager = findViewById(R.id.RUEN_pager);
        pagerResults = findViewById(R.id.results_pager);
        tabLayout = findViewById(R.id.tab_layout);
        okButton = findViewById(R.id.ok_button);
        answers =  new ArrayList<>();
        positions = new ArrayList<>();
        Bundle arguments = getIntent().getExtras();
        String selectedTest = arguments.get("selectedTest").toString();
        words = arguments.getParcelableArrayList("words");

        switch (selectedTest){
            case "RUEN":
                initRUEN();
                setTitle("Test RU/EN");
                break;
            case "ENRU":
                break;
            case "Variants":
                break;
            case "Combo":
                break;
            default:
                break;
        }
    }

    private void initRUEN() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for(Word word: words) {
            TestRUENFragment fragment = TestRUENFragment.getInstance(word);
            fragment.setOnFragmentAnswerListener(this);
            fragments.add(fragment);
        }
        countFragments = fragments.size();
        TestPagerAdapter pagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(10);
        tabLayout.setupWithViewPager(pager, true);
    }

    @Override
    public void onFragmentAnswer(int position, String answer) {
        answers.add(answer);
        positions.add(position);
        countAnswered++;
        if(countAnswered == countFragments){
            RUENEnded();
        }
    }

    private void RUENEnded(){
        isEnded = true;
        pager.setVisibility(View.GONE);
        setTitle("Results");
        ArrayList<Fragment> fragments = new ArrayList<>();
        int i = 0;
        for(Word word: words) {
            ResultsFragment fragment = ResultsFragment.getInstance(word, answers.get(i), positions.get(i));
            fragments.add(fragment);
            i++;
        }
        TestPagerAdapter pagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), fragments);
        pagerResults.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pagerResults, true);
        pagerResults.setVisibility(View.VISIBLE);
        okButton.setVisibility(View.VISIBLE);
        okButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("words", words);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isEnded){
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("words", words);
            setResult(RESULT_OK, intent);
            finish();
        }
        else{
            finish();
        }
    }
}