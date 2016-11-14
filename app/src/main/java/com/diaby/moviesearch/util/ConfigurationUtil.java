package com.diaby.moviesearch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.diaby.moviesearch.R;
import com.diaby.moviesearch.model.MConfiguration;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abhijitnukalapati on 11/8/16.
 */

public class ConfigurationUtil {
    private static final String APP_PREFS = "com.diaby.moviesearch.APP_PREFS";
    private static final String CONFIGURATION_MODEL = "com.diaby.moviesearch.CONFIGURATION_MODEL";
    private static final String TIME_STAMP = "com.diaby.moviesearch.TIME_STAMP";

    /**
     * 5 days -- need to re-request every few days according to docs
     */
    private static final long CONFIGURATION_VALIDITY = 5 * 24 * 60 * 60 * 1000;

    private static ConfigurationUtil sConfigurationUtil;
    private Context context;
    private MConfiguration configuration;

    public static ConfigurationUtil getInstance(Context context) {
        if(sConfigurationUtil == null) {
            sConfigurationUtil  = new ConfigurationUtil(context);
        }
        return sConfigurationUtil;
    }

    private ConfigurationUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    public MConfiguration retrieveConfigFromApi(){
        String url = context.getString(R.string.configuration_url);
        String apiKey = context.getString(R.string.api_key);
        InstanceUtil instanceUtil = InstanceUtil.getInstance();

        Uri uri = Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("api_key", apiKey)
                .build();
        try {
            Request configRequest = new Request.Builder().url(uri.toString()).build();
            Response response = instanceUtil.getClient().newCall(configRequest).execute();
            configuration = instanceUtil.getGson().fromJson(response.body().string(), MConfiguration.class);
            return configuration;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveConfigToDisk(MConfiguration configuration) {
        SharedPreferences sharedPref = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CONFIGURATION_MODEL, InstanceUtil.getInstance().getGson().toJson(configuration));
        editor.putLong(TIME_STAMP, System.currentTimeMillis());
        editor.apply();
    }

    @Nullable
    public MConfiguration retrieveConfig() {
        if(configuration != null) {
            return configuration;
        } else {
            SharedPreferences sharedPref = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
            String configurationModelString = sharedPref.getString(CONFIGURATION_MODEL, null);
            if(configurationModelString != null) {
                configuration = InstanceUtil.getInstance().getGson().fromJson(configurationModelString, MConfiguration.class);
                return configuration;
            }
        }

        return null;
    }

    public boolean hasConfig() {
        SharedPreferences sharedPref = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        String configurationModelString = sharedPref.getString(CONFIGURATION_MODEL, null);

        boolean isValid = System.currentTimeMillis() - sharedPref.getLong(TIME_STAMP, 0) < CONFIGURATION_VALIDITY;
        return configurationModelString != null && isValid;
    }

}
