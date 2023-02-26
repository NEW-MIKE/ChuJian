package com.glide.chujian.view;

import static com.glide.chujian.util.GuideUtil.getIntNumberNoUnit;
import static java.lang.Math.abs;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.glide.chujian.AstroLibraryActivity;
import com.glide.chujian.BaseActivity;
import com.glide.chujian.R;
import com.glide.chujian.databinding.GotoPanelLayoutBinding;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.GotoPopHandler;
import com.glide.chujian.util.GuideUtil;
import com.glide.chujian.util.Guider;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.view.ScrollPickerView.ScrollPickerAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GotoPop extends PopupWindow {
    private String TAG = "GotoPop";
    private GotoPanelLayoutBinding binding;
    private GotoProcessDialog mGotoProcessDialog;
    private GotoProcessDialog.Builder mGotoBuilder;
    private int mRaHour;
    private int mRaMinute;
    private int mRaSecond;
    private int mDecDegree;
    private int mDecDegreeIndex;
    private int mDecMinute;
    private int mDecSecond;
    private int mSpeed;
    private GotoPopHandler mGotoPopHandler;
    private Guider mGuider;
    private double mTargetRA = 0.0;
    private double mTargetDec = 0;
    private ArrayList<String> mTimeHourDataList = new ArrayList<String>();
    private ArrayList<String> mTimeMinuteDataList = new ArrayList<String>();
    private ArrayList<String> mTimeSecondDataList = new ArrayList<String>();
    private ArrayList<String> mArcDegreeDataList = new ArrayList<String>();
    private ArrayList<String> mArcMinuteDataList = new ArrayList<String>();
    private ArrayList<String> mArcSecondDataList = new ArrayList<String>();
    private ArrayList<String> mSpeedDataList = new ArrayList<String>();
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<String> mTimeHourBuilder;
    private ScrollPickerAdapter<String> mTimeHourScrollPickerAdapter;
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<String> mTimeMinuteBuilder;
    private ScrollPickerAdapter<String> mTimeMinuteScrollPickerAdapter;
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<String> mTimeSecondBuilder;
    private ScrollPickerAdapter<String> mTimeSecondScrollPickerAdapter;
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<String> mArcDegreeBuilder;
    private ScrollPickerAdapter<String> mArcDegreeScrollPickerAdapter;
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<String> mArcMinuteBuilder;
    private ScrollPickerAdapter<String> mArcMinuteScrollPickerAdapter;
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<String> mArcSecondBuilder;
    private ScrollPickerAdapter<String> mArcSecondScrollPickerAdapter;
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<String> mSpeedBuilder;
    private ScrollPickerAdapter<String> mSpeedScrollPickerAdapter;
    private BaseActivity mActivity;

    private boolean mIsScopeMoving = false;
    private Timer mCoordTimer;
    private CoordTimerTask mCoordTimerTask;
    public GotoPop(BaseActivity mActivity) {
        this.mActivity = mActivity;
        binding = GotoPanelLayoutBinding.inflate(LayoutInflater.from(mActivity));
        binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        mGuider = Guider.getInstance();
        setContentView(binding.getRoot());
        setFocusable(true);
        setOutsideTouchable(true);
        initScroller();
        initView();
        initEvent();
        mGotoPopHandler = new GotoPopHandler(this);
        mGuider.registerHandlerListener(mGotoPopHandler);
    }

    public void setGotoPark(boolean isPark){
        binding.astrolibraryBtnGo.setEnabled(!isPark);
        if (isPark){
            binding.parkCover.setVisibility(View.VISIBLE);
        }else {
            binding.parkCover.setVisibility(View.INVISIBLE);
        }
    }

    private void initView(){
        updateConnectRaDec();
    }

    private void initEvent(){
        binding.astrolibraryBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AstroLibraryActivity.actionStart(mActivity);
                dismiss();
            }
        });

        binding.southBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(true,"S");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(false,"S");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
                return false;
            }
        });
        binding.westBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(true,"W");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(false,"W");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
                return false;
            }
        });

        binding.northBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(true,"N");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(false,"N");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
                return false;
            }
        });

        binding.eastBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(true,"E");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Guider.getInstance().guideMove(false,"E");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
                return false;
            }
        });
        binding.astrolibraryBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTargetRA = GuideUtil.RaToDegree(mRaHour,mRaMinute,mRaSecond);
                mTargetDec = GuideUtil.DecToDegree(mDecDegree,mDecMinute,mDecSecond);
                mGotoBuilder =  new GotoProcessDialog.Builder(mActivity)
                        .setTargetRa(mTargetRA)
                        .setTargetDec(mTargetDec)
                        .setCurrentRa(mGuider.mCoordRa)
                        .setCurrentDec(mGuider.mCoordDec)
                        .setIsCancelable(true)
                        .setIsCancelOutside(false)
                        .setOnStopGotoListener(new GotoProcessDialog.OnStopGotoListener() {
                            @Override
                            public void stopGotoLocation(){
                                new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            mGuider.guideCoord(false, 1.1,1.1);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                                mGotoProcessDialog.dismiss();
                            }
                        });
                mGotoProcessDialog = mGotoBuilder.create();

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGotoProcessDialog.show();
                                }
                            });
                            boolean isCoordSuccess = Guider.getInstance().guideCoord(true, mTargetRA, mTargetDec);
                            if (!isCoordSuccess){
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mGotoProcessDialog.dismiss();
                                        ToastUtil.showToast(mActivity.getResources().getString(R.string.can_not_goto_target), true);
                                    }
                                });
                            }else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public void setGotoEnabled(boolean enabled){
        binding.astrolibraryBtnGo.setEnabled(enabled);
    }

    public void loadSpeedData(){
        int guidingSpeed = Guider.getInstance().mMountSpeedChangedValue;
        ArrayList<String> guidingSpeedList = Guider.getInstance().mScopeGuidingSpeedList;
        mSpeedDataList.clear();
        mSpeedDataList.add(Constant.CUSTOM_DATA);
        if (guidingSpeedList.size() == 0) {
            mSpeedDataList.add("1x");
            mSpeedDataList.add("2x");
            mSpeedDataList.add("3x");
            mSpeedDataList.add("4x");
            mSpeedDataList.add("5x");
            mSpeedDataList.add("6x");
            mSpeedDataList.add("7x");
            mSpeedDataList.add("8x");
            mSpeedDataList.add("9x");
        }
        else{
            for (String speed : guidingSpeedList) {
                mSpeedDataList.add(speed);
            }
        }
        mSpeedDataList.add(Constant.NO_CUSTOM_DATA);
        mSpeedBuilder.setDataList(mSpeedDataList);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (guidingSpeed != -1) {
                    binding.speedScroller.scrollPickerView.moveToCenterPosition(convertPosition(guidingSpeed));
                }
            }
        });
    }
    public void initScroller(){
        for (int i = 0;i <= 23;i++){
            if (i<10) {
                mTimeHourDataList.add("0"+i);
            }
            else mTimeHourDataList.add(""+i);
        }
        for (int i = 0;i <= 59; i++){
            if (i<10) {
                mTimeMinuteDataList.add("0"+i);
                mTimeSecondDataList.add("0"+i);
                mArcMinuteDataList.add("0"+i);
            }
            else {
                mTimeMinuteDataList.add(""+i);
                mTimeSecondDataList.add(""+i);
                mArcMinuteDataList.add(""+i);
            }
        }

        mArcSecondDataList.addAll(mArcMinuteDataList);

        for (int i = -90;i <= 90;i++) {
            if (abs(i) < 10) {
                if (i < 0)
                    mArcDegreeDataList.add("-0" + abs(i));
                else
                    mArcDegreeDataList.add("0" + abs(i));
            } else mArcDegreeDataList.add(""+i);
        }
        initScrollerView();
    }

    public void updateConnectRaDec(){
        double ra = mGuider.mCoordRa;
        double dec = mGuider.mCoordDec;
        mRaHour = (int)ra;
        mRaMinute = (int)((ra - mRaHour) * 60);
        mRaSecond = (int)(((ra - mRaHour) * 60 - mRaMinute) * 60);
        mDecDegreeIndex = 90;
        mDecDegree = (int)dec;
        mDecDegreeIndex += mDecDegree;
        mDecMinute = abs((int)((dec - mDecDegree) * 60));
        mDecSecond = (int)((abs((dec - mDecDegree) * 60) - mDecMinute) * 60);
    }
    private void loadRaDecData(){
        updateConnectRaDec();
        mDecDegreeIndex = 90;
        mDecDegreeIndex += mDecDegree;
        binding.hourScroller.scrollPickerView.moveToCenterPosition(convertPosition(mRaHour));
        binding.minuteScroller.scrollPickerView.moveToCenterPosition(convertPosition(mRaMinute));
        binding.secondScroller.scrollPickerView.moveToCenterPosition(convertPosition(mRaSecond));
        binding.zeroScroller.scrollPickerView.moveToCenterPosition(convertPosition(mDecDegreeIndex));
        binding.oneDotScroller.scrollPickerView.moveToCenterPosition(convertPosition(mDecMinute));
        binding.twoDotScroller.scrollPickerView.moveToCenterPosition(convertPosition(mDecSecond));
    }

    private int convertPosition(int index){
        return index + 1;
    }
    public void refreshFavoriteView(){
        mGuider.registerHandlerListener(mGotoPopHandler);
        loadSpeedData();
        loadRaDecData();
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
        if (mGotoProcessDialog != null){
            mGotoProcessDialog.dismiss();
            stopCoordTimer();
        }
        mGuider.unregisterHandlerListener(mGotoPopHandler);
    }
    private class CoordTimerTask extends TimerTask {
        @Override
        public void run() {
            if ( mActivity!= null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsScopeMoving) {
                            if (mGotoBuilder != null) {
                                mGotoBuilder.updateCurrentRa(mGuider.mCoordRa, false);
                                mGotoBuilder.updateCurrentDec(mGuider.mCoordDec, false);
                            }
                        }
                    }
                });
                mActivity.updateRaDecView();
            }
        }
    }

    private void startCoordTimer() {
        if (mCoordTimer != null){
            stopCoordTimer();
        }
        mCoordTimer = new Timer(true);
        mCoordTimerTask = new CoordTimerTask();
        mCoordTimer.schedule(mCoordTimerTask, 0, 100);
    }

    private void stopCoordTimer() {
        if (mCoordTimer != null) {
            mCoordTimer.cancel();
            mCoordTimer = null;
        }
        if (mCoordTimerTask != null){
            mCoordTimerTask.cancel();
            mCoordTimerTask = null;
        }
    }

    public void startSlewEvent(){
        mIsScopeMoving = true;
        startCoordTimer();
    }
    public void stopSlewEvent(){
        stopCoordTimer();
        mIsScopeMoving = false;
        if (mGotoBuilder != null){
            mGotoBuilder.updateCurrentRa(mGuider.mCoordRa,true);
            mGotoBuilder.updateCurrentDec(mGuider.mCoordDec,true);
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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGotoProcessDialog != null){
                            mGotoProcessDialog.dismiss();
                        }
                    }
                });
            }
        }.start();
    }

    public void gotoErrorEvent(){
        if (mGuider.mAlertMsg.equals("Failed to slew. ")){
            if (mGotoProcessDialog != null){
                mGotoProcessDialog.dismiss();
            }
        }
    }

    public void deviceConnectEvent(){
        // updateConnectRaDec();
    }

    public void mountSpeedChangedEvent(){
        binding.speedScroller.scrollPickerView.moveToCenterPosition(convertPosition(mGuider.mMountSpeedChangedValue));
    }

    private void initScrollerView(){
        /*小时*/
        mTimeHourBuilder = new
                ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mActivity)
                .setDataList(mTimeHourDataList)
                .selectedItemOffset(mRaHour)
                .visibleItemNumber(3)
                .setItemViewProvider(null)
                .setViewType(ScrollPickerAdapter.DIALOG_TYPE)
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View currentItemView) {
                        String text = (String) currentItemView.getTag();
                        if (text != null) {
                            mRaHour = Integer.parseInt(text);
                        }
                    }

                    @Override
                    public void onScroll() {

                    }

                    @Override
                    public void hideCover() {

                    }
                });
        mTimeHourScrollPickerAdapter = mTimeHourBuilder.build();

        /*分钟*/
        mTimeMinuteBuilder = new
                ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mActivity)
                .setDataList(mTimeMinuteDataList)
                .selectedItemOffset(mRaMinute)
                .visibleItemNumber(3)
                .setItemViewProvider(null)
                .setViewType(ScrollPickerAdapter.DIALOG_TYPE)
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View currentItemView) {
                        String text = (String) currentItemView.getTag();
                        if (text != null) {
                            mRaMinute = Integer.parseInt(text);
                        }
                    }

                    @Override
                    public void onScroll() {

                    }

                    @Override
                    public void hideCover() {

                    }
                });
        mTimeMinuteScrollPickerAdapter = mTimeMinuteBuilder.build();

        /*秒钟*/
        mTimeSecondBuilder = new
                ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mActivity)
                .setDataList(mTimeSecondDataList)
                .selectedItemOffset(mRaSecond)
                .visibleItemNumber(3)
                .setItemViewProvider(null)
                .setViewType(ScrollPickerAdapter.DIALOG_TYPE)
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View currentItemView) {
                        String text = (String) currentItemView.getTag();
                        if (text != null) {
                            mRaSecond = Integer.parseInt(text);
                        }
                    }

                    @Override
                    public void onScroll() {

                    }

                    @Override
                    public void hideCover() {

                    }
                });
        mTimeSecondScrollPickerAdapter = mTimeSecondBuilder.build();

        /*度*/
        mArcDegreeBuilder = new
                ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mActivity)
                .setDataList(mArcDegreeDataList)
                .selectedItemOffset(mDecDegreeIndex)
                .visibleItemNumber(3)
                .setItemViewProvider(null)
                .setViewType(ScrollPickerAdapter.DIALOG_TYPE)
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View currentItemView) {
                        String text = (String) currentItemView.getTag();
                        if (text != null) {
                            mDecDegree = Integer.parseInt(text);
                            Log.e(TAG, "onScrolled: get_scope_coord"+mDecDegree );
                        }
                    }

                    @Override
                    public void onScroll() {

                    }

                    @Override
                    public void hideCover() {

                    }
                });
        mArcDegreeScrollPickerAdapter = mArcDegreeBuilder.build();

        /*度分*/
        mArcMinuteBuilder = new
                ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mActivity)
                .setDataList(mArcMinuteDataList)
                .selectedItemOffset(mDecMinute)
                .visibleItemNumber(3)
                .setItemViewProvider(null)
                .setViewType(ScrollPickerAdapter.DIALOG_TYPE)
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View currentItemView) {
                        String text = (String) currentItemView.getTag();
                        if (text != null) {
                            mDecMinute = Integer.parseInt(text);
                        }
                    }

                    @Override
                    public void onScroll() {

                    }

                    @Override
                    public void hideCover() {

                    }
                });
        mArcMinuteScrollPickerAdapter = mArcMinuteBuilder.build();

        /*度秒*/
        mArcSecondBuilder = new
                ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mActivity)
                .setDataList(mArcSecondDataList)
                .selectedItemOffset(mDecSecond)
                .visibleItemNumber(3)
                .setItemViewProvider(null)
                .setViewType(ScrollPickerAdapter.DIALOG_TYPE)
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View currentItemView) {
                        String text = (String) currentItemView.getTag();
                        if (text != null) {
                            mDecSecond = Integer.parseInt(text);
                        }
                    }

                    @Override
                    public void onScroll() {

                    }

                    @Override
                    public void hideCover() {

                    }
                });
        mArcSecondScrollPickerAdapter = mArcSecondBuilder.build();

        /*速度*/
        mSpeedBuilder = new
                ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(mActivity)
                .setDataList(mSpeedDataList)
                .selectedItemOffset(0)
                .visibleItemNumber(3)
                .setItemViewProvider(null)
                .setViewType(ScrollPickerAdapter.DIALOG_TYPE)
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View currentItemView) {
                        String text = (String) currentItemView.getTag();
                        if (text != null) {
                            mSpeed = getIntNumberNoUnit(text);
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    Guider.getInstance().setScopeSpeed(mSpeed - 1);
                                }
                            }.start();
                        }
                    }

                    @Override
                    public void onScroll() {

                    }

                    @Override
                    public void hideCover() {

                    }
                });
        mSpeedScrollPickerAdapter = mSpeedBuilder.build();

        binding.hourScroller.scrollPickerView.setAdapter(mTimeHourScrollPickerAdapter);
        binding.minuteScroller.scrollPickerView.setAdapter(mTimeMinuteScrollPickerAdapter);
        binding.secondScroller.scrollPickerView.setAdapter(mTimeSecondScrollPickerAdapter);
        binding.zeroScroller.scrollPickerView.setAdapter(mArcDegreeScrollPickerAdapter);
        binding.oneDotScroller.scrollPickerView.setAdapter(mArcMinuteScrollPickerAdapter);
        binding.twoDotScroller.scrollPickerView.setAdapter(mArcSecondScrollPickerAdapter);
        binding.speedScroller.scrollPickerView.setAdapter(mSpeedScrollPickerAdapter);
    }

}
