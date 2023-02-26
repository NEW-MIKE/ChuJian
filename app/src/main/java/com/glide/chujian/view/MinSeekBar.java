package com.glide.chujian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.glide.chujian.R;

public class MinSeekBar extends FrameLayout {
    private String TAG = "MinSeekBar";
    private AppCompatSeekBar mMinSeekBar;
    private int MaxNumber = 1;
    private int MinNumber = 0;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    public MinSeekBar(@NonNull Context context) {
        super(context);
    }

    public void setMinEnabled(boolean enabled){
        if (mMinSeekBar != null){
            mMinSeekBar.setEnabled(enabled);
        }
    }
    public MinSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.min_seekbar, this);
        mMinSeekBar = findViewById(R.id.min_seekbar);
        mMinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onProgressChanged((AppCompatSeekBar) seekBar, progress + MinNumber, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mMinSeekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (mOnSeekBarChangeListener != null) {
                            mOnSeekBarChangeListener.onStopTrackingTouch(mMinSeekBar);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        if (mOnSeekBarChangeListener != null) {
                            mOnSeekBarChangeListener.onStartTrackingTouch(mMinSeekBar);
                        }
                        break;
                }
                return false;
            }
        });
    }

    public void setBarMax(int max){
        Log.e(TAG, "setBarMax: "+max );
        MaxNumber = max;
        if (MaxNumber > MinNumber) {
            mMinSeekBar.setMax(MaxNumber - MinNumber);
        }
    }

    public void setBarMin(int min){
        Log.e(TAG, "setBarMin: "+min);
        MinNumber = min;
        if (MaxNumber > MinNumber) {
            mMinSeekBar.setMax(MaxNumber - MinNumber);
        }
    }

    public void setProgress(int progress){
        mMinSeekBar.setProgress(progress - MinNumber);
    }

    public int getProgress(){
        return (mMinSeekBar.getProgress() + MinNumber);
    }
    public interface OnSeekBarChangeListener {
        void onProgressChanged(AppCompatSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(AppCompatSeekBar seekBar);

        void onStopTrackingTouch(AppCompatSeekBar seekBar);
    }
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }
}