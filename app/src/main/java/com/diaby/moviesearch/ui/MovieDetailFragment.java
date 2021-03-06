package com.diaby.moviesearch.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diaby.moviesearch.R;
import com.diaby.moviesearch.loader.MovieDetailLoader;
import com.diaby.moviesearch.model.MMovieDetail;
import com.diaby.moviesearch.util.FlexibleImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.diaby.moviesearch.loader.MovieDetailLoader.MOVIES_DETAIL_LOADER_ID;
import static com.diaby.moviesearch.util.FlexibleImageLoader.IMAGE_TYPE.BACKDROP;

/**
 * Created by abhijitnukalapati on 11/13/16.
 */

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<MMovieDetail>{

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    private CollapsingToolbarLayout vCollapsingToolbarLayout;
    private ImageView vBackdrop;
    private TextView vOverview;
    private TextView vRuntime;
    private TextView vReleaseDate;

    public static final String MOVIE_ID = "movie_id";

    public static MovieDetailFragment newInstance(int id) {
        MovieDetailFragment detailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID, id);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null) {
            getLoaderManager().initLoader(MOVIES_DETAIL_LOADER_ID, getArguments(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        vCollapsingToolbarLayout = (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar_layout);
        vBackdrop = (ImageView) root.findViewById(R.id.backdrop);
        vOverview = (TextView) root.findViewById(R.id.overview);
        vRuntime = (TextView) root.findViewById(R.id.runtime);
        vReleaseDate = (TextView) root.findViewById(R.id.release_date);

        return root;
    }

    @Override
    public Loader<MMovieDetail> onCreateLoader(int id, Bundle args) {
        return new MovieDetailLoader(getActivity(), args.getInt(MOVIE_ID));
    }

    public void updateData(int movieId) {
        if(isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_ID, movieId);
            getLoaderManager().restartLoader(MOVIES_DETAIL_LOADER_ID, bundle, this);
        }
    }


    @Override
    public void onLoadFinished(Loader<MMovieDetail> loader, MMovieDetail movieDetail) {
        Glide.with(this)
                .using(new FlexibleImageLoader(getActivity(), BACKDROP))
                .load(movieDetail.getBackdropPath())
                .fitCenter()
                .crossFade(getResources().getInteger(R.integer.image_animation_duration))
                .into(vBackdrop);

        vCollapsingToolbarLayout.setTitle(movieDetail.getTitle());

        vOverview.setText(movieDetail.getOverview());

        if(!TextUtils.isEmpty(movieDetail.getReleaseDate())) {
            String releaseDate = getString(R.string.release_date, getFormattedDate(movieDetail.getReleaseDate()));
            vReleaseDate.setText(formatLabel(releaseDate));
            vReleaseDate.setVisibility(View.VISIBLE);
        } else {
            vReleaseDate.setVisibility(View.GONE);
        }

        if(movieDetail.getRuntime() > 0) {
            String runTime = getString(R.string.runtime, getFormattedTime(movieDetail.getRuntime()));
            vRuntime.setText(formatLabel(runTime));
            vRuntime.setVisibility(View.VISIBLE);
        } else {
            vRuntime.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<MMovieDetail> loader) {

    }

    private String getFormattedDate(String dateString){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException pe) {
            Log.e(TAG, "Couldn't parse date. Returning the given string." +
                    "Expected format is yyyy-MM-dd. But string is " + dateString);
        }
        return dateString;
    }

    private String getFormattedTime(int runtime) {
        return String.format(Locale.getDefault(), "%1$dh %2$02dm", runtime/60, runtime % 60);
    }

    private SpannableString formatLabel(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.indexOf(':'), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
