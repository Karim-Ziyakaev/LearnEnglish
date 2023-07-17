package com.example.LearnEnglish.adapters;

import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH; // полный путь к базе данных
    private static final String DB_NAME = "lengdb.db";
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE_WORDS = "words"; // название таблицы в бд
    static final String TABLE_RANDOM = "randomwords"; // название таблицы в бд
    static final String TABLE_ACHIEVEMENTS = "achievements";

    // названия столбцов
    static final String COLUMN_ID = "_id";

    static final String COLUMN_WORD = "word";
    static final String COLUMN_TRANSLATE = "translate";
    static final String COLUMN_IS_FAVORITE = "is_favorite";
    static final String COLUMN_CORRECT_ANSWERS = "correct_answers";
    static final String COLUMN_WRONG_ANSWERS = "wrong_answers";
    static final String COLUMN_TOTAL_ATTEMPTS = "total_attempts";
    static final String COLUMN_IS_LEARNED = "is_learned";

    static final String COLUMN_RANDOM_WORD = "random_word";
    static final String COLUMN_RANDOM_TRANSLATE = "random_translate";

    static final String COLUMN_TITLE = "title";
    static final String COLUMN_DESCRIPTION = "description";
    static final String COLUMN_PROGRESS = "progress";
    static final String COLUMN_TOTAL_PROGRESS = "total_progress";


    private Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext=context;
        DB_PATH =context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_WORDS + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_WORD
                + " TEXT NOT NULL, " + COLUMN_TRANSLATE + " TEXT NOT NULL, "
                + "is_favorite INTEGER DEFAULT 0);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_WORDS);
        onCreate(db);
    }

    public void create_db(){

        File file = new File(DB_PATH);
        if (!file.exists()) {
            //получаем локальную бд как поток
            try(InputStream myInput = myContext.getAssets().open(DB_NAME);
                // Открываем пустую бд
                OutputStream myOutput = new FileOutputStream(DB_PATH)) {

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }
    public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
