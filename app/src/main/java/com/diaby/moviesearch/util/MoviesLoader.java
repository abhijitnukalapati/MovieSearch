package com.diaby.moviesearch.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovieSearch;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abhijitnukalapati on 11/9/16.
 */

public class MoviesLoader extends AsyncTaskLoader<MMovieSearch> {

    private static final String TAG = MoviesLoader.class.getSimpleName();
    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie";
    private String mUrl;
    private String mQuery;

    public MoviesLoader(Context context, String query) {
        super(context);
        mQuery = query;

        String apiKey = context.getResources().getString(R.string.api_key);
        mUrl = Uri.parse(SEARCH_URL).buildUpon()
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("query", query)
                .toString();
    }

    @Override
    protected void onStartLoading() {
        if(!TextUtils.isEmpty(mQuery)) {
            forceLoad();
        }
    }

    @Override
    public MMovieSearch loadInBackground() {
        Request searchRequest = new Request.Builder()
                .url(mUrl)
                .build();

        InstanceUtil instanceUtil = InstanceUtil.getInstance();
        try {
            Response response = instanceUtil.getClient().newCall(searchRequest).execute();
            return instanceUtil.getGson().fromJson(response.body().string(), MMovieSearch.class);
        } catch (IOException e) {
            Log.d(TAG, "Search Request failed: " + e.getMessage());
        }

        return  null;
    }
}
