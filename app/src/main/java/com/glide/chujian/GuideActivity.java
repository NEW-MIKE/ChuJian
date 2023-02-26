package com.glide.chujian;

import static com.glide.chujian.util.Constant.*;
import static com.glide.chujian.util.GuideUtil.getFloatNumberNoUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.adapter.ClickMenuAdapter;
import com.glide.chujian.adapter.ClickMenuAdapterListener;
import com.glide.chujian.databinding.ActivityGuideBinding;
import com.glide.chujian.fragment.FilterWheelFragment;
import com.glide.chujian.fragment.FocuserFragment;
import com.glide.chujian.fragment.GuideCameraFragment;
import com.glide.chujian.fragment.MainCameraFragment;
import com.glide.chujian.fragment.MiscFragment;
import com.glide.chujian.fragment.ScopeFragment;
import com.glide.chujian.fragment.TabItem;
import com.glide.chujian.manager.CombinedChartManager;
import com.glide.chujian.model.ClickMenuItem;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.Guider;
import com.glide.chujian.util.LogUtil;
import com.glide.chujian.util.ScreenUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpLib;
import com.glide.chujian.util.TpScreenGuideHandler;
import com.glide.chujian.util.CheckParamUtil;
import com.glide.chujian.view.BrokenLineView;
import com.glide.chujian.view.GotoPop;
import com.glide.chujian.view.GotoProcessDialog;
import com.glide.chujian.view.GuidingCrossView;
import com.glide.chujian.view.GuidingRectView;
import com.glide.chujian.view.LogDialog;
import com.glide.chujian.view.ParkDialog;
import com.glide.chujian.view.ProgressBar;
import com.glide.chujian.view.SettingPageMenuLayout;
import com.glide.chujian.view.VerticalSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuideActivity extends BaseActivity{
    private String TAG = "GuideActivity";
    private boolean mIsNeedRestoreGuidingStatus = true;
    private int mDockStarPeak = 0;
    private static String FROM_TAG = "route";
    private String mFromRoute = "";
    public TpLib mLib;
    private int mWidth;
    private int mHeight;
    private int mClickRawX,mClickRawY;
    private GotoProcessDialog mGotoProcessDialog;
    private GotoProcessDialog.Builder mGotoBuilder;

    private int mDockCurrentTabId = 0;//用于记忆点击label的时候，记住上一次的label
    private int mStreamRestartCnt = 0;
    private final ArrayList<String> mResDataList = new ArrayList();
    private final ArrayList<String> mResDataValueList = new ArrayList();
    private int mCurrentResIndex = 0;
    private Timer mCoordTimer;
    private CoordTimerTask mCoordTimerTask;
    private double mTargetRA = 0.0;
    private double mTargetDec = 0;
    private final int INIT = -1;
    private final int MODE_SINGLE = 0;
    private final int MODE_SPLIT = 1;
    private final int MODE_TAB = 2;
    private final int MODE_CHART_SINGLE = 3;
    private final int MODE_STAR_SINGLE = 4;
    private int mLastMode = MODE_SPLIT;
    private final Integer PANEL_S = 1;
    private final Integer PANEL_C = 2;
    PointF mPt = new PointF();
    private ArrayList<Fragment> mFragments = new ArrayList();
    private final TabItem[] mTabs = new TabItem[]{
            new TabItem(MainCameraFragment.class),
            new TabItem(GuideCameraFragment.class),
            new TabItem(FilterWheelFragment.class),
            new TabItem(FocuserFragment.class),
            new TabItem(ScopeFragment.class),
            new TabItem(MiscFragment.class)};
    private String mCurrentGain = App.INSTANCE.getResources().getString(R.string.Gain)+": ";
    private String mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp)+": ";
    private String mCurrentRes = "";
    private String mEditExpValue = "";
    private String mEditDitherValue = "";
    private Double mCurrentDitherValue = 0.0;
    private String mCurrentDither = App.INSTANCE.getResources().getString(R.string.dither)+": ";
    private int mCurrentGainIndex = 0;
    private int mSeekBarIndex = 0;
    private int mCurrentExpIndex = 0;
    private int mCurrentDitherIndex = 0;
    private String mGuidingStatus = "";
    private String mFps = "";
    private final ArrayList<String> mGainDataList = new ArrayList();
    private final ArrayList<ClickMenuItem> mClickMenuList = new ArrayList();
    private final ArrayList<String> mDitherDataList = new ArrayList();
    private ClickMenuAdapter mClickMenuAdapter;
    private GotoPop mGoToPop;
    private ArrayList<Integer> mPanelList = new ArrayList();
    private int mPanelMode = INIT;
    private double mStreamFpsCnt = 0;
    private Double mResX = 0.0;
    private Double mResY = 0.0;
    private Timer mFpsTimer;
    private MyFpsTimerTask mFpsTimerTask;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private CombinedChartManager mChartManager;

    ArrayList<String> mBarNames = new ArrayList<String>();
    ArrayList<Integer> mLineColors = new ArrayList<Integer>();
    ArrayList<Integer> mBarColors = new ArrayList<Integer>();
    ArrayList<String> mLineNames = new ArrayList<String>();
    ArrayList<List<Float>> mYLineData = new ArrayList<List<Float>>();

    private ParkDialog.Builder mUnParkBuilder;
    private ParkDialog mUnParkDialog;
    private LogDialog.Builder mLogBuilder;
    private LogDialog mLogDialog;
    boolean mIsStarLost = false;
    boolean mIsStarGuiding = false;
    int mTitleHeight = 0;
    private int mExposureTime = 10;
    private TpScreenGuideHandler mHandler  = new TpScreenGuideHandler(this);

    private ActivityGuideBinding binding;
    private Guider mGuider;

    private ArrayList<ToggleButton> mToggleBtnList = new ArrayList<ToggleButton>();
    private ArrayList<RadioButton> mRadioBtnList = new ArrayList<RadioButton>();

    private RaDecTask mRaDecTask;
    private Timer mRaDecTimer;
    private boolean mIsScopeMoving = false;

    private List<BrokenLineView.XValue> xValues = new ArrayList();
    private List<BrokenLineView.YValue> yValues = new ArrayList();
    private List<BrokenLineView.LineValue> lineValues = new ArrayList();

    public static void actionStart(Context context, String route){
        Intent intent = new Intent(context, GuideActivity.class);
        intent.putExtra(FROM_TAG,route);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }

    @Override
    public void updateRaDecView() {
        if (mGuider.mScopeIsDeviceConnected && mGuider.mIsNetworkOnLine) {
            mGuider.updateCoordData();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mScopeIsDeviceConnected && mGuider.mIsNetworkOnLine) {
                    binding.leftLayout.raDecContainer.setVisibility(View.VISIBLE);
                    if (mGuider.mIsScopeValid) {
                        binding.leftLayout.raDecValue.setText(String.format("%.2f", mGuider.mCoordRa)
                                + "\n" + String.format("%.2f", mGuider.mCoordDec));
                    }else {
                        binding.leftLayout.raDecValue.setText("null"
                                + "\n" + "null");
                    }
                    if (mIsScopeMoving) {
                        if (mGotoBuilder != null) {
                            mGotoBuilder.updateCurrentRa(mGuider.mCoordRa, false);
                            mGotoBuilder.updateCurrentDec(mGuider.mCoordDec, false);
                        }
                    }
                }else {
                    binding.leftLayout.raDecContainer.setVisibility(View.INVISIBLE);
                    binding.leftLayout.raDecValue.setText("");
                }
            }
        });
    }

    private class RaDecTask extends TimerTask{
        @Override
        public void run() {
            updateRaDecView();
        }
    }
    private void startRaDecTask(){
        stopRaDecTask();
        mRaDecTimer = new Timer();
        mRaDecTask = new RaDecTask();
        mRaDecTimer.schedule(mRaDecTask,0,5000);
    }

    private void stopRaDecTask(){
        if (mRaDecTask != null){
            mRaDecTask.cancel();
            mRaDecTask = null;
        }
        if (mRaDecTimer != null){
            mRaDecTimer.cancel();
            mRaDecTimer = null;
        }
    }

    @Override
    public void updateWifiName(String wifi_name) {
        if (wifi_name != null) {
            binding.bottom.leftTv.setText(wifi_name.replace("\"", ""));
        }
        if (wifi_name.equals("")){
            mGuider.setHeartBeatTimeOut();
            binding.leftLayout.raDecContainer.setVisibility(View.INVISIBLE);
            connectSuccess(false,false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: dockToGuideBtn");
        mLib = TpLib.getInstance();
        mLib.init();
        mLib.setPreviewHandler(mHandler);
        Log.e(TAG, "onCreate: enter  guidestream" );
        super.onCreate(savedInstanceState);
        binding = ActivityGuideBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mGuider = Guider.getInstance();
        mGuider.registerHandlerListener(mHandler);
        init();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart: dockToGuideBtn");
        super.onStart();
        mStreamFpsCnt = 0;
        mLib.setPreviewHandler(mHandler);
        mGuider.registerHandlerListener(mHandler);
        clearDockStarView();
        enterGuideFromInit();
        initDockView();
        keepDockStatus();
        keepDockStarVisible();
        checkFrameView();
        updateChartInformationView();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: dockToGuideBtn");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuider.unregisterHandlerListener(mHandler);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsNeedRestoreGuidingStatus = true;
        stopRaDecTask();
        stopFpsTimer();
        mGuider.unregisterHandlerListener(mHandler);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 100:{
                gotoProgress(data.getDoubleExtra("target_ra",0),data.getDoubleExtra("target_dec",0));
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void hideSoftInput(){
        if (getCurrentFocus() instanceof EditText) {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View view = getCurrentFocus(); //获得当前聚焦控件
                            if (view instanceof EditText) {
                                Rect rect = new Rect();
                                view.getGlobalVisibleRect(rect); //获得控件在屏幕上的显示区域
                                //判断：如果点击区域不在控件中
                                if (!rect.contains(mClickRawX, mClickRawY)) {
                                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.settingPanel.blankArea.getWindowToken(), 0);
                                    view.clearFocus(); //清除焦点
                                    Log.e(TAG, "run: cannotclick  点击区域不在控件中,清除焦点");
                                } else {
                                }
                            }else {
                                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.settingPanel.blankArea.getWindowToken(), 0);
                            }
                        }
                    });
                }
            }.start();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN : {
                LogUtil.writeTouchDelayFile("guide dispatchTouchEvent: ACTION_DOWN");
                if (getCurrentFocus() instanceof EditText) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(60);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    View view = getCurrentFocus(); //获得当前聚焦控件
                                    if (view instanceof EditText) {
                                        Rect rect =new Rect();
                                        view.getGlobalVisibleRect(rect); //获得控件在屏幕上的显示区域
                                        //判断：如果点击区域不在控件中
                                        if (!rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                                            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.settingPanel.blankArea.getWindowToken(),0);
                                            view.clearFocus(); //清除焦点
                                        }else {
                                            new Thread(){
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    try {
                                                        Thread.sleep(100);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            view.requestFocus();
                                                        }
                                                    });
                                                }
                                            }.start();
                                        }
                                    }
                                }
                            });
                        }
                    }.start();
                }
            }
        }
        LogUtil.writeTouchDelayFile("guide dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }
    //decision make area
    private void checkFrameView(){
        if (!mGuider.mGuideIsDeviceConnected){
            releaseStream();
            resetFrame();
        }
    }

    private void manageAppStateGuidingStatus(){
        setGuidingStatusMode(mGuider.mStarGuidingMode);
    }

    private void setDockStarCtrlView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mGuideStarProfileMode == 0){
                    dealTypeConflict(binding.dockStarPanel.midRowTbtn);
                }else if (mGuider.mGuideStarProfileMode == 1){
                    dealTypeConflict(binding.dockStarPanel.avgRowTbtn);
                }else if (mGuider.mGuideStarProfileMode == 2){
                    dealTypeConflict(binding.dockStarPanel.avgColTbtn);
                }else {
                }
            }
        });
    }

    private void enterGuideFromInit(){
        updateGuideDeviceTitle();
        setDockStarCtrlView();
        connectSuccess(mGuider.mIsNetworkOnLine,mGuider.mGuideIsDeviceConnected);
        if (mGuider.mGuideIsDeviceConnected){
            loadExpData();
            loadGainData();
            loadDitherData();
            parseResData();

            manageGuideCaptureStatus();
        }
        manageGuidingBtnStatus();
        manageGotoBtnStatus();
    }

    public void startLoadAllDataCache(){
        mCheckConnectProcess.showDataLoadingDialog();
    }
    public void finishLoadAllDataCache(){
        mCheckConnectProcess.dismissDataLoadingDialog();
    }

    public void enterGuideFromWorkState(){
        updateGuideDeviceTitle();
        connectSuccess(mGuider.mIsNetworkOnLine,mGuider.mGuideIsDeviceConnected);
        if (mGuider.mWorkStates.containsKey("guide")){
            if (mGuider.mGuideIsDeviceConnected){
                loadExpData();
                loadGainData();
                loadDitherData();
                parseResData();
            }else {
            }
            if (mGuider.mIsGuideRtspSource && mGuider.mIsGuideStartStreamDirectly) {
                startStream();
            }
        }
        manageGuidingBtnStatus();
        manageGotoBtnStatus();
    }

    private void loadGainData(){
        int gain = mGuider.mGuideGain;
        if (gain == -1) return;
        updateGainCache(gain);
    }

    private void updateGainCache(int gain){
        mCurrentGainIndex = gain;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateGainTitle(gain+"");
            }
        });
    }
    private void loadDitherData(){
        mCurrentDitherValue = mGuider.mGuideDitherScale;
        double mDither = mGuider.mGuideDitherScale;
        if (!mDitherDataList.contains(String.valueOf(mDither))){
            mEditDitherValue = String.format("%.1f", mDither);
            mCurrentDitherIndex = mDitherDataList.size();
        }
        else{
            mCurrentDitherIndex = mDitherDataList.indexOf(String.valueOf(mDither));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentDither = String.format("%.1f", mDither);
                updateDitherTitle(mCurrentDither);
            }
        });
    }

    private void updateExpEvent(int exposure){
        if (mExposureTime == exposure){
            return;
        }
        mExposureTime = mGuider.mGuideExpData;
        mCurrentExpIndex = mGuider.getmGuideExpDataList().indexOf(((double)mExposureTime/1000)+"s");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String temp = ((double)mExposureTime/1000)+"s";
                updateExpTitle(temp);
            }
        });
    }

    private void loadExpData(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentExpIndex = mGuider.getmGuideExpDataList().indexOf(((double)mGuider.mGuideExpData/1000)+"s");
                mExposureTime = mGuider.mGuideExpData;
                String temp = ((double)mGuider.mGuideExpData/1000)+"s";
                updateExpTitle(temp);
            }
        });
    }
    private void setGuidingStatusMode(int statusMode){
        manageScopeStatus();
        Log.e(TAG, "setGuidingStatusMode: "+statusMode );
        switch (statusMode){
            case STAR_IDLE:
            case STAR_GUIDING_STOPPED: {
                mIsStarGuiding = false;
                binding.rightLayout.guidingBtn.setChecked(false);
                updateGuidingTitle("");
                mGuider.mStarGuidingMode = statusMode;
                binding.crossLine.setVisibility(View.INVISIBLE);
                binding.selectRect.setVisibility(View.INVISIBLE);
                break;
            }
            case STAR_GUIDING:{
                updateGuidingTitle(getResources().getString(R.string.guiding_processing));
                mGuider.mStarGuidingMode = statusMode;
                binding.selectRect.setGuidingColor(GuidingRectView.SELECTED);
                binding.crossLine.setGuidingColor(GuidingCrossView.SELECTED);
                binding.crossLine.setLineDashed(false);
                binding.rightLayout.guidingBtn.setChecked(true);
                try {
                    loadStarPosition();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case STAR_GUIDING_STARTED:{
                mIsStarGuiding = true;
                mGuider.mStarGuidingMode = statusMode;

                updateGuidingTitle(getResources().getString(R.string.guiding_processing));
                int mBoundRight = (int) binding.surface.mRender.mBoundRight;
                int mBoundLeft = (int) binding.surface.mRender.mBoundLeft;
                int mBoundTop = (int) binding.surface.mRender.mBoundTop;
                int mBoundBottom = (int) binding.surface.mRender.mBoundBottom;
                CopyOnWriteArrayList<Double> selectedStar = mGuider.mStarSelected;
                if (selectedStar.size() != 0) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                ArrayList<Float> physicalLocation = getPhysicalLocation(selectedStar.get(0), selectedStar.get(1));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.crossLine.setNewLocation(physicalLocation.get(0).intValue(), (int) (physicalLocation.get(1) + mTitleHeight),
                                                mBoundLeft,mBoundRight, mBoundTop + mTitleHeight,mBoundBottom + mTitleHeight);
                                        binding.crossLine.setLineDashed(false);
                                        binding.crossLine.setGuidingColor(GuidingCrossView.SELECTED);
                                        binding.crossLine.setVisibility(View.VISIBLE);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
            }
            case STAR_LOST:{
                updateGuidingTitle(getResources().getString(R.string.guide_start_lost));
                mGuider.mStarGuidingMode = statusMode;
                if (!mIsStarGuiding) {
                    mIsStarLost = true;
                    binding.selectRect.setVisibility(View.INVISIBLE);
                }else
                {
                    binding.selectRect.setGuidingColor(GuidingRectView.SELECTING);
                }
                break;
            }
            case STAR_CALIBRATING:{
                binding.rightLayout.guidingBtn.setChecked(true);
                updateGuidingTitle(getResources().getString(R.string.calibrating));
                calibrationStartEvent();
                mGuider.mStarGuidingMode = statusMode;
                double x = mGuider.mCalibrating.mPos.mPosX;
                double y = mGuider.mCalibrating.mPos.mPosY;
                mTitleHeight = binding.title.statusTv.getHeight();
                binding.crossLine.setGuidingColor(GuidingCrossView.SELECTING);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            ArrayList<Float> physicalLocation = getPhysicalLocation(x,y);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.selectRect.setVisibility(View.VISIBLE);
                                    updateLogInfo("Event mCalibrating x "+":"+ x);
                                    updateLogInfo("Event mCalibrating y "+":"+ y);
                                    updateLogInfo("calibrationEvent 对应手机屏幕的位置  x "+":"+ physicalLocation.get(0));
                                    updateLogInfo("calibrationEvent 对应手机屏幕的位置  y "+":"+ (physicalLocation.get(1) + mTitleHeight));
                                    binding.selectRect.setNewLocation(physicalLocation.get(0).intValue(), (int) (physicalLocation.get(1) + mTitleHeight));
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            }
            case STAR_CALIBRATION_START:{
                mGuider.mStarGuidingMode = statusMode;
                /*十字线：(虚黄线)*/
                updateGuidingTitle(getResources().getString(R.string.calibrating));
                int mBoundRight = (int) binding.surface.mRender.mBoundRight;
                int mBoundLeft = (int) binding.surface.mRender.mBoundLeft;
                int mBoundTop = (int) binding.surface.mRender.mBoundTop;
                int mBoundBottom = (int) binding.surface.mRender.mBoundBottom;
                CopyOnWriteArrayList<Double> selectedStar = mGuider.mStarSelected;
                if (selectedStar.size() != 0) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                ArrayList<Float> physicalLocation = getPhysicalLocation(selectedStar.get(0), selectedStar.get(1));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateLogInfo("Event calibrationStartEvent x "+":"+ selectedStar.get(0));
                                        updateLogInfo("Event calibrationStartEvent y "+":"+ selectedStar.get(1));
                                        updateLogInfo("calibrationStartEvent 对应手机屏幕的位置  x "+":"+ physicalLocation.get(0));
                                        updateLogInfo("calibrationStartEvent 对应手机屏幕的位置  y "+":"+ (physicalLocation.get(1) + mTitleHeight));
                                        binding.crossLine.setNewLocation(physicalLocation.get(0).intValue(), (int) (physicalLocation.get(1) + mTitleHeight),
                                                mBoundLeft,mBoundRight, mBoundTop + mTitleHeight,mBoundBottom + mTitleHeight);
                                        binding.crossLine.setLineDashed(true);
                                        binding.crossLine.setGuidingColor(GuidingCrossView.SELECTING);
                                        binding.crossLine.setVisibility(View.VISIBLE);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
            }
            case STAR_CAPTURE_LOOPING:{
                if (mIsStarLost){
                    binding.selectRect.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case STAR_SELECTED:{
                updateGuidingTitle(getResources().getString(R.string.star_selected));
                mIsStarLost = false;
                mGuider.mStarGuidingMode = statusMode;
                mTitleHeight = binding.title.statusTv.getHeight();
                CopyOnWriteArrayList<Double> selectedStar = mGuider.mStarSelected;
                if (selectedStar.size() != 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                ArrayList<Float> physicalLocation = getPhysicalLocation(selectedStar.get(0), selectedStar.get(1));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.selectRect.setVisibility(View.VISIBLE);

                                        updateLogInfo("Event StarSelect x "+":"+ selectedStar.get(0));
                                        updateLogInfo("Event StarSelect y "+":"+ selectedStar.get(1));
                                        updateLogInfo("StarSelect 对应手机屏幕的位置  x "+":"+ physicalLocation.get(0));
                                        updateLogInfo("StarSelect 对应手机屏幕的位置  y "+":"+ (physicalLocation.get(1) + mTitleHeight));
                                        binding.selectRect.setNewLocation(physicalLocation.get(0).intValue(), (int) (physicalLocation.get(1) + mTitleHeight));
                                        binding.selectRect.setGuidingColor(GuidingRectView.SELECTED);
                                        Log.e(TAG, "run: setGuidingStatusMode x 是"+physicalLocation.get(0).intValue()
                                        +"  y 是"+(int) (physicalLocation.get(1) + mTitleHeight));
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }else {
                    Log.e(TAG, "setGuidingStatusMode: selectedStar.size()"+selectedStar.size() );
                }
                break;
            }
            case STAR_CALIBRATION_FAILED:{
                binding.rightLayout.guidingBtn.setChecked(false);
                mGuider.mStarGuidingMode = statusMode;
                binding.selectRect.setVisibility(View.INVISIBLE);
                binding.crossLine.setVisibility(View.INVISIBLE);
                break;
            }
        }
    }

    private void manageGotoBtnStatus(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                manageScopeStatus();
            }
        });

        if (mGuider.mScopeIsDeviceConnected){
            startRaDecTask();
        }else {
            stopRaDecTask();
        }
    }
    private void manageGuidingBtnStatus(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mGuideIsDeviceConnected && mGuider.mScopeIsDeviceConnected){
                    binding.rightLayout.guidingBtn.setEnabled(true);
                    binding.rightLayout.guidingBtn.setChecked(mGuider.getGuidingState().equals("GuidingStarted") || mGuider.getGuidingState().equals("GuidingResumed"));
                }else {
                    binding.rightLayout.guidingBtn.setEnabled(false);
                }
            }
        });
    }

    //init area
    private void init(){
        mFromRoute = getIntent().getStringExtra(FROM_TAG);
        initView();
        initData();
        initEvent();
        dockCtrlAreaEvent();
        mEditDitherValue = SharedPreferencesUtil.getInstance().getString(Constant.SP_DITHER_SCALE_ID,"20.0");
        mEditExpValue = SharedPreferencesUtil.getInstance().getString(Constant.SP_GUIDE_EXP_EDIT_ID,"1.0s");
    }

    private void initDockView(){
        binding.dockGuideChartPanel.dxDyCb.setChecked(mGuider.mIsDxDyVisibility);
        binding.dockGuideChartPanel.raDecCb.setChecked(mGuider.mIsRaDecVisibility);
        binding.dockGuideChartPanel.snrCb.setChecked(mGuider.mIsSnrVisibility);
        binding.dockGuideChartPanel.masCb.setChecked(mGuider.mIsMassVisibility);
    }

    private void initView(){
        connectSuccess(mGuider.mIsNetworkOnLine,mGuider.mGuideIsDeviceConnected);
        //resetViewForNetworkReconnect();
        mUnParkBuilder = new ParkDialog.Builder(GuideActivity.this)
                .setmMessageContent(getResources().getString(R.string.current_is_park))
                .setmMessageCancelContent(getResources().getString(R.string.cancel_unpark))
                .setmMessageConfirmContent(getResources().getString(R.string.confirm_unpark));
        mUnParkDialog = mUnParkBuilder.create();
        mLogBuilder = new LogDialog.Builder(GuideActivity.this);
        mLogDialog = mLogBuilder.create();
        initSettingPanel();
        initChart();
        initPopMenu();
        initDockView();
        updateChartInformationView();

        try {
            initFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.dockChartLabelTv.setText(getResources().getString(R.string.chart_label));
        binding.dockStarPanel.chart.showXAxis(false);
        binding.dockStarPanel.chart.showYAxis(false);
    }

    private void initPopMenu(){
        mGainDataList.clear();
        for (int i = 1;i < 101;i++){
            mGainDataList.add(""+i);
        }

        mDitherDataList.clear();
        mDitherDataList.add("0.1");
        mDitherDataList.add("0.2");
        mDitherDataList.add("0.3");
        mDitherDataList.add("0.4");
        mDitherDataList.add("0.5");
        mDitherDataList.add("0.6");
        mDitherDataList.add("0.7");
        mDitherDataList.add("0.8");
        mDitherDataList.add("0.9");
        mDitherDataList.add("1.0");
        mDitherDataList.add("5.0");
        mDitherDataList.add("10.0");
        mDitherDataList.add("20.0");
        mDitherDataList.add("30.0");
        mDitherDataList.add("40.0");
        mDitherDataList.add("50.0");
        mDitherDataList.add("60.0");
        mDitherDataList.add("70.0");
        mDitherDataList.add("80.0");
        mDitherDataList.add("90.0");
        mDitherDataList.add("100.0");
       // updateDitherTitle(mDitherDataList.get(mCurrentDitherIndex));

        mClickMenuAdapter = new ClickMenuAdapter(mClickMenuList,this);
        mClickMenuAdapter.setOnClickListener(new ClickMenuAdapterListener() {
            @Override
            public void chooseItem(int position) {
                menuClickEvent(position);
            }

            @Override
            public void editAction() {
                if (binding.rightLayout.ditherTbtn.isChecked()){
                    binding.editExpEt.getText().clear();
                    binding.editExpEt.setText(mEditDitherValue);
                    binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.setDitherScale(Float.valueOf(mEditDitherValue));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }else if (binding.rightLayout.expTbtn.isChecked()){
                    binding.editExpEt.getText().clear();
                    binding.editExpEt.setText(mEditExpValue.replace("s",""));
                    binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                int exposure = (int) (getFloatNumberNoUnit(mEditExpValue) * 1000);
                                mGuider.setExposure("guide", exposure);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                binding.editExpContainer.setVisibility(View.VISIBLE);
                binding.editExpEt.requestFocus();
                showSoftInputNoEvent(binding.editExpEt,true,true);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        binding.clickMenuRv.setLayoutManager(linearLayoutManager);
        binding.clickMenuRv.setAdapter(mClickMenuAdapter);
    }

    private void menuClickEvent(int position){
        if (binding.rightLayout.expTbtn.isChecked()){
            mCurrentExpIndex = position;
            String text = mClickMenuList.get(position).mValue;
            mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp)+": "+text;
            Log.e(TAG, "run11: exp"+text +position);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateExpTitle(text);
                }
            });
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        mExposureTime = (int) (getFloatNumberNoUnit(text) * 1000);
                        mGuider.setExposure("guide",mExposureTime);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }else if (binding.rightLayout.ditherTbtn.isChecked()){
            mCurrentDitherIndex = position;
            String text = mClickMenuList.get(position).mValue;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDitherTitle(text);
                }
            });
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    mGuider.mGuideDitherScale = Double.valueOf(text);
                    mCurrentDitherValue = mGuider.mGuideDitherScale;
                    mGuider.setDitherScale((float) mGuider.mGuideDitherScale);
                }
            }.start();
        }else if (binding.rightLayout.resTbtn.isChecked()){
            mCurrentResIndex = position;

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        mGuider.setPushResolution("guide",position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {

        }
    }
    private void initGoto(){
        mGoToPop = new GotoPop(GuideActivity.this);
        mGoToPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mGoToPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }



    private void initData(){
        mToggleBtnList.clear();
        mToggleBtnList.add(binding.rightLayout.gainTbtn);
        mToggleBtnList.add(binding.rightLayout.expTbtn);
        mToggleBtnList.add(binding.rightLayout.ditherTbtn);
        mToggleBtnList.add(binding.rightLayout.resTbtn);

        mRadioBtnList.clear();
        mRadioBtnList.add(binding.dockStarPanel.avgColTbtn);
        mRadioBtnList.add(binding.dockStarPanel.avgRowTbtn);
        mRadioBtnList.add(binding.dockStarPanel.midRowTbtn);
    }

    private void hideMenuView(){
        binding.rightLayout.gainTbtn.setChecked(false);
        binding.rightLayout.expTbtn.setChecked(false);
        binding.rightLayout.ditherTbtn.setChecked(false);
        binding.rightLayout.resTbtn.setChecked(false);
    }

    private void dealTypeConflict(ToggleButton toggleButton){
        mToggleBtnList.remove(toggleButton);
        for (ToggleButton it:mToggleBtnList){
            it.setChecked(false);
        }
        mToggleBtnList.add(toggleButton);
    }

    private void exitActivity(){
        if (mFromRoute.equals("main")){
            releaseStream();
            mGuider.unregisterHandlerListener(mHandler);
            mLib.setPreviewHandler(null);
            finish();
        }else if (mFromRoute.equals("device")){
            releaseStream();
            mGuider.unregisterHandlerListener(mHandler);
            mLib.setPreviewHandler(null);
            ScreenMainActivity.actionStart(GuideActivity.this);
            finish();
        }
    }

    private void showGotoPop(){
        Rect rect = new Rect();
        binding.leftLayout.goToBtn.getGlobalVisibleRect(rect);
        int y = (int) (rect.bottom  - binding.leftLayout.goToBtn.getHeight()/2 - mGoToPop.getContentView().getMeasuredHeight()/2);
        mGoToPop.showAtLocation( binding.leftLayout.goToBtn, Gravity.TOP | Gravity.LEFT,
                binding.leftLayoutContainer.getWidth()+ScreenUtil.androidAutoSizeDpToPx(getResources().getInteger(R.integer.pop_margin_side)),y);
    }
    private void initEvent(){
        binding.dockStarPanel.midRowTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealTypeConflict(binding.dockStarPanel.midRowTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.setStarProfileMode(0);
                    }
                }.start();
            }
        });
        binding.dockStarPanel.avgRowTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealTypeConflict(binding.dockStarPanel.avgRowTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.setStarProfileMode(1);
                    }
                }.start();
            }
        });
        binding.dockStarPanel.avgColTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealTypeConflict(binding.dockStarPanel.avgColTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.setStarProfileMode(2);
                    }
                }.start();
            }
        });

        binding.editExpEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.editExpEt.clearFocus();
                ((InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.editExpEt.getWindowToken(),0);
                return true;
            }
        });
        binding.editExpEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    if (binding.rightLayout.expTbtn.isChecked()) {
                        binding.editExpContainer.setVisibility(View.INVISIBLE);
                        String expValue = binding.editExpEt.getText().toString();
                        Log.e(TAG, "onFocusChange: replace expValue before"+expValue );
                        expValue = expValue.replace("s","");
                        Log.e(TAG, "onFocusChange: replace expValue after"+expValue );
                        if (CheckParamUtil.isRangeLegal(GuideActivity.this,expValue, (double) mGuider.mGuiderExpMin, (double) mGuider.mGuiderExpMax,1000.0,"s")){
                            Double value = Double.valueOf(expValue);
                            String temp = String.format("%.3f", value);
                            String strPrice = Double.parseDouble(temp)+"";
                            binding.editExpEt.getText().clear();
                            binding.editExpEt.setText(strPrice);
                            expValue = strPrice;

                            if (expValue.contains(".")) {
                                expValue += "s";
                            } else {
                                expValue += ".0s";
                            }
                            ArrayList<String> expDataList = mGuider.getmGuideExpDataList();
                            mEditExpValue = expValue;
                            SharedPreferencesUtil.getInstance().putString(Constant.SP_GUIDE_EXP_EDIT_ID, expValue);
                            String text = expValue;
                            updateExpTitle(text);
                            mClickMenuList.clear();
                            for (String item : expDataList) {
                                mClickMenuList.add(new ClickMenuItem(item, true));
                            }
                            mClickMenuList.add(new ClickMenuItem(mEditExpValue, true));
                            mCurrentExpIndex = mClickMenuList.size() - 1;
                            mClickMenuAdapter.setSelectedIndex(mCurrentExpIndex);
                            mClickMenuAdapter.notifyDataSetChanged();
                            binding.clickMenuRv.moveToCenterPosition(mCurrentExpIndex);
                            binding.editExpContainer.setVisibility(View.INVISIBLE);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        mExposureTime = (int) (getFloatNumberNoUnit(text) * 1000);
                                        mGuider.setExposure("guide", mExposureTime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    }
                    else if(binding.rightLayout.ditherTbtn.isChecked()){
                        binding.editExpContainer.setVisibility(View.INVISIBLE);
                        String ditherValue = binding.editExpEt.getText().toString();
                        if (CheckParamUtil.isRangeLegal(GuideActivity.this,ditherValue,0.1,100.0,1.0,"")){
                            Double value = Double.valueOf(ditherValue);
                            String strPrice = String.format("%.1f",value);//返回字符串
                            binding.editExpEt.getText().clear();
                            binding.editExpEt.setText(strPrice);
                            ditherValue = strPrice;
                            Double ditherDoubleValue = Double.valueOf(ditherValue);
                            mCurrentDitherValue = ditherDoubleValue;
                            mEditDitherValue = String.format("%.1f", ditherDoubleValue);
                            SharedPreferencesUtil.getInstance().putString(Constant.SP_DITHER_SCALE_ID, String.valueOf(ditherDoubleValue));
                            String text = String.format("%.1f", ditherDoubleValue);
                            updateDitherTitle(text);
                            mClickMenuList.clear();
                            for (String item : mDitherDataList) {
                                mClickMenuList.add(new ClickMenuItem(item, true));
                            }
                            mClickMenuList.add(new ClickMenuItem(mEditDitherValue, true));
                            mCurrentDitherIndex = mClickMenuList.size() - 1;
                            mClickMenuAdapter.setSelectedIndex(mCurrentDitherIndex);
                            mClickMenuAdapter.notifyDataSetChanged();
                            binding.clickMenuRv.moveToCenterPosition(mCurrentDitherIndex);
                            binding.editExpContainer.setVisibility(View.INVISIBLE);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        mGuider.mGuideDitherScale = ditherDoubleValue;
                                        mGuider.setDitherScale(ditherDoubleValue.floatValue());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    }

                    int index = mClickMenuList.size() - 1;
                    mClickMenuAdapter.setSelectedIndex(index);
                    mClickMenuAdapter.notifyDataSetChanged();
                    binding.clickMenuRv.moveToCenterPosition(index);
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.clickMenuRv.scrollToPosition(mClickMenuList.size()+1);
                                }
                            });
                        }
                    }.start();
                }
            }
        });
        binding.rightLayout.logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrueLogInfo();
                mLogDialog.show();
            }
        });

        binding.seekBar.setOnSeekBarChangeListener(new ProgressBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                if (binding.rightLayout.gainTbtn.isChecked()){
                    updateGainTitle(progress+"");
                    binding.seekBar.setType(getResources().getString(R.string.Gain));
                    binding.seekBar.setBarDegree(progress+"");
                }
                mSeekBarIndex = progress;
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {
                if(binding.rightLayout.gainTbtn.isChecked()){
                    mCurrentGainIndex = mSeekBarIndex;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.setGain("guide",mCurrentGainIndex);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
        binding.rightLayout.ditherTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dealTypeConflict(binding.rightLayout.ditherTbtn);
                LogUtil.writeTouchDelayFile("guide ditherTbtn isChecked:"+b);
                if (b){
                    binding.clickMenuRvContainer.setVisibility(View.VISIBLE);
                    mClickMenuList.clear();
                    for (String item : mDitherDataList) {
                        mClickMenuList.add(new ClickMenuItem(item,true));
                    }
                    binding.editExpEt.getText().clear();
                    binding.editExpEt.setText(mEditDitherValue);
                    binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                    mClickMenuList.add(new ClickMenuItem(mEditDitherValue,true));
                    mClickMenuAdapter.setSelectedIndex(mCurrentDitherIndex);
                    mClickMenuAdapter.notifyDataSetChanged();
                    binding.clickMenuRv.moveToCenterPosition(mCurrentDitherIndex);
                }else {
                    binding.clickMenuRvContainer.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.rightLayout.expTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dealTypeConflict(binding.rightLayout.expTbtn);
                LogUtil.writeTouchDelayFile("guide expTbtn isChecked:"+b);
                if (b){
                    binding.clickMenuRvContainer.setVisibility(View.VISIBLE);
                    mClickMenuList.clear();

                    ArrayList<String> tempGuideExpDataList = mGuider.getmGuideExpDataList();
                    for (String item : tempGuideExpDataList) {
                        mClickMenuList.add(new ClickMenuItem(item,true));
                    }
                    String mTempExposureTime = (((float)mExposureTime) / 1000) + "s";
                    if (tempGuideExpDataList.contains(mTempExposureTime)){
                        if (mCurrentExpIndex == -1) {
                            mCurrentExpIndex = tempGuideExpDataList.indexOf(mTempExposureTime);
                        }
                        mClickMenuList.add(new ClickMenuItem(mEditExpValue,true));
                    }else {
                        mEditExpValue = mTempExposureTime;
                        mClickMenuList.add(new ClickMenuItem(mEditExpValue,true));
                        if (mCurrentExpIndex == -1) {
                            mCurrentExpIndex = mClickMenuList.size() - 1;
                        }
                    }
                    String editSelection = mEditExpValue;
                    binding.editExpEt.getText().clear();
                    binding.editExpEt.setText(editSelection);
                    binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                    mClickMenuAdapter.setSelectedIndex(mCurrentExpIndex);
                    mClickMenuAdapter.notifyDataSetChanged();
                    binding.clickMenuRv.moveToCenterPosition(mCurrentExpIndex);
                }else {
                    binding.clickMenuRvContainer.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.rightLayout.gainTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dealTypeConflict(binding.rightLayout.gainTbtn);
                LogUtil.writeTouchDelayFile("guide gainTbtn isChecked:"+b);
                if (b){
                    binding.seekbarContainer.setVisibility(View.VISIBLE);
                    binding.seekBar.setBarMax(100);
                    binding.seekBar.setProgress(mCurrentGainIndex);
                }else{
                    binding.seekbarContainer.setVisibility(View.GONE);
                }
            }
        });

        binding.rightLayout.resTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealTypeConflict(binding.rightLayout.resTbtn);
                LogUtil.writeTouchDelayFile("guide resTbtn isChecked:"+isChecked);
                if (isChecked){
                    Log.e(TAG, "onCheckedChanged: guide resTbtn isChecked:VISIBLE"+mResDataList.toString());
                    binding.clickMenuRvContainer.setVisibility(View.VISIBLE);
                    mClickMenuList.clear();
                    for (String item : mResDataList) {
                        mClickMenuList.add(new ClickMenuItem(item));
                    }
                    mClickMenuAdapter.setSelectedIndex(mCurrentResIndex);
                    mClickMenuAdapter.notifyDataSetChanged();
                    binding.clickMenuRv.moveToCenterPosition(mCurrentResIndex);
                }else {
                    binding.clickMenuRvContainer.setVisibility(View.GONE);
                }
            }
        });

        binding.title.backGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });
        binding.title.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });

        binding.rightLayout.guidingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMenuView();
                if (binding.rightLayout.guidingBtn.isChecked()){
                    JSONObject params = new JSONObject();
                    try {
                        params.put("pixels",2.0);
                        params.put("time",10);
                        params.put("timeout",10);
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                boolean result = mGuider.guide(params,false);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!result){
                                            binding.rightLayout.guidingBtn.setChecked(false);
                                        }
                                    }
                                });
                            }
                        }.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            boolean result = mGuider.stopGuide();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (result) {
                                        setGuidingStatusMode(STAR_GUIDING_STOPPED);
                                    }else {
                                        binding.rightLayout.guidingBtn.setChecked(true);
                                    }
                                }
                            });
                        }
                    }.start();
                }
            }
        });

        binding.leftLayout.starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetToInitStatus();
                if (binding.starPanelContainer.getVisibility() == View.VISIBLE)
                {
                    setDockStarVisible(false);
                }else {
                    setDockStarVisible(true);
                }
            }
        });

        binding.leftLayout.guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetToInitStatus();
                updateChartInformationView();
                if (!mPanelList.contains(PANEL_C)) {
                    mPanelList.add(PANEL_C);
                    binding.guideChartContainer.setVisibility( View.VISIBLE);
                    if (mLastMode == MODE_SPLIT) {
                        setDockMode(MODE_CHART_SINGLE);
                        setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
                    }else {
                        setDockMode(MODE_CHART_SINGLE);
                        setDockGuidePanelFlag("");
                        binding.dockChartLabelTv.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    mPanelList.clear();

                    binding.guideChartContainer.setVisibility(View.GONE);
                    hideAllGuideDockPanelView();
                }
                setTabSwitchBg(mLastMode);

                saveDockStatus();
            }
        });

        binding.clickMenuRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0)) > 0){
                    binding.menuRvTopCover.setVisibility(View.VISIBLE);
                }else {
                    binding.menuRvTopCover.setVisibility(View.GONE);
                }

                if  (recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1)) < recyclerView.getAdapter().getItemCount() - 1){
                    binding.menuRvBottomCover.setVisibility(View.VISIBLE);
                }else {
                    binding.menuRvBottomCover.setVisibility(View.GONE);
                }
            }
        });
        binding.leftLayout.goToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetToInitStatus();
                if (mGoToPop == null){
                    initGoto();
                }
                mGoToPop.refreshFavoriteView();
                if (mGuider.mScopePark){
                    if (mUnParkDialog != null) {
                        mUnParkDialog.show();
                    }
                }else {
                    mGoToPop.setGotoPark(false);
                    showGotoPop();
                }
            }
        });

        binding.leftLayout.rootBrowserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLib.setPreviewHandler(null);
                mGuider.unregisterHandlerListener(mHandler);
                BrowserActivity.actionStart(GuideActivity.this,"root");
            }
        });

        binding.leftLayout.settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.settingPanelContainer.getVisibility() == View.VISIBLE) {
                    hideAllSettingPanel();
                }
                else {
                    try {
                        initFragment();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    binding.settingPanelContainer.setVisibility(View.VISIBLE);
                    resetToInitStatus();
                }
            }
        });

        binding.dockTabSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPanelMode == MODE_SPLIT) {
                    mLastMode = MODE_TAB;
                    setDockTabMode(mDockCurrentTabId);
                } else if (mPanelMode == MODE_TAB) {
                    mLastMode = MODE_SPLIT;
                    setDockMode(MODE_SPLIT);
                } else if (mPanelMode == MODE_STAR_SINGLE) {
                    dockSingleSSplitTabChange();
                } else if (mPanelMode == MODE_CHART_SINGLE) {
                    dockSingleCSplitTabChange();
                }else if(mPanelMode == MODE_SINGLE){
                    if (mPanelList.contains(PANEL_S)){
                        dockSingleSSplitTabChange();
                    }else {
                        dockSingleCSplitTabChange();
                    }
                }
                saveDockStatus();
            }
        });

        binding.dockCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mPanelMode) {
                    case MODE_SPLIT : {
                        hideAllGuideDockPanelView();
                        mPanelList.clear();
                    }
                    case MODE_CHART_SINGLE :
                    case MODE_STAR_SINGLE :
                    case MODE_SINGLE : {
                        hideAllGuideDockPanelView();
                        mPanelList.clear();
                    }
                    case MODE_TAB : {
                        if (mPanelList.size() == 1) {
                            hideAllGuideDockPanelView();
                            mPanelList.clear();
                        }
                    }
                }
                binding.leftLayout.guideBtn.setChecked(binding.guideChartContainer.getVisibility() == View.VISIBLE);
                saveDockStatus();
            }
        });

        binding.dockChartLabelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDockCurrentTabId = 1;
                binding.dockChartLabelTv.setChecked(true);

                binding.guideChartContainer.setVisibility(View.VISIBLE);
                saveDockStatus();
            }
        });

        binding.closeSettingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAllSettingPanel();
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                binding.surface.mRender.scale(scaleGestureDetector.getScaleFactor(), scaleGestureDetector.getFocusX(),
                        scaleGestureDetector.getFocusY());

            }
        }) ;
        mGestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                Float[] clickPos = getClickPosition(e);
