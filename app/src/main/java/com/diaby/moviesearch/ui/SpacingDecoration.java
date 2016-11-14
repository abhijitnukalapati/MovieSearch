package com.diaby.moviesearch.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by abhijitnukalapati on 11/8/16.
 */

public class SpacingDecoration extends RecyclerView.ItemDecoration{
    private int spacing;

    public SpacingDecoration(@DimenRes int spacing, Context context) {
        this.spacing = context.getResources().getDimensionPixelOffset(spacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(spacing, spacing, spacing, spacing);
    }
}
