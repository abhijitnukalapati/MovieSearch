package com.diaby.moviesearch.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by abhijitnukalapati on 11/8/16.
 */

public class AppUtils {

    /**
     * Hides the software input method if visible (most commonly the keyboard).
     * @param activity the current top-level activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
