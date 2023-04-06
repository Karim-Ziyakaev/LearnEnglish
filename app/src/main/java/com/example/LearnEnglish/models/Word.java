package com.example.LearnEnglish.models;

public class Word {
    private long id;
    private String word;
    private String translate;
    private boolean isSelected;
    private int isFavorite;

    public Word(long id, String word, String translate, int isFavorite){
        this.id = id;
        this.word = word;
        this.translate = translate;
        this.isSelected = false;
        this.isFavorite = isFavorite;
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

    public void setIsFavorite(int isFavorite){
        this.isFavorite = isFavorite;
    }
    public int getIsFavorite(){
        return isFavorite;
    }
}
