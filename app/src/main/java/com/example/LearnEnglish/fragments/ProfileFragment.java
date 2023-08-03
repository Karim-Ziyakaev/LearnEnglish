package com.example.LearnEnglish.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.AchievementsAdapter;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.models.Achievement;
import com.example.LearnEnglish.models.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private ProfileFragment.OnWordsChangeListener mListener;
    private ArrayList<Word> words;
    SharedPreferences settings;

    public interface OnWordsChangeListener {
        void onWordsChanged(ArrayList<Word> words);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ProfileFragment.OnWordsChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnWordsChangeListener");
        }
    }

    public ProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        requireActivity().setTitle(getString(R.string.profile));
        Button deleteWords = rootView.findViewById(R.id.delete_words_button);
        Button resetAll = rootView.findViewById(R.id.reset_all_button);
        Button language = rootView.findViewById(R.id.language_button);
        TextView countAddedWords = rootView.findViewById(R.id.count_added_text);
        TextView countLearnedWords = rootView.findViewById(R.id.count_learned_text);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseAdapter db_adapter = new DatabaseAdapter(requireContext());
        db_adapter.open();
        List<Achievement> achievements = db_adapter.getAllAchievementsProfile();
        db_adapter.close();
        AchievementsAdapter adapter = new AchievementsAdapter(achievements);
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        //костыль с подсчетом количества слов и выученных, данные берутся из ачивки с 1000
        countAddedWords.setText(String.valueOf(achievements.get(4).getProgress()));
        countLearnedWords.setText(String.valueOf(achievements.get(8).getProgress()));

        int haveProgress = 0;
        if (words.isEmpty()) {
            deleteWords.setEnabled(false);
            for (Achievement i: achievements){
                if(i.getProgress() > 0){
                    haveProgress++;
                }
            }
        }
        if (haveProgress == 1)
            resetAll.setEnabled(false);


        deleteWords.setOnClickListener(view -> {
            for (Word i: words){
                db_adapter.delete(i.getId());
            }
            words.clear();
            mListener.onWordsChanged(words);
            deleteWords.setEnabled(false);
        });

        resetAll.setOnClickListener(view -> {
            db_adapter.resetAll();
            resetAll.setEnabled(false);
            deleteWords.setEnabled(false);
            words.clear();
            mListener.onWordsChanged(words);
            for(Achievement i: achievements){
                if(i.getId()!=1){
                    i.setProgress(0);
                }
            }
            adapter.setAchievements(achievements);
            countAddedWords.setText("0");
            countLearnedWords.setText("0");

            settings = requireContext().getSharedPreferences("LastVideo", 0);
            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.putString("course", "не определено");
            prefEditor.putString("video_id", "не определено");
            prefEditor.putString("title", "не определено");
            prefEditor.apply();
        });

        language.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.select_language_dialog, null);
            final Button english_button = dialogView.findViewById(R.id.english_button);
            final Button russian_button = dialogView.findViewById(R.id.russian_button);
            builder.setView(dialogView)
                    .setNeutralButton(getString(R.string.close), null)
                    .create()
                    .show();
            english_button.setOnClickListener(view1 -> {
                setLanguage(requireActivity(), "en");
                requireActivity().finish();
                startActivity(((Activity) requireContext()).getIntent());
            });
            russian_button.setOnClickListener(view1 -> {
                setLanguage(requireActivity(), "ru");
                requireActivity().finish();
                startActivity(((Activity) requireContext()).getIntent());
            });
        });

        return rootView;
    }

    public void setLanguage(Activity activity, String language){
        Locale locale = new Locale(language);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}