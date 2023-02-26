package com.glide.chujian.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;

public class BrowserRecyclerView extends RecyclerView {
    private final String TAG = "BrowserRecyclerView";
    public BrowserRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public BrowserRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrowserRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
/*        int height = getHeight() / 3 - getResources().getInteger(R.integer.browser_item_margin);
        ViewGroup.LayoutParams layoutParams;
        for (int i = 0;i < getChildCount();i++){
            layoutParams = getChildAt(i).getLayoutParams();
            layoutParams.height = height;
            getChildAt(i).setLayoutParams(layoutParams);
        }*/
    }
}
