package com.example.LearnEnglish.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.activitys.Course1Activity;
import com.example.LearnEnglish.activitys.TestActivity;
import com.example.LearnEnglish.models.Word;

import java.util.ArrayList;
import java.util.List;

public class TestsFragment extends Fragment {
    ArrayList<Word> words;
    private Context context;

    public TestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("words")) {
            words = getArguments().getParcelableArrayList("words");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tests, container, false);
        requireActivity().setTitle("Tests");

        Intent intent = new Intent(requireContext(), TestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putParcelableArrayListExtra("words", words);

        CardView cardTestRUEN = rootView.findViewById(R.id.cardTestRUEN);
        cardTestRUEN.setOnClickListener(view -> {
            intent.putExtra("selectedTest", "RUEN");
            startActivityForResult(intent, 1);
        });

        CardView cardTestENRU = rootView.findViewById(R.id.cardTestENRU);
        cardTestENRU.setOnClickListener(view -> {
            intent.putExtra("selectedTest", "ENRU");
            startActivityForResult(intent, 1);
        });

        CardView cardTestChoice = rootView.findViewById(R.id.cardTestChoice);
        cardTestChoice.setOnClickListener(view -> {
            intent.putExtra("selectedTest", "Choice");
            startActivityForResult(intent, 1);
        });

        CardView cardTestChoiceRu = rootView.findViewById(R.id.cardTestChoiceRu);
        cardTestChoiceRu.setOnClickListener(view -> {
            intent.putExtra("selectedTest", "ChoiceRu");
            startActivityForResult(intent, 1);
        });

        CardView cardTestCombo = rootView.findViewById(R.id.cardTestCombo);
        cardTestCombo.setOnClickListener(view -> {
            intent.putExtra("selectedTest", "Combo");
            startActivityForResult(intent, 1);
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}