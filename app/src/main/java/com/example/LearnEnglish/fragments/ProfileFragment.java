package com.example.LearnEnglish.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.AchievementsAdapter;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.models.Achievement;
import com.example.LearnEnglish.models.Word;

import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private AchievementsAdapter adapter;
    private List<Achievement> achievements;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        requireActivity().setTitle("Profile");
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        achievements = loadAchievements(); // Загрузите достижения из вашего источника данных
        adapter = new AchievementsAdapter(achievements);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    private List<Achievement> loadAchievements() {

        return null;
    }
}