package com.example.LearnEnglish.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.models.Word;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        requireActivity().setTitle("Profile");

        TextView textWord = rootView.findViewById(R.id.text_translate);
        TextView textTranslate = rootView.findViewById(R.id.text_word);

        DatabaseAdapter db_adapter = new DatabaseAdapter(requireContext());
        db_adapter.open();

        Word word = db_adapter.getRandomWord();

        textWord.setText(word.getWord());
        textTranslate.setText(word.getTranslate());

        // Inflate the layout for this fragment
        return rootView;
    }
}