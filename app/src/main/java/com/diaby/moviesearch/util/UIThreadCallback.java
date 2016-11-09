package com.diaby.moviesearch.util;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 *
 * Callback for use with OkHttp which will return the response
 * on on the main thread.
 */
public abstract class UIThreadCallback implements Callback {

    public abstract void onSuccess(String response, int statusCode);

    public abstract void onFailure(String response, int statusCode);

    public abstract void onException(IOException exception);

    private Handler mUIThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public final void onFailure(Call call, final IOException exception) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                onException(exception);
            }
        });
    }

    @Override
    public final void onResponse(final Call call, final Response response) {
        try {
            final String responseString = response.body().string();
            final int statusCode = response.code();
            final boolean isSuccessful = response.isSuccessful();

            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isSuccessful) {
                        onSuccess(responseString, statusCode);
                    } else {
                        onFailure(responseString, statusCode);
                    }
                }
            });
        } catch (IOException exception) {
            exception.printStackTrace();
            onException(exception);
        }
    }


}