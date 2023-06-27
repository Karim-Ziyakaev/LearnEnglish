package com.example.LearnEnglish.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.LearnEnglish.R;
import com.example.LearnEnglish.models.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends ArrayAdapter<Video> {
    private LayoutInflater inflater;
    private int layout;
    private List<Video> videos;

    public VideoAdapter(Context context, int resource, List<Video> videos) {
        super(context, resource, videos);
        this.videos = videos;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView thumbnailView = view.findViewById(R.id.thumbnail);
        TextView titleView = view.findViewById(R.id.title);

        Video video = videos.get(position);

        String url = "https://i.ytimg.com/vi/" + video.getId() + "/maxresdefault.jpg";
        Picasso.get().load(url).fit().placeholder(R.drawable.ic_baseline_cached_24).into(thumbnailView);
        titleView.setText(video.getTitle());

        return view;
    }
}
