package com.example.LearnEnglish.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {
    private long id;
    private String word;
    private String translate;
    private boolean isSelected;
    private int isFavorite;
    private int attempts;
    private int wrongAttempts;
    private int correctAttempts;
    private int isLearned;

    public Word(long id, String word, String translate, int isFavorite){
        this.id = id;
        this.word = word;
        this.translate = translate;
        this.isSelected = false;
        this.isFavorite = isFavorite;
        attempts = 0;
        wrongAttempts = 0;
        correctAttempts = 0;
        isLearned = 0;
    }

    public Word(long id, String word, String translate, int isFavorite, int correctAttempts, int wrongAttempts, int attempts, int isLearned){
        this.id = id;
        this.word = word;
        this.translate = translate;
        this.isSelected = false;
        this.isFavorite = isFavorite;
        this.correctAttempts = correctAttempts;
        this.wrongAttempts = wrongAttempts;
        this.attempts = attempts;
        this.isLearned = isLearned;
    }

    protected Word(Parcel in) {
        id = in.readLong();
        word = in.readString();
        translate = in.readString();
        isSelected = in.readByte() != 0;
        isFavorite = in.readInt();
        attempts = in.readInt();
        wrongAttempts = in.readInt();
        correctAttempts = in.readInt();
        isLearned = in.readInt();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

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

    public void setTranslate(String translate){
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

    public void setAttempts(int attempts){
        this.attempts = attempts;
    }
    public int getAttempts(){
        return attempts;
    }

    public void setWrongAttempts(int wrongAttempts){
        this.wrongAttempts = wrongAttempts;
    }
    public int getWrongAttempts(){
        return wrongAttempts;
    }

    public void setCorrectAttempts(int correctAttempts){
        this.correctAttempts = correctAttempts;
    }
    public int getCorrectAttempts(){
        return correctAttempts;
    }

    public void setIsLearned(int isLearned){
        this.isLearned = isLearned;
    }

    public int getIsLearned(){
        return isLearned;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(word);
        parcel.writeString(translate);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeInt(isFavorite);
        parcel.writeInt(attempts);
        parcel.writeInt(wrongAttempts);
        parcel.writeInt(correctAttempts);
        parcel.writeInt(isLearned);
    }
}
