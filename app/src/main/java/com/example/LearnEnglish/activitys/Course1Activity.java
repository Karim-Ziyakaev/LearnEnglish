package com.example.LearnEnglish.activitys;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.VideoAdapter;
import com.example.LearnEnglish.models.Video;

import java.util.ArrayList;

public class Course1Activity extends AppCompatActivity {

    ArrayList<Video> videos = new ArrayList<Video>();
    ListView videosList;
    String videosID[] = new String[] {
            "BAahBqreWZw","ysz_Dxcrfsg", "EMdmZ42heRE", "xC0MwjgLGA4", "Gn4gfQmxlK0",
            "GQP2b8ZMHWI", "xmcLMu2OxmA", "5VIgKFpp3ak", "Ik1G4nlbXGQ", "x-P7-oML3mQ",
            "IGyp82h3no8", "5B44ZzBs2Tg", "owkrGMINkOM", "jKzF4lBimXc", "SOcCOltbk_o",
            "oiVXwDhPfPs", "EYWHhBe1u4c", "odWkcYJwVWQ", "qyr3HGYtg-A", "qLxBLp1At40",
            "7Bgw6kzWI6g", "WjnKDFCJg7Q", "un_8wI2_8ME", "Cl91cczpGho", "Z7a72Fo4pbc",
            "PaVjeXxvdNE", "-1OsLpzk_rw", "D9ppa0uRHZw", "LAwq8zpYUyg", "ZtycLb1isko",
            "DQaI8xpTqes", "r5pLQRf9fHY", "QWAXSVuc9RY", "Z-_AJ2EbA-s", "1BgvxodE3Zo",
            "hBzrp91-moM", "yDo4ctMQzv0", "Fd1zdFeaCog", "al6c0zXUhtw", "S9t2mJiBlEY",
            "wABrHheTURo", "q63fvc8FYJU", "Ic5pPWSQ9NM", "J8MyJEoEdP4", "vv8KEuwpuTQ",
            "n_mrp-fnbjU", "Nd9cXkOnyP0", "2aMUxHMvJPY", "dqx67G3ErEk", "IJyIbRwP1WU"
    };
    final int COUNT_ID = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course1);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // начальная инициализация списка
        setInitialData();
        // получаем элемент ListView
        videosList = findViewById(R.id.videosList);
        // создаем адаптер
        VideoAdapter stateAdapter = new VideoAdapter(this, R.layout.list_video_item, videos);
        // устанавливаем адаптер
        videosList.setAdapter(stateAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Video selectedVideo = (Video)parent.getItemAtPosition(position);
                Intent intent = new Intent(Course1Activity.this, VideoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("id", selectedVideo.getThumbnailResource());
                startActivity(intent);
            }
        };
        videosList.setOnItemClickListener(itemListener);
    }

    private void setInitialData(){
        String title = "ВЕСЬ АНГЛИЙСКИЙ ЯЗЫК В ОДНОМ КУРСЕ УРОК ";
        for (int i = 0; i < COUNT_ID; i++) {
            videos.add(new Video(title + (i + 1), videosID[i]));
        }
    }
}