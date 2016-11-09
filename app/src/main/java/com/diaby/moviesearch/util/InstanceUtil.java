package com.diaby.moviesearch.util;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

/**
 * Created by abhijitnukalapati on 11/7/16.
 */
public final class InstanceUtil {

    private static InstanceUtil sInstance;

    private OkHttpClient client;
    private Gson gson;

    // util class - prevent instantiation
    private InstanceUtil() {
    }

    public static InstanceUtil getInstance() {
        if(sInstance == null) {
            sInstance  = new InstanceUtil();
        }
        return sInstance;
    }

    public Gson getGson() {
        if(gson == null) {
            gson  = new Gson();
        }
        return gson;
    }

    public OkHttpClient getClient() {
        if(client == null) {
            client  = new OkHttpClient();
        }
        return client;
    }

}
