package com.diaby.moviesearch.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.diaby.moviesearch.R;

/**
 * Created by abhijitnukalapati on 11/8/16.
 */

public class SpacingDecoration extends RecyclerView.ItemDecoration{

    private Paint spacingPaint;
    private int spacing;

    public SpacingDecoration(@DimenRes int spacing, Context context) {
        spacingPaint = new Paint();
        spacingPaint.setColor(ContextCompat.getColor(context, R.color.movies_list_background));
        spacingPaint.setStyle(Paint.Style.FILL);

        this.spacing = context.getResources().getDimensionPixelOffset(spacing);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        final int childCount = parent.getChildCount();
        final RecyclerView.LayoutManager lm = parent.getLayoutManager();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final int right = lm.getDecoratedRight(child);
            final int bottom = lm.getDecoratedBottom(child);

            // draw the bottom spacer
            canvas.drawRect(lm.getDecoratedLeft(child),
                    bottom - spacing,
                    right,
                    bottom,
                    spacingPaint);

            // draw the right spacer
            canvas.drawRect(right - spacing,
                    lm.getDecoratedTop(child),
                    right,
                    bottom - spacing,
                    spacingPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, spacing, spacing);
    }
}