/*                if (isClickInScaleArea(e!!)) {
                    val point = getScalePoint(e!!, 15f, 15f, xScalePoint, yScalePoint)
                    loge("point", point[0].toString() + "ypoint is " + point[1].toString())
                    return true
                }*/

                updateLogInfo("MotionEvent: getX "+":"+e.getX());
                updateLogInfo("MotionEvent: getY "+":"+e.getY());
                updateLogInfo("对应不放缩的位置X "+":"+ clickPos[0].toString());
                updateLogInfo("对应不放缩的位置Y "+":"+ clickPos[1].toString());
                Log.d("MotionEvent: ex ", ""+e.getX());
                Log.d("MotionEvent: ey ", ""+e.getY());
                Log.d("getClickPosition ex", clickPos[0].toString());
                Log.d("getClickPosition ey", clickPos[1].toString());
                if (e != null /*&& guider.isConnected*/) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                float x = clickPos[0] - binding.surface.mRender.mBoundLeft;
                                float y = clickPos[1] - binding.surface.mRender.mBoundTop;
                                if (x < 0 || clickPos[0] > binding.surface.mRender.mBoundRight || y < 0 || clickPos[1] > binding.surface.mRender.mBoundBottom) {
                                    Log.e(TAG, "out of bound ${e.x} ${e.y} $x $y");
                                    return;
                                }
                                float[] points = new float[]{x,y};
                                binding.surface.mRender.mapPoints(points);
                                mPt.x = points[0];
                                mPt.y = points[1];
                                // guider.setLockPosition(points[0], points[1], true)
                                mGuider.findStarByPos(mPt);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                super.onLongPress(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                binding.surface.mRender.trans(-distanceX, distanceY);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                binding.surface.mRender.resetMatrix();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mIsNumberInputShow){
                    mIsNumberInputShow = false;
                }
                else if (binding.seekbarContainer.getVisibility() == View.VISIBLE || binding.clickMenuRvContainer.getVisibility() == View.VISIBLE){
                    resetToInitStatus();
                }
                else if (binding.rightLayoutContainer.getVisibility() == View.VISIBLE) {
                    binding.rightLayoutContainer.setVisibility(View.GONE);
                    binding.leftLayoutContainer.setVisibility(View.GONE);
                    if (mGoToPop != null) {
                        mGoToPop.dismiss();
                    }

                } else {
                    binding.rightLayoutContainer.setVisibility(View.VISIBLE);
                    binding.leftLayoutContainer.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });

        mUnParkBuilder.setOnNormalClickListener(new ParkDialog.OnNormalClickListener() {
            @Override
            public void sure() {
                showGotoPop();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.setScopePark(false);
                    }
                }.start();
                mGoToPop.setGotoPark(false);
                mUnParkDialog.dismiss();
            }

            @Override
            public void cancel() {
                mUnParkDialog.dismiss();
                showGotoPop();
                mGoToPop.setGotoPark(true);
            }
        });
    }
    private void keepDockStatus(){
        mPanelList.clear();
        mLastMode = mGuider.mGuideDockStatus.mGuideDockMode;
        if (mGuider.mGuideDockStatus.mGuideDockNum == 1){
            if (mGuider.mGuideDockStatus.mGuideDockMode == MODE_SPLIT){
                if (mGuider.mGuideDockStatus.mGuideDockSelectedItem == PANEL_C) {
                    mPanelList.add(PANEL_C);
                    setDockMode(MODE_CHART_SINGLE);
                    setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
                    binding.leftLayout.guideBtn.setChecked(true);
                }
                setTabSwitchBg(MODE_SPLIT);
            }else {
                if (mGuider.mGuideDockStatus.mGuideDockSelectedItem == PANEL_C) {
                    mPanelList.add(PANEL_C);
                    setDockMode(MODE_CHART_SINGLE);
                    binding.dockChartLabelTv.setVisibility(View.VISIBLE);
                    setDockGuidePanelFlag("");
                    binding.leftLayout.guideBtn.setChecked(true);
                }
                setTabSwitchBg(MODE_TAB);
            }
        }
    }

    private void saveDockStatus(){
        mGuider.mGuideDockStatus.mGuideDockNum = mPanelList.size();
        mGuider.mGuideDockStatus.mGuideDockMode = mLastMode;
        int selectedDock = 0;
        if(mLastMode == MODE_TAB) {
            if (binding.dockChartLabelTv.isChecked()) {
                selectedDock = PANEL_C;
            }
        }else {
            if (binding.leftLayout.guideBtn.isChecked()){
                selectedDock = PANEL_C;
            }
        }
        mGuider.mGuideDockStatus.mGuideDockSelectedItem = selectedDock;
    }

    private void setTabSwitchBg(int currentMode){
        if (currentMode == MODE_SPLIT){
            binding.dockTabSwitchBtn.setBackground(getDrawable(R.drawable.ic_split_normal));
        }else {
            binding.dockTabSwitchBtn.setBackground(getDrawable(R.drawable.ic_tab_normal));
        }
    }
    private void dockSingleSSplitTabChange(){
        if (mLastMode == MODE_SPLIT){
            binding.dockChartLabelTv.setVisibility(View.GONE);
            mLastMode = MODE_TAB;
        }else {
            mLastMode = MODE_SPLIT;
        }
        setTabSwitchBg(mLastMode);
    }
    private void dockSingleCSplitTabChange(){
        if (mLastMode == MODE_SPLIT){
            binding.dockChartLabelTv.setVisibility(View.VISIBLE);
            binding.dockChartLabelTv.setChecked(true);
            setDockGuidePanelFlag("");
            mLastMode = MODE_TAB;
        }else {
            binding.dockChartLabelTv.setVisibility(View.GONE);
            setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
            mLastMode = MODE_SPLIT;
        }
        setTabSwitchBg(mLastMode);
    }



    private void setDockGuidePanelFlag(String value){
        if (value.equals("")){
            binding.dockGuideChartPanel.panelFlagTv.setVisibility(View.GONE);
        }else {
            binding.dockGuideChartPanel.panelFlagTv.setVisibility(View.VISIBLE);
        }
        binding.dockGuideChartPanel.panelFlagTv.setText(value);
    }

    public void resetFrame(){
        LogUtil.writeLogtoFile("guide resetFrame");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setGuidingStatusMode(STAR_IDLE);
                ByteBuffer mDirectBuffer = binding.surface.mRender.getDirectBuffer();
                if (mDirectBuffer != null){
                    if (mLib != null) {
                        mLib.clearFrame(mDirectBuffer, mDirectBuffer.capacity());
                    }else {
                        Log.e(TAG, "run: guide resetFrame mLib is null" );
                    }
                }else {
                    Log.e(TAG, "run: guide resetFrame mDirectBuffer is null" );
                }
                binding.surface.requestRender();
            }
        });
    }
    private void gotoProgress(double target_ra,double target_dec){
        mTargetRA = target_ra;
        mTargetDec = target_dec;
        mGotoBuilder =  new GotoProcessDialog.Builder(this)
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGotoProcessDialog.show();
                        }
                    });
                    boolean result = mGuider.guideCoord(true, mTargetRA, mTargetDec);
                    if (!result){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mGotoProcessDialog.dismiss();
                                ToastUtil.showToast(getResources().getString(R.string.can_not_goto_target),true);
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


    private class CoordTimerTask extends TimerTask {
        @Override
        public void run() {
            updateRaDecView();
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

    private void resetToInitStatus(){
        binding.rightLayout.gainTbtn.setChecked(false);
        binding.rightLayout.expTbtn.setChecked(false);
        binding.rightLayout.ditherTbtn.setChecked(false);
        binding.rightLayout.resTbtn.setChecked(false);
        Log.e(TAG, "resetToInitStatus: guide resTbtn isChecked:GONE");
        binding.clickMenuRvContainer.setVisibility(View.GONE);
        binding.seekbarContainer.setVisibility(View.GONE);
    }
    private void initFragment() throws IllegalAccessException, InstantiationException {
        if (mFragments.isEmpty()) {
            for (TabItem it: mTabs) {
                Fragment f = it.mFragmentCls.newInstance();
                mFragments.add(f);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (Fragment it: mFragments){
                if (!it.isAdded()) transaction.add(
                        R.id.fl_content, it, it.getClass().getSimpleName()
                ).hide(it);
            }
            transaction.commit();
            initTab(mGuider.mSettingCurrentPosition);
        }else {
            initTab(mGuider.mSettingCurrentPosition);
        }
    }
    private void initSettingPanel(){

        //  initFragment()

        binding.settingPanel.blankArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.settingPanel.settingPageLayout.setMenuItemIconsAndTexts();
        binding.settingPanel.settingPageLayout.setOnMenuItemClickListener(new SettingPageMenuLayout.OnMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                mGuider.mSettingCurrentPosition = pos;
                initTab(mGuider.mSettingCurrentPosition);
            }
        });

    }
    private void initTab(int position) {
        binding.settingPanel.settingPageLayout.setCurrentPosition(position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0;i < mFragments.size();i++){
            if (i == position) {
                transaction.show(mFragments.get(i));
            } else {
                transaction.hide(mFragments.get(i));
            }
        }
        transaction.commit();
    }

    private void hideFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment it: mFragments){
            if (it.isVisible()) transaction.hide(it);
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideAllSettingPanel(){
        binding.settingPanelContainer.setVisibility(View.INVISIBLE);
        hideFragment();
    }

    private void startStream() {
        if (mLib != null){
            if (mLib.isAlive() == false) {
                mLib.OpenCamera(mGuider.host);
                mLib.startCameraStream();
            } else {
                Log.e(TAG, "stream is playing guidestream");
            }
        }
        LogUtil.writeLogtoFile("guide startStream: guidestream");
    }

    public void update(int width, int height) {
        Log.e(TAG, "update: frame come  guidestream" );
       // LogUtil.writeWarnErrorToFile("update: frame come  guidestream");
        if(!mGuider.mGuideIsDeviceConnected || !mGuider.mIsGuideRtspSource){
            return;
        }
        mStreamRestartCnt = 0;
        if (mFpsTimerTask == null){
            startFpsTimer();
        }
        if (mWidth != width || mHeight != height) {
            initSize(width, height);
            mWidth = width;
            mHeight = height;
            mResX = (double)width;
            mResY = (double)height;
            mCurrentRes = width + "x" + height;
            Log.e(TAG, "update : " + "width: " + width + "height" + height);
        }

        mStreamFpsCnt ++;

        if (mWidth != 0 && mHeight != 0) {
            updateFrame();
        }
        if (mIsNeedRestoreGuidingStatus){
            if (mResX != 0 && mResY != 0 && (binding.surface.mRender.mNewPoints[2] - binding.surface.mRender.mNewPoints[0]) != 0) {
                mIsNeedRestoreGuidingStatus = false;
                manageAppStateGuidingStatus();
            }
        }
    }

    private void updateFrame(){
        ByteBuffer mDirectBuffer = binding.surface.mRender.getDirectBuffer();
        if (mDirectBuffer != null){
            if (mLib != null) {
                mLib.updateJFrame(mDirectBuffer);
            }else {
                Log.e(TAG, "updateFrame: mLib is null");
            }
        }else {
            Log.e(TAG, "updateFrame: mDirectBuffer is null");
        }
        binding.surface.requestRender();
    }

    private void initSize(int videoWidth, int videoHeight) {
        binding.surface.mRender.setCurrentVideoSize(videoWidth, videoHeight) ;// todo
    }
    public void warnError(){
        try {
            if (mLib != null) {
                if (mLib.isAlive() == true){
                    if (mStreamRestartCnt == 0) {
                        Log.e(TAG, "warnError restart stream");
                        LogUtil.writeGuideFps0ToFile("导星相机 拉流第一次重启 开始调用releaseStream() 当前fps计数为"+mStreamFpsCnt);
                        releaseStream();
                        LogUtil.writeGuideFps0ToFile("导星相机 拉流第一次重启 开始调用startStream() 当前fps计数为"+mStreamFpsCnt);
                        startStream();
                        LogUtil.writeGuideFps0ToFile("导星相机 拉流第一次重启结束 当前fps计数为"+mStreamFpsCnt);
                        mStreamRestartCnt ++;
                    }else {
                        LogUtil.writeGuideFps0ToFile("导星相机 拉流第一次重启 当前fps计数为"+mStreamFpsCnt);
                        releaseStream();
                        ToastUtil.showToast(App.INSTANCE.getResources().getString(R.string.video_transform_fail), false);
                    }
                } else{
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void chartVisibleIndicator(){
        binding.dockGuideChartPanel.eastTv.setVisibility(View.VISIBLE);
        binding.dockGuideChartPanel.northTv.setVisibility(View.VISIBLE);
    }
    public void updateChartInformationView() {
        ArrayList<List<Float>> yBarData = new ArrayList<List<Float>>();

        ArrayList<Float> valuesRADuration = new ArrayList<Float>();
        mYLineData.clear();
        mLineNames.clear();
        mLineColors.clear();
        mBarColors.clear();
        if (mGuider.mIsDxDyVisibility) {
            mYLineData.add(mGuider.mValuesDX);
            mYLineData.add(mGuider.mValuesDY);
            mLineNames.add("dx");
            mLineNames.add("dy");
            mLineColors.add(getResources().getColor(R.color.blue));
            mLineColors.add(getResources().getColor(R.color.red));
        }
        if (mGuider.mIsMassVisibility) {
            mYLineData.add(mGuider.mValuesStarMass);
            mLineNames.add("StarMass");
            mLineColors.add(Color.WHITE);
        }
        if (mGuider.mIsSnrVisibility) {
            mYLineData.add(mGuider.mValuesSNR);
            mLineNames.add("SNR");
            mLineColors.add(Color.GREEN);
        }

        if (mGuider.mIsRaDecVisibility){
            mYLineData.add(mGuider.mValuesRADistanceRaw);
            mYLineData.add(mGuider.mValuesDecDistanceRaw);
            yBarData.add(mGuider.mValuesRADuration);
            yBarData.add(mGuider.mValuesDECDuration);
            mLineNames.add("RADistanceRaw");
            mLineNames.add("DecDistanceRaw");
            mLineColors.add(getResources().getColor(R.color.purple_200));
            mLineColors.add(Color.YELLOW);
            mBarColors.add(getResources().getColor(R.color.blue));
            mBarColors.add(getResources().getColor(R.color.red));

        }else {
            yBarData.add(valuesRADuration);
            yBarData.add(valuesRADuration);
            mBarColors.add(getResources().getColor(R.color.purple_200));
            mBarColors.add(Color.YELLOW);
        }
        mChartManager.showCombinedChart(
                mGuider.maxisMaximum, yBarData, mYLineData, mBarNames, mLineNames,
                mBarColors, mLineColors
        );
    }
    /*这个就是用来解析返回来的值*/
    private void parseResData(){
        ArrayList<JSONObject> resolutions = mGuider.getGuideLocalPushResolutions();
        mCurrentResIndex = mGuider.getGuideLocalPushCurrentResolution();
        mCurrentResIndex = mCurrentResIndex > 0?mCurrentResIndex:0;
        ArrayList<String> resolutionList = new ArrayList<String>();
        ArrayList<String> resolutionValueList = new ArrayList<String>();
        for (JSONObject it: resolutions)
        {
            try {
                resolutionValueList.add(it.getInt("width")+"x"+it.getInt("height"));
                resolutionList.add(""+it.getInt("height")+"p");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mResDataList.clear();
        mResDataValueList.clear();
        mResDataValueList.addAll(resolutionValueList);
        mResDataList.addAll(resolutionList);
    }

    public void dockCtrlAreaEvent(){
        binding.dockGuideChartPanel.dxDyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGuider.mIsDxDyVisibility = isChecked;
                updateChartInformationView();
            }
        });

        binding.dockGuideChartPanel.dxDyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dockGuideChartPanel.dxDyCb.setChecked(!binding.dockGuideChartPanel.dxDyCb.isChecked());
            }
        });

        binding.dockGuideChartPanel.raDecCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGuider.mIsRaDecVisibility = isChecked;
                updateChartInformationView();
            }
        });

        binding.dockGuideChartPanel.raDecTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dockGuideChartPanel.raDecCb.setChecked(!binding.dockGuideChartPanel.raDecCb.isChecked());
            }
        });

        binding.dockGuideChartPanel.snrCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGuider.mIsSnrVisibility = isChecked;
                updateChartInformationView();
            }
        });

        binding.dockGuideChartPanel.snrTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dockGuideChartPanel.snrCb.setChecked(!binding.dockGuideChartPanel.snrCb.isChecked());
            }
        });

        binding.dockGuideChartPanel.masCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGuider.mIsMassVisibility = isChecked;
                updateChartInformationView();
            }
        });
        binding.dockGuideChartPanel.masTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dockGuideChartPanel.masCb.setChecked(!binding.dockGuideChartPanel.masCb.isChecked());
            }
        });
    }

    private void initChart() {
        mBarNames.add("RADuration");
        mBarNames.add("DECDuration");
        mLineColors.add(getResources().getColor(R.color.blue));
        mLineColors.add(getResources().getColor(R.color.red));
        mLineColors.add(getResources().getColor(R.color.purple_200));
        mLineColors.add(Color.YELLOW);
        mLineColors.add(Color.WHITE);
        mLineColors.add(Color.GREEN);
        mLineNames.add("dx");
        mLineNames.add("dy");
        mLineNames.add("StarMass");
        mLineNames.add("SNR");
        mLineNames.add("RADistanceRaw");
        mLineNames.add("DecDistanceRaw");

        mChartManager = new CombinedChartManager(binding.dockGuideChartPanel.chart);
    }

    private void setDockTabMode(int currentDockId){
        mDockCurrentTabId = currentDockId;
        setDockMode(MODE_TAB);
    }

    private void setDockMode(int mode) {
        setDockGuidePanelFlag("");
        switch (mode)  {
            case MODE_SINGLE : {
                mPanelMode = MODE_SINGLE;
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                binding.dockChartLabelTv.setVisibility(View.INVISIBLE);
            }
            break;
            case MODE_SPLIT : {
                mPanelMode = MODE_SPLIT;
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                setTabSwitchBg(MODE_SPLIT);
                binding.dockChartLabelTv.setVisibility(View.INVISIBLE);
                binding.guideChartContainer.setVisibility(View.VISIBLE);
                setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
            }
            break;
            case MODE_TAB : {
                mPanelMode = MODE_TAB;
                binding.dockChartLabelTv.setChecked(true);
                binding.guideChartContainer.setVisibility(View.VISIBLE);
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                binding.dockChartLabelTv.setVisibility(View.VISIBLE);
                setTabSwitchBg(MODE_TAB);
            }
            break;
            case MODE_CHART_SINGLE : {
                mPanelMode = MODE_CHART_SINGLE;
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                binding.dockChartLabelTv.setChecked(true);
                binding.guideChartContainer.setVisibility(View.VISIBLE);
            }
            break;
        }
        setGuidingStatusMode(mGuider.mStarGuidingMode);
    }

    private void keepDockStarVisible(){
        setDockStarVisible(mGuider.mIsDockStarVisible);
        binding.leftLayout.starBtn.setChecked(mGuider.mIsDockStarVisible);
    }
    private void setDockStarVisible(boolean isVisible){
        if (isVisible){
            mGuider.mIsDockStarVisible = true;
            binding.starPanelContainer.setVisibility(View.VISIBLE);
        }else {
            mGuider.mIsDockStarVisible = false;
            binding.starPanelContainer.setVisibility(View.GONE);
        }

    }
    private void hideAllDockPanelView() {
        mPanelList.clear();
        binding.editContainer.setVisibility(View.GONE);
        binding.dockCloseBtn.setVisibility(View.GONE);
        binding.dockTabSwitchBtn.setVisibility(View.GONE);
        binding.dockChartLabelTv.setVisibility(View.GONE);
        setDockStarVisible(false);
        binding.guideChartContainer.setVisibility(View.GONE);
        binding.dockChartLabelTv.setChecked(false);
        binding.leftLayout.starBtn.setChecked(false);
        binding.leftLayout.guideBtn.setChecked(false);
    }

    private void hideAllGuideDockPanelView(){
        mPanelList.clear();
        binding.editContainer.setVisibility(View.GONE);
        binding.dockCloseBtn.setVisibility(View.GONE);
        binding.dockTabSwitchBtn.setVisibility(View.GONE);
        binding.dockChartLabelTv.setVisibility(View.GONE);
        binding.guideChartContainer.setVisibility(View.GONE);
        binding.dockChartLabelTv.setChecked(false);
        binding.leftLayout.guideBtn.setChecked(false);
    }

    public void loadStarPosition(){
        int mBoundRight = (int) binding.surface.mRender.mBoundRight;
        int mBoundLeft = (int) binding.surface.mRender.mBoundLeft;
        int mBoundTop = (int) binding.surface.mRender.mBoundTop;
        int mBoundBottom = (int) binding.surface.mRender.mBoundBottom;
        mTitleHeight = binding.title.statusTv.getHeight();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Double[] selectedStar = mGuider.getLockedPosition();
                    Double chartDx = mGuider.mChartData.mDx;
                    Double chartDy = mGuider.mChartData.mDy;
                    int dx = 0;
                    int dy = 0;
                    if ((chartDx != null) && (chartDy != null)){
                        dx = chartDx.intValue();
                        dy = chartDy.intValue();
                    }
                    if (selectedStar.length != 0) {
                        ArrayList<Float> rectPhysicalLocation = getPhysicalLocation(selectedStar[0]+dx, selectedStar[1] + dy);

                        ArrayList<Float> crossPhysicalLocation = getPhysicalLocation(selectedStar[0], selectedStar[1]);

                        int xLocation = rectPhysicalLocation.get(0).intValue();
                        int yLocation = (int) (rectPhysicalLocation.get(1) + mTitleHeight);
                        updateLogInfo("Event Guiding 绿框 LockPosition + dx "+":"+ (selectedStar[0] + dx));
                        updateLogInfo("Event Guiding 绿框 LockPosition + dy "+":"+ (selectedStar[1] + dy));
                        updateLogInfo("Guiding 绿框  对应手机屏幕的位置 x "+":"+ xLocation);
                        updateLogInfo("Guiding 绿框  对应手机屏幕的位置 y "+":"+ yLocation);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.crossLine.setNewLocation(crossPhysicalLocation.get(0).intValue(), (int) (crossPhysicalLocation.get(1) + mTitleHeight),
                                        mBoundLeft, mBoundRight, mBoundTop + mTitleHeight, mBoundBottom + mTitleHeight);
                                binding.selectRect.setNewLocation(xLocation, yLocation);
                                if (mGuider.mStarGuidingMode == STAR_GUIDING_STOPPED){
                                    binding.selectRect.setVisibility(View.INVISIBLE);
                                    binding.crossLine.setVisibility(View.INVISIBLE);
                                }else {
                                    binding.selectRect.setVisibility(View.VISIBLE);
                                    binding.crossLine.setVisibility(View.VISIBLE);
                                }
                                if (mGuider.mStarGuidingMode == STAR_GUIDING_STOPPED){
                                    binding.selectRect.setVisibility(View.INVISIBLE);
                                    binding.crossLine.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void updateGuideDeviceTitle(){
        updateLogInfo("updateDevices");
        if (mGuider.mGuideIsDeviceConnected){
            binding.title.statusTv.setText(getResources().getString(R.string.guidingcamera)+" ["+
                    mGuider.mSelectedDevices.get("guide")+"]");
        }else {
            binding.title.statusTv.setText(getResources().getString(R.string.guidingcamera)+" [None]");
        }
    }

    //refresh ui area

    private void updateGainTitle(String msg){
        mCurrentGain = App.INSTANCE.getResources().getString(R.string.Gain)+": " + msg;
        binding.title.rightTv.setText(mCurrentGain + "  "+ mCurrentExp + "  "+ mCurrentDither);
    }

    private void updateExpTitle(String msg){
        mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp)+": "+msg;
        binding.title.rightTv.setText(mCurrentGain + "  "+ mCurrentExp + "  "+ mCurrentDither);
    }

    private void updateDitherTitle(String msg){
        mCurrentDither = App.INSTANCE.getResources().getString(R.string.dither)+": "+msg;
        binding.title.rightTv.setText(mCurrentGain + "  "+ mCurrentExp + "  "+ mCurrentDither);
    }

    private void updateGuidingTitle(String msg){
        mGuidingStatus = msg;
        binding.bottom.centerTv.setText(mGuidingStatus);
        Log.e(TAG, "updateGuidingTitle: " );
    }

    private Float[] getClickPosition(MotionEvent e){
        float[] oldPoint = binding.surface.mRender.mOldPoints;
        float[] newPoint = binding.surface.mRender.mNewPoints;
        float mBoundRight = binding.surface.mRender.mBoundRight;
        float mBoundLeft = binding.surface.mRender.mBoundLeft;
        float mBoundTop = binding.surface.mRender.mBoundTop;
        float mBoundBottom = binding.surface.mRender.mBoundBottom;

        Log.d(
                TAG,
                "getClickPosition: oldPoint is " + oldPoint.toString() + "newPoint is " + newPoint.toString()
        );
        Log.d(
                TAG,
                "getClickPosition: mBoundRight is " + mBoundRight + "  mBoundLeft is " + mBoundLeft + " mBoundTop is  " + mBoundTop + "mBoundBottom  is  " + mBoundBottom
        );
        float xScale = (mBoundRight - mBoundLeft) / (oldPoint[2] - oldPoint[0]);
        float yScale = (mBoundBottom - mBoundTop) / (oldPoint[5] - oldPoint[1]);

        Float[] centerPoint = new Float[]{
                (mBoundRight - mBoundLeft) / 2 + mBoundLeft,
                (mBoundBottom - mBoundTop) / 2 + mBoundTop
        };

        Float[] scaleClickPoint = new Float[]{
                (e.getX() - centerPoint[0]) / xScale,
                -(e.getY() - centerPoint[1]) / yScale
        };

        Float[] clickPointScale = new Float[]{
                (scaleClickPoint[0] - newPoint[4]) / (newPoint[6] - newPoint[4]),
                (newPoint[5] - scaleClickPoint[1]) / (newPoint[5] - newPoint[1])
        };

        Float[] newClickPoint = new Float[]{
                (mBoundRight - mBoundLeft) * clickPointScale[0] + mBoundLeft,
                (mBoundBottom - mBoundTop) * clickPointScale[1] + mBoundTop - binding.title.statusTv.getHeight()
        };
        return newClickPoint;
    }

    public ArrayList<Double> cropPosition(Double x, Double oldy) throws Exception {
        Boolean mode = mGuider.mIsMainRtspSource;
        ArrayList<Double> targetPos =new ArrayList<Double>();
        ArrayList<Integer> fullSize =new ArrayList<Integer>();
        if (mode) {
            if (mGuider.mMainFullSize.size() == 0) {
                mGuider.mMainFullSize = mGuider.getFullSize("main");
            }
            fullSize = mGuider.mMainFullSize;
        } else {
            if (mGuider.mGuideFullSize.size() == 0){
                mGuider.mGuideFullSize = mGuider.getFullSize("guide");
            }
            fullSize = mGuider.mGuideFullSize;
        }
        Log.e(TAG, "starSelected + cropPosition: fullSize.get(0)"+fullSize.get(0)+" fullSize.get(1) is " +fullSize.get(1)+"mode"+mode);
        Double y = fullSize.get(1) - oldy;
        Double scaleX = fullSize.get(0) / mResX;
        Double scaleY = fullSize.get(1) / mResY;
        if (scaleX <= scaleY) {
            Double newCropY = (mResY / mResX) * fullSize.get(0);
            Double deltaY = (fullSize.get(1) - newCropY)/2;
            Double newY = y - deltaY;
            Double targetX = x / fullSize.get(0);
            Double targetY = newY / newCropY;
            targetPos.add(targetX);
            targetPos.add(targetY);
            Log.e(TAG, "cropPosition: starSelected newCropY"+newCropY+"mResX" +mResX+"mResY"+mResY);
        } else {
            Double newCropX = (mResX / mResY) * fullSize.get(1);
            Double deltaX = (fullSize.get(0) - newCropX)/2;
            Double newX = x - deltaX;
            Double targetX = newX / newCropX;
            Double targetY = y / fullSize.get(1);
            targetPos.add(targetX);
            targetPos.add(targetY);
        }
        return targetPos;
    }

    private ArrayList<Float> getPhysicalLocation(Double x,Double y) throws Exception {
        ArrayList<Float> physicalLocation = new ArrayList<Float>();
        float[] oldPoint = binding.surface.mRender.mOldPoints;
        float[] newPoint = binding.surface.mRender.mNewPoints;
        float mBoundRight = binding.surface.mRender.mBoundRight;
        float mBoundLeft = binding.surface.mRender.mBoundLeft;
        float mBoundTop = binding.surface.mRender.mBoundTop;
        float mBoundBottom = binding.surface.mRender.mBoundBottom;

        /*计算当前选中位置在裁剪图像中的比例,其中这个比例是以左上角，向左向下作为坐标系统的*/
        Log.d(TAG, "starSelected: x is :" + x + "   y is :"+ y);
        ArrayList<Double> scalePosition = null;
        try {
            scalePosition = cropPosition(x,y);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        Log.d(TAG, "starSelected: xPhysicalLocation:  scalePosition[0]" + scalePosition.get(0) + "   scalePosition[1]:"+ scalePosition.get(1));
        /*获取放缩后的长和高度*/
        Float xLength = newPoint[2] - newPoint[0];
        Float yLength = newPoint[7] - newPoint[3];

        Log.d(TAG, "starSelected: xLength:" + xLength + "   yLength:"+yLength);
        /*计算在这个放缩坐标系里面的长度和高度*/
        Double xDistance = xLength * scalePosition.get(0);
        Double yDistance = yLength * scalePosition.get(1);

        Log.d(TAG, "starSelected: xDistance:" + xDistance + "   yDistance:"+yDistance);

        /*计算在这个放缩坐标系里面的位置，坐标系有点问题*/
        double xMapLocation = newPoint[4] + xDistance;
        double yMapLocation = newPoint[7] - yDistance;
        Log.d(TAG, "starSelected: xPhysicalLocation:  newPoint[7]" + newPoint[7] + "   yMapLocation:"+yMapLocation );
        //  var yMapLocation = yDistance - newPoint[7]
        /*双向的比例系数的求解*/
        Float xscale = (mBoundRight - mBoundLeft) / (oldPoint[2] - oldPoint[0]);
        Float yscale = (mBoundBottom - mBoundTop) / (oldPoint[5] - oldPoint[1]);
        Log.d(TAG, "starSelected: xscale:" + xscale + "   yscale:"+yscale);
        /*求解设备空间与中心点的距离*/
        double xPhysicalDistance = xMapLocation * xscale;
        double yPhysicalDistance = yMapLocation * yscale;
        Log.d(TAG, "starSelected: xPhysicalDistance:" + xPhysicalDistance + "   yPhysicalDistance:"+yPhysicalDistance);
        /*中心点的坐标位置*/
        Float[] centerPoint = new Float[]{
                (mBoundRight - mBoundLeft) / 2 + mBoundLeft,
                (mBoundBottom - mBoundTop) / 2 + mBoundTop
        };
        Log.d(TAG, "starSelected: centerPoint[0]:" + centerPoint[0] + "   centerPoint[1]:"+centerPoint[1] );
        double xPhysicalLocation = xPhysicalDistance + centerPoint[0];
        double yPhysicalLocation = centerPoint[1] + yPhysicalDistance;

        physicalLocation.add((float)xPhysicalLocation);
        physicalLocation.add((float)yPhysicalLocation);
        Log.d(TAG, "starSelected: xPhysicalLocation:" + xPhysicalLocation + "   yPhysicalLocation:"+yPhysicalLocation);

        return physicalLocation;
    }

    //event response area

    public void starSelected() {
        updateLogInfo("starSelected");
        /*方框：绿实线*/
        setGuidingStatusMode(STAR_SELECTED);
    }

    public void rtspResChangedEvent(){
        if (!mGuider.mRtspResChangeDevice.equals("guide")){
            return;
        }
        /*释放相机，restart 拉流*/
        releaseStream();
        mCurrentResIndex = mGuider.mRtspResIndex;
        mIsNeedRestoreGuidingStatus = true;
    }

    public void updateRtspSource(){
    }

    public void captureLoopingEvent(){
        setGuidingStatusMode(STAR_CAPTURE_LOOPING);
    }


    public void releaseStream(){
        if (mLib != null) {
            mLib.releaseCamera();
        }
    }

    public void rtspIsReadyEvent(){
        startStream();
    }

    public void calibrationStartEvent(){
        updateLogInfo("calibrationStartEvent");
        setGuidingStatusMode(STAR_CALIBRATION_START);
    }

    public void calibratingEvent(){
        updateLogInfo("calibratingEvent");
        setGuidingStatusMode(STAR_CALIBRATING);
    }

    public void starLostEvent(){
        /*方框：(消失，如果当前是在guiding状态中，不会消失，只会变颜色，guiding回来之后，变成绿色
        （等效为guiding start 和guiding stop这个中间不会消失）)*/
        setGuidingStatusMode(STAR_LOST);
        clearDockStarView();
    }

    private void resetViewForNetworkReconnect(){
        resetToInitStatus();
        hideAllDockPanelView();
        binding.rightLayout.guidingBtn.setChecked(false);
        binding.title.statusTv.setText(getResources().getString(R.string.guide_camera_status)+" [None]");
        updateFpsTitle();
        binding.title.rightTv.setText("");
        binding.bottom.centerTv.setText("");
    }

    private void resetViewForDeviceReconnect(){
        stopFpsTimer();
        setGuidingStatusMode(STAR_IDLE);
        updateFpsTitle();
        binding.title.rightTv.setText("");
        binding.bottom.centerTv.setText("");
    }
    public void guidingStoppedEvent(){
        updateLogInfo("guidingStoppedEvent");
        /*十字线：消失，方框消失*/
        clearDockStarView();
        setGuidingStatusMode(STAR_GUIDING_STOPPED);
    }

    public void guidingStartedEvent(){
        updateLogInfo("guidingStartedEvent");
        /*十字线：绿实线*/
        setGuidingStatusMode(STAR_GUIDING_STARTED);
    }

    public void guidingEvent(){
        updateLogInfo("guidingEvent");
        setGuidingStatusMode(STAR_GUIDING);
    }

    public void gainChangedEvent(){
        if (mGuider.mGainChangedDevice.equals("guide")){
            updateGainCache(mGuider.mGainChangedValue);
        }
    }

    public void expChangedEvent(){
        if (mGuider.mExposureChangedDevice.equals("guide")) {
            updateExpEvent(mGuider.mExposureChangedValue);
        }
    }

    public void guidingDithered(){
    }

    public void calibrationFailedEvent(){
        updateLogInfo("calibrationFailedEvent");
        setGuidingStatusMode(STAR_CALIBRATION_FAILED);
        manageStopGuide();
    }

    public void connectionLostEvent(){
        hideDiySoftInput();
        releaseStream();
        resetFrame();
        manageGuidingBtnStatus();
        hideAllSettingPanel();
        if (mGoToPop != null) {
            mGoToPop.dismiss();
        }
        if (mUnParkDialog != null) {
            mUnParkDialog.dismiss();
        }
        clearDockStarView();
        setGuidingStatusMode(STAR_IDLE);
        manageGotoBtnStatus();
        stopFpsTimer();
        resetViewForNetworkReconnect();
        mCheckConnectProcess.showConnectingDialog();
        connectSuccess(mGuider.mIsNetworkOnLine,mGuider.mGuideIsDeviceConnected);
    }

    public void manageChartCacheView(){
        if (mGuider.mCurrentConnectedDevice.equals("guide")) {
            if (!mGuider.mGuideIsDeviceConnected){
                updateChartInformationView();
            }
        }

        if (mGuider.mCurrentConnectedDevice.equals("mount")) {
            if (!mGuider.mScopeIsDeviceConnected){
                updateChartInformationView();
            }
        }
    }

    public void deviceConnectEvent(){
        manageGuidingBtnStatus();
        manageGotoBtnStatus();
        manageChartCacheView();
        if (!mGuider.mCurrentConnectedDevice.equals("guide")) {
            return;
        }
        if (mGuider.mGuideIsDeviceConnected) {
          //  enterGuideFromInit();
        } else {
            updateGuideDeviceTitle();
            connectSuccess(mGuider.mIsNetworkOnLine,mGuider.mGuideIsDeviceConnected);
            setGuidingStatusMode(STAR_IDLE);
            releaseStream();
            resetFrame();
            resetViewForDeviceReconnect();
            clearDockStarView();
        }
        if (!mGuider.mScopeIsDeviceConnected){
            manageStopGuide();
        }else {
        }
    }


    private class MyFpsTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateFpsTitle();
                }
            });
        }
    }

    private void startFpsTimer() {
        if (mFpsTimer != null){
            stopFpsTimer();
        }
        mFpsTimer = new Timer(true);
        mFpsTimerTask = new MyFpsTimerTask();
        mFpsTimer.schedule(mFpsTimerTask, 0, 1000);
    }

    private void stopFpsTimer() {
        mStreamFpsCnt = 0;
        if (mFpsTimer != null) {
            mFpsTimer.cancel();
            mFpsTimer = null;
        }
        if (mFpsTimerTask != null) {
            mFpsTimerTask.cancel();
            mFpsTimerTask = null;
        }
    }

    private void updateFpsTitle(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mGuideIsDeviceConnected && mGuider.mIsNetworkOnLine) {
                    mFps = mStreamFpsCnt + "fps";
                    binding.bottom.rightTv.setText(mCurrentRes + "  " + mFps);
                }else {
                    binding.bottom.rightTv.setText("");
                }
                mStreamFpsCnt = 0;
            }
        });
    }

    public void updateLogInfo(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: setGuidingStatusMode x 是"+msg);
                if (mLogBuilder != null){
                  //  logBuilder.addMessageContent(msg);
                }
            }
        });
    }

    private void dealTypeConflict(RadioButton radioButton){
        radioButton.setChecked(true);
        for (RadioButton it:mRadioBtnList){
            if (radioButton != it){
                if (it.isChecked()) it.setChecked(false);
            }
        }
    }

    public void updateTrueLogInfo(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLogBuilder != null){
                    mLogBuilder.addAllMessageContent(getDataList());
                }
            }
        });
    }

    private ArrayList<String> getDataList(){
        ArrayList<String> listData = new ArrayList<>();
        listData.add("Main device parameter:");
        listData.add("isNetworkOnLine:"+mGuider.mIsNetworkOnLine);
        listData.add("mMainIsDeviceConnected:"+mGuider.mMainIsDeviceConnected);
        listData.add("mMainFocalLength:"+mGuider.mMainFocalLength);
        listData.add("mMainGain:"+mGuider.mMainGain);
        listData.add("mMainGainMode:"+mGuider.mMainGainMode);
        listData.add("mMainLowNoiseMode:"+mGuider.mMainLowNoiseMode);
        listData.add("mMainCooling:"+mGuider.mMainCooling);
        listData.add("mMainFan:"+mGuider.mMainFan);
        listData.add("mMainTargetTemp:"+mGuider.mMainTargetTemp);
        listData.add("mMainMaxTemp:"+mGuider.mMainMaxTemp);
        listData.add("mMainMinTemp:"+mGuider.mMainMinTemp);
        listData.add("mMainExpData:"+mGuider.mMainExpData);
        listData.add("mMainCurrentResolution:"+mGuider.mMainCurrentResolution);
        listData.add("mMainBinning:"+mGuider.mMainBinning);
        listData.add("mMainCaptureMode:"+mGuider.mMainCaptureMode);
        listData.add("mCoordRa:"+mGuider.mCoordRa);
        listData.add("mCoordDec:"+mGuider.mCoordDec);
        listData.add("mMainExpRange:"+mGuider.mMainExpRange.toString());
        listData.add("mMainExpDataList:"+mGuider.mMainExpDataList.toString());
        listData.add("mMainTargetTempDataList:"+mGuider.mMainTargetTempDataList.toString());
        listData.add("mMainBinningDataList:"+mGuider.mMainBinningDataList.toString());
        listData.add("mMainResDataList:"+mGuider.mMainResDataList.toString());
        listData.add("mMainFullSize:"+mGuider.mMainFullSize.toString());
        listData.add("mGuideFullSize:"+mGuider.mGuideFullSize.toString());
        listData.add("Guide parameter:");
        listData.add("mGuideIsDeviceConnected:"+mGuider.mGuideIsDeviceConnected);
        listData.add("mGuideFocalLength:"+mGuider.mGuideFocalLength);
        listData.add("mGuideGain:"+mGuider.mGuideGain);
        listData.add("mGuideExpData:"+mGuider.mGuideExpData);
        listData.add("mGuideExpNumber:"+mGuider.mGuideExpNumber);
        listData.add("mGuideBinning:"+mGuider.mGuideBinning);
        listData.add("mGuideDitherScale:"+mGuider.mGuideDitherScale);
        listData.add("mGuideMaxDec:"+mGuider.mGuideMaxDec);
        listData.add("mGuideMaxRa:"+mGuider.mGuideMaxRa);
        listData.add("mGuideDitherMode:"+mGuider.mGuideDitherMode);
        listData.add("mGuideRaOnly:"+mGuider.mGuideRaOnly);
        listData.add("mGuideBinMax:"+mGuider.mGuideBinMax);
        listData.add("mGuideExpDataList:"+mGuider.mGuideExpDataList.toString());
        listData.add("mGuideExpDataValueList:"+mGuider.mGuideExpDataValueList.toString());
        listData.add("FilterWheel:");
        listData.add("mFilterWheelIsDeviceConnected:"+mGuider.mFilterWheelIsDeviceConnected);
        listData.add("mFilterWheelUnidirectional:"+mGuider.mFilterWheelUnidirectional);
        listData.add("mFilterWheelSlotNumber:"+mGuider.mFilterWheelSlotNumber);
        listData.add("mFilterWheelPosition:"+mGuider.mFilterWheelPosition);
        listData.add("mFilterWheelSlotNumberMax:"+mGuider.mFilterWheelSlotNumberMax);
        listData.add("Focuser:");
        listData.add("mFocuserIsDeviceConnected:"+mGuider.mFocuserIsDeviceConnected);
        listData.add("mFocuserPosition:"+mGuider.mFocuserPosition);
        listData.add("Scope:");
        listData.add("mScopeIsDeviceConnected:"+mGuider.mScopeIsDeviceConnected);
        listData.add("mScopePark:"+mGuider.mScopePark);
        listData.add("mMountSpeedChangedValue:"+mGuider.mMountSpeedChangedValue);
        listData.add("mScopeBaudrate:"+mGuider.mScopeBaudrate);
        listData.add("mScopeTracking:"+mGuider.mScopeTracking);
        listData.add("mScopeTrackingRate:"+mGuider.mScopeTrackingRate);
        listData.add("mScopeCoord:Ra: "+mGuider.mCoordRa+" DEC: "+ mGuider.mCoordDec);
        listData.add("mScopeGuidingSpeedList:"+mGuider.mScopeGuidingSpeedList.toString());
        listData.add("mScopeBaudrateList:"+mGuider.mScopeBaudrateList.toString());
        listData.add("mCurrentDevices:"+mGuider.mCurrentDevices.toString());
        listData.add("mSelectedDevices:"+mGuider.mSelectedDevices.toString());
        listData.add("mDevicesConnected:"+mGuider.mDevicesConnected.toString());
        listData.add("mWorkStates:"+mGuider.mWorkStates.toString());
        listData.add("dc_one_status:"+mGuider.mDcOneStatus);
        listData.add("dc_two_status:"+mGuider.mDcTwoStatus);
        listData.add("dc_three_status:"+mGuider.mDcThreeStatus);
        listData.add("dc_four_status:"+mGuider.mDcFourStatus);
        return listData;
    }

    public void connectSuccess(boolean isNetworkConnected,boolean isDeviceConnected){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isNetworkConnected && isDeviceConnected){
                    binding.title.rightTv.setVisibility(View.VISIBLE);
                    binding.rightLayout.resTbtn.setEnabled(true);
                    binding.rightLayout.gainTbtn.setEnabled(true);
                    binding.rightLayout.expTbtn.setEnabled(true);
                    binding.rightLayout.ditherTbtn.setEnabled(true);
                }else {
                    binding.title.rightTv.setVisibility(View.INVISIBLE);
                    binding.rightLayout.resTbtn.setEnabled(false);
                    binding.rightLayout.gainTbtn.setEnabled(false);
                    binding.rightLayout.expTbtn.setEnabled(false);
                    binding.rightLayout.ditherTbtn.setEnabled(false);
                }
                if (isNetworkConnected){
                    binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_success));
                }else {
                    manageScopeStatus();
                    binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_fail));
                }
            }
        });
    }
    public void connectionSuccessEvent(){
        mCheckConnectProcess.dismissConnectedDialog();
        connectSuccess(mGuider.mIsNetworkOnLine,mGuider.mGuideIsDeviceConnected);
    }


    public void startSlewEvent(){
        mIsScopeMoving = true;
        startCoordTimer();
    }
    public void stopSlewEvent(){
        mIsScopeMoving = false;
        stopCoordTimer();
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
                runOnUiThread(new Runnable() {
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

    public void parameterLoadSuccess(){
        updateGuideDeviceTitle();
        setDockStarCtrlView();
        connectSuccess(mGuider.mIsNetworkOnLine,mGuider.mGuideIsDeviceConnected);
        if (mGuider.mGuideIsDeviceConnected) {
            mIsNeedRestoreGuidingStatus = true;
            loadExpData();
            loadGainData();
            loadDitherData();
            parseResData();
            manageGuideCaptureStatus();
        } else {
            releaseStream();
            resetFrame();
            resetViewForDeviceReconnect();
            manageStopGuide();
        }
        manageGuidingBtnStatus();
        manageGotoBtnStatus();
    }

    private void manageStopGuide(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (mGuider.stopGuide()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setGuidingStatusMode(STAR_GUIDING_STOPPED);
                        }
                    });
                }
            }
        }.start();
    }
    private void manageGuideCaptureStatus(){
        if (mGuider.mGuideIsDeviceConnected) {
            if (!mGuider.mIsGuideCapturing) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mGuider.startCapture("guide");
                    }
                }.start();
            }

            if (mGuider.mIsGuideRtspSource && mGuider.mIsGuideStartStreamDirectly) {
                startStream();
            }else {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.setRtspSource(false);
                    }
                }.start();
            }
        }
    }

    private void clearDockStarView(){
        binding.dockStarPanel.infoTv.setText("");
        binding.dockStarPanel.peak.setText("");
        binding.dockStarPanel.chart.setVisibility(View.INVISIBLE);
    }

    private void clearDockGuideDataView(){

    }


    public void manageScopeStatus(){
        if (mGuider.mScopeIsDeviceConnected){
            if (mGuider.mIsGuidingStarted){
                if (mGoToPop != null) {
                    mGoToPop.dismiss();
                }
                binding.leftLayout.goToBtn.setEnabled(false);
            }else {
                binding.leftLayout.goToBtn.setEnabled(true);
            }
        }else {
            binding.leftLayout.goToBtn.setEnabled(false);
        }
    }


    public void starProfileUpdateEvent(){
        try {
            binding.dockStarPanel.chart.setVisibility(View.VISIBLE);
            String info = mGuider.mStarProfileInfo.mMode + "  FWHM: "+mGuider.mStarProfileInfo.mFWHM +"  HFD: "+mGuider.mStarProfileInfo.mHFD +"("
                    +mGuider.mStarProfileInfo.mHFD +"″)";
            binding.dockStarPanel.infoTv.setText(info);
            xValues.clear();
            for (int i = 0; i < mGuider.mStarProfileInfo.mProfile.length(); i++) {
                BrokenLineView.XValue xValue = new BrokenLineView.XValue(i, i + "");
                xValues.add(xValue);
            }

            lineValues.clear();
            mDockStarPeak = 0;
            int value = 0;
            for (int i = 0; i < mGuider.mStarProfileInfo.mProfile.length(); i++) {
                try {
                    value = mGuider.mStarProfileInfo.mProfile.getJSONObject(i).getInt("value");
                    if (value > mDockStarPeak){
                        mDockStarPeak = value;
                    }
                    BrokenLineView.LineValue lineValue = new BrokenLineView.LineValue(value, i,value + "");
                    lineValues.add(lineValue);
                }catch (Exception e){
                    BrokenLineView.LineValue lineValue = new BrokenLineView.LineValue(0,i, 0 + "");
                    lineValues.add(lineValue);
                    e.printStackTrace();
                }
            }
            yValues.clear();
            BrokenLineView.YValue yValue3 = new BrokenLineView.YValue(mDockStarPeak, "");
            yValues.add(yValue3);

            binding.dockStarPanel.chart.setPaintColor("#ED1101");
            binding.dockStarPanel.peak.setText("Peak\n"+mGuider.mStarProfileInfo.mPeak);
            binding.dockStarPanel.chart.setValue(lineValues, xValues, yValues);
            // starProfileChartManager.showLineChart();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void gotoErrorEvent(){
        if (mGuider.mAlertMsg.equals("Failed to slew. ")){
            if (mGotoProcessDialog != null){
                mGotoProcessDialog.dismiss();
            }
        }
    }
    public void ditherScaleChangedEvent(){
        Double ditherDoubleValue = mGuider.mGuideDitherScale;
        if (mCurrentDitherValue.equals(ditherDoubleValue)) {
            return;
        }
        String text = String.format("%.1f", ditherDoubleValue);
        updateDitherTitle(text);
        mClickMenuList.clear();
        for (String item : mDitherDataList) {
            mClickMenuList.add(new ClickMenuItem(item, true));
        }
        if (mDitherDataList.contains(text)){
            mCurrentDitherIndex = mDitherDataList.indexOf(text);
            mClickMenuList.add(new ClickMenuItem(mEditDitherValue, true));
        }else {
            mEditDitherValue = String.format("%.1f", ditherDoubleValue);
            SharedPreferencesUtil.getInstance().putString(Constant.SP_DITHER_SCALE_ID, String.valueOf(ditherDoubleValue));
            mClickMenuList.add(new ClickMenuItem(mEditDitherValue, true));
            mCurrentDitherIndex = mClickMenuList.size() - 1;
        }
        mClickMenuAdapter.setSelectedIndex(mCurrentDitherIndex);
        mClickMenuAdapter.notifyDataSetChanged();
    }

}
