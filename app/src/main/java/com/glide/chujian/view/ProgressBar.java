package com.glide.chujian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.glide.chujian.R;
import com.glide.chujian.util.ScreenUtil;

public class ProgressBar extends FrameLayout
{
    private String TAG = "ProgressBar";
    private VerticalSeekBar mSeekBar;

    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private Context mContext;
    private double mHeight, mfDensity;
    private int MaxNumber = 1;
    private int MinNumber = 0;
    private TextView mProgressTv;
    private TextView mTypeTv;
    private ConstraintLayout mConstraintLayout;
    private boolean mIsInfoVisible = false;

    private boolean mIsFromUser = false;
    public void setBarDegree(String degree){
        if (mIsInfoVisible) {
            mProgressTv.setText(degree);
        }
    }

    public void setType(String type){
        mTypeTv.setText(type);
    }

    public ProgressBar(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.progress_bar, this);
        mSeekBar = findViewById(R.id.move_seekbar);
        mProgressTv = findViewById(R.id.progress_tv);
        mConstraintLayout = findViewById(R.id.progress_parent);
        mTypeTv = findViewById(R.id.type_tv);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onProgressChanged((VerticalSeekBar) seekBar, progress + MinNumber, mIsFromUser);
                }
                mHeight = seekBar.getHeight();
                mfDensity = (mHeight - ScreenUtil.androidAutoSizeDpToPx(20)) / (MaxNumber - MinNumber);
                ConstraintSet constraint = new ConstraintSet();

                constraint.clone(context, R.layout.progress_bar);
                constraint.connect(
                        mProgressTv.getId(),
                        ConstraintSet.BOTTOM,
                        mConstraintLayout.getId(),
                        ConstraintSet.BOTTOM,
                        (int) ((progress * mfDensity) - mProgressTv.getHeight()/2+ScreenUtil.androidAutoSizeDpToPx(50))
                );

                constraint.applyTo(mConstraintLayout);
                if (mIsInfoVisible) {
                    mProgressTv.setVisibility(VISIBLE);
                }else {
                    mProgressTv.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mSeekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mIsInfoVisible = false;
                        mIsFromUser = false;
                        if (mOnSeekBarChangeListener != null) {
                            mOnSeekBarChangeListener.onStopTrackingTouch(mSeekBar);
                        }
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mProgressTv.setVisibility(INVISIBLE);
                            }
                        }.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mIsInfoVisible = true;
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mIsInfoVisible = true;
                        mIsFromUser = true;
                        if (mOnSeekBarChangeListener != null) {
                            mOnSeekBarChangeListener.onStartTrackingTouch(mSeekBar);
                        }
                        break;
                }
                return false;
            }
        });
    }

    public void setBarMax(int max){
        MaxNumber = max;
        if (MaxNumber > MinNumber) {
            mSeekBar.setMax(MaxNumber - MinNumber);
        }
    }

    public void setBarMin(int min){
        MinNumber = min;
        if (MaxNumber > MinNumber) {
            mSeekBar.setMax(MaxNumber - MinNumber);
        }
    }

    public void setProgress(int progress){
        mSeekBar.setProgress(progress - MinNumber);
    }

    public int getProgress(){
        return (mSeekBar.getProgress() + MinNumber);
    }
    public interface OnSeekBarChangeListener {
        void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(VerticalSeekBar seekBar);

        void onStopTrackingTouch(VerticalSeekBar seekBar);
    }
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }
}