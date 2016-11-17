package com.diaby.moviesearch.ui;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;
import com.diaby.moviesearch.util.AppUtils;
import com.diaby.moviesearch.loader.MoviesLoader;

import java.util.List;

import static com.diaby.moviesearch.loader.MoviesLoader.MOVIES_LOADER_ID;

/**
 * Created by abhijitnukalapati on 11/13/16.
 */

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MMovie>>{
    private RecyclerView vRecyclerView;
    private ProgressBar vProgressBar;
    private AutoCompleteTextView vSearchView;
    private TextView vEmptyView;
    private MoviesAdapter mMoviesAdapter;
    private String mSearchQuery;

    private static final String SEARCH_QUERY = "search_query";
    private static final String TAG = MoviesFragment.class.getSimpleName();

    public interface onMovieClickListener {
        void onMovieClicked(MMovie movie);
    }

    public static MoviesFragment newInstance() {
        return new MoviesFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_QUERY, mSearchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_list, container, false);
        vRecyclerView = (RecyclerView) root.findViewById(R.id.movies_recycler_view);
        vProgressBar = (ProgressBar) root.findViewById(R.id.movies_progress_bar);
        vSearchView = (AutoCompleteTextView) root.findViewById(R.id.movies_search_bar);
        vEmptyView = (TextView) root.findViewById(R.id.empty_view);

        if(savedInstanceState != null && TextUtils.isEmpty(savedInstanceState.getString(SEARCH_QUERY))) {
            vSearchView.setText(savedInstanceState.getString(SEARCH_QUERY));
        }

        setupSearchView();
        setupRecyclerView();

        return root;
    }

    @Override
    public Loader<List<MMovie>> onCreateLoader(int i, Bundle bundle) {
        String query = bundle != null ? bundle.getString(SEARCH_QUERY, "") : "";
        return new MoviesLoader(getActivity(), query);
    }

    @Override
    public void onLoadFinished(Loader<List<MMovie>> loader, List<MMovie> movies) {
        if(vProgressBar.getVisibility() != View.GONE) {
            vProgressBar.setVisibility(View.GONE);
        }

        mMoviesAdapter.setShowLoader(false);
        if(movies != null && movies.size() > 0) {
            vEmptyView.setVisibility(View.GONE);
            mMoviesAdapter.updateData(movies);
        } else {
            vEmptyView.setVisibility(View.VISIBLE);
            vEmptyView.setText(getString(R.string.movies_list_empty_results));
        }

    }

    @Override
    public void onLoaderReset(Loader<List<MMovie>> loader) {
        Log.d(TAG, "loader reset");
    }

    private void setupSearchView() {
        vSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean consumed = false;
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    consumed = true;

                    final String query = v.getText().toString().trim();
                    if(validateSearch(query)) {
                        mSearchQuery = query;
                        AppUtils.hideSoftInput(getActivity());
                        Log.d(TAG, String.format("Searching for: \"%1$s\"", query));

                        // TODO: save user's search queries

                        Bundle bundle = new Bundle();
                        bundle.putString(SEARCH_QUERY, query);
                        vProgressBar.setVisibility(View.VISIBLE);
                        getLoaderManager().restartLoader(MOVIES_LOADER_ID, bundle, MoviesFragment.this);
                    }
                }

                return consumed;
            }
        });
    }

    private void setupRecyclerView() {
        final int spanCount = getResources().getInteger(R.integer.movies_grid_span_count);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(mMoviesAdapter == null) {
                    return 0;
                } else {
                    return mMoviesAdapter.getSpanCount(position);
                }
            }
        });

        vRecyclerView.setLayoutManager(gridLayoutManager);
        vRecyclerView.setAdapter(mMoviesAdapter = new MoviesAdapter(getActivity()));
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

    private boolean validateSearch(String query){
        boolean isValidTerm = true;

        if(TextUtils.isEmpty(query)) {
            isValidTerm = false;

            float wiggleDistance = 10f;
            float[] wiggleValues = new float[]{
                    -wiggleDistance, wiggleDistance,
                    -wiggleDistance, wiggleDistance,
                    -wiggleDistance, wiggleDistance, 0
            };

            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, wiggleValues);
            ObjectAnimator.ofPropertyValuesHolder(vSearchView, pvhX)
                    .setDuration(400)
                    .start();
        }

        return isValidTerm;
    }
}
