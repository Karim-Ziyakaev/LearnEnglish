package com.example.LearnEnglish.models;

public class Video {
    private String title;   // название видева
    private String thumbnailResource;  // пикча

    public Video(String title, String thumbnailResourse) {
        this.title = title;
        this.thumbnailResource = thumbnailResourse;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getThumbnailResource(){
        return this.thumbnailResource;
    }

    public void setThumbnailResource(String thumbnailResource){
        this.thumbnailResource = thumbnailResource;
    }
}
