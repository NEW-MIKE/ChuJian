package com.glide.chujian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerDropPicView extends androidx.appcompat.widget.AppCompatTextView {
    public SpinnerDropPicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }
}
