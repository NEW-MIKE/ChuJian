package com.glide.chujian.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.glide.chujian.R;
import com.glide.chujian.util.ScreenUtil;

public class TargetLocationView extends FrameLayout
{
    private String TAG = "TargetLocationView";
    private float mMaxNumber = 22;
    private float mMinNumber = 10;
    private float mOffset;
    private ConstraintLayout mConstraintLayout;
    private VerticalLineView mVerticalLineView;
    private TextView mMaxNumberTv;

    private Runnable mMoveToTargetChangedTask;
    private float mTargetLocation = 0;

    public TargetLocationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.target_move_layout, this);
        mOffset = ScreenUtil.androidAutoSizeDpToPx(17);
        mConstraintLayout = findViewById(R.id.horizontal_progress_parent);
        mVerticalLineView = findViewById(R.id.center_line);
        mMaxNumberTv = findViewById(R.id.target_value);
        mMoveToTargetChangedTask = new Runnable() {
            @Override
            public void run() {
                toTargetLocation();
            }
        };
    }

    public void setMaxNumber(float maxNumber ){
        if (mMaxNumber != maxNumber) {
            mMaxNumber = maxNumber;
        }
    }
    public void setMinNumber(float minNumber){
        if (mMinNumber != minNumber) {
            mMinNumber = minNumber;
        }
    }

    private void toTargetLocation(){
        Rect tempContainerRect = new Rect();
        mConstraintLayout.getGlobalVisibleRect(tempContainerRect);
        float containerWidth = (tempContainerRect.right - tempContainerRect.left - mOffset - mOffset);
        float scale = (mTargetLocation)/(mMaxNumber - mMinNumber);
        float middleMarginLeft = (containerWidth * scale);
        mVerticalLineView.setTranslationX(middleMarginLeft);

        float deltaTime = 12f * scale + 18;
        int minute = (int)((deltaTime - ((int) deltaTime)) * 60);
        int deltaHour = (int)deltaTime;
        if (deltaHour >= 24){
            deltaHour = deltaHour - 24;
        }
        String time = "";
        if (deltaHour < 10){
            time = "0"+deltaHour;
        }else {
            time = ""+deltaHour;
        }
        if (minute < 10){
            time += ":0"+minute;
        }else {
            time += ":"+minute;
        }

        mMaxNumberTv.setText(time);
        mMaxNumberTv.setTranslationX(middleMarginLeft);
        mVerticalLineView.setVisibility(VISIBLE);
        mMaxNumberTv.setVisibility(VISIBLE);
    }
    public void setToTargetPosition(float number){
        mTargetLocation = number;
        mVerticalLineView.setVisibility(INVISIBLE);
        mMaxNumberTv.setVisibility(INVISIBLE);
        postDelayed(mMoveToTargetChangedTask, 0);
    }
}
