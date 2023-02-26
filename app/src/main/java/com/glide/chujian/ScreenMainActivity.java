package com.glide.chujian;

import static com.glide.chujian.util.Constant.PATH_VIDEOS;
import static com.glide.chujian.util.GuideUtil.getFloatNumberNoUnit;
import static com.glide.chujian.util.GuideUtil.parseBinNumber;

import static java.lang.Math.pow;
import static java.lang.Math.round;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.adapter.ClickMenuAdapter;
import com.glide.chujian.adapter.ClickMenuAdapterListener;
import com.glide.chujian.databinding.ActivityScreenMainBinding;
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
import com.glide.chujian.util.FileLoader;
import com.glide.chujian.util.Guider;
import com.glide.chujian.util.LogUtil;
import com.glide.chujian.util.ScreenUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpLib;
import com.glide.chujian.util.TpScreenMainDownloadHandler;
import com.glide.chujian.util.TpScreenMainHandler;
import com.glide.chujian.util.CheckParamUtil;
import com.glide.chujian.view.DeviceChangingDialog;
import com.glide.chujian.view.FocuserPop;
import com.glide.chujian.view.GotoPop;
import com.glide.chujian.view.GotoProcessDialog;
import com.glide.chujian.view.HistogramLineView;
import com.glide.chujian.view.HistogramTick;
import com.glide.chujian.view.ParkDialog;
import com.glide.chujian.view.ProgressBar;
import com.glide.chujian.view.SettingPageMenuLayout;
import com.glide.chujian.view.TakePhotoView;
import com.glide.chujian.view.VerticalSeekBar;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenMainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Bundle> {
    private final String TAG = ScreenMainActivity.class.getSimpleName();
    public static final String FRAME_SAVED_NAME = "name";
    public static final String FRAME_SAVED_PATH = "images";
    public static final String FRAME_LENGTH = "file_length";

    private int mDockCurrentTabId = 0;//用于记忆点击label的时候，记住上一次的label
    private int mDockCurrentId = 0;
    @Nullable
    private TpLib mLib;
    private int mWidth;
    private int mHeight;
    private int mClickRawX,mClickRawY;
    private Timer mTimer;
    private long mExpTimeMin;
    private long mExpTimeMax;
    private int mExpSliderMin;
    private int mExpSliderMax;
    private String mEditExpValue = "";
    private String mEditTempValue = "";
    private String mCurrentPictureSource = "";
    private TimerTask mTimerTask;
    private final long DELAY = 2000L;
    private final long VIDEO_MODE_MAX_EXP = 5000L;
    private final long VIDEO_MODE_DEF_EXP = 1000L;
    private int mCount;
    private final int INIT = -1;
    private final int MODE_SINGLE = 0;
    private final int MODE_SPLIT = 1;
    private int mLastMode = MODE_SPLIT;
    private final int MODE_TAB = 2;
    private final int MODE_CHART_SINGLE = 3;
    private final int MODE_HISTOR_SINGLE = 4;
    private final Integer PANEL_H = 1;
    private final Integer PANEL_C = 2;
    private final long STREAM_DELAY_TIME = 8L;
    private final int MENU_INIT = -1;
    private final int MENU_VIDEO_MODE = 0;
    private final int MENU_PHOTO_PLAN_MODE = 1;
    private final int MENU_REALTIME_MODE = 2;
    private final int MENU_AUTO_FOCUSER_MODE = 3;
    private final int MENU_SINGLE_TRIGGER_MODE = 4;
    private final int CIRCLE_IDLE = 0;
    private final int CIRCLE_READY = 1;
    private final int CIRCLE_DOING = 2;
    private final int CIRCLE_SINGLE_DOING = 3;
    private final int CIRCLE_VIDEO_DOING = 4;
    private final Object mLock = new Object();
    private ArrayList<Fragment> mFragments = new ArrayList();
    private final TabItem[] mTabs = new TabItem[]{
            new TabItem(MainCameraFragment.class),
            new TabItem(GuideCameraFragment.class),
            new TabItem(FilterWheelFragment.class),
            new TabItem(FocuserFragment.class),
            new TabItem(ScopeFragment.class),
            new TabItem(MiscFragment.class)};
    private String mCurrentGain = App.INSTANCE.getResources().getString(R.string.Gain) + ": ";
    private int mCurrentGainIndex = 0;
    private int mSeekBarIndex = 0;
    private int mExpSeekBarIndex = 0;
    private String mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": ";
    private int mCurrentExpIndex = 0;
    private int mExposureValue = 0;
    private String mCurrentBin = "Bin";
    private int mCurrentBinIndex = 0;
    private Double mTargetTempValue = 0.0;
    private String mTargetTemp = getTargetTemp("");

    private int mCurrentTemperatureIndex = 0;
    private String mCurrentRes = "";
    private int mCurrentResIndex = 0;
    private String mVideoTime = "";
    private String mFps = "";
    private String mHistogramName = "";
    private double mStreamFpsCnt = 0;
    private long mLastDownloadTime;
    private FileLoader mFileLoader;
    public DeviceChangingDialog mLoadingDialog;
    private final ArrayList<ClickMenuItem> mClickMenuList = new ArrayList();
    private final ArrayList<String> mGainDataList = new ArrayList();
    private final ArrayList<String> mResDataList = new ArrayList();
    private final ArrayList<String> mResDataValueList = new ArrayList();
    private ClickMenuAdapter mClickMenuAdapter;
    private GotoPop mGoToPop;
    private FocuserPop mFocuserPop;
    private ArrayList<Integer> mPanelList = new ArrayList();
    private int mPanelMode = INIT;
    private int mMenuCurrentMode = MENU_INIT;
    private String mCaptureMode = "";
    private int mCircleStatus = CIRCLE_IDLE;
    private int mVideoModeEXPMin = 0;
    private int mVideoModeEXPMax = 0;
    private int mStreamRestartCnt = 0;

    private boolean mIsMainDeviceActive = false;
    private boolean mIsCameraHasCooling = false;
    private boolean mIsPictureViewVisibility = false;
    private boolean mIsPictureDownloading = false;

    private Timer mVideoTimer, mFpsTimer, mCurrentTempTimer, mDownloadTimer, mVideoModeLoadingTimer;
    private MyVideoTimerTask mVideoTimerTask;
    private long mVideoTimeCnt;
    private long mVideoLoadingTimeCnt;
    private MyFpsTimerTask mFpsTimerTask;
    private MyNowTempTimerTask mNowTempTimerTask;
    private DownloadTimerTask mDownloadTimerTask;
    private VideoModeTimerLoadingTask mVideoModeTimerTask;

    private GestureDetector mGestureDetector;
    private CombinedChartManager mCombinedChartManager;

    private String mCurrentFileName = "";
    private boolean mIsRecordingVideo = false;
    ArrayList<String> mBarNames = new ArrayList<String>();
    ArrayList<Integer> mLineColors = new ArrayList<Integer>();
    ArrayList<Integer> mBarColors = new ArrayList<Integer>();
    ArrayList<String> mLineNames = new ArrayList<String>();
    ArrayList<List<Float>> mYLineData = new ArrayList<List<Float>>();

    private ArrayList<ToggleButton> mToggleBtnList = new ArrayList<ToggleButton>();
    private TpScreenMainHandler mHandler = new TpScreenMainHandler(this);
    private TpScreenMainDownloadHandler mDownloadHandler = new TpScreenMainDownloadHandler(this);

    private ParkDialog.Builder mUnParkBuilder;
    private ParkDialog mUnParkDialog;
    private ActivityScreenMainBinding binding;
    private Guider mGuider;

    private GotoProcessDialog mGotoProcessDialog;
    private GotoProcessDialog.Builder mGotoBuilder;

    private Timer mCoordTimer;
    private CoordTimerTask mCoordTimerTask;
    private double mTargetRA = 0.0;
    private double mTargetDec = 0;
    private RaDecTask mRaDecTask;
    private Timer mRaDecTimer;
    private boolean mIsScopeMoving = false;

    private ToggleButton mLastToggleBtn;
    private ArrayList<ToggleButton> mMenuViewList = new ArrayList<ToggleButton>();

    private class RaDecTask extends TimerTask {
        @Override
        public void run() {
            updateRaDecView();
        }
    }

    private void startRaDecTask() {
        stopRaDecTask();
        mRaDecTimer = new Timer();
        mRaDecTask = new RaDecTask();
        mRaDecTimer.schedule(mRaDecTask, 0, 5000);
    }

    private void stopRaDecTask() {
        if (mRaDecTask != null) {
            mRaDecTask.cancel();
            mRaDecTask = null;
        }
        if (mRaDecTimer != null) {
            mRaDecTimer.cancel();
            mRaDecTimer = null;
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ScreenMainActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    public void updateWifiName(String wifi_name) {
        if (wifi_name != null) {
            binding.bottom.leftTv.setText(wifi_name.replace("\"", ""));
            checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);
        }
        if (wifi_name.equals("")) {
            mGuider.setHeartBeatTimeOut();
            checkDeviceAndNetworkConnection(false, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScreenMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mLib = TpLib.getInstance();
        mLib.init();
        mLib.setPreviewHandler(mHandler);
        mGuider = Guider.getInstance();
        mGuider.registerHandlerListener(mHandler);
        mGuider.registerHandlerListener(mDownloadHandler);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLib.setPreviewHandler(mHandler);
        mGuider.registerHandlerListener(mHandler);
        mStreamFpsCnt = 0;
        formatCurrentDeviceShowInfo();
        onlyUpdateMainScreen(false);
        initDockChartView();
        updateChartInformationView();
    }

    private void initMainCameraUI() {
        manageNetworkStatus(mGuider.mIsNetworkOnLine);
    }

    private void manageNetworkStatus(boolean isNetworkOnline) {
        checkDeviceAndNetworkConnection(isNetworkOnline, mGuider.mMainIsDeviceConnected);
        if (isNetworkOnline) {
            manageCameraConnection(mGuider.mMainIsDeviceConnected, mGuider.mIsMainLoaded);
            manageScopeConnection(mGuider.mScopeIsDeviceConnected, mGuider.mIsScopeLoaded);
        } else {
            //在这里处理网络断开的情况
        }
    }

    private void onlyUpdateMainScreen(boolean isOnlyUpdateUi) {
        checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);
        if (mGuider.mMainIsDeviceConnected && mGuider.mIsMainLoaded) {
            loadBinData();
            updateGainUi();
            parseResData();
            CameraSupportCooling();

            workModeChangeExpData();
            if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                changeResViewData(MENU_SINGLE_TRIGGER_MODE, mIsPictureViewVisibility);
            }

            startNowTempTimer();
            onlyUpdateDeviceWorkMode(mGuider.mMainCaptureMode, isOnlyUpdateUi);
        } else {
        }

        if (mGuider.mMainIsDeviceConnected && !mGuider.mIsMainRtspSource) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    mGuider.setRtspSource(true);
                }
            }.start();
        }
        if (!isOnlyUpdateUi) {
            manageScopeConnection(mGuider.mScopeIsDeviceConnected, mGuider.mIsScopeLoaded);
        }
    }

    private void onlyUpdateDeviceWorkMode(String workMode, boolean isOnlyUpdateUi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (workMode.equals("")) {
                    onlyUpdateModeViewByTag("Single");
                } else {
                    onlyUpdateModeViewByTag(workMode);
                }
            }
        });
        switch (workMode) {
            case "Single": {
                enterMainSingleMode();
                break;
            }
            case "Video": {
                onlyUpdateVideoModeUi(isOnlyUpdateUi);
                break;
            }
        }
    }

    private void onlyUpdateModeViewByTag(String tag) {
        for (ToggleButton it : mMenuViewList) {
            if (it.getTag() != null) {
                if (tag.equals(it.getTag().toString())) {
                    it.setChecked(true);
                    onlyUpdateMenuModeCheck();
                } else {
                    it.setChecked(false);
                }
            }
        }
    }


    private void onlyUpdateMenuModeCheck() {
        if (binding.realTimeStackBtn.isChecked()) {
            mLastToggleBtn = binding.realTimeStackBtn;
        }

        if (binding.autoFocuseBtn.isChecked()) {
            mLastToggleBtn = binding.autoFocuseBtn;
        }

        if (binding.singleTriggerBtn.isChecked()) {
            mLastToggleBtn = binding.singleTriggerBtn;
        }

        if (binding.videoModeBtn.isChecked()) {
            mLastToggleBtn = binding.videoModeBtn;
        }

        if (binding.planPhotoBtn.isChecked()) {
            mLastToggleBtn = binding.planPhotoBtn;
            enterMainPlanMode();
        }
    }

    private void setDockToGuideBtnVisibility(){
        if (mMenuCurrentMode == MENU_VIDEO_MODE) {
            binding.dockToGuideBtn.setVisibility(View.INVISIBLE);
        } else {
            if (binding.leftLayout.chartPanelBtn.isChecked()){
                binding.dockToGuideBtn.setVisibility(View.VISIBLE);
            }else {
                binding.dockToGuideBtn.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void onlyUpdateVideoModeUi(boolean isOnlyUpdateUi) {
        mIsPictureViewVisibility = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMenuCurrentMode != MENU_VIDEO_MODE) {
                    enterVideoModeAndLoading();
                    mCircleStatus = CIRCLE_IDLE;
                    mMenuCurrentMode = MENU_VIDEO_MODE;
                    setTakePhotoMode(TakePhotoView.VIDEO_MODE_IDLE);
                }
                chooseVideoModeVisibleView();
                setDockToGuideBtnVisibility();
            }
        });
        workModeChangeExpData();
        if (!isOnlyUpdateUi) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                 //   videoModeStartCapture();
                }
            }.start();
        }
    }

    private void manageScopeConnection(boolean isDeviceConnected, boolean isDataLoaded) {
        if (isDeviceConnected) {
            startRaDecTask();
        } else {
            stopRaDecTask();
        }
        loadScopeView();
        manageFocuserStatus();
    }

    private void manageCameraConnection(boolean isDeviceConnected, boolean isDataLoaded) {
        if (isDeviceConnected && isDataLoaded) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    mGuider.setRtspSource(true);
                }
            }.start();
            loadBinData();
            updateGainUi();
            parseResData();
            CameraSupportCooling();

            workModeChangeExpData();
            if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                changeResViewData(MENU_SINGLE_TRIGGER_MODE, mIsPictureViewVisibility);
            }

            startNowTempTimer();
            deviceConnectedView();
            manageDeviceWorkMode(mGuider.mMainCaptureMode);
        } else {
        }
    }

    private void manageDeviceWorkMode(String currentMode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentMode.equals("")) {
                    checkCurrentViewByTag("Single");
                } else {
                    checkCurrentViewByTag(currentMode);
                }
            }
        });
        switch (currentMode) {
            case "Single": {
                enterMainSingleMode();
                break;
            }
            case "Video": {
                enterMainVideoMode();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 100: {
                gotoProgress(data.getDoubleExtra("target_ra", 0), data.getDoubleExtra("target_dec", 0));
                break;
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopRecordVideo();
        stopRaDecTask();
        mGuider.unregisterHandlerListener(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFpsTimer();
        mGuider.unregisterHandlerListener(mHandler);
    }

    private void exitScreenMainActivity() {
        //此处的考虑是，如果退出了当前的页面，那么，就没必要发送一些接口请求了，停止一些计时器了。
        stopNowTempTimer();
        hideModeMenu();
        if (mLib != null){
            mLib.setPreviewHandler(null);
        }
        mGuider.unregisterHandlerListener(mHandler);
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
        Log.e(TAG, "resbtn dispatchTouchEvent: cannotclick" );

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "resbtn dispatchTouchEvent ACTION_UP: cannotclick" );
                LogUtil.writeTouchDelayFile("dispatchTouchEvent ACTION_UP");
               // hideSoftInput();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "resbtn dispatchTouchEvent ACTION_CANCEL: cannotclick" );
                LogUtil.writeTouchDelayFile("dispatchTouchEvent ACTION_CANCEL");
                break;
            case MotionEvent.ACTION_DOWN: {
                mClickRawX = (int) ev.getRawX();
                mClickRawY = (int) ev.getRawY();
                LogUtil.writeTouchDelayFile("dispatchTouchEvent ACTION_DOWN");
                Log.e(TAG, "resbtn dispatchTouchEvent ACTION_DOWN: cannotclick" );
                if (getCurrentFocus() instanceof EditText) {
                    Log.e(TAG, "clickCenterCircle: dispatchTouchEvent ACTION_DOWN EditText cannotclick");
                    new Thread() {
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
                                        Rect rect = new Rect();
                                        view.getGlobalVisibleRect(rect); //获得控件在屏幕上的显示区域
                                        //判断：如果点击区域不在控件中
                                        if (!rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                                            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.settingPanel.blankArea.getWindowToken(), 0);
                                            view.clearFocus(); //清除焦点
                                        } else {
                                            new Thread() {
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
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @NonNull
    @Override
    public Loader<Bundle> onCreateLoader(int id, @androidx.annotation.Nullable Bundle args) {
        Log.e(TAG, "onCreateLoader: ");
        mFileLoader = new FileLoader(this, args);
        mFileLoader.setPreviewHandler(mDownloadHandler);
        return mFileLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Bundle> loader, Bundle data) {
        if (data == null) return;
        if (data.getBoolean(FileLoader.KEY_SUCCESS)) {
            mCurrentFileName = data.getString(FileLoader.KEY_RESULT);
            handleOpenFitsFrame(mCurrentFileName);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Bundle> loader) {

    }

    private void initPopMenu() {
        mGainDataList.clear();
        for (int i = 0; i < 101; i++) {
            mGainDataList.add(i + "");
        }

        mClickMenuAdapter = new ClickMenuAdapter(mClickMenuList, this);
        mClickMenuAdapter.setOnClickListener(new ClickMenuAdapterListener() {
            @Override
            public void chooseItem(int position) {
                menuClickEvent(position);
            }

            @Override
            public void editAction() {
                if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                    if (binding.rightLayout.expTbtn.isChecked()) {
                        binding.editExpEt.getText().clear();
                        binding.editExpEt.setText(mEditExpValue.replace("s",""));
                        binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    int exposure = (int) (getFloatNumberNoUnit(mEditExpValue) * 1000);
                                    mGuider.setExposure("main", exposure);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        showSoftInputNoEvent(binding.editExpEt,true,true);
                    } else if (binding.rightLayout.tempTbtn.isChecked()) {
                        binding.editExpEt.getText().clear();
                        binding.editExpEt.setText(mEditTempValue);
                        binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.setTargetTemperature("main", Float.valueOf(mEditTempValue));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        showSoftInputNoEvent(binding.editExpEt,false,true);
                    }
                    binding.editExpContainer.setVisibility(View.VISIBLE);
                    binding.editExpEt.requestFocus();
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.clickMenuRv.setLayoutManager(linearLayoutManager);
        binding.clickMenuRv.setAdapter(mClickMenuAdapter);
    }

    private void resetPopMenuToInitStatus() {
        binding.leftLayout.modeMenuBtn.setChecked(false);
        binding.rightLayout.gainTbtn.setChecked(false);
        binding.rightLayout.expTbtn.setChecked(false);
        binding.rightLayout.binTbtn.setChecked(false);
        binding.rightLayout.tempTbtn.setChecked(false);
        binding.rightLayout.resTbtn.setChecked(false);
        binding.clickMenuRvContainer.setVisibility(View.GONE);
        binding.seekbarContainer.setVisibility(View.INVISIBLE);
    }

    private void menuClickEvent(int position) {
        if (binding.rightLayout.expTbtn.isChecked()) {
            mCurrentExpIndex = position;
            String text = mClickMenuList.get(position).mValue;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + text;
                    formatDeviceParameterShowInfo();
                }
            });
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        mExposureValue = (int) (getFloatNumberNoUnit(text) * 1000);
                        Log.e(TAG, "run: menuClickEvent" + mExposureValue);
                        mGuider.setExposure("main", mExposureValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (binding.rightLayout.binTbtn.isChecked()) {
            mCurrentBinIndex = position;
            String text = mGuider.mMainBinningDataList.get(position);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCurrentBin = "Bin: " + text;
                    formatDeviceParameterShowInfo();
                }
            });
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        mGuider.setBinning("main", parseBinNumber(text));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (binding.rightLayout.tempTbtn.isChecked()) {
            mCurrentTemperatureIndex = position;
            String text = mClickMenuList.get(position).mValue;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        if (text.equals("Off")) {
                            if (mGuider.setCooling("main", false)) {
                                mGuider.mMainCooling = "close";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTargetTemp = getTargetTemp(text + "");
                                        formatDeviceParameterShowInfo();
                                    }
                                });
                            }
                        } else {
                            Float tempValue = Float.parseFloat(text);
                            mTargetTempValue = tempValue.doubleValue();
                            if (mGuider.mMainCooling.equals("close")) {
                                if (mGuider.setCooling("main", true)) {
                                    mGuider.mMainCooling = "open";
                                    if (mGuider.setTargetTemperature("main", tempValue)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mTargetTemp = getTargetTemp(text + "℃");
                                                formatDeviceParameterShowInfo();
                                            }
                                        });
                                    }
                                }
                            } else {
                                if (mGuider.setTargetTemperature("main", tempValue)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTargetTemp = getTargetTemp(text + "℃");
                                            formatDeviceParameterShowInfo();
                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (binding.rightLayout.resTbtn.isChecked()) {
            mCurrentResIndex = position;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        mGuider.setPushResolution("main", position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {

        }
    }

    //decision area
    public void enterMainFromWorkState() {
        checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);
        formatCurrentDeviceShowInfo();
        if (mGuider.mWorkStates.containsKey("main")) {
            if (mGuider.mWorkStates.get("main").equals("CaptureLooping")) {
                exitVideoModeLoading();
                if (mGuider.mIsMainRtspSource) {
                    startStream();
                }
            }
        }
    }

    //mode control area
    public void enterMainVideoMode() {
        clearPictureDownloading();
        resetHistogramView();
        mIsPictureViewVisibility = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetPopMenuToInitStatus();
                if (mMenuCurrentMode != MENU_VIDEO_MODE) {
                    enterVideoModeAndLoading();
                    mCircleStatus = CIRCLE_IDLE;
                    mMenuCurrentMode = MENU_VIDEO_MODE;
                    setTakePhotoMode(TakePhotoView.VIDEO_MODE_IDLE);
                }
                chooseVideoModeVisibleView();
                setDockToGuideBtnVisibility();
            }
        });
    }

    public void enterMainSingleMode() {
        enterMainSingleModeView();
    }

    public void enterMainPlanMode() {
        enterPlanModeView();
    }

    public void enterPlanModeView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetPopMenuToInitStatus();
                if (mMenuCurrentMode != MENU_PHOTO_PLAN_MODE) {
                    mCircleStatus = CIRCLE_IDLE;
                    mMenuCurrentMode = MENU_PHOTO_PLAN_MODE;
                    setTakePhotoMode(TakePhotoView.PLAN_SHOOT_MODE_IDLE);
                    choosePlanModeVisibleView();
                }
            }
        });
    }

    public void enterMainSingleModeView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetPopMenuToInitStatus();
                Log.e(TAG, "run:enterMainSingleModeView " + mMenuCurrentMode);
                if (mMenuCurrentMode != MENU_SINGLE_TRIGGER_MODE) {
                    mCircleStatus = CIRCLE_IDLE;
                    mMenuCurrentMode = MENU_SINGLE_TRIGGER_MODE;
                    setTakePhotoMode(TakePhotoView.SINGLE_TRIGGER_MODE_IDLE);
                }
                syncSingleModeVisibleView();
                setDockToGuideBtnVisibility();
            }
        });
    }

    public void syncSingleModeVisibleView() {
        hideRightLayoutAllElement();
        binding.leftLayout.goToBtn.setVisibility(View.VISIBLE);
        binding.leftLayout.chartPanelBtn.setVisibility(View.VISIBLE);
        binding.rightLayout.binTbtn.setVisibility(View.VISIBLE);
        binding.rightLayout.expTbtn.setVisibility(View.VISIBLE);
        binding.rightLayout.gainTbtn.setVisibility(View.VISIBLE);
        binding.rightLayout.tempTbtn.setVisibility(View.VISIBLE);
        binding.rightLayout.enterTaskBtn.setVisibility(View.INVISIBLE);
        binding.rightLayout.browserBtn.setBackground(getResources().getDrawable(R.drawable.select_picture_btn_bg, null));
        changeResViewData(MENU_SINGLE_TRIGGER_MODE, mIsPictureViewVisibility);
        workModeChangeExpData();
    }

    public void chooseVideoModeVisibleView() {
        hideRightLayoutAllElement();
        binding.leftLayout.goToBtn.setVisibility(View.VISIBLE);
        binding.leftLayout.chartPanelBtn.setVisibility(View.VISIBLE);

        binding.rightLayout.expTbtn.setVisibility(View.VISIBLE);
        binding.rightLayout.gainTbtn.setVisibility(View.VISIBLE);
        binding.rightLayout.binTbtn.setVisibility(View.VISIBLE);
        binding.rightLayout.resTbtn.setVisibility(View.VISIBLE);

        binding.rightLayout.browserBtn.setBackground(getResources().getDrawable(R.drawable.select_video_btn_bg, null));
    }

    public void choosePlanModeVisibleView() {
        hideRightLayoutAllElement();
        binding.leftLayout.goToBtn.setVisibility(View.INVISIBLE);
        binding.leftLayout.chartPanelBtn.setVisibility(View.VISIBLE);
        binding.rightLayout.enterTaskBtn.setVisibility(View.VISIBLE);
        binding.rightLayout.browserBtn.setBackground(getResources().getDrawable(R.drawable.select_picture_btn_bg, null));
    }

    //stream area

    public void releaseStream() {
        if (mLib != null) {
            mLib.releaseCamera();
            Log.e(TAG, "releaseStream: ");
            LogUtil.writeLogtoFile("mLib.releaseCamera()");
        }
    }

    private void startStream() {
   //     LogUtil.writeWarnErrorToFile("startStream");
        if (mLib != null) {
            if (mLib.isAlive() == false) {
                mLib.OpenCamera(mGuider.host);
                mLib.startCameraStream();
            } else {
                Log.d(TAG, "stream is playing");
            }
        }

        LogUtil.writeLogtoFile("startStream()");
    }

    public void updateVideoRender(int width, int height) {
        Log.d(TAG, "update: frame come mainstream"+mStreamFpsCnt+":"+mGuider.mIsMainRtspSource
        +" width" + width+"height"+height);
       // LogUtil.writeWarnErrorToFile("update: frame come mainstream");
        if (!mGuider.mMainIsDeviceConnected || !mGuider.mIsMainRtspSource) {
            return;
        }
        mStreamRestartCnt = 0;
        if (mFpsTimerTask == null) {
            startFpsTimer();
        }
        if (mWidth != width || mHeight != height) {
            initSize(width, height);
            mWidth = width;
            mHeight = height;
            LogUtil.writeDebugFile(width + ":" + height);
            Log.d(TAG, "update : " + "width: " + width + "height" + height);
        }
        mStreamFpsCnt++;
        if (mMenuCurrentMode == MENU_VIDEO_MODE) {
            changeResViewData(MENU_VIDEO_MODE, false);
            binding.dockToGuideBtn.setVisibility(View.INVISIBLE);
        }
        if (mWidth != 0 && mHeight != 0) {
            if (mMenuCurrentMode == MENU_VIDEO_MODE) {
                binding.surface.mRender.setFrameSource(false);
            }
            updateVideoFrame();
        }
    }
    private void updatePictureRender(int width, int height) {
        Log.d(TAG, "updateLocal: frame come mainstream isPictureSource"+mStreamFpsCnt+":"+mGuider.mIsMainRtspSource
                +" width" + width+"height"+height);
        if (!mGuider.mMainIsDeviceConnected) {
            return;
        }
        if (mWidth != width || mHeight != height) {
            initSize(width, height);
            mWidth = width;
            mHeight = height;
        }
        if (mWidth != 0 && mHeight != 0) {
            if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                changeResViewData(MENU_SINGLE_TRIGGER_MODE, mIsPictureViewVisibility);
                binding.surface.mRender.setFrameSource(true);
            }
            updatePictureFrame();
        }
    }

    private void resetHistogramView() {
        float[] finalData = new float[256];
        binding.dockHistorgramPanel.histogramChart.setLineVisible(false);
        updateHistogramViewD(finalData);
        binding.dockHistorgramPanel.moveView.setMinNumber(0);
        binding.dockHistorgramPanel.moveView.setMaxNumber(255);
    }

    private void updateVideoFrame() {
        ByteBuffer mDirectBuffer = binding.surface.mRender.getDirectBuffer();
        if (mDirectBuffer != null) {
            if (mLib != null) {
                if (mMenuCurrentMode == MENU_VIDEO_MODE) {
                    int[] histData = mLib.updateJVideoFrame(mDirectBuffer);
                    float[] finalData = new float[histData.length];
                    float maxScale = 1;
                    float maxData = 0;
                    for (int i = 0; i < histData.length; i++) {
                        if (histData[i] > maxData) {
                            maxData = histData[i];
                        }
                    }
                    maxScale = maxData / 5000f;

                    for (int i = 0; i < histData.length; i++) {
                        finalData[i] = histData[i] / maxScale;
                    }

                    binding.dockHistorgramPanel.histogramChart.setLineVisible(true);
                    updateHistogramViewD(finalData);
                }
            } else {
                Log.e(TAG, "updateFrame: mLib is null");
            }
        } else {
            Log.e(TAG, "updateFrame: mDirectBuffer is null");
        }
        binding.surface.requestRender();
    }

    private void updatePictureFrame() {
        ByteBuffer mDirectBuffer = binding.surface.mRender.getDirectBuffer();
        if (mDirectBuffer != null) {
            if (mLib != null) {
                if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                    if (mIsPictureViewVisibility) {
                        int[] histData = mLib.updateJPictureFrame(mDirectBuffer, Constant.PATH_PICTURES + mCurrentFileName);
                        if (!mHistogramName.equals(mCurrentFileName)) {
                            mHistogramName = mCurrentFileName;
                            float[] finalData = new float[histData.length];
                            float maxScale = 1;
                            float maxData = 0;
                            for (int i = 0; i < histData.length; i++) {
                                if (histData[i] > maxData) {
                                    maxData = histData[i];
                                }
                            }
                            maxScale = maxData / 5000f;

                            for (int i = 0; i < histData.length; i++) {
                                finalData[i] = histData[i] / maxScale;
                            }

                            binding.dockHistorgramPanel.histogramChart.setLineVisible(true);
                            updateHistogramViewD(finalData);
                            binding.dockHistorgramPanel.moveView.resetNumber();
                        }
                    }
                }

            } else {
                Log.e(TAG, "updateFrame: mLib is null");
            }
        } else {
            Log.e(TAG, "updateFrame: mDirectBuffer is null");
        }
        binding.surface.requestRender();
    }

    public void resetFrame() {
        LogUtil.writeLogtoFile("main resetFrame");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ByteBuffer mDirectBuffer = binding.surface.mRender.getDirectBuffer();
                if (mDirectBuffer != null) {
                    if (mLib != null) {
                        mLib.clearMainFrame(mDirectBuffer, mDirectBuffer.capacity());
                    } else {
                    }
                } else {
                }
                resetHistogramView();

                binding.surface.requestRender();
                mIsPictureViewVisibility = false;
            }
        });
    }

    private void updateFitsPicture(String fileName) {
        if (mLib != null) {
            mIsPictureViewVisibility = true;
            int[] size = mLib.loadFitsPicture(Constant.PATH_PICTURES + fileName);
            updatePictureRender(size[0],size[1]);
        }
    }

    private void initSize(int videoWidth, int videoHeight) {
        binding.surface.mRender.setCurrentVideoSize(videoWidth, videoHeight);// todo
    }

    public void warnError() {
        LogUtil.writeWarnErrorToFile("main stream warnError");
        try {
            if (mLib != null) {
                if (mLib.isAlive() == true) {
                    if (mStreamRestartCnt == 0) {
                        LogUtil.writeGuideFps0ToFile("主相机 拉流第一次重启 开始调用releaseStream() 当前fps计数为"+mStreamFpsCnt);
                        releaseStream();
                        LogUtil.writeGuideFps0ToFile("主相机 拉流第一次重启 开始调用startStream() 当前fps计数为"+mStreamFpsCnt);
                        startStream();
                        LogUtil.writeGuideFps0ToFile("主相机 拉流第一次重启结束 当前fps计数为"+mStreamFpsCnt);
                        mStreamRestartCnt++;
                    } else {
                        releaseStream();
                        ToastUtil.showToast(App.INSTANCE.getResources().getString(R.string.video_transform_fail), false);
                    }
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterVideoModeAndLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = new DeviceChangingDialog.Builder(this)
                .setIsCancelable(true)
                .setIsCancelOutside(false)
                .create();
        mLoadingDialog.show();
        startVideoLoadingTimer();
    }

    private void exitVideoModeLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        stopVideoLoadingTimer();
    }

    private void startRecordVideo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String VideoPath = PATH_VIDEOS + simpleDateFormat.format(date) + ".mp4";
        if (mLib != null) {
            mLib.startRecordVideo(VideoPath);
        }
        startVideoTimer();
        disableBinScroller();
        disableResScroller();
        setModeMenuEnable(false);
        mIsRecordingVideo = true;
        if (mGoToPop != null) {
            mGoToPop.setGotoEnabled(false);
        }
    }

    private void stopRecordVideo() {
        if (mIsRecordingVideo) {
            stopVideoTimer();
            stopTimer();
            takePhotoIdle();
            if (mLib != null) {
                if (mLib.stopRecordVideo()) {
                    ToastUtil.showToast(getResources().getString(R.string.video_save_success), false);
                } else {
                    ToastUtil.showToast( getResources().getString(R.string.video_save_fail), false);
                }
            }
            enableResScroller();
            enableBinScroller();
            Log.e(TAG, "stopRecordVideo: video");
            setModeMenuEnable(true);
            mIsRecordingVideo = false;
        }
        if (mGoToPop != null) {
            mGoToPop.setGotoEnabled(true);
        }
    }

    //init area
    private void init() {
        initView();
        initData();
        initEvent();
        dockCtrlAreaEvent();
        mEditExpValue = SharedPreferencesUtil.getInstance().getString(Constant.SP_MAIN_EXP_EDIT_ID, "1.0s");
        mEditTempValue = SharedPreferencesUtil.getInstance().getString(Constant.SP_MAIN_TEMP_EDIT_ID, "20.0");
        try {
            if (mGuider.mWorkStates.containsKey("main")) {
                if (mGuider.mWorkStates.get("main").equals("CaptureLooping")) {
                    if (mGuider.mIsMainRtspSource) {
                        startStream();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void hideAllSettingPanel() {
        binding.settingPanelContainer.setVisibility(View.INVISIBLE);
        hideFragment();
    }

    private void hideModeMenu() {
        binding.modeMenuContainer.setVisibility(View.INVISIBLE);
    }

    private void cancelDownloading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsPictureDownloading = false;
                setModeMenuEnable(true);
                setTakePhotoBtnEnabled(true);
                if (mFileLoader != null) {
                    mFileLoader.cancelDownload(true);
                }
            }
        });
    }

    private void showGotoPop(){
        Rect rect = new Rect();
        binding.leftLayout.goToBtn.getGlobalVisibleRect(rect);
        int y = (int) (rect.bottom  - binding.leftLayout.goToBtn.getHeight()/2 - mGoToPop.getContentView().getMeasuredHeight()/2);
        mGoToPop.showAtLocation( binding.leftLayout.goToBtn, Gravity.TOP | Gravity.LEFT,
                binding.leftLayoutContainer.getWidth()+ScreenUtil.androidAutoSizeDpToPx(getResources().getInteger(R.integer.pop_margin_side)),y);
    }
    private void showFocuserPop(){
        Rect rect = new Rect();
        binding.leftLayout.focuserBtn.getGlobalVisibleRect(rect);
        int y = (int) (rect.bottom - binding.leftLayout.focuserBtn.getHeight()/2 - mFocuserPop.getContentView().getMeasuredHeight()/2);
        mFocuserPop.showAtLocation(binding.leftLayout.goToBtn, Gravity.TOP | Gravity.LEFT,
                binding.leftLayoutContainer.getWidth() + ScreenUtil.androidAutoSizeDpToPx(getResources().getInteger(R.integer.pop_margin_side)),y);
        mFocuserPop.updateFocuserPop();
    }

    private void initEvent() {
        initModeMenuEvent();
        binding.leftLayout.focuserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideModeMenu();
                resetPopMenuToInitStatus();
                showFocuserPop();
            }
        });
        binding.rightLayout.photoCancelDownloadingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDownloadTimer();
                clearPictureDownloading();
            }
        });
        binding.dockHistorgramPanel.restBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dockHistorgramPanel.moveView.resetNumber();
            }
        });
        binding.editExpEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.editExpEt.clearFocus();
                ((InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.editExpEt.getWindowToken(), 0);
                return true;
            }
        });
        binding.editExpEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (binding.rightLayout.expTbtn.isChecked()) {
                        String expValue = binding.editExpEt.getText().toString();
                        Log.e(TAG, "onFocusChange: replace expValue before"+expValue );
                        expValue = expValue.replace("s","");
                        Log.e(TAG, "onFocusChange: replace expValue after"+expValue );
                        if (CheckParamUtil.isRangeLegal(ScreenMainActivity.this, expValue, mGuider.mMainExpMin.doubleValue(), mGuider.mMainExpMax.doubleValue(), 1000.0, "s")) {
                            Double value = Double.valueOf(expValue);
                            String temp = String.format("%.3f", value);
                            String strPrice = Double.parseDouble(temp)+"";
                            binding.editExpEt.getText().clear();
                            binding.editExpEt.setText(strPrice);
                            expValue = strPrice;
                            Log.e(TAG, "onFocusChange: expValue"+expValue );
                            if (expValue.contains(".")) {
                                expValue += "s";
                            } else {
                                expValue += ".0s";
                            }
                            ArrayList<String> tempMainExpDataList = mGuider.getMainExpDataList();
                            mEditExpValue = expValue;
                            SharedPreferencesUtil.getInstance().putString(Constant.SP_MAIN_EXP_EDIT_ID, expValue);
                            mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + expValue;
                            formatDeviceParameterShowInfo();
                            mClickMenuList.clear();
                            for (String item : tempMainExpDataList) {
                                mClickMenuList.add(new ClickMenuItem(item, true));
                            }
                            mClickMenuList.add(new ClickMenuItem(mEditExpValue, true));
                            mCurrentExpIndex = mClickMenuList.size() - 1;

                            mClickMenuAdapter.setSelectedIndex(mCurrentExpIndex);
                            mClickMenuAdapter.notifyDataSetChanged();
                            binding.clickMenuRv.moveToCenterPosition(mCurrentExpIndex);
                            binding.editExpContainer.setVisibility(View.INVISIBLE);
                            mExposureValue = (int) (getFloatNumberNoUnit(expValue) * 1000);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        mGuider.setExposure("main", mExposureValue);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    } else if (binding.rightLayout.tempTbtn.isChecked()) {
                        String tempValue = binding.editExpEt.getText().toString();
                        if (CheckParamUtil.isRangeLegal(ScreenMainActivity.this, tempValue, mGuider.mMainMinTemp, mGuider.mMainMaxTemp, 1.0, "℃")) {
                            Double value = Double.valueOf(tempValue);
                            String temp = String.format("%.2f", value);
                            String strPrice = Double.parseDouble(temp)+"";
                            binding.editExpEt.getText().clear();
                            binding.editExpEt.setText(strPrice);
                            tempValue = strPrice;
                            String inputTempValue = tempValue;
                            if (!tempValue.contains(".")) {
                                tempValue += ".0";
                            }
                            mEditTempValue = tempValue;
                            mClickMenuList.clear();
                            for (String item : mGuider.mMainTargetTempDataList) {
                                mClickMenuList.add(new ClickMenuItem(item, true));
                            }

                            mTargetTemp = getTargetTemp(tempValue + "℃");
                            formatDeviceParameterShowInfo();
                            mClickMenuList.add(new ClickMenuItem(mEditTempValue, true));
                            mCurrentTemperatureIndex = mClickMenuList.size() - 1;
                            mClickMenuAdapter.setSelectedIndex(mCurrentTemperatureIndex);
                            mClickMenuAdapter.notifyDataSetChanged();
                            binding.clickMenuRv.moveToCenterPosition(mCurrentTemperatureIndex);
                            SharedPreferencesUtil.getInstance().putString(Constant.SP_MAIN_TEMP_EDIT_ID, tempValue);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        mTargetTempValue = Double.valueOf(inputTempValue);
                                        mGuider.setTargetTemperature("main", Float.valueOf(inputTempValue));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    }
                    binding.editExpContainer.setVisibility(View.INVISIBLE);
                    int index = mClickMenuList.size() - 1;
                    mClickMenuAdapter.setSelectedIndex(index);
                    mClickMenuAdapter.notifyDataSetChanged();
                    binding.clickMenuRv.moveToCenterPosition(index);
                    new Thread() {
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
                                    binding.clickMenuRv.scrollToPosition(mClickMenuList.size() + 1);
                                }
                            });
                        }
                    }.start();
                }
            }
        });
        binding.dockHistorgramPanel.moveView.setOnMoveBarChangeListener(new HistogramTick.OnChanged() {
            @Override
            public void onLeftMovePosition(float position) {
                int low = round(position);
                Log.e(TAG, "onLeftMovePosition: low" + low);
                if (mLib != null) {
                    mLib.setHistLow(low);
                    if (mIsPictureViewVisibility) {
                        updateFitsPicture(mCurrentPictureSource);
                    }
                }
            }

            @Override
            public void onRightMovePosition(float position) {
                int high = round(position);
                Log.e(TAG, "onLeftMovePosition: histHigh " + high);
                if (mLib != null) {
                    mLib.setHistHigh(high);
                    if (mIsPictureViewVisibility) {
                        updateFitsPicture(mCurrentPictureSource);
                    }
                }
            }

            @Override
            public void onRelease() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.dockHistorgramPanel.moveHistorgramPositionTv.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCurrentPosition(float position) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "onCurrentPosition: high" + round(position));
                        binding.dockHistorgramPanel.moveHistorgramPositionTv.setVisibility(View.VISIBLE);
                        binding.dockHistorgramPanel.moveHistorgramPositionTv.setText("" + round(position));
                    }
                });
            }
        });

        binding.expSeekBar.setOnSeekBarChangeListener(new ProgressBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                if (binding.rightLayout.expTbtn.isChecked()) {
                    // mExpSeekBarIndex = (int) ((progress + mVideoModeEXPMin) * Math.log1p(progress + mVideoModeEXPMin));
                    mExpSeekBarIndex = (int) (progress);
                    long data = (long) (expSlider2Real(mExpSeekBarIndex) / 1000.0);
                    mExpSeekBarIndex = (int) data;
                    binding.expSeekBar.setBarDegree(data + "");

                    if (fromUser) {
                        if (mMenuCurrentMode == MENU_VIDEO_MODE) {
                            mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + (data) + "ms";
                            formatDeviceParameterShowInfo();
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {
                if (binding.rightLayout.expTbtn.isChecked()) {
                    if (mMenuCurrentMode == MENU_VIDEO_MODE) {
                        mExposureValue = mExpSeekBarIndex;
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Log.e(TAG, "run:  onStopTrackingTouch");
                                    mGuider.setExposure("main", mExposureValue);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
            }
        });
        binding.gainSeekBar.setOnSeekBarChangeListener(new ProgressBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                if (binding.rightLayout.gainTbtn.isChecked()) {
                    mCurrentGain = App.INSTANCE.getResources().getString(R.string.Gain) + ": " + progress;
                    formatDeviceParameterShowInfo();
                    binding.gainSeekBar.setBarDegree(progress + "");
                }
                mSeekBarIndex = progress;
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {
                if (binding.rightLayout.gainTbtn.isChecked()) {
                    mCurrentGainIndex = mSeekBarIndex;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.setGain("main", mCurrentGainIndex);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

        binding.rightLayout.gainTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealTypeConflict(binding.rightLayout.gainTbtn);
                LogUtil.writeTouchDelayFile("rightLayout gain checked:"+isChecked);
                if (isChecked) {
                    hideModeMenu();
                    binding.gainSeekBar.setType(getResources().getString(R.string.Gain));
                    binding.seekbarContainer.setVisibility(View.VISIBLE);
                    binding.gainSeekBar.setVisibility(View.VISIBLE);
                    binding.expSeekBar.setVisibility(View.GONE);
                    binding.gainSeekBar.setBarMax(100);
                    binding.gainSeekBar.setBarMin(0);
                    binding.gainSeekBar.setProgress(mCurrentGainIndex);
                } else {
                    binding.gainSeekBar.setVisibility(View.GONE);
                    binding.seekbarContainer.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.clickMenuRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0)) > 0) {
                    binding.menuRvTopCover.setVisibility(View.VISIBLE);
                } else {
                    binding.menuRvTopCover.setVisibility(View.GONE);
                }

                if (recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1)) < recyclerView.getAdapter().getItemCount() - 1) {
                    binding.menuRvBottomCover.setVisibility(View.VISIBLE);
                } else {
                    binding.menuRvBottomCover.setVisibility(View.GONE);
                }
            }
        });
        binding.rightLayout.expTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealTypeConflict(binding.rightLayout.expTbtn);
                LogUtil.writeTouchDelayFile("rightLayout exp checked:"+isChecked);
                try {
                    if (isChecked) {
                        hideModeMenu();
                        // InvisibleDockPanelView();
                        if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                            binding.clickMenuRvContainer.setVisibility(View.VISIBLE);
                            mClickMenuList.clear();
                            ArrayList<String> tempMainExpDataList = mGuider.getMainExpDataList();
                            ArrayList<Integer> tempMainExpDataValueList = mGuider.getMainExpDataValueList();
                            for (String item : tempMainExpDataList) {
                                mClickMenuList.add(new ClickMenuItem(item, true));
                            }
                            LogUtil.writeConstraintToFile("单帧模式下，曝光时间mGuider.getmMainExpDataList()：" + mGuider.getMainExpDataList());
                            Log.e(TAG, "onCheckedChanged: mExposureValue 1" + mExposureValue);
                            //此处需要用来修正从视频模式转换过来的exp的曝光时间
                            if (tempMainExpDataValueList.contains(mExposureValue)) {
                                if (mCurrentExpIndex == -1) {
                                    mCurrentExpIndex = tempMainExpDataValueList.indexOf(mExposureValue);
                                }
                                mClickMenuList.add(new ClickMenuItem(mEditExpValue, true));
                            } else {
                                mEditExpValue = (((float) mExposureValue) / 1000) + "s";
                                mClickMenuList.add(new ClickMenuItem(mEditExpValue, true));
                                if (mCurrentExpIndex == -1) {
                                    mCurrentExpIndex = mClickMenuList.size() - 1;
                                }
                            }
                            String editSelection = mExposureValue / 1000 + "";
                            binding.editExpEt.getText().clear();
                            binding.editExpEt.setText(editSelection);

                            binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                            mClickMenuAdapter.setSelectedIndex(mCurrentExpIndex);
                            mClickMenuAdapter.notifyDataSetChanged();
                            binding.clickMenuRv.moveToCenterPosition(mCurrentExpIndex);
                        } else if (mMenuCurrentMode == MENU_VIDEO_MODE) {
                            if (mGuider.mMainExpRange.size() != 0) {
                                mVideoModeEXPMin = mGuider.mMainExpRange.get(0);
                                if (mGuider.mMainExpRange.get(1) > VIDEO_MODE_MAX_EXP) {
                                    mVideoModeEXPMax = (int) VIDEO_MODE_MAX_EXP;
                                } else {
                                    mVideoModeEXPMax = mGuider.mMainExpRange.get(1);
                                }
                                expSliderRange((long) (mVideoModeEXPMin * 1000), mVideoModeEXPMax * 1000);
                            }
                            mCurrentExpIndex = -1;
                            binding.expSeekBar.setType(getResources().getString(R.string.Exp));
                            binding.expSeekBar.setBarMax(mVideoModeEXPMax);
                            binding.expSeekBar.setBarMin(mVideoModeEXPMin);

                            if ((int) (expSlider2Real(binding.expSeekBar.getProgress()) / 1000.0) != mExposureValue) {
                                binding.expSeekBar.setProgress(real2ExpSlider((long) (mExposureValue * 1000)));
                            }
                            binding.seekbarContainer.setVisibility(View.VISIBLE);
                            binding.expSeekBar.setVisibility(View.VISIBLE);
                            binding.gainSeekBar.setVisibility(View.GONE);
                        }

                    } else {
                        binding.editExpContainer.setVisibility(View.INVISIBLE);
                        binding.expSeekBar.setVisibility(View.GONE);
                        binding.seekbarContainer.setVisibility(View.INVISIBLE);
                        binding.clickMenuRvContainer.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        binding.rightLayout.binTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealTypeConflict(binding.rightLayout.binTbtn);
                Log.e(TAG, "onCheckedChanged: cannotclick binTbtn"+isChecked );
                LogUtil.writeTouchDelayFile("rightLayout bin checked:"+isChecked);
                try {
                    if (isChecked) {
                        hideModeMenu();
                        binding.clickMenuRvContainer.setVisibility(View.VISIBLE);
                        mClickMenuList.clear();
                        for (String item : mGuider.mMainBinningDataList) {
                            mClickMenuList.add(new ClickMenuItem(item));
                        }

                        mClickMenuAdapter.setSelectedIndex(mCurrentBinIndex);
                        mClickMenuAdapter.notifyDataSetChanged();
                        binding.clickMenuRv.moveToCenterPosition(mCurrentBinIndex);
                    } else {
                        binding.clickMenuRvContainer.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        binding.rightLayout.tempTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealTypeConflict(binding.rightLayout.tempTbtn);
                LogUtil.writeTouchDelayFile("rightLayout temp checked:"+isChecked);
                try {
                    if (isChecked) {
                        hideModeMenu();
                        binding.clickMenuRvContainer.setVisibility(View.VISIBLE);
                        mClickMenuList.clear();
                        for (String item : mGuider.mMainTargetTempDataList) {
                            mClickMenuList.add(new ClickMenuItem(item, true));
                        }
                        binding.editExpEt.getText().clear();
                        binding.editExpEt.setText(mEditTempValue);
                        binding.editExpEt.setSelection(binding.editExpEt.getText().length());
                        mClickMenuList.add(new ClickMenuItem(mEditTempValue, true));
                        mClickMenuAdapter.setSelectedIndex(mCurrentTemperatureIndex);
                        mClickMenuAdapter.notifyDataSetChanged();
                        binding.clickMenuRv.moveToCenterPosition(mCurrentTemperatureIndex);
                    } else {
                        binding.clickMenuRvContainer.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        binding.rightLayout.resTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e(TAG, "resbtn onCheckedChanged: cannotclick" );
                dealTypeConflict(binding.rightLayout.resTbtn);
                LogUtil.writeTouchDelayFile("rightLayout res checked:"+isChecked);
                try {
                    if (isChecked) {
                        hideModeMenu();
                        binding.clickMenuRvContainer.setVisibility(View.VISIBLE);
                        mClickMenuList.clear();
                        for (String item : mResDataList) {
                            mClickMenuList.add(new ClickMenuItem(item));
                        }
                        mClickMenuAdapter.setSelectedIndex(mCurrentResIndex);
                        mClickMenuAdapter.notifyDataSetChanged();
                        binding.clickMenuRv.moveToCenterPosition(mCurrentResIndex);
                    } else {
                        binding.clickMenuRvContainer.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        binding.leftLayout.chartPanelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateChartInformationView();
                resetPopMenuToInitStatus();
                if (mPanelList.contains(PANEL_C) && mPanelList.contains(PANEL_H)) {
                    mPanelList.remove(PANEL_C);
                    setDockMode(MODE_HISTOR_SINGLE);
                    if (mLastMode == MODE_SPLIT) {
                        setDockHistogramPanelFlag(getResources().getString(R.string.historgram));
                    } else {
                        binding.dockHistorLabelTv.setVisibility(View.VISIBLE);
                        setDockHistogramPanelFlag("");
                    }
                } else if (!mPanelList.contains(PANEL_C) && mPanelList.contains(PANEL_H)) {
                    mPanelList.add(PANEL_C);
                    if (mLastMode == MODE_SPLIT) {
                        binding.guideChartContainer.setVisibility(View.VISIBLE);
                        setDockMode(MODE_SPLIT);
                    } else {
                        binding.guideChartContainer.setVisibility(View.VISIBLE);
                        setDockTabMode(0);
                    }
                } else if (!mPanelList.contains(PANEL_C)) {
                    mPanelList.add(PANEL_C);
                    binding.guideChartContainer.setVisibility(View.VISIBLE);
                    setDockToGuideBtnVisibility();
                    if (mLastMode == MODE_SPLIT) {
                        setDockMode(MODE_CHART_SINGLE);
                        setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
                    } else {
                        setDockMode(MODE_CHART_SINGLE);
                        setDockGuidePanelFlag("");
                        binding.dockChartLabelTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    mPanelList.clear();
                    hideAllDockPanelView();
                }
                setTabSwitchBg(mLastMode);
            }
        });

        binding.leftLayout.histogramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPopMenuToInitStatus();
                if (mPanelList.contains(PANEL_C) && mPanelList.contains(PANEL_H)) {
                    mPanelList.remove(PANEL_H);
                    setDockMode(MODE_CHART_SINGLE);
                    if (mLastMode == MODE_SPLIT) {
                        setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
                    } else {
                        binding.dockChartLabelTv.setVisibility(View.VISIBLE);
                        setDockGuidePanelFlag("");
                    }
                } else if (!mPanelList.contains(PANEL_H) && mPanelList.contains(PANEL_C)) {
                    mPanelList.add(PANEL_H);
                    if (mLastMode == MODE_SPLIT) {
                        binding.histogramPanelContainer.setVisibility(View.VISIBLE);
                        setDockMode(MODE_SPLIT);
                    } else {
                        binding.histogramPanelContainer.setVisibility(View.VISIBLE);
                        setDockTabMode(1);
                    }
                } else if (!mPanelList.contains(PANEL_H)) {
                    mPanelList.add(PANEL_H);
                    binding.histogramPanelContainer.setVisibility(View.VISIBLE);
                    if (mLastMode == MODE_SPLIT) {
                        setDockMode(MODE_HISTOR_SINGLE);
                        setDockHistogramPanelFlag(getResources().getString(R.string.historgram));
                    } else {
                        setDockMode(MODE_HISTOR_SINGLE);
                        setDockHistogramPanelFlag("");
                        binding.dockHistorLabelTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    mPanelList.clear();
                    hideAllDockPanelView();
                }
                setTabSwitchBg(mLastMode);
            }
        });
        binding.leftLayout.modeMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.modeMenuContainer.getVisibility() == View.VISIBLE) {
                    hideModeMenu();
                } else {
                    binding.modeMenuContainer.setVisibility(View.VISIBLE);
                }
                dealTypeConflict(binding.leftLayout.modeMenuBtn);
            }
        });

        binding.leftLayout.goToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPopMenuToInitStatus();
                hideModeMenu();
                if (mGoToPop == null) {
                    initGoto();
                }
                mGoToPop.refreshFavoriteView();
                mGoToPop.setGotoEnabled(!mIsRecordingVideo);
                if (mGuider.mScopePark) {
                    if (mUnParkDialog != null) {
                        mUnParkDialog.show();
                    }
                } else {
                    showGotoPop();
                    mGoToPop.setGotoPark(false);
                }
            }
        });

        binding.leftLayout.rootBrowserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecordVideo();
                exitScreenMainActivity();
                BrowserActivity.actionStart(ScreenMainActivity.this, "root");
            }
        });

        binding.leftLayout.settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.settingPanelContainer.getVisibility() == View.VISIBLE) {
                    hideAllSettingPanel();
                } else {
                    initTab(mGuider.mSettingCurrentPosition);
                    binding.settingPanelContainer.setVisibility(View.VISIBLE);
                    resetPopMenuToInitStatus();
                }
            }
        });
        binding.rightLayout.takePhotoBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "clickCenterCircle: click");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideModeMenu();
                    resetPopMenuToInitStatus();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsPictureDownloading) {
                                return;
                            } else {
                            }
                            if (mGuider.mMainIsDeviceConnected) {
                                clickCenterCircle();
                            } else {
                            }
                        }
                    });
                }
                return true;
            }
        });

        binding.rightLayout.takePhotoBtn.setStatusListener(new TakePhotoView.StatusListener() {
            @Override
            public void backToIdle() {
                Log.d(TAG, "backToIdle: time out ");
                stopTimer();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsPictureDownloading) {
                        } else {
                            mIsPictureDownloading = true;
                            setTakePhotoBtnEnabled(false);
                            pictureDownloading(0, 100);
                        }
                        takePhotoIdle();
                    }
                });
            }
        });

        binding.rightLayout.enterTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitScreenMainActivity();
                TaskActivity.actionStart(ScreenMainActivity.this);
            }
        });

        binding.rightLayout.browserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecordVideo();
                switch (mMenuCurrentMode) {
                    case MENU_VIDEO_MODE: {
                        exitScreenMainActivity();
                        BrowserActivity.actionStart(ScreenMainActivity.this, "videos");
                    }
                    break;
                    case MENU_PHOTO_PLAN_MODE: {
                        exitScreenMainActivity();
                        BrowserActivity.actionStart(ScreenMainActivity.this, "images_task");
                    }
                    break;
                    case MENU_SINGLE_TRIGGER_MODE:
                    case MENU_INIT: {
                        exitScreenMainActivity();
                        BrowserActivity.actionStart(ScreenMainActivity.this, "images");
                    }
                    break;
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
                } else if (mPanelMode == MODE_HISTOR_SINGLE) {
                    dockSingleHSplitTabChange();
                } else if (mPanelMode == MODE_CHART_SINGLE) {
                    dockSingleCSplitTabChange();
                } else if (mPanelMode == MODE_SINGLE) {
                    if (mPanelList.contains(PANEL_H)) {
                        dockSingleHSplitTabChange();
                    } else {
                        dockSingleCSplitTabChange();
                    }
                }
            }
        });

        binding.dockCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mPanelMode) {
                    case MODE_SPLIT: {
                        binding.dockGuideChartPanel.centerLine.setVisibility(View.VISIBLE);
                        hideAllDockPanelView();
                        mPanelList.clear();
                    }
                    case MODE_CHART_SINGLE:
                    case MODE_HISTOR_SINGLE:
                    case MODE_SINGLE: {
                        binding.dockGuideChartPanel.centerLine.setVisibility(View.GONE);
                        hideAllDockPanelView();
                        mPanelList.clear();
                    }
                    case MODE_TAB: {
                        binding.dockGuideChartPanel.centerLine.setVisibility(View.GONE);
                        if (mPanelList.size() == 2) {
                            if (binding.guideChartContainer.getVisibility() == View.VISIBLE) {
                                binding.histogramPanelContainer.setVisibility(View.VISIBLE);
                                binding.dockChartLabelTv.setChecked(false);
                                binding.leftLayout.chartPanelBtn.setChecked(false);
                                mPanelList.remove(PANEL_C);
                                setDockMode(MODE_HISTOR_SINGLE);
                            } else {
                                binding.guideChartContainer.setVisibility(View.VISIBLE);
                                binding.dockHistorLabelTv.setChecked(false);
                                binding.leftLayout.histogramBtn.setChecked(false);
                                mPanelList.remove(PANEL_H);
                                setDockMode(MODE_CHART_SINGLE);
                            }
                        } else if (mPanelList.size() == 1) {
                            hideAllDockPanelView();
                            mPanelList.clear();
                        }
                    }
                }
                binding.leftLayout.chartPanelBtn.setChecked(binding.guideChartContainer.getVisibility() == View.VISIBLE);
                binding.leftLayout.histogramBtn.setChecked(binding.histogramPanelContainer.getVisibility() == View.VISIBLE);
            }
        });
        binding.dockToGuideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: dockToGuideBtn");
                releaseStream();
                exitScreenMainActivity();
                GuideActivity.actionStart(ScreenMainActivity.this, "main");
            }
        });

        binding.dockChartLabelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDockCurrentTabId = 0;
                binding.dockChartLabelTv.setChecked(true);
                if (binding.dockHistorLabelTv.isChecked())
                    binding.dockHistorLabelTv.setChecked(false);

                binding.guideChartContainer.setVisibility(View.VISIBLE);
                setDockToGuideBtnVisibility();
                binding.histogramPanelContainer.setVisibility(View.INVISIBLE);
            }
        });

        binding.dockHistorLabelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDockCurrentTabId = 1;
                binding.dockHistorLabelTv.setChecked(true);
                if (binding.dockChartLabelTv.isChecked())
                    binding.dockChartLabelTv.setChecked(false);
                binding.histogramPanelContainer.setVisibility(View.VISIBLE);
                binding.guideChartContainer.setVisibility(View.INVISIBLE);
            }
        });

        binding.closeSettingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAllSettingPanel();
            }
        });

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mIsNumberInputShow){
                    mIsNumberInputShow = false;
                }
                else if (binding.seekbarContainer.getVisibility() == View.VISIBLE
                        || binding.clickMenuRvContainer.getVisibility() == View.VISIBLE
                        || binding.modeMenuContainer.getVisibility() == View.VISIBLE) {
                    resetPopMenuToInitStatus();
                    hideModeMenu();
                } else if (binding.rightLayoutContainer.getVisibility() == View.VISIBLE) {
                    binding.rightLayoutContainer.setVisibility(View.GONE);
                    binding.leftLayoutContainer.setVisibility(View.GONE);
                    if (mGoToPop != null) {
                        mGoToPop.dismiss();
                    }
                    if (mFocuserPop != null){
                        mFocuserPop.dismiss();
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
                mGoToPop.setGotoPark(false);
                showGotoPop();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mGuider.setScopePark(false);
                    }
                }.start();
                mUnParkDialog.dismiss();
            }

            @Override
            public void cancel() {
                mUnParkDialog.dismiss();
                mGoToPop.setGotoPark(true);
                showGotoPop();
            }
        });
    }

    private void setTabSwitchBg(int currentMode) {
        if (currentMode == MODE_SPLIT) {
            binding.dockTabSwitchBtn.setBackground(getDrawable(R.drawable.ic_split_normal));
        } else {
            binding.dockTabSwitchBtn.setBackground(getDrawable(R.drawable.ic_tab_normal));
        }
    }

    private void dockSingleHSplitTabChange() {
        if (mLastMode == MODE_SPLIT) {
            binding.dockHistorLabelTv.setVisibility(View.VISIBLE);
            binding.dockChartLabelTv.setVisibility(View.GONE);
            binding.dockHistorLabelTv.setChecked(true);
            setDockHistogramPanelFlag("");
            mLastMode = MODE_TAB;
        } else {
            binding.dockHistorLabelTv.setVisibility(View.GONE);
            setDockHistogramPanelFlag(getResources().getString(R.string.historgram));
            mLastMode = MODE_SPLIT;
        }
        setTabSwitchBg(mLastMode);
    }

    private void dockSingleCSplitTabChange() {
        if (mLastMode == MODE_SPLIT) {
            binding.dockChartLabelTv.setVisibility(View.VISIBLE);
            binding.dockHistorLabelTv.setVisibility(View.GONE);
            binding.dockChartLabelTv.setChecked(true);
            setDockGuidePanelFlag("");
            mLastMode = MODE_TAB;
        } else {
            binding.dockChartLabelTv.setVisibility(View.GONE);
            setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
            mLastMode = MODE_SPLIT;
        }
        setTabSwitchBg(mLastMode);
    }

    private void initView() {
        checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);
        mUnParkBuilder = new ParkDialog.Builder(ScreenMainActivity.this)
                .setmMessageContent(getResources().getString(R.string.current_is_park))
                .setmMessageCancelContent(getResources().getString(R.string.cancel_unpark))
                .setmMessageConfirmContent(getResources().getString(R.string.confirm_unpark));
        mUnParkDialog = mUnParkBuilder.create();
        initFocuserPop();
        initChart();
        initPopMenu();
        initSettingPanel();
        updateChartInformationView();
        try {
            initFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.dockChartLabelTv.setText(getResources().getString(R.string.chart_label));
        binding.dockHistorLabelTv.setText(getResources().getString(R.string.historgram));
    }

    private void initDockChartView() {
        binding.dockGuideChartPanel.dxDyCb.setChecked(mGuider.mIsMainDxDyVisibility);
        binding.dockGuideChartPanel.raDecCb.setChecked(mGuider.mIsMainRaDecVisibility);
        binding.dockGuideChartPanel.snrCb.setChecked(mGuider.mIsMainSnrVisibility);
        binding.dockGuideChartPanel.masCb.setChecked(mGuider.mIsMainMassVisibility);
    }

    private void updateHistogramViewD(float[] data) {
        List<HistogramLineView.LineValue> lineValues = new ArrayList();
        for (int i = 0; i < data.length; i++) {
            HistogramLineView.LineValue lineValue = new HistogramLineView.LineValue(data[i], i);
            lineValues.add(lineValue);
        }

        binding.dockHistorgramPanel.moveView.setMaxNumber((float) data.length - 1);
        binding.dockHistorgramPanel.moveView.setMinNumber(0);
        List<HistogramLineView.XValue> xValues = new ArrayList();
        int value = 0;
        if (data.length == 256) {
            value = 0;
            HistogramLineView.XValue xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 50;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 100;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 150;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 200;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 255;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
        } else if (data.length == 65536) {
            value = 0;
            HistogramLineView.XValue xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 10000;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 20000;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 30000;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 40000;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 50000;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 60000;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
            value = 65535;
            xValue = new HistogramLineView.XValue(value, value + "");
            xValues.add(xValue);
        }
        binding.dockHistorgramPanel.histogramChart.setValue(lineValues, xValues);
    }

    private void initGoto() {
        mGoToPop = new GotoPop(ScreenMainActivity.this);
        mGoToPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mGoToPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initFocuserPop(){
        mFocuserPop = new FocuserPop(ScreenMainActivity.this);
        mFocuserPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mFocuserPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initData() {
        mToggleBtnList.add(binding.rightLayout.gainTbtn);
        mToggleBtnList.add(binding.rightLayout.expTbtn);
        mToggleBtnList.add(binding.rightLayout.binTbtn);
        mToggleBtnList.add(binding.rightLayout.tempTbtn);
        mToggleBtnList.add(binding.rightLayout.resTbtn);
        mToggleBtnList.add(binding.leftLayout.modeMenuBtn);
        formatDeviceParameterShowInfo();
        formatCurrentDeviceShowInfo();
    }

    private void initFragment() throws IllegalAccessException, InstantiationException {
        if (mFragments.isEmpty()) {
            for (TabItem it : mTabs) {
                Fragment f = it.mFragmentCls.newInstance();
                mFragments.add(f);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (Fragment it : mFragments) {
                if (!it.isAdded()) transaction.add(
                        R.id.fl_content, it, it.getClass().getSimpleName()
                ).hide(it);
            }
            transaction.commit();
        } else {
        }
    }

    private void initSettingPanel() {
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
        for (int i = 0; i < mFragments.size(); i++) {
            if (i != position){
                transaction.hide(mFragments.get(i));
            }
        }
        transaction.show(mFragments.get(position));
        transaction.commit();
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

        mCombinedChartManager = new CombinedChartManager(binding.dockGuideChartPanel.chart);
    }

    //view show area

    private void loadScopeView() {
        manageScopeStatus();
    }

    private void changeResViewData(int mMode, boolean isPictureSource) {
        try {
            if (mMode == MENU_VIDEO_MODE) {
                mCurrentRes = mWidth + "x" + mHeight;
                formatDeviceParameterShowInfo();
            } else if (mMode == MENU_SINGLE_TRIGGER_MODE) {
                if (isPictureSource) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentRes = mWidth + "x" + mHeight;
                            formatDeviceParameterShowInfo();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentRes = "";
                            if (mGuider.mMainResDataList.size() != 0) {
                                if (mGuider.mMainCurrentResolution != -1) {
                                    mCurrentRes = "" + mGuider.mMainResDataList.get(mGuider.mMainCurrentResolution);
                                }
                            }
                            formatDeviceParameterShowInfo();
                        }
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateGainUi() {
        int gain = mGuider.mMainGain;
        if (gain == -1) return;
        updateGainCache(gain);
    }

    private void updateGainCache(int gain) {
        mCurrentGainIndex = gain;
        mCurrentGain = App.INSTANCE.getResources().getString(R.string.Gain) + ": " + mGainDataList.get(mCurrentGainIndex);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                formatDeviceParameterShowInfo();
            }
        });
    }

    private void workModeChangeExpData() {
        mExposureValue = mGuider.mMainExpData;
        ArrayList<String> tempMainExpDataList = mGuider.getMainExpDataList();
        mCurrentExpIndex = tempMainExpDataList.indexOf(((double) mExposureValue / 1000) + "s");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((mMenuCurrentMode == MENU_VIDEO_MODE)) {
                    if (mExposureValue > VIDEO_MODE_MAX_EXP) {
                        mExposureValue = (int) VIDEO_MODE_DEF_EXP;
                        mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + mExposureValue + "ms";
                        mCurrentExpIndex = tempMainExpDataList.indexOf(((double) mExposureValue / 1000) + "s");
                    } else {
                        mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + mExposureValue + "ms";
                    }
                } else {
                    mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + ((double) mExposureValue / 1000) + "s";
                }
                formatDeviceParameterShowInfo();
            }
        });
    }

    private void videoModeStartCapture() {
        if (mExposureValue > VIDEO_MODE_MAX_EXP) {
            mExposureValue = (int) VIDEO_MODE_DEF_EXP;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        mGuider.setExposure("main", mExposureValue);
                        mGuider.setRtspSource(true);
                        mGuider.startCapture("main");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    mGuider.setRtspSource(true);
                    mGuider.startCapture("main");
                }
            }.start();
        }
    }

    private void updateExpEvent(int exposure) {
        if (mExposureValue == exposure) {
            return;
        }
        mExposureValue = mGuider.mMainExpData;
        mCurrentExpIndex = mGuider.getMainExpDataList().indexOf(((double) mExposureValue / 1000) + "s");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((mMenuCurrentMode == MENU_VIDEO_MODE)) {
                    mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + mExposureValue + "ms";
                } else {
                    mCurrentExp = App.INSTANCE.getResources().getString(R.string.Exp) + ": " + ((double) mExposureValue / 1000) + "s";
                }
                formatDeviceParameterShowInfo();
                if (binding.rightLayout.expTbtn.isChecked()) {

                }
            }
        });

    }

    private void tempTbtnStatus() {
        String coolingStatus = mGuider.mMainCooling;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (coolingStatus.equals("error")) {
                    mIsCameraHasCooling = false;
                    binding.rightLayout.tempTbtn.setEnabled(false);
                } else {
                    mIsCameraHasCooling = true;
                    binding.rightLayout.tempTbtn.setEnabled(true);
                }
            }
        });
    }

    private void deviceConnectedView() {
        tempTbtnStatus();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setModeMenuEnable(true);
                binding.rightLayout.gainTbtn.setEnabled(true);
                binding.rightLayout.expTbtn.setEnabled(true);
                binding.rightLayout.binTbtn.setEnabled(true);
                binding.rightLayout.resTbtn.setEnabled(true);
            }
        });
    }

    private void deviceDisconnectedView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.title.rightTv.setText("");
                binding.bottom.centerTv.setText("");
                binding.bottom.rightTv.setText("");
                setModeMenuEnable(false);
                binding.rightLayout.gainTbtn.setEnabled(false);
                binding.rightLayout.expTbtn.setEnabled(false);
                binding.rightLayout.binTbtn.setEnabled(false);
                binding.rightLayout.tempTbtn.setEnabled(false);
                binding.rightLayout.resTbtn.setEnabled(false);
                binding.rightLayout.browserBtn.setBackground(getResources().getDrawable(R.drawable.select_picture_btn_bg, null));

                setDockToGuideBtnVisibility();
            }
        });
    }

    private String getTargetTemp(String temp) {
        if (mIsCameraHasCooling) {
            return App.INSTANCE.getResources().getString(R.string.target_temp) + ": " + temp;
        } else {
            return "";
        }
    }

    /*这里除了刷新了bin的ui，还标记了下次点击的时候，当前的下标在哪个位置*/
    private void loadBinData() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int bin = mGuider.mMainBinning;
                    if (bin != -1) {
                        /*bin data list*/
                        mCurrentBinIndex = mGuider.mMainBinningDataList.indexOf(bin + "x" + bin);
                        if (mCurrentBinIndex != -1) {
                            mCurrentBin = "Bin: " + mGuider.mMainBinningDataList.get(mCurrentBinIndex);
                            formatDeviceParameterShowInfo();
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*目前这个部分的逻辑是检查是否支持制冷，并且处理目标温度是否存在的情况*/
    private void CameraSupportCooling() {
        Double targetTemp = mGuider.mMainTargetTemp;
        mTargetTempValue = targetTemp;
        if (mGuider.mMainCooling.equals("open")) {
            if (!String.valueOf(targetTemp).equals("null")) {
                mCurrentTemperatureIndex = mGuider.mMainTargetTempDataList.indexOf(String.valueOf(targetTemp));
                if (mCurrentTemperatureIndex == -1) {
                    mEditTempValue = targetTemp.toString();
                    mCurrentTemperatureIndex = mGuider.mMainTargetTempDataList.size();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTargetTemp = getTargetTemp(targetTemp + "℃");
                        formatDeviceParameterShowInfo();
                    }
                });
            }
        } else if (mGuider.mMainCooling.equals("close")) {
            mCurrentTemperatureIndex = mGuider.mMainTargetTempDataList.indexOf(String.valueOf(targetTemp));
            if (mCurrentTemperatureIndex == -1) {
                mEditTempValue = targetTemp.toString();
            }
            mCurrentTemperatureIndex = 0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTargetTemp = getTargetTemp("Off" + "");
                    formatDeviceParameterShowInfo();
                }
            });
        } else {
            mTargetTemp = getTargetTemp("");
            formatDeviceParameterShowInfo();
        }

        if (binding.rightLayout.tempTbtn.isChecked()) {
            mClickMenuList.clear();
            for (String item : mGuider.mMainTargetTempDataList) {
                mClickMenuList.add(new ClickMenuItem(item, true));
            }
            mClickMenuList.add(new ClickMenuItem(mEditTempValue, true));
            mClickMenuAdapter.setSelectedIndex(mCurrentTemperatureIndex);
            mClickMenuAdapter.notifyDataSetChanged();
            binding.clickMenuRv.moveToCenterPosition(mCurrentTemperatureIndex);
        }
    }

    /*这个就是用来解析返回来的值*/
    private void parseResData() {
        ArrayList<JSONObject> resolutions = mGuider.getMainLocalPushResolutions();
        mCurrentResIndex = mGuider.getMainLocalPushCurrentResolution();
        mCurrentResIndex = mCurrentResIndex > 0 ? mCurrentResIndex : 0;
        ArrayList<String> resolutionList = new ArrayList<String>();
        ArrayList<String> resolutionValueList = new ArrayList<String>();
        for (JSONObject it : resolutions) {
            try {
                resolutionValueList.add(it.getInt("width") + "x" + it.getInt("height"));
                resolutionList.add("" + it.getInt("height") + "p");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mResDataList.clear();
        mResDataValueList.clear();
        mResDataValueList.addAll(resolutionValueList);
        mResDataList.addAll(resolutionList);
    }

    private void enableBinScroller() {
        binding.rightLayout.binTbtn.setEnabled(true);
    }

    private void disableBinScroller() {
        binding.rightLayout.binTbtn.setEnabled(false);
    }

    private void enableResScroller() {
        binding.rightLayout.resTbtn.setEnabled(true);
        binding.rightLayout.resTbtn.setVisibility(View.VISIBLE);
    }

    private void disableResScroller() {
        binding.rightLayout.resTbtn.setEnabled(false);
        binding.rightLayout.resTbtn.setVisibility(View.VISIBLE);
    }

    //view control area

    private void hideFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment it : mFragments) {
            if (it.isVisible()) transaction.hide(it);
        }
        transaction.commitAllowingStateLoss();
    }

    public void dockCtrlAreaEvent() {
        binding.dockGuideChartPanel.dxDyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGuider.mIsMainDxDyVisibility = isChecked;
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
                mGuider.mIsMainRaDecVisibility = isChecked;
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
                mGuider.mIsMainSnrVisibility = isChecked;
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
                mGuider.mIsMainMassVisibility = isChecked;
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

    public void hideRightLayoutAllElement() {
        binding.rightLayout.gainTbtn.setVisibility(View.INVISIBLE);
        binding.rightLayout.expTbtn.setVisibility(View.INVISIBLE);
        binding.rightLayout.tempTbtn.setVisibility(View.INVISIBLE);
        binding.rightLayout.binTbtn.setVisibility(View.INVISIBLE);
        binding.rightLayout.resTbtn.setVisibility(View.INVISIBLE);
        binding.rightLayout.enterTaskBtn.setVisibility(View.INVISIBLE);
    }

    private void clearPictureDownloading() {
        cancelDownloading();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.rightLayout.photoCancelDownloadingTv.setVisibility(View.INVISIBLE);
                binding.rightLayout.photoDownloadProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setTakePhotoMode(int mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.rightLayout.takePhotoBtn.setMode(mode);
            }
        });
    }

    public void clickCenterCircle() {
        Log.d(TAG, "clickCenterCircle: " + mCircleStatus + "mMenuCurrentMode" + mMenuCurrentMode);
        if (mCircleStatus == CIRCLE_IDLE) {
            switch (mMenuCurrentMode) {
                case MENU_VIDEO_MODE: {
                    startRecordVideo();
                    mCircleStatus = CIRCLE_VIDEO_DOING;
                    setTakePhotoMode(TakePhotoView.VIDEO_MODE_DOING);
                }
                break;
                case MENU_PHOTO_PLAN_MODE: {
                    mCircleStatus = CIRCLE_DOING;
                    binding.rightLayout.takePhotoBtn.setCircleMaxNumber(4);
                    startTimer(4L, 4);
                    setTakePhotoMode(TakePhotoView.PLAN_SHOOT_MODE_DOING);
                }
                break;
                case MENU_SINGLE_TRIGGER_MODE: {
                    mCircleStatus = CIRCLE_READY;
                    setModeMenuEnable(false);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            boolean result = mGuider.captureSingleFrame(mExposureValue, "main");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!result) {
                                        clearPictureDownloading();
                                        ToastUtil.showToast(App.INSTANCE.getResources().getString(R.string.capture_frame_fail), false);
                                    }
                                }
                            });
                        }
                    }.start();
                    int number = mExposureValue > 10000 ? 500 : 100;
                    binding.rightLayout.takePhotoBtn.setCircleMaxNumber(number);
                    setTakePhotoMode(TakePhotoView.SINGLE_TRIGGER_MODE_READY);
                    double duration = ((double) mExposureValue / (double) number);
                    binding.rightLayout.takePhotoBtn.setDuration(duration);
                    startTimer((long) duration, 0);
                }
                break;
            }
        } else {
            if (mCircleStatus == CIRCLE_SINGLE_DOING) {
                if (mCount != 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            mGuider.stopCapture("main");
                        }
                    }.start();
                }
                setModeMenuEnable(true);
                stopTimer();
                takePhotoIdle();
            } else if (mCircleStatus == CIRCLE_VIDEO_DOING) {
                stopRecordVideo();
                takePhotoIdle();
            }
        }
    }

    private void takePhotoIdle() {
        mCircleStatus = CIRCLE_IDLE;
        switch (mMenuCurrentMode) {
            case MENU_VIDEO_MODE: {
                setTakePhotoMode(TakePhotoView.VIDEO_MODE_IDLE);
            }
            break;
            case MENU_PHOTO_PLAN_MODE: {
                setTakePhotoMode(TakePhotoView.PLAN_SHOOT_MODE_IDLE);
            }
            break;
            case MENU_SINGLE_TRIGGER_MODE: {
                setTakePhotoMode(TakePhotoView.SINGLE_TRIGGER_MODE_IDLE);
            }
            break;
        }
        stopTimer();
    }
    //dock control area

    private void setDockTabMode(int currentDockId) {
        mDockCurrentId = currentDockId;
        mDockCurrentTabId = currentDockId;
        setDockMode(MODE_TAB);
    }

    private void setDockMode(int mode) {
        setDockHistogramPanelFlag("");
        setDockGuidePanelFlag("");
        switch (mode) {
            case MODE_SINGLE: {
                LogUtil.writeConstraintToFile("SINGLE模式");
                mPanelMode = MODE_SINGLE;
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                binding.dockChartLabelTv.setVisibility(View.INVISIBLE);
                binding.dockHistorLabelTv.setVisibility(View.INVISIBLE);
                binding.dockGuideChartPanel.centerLine.setVisibility(View.GONE);
            }
            break;
            case MODE_SPLIT: {
                mPanelMode = MODE_SPLIT;
                binding.guideChartContainer.setVisibility(View.VISIBLE);
                binding.histogramPanelContainer.setVisibility(View.VISIBLE);

                LogUtil.writeConstraintToFile("SPLIT模式");
                ConstraintSet constraint = new ConstraintSet();

                constraint.clone(this, R.layout.activity_screen_main);

                constraint.connect(
                        binding.guideChartContainer.getId(),
                        ConstraintSet.END,
                        binding.histogramPanelContainer.getId(),
                        ConstraintSet.START, 0
                );

                constraint.connect(
                        binding.histogramPanelContainer.getId(),
                        ConstraintSet.START,
                        binding.guideChartContainer.getId(),
                        ConstraintSet.END, 0
                );

                constraint.applyTo(binding.screenMainActivityLayout);

                binding.dockGuideChartPanel.centerLine.setVisibility(View.VISIBLE);
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setBackground(getDrawable(R.drawable.ic_split_normal));
                binding.dockChartLabelTv.setVisibility(View.INVISIBLE);
                binding.dockHistorLabelTv.setVisibility(View.INVISIBLE);
                binding.histogramPanelContainer.setVisibility(View.VISIBLE);
                binding.guideChartContainer.setVisibility(View.VISIBLE);
                setDockHistogramPanelFlag(getResources().getString(R.string.historgram));
                setDockGuidePanelFlag(getResources().getString(R.string.guidechart));
            }
            break;
            case MODE_TAB: {
                int id;
                if (mDockCurrentId == 1) {
                    id = binding.histogramPanelContainer.getId();
                    LogUtil.writeConstraintToFile("TAB模式，当前的id是直方图的");
                } else {
                    id = binding.guideChartContainer.getId();
                    LogUtil.writeConstraintToFile("TAB模式，当前的id是guide导星的");
                }

                dockSplitToNormal(id);
                mPanelMode = MODE_TAB;

                if (id == binding.histogramPanelContainer.getId()) {
                    binding.histogramPanelContainer.setVisibility(View.VISIBLE);
                    binding.dockChartLabelTv.setChecked(false);
                    binding.dockHistorLabelTv.setChecked(true);
                } else {
                    binding.dockChartLabelTv.setChecked(true);
                    binding.guideChartContainer.setVisibility(View.VISIBLE);
                    binding.dockHistorLabelTv.setChecked(false);
                }
                binding.dockGuideChartPanel.centerLine.setVisibility(View.GONE);
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                binding.dockChartLabelTv.setVisibility(View.VISIBLE);
                binding.dockHistorLabelTv.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setBackground(getDrawable(R.drawable.ic_tab_normal));
            }
            break;
            case MODE_CHART_SINGLE: {
                mPanelMode = MODE_CHART_SINGLE;
                LogUtil.writeConstraintToFile("CHART_SINGLE模式");
                dockSplitToNormal(binding.guideChartContainer.getId());
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                //  binding.dockChartLabelTv.setVisibility(View.VISIBLE);
                binding.dockChartLabelTv.setChecked(true);
                binding.dockHistorLabelTv.setVisibility(View.GONE);
                binding.guideChartContainer.setVisibility(View.VISIBLE);
                binding.dockGuideChartPanel.centerLine.setVisibility(View.GONE);
            }
            break;
            case MODE_HISTOR_SINGLE: {
                mPanelMode = MODE_HISTOR_SINGLE;
                LogUtil.writeConstraintToFile("HISTOR_SINGLE模式");
                dockSplitToNormal(binding.histogramPanelContainer.getId());
                binding.dockGuideChartPanel.centerLine.setVisibility(View.GONE);
                binding.editContainer.setVisibility(View.VISIBLE);
                binding.dockCloseBtn.setVisibility(View.VISIBLE);
                binding.dockTabSwitchBtn.setVisibility(View.VISIBLE);
                binding.dockChartLabelTv.setVisibility(View.GONE);
                //  binding.dockHistorLabelTv.setVisibility(View.VISIBLE);
                binding.dockHistorLabelTv.setChecked(true);
                binding.histogramPanelContainer.setVisibility(View.VISIBLE);
            }
            break;
        }
        setDockToGuideBtnVisibility();
    }

    private void setDockGuidePanelFlag(String value) {
        if (value.equals("")) {
            binding.dockGuideChartPanel.panelFlagTv.setVisibility(View.GONE);
        } else {
            binding.dockGuideChartPanel.panelFlagTv.setVisibility(View.VISIBLE);
        }
        binding.dockGuideChartPanel.panelFlagTv.setText(value);
    }

    private void setDockHistogramPanelFlag(String value) {
        if (value.equals("")) {
            binding.dockHistorgramPanel.panelFlagTv.setVisibility(View.GONE);
        } else {
            binding.dockHistorgramPanel.panelFlagTv.setVisibility(View.VISIBLE);
        }
        binding.dockHistorgramPanel.panelFlagTv.setText(value);
    }

    private void dockSplitToNormal(int id) {
        ConstraintSet constraint = new ConstraintSet();

        constraint.clone(this, R.layout.activity_screen_main);
        constraint.connect(
                id,
                ConstraintSet.END,
                binding.editContainer.getId(),
                ConstraintSet.END,
                0
        );

        constraint.connect(
                id,
                ConstraintSet.START,
                binding.editContainer.getId(),
                ConstraintSet.START,
                0
        );

        constraint.applyTo(binding.screenMainActivityLayout);
    }

    private void hideAllDockPanelView() {
        binding.editContainer.setVisibility(View.GONE);
        binding.dockCloseBtn.setVisibility(View.GONE);
        binding.dockTabSwitchBtn.setVisibility(View.GONE);
        binding.dockChartLabelTv.setVisibility(View.GONE);
        binding.dockHistorLabelTv.setVisibility(View.GONE);
        binding.histogramPanelContainer.setVisibility(View.GONE);
        binding.guideChartContainer.setVisibility(View.GONE);
        binding.dockToGuideBtn.setVisibility(View.GONE);
        binding.dockChartLabelTv.setChecked(false);
        binding.dockHistorLabelTv.setChecked(false);
    }

    private void resetAllDockPanelView() {
        binding.editContainer.setVisibility(View.GONE);
        binding.dockCloseBtn.setVisibility(View.GONE);
        binding.dockTabSwitchBtn.setVisibility(View.GONE);
        binding.dockChartLabelTv.setVisibility(View.GONE);
        binding.dockHistorLabelTv.setVisibility(View.GONE);
        binding.histogramPanelContainer.setVisibility(View.GONE);
        binding.guideChartContainer.setVisibility(View.GONE);
        binding.dockToGuideBtn.setVisibility(View.GONE);
        binding.dockChartLabelTv.setChecked(false);
        binding.dockHistorLabelTv.setChecked(false);
        binding.leftLayout.histogramBtn.setChecked(false);
        binding.leftLayout.chartPanelBtn.setChecked(false);
        mPanelList.clear();
    }


    //event control area

    public void syncCaptureMode() {
        switch (mMenuCurrentMode) {
            case MENU_VIDEO_MODE: {
                mCaptureMode = "Video";
            }
            break;
            case MENU_PHOTO_PLAN_MODE: {
                mCaptureMode = "Sequence";
            }
            break;
            case MENU_SINGLE_TRIGGER_MODE: {
                mCaptureMode = "Single";
            }
            break;
        }
        if (!mCaptureMode.equals(mGuider.mMainCaptureMode)) {
            mCaptureMode = mGuider.mMainCaptureMode;
            checkCurrentViewByTag(mCaptureMode);
        }
    }

    public void rtspResChangedEvent() {
        if (!mGuider.mRtspResChangeDevice.equals("main")) {
            return;
        }
        /*释放相机，restart 拉流*/
        releaseStream();
        mCurrentResIndex = mGuider.mRtspResIndex;
    }

    public void rtspReadyEvent() {
        startStream();
    }

    public void captureLooping() {
        exitVideoModeLoading();
    }

    public void updateRtspSource() {
    }

    public void startLoadAllDataCache() {
        Log.e(TAG, "startLoadAllDataCache: ");
        mCheckConnectProcess.showDataLoadingDialog();
    }

    public void finishLoadAllDataCache() {
        mCheckConnectProcess.dismissDataLoadingDialog();
    }

    public void handleFrameSaved() {
        Log.d(TAG, "backToIdle: handleFrameSaved ");
        if (mGuider.mCaptureFrameSaved.mSuccess) {
            //  if (mPictureDownloadMsgCache.size() == 0){
            Bundle bundle = new Bundle();
            bundle.putString(FRAME_SAVED_NAME, mGuider.mCaptureFrameSaved.mFileName);
            bundle.putString(FRAME_SAVED_PATH, Constant.PATH_PICTURES);
            bundle.putInt(FRAME_LENGTH, (int) mGuider.mCaptureFrameSaved.mFileSize);
            if (mFileLoader != null) {
                mFileLoader.cancelDownload(false);
            }
            LoaderManager.getInstance(this).restartLoader(0, bundle, this).forceLoad();
            //startDownloadTimer();
            //  }
            //  mPictureDownloadMsgCache.add(mGuider.mCaptureFrameSaved.filename);
        } else {

        }
    }

    public void handleOpenFitsFrame(String fileName) {
        mCurrentPictureSource = fileName;
        updateFitsPicture(fileName);
    }

    public void formatCurrentDeviceShowInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mMainIsDeviceConnected) {
                    binding.title.statusTv.setText(getResources().getString(R.string.maincamera) + " [" + mGuider.mSelectedDevices.get("main") + "]");
                } else {
                    binding.title.statusTv.setText(getResources().getString(R.string.maincamera) + " [None]");
                }
            }
        });
    }

    private void formatDeviceParameterShowInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String info = "";
                info = mCurrentGain + "  " + mCurrentExp + "  " + mCurrentBin;
                if (!mTargetTemp.equals("")) {
                    info += "  " + mTargetTemp;
                }
                binding.title.rightTv.setText(info);
                if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                    binding.bottom.rightTv.setText(mCurrentRes + "  " + mGuider.mMainCurrentTemp);
                } else if (mMenuCurrentMode == MENU_VIDEO_MODE) {
                    binding.bottom.rightTv.setText(mCurrentRes + "  " + mFps + "  " + mGuider.mMainCurrentTemp);
                }
            }
        });
    }

    public void deviceConnectEvent() {
        manageScopeStatus();
        manageFocuserStatus();
        manageScopeConnection(mGuider.mScopeIsDeviceConnected, mGuider.mIsScopeLoaded);
        if (!mGuider.mCurrentConnectedDevice.equals("main")) {
            return;
        }
        if (!mGuider.mMainIsDeviceConnected) {
            checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);
            formatCurrentDeviceShowInfo();
            deviceDisconnectedView();
        }
    }

    public void gainChangedEvent() {
        if (mGuider.mGainChangedDevice.equals("main")) {
            updateGainCache(mGuider.mGainChangedValue);
        }
    }

    public void expChangedEvent() {
        if (mGuider.mExposureChangedDevice.equals("main")) {
            updateExpEvent(mGuider.mExposureChangedValue);
        }
    }

    public void binningChangedEvent() {
        try {
            if (mGuider.mBinningChangedDevice.equals("main")) {
                int bin = mGuider.mBinningChangedValue;
                mCurrentBinIndex = mGuider.mMainBinningDataList.indexOf(bin + "x" + bin);
                if (mCurrentBinIndex != -1) {
                    mCurrentBin = "Bin: " + mGuider.mMainBinningDataList.get(mCurrentBinIndex);
                    formatDeviceParameterShowInfo();
                }

                if (binding.rightLayout.binTbtn.isChecked()) {
                    mClickMenuAdapter.setSelectedIndex(mCurrentBinIndex);
                    mClickMenuAdapter.notifyDataSetChanged();
                    binding.clickMenuRv.moveToCenterPosition(mCurrentBinIndex);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void targetTempChangedEvent() {
        if (mTargetTempValue.equals(mGuider.mMainTargetTemp)) return;
        if (mGuider.mTargetTempChangedDevice.equals("main")) {
            CameraSupportCooling();
        }
    }

    public void pictureDownloading(int current, int total) {
        if (!mIsPictureDownloading) {
            if (mFileLoader != null) {
                mFileLoader.cancelDownload(true);
            }
            return;
        }
        startDownloadTimer();
        int progress = (int) (((double) current) / ((double) total) * 100);

        binding.rightLayout.photoDownloadProgress.setVisibility(View.VISIBLE);
        binding.rightLayout.photoCancelDownloadingTv.setVisibility(View.VISIBLE);
        binding.rightLayout.photoDownloadProgress.setProgress(progress);
        if (current >= total) {
            mFileLoader = null;
            syncCaptureMode();
            stopDownloadTimer();
            setModeMenuEnable(true);
            mIsPictureDownloading = false;
            setTakePhotoBtnEnabled(true);
            binding.rightLayout.photoCancelDownloadingTv.setVisibility(View.INVISIBLE);
            binding.rightLayout.photoDownloadProgress.setVisibility(View.INVISIBLE);
        }
    }


    public void chartVisibleIndicator() {
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
        if (mGuider.mIsMainDxDyVisibility) {
            mYLineData.add(mGuider.mValuesDX);
            mYLineData.add(mGuider.mValuesDY);
            mLineNames.add("dx");
            mLineNames.add("dy");
            mLineColors.add(getResources().getColor(R.color.blue));
            mLineColors.add(getResources().getColor(R.color.red));
        }
        if (mGuider.mIsMainMassVisibility) {
            mYLineData.add(mGuider.mValuesStarMass);
            mLineNames.add("StarMass");
            mLineColors.add(Color.WHITE);
        }
        if (mGuider.mIsMainSnrVisibility) {
            mYLineData.add(mGuider.mValuesSNR);
            mLineNames.add("SNR");
            mLineColors.add(Color.GREEN);
        }

        if (mGuider.mIsMainRaDecVisibility) {
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

        } else {
            yBarData.add(valuesRADuration);
            yBarData.add(valuesRADuration);
            mBarColors.add(getResources().getColor(R.color.purple_200));
            mBarColors.add(Color.YELLOW);
        }
        mCombinedChartManager.showCombinedChart(
                mGuider.maxisMaximum, yBarData, mYLineData, mBarNames, mLineNames,
                mBarColors, mLineColors
        );
    }

    public void resolutionChangedEvent() {
        changeResViewData(mMenuCurrentMode, mIsPictureViewVisibility);

        releaseStream();
    }

    private void updateVideoTimeTitle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMenuCurrentMode == MENU_VIDEO_MODE) {
                    binding.bottom.rightTv.setText(mCurrentRes + "  " + mFps + "  " + mGuider.mMainCurrentTemp);
                } else if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                } else {
                }
            }
        });
    }

    private void updateVideoTime() {
        int hour = 0;
        int minute = 0;
        int second = 0;
        hour = (int) (mVideoTimeCnt / 3600);
        minute = (int) ((mVideoTimeCnt - hour * 3600) / 60);
        second = (int) (mVideoTimeCnt - hour * 3600 - minute * 60);
        mVideoTime = formatTime(hour) + ":" + formatTime(minute) + ":" + formatTime(second);
        binding.bottom.centerTv.setText(mVideoTime);
    }

    private void updateFpsTitle(String fpsValue) {
        if (fpsValue.equals("")){
            mFps = "";
        }else {
            mFps = mStreamFpsCnt + "fps";
        }
        mStreamFpsCnt = 0;
        updateVideoTimeTitle();
    }

    //tool
    private void dealTypeConflict(ToggleButton toggleButton) {
        mToggleBtnList.remove(toggleButton);
        for (ToggleButton it : mToggleBtnList) {
            it.setChecked(false);
        }
        mToggleBtnList.add(toggleButton);
    }

    //time task control area

    private String formatTime(int cnt) {
        if (cnt < 10) {
            return "0" + cnt;
        } else return "" + cnt;
    }

    private class MyVideoTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mVideoTimeCnt++;
                    updateVideoTime();
                }
            });
        }
    }

    private void startVideoTimer() {
        if (mVideoTimer != null) {
            stopVideoTimer();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.bottom.centerTv.setVisibility(View.VISIBLE);
                updateVideoTime();
            }
        });
        mVideoTimer = new Timer(true);
        mVideoTimerTask = new MyVideoTimerTask();
        mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);
    }

    private void stopVideoTimer() {
        mVideoTimeCnt = 0;
        binding.bottom.centerTv.setVisibility(View.GONE);
        if (mVideoTimer != null) {
            mVideoTimer.cancel();
            mVideoTimer = null;
        }
        if (mVideoTimerTask != null) {
            mVideoTimerTask.cancel();
            mVideoTimerTask = null;
        }
    }

    private class MyFpsTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateFpsTitle(mStreamFpsCnt + "");
                    Log.e(TAG, "run: MyFpsTimerTask");
                }
            });
        }
    }

    private void startFpsTimer() {
        if (mFpsTimer != null) {
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
        updateFpsTitle("");
    }

    private class MyNowTempTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mGuider.mMainIsDeviceConnected) {
                Double temp = mGuider.getTemperature("main");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (temp != null) {
                            mGuider.mMainCurrentTemp = String.valueOf(temp) + "℃";
                        } else {
                           // mCurrentTemp = "";
                        }
                        formatDeviceParameterShowInfo();
                    }
                });
            }
        }
    }

    private void startNowTempTimer() {
        synchronized (mLock) {
            if (mCurrentTempTimer != null) {
                stopNowTempTimer();
            }
            mCurrentTempTimer = new Timer(true);
            mNowTempTimerTask = new MyNowTempTimerTask();
            mCurrentTempTimer.schedule(mNowTempTimerTask, 0, 1000);
        }
    }

    private void stopNowTempTimer() {
        synchronized (mLock) {
            if (mCurrentTempTimer != null) {
                mCurrentTempTimer.cancel();
                mCurrentTempTimer = null;
            }
            if (mNowTempTimerTask != null) {
                mNowTempTimerTask.cancel();
                mNowTempTimerTask = null;
            }
        }
    }

    private class DownloadTimerTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mLastDownloadTime > 15000) {
                stopDownloadTimer();
                clearPictureDownloading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast( App.INSTANCE.getResources().getString(R.string.capture_frame_fail), false);
                    }
                });
            } else {
            }
        }
    }

    private void startDownloadTimer() {
        stopDownloadTimer();
        mLastDownloadTime = System.currentTimeMillis();
        mDownloadTimer = new Timer(true);
        mDownloadTimerTask = new DownloadTimerTask();
        mDownloadTimer.schedule(mDownloadTimerTask, 0, 500);
    }

    private void stopDownloadTimer() {
        if (mDownloadTimer != null) {
            mDownloadTimer.cancel();
            mDownloadTimer = null;
        }
        if (mDownloadTimerTask != null) {
            mDownloadTimerTask.cancel();
            mDownloadTimerTask = null;
        }
    }

    private void startTimer(long period, int maxNumber) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mCount++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mMenuCurrentMode == MENU_SINGLE_TRIGGER_MODE) {
                                if (mCircleStatus == CIRCLE_READY) {
                                    mCircleStatus = CIRCLE_SINGLE_DOING;
                                    setTakePhotoMode(TakePhotoView.SINGLE_TRIGGER_MODE_DOING);
                                }
                                binding.rightLayout.takePhotoBtn.setCircleDegree(mCount);
                            } else if (mMenuCurrentMode == MENU_PHOTO_PLAN_MODE) {
                                binding.rightLayout.takePhotoBtn.setCircleDegree(mCount);
                            }
                        }
                    });
