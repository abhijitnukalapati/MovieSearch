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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;
import com.diaby.moviesearch.util.FlexibleImageLoader;

import static com.diaby.moviesearch.ui.MoviesFragment.onMovieClickListener;
import static com.diaby.moviesearch.util.FlexibleImageLoader.IMAGE_TYPE.POSTER;

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
    private int posterHeight;
    private int spanCount;

    public MoviesAdapter(Context context) {
        placeHolder = ContextCompat.getDrawable(context, android.R.color.darker_gray);
        spanCount = context.getResources().getInteger(R.integer.movies_grid_span_count);

        int gridSpacing = context.getResources().getDimensionPixelOffset(R.dimen.grid_divider_space);
        int widthFactor = context.getResources().getInteger(R.integer.width_factor);
        posterWidth = (context.getResources().getDisplayMetrics().widthPixels/(widthFactor * spanCount)) - (gridSpacing * 2);
        posterHeight = (int) (posterWidth * 1.45f);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v;

        if(viewType == ITEM_VIEW_TYPE_LOADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loadmore_progress_bar, parent, false);
            return new LoaderViewHolder(v);
        } else if(viewType == ITEM_VIEW_TYPE_MOVIE){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_cell, parent, false);
            ImageView imageView = (ImageView) v.findViewById(R.id.movie_poster);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = posterHeight;
            params.width = posterWidth;
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

    private void bindMovieViewHolder(final MovieViewHolder holder, final int position) {
        final Context context = holder.moviePoster.getContext();
        final MMovie movie = movies.get(position);

        Glide.with(context)
                .using(new FlexibleImageLoader(context, POSTER))
                .load(movie.getPosterPath())
                .placeholder(placeHolder)
                .dontAnimate() // TODO: investigate flicker when animation is turned on
                .into(holder.moviePoster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof onMovieClickListener) {
                    ((onMovieClickListener) context).onMovieClicked(movie);
                } else {
                    Log.w(TAG, "Context is not an instance of onMovieClickListener, ignoring poster click");
                }
            }
        });

        holder.movieTitle.setText(movie.getTitle());
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
        private ImageView moviePoster;
        private TextView movieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
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
