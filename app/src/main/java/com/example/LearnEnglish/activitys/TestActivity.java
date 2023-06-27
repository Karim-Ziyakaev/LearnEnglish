package com.example.LearnEnglish.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.TestPagerAdapter;
import com.example.LearnEnglish.fragments.ResultsFragment;
import com.example.LearnEnglish.fragments.TestChoiceFragment;
import com.example.LearnEnglish.fragments.TestChoiceRuFragment;
import com.example.LearnEnglish.fragments.TestENRUFragment;
import com.example.LearnEnglish.fragments.TestRUENFragment;
import com.example.LearnEnglish.models.Word;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class TestActivity extends AppCompatActivity implements TestRUENFragment.OnFragmentAnswerListener,
        TestENRUFragment.OnFragmentAnswerListener,
        TestChoiceFragment.OnFragmentAnswerListener,
        TestChoiceRuFragment.OnFragmentAnswerListener {

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
    ArrayList<Integer> indexes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        pager = findViewById(R.id.test_pager);
        pagerResults = findViewById(R.id.results_pager);
        tabLayout = findViewById(R.id.tab_layout);
        okButton = findViewById(R.id.ok_button);
        answers =  new ArrayList<>();
        positions = new ArrayList<>();
        Bundle arguments = getIntent().getExtras();
        String selectedTest = arguments.get("selectedTest").toString();
        words = arguments.getParcelableArrayList("words");
        indexes = new ArrayList<>();

        switch (selectedTest){
            case "RUEN":
                initRUEN();
                setTitle("Test RU/EN");
                break;
            case "ENRU":
                initENRU();
                setTitle("Test EN/RU");
                break;
            case "Choice":
                initChoice();
                setTitle("Test choice");
                break;
            case "ChoiceRu":
                initChoiceRu();
                setTitle("Test choice");
            case "Combo":
                initCombo();
                setTitle("Test combo");
                break;
            default:
                break;
        }
    }

    private void initCombo() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        Random random = new Random();
        HashSet<Integer> selectedIndexes = new HashSet<>();
        int size = words.size();
        int fragmentsCount = Math.min(size, 12);
        int[] fragmentTypes = new int[] {0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};

        for (int i = 0; i < fragmentsCount; i++) {
            int type = fragmentTypes[i];
            int randomIndex;
            do {
                randomIndex = random.nextInt(size);
            } while (selectedIndexes.contains(randomIndex));
            selectedIndexes.add(randomIndex);
            indexes.add(randomIndex);
            Word word = words.get(randomIndex);

            switch (type) {
                case 0:
                    TestENRUFragment enruFragment = TestENRUFragment.getInstance(word);
                    enruFragment.setOnFragmentAnswerListener(this);
                    fragments.add(enruFragment);
                    break;
                case 1:
                    TestRUENFragment ruenFragment = TestRUENFragment.getInstance(word);
                    ruenFragment.setOnFragmentAnswerListener(this);
                    fragments.add(ruenFragment);
                    break;
                case 2:
                    TestChoiceFragment choiceFragment = TestChoiceFragment.getInstance(word, words);
                    choiceFragment.setOnFragmentAnswerListener(this);
                    fragments.add(choiceFragment);
                    break;
                case 3:
                    TestChoiceRuFragment choiceRuFragment = TestChoiceRuFragment.getInstance(word, words);
                    choiceRuFragment.setOnFragmentAnswerListener(this);
                    fragments.add(choiceRuFragment);
                    break;
            }
        }

        countFragments = fragments.size();
        TestPagerAdapter pagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(12);
        tabLayout.setupWithViewPager(pager, true);
    }

    private void initChoiceRu() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        Random random = new Random();
        HashSet<Integer> selectedIndexes = new HashSet<>();
        int size = words.size();
        int fragmentsCount = Math.min(size, 10);

        for (int i = 0; i < fragmentsCount; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(size);
            } while (selectedIndexes.contains(randomIndex));
            selectedIndexes.add(randomIndex);
            indexes.add(randomIndex);
            Word word = words.get(randomIndex);
            TestChoiceRuFragment fragment = TestChoiceRuFragment.getInstance(word, words);
            fragment.setOnFragmentAnswerListener(this);
            fragments.add(fragment);
        }

        countFragments = fragments.size();
        TestPagerAdapter pagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(10);
        tabLayout.setupWithViewPager(pager, true);
    }

    private void initChoice() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        Random random = new Random();
        HashSet<Integer> selectedIndexes = new HashSet<>();
        int size = words.size();
        int fragmentsCount = Math.min(size, 10); // вычисляем количество фрагментов, которое нужно создать

        for (int i = 0; i < fragmentsCount; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(size); // генерируем новый случайный индекс
            } while (selectedIndexes.contains(randomIndex)); // проверяем, был ли уже выбран этот индекс
            selectedIndexes.add(randomIndex); // добавляем индекс в HashSet
            indexes.add(randomIndex);
            Word word = words.get(randomIndex);
            TestChoiceFragment fragment = TestChoiceFragment.getInstance(word, words);
            fragment.setOnFragmentAnswerListener(this);
            fragments.add(fragment);
        }

        countFragments = fragments.size();
        TestPagerAdapter pagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(10);
        tabLayout.setupWithViewPager(pager, true);
    }

    private void initENRU() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        Random random = new Random();
        HashSet<Integer> selectedIndexes = new HashSet<>();
        int size = words.size();
        int fragmentsCount = Math.min(size, 10);

        for (int i = 0; i < fragmentsCount; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(size);
            } while (selectedIndexes.contains(randomIndex));
            selectedIndexes.add(randomIndex);
            indexes.add(randomIndex);
            Word word = words.get(randomIndex);
            TestENRUFragment fragment = TestENRUFragment.getInstance(word);
            fragment.setOnFragmentAnswerListener(this);
            fragments.add(fragment);
        }

        countFragments = fragments.size();
        TestPagerAdapter pagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(10);
        tabLayout.setupWithViewPager(pager, true);
    }

    private void initRUEN() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        Random random = new Random();
        HashSet<Integer> selectedIndexes = new HashSet<>();
        int size = words.size();
        int fragmentsCount = Math.min(size, 10);

        for (int i = 0; i < fragmentsCount; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(size);
            } while (selectedIndexes.contains(randomIndex));
            selectedIndexes.add(randomIndex);
            indexes.add(randomIndex);
            Word word = words.get(randomIndex);
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
            testEnded();
        }
    }

    private void testEnded(){
        isEnded = true;
        pager.setVisibility(View.GONE);
        setTitle("Results");
        ArrayList<Fragment> fragments = new ArrayList<>();
        int i = 0;
        for(int j : indexes) {
            ResultsFragment fragment = ResultsFragment.getInstance(words.get(j), answers.get(i), positions.get(i));
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