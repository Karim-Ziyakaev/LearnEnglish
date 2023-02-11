package com.example.LearnEnglish.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LearnEnglish.R;
import com.example.LearnEnglish.models.Word;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> implements MyItemTouchHelperCallback.ItemTouchHelperAdapter {

    private boolean showTranslation = true;
    private boolean showWord = true;
    private boolean showButtons = true;

    private Context context;
    private List<Word> wordList;
    private Set<Integer> wSelectedPositions;
    private LayoutInflater inflater;

    // Interface for handling clicks on items
//    public interface OnItemClickListener {
//        void onItemClick(Word word, int position);
//    }
//
//    private OnItemClickListener listener;
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }

    public WordAdapter(Context context, List<Word> wordList/*, OnItemClickListener onClickListener*/) {
//        this.listener = onClickListener;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.wordList = wordList;
        wSelectedPositions = new HashSet<>();
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            holder.changeImageView.setVisibility(View.VISIBLE);
            holder.deleteImageView.setVisibility(View.VISIBLE);
            holder.statisticsImageView.setVisibility(View.VISIBLE);
        } else {
            holder.changeImageView.setVisibility(View.GONE);
            holder.deleteImageView.setVisibility(View.GONE);
            holder.statisticsImageView.setVisibility(View.GONE);
        }

        if (wSelectedPositions.contains(position)) {
        // Элемент выделен
            currentWord.setIs_selected(true);
            holder.itemView.setBackgroundResource(R.drawable.border_move);
        } else {
        // Элемент не выделен
            currentWord.setIs_selected(false);
            holder.itemView.setBackgroundResource(R.drawable.border);
        }

        // обработка нажатия
//        holder.itemView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//                // вызываем метод слушателя, передавая ему данные
//                listener.onItemClick(currentWord, position);
//            }
//        });
    }

    public void toggleSelection(int position) {
        if (wSelectedPositions.contains(position)) {
            // Элемент уже выделен, поэтому убираем его из выделенных
            wSelectedPositions.remove(position);
        } else {
            // Элемент не выделен, поэтому добавляем его к выделенным
            wSelectedPositions.add(position);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        wSelectedPositions.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;
        TextView translationTextView;
        ImageView deleteImageView;
        ImageView changeImageView;
        ImageView statisticsImageView;

        public WordViewHolder(View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.word_text_view);
            translationTextView = itemView.findViewById(R.id.translation_text_view);
            deleteImageView = itemView.findViewById(R.id.delete_image_view);
            changeImageView = itemView.findViewById(R.id.change_image_view);
            statisticsImageView = itemView.findViewById(R.id.statistics_image_view);

            deleteImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // Remove the item from the list
                    int position = getAdapterPosition();
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
            });

            changeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
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

                                        wordList.set(position, new Word(word.getId(), s_word, s_translate));
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
            });

            statisticsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle statistics icon click
                }
            });

            // Устанавливаем слушатель нажатий на элемент
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Переключаем состояние выделения элемента
                    toggleSelection(getAdapterPosition());
                }
            });
        }
    }

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
//        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
//        db_adapter.open();
//        long temp = wordList.get(fromPosition).getId();
//        wordList.get(fromPosition).setId(wordList.get(toPosition).getId());
//        wordList.get(toPosition).setId(temp);
//        db_adapter.update(wordList.get(fromPosition));
//        db_adapter.update(wordList.get(toPosition));
//        db_adapter.close();
    }

    @Override
    public void onItemDismiss(int position) {
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


}