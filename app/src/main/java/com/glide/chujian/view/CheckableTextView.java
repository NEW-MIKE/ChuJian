package com.glide.chujian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import android.R;

public class CheckableTextView extends AppCompatTextView implements Checkable {
    private boolean mChecked = false;
    private int[] CHECKED_STATE_SET = {R.attr.state_checked};
    public CheckableTextView(@NonNull Context context) {
        super(context);
    }

    public CheckableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        refreshDrawableState();
    }

    public void setCheckedd(boolean checked) {
        mChecked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
