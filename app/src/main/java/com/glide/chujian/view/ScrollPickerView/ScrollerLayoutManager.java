package com.glide.chujian.view.ScrollPickerView;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.App;

public class ScrollerLayoutManager extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 10f;
    private Context mContext;
    private boolean isScrollEnabled = true;

    public ScrollerLayoutManager(Context context) {
        super(context);
        mContext = context;
    }
    public void setScrollEnabled(boolean flag) {
        isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(App.INSTANCE) {

                    //This controls the direction in which smoothScroll looks
                    //for your view
                    @Override
                    public PointF computeScrollVectorForPosition
                    (int targetPosition) {
                        return ScrollerLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    //This returns the milliseconds it takes to
                    //scroll one pixel.
                    @Override
                    protected float calculateSpeedPerPixel
                    (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH/displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}