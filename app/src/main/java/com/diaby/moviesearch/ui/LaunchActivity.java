package com.diaby.moviesearch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.loader.ConfigLoader;
import com.diaby.moviesearch.model.MConfiguration;
import com.diaby.moviesearch.util.ConfigurationUtil;

import static com.diaby.moviesearch.loader.ConfigLoader.CONFIG_LOADER_ID;
/**
 * Created by abhijitnukalapati on 11/14/16.
 */
public class LaunchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MConfiguration>{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        if(!ConfigurationUtil.getInstance(this).hasConfig()) {
            getLoaderManager().initLoader(CONFIG_LOADER_ID, null, this);
        } else {
            startMoviesActivity();
        }
    }

    @Override
    public Loader<MConfiguration> onCreateLoader(int id, Bundle args) {
        return new ConfigLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<MConfiguration> loader, MConfiguration data) {
        ConfigurationUtil.getInstance(this).saveConfigToDisk(data);
        startMoviesActivity();
    }

    @Override
    public void onLoaderReset(Loader<MConfiguration> loader) {

    }

    private void startMoviesActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MoviesActivity.class);
        startActivity(intent);
    }
}
