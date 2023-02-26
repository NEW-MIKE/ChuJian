package com.glide.chujian.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.glide.chujian.R;

public class VerticalLineView extends View {
    Paint mPaint;
    private int mXLocation = 0;
    private int mYStartLocation = 0;
    private int mYEndLocation = 0;
    public VerticalLineView(Context context){
        this(context,null);
    }
    public VerticalLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.green));
        mPaint.setPathEffect(new DashPathEffect(new float[]{8,8},0));
        mPaint.setStrokeJoin(Paint.Join.ROUND);//设置画笔图形接触时笔迹的形状
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔离开画板时笔迹的形状
        mPaint.setStrokeWidth(4);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mYEndLocation = h;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mXLocation, mYStartLocation, mXLocation, mYEndLocation, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

}
