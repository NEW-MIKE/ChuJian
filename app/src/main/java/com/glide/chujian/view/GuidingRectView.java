package com.glide.chujian.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.glide.chujian.R;

public class GuidingRectView extends View {
    Paint mPaint;
    private int mWidth = 40;
    private int mTop = 0;
    private int mLeft = 0;
    private int mBottom = 0;
    private int mRight = 0;
    public static int SELECTED = 1;
    public static int SELECTING = 0;
    public GuidingRectView(Context context) {
        this(context,null);
    }

    public GuidingRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.green));
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
        super.onDraw(canvas);
    }

    public void setNewLocation(int x,int y){
        mTop = y - mWidth;
        mLeft = x - mWidth;
        mBottom = y + mWidth;
        mRight = x + mWidth;
       // setVisibility(View.VISIBLE);
        invalidate();
    }

    public void setGuidingColor(int modeColor){
        if (modeColor == SELECTED){
            mPaint.setColor(getResources().getColor(R.color.green));
        }else {
            mPaint.setColor(getResources().getColor(R.color.yellow));
        }
        setVisibility(View.VISIBLE);
        invalidate();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }
}
