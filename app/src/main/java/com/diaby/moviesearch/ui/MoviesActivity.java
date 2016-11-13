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
import com.diaby.moviesearch.model.MMovie;
import com.diaby.moviesearch.util.AppUtils;
import com.diaby.moviesearch.util.MoviesLoader;
import com.diaby.moviesearch.util.SpacingDecoration;

import java.util.List;

public class MoviesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<MMovie>>{

    private RecyclerView vRecyclerView;
    private ProgressBar vProgressBar;
    private MoviesAdapter mMoviesAdapter;

    private static final String TAG = MoviesActivity.class.getSimpleName();
    private static final int MOVIES_LOADER_ID = 30;
    private static final String SEARCH_QUERY = "search_query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        vProgressBar = (ProgressBar) findViewById(R.id.movies_progress_bar);

        final int spanCount = getResources().getInteger(R.integer.movies_grid_span_count);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mMoviesAdapter.getItemViewType(position) == MoviesAdapter.ITEM_VIEW_TYPE_LOADER) {
                    return spanCount;
                } else {
                    return 1;
                }
            }
        });

        vRecyclerView.setLayoutManager(gridLayoutManager);
        vRecyclerView.setAdapter(mMoviesAdapter = new MoviesAdapter(this));
        vRecyclerView.addItemDecoration(new SpacingDecoration(R.dimen.grid_divider, this));

        vRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
              @Override
              public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                  final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                  final int currentItemCount = layoutManager.getItemCount();
                  final int visibleItemCount = recyclerView.getChildCount();
                  final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                  final int buffer = spanCount * 2;

                  Loader<List<MMovie>> loader = getLoaderManager().getLoader(MOVIES_LOADER_ID);
                  MoviesLoader moviesLoader = (MoviesLoader) loader;

                  // load more items if we reached the end of the list
                  if ((currentItemCount - visibleItemCount) <= (firstVisibleItemPosition + buffer) && !moviesLoader.isLoading()) {
                      vRecyclerView.post(new Runnable() {
                          @Override
                          public void run() {
                              mMoviesAdapter.setShowLoader(true);
                          }
                      });

                      moviesLoader.loadNextPage();
                  }
              }
        });

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

        AppUtils.hideSoftInput(this);

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
    public Loader<List<MMovie>> onCreateLoader(int i, Bundle bundle) {
        String query = bundle != null ? bundle.getString(SEARCH_QUERY, "") : "";
        return new MoviesLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<MMovie>> loader, final List<MMovie> movies) {
        if(vProgressBar.getVisibility() != View.GONE) {
            vProgressBar.setVisibility(View.GONE);
        }

        mMoviesAdapter.setShowLoader(false);
        mMoviesAdapter.updateData(movies);
    }

    @Override
    public void onLoaderReset(Loader<List<MMovie>> loader) {
    }
}
