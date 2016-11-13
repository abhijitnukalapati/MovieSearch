package com.diaby.moviesearch.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by abhijitnukalapati on 11/13/16.
 */

public class PaginationHelper extends RecyclerView.OnScrollListener {

    public interface PaginationCallback {
        void onLoadMore(RecyclerView recyclerView);

        boolean shouldLoadMore();
    }

    private PaginationCallback paginationCallback;

    public PaginationHelper(PaginationCallback callback) {
        this.paginationCallback = callback;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        final int currentItemCount = layoutManager.getItemCount();
        final int visibleItemCount = recyclerView.getChildCount();
        final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        final int buffer = layoutManager.getSpanCount() * 2;

        // load more items if we reached the end of the list
        if ((currentItemCount - visibleItemCount) <= (firstVisibleItemPosition + buffer)
                && paginationCallback.shouldLoadMore()) {
            paginationCallback.onLoadMore(recyclerView);
        }

    }
}
