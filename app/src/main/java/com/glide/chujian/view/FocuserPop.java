package com.glide.chujian.view;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.glide.chujian.App;
import com.glide.chujian.BaseActivity;
import com.glide.chujian.R;
import com.glide.chujian.databinding.FocuserPopPanelLayoutBinding;
import com.glide.chujian.util.FocuserPopHandler;
import com.glide.chujian.util.Guider;

import java.util.Timer;
import java.util.TimerTask;

public class FocuserPop extends PopupWindow {
    private String TAG = "FocuserPop";
    private FocuserPopPanelLayoutBinding binding;
    private BaseActivity mActivity;
    private boolean mIsForwardLongClick = false;
    private boolean mIsBackLongClick = false;
    private Guider mGuider;
    private FocuserPopHandler mFocuserPopHandler;

    private Timer mFocuserCurrentPositionChangedTimer;
    private FocuserCurrentPositionChangedTask mFocuserCurrentPositionChangedTask;
    public FocuserPop(BaseActivity mActivity) {
        this.mActivity = mActivity;
        binding = FocuserPopPanelLayoutBinding.inflate(LayoutInflater.from(mActivity));
        binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        setContentView(binding.getRoot());
        setFocusable(true);
        setOutsideTouchable(true);
        mGuider = Guider.getInstance();
        mFocuserPopHandler = new FocuserPopHandler(this);
        mGuider.registerHandlerListener(mFocuserPopHandler);
        initEvent();
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
        mGuider.unregisterHandlerListener(mFocuserPopHandler);
        stopFocuserCurrentPositionChangedTask();
    }

    public void updateFocuserPop(){
        mGuider.registerHandlerListener(mFocuserPopHandler);
        Boolean value = mGuider.mFocuserMode;
        if (value != null){
            binding.stepSwitch.setChecked(!value);
        }
        binding.focuserCurrentValueTv.setText(mGuider.mFocuserPosition+"");
        checkFocuserMoving(mGuider.mFocuserIsMoving);
    }

    public void focuserModeEvent(){
        Boolean value = mGuider.mFocuserMode;
        if (value != null){
            binding.stepSwitch.setChecked(!value);
        }
    }

    private void checkFocuserMoving(boolean moving){
        boolean isEnabled = !moving;
        binding.stepSwitch.setEnabled(isEnabled);
        binding.focuserForwardTv.setEnabled(isEnabled);
        binding.focuserBackTv.setEnabled(isEnabled);
    }

    public void focuserCurrentStepEvent(){
        startFocuserCurrentPositionChangedTask();
        updateCurrentStep();
    }
    private void updateCurrentStep(){
        binding.focuserCurrentValueTv.setTextColor(App.INSTANCE.getResources().getColor(R.color.histogram_data_line_color));
        binding.focuserCurrentValueTv.setText(mGuider.mFocuserPosition+"");
        checkFocuserMoving(mGuider.mFocuserIsMoving);
    }

    private void initEvent(){
        binding.stepSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean value = !binding.stepSwitch.isChecked();
                mGuider.mFocuserMode = value;
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.setFocuserMode(value);
                    }
                }.start();
            }
        });
        binding.focuserForwardTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.focuserMovePulse(true);
                    }
                }.start();
            }
        });

        binding.focuserForwardTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIsForwardLongClick = true;
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.focuserMove(true);
                    }
                }.start();
                return true;
            }
        });

        binding.focuserForwardTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mIsForwardLongClick = false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (mIsForwardLongClick){
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                mGuider.focuserMoveStop();
                            }
                        }.start();
                    }
                }
                return false;
            }
        });

        binding.focuserBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.focuserMovePulse(false);
                    }
                }.start();
            }
        });

        binding.focuserBackTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIsBackLongClick = true;
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.focuserMove(false);
                    }
                }.start();
                return true;
            }
        });

        binding.focuserBackTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mIsBackLongClick = false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (mIsBackLongClick){
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                mGuider.focuserMoveStop();
                            }
                        }.start();
                    }
                }
                return false;
            }
        });
    }

    private class FocuserCurrentPositionChangedTask extends TimerTask {
        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.focuserCurrentValueTv.setTextColor(App.INSTANCE.getResources().getColor(R.color.normal_text_color));
                }
            });
        }
    }
    private void startFocuserCurrentPositionChangedTask(){
        stopFocuserCurrentPositionChangedTask();
        mFocuserCurrentPositionChangedTimer = new Timer();
        mFocuserCurrentPositionChangedTask = new FocuserCurrentPositionChangedTask();
        mFocuserCurrentPositionChangedTimer.schedule(mFocuserCurrentPositionChangedTask,1000);
    }

    private void stopFocuserCurrentPositionChangedTask(){
        if (mFocuserCurrentPositionChangedTask != null){
            mFocuserCurrentPositionChangedTask.cancel();
            mFocuserCurrentPositionChangedTask = null;
        }
        if (mFocuserCurrentPositionChangedTimer != null){
            mFocuserCurrentPositionChangedTimer.cancel();
            mFocuserCurrentPositionChangedTimer = null;
        }
    }
}
