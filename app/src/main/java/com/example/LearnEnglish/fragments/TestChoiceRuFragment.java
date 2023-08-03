package com.example.LearnEnglish.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.models.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestChoiceRuFragment extends Fragment {

    private Word mWord;
    private ArrayList<Word> mWords;
    private TextView wordText;
    private RadioGroup radioGroup;
    private RadioButton button1;
    private RadioButton button2;
    private RadioButton button3;
    private RadioButton button4;
    private Button checkButton;
    private CardView cardView;

    private TestChoiceRuFragment.OnFragmentAnswerListener mListener;

    public void setOnFragmentAnswerListener(TestChoiceRuFragment.OnFragmentAnswerListener listener) {
        mListener = listener;
    }

    public interface OnFragmentAnswerListener {
        void onFragmentAnswer(int position, String answer);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TestChoiceRuFragment.OnFragmentAnswerListener) {
            mListener = (TestChoiceRuFragment.OnFragmentAnswerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentAnswerListener");
        }
    }

    public static TestChoiceRuFragment getInstance(Word word, ArrayList<Word> words) {
        TestChoiceRuFragment fragment = new TestChoiceRuFragment();
        if (word!=null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("word", word);
            bundle.putParcelableArrayList("words", words);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public TestChoiceRuFragment() {
        // Required empty public constructor
    }

    public static TestChoiceRuFragment newInstance() {
        TestChoiceRuFragment fragment = new TestChoiceRuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_choice_ru, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordText = view.findViewById(R.id.word_text);
        radioGroup =view.findViewById(R.id.radios);
        checkButton = view.findViewById(R.id.check_button);
        button1 = view.findViewById(R.id.btn1);
        button2 = view.findViewById(R.id.btn2);
        button3 = view.findViewById(R.id.btn3);
        button4 = view.findViewById(R.id.btn4);
        cardView = view.findViewById(R.id.cardTest);
        if (getArguments()!=null){
            mWord = getArguments().getParcelable("word");
            mWords = getArguments().getParcelableArrayList("words");
            init();
        }
    }

    private void init(){
        if(mWord!=null){
            wordText.setText(mWord.getTranslate());
        }
        final int NUM_BUTTONS = 4;
        Random random = new Random();
        int correctIndex = random.nextInt(NUM_BUTTONS);
        List<Integer> usedIndexes = new ArrayList<>();
        List<String> usedWords = new ArrayList<>();
        usedIndexes.add(mWords.indexOf(mWord));
        Button[] buttons = {button1, button2, button3, button4};

        for (int i = 0; i < NUM_BUTTONS; i++) {
            if (i == correctIndex) {
                buttons[i].setText(mWord.getWord());
            } else if (mWords.size() - 1 >= NUM_BUTTONS) {
                int randomIndex = random.nextInt(mWords.size() - 1);
                while (usedIndexes.contains(randomIndex)) {
                    randomIndex = random.nextInt(mWords.size()-1);
                }
                usedIndexes.add(randomIndex);
                buttons[i].setText(mWords.get(randomIndex).getWord());
            }
            else{
                DatabaseAdapter db_adapter = new DatabaseAdapter(requireContext());
                db_adapter.open();
                Word temp = db_adapter.getRandomWord();
                while (usedWords.contains(temp.getWord())) {
                    temp = db_adapter.getRandomWord();
                }
                usedWords.add(temp.getWord());
                db_adapter.close();
                buttons[i].setText(temp.getWord());
            }
        }

        final int[] selectedPos = {-1};
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.btn1:
                        selectedPos[0] = 0;
                        break;
                    case R.id.btn2:
                        selectedPos[0] = 1;
                        break;
                    case R.id.btn3:
                        selectedPos[0] = 2;
                        break;
                    case R.id.btn4:
                        selectedPos[0] = 3;
                        break;
                    default:
                        break;
                }
            }});
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedPos[0]==-1){
                    Toast.makeText(requireContext(), getString(R.string.choose_answer), Toast.LENGTH_SHORT).show();
                }
                else {
                    if (correctIndex == selectedPos[0]) {
                        wordText.setText("");
                        wordText.setTextSize(50);
                        wordText.setTextColor(Color.GREEN);
                        mWord.setAttempts(mWord.getAttempts() + 1);
                        mWord.setCorrectAttempts(mWord.getCorrectAttempts() + 1);
                        cardView.setCardBackgroundColor(Color.rgb(56, 194, 47));
                    } else {
                        mWord.setAttempts(mWord.getAttempts() + 1);
                        mWord.setWrongAttempts(mWord.getWrongAttempts() + 1);
                        wordText.setText("");
                        cardView.setCardBackgroundColor(Color.RED);
                    }
                    checkButton.setVisibility(View.GONE);

                    ViewPager viewPager = requireActivity().findViewById(R.id.test_pager);
                    if (mListener != null) {
                        mListener.onFragmentAnswer(viewPager.getCurrentItem(), (String) buttons[selectedPos[0]].getText());
                    }
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
    }


}