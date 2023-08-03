package com.example.LearnEnglish.models;

public class Video {
    private String title;   // название видева
    private String id;  // пикча

    public Video(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public Video() {

    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String thumbnailResource){
        this.id = thumbnailResource;
    }
}
