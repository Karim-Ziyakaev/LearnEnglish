package com.example.LearnEnglish.fragments;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.models.Word;

public class TestENRUFragment extends Fragment {
    private Word mWord;
    private TextView wordText;
    private EditText wordEditText;
    private Button checkButton;
    private CardView cardView;

    private TestENRUFragment.OnFragmentAnswerListener mListener;

    public void setOnFragmentAnswerListener(TestENRUFragment.OnFragmentAnswerListener listener) {
        mListener = listener;
    }

    public interface OnFragmentAnswerListener {
        void onFragmentAnswer(int position, String answer);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TestENRUFragment.OnFragmentAnswerListener) {
            mListener = (TestENRUFragment.OnFragmentAnswerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentAnswerListener");
        }
    }

    public static TestENRUFragment getInstance(Word word) {
        TestENRUFragment fragment = new TestENRUFragment();
        if (word!=null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("word", word);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public TestENRUFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_enru, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordText = view.findViewById(R.id.word_text);
        wordEditText =view.findViewById(R.id.word_edit_text);
        checkButton = view.findViewById(R.id.check_button);
        cardView = view.findViewById(R.id.cardTest);
        if (getArguments()!=null){
            mWord = getArguments().getParcelable("word");
            init();
        }
    }

    private void init(){
        if(mWord!=null){
            wordText.setText(mWord.getWord());
        }
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wordEditText.getText().toString().toLowerCase().trim().equals(mWord.getWord().toLowerCase())){
                    Toast.makeText(requireContext(), getString(R.string.type_answer), Toast.LENGTH_SHORT).show();
                }
                else {
                    if (wordEditText.getText().toString().toLowerCase().trim().equals(mWord.getTranslate().toLowerCase())) {
                        wordText.setText("");
                        wordText.setTextColor(Color.GREEN);
                        mWord.setAttempts(mWord.getAttempts() + 1);
                        mWord.setCorrectAttempts(mWord.getCorrectAttempts() + 1);
                        cardView.setCardBackgroundColor(Color.rgb(56, 194, 47));
                        wordEditText.setVisibility(View.INVISIBLE);
                    } else {
                        mWord.setAttempts(mWord.getAttempts() + 1);
                        mWord.setWrongAttempts(mWord.getWrongAttempts() + 1);
                        wordText.setText("");
                        cardView.setCardBackgroundColor(Color.RED);
                        wordEditText.setVisibility(View.INVISIBLE);
                    }
                    checkButton.setVisibility(View.GONE);

                    ViewPager viewPager = requireActivity().findViewById(R.id.test_pager);
                    if (mListener != null) {
                        mListener.onFragmentAnswer(viewPager.getCurrentItem(), wordEditText.getText().toString().trim());
                    }
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
    }
}