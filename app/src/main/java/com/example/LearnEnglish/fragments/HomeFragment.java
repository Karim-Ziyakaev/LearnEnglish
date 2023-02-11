package com.example.LearnEnglish.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

public class HomeFragment extends Fragment {
    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;

    boolean showWord;
    boolean showTranslation;
    boolean showButtons;

    WordAdapter adapter;
    RecyclerView recyclerView ;
    private Context context;
    List<Word> list_words;

    private MenuItem menuItem;
    private SearchView searchView;

    public HomeFragment(Context context) {
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

        settings = getContext().getSharedPreferences("Show", Context.MODE_PRIVATE);

//        WordAdapter.OnItemClickListener itemClickListener = new WordAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Word word, int position) {
//                Intent intent = new Intent(context, UserActivity.class);
//                intent.putExtra("id", word.getId());
//                startActivity(intent);
//            }
//        };

        adapter = new WordAdapter(context, list_words/*, itemClickListener*/);
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
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu resource that contains the search button
        inflater.inflate(R.menu.actionbar_menu, menu);

        // Assign the menu item to the menuItem field
        menuItem = menu.findItem(R.id.item_search);
        searchView =(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setIconified(true);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter your data here.
                List<Word> filteredList = filterData(query);
                // Pass the filtered list to your adapter and call notifyDataSetChanged.
                adapter.setList(filteredList);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter your data here.
                List<Word> filteredList = filterData(newText);
                // Pass the filtered list to your adapter and call notifyDataSetChanged.
                adapter.setList(filteredList);
                return true;
            }
        });

        MenuItem item_btns_elements;
        MenuItem item_hide_word;
        MenuItem item_hide_translate;
        item_btns_elements = menu.findItem(R.id.item_btns_elements);
        item_hide_word = menu.findItem(R.id.item_hide_word);
        item_hide_translate = menu.findItem(R.id.item_hide_translate);

        if (!showButtons)
            item_btns_elements.setChecked(true);
        if (!showWord)
            item_hide_word.setChecked(true);
        if (!showTranslation)
            item_hide_translate.setChecked(true);

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
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                return true;
            case R.id.item_sort:
                if(item.isChecked()) {
                    adapter.setList(list_words);
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
                }
                return true;
            case R.id.item_hide_translate:
                // Toggle the boolean flag that determines whether or not to show the translation
                adapter.setShowTranslation(!adapter.getShowTranslation());
                // Update the view by calling notifyDataSetChanged() on the adapter
                adapter.notifyDataSetChanged();
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                return true;
            case R.id.item_hide_word:
                // Toggle the boolean flag that determines whether or not to show the word
                adapter.setShowWord(!adapter.getShowWord());
                // Update the view by calling notifyDataSetChanged() on the adapter
                adapter.notifyDataSetChanged();
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
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

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//
//        int numSelected = getNumSelectedItems();
//        if (numSelected > 0) {
//            // Switch to the "selected_menu" menu
//            getActivity().getMenuInflater().inflate(R.menu.selected_menu, menu);
//        } else {
//            // Switch to the "actionbar_menu" menu
//            getActivity().getMenuInflater().inflate(R.menu.actionbar_menu, menu);
//        }
//    }

    private int getNumSelectedItems() {
        // Replace this with a call to your adapter to get the number of selected items
        return adapter.getSelectedItemCount();
    }

}