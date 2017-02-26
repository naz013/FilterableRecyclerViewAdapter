package com.github.naz013.filterablerecyclerview;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.naz013.filterablerecycleradapter.FilterableAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String[] data = new String[]{"Apple", "Samsung", "LG", "Google", "Microsoft", "Xiaomi", "Lenovo"};
    private Random random = new Random();

    private SearchView mSearchView = null;
    private MenuItem mSearchMenu = null;

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (mAdapter != null) mAdapter.filter(query);
            if (mSearchMenu != null) {
                mSearchMenu.collapseActionView();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (mAdapter != null) mAdapter.filter(newText);
            return false;
        }
    };
    private SearchView.OnCloseListener mSearchCloseListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            return false;
        }
    };

    private RecyclerView recyclerView;
    private SimpleAdapter mAdapter;
    private FilterableAdapter.Filter<Model, String> filter = new FilterableAdapter.Filter<Model, String>() {
        @Override
        public boolean filter(Model model, String query) {
            return model.getTitle().toLowerCase().contains(query.toLowerCase());
        }
    };
    private FilterableAdapter.FilterCallback<Model, String> mFilterCallback = new FilterableAdapter.FilterCallback<Model, String>() {
        @Override
        public void onFilter(List<Model> list, String query) {
            recyclerView.smoothScrollToPosition(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SimpleAdapter(this, getTestData(), filter);
        mAdapter.setFilterCallback(mFilterCallback);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mSearchMenu = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (mSearchMenu != null) {
            mSearchView = (SearchView) mSearchMenu.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(queryTextListener);
            mSearchView.setOnCloseListener(mSearchCloseListener);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addRandomItem();
                break;
            case R.id.action_delete:
                deleteRandomItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRandomItem() {
        if (mAdapter.getItemCount() == 0) return;
        int position = random.nextInt(mAdapter.getItemCount());
        mAdapter.removeItem(position);
    }

    private void addRandomItem() {
        int i = random.nextInt(5);
        if (i % 3 == 0) {
            mAdapter.addItem(getNextItem(mAdapter.getOriginalItemCount()));
        } else {
            int position = random.nextInt(mAdapter.getOriginalItemCount()) + 1;
            mAdapter.addItem(position, getNextItem(position));
        }
    }

    private List<Model> getTestData() {
        List<Model> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(getNextItem(i));
        }
        return list;
    }

    private Model getNextItem(int position) {
        return new Model(position + ". " + data[random.nextInt(data.length)] + " + " + data[random.nextInt(data.length)]);
    }
}
