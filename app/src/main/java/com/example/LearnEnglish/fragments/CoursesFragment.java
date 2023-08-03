package com.example.LearnEnglish.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.activitys.Course1Activity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CoursesFragment extends Fragment {
    SharedPreferences settings;
    LinearLayout lastVideoLayout;
    TextView titleText;
    TextView lastCourseText;
    ImageView thumbnail;
    CardView cardLastVideo;
    boolean shouldRefreshLastVideo = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceVideo) {
        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);
        requireActivity().setTitle(getString(R.string.courses));

        settings = requireContext().getSharedPreferences("LastVideo", 0);
        String video_id = settings.getString("video_id","не определено");
        String title = settings.getString("title","не определено");
        String lastCourse = settings.getString("course", "не определено");
        if(!Objects.equals(video_id, "не определено")){
            lastVideoLayout = rootView.findViewById(R.id.last_video_layout);
            lastVideoLayout.setVisibility(View.VISIBLE);

            thumbnail = rootView.findViewById(R.id.thumbnail);
            String url = "https://i.ytimg.com/vi/" + video_id + "/maxresdefault.jpg";
            Picasso.get().load(url).fit().placeholder(R.drawable.ic_baseline_cached_24).into(thumbnail);

            titleText = rootView.findViewById(R.id.title);
            titleText.setText(title);

            lastCourseText = rootView.findViewById(R.id.course_text);
            lastCourseText.setText(lastCourse);

            cardLastVideo = rootView.findViewById(R.id.last_video_card);
            cardLastVideo.setOnClickListener(view -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video_id)));
            });
        }

        CardView cardCourse1 = rootView.findViewById(R.id.cardCourse1);
        cardCourse1.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), Course1Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("selectedCourse", "Course1");
            startActivity(intent);
        });

        CardView cardCourseTimes = rootView.findViewById(R.id.cardCourseTimes);
        cardCourseTimes.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), Course1Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("selectedCourse", "CourseTimes");
            startActivity(intent);
        });

        CardView cardCourseLifehacks = rootView.findViewById(R.id.cardCourseLifehacks);
        cardCourseLifehacks.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), Course1Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("selectedCourse", "CourseLifehacks");
            startActivity(intent);
        });

        CardView cardCourseGames = rootView.findViewById(R.id.cardCourseGames);
        cardCourseGames.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), Course1Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("selectedCourse", "CourseGames");
            startActivity(intent);
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshLastVideo){
            settings = requireContext().getSharedPreferences("LastVideo", 0);
            String video_id = settings.getString("video_id","не определено");
            String title = settings.getString("title","не определено");
            String lastCourse = settings.getString("course", "не определено");
            if(!Objects.equals(video_id, "не определено")){
                lastVideoLayout.setVisibility(View.VISIBLE);

                String url = "https://i.ytimg.com/vi/" + video_id + "/maxresdefault.jpg";
                Picasso.get().load(url).fit().placeholder(R.drawable.ic_baseline_cached_24).into(thumbnail);

                titleText.setText(title);

                lastCourseText.setText(lastCourse);

                cardLastVideo.setOnClickListener(view -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video_id)));
                });
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshLastVideo = true;
    }
}
