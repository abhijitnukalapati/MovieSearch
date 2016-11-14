package com.diaby.moviesearch.loader;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovieDetail;
import com.diaby.moviesearch.util.InstanceUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abhijitnukalapati on 11/13/16.
 */

public class MovieDetailLoader extends AsyncTaskLoader<MMovieDetail> {
    private static final String TAG = MovieDetailLoader.class.getSimpleName();
    public static final int MOVIES_DETAIL_LOADER_ID = 90;

    private Uri.Builder mUri;

    public MovieDetailLoader(Context context, int movieId) {
        super(context);

        String apiKey = context.getResources().getString(R.string.api_key);
        String url = context.getResources().getString(R.string.movie_detail_url);
        mUri = Uri.parse(String.format(Locale.getDefault(), url, movieId))
                .buildUpon()
                .appendQueryParameter("api_key", apiKey);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public MMovieDetail loadInBackground() {
        InstanceUtil instanceUtil = InstanceUtil.getInstance();
        try {
            Request searchRequest = new Request.Builder()
                    .url(mUri.toString())
                    .build();

            Response response = instanceUtil.getClient().newCall(searchRequest).execute();
            MMovieDetail movieDetail = instanceUtil.getGson().fromJson(response.body().string(), MMovieDetail.class);
            return movieDetail;
        } catch (IOException e) {
            Log.d(TAG, "Movie Detail Request failed: " + e.getMessage());
        }

        return null;
    }

}
