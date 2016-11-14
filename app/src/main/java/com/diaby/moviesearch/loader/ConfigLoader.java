package com.diaby.moviesearch.loader;

import android.content.Context;
import android.content.AsyncTaskLoader;

import com.diaby.moviesearch.model.MConfiguration;
import com.diaby.moviesearch.util.ConfigurationUtil;

/**
 * Created by abhijitnukalapati on 11/14/16.
 */

public class ConfigLoader extends AsyncTaskLoader<MConfiguration> {

    public static final int CONFIG_LOADER_ID = 100;

    public ConfigLoader(Context context) {
        super(context);
    }

    @Override
    public MConfiguration loadInBackground() {
        return ConfigurationUtil.getInstance(getContext()).retrieveConfigFromApi();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
