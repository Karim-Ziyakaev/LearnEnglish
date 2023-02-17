package com.example.LearnEnglish.models;

public class Word {
    private long id;
    private String word;
    private String translate;
    private boolean isSelected;

    public Word(long id, String word, String translate){
        this.id = id;
        this.word = word;
        this.translate = translate;
        this.isSelected = false;
    }

    public long getId() {
        return id;
    }
    public void setId(long id){
        this.id = id;
    }

    public void setWord(String word){
        this.word = word;
    }
    public String getWord(){
        return this.word;
    }

    public void SetTranslate(String translate){
        this.translate = translate;
    }
    public String getTranslate(){
        return this.translate;
    }

    public void setIsSelected(boolean isSelected){
        this.isSelected = isSelected;
    }
    public boolean getIsSelected(){
        return isSelected;
    }
}
