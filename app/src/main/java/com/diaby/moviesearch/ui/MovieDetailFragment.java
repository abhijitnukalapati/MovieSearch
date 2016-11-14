package com.diaby.moviesearch.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;

/**
 * Created by abhijitnukalapati on 11/13/16.
 */

public class MovieDetailFragment extends Fragment {

    private CollapsingToolbarLayout vCollapsingToolbarLayout;
    private ImageView vBackdrop;

    private static final String MOVIE_DETAIL = "movie_detail";

    public static MovieDetailFragment newInstance(MMovie movie) {
        MovieDetailFragment detailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE_DETAIL, movie);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        vCollapsingToolbarLayout = (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar_layout);
        vBackdrop = (ImageView) root.findViewById(R.id.backdrop);

        MMovie movie = getArguments().getParcelable(MOVIE_DETAIL);
        Glide.with(this).load("https://image.tmdb.org/t/p/w780" + movie.getBackdropPath())
                .fitCenter()
                .crossFade(getResources().getInteger(R.integer.image_animation_duration))
                .into(vBackdrop);

        vCollapsingToolbarLayout.setTitle(movie.getTitle());

        return root;
    }



}
