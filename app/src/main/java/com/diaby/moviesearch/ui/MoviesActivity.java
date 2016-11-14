package com.diaby.moviesearch.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MMovie;

public class MoviesActivity extends AppCompatActivity implements MoviesFragment.onMovieClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        if(savedInstanceState == null) {
            MoviesFragment fragment = MoviesFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onMovieClicked(MMovie movie) {
        MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movie.getId());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, movieDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
