package com.example.LearnEnglish.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.activitys.Course1Activity;

public class CoursesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceVideo) {
        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);
        requireActivity().setTitle("Courses");

        CardView cardCourse1 = rootView.findViewById(R.id.cardCourse1);
        cardCourse1.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), Course1Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
