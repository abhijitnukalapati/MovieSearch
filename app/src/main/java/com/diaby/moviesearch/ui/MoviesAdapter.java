package com.diaby.moviesearch.ui;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;
import com.diaby.moviesearch.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhijitnukalapati on 11/8/16.
 */

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_VIEW_TYPE_MOVIE = 321;
    public static final int ITEM_VIEW_TYPE_LOADER = 322;

    private List<MMovie> movies = new ArrayList<>();
    private boolean mShowLoader; // flag to determine if more products are being loaded

    private Drawable placeHolder;
    private int posterWidth;

    public MoviesAdapter(Context context) {
        Point point = AppUtils.getScreenSize(context);
        posterWidth = point.x/context.getResources().getInteger(R.integer.movies_grid_span_count);
        placeHolder = ContextCompat.getDrawable(context, R.color.transparent);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v;

        if(viewType == ITEM_VIEW_TYPE_LOADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loadmore_progress_bar, parent, false);
            return new LoaderViewHolder(v);
        } else if(viewType == ITEM_VIEW_TYPE_MOVIE){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_cell, parent, false);
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.width = posterWidth;
            v.setLayoutParams(layoutParams);
            return new MovieViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_VIEW_TYPE_MOVIE) {
            bindMovieViewHolder((MovieViewHolder) holder, position);
        }
    }

    public void bindMovieViewHolder(MovieViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load("https://image.tmdb.org/t/p/w185" + movies.get(position).getPosterPath())
                .placeholder(placeHolder)
                .fitCenter()
                .crossFade(600)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        int pad = mShowLoader ? 1 : 0;

        return movies != null ? movies.size() + pad : 0;
    }

    /**
     * Toggles the visibility of the loader
     * based on the supplied flag
     *
     * @param shouldShow a boolean
     */
    public void setShowLoader(boolean shouldShow) {
        if(mShowLoader != shouldShow) {
            mShowLoader = shouldShow;
            notifyItemInserted(getItemCount());
        }
    }

    public boolean isLoaderShowing() {
        return mShowLoader;
    }

    @Override
    public int getItemViewType(int position) {
        // if we reached the end, display the loader
        if(mShowLoader && position == movies.size()) {
            return ITEM_VIEW_TYPE_LOADER;
        } else {
            return ITEM_VIEW_TYPE_MOVIE;
        }
    }

    /**
     * Adds the given product items to the adapter's
     * list data and notifies the adapter of the
     * change
     *
     * @param moreMovies a list of movies
     */
    public void addMoreItems(@NonNull List<MMovie> moreMovies) {
        movies.addAll(moreMovies);
        notifyItemRangeInserted(getItemCount(), moreMovies.size());
    }

    public void resetData(@NonNull List<MMovie> moreMovies){
        movies.clear();
        movies = moreMovies;
        notifyDataSetChanged();
    }

    public void clearData() {
        movies.clear();
        notifyDataSetChanged();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }

    public static class LoaderViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView;
        }
    }

}
