package com.diaby.moviesearch.loader;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;
import com.diaby.moviesearch.model.MMovieSearch;
import com.diaby.moviesearch.util.InstanceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abhijitnukalapati on 11/9/16.
 */

public class MoviesLoader extends AsyncTaskLoader<List<MMovie>> {
    private static final String TAG = MoviesLoader.class.getSimpleName();
    public static final int MOVIES_LOADER_ID = 30;

    private List<MMovie> mMovies;
    private Uri.Builder mUri;
    private String mQuery;
    private int mCurrentPage = 1;
    private int mTotalPages;
    private boolean mIsLoading;

    public MoviesLoader(Context context, String query) {
        super(context);
        mQuery = query;
        String url = context.getResources().getString(R.string.search_url);

        String apiKey = context.getResources().getString(R.string.api_key);
        mUri = Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("query", query);
    }

    @Override
    protected void onStartLoading() {
        if (mMovies != null) {
            deliverResult(mMovies);
        }

        if ((takeContentChanged() || mMovies == null) && !TextUtils.isEmpty(mQuery)) {
            forceLoad();
        }
    }

    @Override
    public List<MMovie> loadInBackground() {
        mIsLoading = true;

        String url = mUri.appendQueryParameter("page", String.valueOf(mCurrentPage)).toString();
        Request searchRequest = new Request.Builder()
                .url(url)
                .build();

        InstanceUtil instanceUtil = InstanceUtil.getInstance();
        try {
            Response response = instanceUtil.getClient().newCall(searchRequest).execute();

            MMovieSearch movieSearch = instanceUtil.getGson().fromJson(response.body().string(), MMovieSearch.class);
            mCurrentPage = movieSearch.getPage();
            mTotalPages = movieSearch.getTotalPages();

            return filterResults(movieSearch.getResults());
        } catch (IOException e) {
            Log.d(TAG, "Search Request failed: " + e.getMessage());
        }

        return  null;

    }

    public void loadNextPage() {
        if(++mCurrentPage <= mTotalPages) {
            onContentChanged();
        }
    }

    public boolean doesHaveMorePages() {
        return mTotalPages > mCurrentPage;
    }

    @Override
    public void deliverResult(List<MMovie> movies) {
        mIsLoading = false;
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }

        if(mMovies == null) {
            mMovies = new ArrayList<>(movies);
        } else {
            mMovies.addAll(movies);
        }
        if (isStarted()) {
            super.deliverResult(new ArrayList<>(mMovies));
        }
    }

    /**
     * Filter the movie results by removing
     * instances where a poster path doesn't
     * exist (null or empty)
     */
    private List<MMovie> filterResults(List<MMovie> results) {
        Iterator<MMovie> iterator = results.iterator();
        while(iterator.hasNext()) {
            MMovie movie = iterator.next();
            if(TextUtils.isEmpty(movie.getPosterPath())) {
                iterator.remove();
            }
        }

        return results;
    }

    public boolean isLoading() {
        return mIsLoading;
    }
}
