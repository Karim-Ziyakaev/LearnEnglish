package com.example.LearnEnglish.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.LearnEnglish.models.Word;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DatabaseAdapter {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open(){
        database = dbHelper.open();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_WORD, DatabaseHelper.COLUMN_TRANSLATE,
                DatabaseHelper.COLUMN_IS_FAVORITE, DatabaseHelper.COLUMN_CORRECT_ANSWERS, DatabaseHelper.COLUMN_WRONG_ANSWERS,
                DatabaseHelper.COLUMN_TOTAL_ATTEMPTS
        };
        return  database.query(DatabaseHelper.TABLE_WORDS, columns, null, null, null, null, null);
    }

    public ArrayList<Word> getWords(){
        ArrayList<Word> words = new ArrayList<>();
        ArrayList<Word> favWords = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String word = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD));
            String translate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TRANSLATE));
            int isFavorite = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_FAVORITE));
            int correctAnswers = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CORRECT_ANSWERS));
            int wrongAnswers = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_WRONG_ANSWERS));
            int totalAttemps = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_ATTEMPTS));

            if(isFavorite == 1) {
                favWords.add(new Word(id, word, translate, isFavorite, correctAnswers, wrongAnswers, totalAttemps));
            }
            else
                words.add(new Word(id, word, translate, isFavorite, correctAnswers, wrongAnswers, totalAttemps));
        }
        words.addAll(0, favWords);
        cursor.close();
        return words;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_WORDS);
    }

    public Word getWord(long id){
        Word word = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE_WORDS, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String s_word = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD));
            String translate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TRANSLATE));

            int isFavorite = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_FAVORITE));
            int correctAnswers = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CORRECT_ANSWERS));
            int wrongAnswers = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_WRONG_ANSWERS));
            int totalAttemps = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_ATTEMPTS));

            word = new Word(id, s_word, translate, isFavorite, correctAnswers, wrongAnswers, totalAttemps);
        }
        cursor.close();
        return  word;
    }

    public long insert(Word word){

        ContentValues cv = new ContentValues();

        String s_word = word.getWord();
        s_word = s_word.substring(0,1).toUpperCase() + s_word.substring(1).toLowerCase();
        String s_translate = word.getTranslate();
        s_translate = s_translate.substring(0,1).toUpperCase() + s_translate.substring(1).toLowerCase();

        cv.put(DatabaseHelper.COLUMN_WORD, s_word);
        cv.put(DatabaseHelper.COLUMN_TRANSLATE, s_translate);
        
        return database.insert(DatabaseHelper.TABLE_WORDS, null, cv);
    }

    public long delete(long wordId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(wordId)};
        long delCount = database.delete(DatabaseHelper.TABLE_WORDS, whereClause, whereArgs);
        Log.d("mLog", "deleted rows count = " + delCount);
        return delCount;
    }

    public long update(Word word){

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + word.getId();
        ContentValues cv = new ContentValues();

        String s_word = word.getWord();
        s_word = s_word.substring(0,1).toUpperCase() + s_word.substring(1).toLowerCase();
        String s_translate = word.getTranslate();
        s_translate = s_translate.substring(0,1).toUpperCase() + s_translate.substring(1).toLowerCase();

        cv.put(DatabaseHelper.COLUMN_WORD, s_word);
        cv.put(DatabaseHelper.COLUMN_TRANSLATE, s_translate);
        cv.put(DatabaseHelper.COLUMN_CORRECT_ANSWERS, word.getCorrectAttempts());
        cv.put(DatabaseHelper.COLUMN_WRONG_ANSWERS, word.getWrongAttempts());
        cv.put(DatabaseHelper.COLUMN_TOTAL_ATTEMPTS, word.getAttempts());

        long edtCount = database.update(DatabaseHelper.TABLE_WORDS, cv, whereClause, null);
        Log.d("mLog", "changed rows count = " + edtCount);
        return edtCount;
    }

    public Word getRandomWord(){
        Word word = null;

        int index = 0;
        Random r = new Random();
        index = r.nextInt((int)DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_RANDOM));

        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE_RANDOM, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(index)});
        if(cursor.moveToFirst()){
            String s_word = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RANDOM_WORD));
            String translate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RANDOM_TRANSLATE));

            s_word = s_word.substring(0,1).toUpperCase() + s_word.substring(1);
            translate = translate.substring(0,1).toUpperCase() + translate.substring(1);

            word = new Word(index, s_word, translate, 0);
        }
        cursor.close();
        return  word;
    }


    public void setFavorite(long wordId, int isFavorite){
        ContentValues cv = new ContentValues();
        cv.put("is_favorite", isFavorite);
        String whereClause = DatabaseHelper.COLUMN_ID + "=" + wordId;
        database.update(DatabaseHelper.TABLE_WORDS, cv, whereClause, null);
    }

    public Set<Integer> getFavorite(){
        Set<Integer> favoritePositions = new HashSet<>();
        /*
        * Долго размышляя я понял почему i = 0 и i++, потому что я все равно в ресайкл
        * вью передаю массив отсортированный, где сначала будут избранные, так что
        * неважно позиция, главное подсчет сколько их
        */
        int i = 0;
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()){
            int isFavorite = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_FAVORITE));
            if(isFavorite == 1) {
                favoritePositions.add(i);
                i++;
            }
        }
        cursor.close();
        return favoritePositions;
    }


}