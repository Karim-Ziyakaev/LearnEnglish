package com.example.LearnEnglish.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.models.Word;

public class UserActivity extends AppCompatActivity {

    EditText edt_word;
    EditText edt_translate;
    Button btn_delete;
    Button btn_save;

    private DatabaseAdapter adapter;
    private long wordId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        adapter = new DatabaseAdapter(this);
        edt_word = findViewById(R.id.edt_word);
        edt_translate = findViewById(R.id.edt_translate);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            wordId = extras.getLong("id");
        }
        // если 0, то добавление
        if (wordId > 0) {
            // получаем элемент по id из бд
            adapter.open();
            Word word = adapter.getWord(wordId);
            edt_word.setText(word.getWord());
            edt_translate.setText(word.getTranslate());
            adapter.close();
        } else {
            // скрываем кнопку удаления
            btn_delete.setVisibility(View.GONE);
        }
    }

    public void click_save(View view){

        String s_word = edt_word.getText().toString().trim();
        String translate = edt_translate.getText().toString().trim();
        if (s_word.isEmpty() || translate.isEmpty()) {
            if (s_word.isEmpty() && translate.isEmpty()) {
                edt_word.setError("Error, empty word");
                edt_translate.setError("Error, empty translation");
            }
            else if (translate.isEmpty())
                edt_translate.setError("Error, empty translation");
            else
                edt_word.setError("Error empty word");
        } else {
            Word word = new Word(wordId, s_word, translate, 0);

            adapter.open();
            if (wordId > 0) {
                adapter.update(word);
            } else {
                adapter.insert(word);
            }
            adapter.close();
            goHome();
        }
    }
    public void click_delete(View view){
        adapter.open();
        adapter.delete(wordId);
        adapter.close();
        goHome();
    }
    private void goHome(){
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
//        finish();
    }
}