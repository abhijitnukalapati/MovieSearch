package com.diaby.moviesearch.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;
import static com.diaby.moviesearch.ui.MoviesFragment.onMovieClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhijitnukalapati on 11/8/16.
 */

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    public static final int ITEM_VIEW_TYPE_MOVIE = 321;
    public static final int ITEM_VIEW_TYPE_LOADER = 322;

    private List<MMovie> movies = new ArrayList<>();
    private boolean mShowLoader; // flag to determine if more products are being loaded

    private Drawable placeHolder;
    private int posterWidth;
    private int spanCount;

    public MoviesAdapter(Context context) {
        placeHolder = ContextCompat.getDrawable(context, R.color.transparent);
        spanCount = context.getResources().getInteger(R.integer.movies_grid_span_count);

        int gridSpacing = context.getResources().getDimensionPixelOffset(R.dimen.grid_divider_space);
        posterWidth = context.getResources().getDisplayMetrics().widthPixels/spanCount - (gridSpacing * 2);
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

    public void bindMovieViewHolder(final MovieViewHolder holder, final int position) {
        final Context context = holder.imageView.getContext();
        final MMovie movie = movies.get(position);

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w185" + movie.getPosterPath())
                .placeholder(placeHolder)
                .crossFade(context.getResources().getInteger(R.integer.image_animation_duration))
                .fitCenter()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof onMovieClickListener) {
                    ((onMovieClickListener) context).onMovieClicked(movie);
                } else {
                    Log.w(TAG, "Context is not an instance of onMovieClickListener, ignoring poster click");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int pad = mShowLoader ? 1 : 0;
        return movies != null ? movies.size() + pad : 0;
    }

    public int getSpanCount(int position) {
        final int itemViewType = getItemViewType(position);

        if(itemViewType == ITEM_VIEW_TYPE_LOADER) {
            return spanCount;
        } else if (itemViewType == ITEM_VIEW_TYPE_MOVIE){
            return 1;
        } else {
            return 0;
        }
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
            if (mShowLoader) {
                notifyItemInserted(getItemCount());
            } else {
                notifyItemRemoved(getItemCount());
            }
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

    public void updateData(@NonNull List<MMovie> moreMovies){
        if(moreMovies.size() <= movies.size()) {
            movies = new ArrayList<>(moreMovies);
            notifyDataSetChanged();
        } else {
            int addedCount = moreMovies.size() - movies.size();
            movies.addAll(moreMovies.subList(movies.size(), moreMovies.size()));
            notifyItemRangeInserted(getItemCount(), addedCount);
        }
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
