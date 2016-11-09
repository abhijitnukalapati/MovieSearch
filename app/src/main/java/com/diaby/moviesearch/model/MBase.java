package com.diaby.moviesearch.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by abhijitnukalapati on 11/7/16.
 */

public class MBase {
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
