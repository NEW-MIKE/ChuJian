package com.glide.chujian.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public class RecyclerViewPageChangeListenerHelper extends RecyclerView.OnScrollListener {
    private SnapHelper mSnapHelper;
    private OnPageChangeListener mOnPageChangeListener;

    public RecyclerViewPageChangeListenerHelper(SnapHelper snapHelper, OnPageChangeListener onPageChangeListener) {
        this.mSnapHelper = snapHelper;
        this.mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int position = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        //获取当前选中的itemView
        View view = this.mSnapHelper.findSnapView(layoutManager);
        if (view != null) {
            //获取itemView的position
            position = layoutManager.getPosition(view);
        }

        if (this.mOnPageChangeListener != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
            this.mOnPageChangeListener.onPageSelected(position);
        }
    }

    public interface OnPageChangeListener {
        void onPageSelected(int position);
    }
}
