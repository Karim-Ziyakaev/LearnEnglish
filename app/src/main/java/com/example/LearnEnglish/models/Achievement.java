package com.example.LearnEnglish.models;

public class Achievement {
    private String title;
    private String description;
    private int progress;
    private int totalProgress;

    public Achievement(String title, String description, int totalProgress) {
        this.title = title;
        this.description = description;
        this.totalProgress = totalProgress;
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