/*                    try {
                        Thread.sleep(period);
                    }catch (Exception e){
                        e.printStackTrace();
                        stopTimer();
                    }*/
                }
            };
            if (mTimer != null && mTimerTask != null) mTimer.schedule(
                    mTimerTask,
                    20, period > 0 ? period : 1
            );
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mCount = 0;
    }

    private class VideoModeTimerLoadingTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mVideoLoadingTimeCnt > (VIDEO_MODE_MAX_EXP + mExposureValue)) {
                exitVideoModeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getResources().getString(R.string.video_mode_timeout), false);
                    }
                });
            } else {
            }
        }
    }

    private void startVideoLoadingTimer() {
        stopVideoLoadingTimer();
        mVideoLoadingTimeCnt = System.currentTimeMillis();
        mVideoModeLoadingTimer = new Timer(true);
        mVideoModeTimerTask = new VideoModeTimerLoadingTask();
        mVideoModeLoadingTimer.schedule(mVideoModeTimerTask, 0, 1000);
    }

    private void stopVideoLoadingTimer() {
        if (mVideoModeLoadingTimer != null) {
            mVideoModeLoadingTimer.cancel();
            mVideoModeLoadingTimer = null;
        }
        if (mVideoModeTimerTask != null) {
            mVideoModeTimerTask.cancel();
            mVideoModeTimerTask = null;
        }
    }

    public void setTakePhotoBtnEnabled(boolean isEnabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mIsNetworkOnLine && mGuider.mMainIsDeviceConnected) {
                    if (mIsPictureDownloading) {
                        binding.rightLayout.takePhotoBtn.setNormalEnabled(false);
                    } else {
                        binding.rightLayout.takePhotoBtn.setNormalEnabled(isEnabled);
                    }
                } else {
                    binding.rightLayout.takePhotoBtn.setNormalEnabled(false);
                }
            }
        });
    }

    private void setModeMenuEnable(boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.leftLayout.modeMenuBtn.setEnabled(isEnable);
            }
        });
    }

    public void checkDeviceAndNetworkConnection(boolean isNetworkConnected, boolean isDeviceConnected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isNetworkConnected && isDeviceConnected) {
                    setTakePhotoBtnEnabled(true);
                    binding.title.rightTv.setVisibility(View.VISIBLE);
                    binding.rightLayout.gainTbtn.setEnabled(true);
                    binding.rightLayout.expTbtn.setEnabled(true);
                    binding.rightLayout.binTbtn.setEnabled(!mIsRecordingVideo);
                    tempTbtnStatus();
                    binding.rightLayout.resTbtn.setEnabled(!mIsRecordingVideo);
                    setModeMenuEnable(!mIsRecordingVideo);
                } else {
                    setTakePhotoBtnEnabled(false);
                    binding.title.rightTv.setVisibility(View.INVISIBLE);
                    binding.rightLayout.gainTbtn.setEnabled(false);
                    binding.rightLayout.expTbtn.setEnabled(false);
                    binding.rightLayout.binTbtn.setEnabled(false);
                    binding.rightLayout.tempTbtn.setEnabled(false);
                    binding.rightLayout.resTbtn.setEnabled(false);
                    setModeMenuEnable(false);

                    resetFrame();
                    if (mUnParkDialog != null) {
                        mUnParkDialog.dismiss();
                    }
                    hideModeMenu();

                    takePhotoIdle();
                    stopFpsTimer();
                    stopNowTempTimer();
                    stopVideoTimer();
                    stopDownloadTimer();
                    stopTimer();
                    exitVideoModeLoading();

                    mMenuCurrentMode = MENU_INIT;

                    resetPopMenuToInitStatus();

                    binding.title.statusTv.setText(getResources().getString(R.string.maincamera) + " [None]");
                    binding.title.rightTv.setText("");
                    binding.bottom.centerTv.setText("");
                    binding.bottom.rightTv.setText("");

                    stopRecordVideo();
                    mIsMainDeviceActive = false;
                }
                if (isNetworkConnected) {
                    Log.e(TAG, "run: cannotclick green" );
                    binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_success));
                } else {
                    if (mGoToPop != null) {
                        mGoToPop.dismiss();
                    }
                    if (mFocuserPop != null){
                        mFocuserPop.dismiss();
                    }
                    Log.e(TAG, "run: cannotclick red" );
                    binding.leftLayout.raDecContainer.setVisibility(View.INVISIBLE);
                    binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_fail));
                }
                manageFocuserStatus();
                manageScopeStatus();
                if (!mGuider.mScopeIsDeviceConnected) {
                    if (mGoToPop != null) {
                        mGoToPop.dismiss();
                    }
                }
                if (!mGuider.mFocuserIsDeviceConnected){
                    if (mFocuserPop != null){
                        mFocuserPop.dismiss();
                    }
                }
            }
        });
    }

    private void ModeSingle2Video() {
        releaseStream();
        resetFrame();
        new Thread() {
            @Override
            public void run() {
                super.run();
                mGuider.stopCapture("main");
                mGuider.setCaptureMode(binding.singleTriggerBtn.getTag().toString());
            }
        }.start();
    }

    private void ModeVideo2Single() {
        resetFrame();
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (mGuider.setCaptureMode(binding.videoModeBtn.getTag().toString())) {
                    workModeChangeExpData();
                    videoModeStartCapture();
                }
            }
        }.start();
    }

    public void connectionLostEvent() {
        // mMenuCurrentMode = MENU_INIT;
        hideDiySoftInput();
        clearPictureDownloading();

        stopFpsTimer();
        checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);

        resetAllDockPanelView();
        hideAllSettingPanel();
        mCheckConnectProcess.showConnectingDialog();
        deviceDisconnectedView();
    }

    public void connectionSuccessEvent() {
        mCheckConnectProcess.dismissConnectedDialog();
        checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);
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
                    // ArrayList<Double> coord = mGuider.getScopeCoord();
                    binding.leftLayout.raDecContainer.setVisibility(View.VISIBLE);
                    if (mGuider.mIsScopeValid) {
                        binding.leftLayout.raDecValue.setText(String.format("%.2f", mGuider.mCoordRa)
                                + "\n" + String.format("%.2f", mGuider.mCoordDec));
                    } else {
                        binding.leftLayout.raDecValue.setText("null"
                                + "\n" + "null");
                    }
                    if (mIsScopeMoving) {
                        if (mGotoBuilder != null) {
                            mGotoBuilder.updateCurrentRa(mGuider.mCoordRa, false);
                            mGotoBuilder.updateCurrentDec(mGuider.mCoordDec, false);
                        }
                    }
                } else {
                    binding.leftLayout.raDecContainer.setVisibility(View.INVISIBLE);
                    binding.leftLayout.raDecValue.setText("");
                }
            }
        });
    }

    private class CoordTimerTask extends TimerTask {
        @Override
        public void run() {
            updateRaDecView();
        }
    }

    private void startCoordTimer() {
        if (mCoordTimer != null) {
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
        if (mCoordTimerTask != null) {
            mCoordTimerTask.cancel();
            mCoordTimerTask = null;
        }
    }

    public void startSlewEvent() {
        mIsScopeMoving = true;
        startCoordTimer();
    }

    public void stopSlewEvent() {
        stopCoordTimer();
        mIsScopeMoving = false;
        if (mGotoBuilder != null) {
            mGotoBuilder.updateCurrentRa(mGuider.mCoordRa, true);
            mGotoBuilder.updateCurrentDec(mGuider.mCoordDec, true);
        }
        new Thread() {
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
                        if (mGotoProcessDialog != null) {
                            mGotoProcessDialog.dismiss();
                        }
                    }
                });
            }
        }.start();
    }

    public void gotoErrorEvent() {
        if (mGuider.mAlertMsg.equals("Failed to slew. ")) {
            if (mGotoProcessDialog != null) {
                mGotoProcessDialog.dismiss();
            }
        }
    }

    public void coolingChangedEvent() {
        CameraSupportCooling();
    }

    public void manageScopeStatus() {
        if (mGuider.mScopeIsDeviceConnected) {
            if (mGuider.mIsGuidingStarted) {
                if (mGoToPop != null) {
                    mGoToPop.dismiss();
                }
                binding.leftLayout.goToBtn.setEnabled(false);
            } else {
                binding.leftLayout.goToBtn.setEnabled(true);
            }
        } else {
            binding.leftLayout.goToBtn.setEnabled(false);
        }
    }

    public void manageFocuserStatus(){
        if (!mGuider.mFocuserIsDeviceConnected){
            if (mFocuserPop != null){
                mFocuserPop.dismiss();
            }
        }
        binding.leftLayout.focuserBtn.setEnabled(mGuider.mFocuserIsDeviceConnected);
    }
    private void loadParameterConstruct() {
        formatCurrentDeviceShowInfo();
        manageScopeStatus();
        manageFocuserStatus();
        if (mGuider.mMainIsDeviceConnected) {
            deviceConnectedView();
            String captureMode = mGuider.mMainCaptureMode;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (captureMode.equals("")) {
                        checkCurrentViewByTag("Single");
                    } else {
                        checkCurrentViewByTag(captureMode);
                    }
                }
            });
            if (captureMode.equals("Video")) {
                binding.rightLayout.photoDownloadProgress.setVisibility(View.INVISIBLE);
                resetHistogramView();
                mIsPictureViewVisibility = false;
                resetPopMenuToInitStatus();
                if (mMenuCurrentMode != MENU_VIDEO_MODE) {
                    enterVideoModeAndLoading();
                    mCircleStatus = CIRCLE_IDLE;
                    mMenuCurrentMode = MENU_VIDEO_MODE;
                    setTakePhotoMode(TakePhotoView.VIDEO_MODE_IDLE);
                }
                chooseVideoModeVisibleView();
                setDockToGuideBtnVisibility();
            } else if (captureMode.equals("Single")) {
                startNowTempTimer();
                enterMainSingleModeView();
            }
        } else {
            deviceDisconnectedView();
        }
    }

    public void parameterLoadSuccess() {
        loadParameterConstruct();
        if (mGuider.mMainIsDeviceConnected) {
            if (!mIsMainDeviceActive) {
                mIsMainDeviceActive = true;
                initMainCameraUI();
            }
        } else {
        }
        checkDeviceAndNetworkConnection(mGuider.mIsNetworkOnLine, mGuider.mMainIsDeviceConnected);
    }

    public void calibrationFailedEvent() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mGuider.stopGuide();
            }
        }.start();
        ToastUtil.showToast(App.INSTANCE.getResources().getString(R.string.calibrating_fail), false);
    }

    public void starLostEvent() {
        ToastUtil.showToast( App.INSTANCE.getResources().getString(R.string.guide_start_lost), false);
    }

    private void gotoProgress(double target_ra, double target_dec) {
        mTargetRA = target_ra;
        mTargetDec = target_dec;
        Log.e(TAG, "gotoProgress: " + mTargetDec);
        mGotoBuilder = new GotoProcessDialog.Builder(this)
                .setTargetRa(mTargetRA)
                .setTargetDec(mTargetDec)
                .setCurrentRa(mGuider.mCoordRa)
                .setCurrentDec(mGuider.mCoordDec)
                .setIsCancelable(true)
                .setIsCancelOutside(false)
                .setOnStopGotoListener(new GotoProcessDialog.OnStopGotoListener() {
                    @Override
                    public void stopGotoLocation() {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.guideCoord(false, 1.1, 1.1);
                                } catch (Exception e) {
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
                    boolean isCoordSuccess = mGuider.guideCoord(true, mTargetRA, mTargetDec);
                    if (!isCoordSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mGotoProcessDialog.dismiss();
                                ToastUtil.showToast( getResources().getString(R.string.can_not_goto_target), false);
                                Log.e(TAG, "run: gotoProgress showToast");
                            }
                        });
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void expSliderRange(long srcMin, long srcMax) {
        mExpTimeMin = srcMin;
        mExpTimeMax = srcMax;
        mVideoModeEXPMin = (int) (pow(1.0 * srcMin * srcMax * srcMax, 1.0 / 3.0) / 1000.0 + 1.0);
        mVideoModeEXPMax = (int) (srcMax / 1000.0);
        mExpSliderMin = (int) (pow(1.0 * srcMin * srcMax * srcMax, 1.0 / 3.0) / 1000.0 + 1.0);
        mExpSliderMax = (int) (srcMax / 1000.0);
    }

    private int real2ExpSlider(long expTime) {
        double d = expTime / (mExpSliderMax * 1000.0);
        d = pow(d, 1.0 / 3.0);
        int r = (int) (mExpSliderMax * d);
        if (r < mExpSliderMin)
            r = mExpSliderMin;
        if (r > mExpSliderMax)
            r = mExpSliderMax;
        return r;
    }

    private long expSlider2Real(int sliderExpTime) {
        if (sliderExpTime == mExpSliderMin)
            return mExpTimeMin;
        if (sliderExpTime == mExpSliderMax)
            return mExpTimeMax;
        double d = sliderExpTime * 1.0 / mExpSliderMax;
        d *= (d * d * mExpSliderMax * 1000.0);
        long r = (long) d;
        if (r < mExpTimeMin)
            r = mExpTimeMin;
        if (r > mExpTimeMax)
            r = mExpTimeMax;
        return r;
    }


    private void initModeMenuEvent() {
        binding.realTimeStackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCurrentModeView(binding.realTimeStackBtn);
                hideModeMenu();
                stopFpsTimer();
            }
        });
        binding.autoFocuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCurrentModeView(binding.autoFocuseBtn);
                hideModeMenu();
                stopFpsTimer();
            }
        });

        binding.singleTriggerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCurrentModeView(binding.singleTriggerBtn);
                hideModeMenu();
                stopFpsTimer();
                if (mLastToggleBtn != binding.singleTriggerBtn) {
                    binding.dockHistorgramPanel.moveView.resetNumber();
                    mLastToggleBtn = binding.singleTriggerBtn;
                    LogUtil.writeLogtoFile("点击切换到单帧模式");
                    ModeSingle2Video();
                    enterMainSingleMode();
                }
            }
        });

        binding.videoModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCurrentModeView(binding.videoModeBtn);
                hideModeMenu();
                if (mLastToggleBtn != binding.videoModeBtn) {
                    binding.dockHistorgramPanel.moveView.resetNumber();
                    mLastToggleBtn = binding.videoModeBtn;
                    ModeVideo2Single();
                    mCurrentRes = "";
                    formatDeviceParameterShowInfo();
                    enterMainVideoMode();
                }
            }
        });

        binding.planPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCurrentModeView(binding.planPhotoBtn);
                hideModeMenu();
                stopFpsTimer();
                if (mLastToggleBtn != binding.planPhotoBtn) {
                    mLastToggleBtn = binding.planPhotoBtn;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            mGuider.setCaptureMode(binding.planPhotoBtn.getTag().toString());
                        }
                    }.start();
                    enterMainPlanMode();
                }
            }
        });

        mMenuViewList.add(binding.planPhotoBtn);
        mMenuViewList.add(binding.videoModeBtn);
        mMenuViewList.add(binding.singleTriggerBtn);
        mMenuViewList.add(binding.realTimeStackBtn);
        mMenuViewList.add(binding.autoFocuseBtn);
    }

    public void checkCurrentViewByTag(String tag) {
        for (ToggleButton it : mMenuViewList) {
            if (it.getTag() != null) {
                if (tag.equals(it.getTag().toString())) {
                    it.setChecked(true);
                } else {
                    it.setChecked(false);
                }
            }
        }
        menuModeCheck();
    }

    private void menuModeCheck() {
        if (binding.realTimeStackBtn.isChecked()) {
            checkCurrentModeView(binding.realTimeStackBtn);
        }

        if (binding.autoFocuseBtn.isChecked()) {
            checkCurrentModeView(binding.autoFocuseBtn);
        }

        if (binding.singleTriggerBtn.isChecked()) {
            checkCurrentModeView(binding.singleTriggerBtn);
            binding.dockHistorgramPanel.moveView.resetNumber();
            mLastToggleBtn = binding.singleTriggerBtn;
            ModeSingle2Video();
            enterMainSingleMode();
        }

        if (binding.videoModeBtn.isChecked()) {
            checkCurrentModeView(binding.videoModeBtn);
            binding.dockHistorgramPanel.moveView.resetNumber();
            mLastToggleBtn = binding.videoModeBtn;
            ModeVideo2Single();
            enterMainVideoMode();
        }

        if (binding.planPhotoBtn.isChecked()) {
            checkCurrentModeView(binding.planPhotoBtn);
            mLastToggleBtn = binding.planPhotoBtn;
            enterMainPlanMode();
        }
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
    private void checkCurrentModeView(ToggleButton toggleButton) {
        toggleButton.setChecked(true);
        for (ToggleButton it : mMenuViewList) {
            if (toggleButton != it) {
                it.setChecked(false);
            }
        }
    }
}
