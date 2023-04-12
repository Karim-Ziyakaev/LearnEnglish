package com.example.LearnEnglish.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.models.Word;

import java.util.Map;
import java.util.Objects;

public class ResultsFragment extends Fragment {

    private Word mWord;
    private String answer;
    private int position;
    private TextView wordText;
    private TextView percentText;
    private TextView kdText;
    private ImageView stonksImage;
    ProgressBar progressBar;


    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment getInstance(Word word, String answer, int position) {
        ResultsFragment fragment = new ResultsFragment();
        if (word!=null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("word", word);
            bundle.putString("answer", answer);
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
        }
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
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordText = view.findViewById(R.id.word_text);
        percentText =view.findViewById(R.id.percent_text);
        kdText = view.findViewById(R.id.kd_text);
        stonksImage = view.findViewById(R.id.stonks_image_view);
        progressBar = view.findViewById(R.id.text_progress_bar);
        if (getArguments()!=null){
            mWord = getArguments().getParcelable("word");
            position = getArguments().getInt("position");
            answer = getArguments().getString("answer");
            init();
        }
    }

    private void init(){
        wordText.setText(mWord.getWord() + " - " + mWord.getTranslate());
        int correct=mWord.getCorrectAttempts();
        int wrong = mWord.getWrongAttempts()==0?1: mWord.getWrongAttempts();
        float kd = (float) correct/wrong;
        float oldKd;
        String strKd = String.valueOf(kd);
        float progressPercent = (kd/10)*100;
        float oldProgressPercent = 0;
        progressBar.setProgress((int)progressPercent);
        if(Objects.equals(mWord.getTranslate().toLowerCase(), answer.toLowerCase())){
            stonksImage.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
            // реализация setSecondaryProgress, чтобы было видно прогресс изучения
            oldKd = (float)(correct - 1)/wrong;
            oldProgressPercent = (oldKd/10) * 100;
            percentText.setText((int)oldProgressPercent + " +" +(int)(progressPercent-oldProgressPercent)+"%");
        } else{
            stonksImage.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
            if (correct>0) {
                oldKd = (float)(correct - 1)/wrong;
                oldProgressPercent = (oldKd/10) * 100;
            }
            percentText.setText((int)oldProgressPercent + " -" +(int)(progressPercent-oldProgressPercent)+"%");
        }
        progressBar.setProgress((int)oldProgressPercent);
        progressBar.setSecondaryProgress((int)progressPercent);
        kdText.setText(strKd);
        DatabaseAdapter db_adapter = new DatabaseAdapter(requireContext());
        db_adapter.open();
        db_adapter.update(mWord);
        db_adapter.close();
    }
}