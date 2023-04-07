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
        void onFavoriteChanged();
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
        } else {
            wFavoritePositions.add(wFavoritePositions.size());
            db_adapter.setFavorite(word.getId(), 1);
        }
        if (selectionChangedListener != null) {
            selectionChangedListener.onFavoriteChanged();
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
        Collections.swap(wordList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Word word = wordList.get(position);
        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();
        //надо именно гетИд потому что айди в бд другое чем в листе
        db_adapter.delete(word.getId());
        db_adapter.close();
        wordList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundResource(R.drawable.border_move);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
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

    public void removeByIdx(int position)
    {
        // Get the current word and translation
        Word word = wordList.get(position);

        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();
        //надо именно гетИд потому что айди в бд другое чем в листе
        db_adapter.delete(word.getId());
        db_adapter.close();
        wordList.remove(position);
        // Notify the adapter that the data has changed
        notifyItemRemoved(position);
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
            }
            else{
                wFavoritePositions.add(wFavoritePositions.size());
                db_adapter.setFavorite(wordList.get(i).getId(), 1);
            }
        }

        db_adapter.close();
    }
}