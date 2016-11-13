package com.diaby.moviesearch.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.diaby.moviesearch.R;

public class MoviesActivity extends AppCompatActivity {

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

}
