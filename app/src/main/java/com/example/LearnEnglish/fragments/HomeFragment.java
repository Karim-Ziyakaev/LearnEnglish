package com.example.LearnEnglish.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


import com.example.LearnEnglish.R;
import com.example.LearnEnglish.activitys.UserActivity;
import com.example.LearnEnglish.adapters.DatabaseAdapter;
import com.example.LearnEnglish.adapters.MyItemTouchHelperCallback;
import com.example.LearnEnglish.adapters.WordAdapter;
import com.example.LearnEnglish.models.Word;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    //для actionBar
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();

        list_words = db_adapter.getWords();

        settings = requireContext().getSharedPreferences("Show", Context.MODE_PRIVATE);

        adapter = new WordAdapter(context, list_words);
        adapter.setOnSelectionChangedListener(this);
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

        db_adapter.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        FloatingActionButton fab_add = rootView.findViewById(R.id.btn_add);
        fab_add.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            requireActivity().finish();
            startActivity(intent);
        });

        return rootView;
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

    @SuppressLint("NonConstantResourceId")
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
                adapter.clearSelection();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.item_cancel:
                adapter.clearSelection();
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
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Чтобы при удалении элементы на вьюхе убирались
        DatabaseAdapter db_adapter = new DatabaseAdapter(context);
        db_adapter.open();
        list_words = db_adapter.getWords();
        adapter.setList(list_words);

        MenuItem itemDelete = menu.findItem(R.id.item_delete);
        MenuItem itemCancel = menu.findItem(R.id.item_cancel);

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

            itemSearch.setVisible(true);
            itemSort.setVisible(true);
            itemBtns.setVisible(true);
            itemWords.setVisible(true);
            itemTranslates.setVisible(true);

            requireActivity().setTitle("Home");
            requireActivity().invalidateOptionsMenu();
        }
    }

}