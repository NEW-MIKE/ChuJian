package com.glide.chujian;

import static com.glide.chujian.util.DeviceUtil.*;

import com.glide.chujian.databinding.ActivityMainBinding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.glide.chujian.model.GuiderModel.*;
import com.glide.chujian.util.*;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity{
    private final static String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private final double mStartClock = 5 * 60 * 60;
    private final double mEndClock = 22 * 60 * 60;
    private final double MAX_VALUE = 1000;
    private LastTimerTask mLastTimerTask;
    private Timer mTimer;
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }

    @Override
    public void updateWifiName(String wifi_name) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    private void init(){
        binding.lifePrgressBar.setMax((int) MAX_VALUE);
        startTimer();
        initEvent();
    }

    private void initEvent(){
        binding.startDayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerActivity.actionStart(MainActivity.this);
            }
        });

        binding.endDayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.thisManTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeActivity.actionStart(MainActivity.this);
            }
        });
    }

    private double getProgress(){
        double value = (mEndClock - DateUtil.getCurrentTimeSecond())/(mEndClock - mStartClock);
        double progress = value * MAX_VALUE;
        return progress;
    }

    private double getLastSecond(){
        return (mEndClock - DateUtil.getCurrentTimeSecond());
    }

    private String getStringLastTime(double value){
        int hour = (int) (value / (60*60));
        int minute = (int) ((value - hour * 60 * 60)/60);
        int second = (int) (value - hour * 60 * 60 - minute *60);
        String sHour = hour+"";
        String sMinute = minute+"";
        String sSecond = second+"";
        if (hour < 10){
            sHour = "0"+hour+"";
        }
        if (minute < 10){
            sMinute = "0"+minute+"";
        }
        if (second < 10){
            sSecond = "0"+second+"";
        }
        if (hour < 0 || minute < 0 || second < 0){
            return "00:00:00";
        }

        if (DateUtil.getCurrentTimeSecond() < mStartClock){
            return "00:00:00";
        }
        return sHour+":"+sMinute+":"+sSecond;
    }

    private class LastTimerTask extends TimerTask{
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    double value = getProgress();
                    if (DateUtil.getCurrentTimeSecond() < mStartClock){
                        value = 0;
                    }
                    binding.lifePrgressBar.setProgress((int)value);
                    binding.lastLifeTv.setText(getStringLastTime(getLastSecond()));
                }
            });
        }
    }

    private void startTimer(){
        if (mTimer != null){
            stopTimer();
        }
        mTimer = new Timer();
        mLastTimerTask = new LastTimerTask();
        mTimer.schedule(mLastTimerTask,0,1000);
    }

    private void stopTimer(){
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }

        if (mLastTimerTask != null){
            mLastTimerTask.cancel();
            mLastTimerTask = null;
        }
    }
}
