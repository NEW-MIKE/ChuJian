package com.glide.chujian.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CenterRecyclerView extends RecyclerView {
    private String TAG = "CenterRecyclerView";
    private int mToPosition;
    public CenterRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public CenterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (dy == 0){
            moveToCenterPosition(mToPosition);
        }
    }

    public void moveToCenterPosition(int position){
        int firstItem = getChildLayoutPosition(getChildAt(0));
        int lastItem = getChildLayoutPosition(getChildAt(getChildCount() - 1));
        if (position < firstItem) {
            scrollToPosition(position);
            mToPosition = position;
        } else if (position <= lastItem) {
            int groupCenterHeight = getHeight()/2;
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < getChildCount()) {
                int bottom = getChildAt(movePosition).getBottom();
                int top = getChildAt(movePosition).getTop();
                int viewCenterHeight = (bottom + top)/2;
                int dy = viewCenterHeight - groupCenterHeight;
                scrollBy(0, dy);
            }
        }else {
            scrollToPosition(position);
            mToPosition = position;
        }
    }
}
