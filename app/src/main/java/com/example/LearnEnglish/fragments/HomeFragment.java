package com.example.LearnEnglish.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.LearnEnglish.R;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.adapters.MyItemTouchHelperCallback;
import com.example.LearnEnglish.adapters.WordAdapter;
import com.example.LearnEnglish.models.Word;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment implements WordAdapter.OnSelectionChangedListener {
    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;

    boolean showWord;
    boolean showTranslation;
    boolean showButtons;
    boolean sortAZ = false;

    WordAdapter adapter;
    RecyclerView recyclerView;
    private Context context;
    List<Word> list_words;

    Set<Integer> selectedPositions;
    private OnWordsChangeListener mListener;


    public interface OnWordsChangeListener {
        void onWordsChanged(ArrayList<Word> words);
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            mListener = (OnWordsChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnWordsChangeListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("words")) {
            list_words = getArguments().getParcelableArrayList("words");
        }
        adapter = new WordAdapter(requireContext(), list_words);
        adapter.setOnSelectionChangedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        settings = requireContext().getSharedPreferences("Show", Context.MODE_PRIVATE);

        recyclerView.setAdapter(adapter);

        showWord = settings.getBoolean("showWord", true);
        showTranslation = settings.getBoolean("showTranslation", true);
        showButtons = settings.getBoolean("showButtons", true);
        adapter.setShowWord(showWord);
        adapter.setShowTranslation(showTranslation);
        adapter.setShowButtons(showButtons);
        adapter.notifyDataSetChanged();

        MyItemTouchHelperCallback callback = new MyItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab_add = rootView.findViewById(R.id.btn_add);
        fab_add.setOnClickListener(view -> {
            addDialog();
        });

        return rootView;
    }

    private void addDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_word_dialog, null);
        final EditText wordEditText = dialogView.findViewById(R.id.word_edit_text);
        final EditText translationEditText = dialogView.findViewById(R.id.translation_edit_text);
        final Button addButton = dialogView.findViewById(R.id.add_button);
        final Button randomButton = dialogView.findViewById(R.id.random_button);
        final TextView successText = dialogView.findViewById(R.id.success_text);
        builder.setView(dialogView)
