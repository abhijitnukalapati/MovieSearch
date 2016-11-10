package com.diaby.moviesearch.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovieSearch;
import com.diaby.moviesearch.util.MoviesLoader;
import com.diaby.moviesearch.util.SpacingDecoration;

public class MoviesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<MMovieSearch>{

    private RecyclerView vRecyclerView;
    private ProgressBar vProgressBar;

    private MoviesAdapter mMoviesAdapter;

    private static final String TAG = MoviesActivity.class.getSimpleName();
    private static final int MOVIES_LOADER_ID = 30;
    private static final String SEARCH_QUERY = "search_query";
    private static final String CONFIG_URL = "https://api.themoviedb.org/3/configuration?api_key=7fcb31f20b33a399c758eeac37a16610";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        vProgressBar = (ProgressBar) findViewById(R.id.movies_progress_bar);

        vRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.movies_grid_span_count)));
        vRecyclerView.setAdapter(mMoviesAdapter = new MoviesAdapter(this));
        vRecyclerView.addItemDecoration(new SpacingDecoration(R.dimen.grid_divider, this));

        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // setup search view
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.movie_search_hint));
        searchView.setOnQueryTextListener(this);

        // TODO: save recent searches and display them

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, String.format("Searching for: \"%1$s\"",query));

        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_QUERY, query);

        vProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(MOVIES_LOADER_ID, bundle, this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public Loader<MMovieSearch> onCreateLoader(int i, Bundle bundle) {
        String query = bundle != null ? bundle.getString(SEARCH_QUERY, "") : "";
        return new MoviesLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<MMovieSearch> loader, MMovieSearch movieSearch) {
        vProgressBar.setVisibility(View.GONE);

        movieSearch.filterResults();
        mMoviesAdapter.resetData(movieSearch.getResults());
    }

    @Override
    public void onLoaderReset(Loader<MMovieSearch> loader) {
        mMoviesAdapter.clearData();
    }
}
