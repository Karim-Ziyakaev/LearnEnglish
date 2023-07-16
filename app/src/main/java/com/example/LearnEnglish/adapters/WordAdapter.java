package com.example.LearnEnglish.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.models.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> implements MyItemTouchHelperCallback.ItemTouchHelperAdapter {

    private boolean showTranslation = true;
    private boolean showWord = true;
    private boolean showButtons = true;

    private final Context context;
    private List<Word> wordList;
    private Set<Integer> wSelectedPositions;
    private final LayoutInflater inflater;
    private Set<Integer> wFavoritePositions;

    private boolean isFirstMove = true;
    private int oldPosForMove;
    private int newPosForMove;

    public WordAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.wordList = wordList;
        wSelectedPositions = new HashSet<>();
        wFavoritePositions = new HashSet<>();

        Integer count = 0;
        for (Word i : wordList){
            int fav = i.getIsFavorite();
            if (fav == 1)
            {
                wFavoritePositions.add(count);
                count++;
            }
        }
    }

    private OnSelectionChangedListener selectionChangedListener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(Set<Integer> selectedPositions);
        void onFavoriteChanged(int position);
        void onWordChangedPosition(int newPos);
        void onWordDeleted(Word word);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word currentWord = wordList.get(position);

        if (showWord) {
            holder.wordTextView.setText(currentWord.getWord());
            holder.wordTextView.setVisibility(View.VISIBLE);
        } else {
            holder.wordTextView.setVisibility(View.GONE);
        }

        // Check the value of the showTranslation flag
        if (showTranslation) {
            holder.translationTextView.setText(currentWord.getTranslate());
            holder.translationTextView.setVisibility(View.VISIBLE);
        } else {
            holder.translationTextView.setVisibility(View.GONE);
        }

        if (showButtons) {
            holder.favoriteImageButton.setVisibility(View.VISIBLE);
            holder.moreImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.favoriteImageButton.setVisibility(View.GONE);
            holder.moreImageButton.setVisibility(View.GONE);
        }

        if (wSelectedPositions.contains(position)) {
        // Элемент выделен
            currentWord.setIsSelected(true);
            holder.itemView.setBackgroundResource(R.drawable.border_move);
        } else {
        // Элемент не выделен
            currentWord.setIsSelected(false);
            currentWord.setIsSelected(false);
            holder.itemView.setBackgroundResource(R.drawable.border);
        }

        if (wFavoritePositions.contains(position)) {
            currentWord.setIsFavorite(1);
            holder.favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            currentWord.setIsFavorite(0);
            holder.favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
    }

    public void toggleSelection(int position) {
        if (wSelectedPositions.contains(position)) {
            // Элемент уже выделен, поэтому убираем его из выделенных
            wSelectedPositions.remove(position);
        } else {
            // Элемент не выделен, поэтому добавляем его к выделенным
            wSelectedPositions.add(position);
        }
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(wSelectedPositions);
        }
