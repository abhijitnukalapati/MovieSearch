package com.diaby.moviesearch.util;

import android.content.Context;

/**
 * Created by abhijitnukalapati on 11/8/16.
 */

public class ConfigurationUtil {
    private static ConfigurationUtil sConfigurationUtil;
    private Context context;

    public static ConfigurationUtil getInstance(Context context) {
        if(sConfigurationUtil == null) {
            sConfigurationUtil  = new ConfigurationUtil(context);
        }
        return sConfigurationUtil;
    }

    private ConfigurationUtil(Context context) {
        this.context = context.getApplicationContext();
    }



}
