package com.glide.chujian.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.R;

import org.jetbrains.annotations.NotNull;

public class SpacesItemDecorationLine  extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private final Rect mBounds;
    private Paint mPaint;
    private final int[] ATTRS;
    @NotNull
    private final Context mContext;
    private final int mSpace;

    public SpacesItemDecorationLine(@NotNull Context context, int space) {
        ATTRS = new int[]{R.attr.listDivider};
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.mContext = context;
        this.mSpace = space;
        mBounds = new Rect();
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(com.glide.chujian.R.color.all_blue_color));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = this.mSpace;
        outRect.bottom = this.mSpace;
        outRect.left = this.mSpace;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int childCount = parent.getChildCount();

        for(int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            int top = bottom - mDivider.getIntrinsicHeight();
            canvas.drawRect(left, top, right, bottom, mPaint);
            mDivider.setBounds(left, top, right, bottom);
        }

        canvas.restore();
    }
}