//        notifyItemChanged(position);
    }

    public void toggleFavorite(int position){
        Word word = wordList.get(position);
        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();
        // изза того что все равно избранные сверху находятся в начале списке
        if (wFavoritePositions.contains(position)) {
            wFavoritePositions.remove(wFavoritePositions.size()-1);
            db_adapter.setFavorite(word.getId(), 0);
            word.setIsFavorite(0);
        } else {
            wFavoritePositions.add(wFavoritePositions.size());
            db_adapter.setFavorite(word.getId(), 1);
            word.setIsFavorite(1);
        }
        if (selectionChangedListener != null) {
            selectionChangedListener.onFavoriteChanged(position);
        }
        db_adapter.close();
//        notifyItemChanged(position);
    }

    public void clearSelections() {
        wSelectedPositions.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        TextView wordTextView;
        TextView translationTextView;
        ImageButton favoriteImageButton;
        ImageButton moreImageButton;

        public WordViewHolder(View itemView){
            super(itemView);
            wordTextView = itemView.findViewById(R.id.word_text_view);
            translationTextView = itemView.findViewById(R.id.translation_text_view);
            favoriteImageButton = itemView.findViewById(R.id.image_button_favorite);
            moreImageButton = itemView.findViewById(R.id.image_button_more);

            moreImageButton.setOnClickListener(this::showPopupMenu);

            favoriteImageButton.setOnClickListener(v -> {
                toggleFavorite(getAdapterPosition());
            });

            // Устанавливаем слушатель нажатий на элемент
            itemView.setOnClickListener(v -> {
                // Переключаем состояние выделения элемента
                toggleSelection(getAdapterPosition());
            });
        }

        private void showPopupMenu(View view){
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_popup_edit:
                    clickChange(getAdapterPosition());
                    return true;
                case R.id.action_popup_delete:
                    // Remove the item from the list
                    removeByIdx(getAdapterPosition());
                    return true;
                case R.id.action_popup_statistcs:
                    clickStatistics(getAdapterPosition());
                    return true;
            }
            return false;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Word> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
    }

    public void addItem(Word word) {
        wordList.add(word);
        notifyItemInserted(wordList.size()-1);
    }

    public void setShowTranslation(boolean showTranslation) {
        this.showTranslation = showTranslation;
    }
    public boolean getShowTranslation() {
        return showTranslation;
    }

    public void setShowWord(boolean showWord) {
        this.showWord = showWord;
    }
    public boolean getShowWord() {
        return showWord;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public boolean getShowButtons() {
        return showButtons;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if(isFirstMove){
            oldPosForMove = fromPosition;
            isFirstMove = false;
        }
        newPosForMove = toPosition;
        Collections.swap(wordList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete")
                .setMessage(wordList.get(position).getWord() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeByIdx(position);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
        notifyItemChanged(position);
    }

    @Override
    public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundResource(R.drawable.border_move);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
        isFirstMove = true;
//        newPosForMove = viewHolder.getAdapterPosition();
        if (oldPosForMove != newPosForMove) {

            DatabaseAdapter db_adapter = new DatabaseAdapter(context);
            db_adapter.open();

            if (oldPosForMove < newPosForMove) {
                Word temp = new Word(
                        wordList.get(newPosForMove).getId(),
                        wordList.get(newPosForMove).getWord(),
                        wordList.get(newPosForMove).getTranslate(),
                        wordList.get(newPosForMove).getIsFavorite(),
                        wordList.get(newPosForMove).getCorrectAttempts(),
                        wordList.get(newPosForMove).getWrongAttempts(),
                        wordList.get(newPosForMove).getAttempts());

                wordList.get(newPosForMove).setWord(wordList.get(oldPosForMove).getWord());
                wordList.get(newPosForMove).setTranslate(wordList.get(oldPosForMove).getTranslate());
                wordList.get(newPosForMove).setIsFavorite(wordList.get(oldPosForMove).getIsFavorite());
                wordList.get(newPosForMove).setCorrectAttempts(wordList.get(oldPosForMove).getCorrectAttempts());
                wordList.get(newPosForMove).setWrongAttempts(wordList.get(oldPosForMove).getWrongAttempts());
                wordList.get(newPosForMove).setAttempts(wordList.get(oldPosForMove).getAttempts());

                db_adapter.update(wordList.get(newPosForMove));

                for (int i = newPosForMove - 1; i >= oldPosForMove; i--) {
                    Word oldWord = new Word(
                            wordList.get(i).getId(),
                            wordList.get(i).getWord(),
                            wordList.get(i).getTranslate(),
                            wordList.get(i).getIsFavorite(),
                            wordList.get(i).getCorrectAttempts(),
                            wordList.get(i).getWrongAttempts(),
                            wordList.get(i).getAttempts());

                    wordList.get(i).setWord(temp.getWord());
                    wordList.get(i).setTranslate(temp.getTranslate());
                    wordList.get(i).setIsFavorite(temp.getIsFavorite());
                    wordList.get(i).setCorrectAttempts(temp.getCorrectAttempts());
                    wordList.get(i).setWrongAttempts(temp.getWrongAttempts());
                    wordList.get(i).setAttempts(temp.getAttempts());

                    db_adapter.update(wordList.get(i));

                    temp = oldWord;
                }
            } else if (oldPosForMove > newPosForMove) {
                Word temp = new Word(
                        wordList.get(newPosForMove).getId(),
                        wordList.get(newPosForMove).getWord(),
                        wordList.get(newPosForMove).getTranslate(),
                        wordList.get(newPosForMove).getIsFavorite(),
                        wordList.get(newPosForMove).getCorrectAttempts(),
                        wordList.get(newPosForMove).getWrongAttempts(),
                        wordList.get(newPosForMove).getAttempts());

                wordList.get(newPosForMove).setWord(wordList.get(oldPosForMove).getWord());
                wordList.get(newPosForMove).setTranslate(wordList.get(oldPosForMove).getTranslate());
                wordList.get(newPosForMove).setIsFavorite(wordList.get(oldPosForMove).getIsFavorite());
                wordList.get(newPosForMove).setCorrectAttempts(wordList.get(oldPosForMove).getCorrectAttempts());
                wordList.get(newPosForMove).setWrongAttempts(wordList.get(oldPosForMove).getWrongAttempts());
                wordList.get(newPosForMove).setAttempts(wordList.get(oldPosForMove).getAttempts());

                db_adapter.update(wordList.get(newPosForMove));

                for (int i = newPosForMove + 1; i <= oldPosForMove; i++) {
                    Word oldWord = new Word(
                            wordList.get(i).getId(),
                            wordList.get(i).getWord(),
                            wordList.get(i).getTranslate(),
                            wordList.get(i).getIsFavorite(),
                            wordList.get(i).getCorrectAttempts(),
                            wordList.get(i).getWrongAttempts(),
                            wordList.get(i).getAttempts());

                    wordList.get(i).setWord(temp.getWord());
                    wordList.get(i).setTranslate(temp.getTranslate());
                    wordList.get(i).setIsFavorite(temp.getIsFavorite());
                    wordList.get(i).setCorrectAttempts(temp.getCorrectAttempts());
                    wordList.get(i).setWrongAttempts(temp.getWrongAttempts());
                    wordList.get(i).setAttempts(temp.getAttempts());

                    db_adapter.update(wordList.get(i));

                    temp = oldWord;
                }
            }
            db_adapter.close();

            if (selectionChangedListener != null) {
                selectionChangedListener.onWordChangedPosition(newPosForMove);
            }
        }
        newPosForMove = -1;
        oldPosForMove = -1;
        viewHolder.itemView.setBackgroundResource(R.drawable.border);
    }

    public int getSelectedItemCount() {
        return wSelectedPositions.size();
    }

    private void clickChange(int position) {
        // Get the current word and translation
        Word word = wordList.get(position);
        String currentWord = word.getWord();
        String currentTranslation = word.getTranslate();
        // Create an AlertDialog to edit the word and translation
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.change_word_dialog, null);
        final EditText wordEditText = dialogView.findViewById(R.id.word_edit_text);
        final EditText translationEditText = dialogView.findViewById(R.id.translation_edit_text);
        wordEditText.setText(currentWord);
        translationEditText.setText(currentTranslation);
        builder.setView(dialogView)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String s_word = wordEditText.getText().toString().trim();
                        String s_translate = translationEditText.getText().toString().trim();

                        if (s_word.isEmpty() || s_translate.isEmpty()) {
                            if (s_word.isEmpty())
                                wordEditText.setError("Error, empty word");
                            else
                                translationEditText.setError("Error, empty translation");
                        } else {
                            s_word = s_word.substring(0,1).toUpperCase() + s_word.substring(1).toLowerCase();
                            s_translate = s_translate.substring(0,1).toUpperCase() + s_translate.substring(1).toLowerCase();

                            wordList.set(position, new Word(word.getId(), s_word, s_translate, word.getIsFavorite()));
                            DatabaseAdapter db_adapter = new DatabaseAdapter(context);
                            db_adapter.open();
                            // Ошибка раньше была в том что передавал старое слово
                            Word new_word = wordList.get(position);
                            db_adapter.update(new_word);
                            db_adapter.close();
                            // Notify the adapter that the data has changed
                            notifyItemChanged(position);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void clickStatistics(int position){
        Word word = wordList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.statistics_word_dialog, null);
        final TextView wordText = dialogView.findViewById(R.id.word_text);
        final TextView correctText = dialogView.findViewById(R.id.correct_text);
        final TextView wrongText = dialogView.findViewById(R.id.wrong_text);
        final TextView totalText = dialogView.findViewById(R.id.total_text);
        final TextView kdText = dialogView.findViewById(R.id.kd_text);
        final TextView percentProgressText = dialogView.findViewById(R.id.percent_progress_text);
        final ProgressBar progressBar = dialogView.findViewById(R.id.progress_bar);
        wordText.setText(word.getWord());
        correctText.setText("Correct attempts: " + word.getCorrectAttempts());
        wrongText.setText("Wrong attempts: " + word.getWrongAttempts());
        totalText.setText("Total attempts: " + word.getAttempts());
        float kd =(float) word.getCorrectAttempts()/(word.getWrongAttempts()==0?1:word.getWrongAttempts());
        float percent = (kd/10) * 100;
        String strKd = String.valueOf(kd);
        String strPercent = percent + "%";
        kdText.setText(strKd);
        percentProgressText.setText(strPercent);
        progressBar.setProgress((int)percent);
        builder.setView(dialogView)
                .setNeutralButton("Ok", null)
                .create()
                .show();
    }

    public void removeByIdx(int position)
    {
        Word word = wordList.get(position);
        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();
        //надо именно гетИд потому что айди в бд другое чем в листе
        db_adapter.delete(word.getId());
        db_adapter.close();
        wordList.remove(position);
        wSelectedPositions.clear();
        if(word.getIsFavorite() == 1)
            wFavoritePositions.remove(position);
        notifyItemRemoved(position);
        if (selectionChangedListener != null) {
            selectionChangedListener.onWordDeleted(word);
        }
        // Notify the adapter that the data has changed

    }

    public void removeSelected(Set<Integer> selectedPositions){
        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();

        List<Integer> selectedPositionsList = new ArrayList<>(selectedPositions);
        Collections.sort(selectedPositionsList, Collections.reverseOrder());

        for (int i : selectedPositionsList){
            if (i < wordList.size()) {
                Word word = wordList.get(i);
                db_adapter.delete(word.getId());
                if(word.getIsFavorite() == 1)
                    wFavoritePositions.remove(i);
                wordList.remove(i);
                notifyItemRemoved(i);
            }
        }
        db_adapter.close();
    }

    public void setSelectedFavorite(Set<Integer> selectedPositions) {
        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();

        for (int i : selectedPositions){
            if(wordList.get(i).getIsFavorite() == 1) {
                wFavoritePositions.remove(wFavoritePositions.size()-1);
                db_adapter.setFavorite(wordList.get(i).getId(), 0);
                wordList.get(i).setIsFavorite(0);
            }
            else{
                wFavoritePositions.add(wFavoritePositions.size());
                db_adapter.setFavorite(wordList.get(i).getId(), 1);
                wordList.get(i).setIsFavorite(1);
            }
        }
        db_adapter.close();
    }
}