package com.glide.chujian.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.glide.chujian.R;

public class GuidingCrossView  extends View {
    Paint mPaint;
    private int mXLocation = 0;
    private int mYLocation = 0;
    private int mXStartLocation = 0;
    private int mXEndLocation = 0;
    private int mYStartLocation = 0;
    private int mYEndLocation = 0;
    public static int SELECTED = 1;
    public static int SELECTING = 0;
    public GuidingCrossView(Context context){
        this(context,null);
    }
    public GuidingCrossView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.yellow));
        mPaint.setStrokeJoin(Paint.Join.ROUND);//设置画笔图形接触时笔迹的形状
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔离开画板时笔迹的形状
        mPaint.setStrokeWidth(1);
        mPaint.setPathEffect(new DashPathEffect(new float[]{0,0},0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(mXStartLocation, mYLocation, mXEndLocation, mYLocation, mPaint);
        canvas.drawLine(mXLocation, mYStartLocation, mXLocation, mYEndLocation, mPaint);
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

    public int getXLocation(){
        return mXLocation;
    }

    public int getYLocation(){
        return mYLocation;
    }
    public void setLineDashed(boolean isDashed){
        if (isDashed){
            mPaint.setPathEffect(new DashPathEffect(new float[]{4,4},0));
        }else {
            mPaint.setPathEffect(new DashPathEffect(new float[]{0,0},0));
        }
        invalidate();
    }

    public void setNewLocation(int x,int y,int xStart,int xEnd,int yStart,int yEnd){
        mXLocation = x;
        mYLocation = y;
        mXStartLocation = xStart;
        mXEndLocation = xEnd;
        mYStartLocation = yStart;
        mYEndLocation = yEnd;
        Log.e("TAG", "setNewLocation: x is" +x +"  y is"+y );
       // setVisibility(View.VISIBLE);
        invalidate();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

}
