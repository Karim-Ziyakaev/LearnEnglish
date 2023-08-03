package com.example.LearnEnglish.activitys;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.adapters.VideoAdapter;
import com.example.LearnEnglish.models.Video;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Course1Activity extends AppCompatActivity {
    SharedPreferences settings;
    ArrayList<Video> videos;
    ListView videosList;
    String course;
    String courseLastVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course1);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        DatabaseAdapter db_adapter = new DatabaseAdapter(this);
        db_adapter.open();
        db_adapter.updateAchievementCourse();
        db_adapter.close();

        videos = new ArrayList<>();
        // получаем элемент ListView
        videosList = findViewById(R.id.videosList);

        Bundle arguments = getIntent().getExtras();
        String selectedCourse = arguments.get("selectedCourse").toString();

        switch (selectedCourse){
            case "Course1":
                course = "course_english_zero";
                courseLastVideo = getString(R.string.english_zero);
                break;
            case "CourseTimes":
                course = "course_times";
                courseLastVideo = getString(R.string.times_in_english);
                break;
            case "CourseLifehacks":
                course = "course_lifehacks";
                courseLastVideo = getString(R.string.lifehacks);
                break;
            case "CourseGames":
                course = "course_games";
                courseLastVideo = getString(R.string.games_english);
                break;
            default:
                break;
        }

        XmlPullParser xpp = getResources().getXml(R.xml.courses_data);

        Video currentVideo = null;
        boolean inEntry = false;
        String textValue = "";
        String startTag = null;
        String endTag = null;
        try {
            int eventType = xpp.getEventType();
            while (eventType!= XmlPullParser.END_DOCUMENT && !Objects.equals(startTag, course)){
                if(eventType == XmlPullParser.START_TAG){
                    startTag = xpp.getName();
                }
                eventType = xpp.next();
            }
            while(eventType != XmlPullParser.END_DOCUMENT && !Objects.equals(endTag, course)){
                String tagName = xpp.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if("snippet".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentVideo = new Video();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(inEntry){
                            if("snippet".equalsIgnoreCase(tagName)){
                                videos.add(currentVideo);
                                inEntry = false;
                            } else if("videoId".equalsIgnoreCase(tagName)){
                                currentVideo.setId(textValue);
                            } else if("title".equalsIgnoreCase(tagName)){
                                currentVideo.setTitle(textValue);
                            }
                        }
                        endTag = xpp.getName();
                        break;
                    default:
                }
                eventType = xpp.next();
            }
        }
        catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        videosList = findViewById(R.id.videosList);
        VideoAdapter videoAdapter = new VideoAdapter(this, R.layout.list_video_item, videos);
        videosList.setAdapter(videoAdapter);
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Video selectedVideo = (Video)parent.getItemAtPosition(position);

                settings = getSharedPreferences("LastVideo", 0);
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putString("course", courseLastVideo);
                prefEditor.putString("video_id", selectedVideo.getId());
                prefEditor.putString("title", selectedVideo.getTitle());
                prefEditor.apply();

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + selectedVideo.getId())));
            }
        };
        videosList.setOnItemClickListener(itemListener);
    }
}