sa                .setPositiveButton("Close", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
                        db_adapter.open();
                        list_words = db_adapter.getWords();
                        db_adapter.close();
                        adapter.setList(list_words);
                    }
                })
                .create()
                .show();
        addButton.setOnClickListener(view -> {
            String s_word = wordEditText.getText().toString().trim();
            String s_translate = translationEditText.getText().toString().trim();

            if (s_word.isEmpty() || s_translate.isEmpty()) {
                successText.setVisibility(View.INVISIBLE);
                if (s_word.isEmpty() && s_translate.isEmpty()) {
                    wordEditText.setError("Error, empty word");
                    translationEditText.setError("Error, empty translation");
                }
                else if (s_word.isEmpty())
                    wordEditText.setError("Error, empty translation");
                else
                    translationEditText.setError("Error, empty translation");
            } else {
                s_word = s_word.substring(0, 1).toUpperCase() + s_word.substring(1).toLowerCase();
                s_translate = s_translate.substring(0, 1).toUpperCase() + s_translate.substring(1).toLowerCase();
                DatabaseAdapter db_adapter = new DatabaseAdapter(context);
                list_words.add(new Word(-1, s_word, s_translate, 0));
                db_adapter.open();
                db_adapter.insert(list_words.get(list_words.size()-1));
                db_adapter.close();
                adapter.setList(list_words);
                wordEditText.setText("");
                translationEditText.setText("");
                successText.setVisibility(View.VISIBLE);

            }
        });
        randomButton.setOnClickListener(view -> {
            DatabaseAdapter db_adapter = new DatabaseAdapter(context);
            db_adapter.open();
            Word word = db_adapter.getRandomWord();
            db_adapter.close();
            wordEditText.setText(word.getWord());
            translationEditText.setText(word.getTranslate());
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu resource that contains the search button
        inflater.inflate(R.menu.actionbar_menu, menu);

        // Assign the menu item to the menuItem field
        MenuItem menuItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setIconified(true);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Word> filteredList = filterData(query);
                adapter.setList(filteredList);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Word> filteredList = filterData(newText);
                adapter.setList(filteredList);
                return true;
            }
        });

        // Call the super method
        super.onCreateOptionsMenu(menu, inflater);
    }


    private List<Word> filterData(String searchQuery) {
        List<Word> filteredList = new ArrayList<>();
        searchQuery = searchQuery.toLowerCase();
        for (Word word : list_words) {
            if (word.getWord().toLowerCase().contains(searchQuery)) {
                filteredList.add(word);
            } else if (word.getTranslate().toLowerCase().contains(searchQuery))
            {
                filteredList.add(word);
            }
        }
        return filteredList;
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.item_search:
                return true;
            case R.id.item_btns_elements:
                // Toggle the boolean flag that determines whether or not to show the buttons
                adapter.setShowButtons(!adapter.getShowButtons());
                // Update the view by calling notifyDataSetChanged() on the adapter
                adapter.notifyDataSetChanged();
                item.setChecked(!item.isChecked());
                return true;
            case R.id.item_sort:
                if(item.isChecked()) {
                    adapter.setList(list_words);
                    sortAZ = false;
                    item.setChecked(false);
                }
                else {
                    ArrayList<Word> sorted_list_words = new ArrayList<>(list_words);
                    Collections.sort(sorted_list_words, new Comparator<Word>() {
                        @Override
                        public int compare(Word w1, Word w2) {
                            return w1.getWord().compareTo(w2.getWord());
                        }
                    });
                    adapter.setList(sorted_list_words);
                    item.setChecked(true);
                    sortAZ = true;
                }
                return true;
            case R.id.item_hide_translate:
                // Toggle the boolean flag that determines whether or not to show the translation
                adapter.setShowTranslation(!adapter.getShowTranslation());
                // Update the view by calling notifyDataSetChanged() on the adapter
                adapter.notifyDataSetChanged();
                item.setChecked(!item.isChecked());
                return true;
            case R.id.item_hide_word:
                // Toggle the boolean flag that determines whether or not to show the word
                adapter.setShowWord(!adapter.getShowWord());
                // Update the view by calling notifyDataSetChanged() on the adapter
                adapter.notifyDataSetChanged();
                item.setChecked(!item.isChecked());
                return true;
            case R.id.item_delete:
                adapter.removeSelected(selectedPositions);
                List<Integer> selectedPositionsList = new ArrayList<>(selectedPositions);
                Collections.sort(selectedPositionsList, Collections.reverseOrder());
                for(int i: selectedPositionsList){
                    list_words.remove(i);
                }
                mListener.onWordsChanged((ArrayList<Word>) list_words);
                adapter.clearSelections();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.item_cancel:
                adapter.clearSelections();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.item_favorite:
                adapter.setSelectedFavorite(selectedPositions);
                refreshList();
                adapter.setList(list_words);
                mListener.onWordsChanged((ArrayList<Word>) list_words);
                adapter.clearSelections();
                requireActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        showWord = adapter.getShowWord();
        showTranslation = adapter.getShowTranslation();
        showButtons = adapter.getShowButtons();
        prefEditor = settings.edit();
        prefEditor.putBoolean("showWord", showWord);
        prefEditor.putBoolean("showTranslation", showTranslation);
        prefEditor.putBoolean("showButtons", showButtons);
        prefEditor.apply();
    }

    @Override
    public void onSelectionChanged(Set<Integer> selectedPositions) {
        // Handle selection state changes
        this.selectedPositions = selectedPositions;
        requireActivity().invalidateOptionsMenu();
    }

    @Override
    public void onFavoriteChanged(int position){
        Word word = list_words.remove(position);
        boolean isMoved = false;
        int j = position;
        if(word.getIsFavorite() == 1) {
            if (j == list_words.size())
                j--;
            while (!isMoved) {
                isMoved = true;
                if (j >= 0 && (word.getId() < list_words.get(j).getId() || list_words.get(j).getIsFavorite() == 0)) {
                    j--;
                    isMoved = false;
                } else {
                    if (j == -1)
                        list_words.add(0, word);
                    else
                        list_words.add(j+1, word);
                }
            }
        }
        else {
            while (!isMoved) {
                isMoved = true;
                if (j < list_words.size() && (list_words.get(j).getIsFavorite() == 1 || list_words.get(j).getId()<word.getId())){
                    j++;
                    isMoved = false;
                }
                else{
                    if(j == list_words.size())
                        list_words.add(word);
                    else
                        list_words.add(j, word);
                }
            }
        }
        adapter.setList(list_words);
        mListener.onWordsChanged((ArrayList<Word>) list_words);
    }

    @Override
    public void onWordChangedPosition(int newPos) {
        refreshList();
        adapter.setList(list_words);
        mListener.onWordsChanged((ArrayList<Word>) list_words);
    }

    @Override
    public void onWordDeleted() {
        mListener.onWordsChanged((ArrayList<Word>) list_words);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem itemDelete = menu.findItem(R.id.item_delete);
        MenuItem itemCancel = menu.findItem(R.id.item_cancel);
        MenuItem itemFavorite = menu.findItem(R.id.item_favorite);

        MenuItem itemSearch = menu.findItem(R.id.item_search);
        MenuItem itemSort = menu.findItem(R.id.item_sort);
        MenuItem itemBtns = menu.findItem(R.id.item_btns_elements);
        MenuItem itemWords = menu.findItem(R.id.item_hide_word);
        MenuItem itemTranslates = menu.findItem(R.id.item_hide_translate);

        itemWords.setChecked(!adapter.getShowWord());
        itemBtns.setChecked(!adapter.getShowButtons());
        itemTranslates.setChecked(!adapter.getShowTranslation());
        itemSort.setChecked(sortAZ);

        //грязный костыль для сорт, можно сделать потом проверку на изменения
        if(sortAZ)
        {
            ArrayList<Word> sorted_list_words = new ArrayList<>(list_words);
            Collections.sort(sorted_list_words, new Comparator<Word>() {
                @Override
                public int compare(Word w1, Word w2) {
                    return w1.getWord().compareTo(w2.getWord());
                }
            });
            adapter.setList(sorted_list_words);
        }


        if (selectedPositions!=null && !selectedPositions.isEmpty()) {
            itemDelete.setVisible(true);
            itemCancel.setVisible(true);
            itemFavorite.setVisible(true);

            itemSearch.setVisible(false);
            itemSort.setVisible(false);
            itemBtns.setVisible(false);
            itemWords.setVisible(false);
            itemTranslates.setVisible(false);

            requireActivity().setTitle(String.format("Selected: %d", adapter.getSelectedItemCount()));
            requireActivity().invalidateOptionsMenu();
        } else {
            itemDelete.setVisible(false);
            itemCancel.setVisible(false);
            itemFavorite.setVisible(false);

            itemSearch.setVisible(true);
            itemSort.setVisible(true);
            itemBtns.setVisible(true);
            itemWords.setVisible(true);
            itemTranslates.setVisible(true);

            requireActivity().setTitle("Home");
            requireActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.onWordsChanged((ArrayList<Word>) list_words);
    }

    public void refreshList(){
        ArrayList<Word> temp = new ArrayList<>(list_words);
        for(int i = 0; i < list_words.size(); i++){
            boolean isMoved = false;
            Word word = temp.remove(i);
            int j = i;
            if(word.getIsFavorite() == 1){
                while(!isMoved){
                    isMoved = true;
                    if(j!=0){
                        if(word.getId() < temp.get(j-1).getId() || temp.get(j-1).getIsFavorite() == 0) {
                            j--;
                            isMoved = false;
                        } else {
                            temp.add(j, word);
                        }
                    }else {
                        temp.add(j, word);
                    }
                }
            } else {
                while (!isMoved) {
                    isMoved = true;
                    if (j!=0 && word.getId() < temp.get(j-1).getId() && temp.get(j-1).getIsFavorite() == 0) {
                        j--;
                        isMoved = false;
                    } else if (j == 0 || word.getId() > temp.get(j-1).getId() || temp.get(j-1).getIsFavorite() == 1) {
                        temp.add(j, word);
                    }
                }
            }
        }
        list_words = temp;
    }
}