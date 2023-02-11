package com.example.LearnEnglish.models;

public class Word {
    private long id;
    private String word;
    private String translate;
    private boolean is_selected;

    public Word(long id, String word, String translate){
        this.id = id;
        this.word = word;
        this.translate = translate;
        this.is_selected = false;
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

    public void setIs_selected(boolean is_selected){
        this.is_selected = is_selected;
    }
    public boolean getIs_selected(){
        return is_selected;
    }
}
