package com.diaby.moviesearch.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;
import com.diaby.moviesearch.util.AppUtils;
import com.diaby.moviesearch.util.MoviesAdapter;
import com.diaby.moviesearch.util.MoviesLoader;

import java.util.List;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MMovie>>{

    private RecyclerView vRecyclerView;
    private ProgressBar vProgressBar;
    private AutoCompleteTextView vSearchView;
    private MoviesAdapter mMoviesAdapter;

    private static final String TAG = MoviesActivity.class.getSimpleName();
    private static final int MOVIES_LOADER_ID = 30;
    private static final String SEARCH_QUERY = "search_query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        vRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        vProgressBar = (ProgressBar) findViewById(R.id.movies_progress_bar);
        vSearchView = (AutoCompleteTextView) findViewById(R.id.movies_search_bar);

        setupSearchView();

        setupRecyclerView();

        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
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

    private void setupSearchView() {
        vSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean consumed = false;
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    consumed = true;

                    // TODO: validate query
                    final String query = v.getText().toString().trim();
                    AppUtils.hideSoftInput(MoviesActivity.this);

                    Log.d(TAG, String.format("Searching for: \"%1$s\"", query));

                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_QUERY, query);

                    vProgressBar.setVisibility(View.VISIBLE);
                    getLoaderManager().restartLoader(MOVIES_LOADER_ID, bundle, MoviesActivity.this);
                }

                return consumed;
            }
        });
    }

    private void setupRecyclerView() {
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
        vRecyclerView.addOnScrollListener(new PaginationHelper(new PaginationHelper.PaginationCallback() {
            @Override
            public void onLoadMore(RecyclerView recyclerView) {
                Loader<List<MMovie>> loader = getLoaderManager().getLoader(MOVIES_LOADER_ID);
                MoviesLoader moviesLoader = (MoviesLoader) loader;
                moviesLoader.loadNextPage();

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mMoviesAdapter.setShowLoader(true);
                    }
                });
            }

            @Override
            public boolean shouldLoadMore() {
                Loader<List<MMovie>> loader = getLoaderManager().getLoader(MOVIES_LOADER_ID);
                MoviesLoader moviesLoader = (MoviesLoader) loader;
                return !moviesLoader.isLoading() && moviesLoader.doesHaveMorePages();
            }
        }));
    }
}
