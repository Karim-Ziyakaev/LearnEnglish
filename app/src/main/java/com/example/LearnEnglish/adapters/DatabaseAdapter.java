package com.example.LearnEnglish.adapters;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.LearnEnglish.models.Achievement;
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
                DatabaseHelper.COLUMN_TOTAL_ATTEMPTS, DatabaseHelper.COLUMN_IS_LEARNED
        };
        return  database.query(DatabaseHelper.TABLE_WORDS, columns, null, null, null, null, null);
    }

    @SuppressLint("Range")
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
            int isLearned = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_LEARNED));

            if(isFavorite == 1) {
                favWords.add(new Word(id, word, translate, isFavorite, correctAnswers, wrongAnswers, totalAttemps, isLearned));
            }
            else
                words.add(new Word(id, word, translate, isFavorite, correctAnswers, wrongAnswers, totalAttemps, isLearned));
        }
        words.addAll(0, favWords);
        cursor.close();
        return words;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_WORDS);
    }

    @SuppressLint("Range")
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
            int isLearned = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_LEARNED));

            word = new Word(id, s_word, translate, isFavorite, correctAnswers, wrongAnswers, totalAttemps, isLearned);
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
        cv.put(DatabaseHelper.COLUMN_IS_FAVORITE, word.getIsFavorite());
        cv.put(DatabaseHelper.COLUMN_CORRECT_ANSWERS, word.getCorrectAttempts());
        cv.put(DatabaseHelper.COLUMN_WRONG_ANSWERS, word.getWrongAttempts());
        cv.put(DatabaseHelper.COLUMN_TOTAL_ATTEMPTS, word.getAttempts());
        cv.put(DatabaseHelper.COLUMN_IS_LEARNED, word.getIsLearned());

        long edtCount = database.update(DatabaseHelper.TABLE_WORDS, cv, whereClause, null);
        Log.d("mLog", "changed rows count = " + edtCount);
        return edtCount;
    }

    @SuppressLint("Range")
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

    @SuppressLint("Range")
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

    @SuppressLint("Range")
    public List<Achievement> getAllAchievements() {
        ArrayList<Achievement> achievements = new ArrayList<>();
        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_PROGRESS, DatabaseHelper.COLUMN_TOTAL_PROGRESS
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_ACHIEVEMENTS, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
                int progress = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PROGRESS));
                int totalProgress = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_PROGRESS));

                Achievement achievement = new Achievement(id, title, description, progress, totalProgress);
                achievements.add(achievement);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return achievements;
    }

    public List<Achievement> getAllAchievementsProfile(){
        ArrayList<Achievement> achievements = new ArrayList<>();
        ArrayList<Achievement> completedAchievements = new ArrayList<>();
        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_PROGRESS, DatabaseHelper.COLUMN_TOTAL_PROGRESS
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_ACHIEVEMENTS, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
                int progress = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PROGRESS));
                int totalProgress = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_PROGRESS));

                Achievement achievement = new Achievement(id, title, description, progress, totalProgress);
                if (progress == totalProgress)
                    completedAchievements.add(achievement);
                else
                    achievements.add(achievement);
            } while (cursor.moveToNext());
        }
        cursor.close();
        achievements.addAll(0, completedAchievements);
        return achievements;
    }

//    public void updateAchievement(Achievement achievement){
//        String whereClause = DatabaseHelper.COLUMN_ID + "=" + achievement.getId();
//        ContentValues cv = new ContentValues();
//
//        cv.put(DatabaseHelper.COLUMN_PROGRESS, achievement.getProgress());
//
//        database.update(DatabaseHelper.TABLE_ACHIEVEMENTS, cv, whereClause, null);
//    }
    public void updateAchievementWords(){
        List<Achievement> achievements = getAllAchievements();
        for(int i = 2; i<6; i++){
            String whereClause = DatabaseHelper.COLUMN_ID + "=" + i;
            ContentValues cv = new ContentValues();

            //i-1 потому что с 0 список, а i тут как айди
            int progress = achievements.get(i-1).getProgress();
            if (progress != achievements.get(i-1).getTotalProgress())
                progress++;
            cv.put(DatabaseHelper.COLUMN_PROGRESS, progress);

            database.update(DatabaseHelper.TABLE_ACHIEVEMENTS, cv, whereClause, null);
        }
    }

    public void updateAchievementLearn(){
        List<Achievement> achievements = getAllAchievements();
        for(int i = 6; i<10; i++){
            String whereClause = DatabaseHelper.COLUMN_ID + "=" + i;
            ContentValues cv = new ContentValues();

            int progress = achievements.get(i-1).getProgress();
            if (progress != achievements.get(i-1).getTotalProgress())
                progress++;
            cv.put(DatabaseHelper.COLUMN_PROGRESS, progress);

            database.update(DatabaseHelper.TABLE_ACHIEVEMENTS, cv, whereClause, null);
        }
    }

    public void resetAll(){
        database.delete(DatabaseHelper.TABLE_WORDS, null, null);

        String whereClause = DatabaseHelper.COLUMN_ID + "=1";
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_PROGRESS, 0);
        database.update(DatabaseHelper.TABLE_ACHIEVEMENTS, cv, null, null);
        cv.clear();
        cv.put(DatabaseHelper.COLUMN_PROGRESS, 1);
        database.update(DatabaseHelper.TABLE_ACHIEVEMENTS, cv, whereClause, null);
    }
}