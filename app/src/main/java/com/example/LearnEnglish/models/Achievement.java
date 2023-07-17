package com.example.LearnEnglish.models;

public class Achievement {
    private int id;
    private String title;
    private String description;
    private int progress;
    private int totalProgress;

    public Achievement(int id, String title, String description, int progress, int totalProgress) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.totalProgress = totalProgress;
    }

    public int getId()
    {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getProgress() {
        return progress;
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
