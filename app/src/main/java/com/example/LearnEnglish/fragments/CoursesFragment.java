package com.example.LearnEnglish.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.activitys.Course1Activity;

public class CoursesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);
        requireActivity().setTitle("Courses");

        CardView cardCourse1 = rootView.findViewById(R.id.cardCourse1);
        cardCourse1.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), Course1Activity.class);
            startActivity(intent);
        });


        return rootView;
    }
}
