package com.glide.chujian.util;

import static com.glide.chujian.util.LogUtil.loge;
import static com.glide.chujian.util.LogUtil.logi;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.model.Chart;
import com.glide.chujian.model.GuideDockStatus;
import com.glide.chujian.model.GuiderModel.*;
import com.glide.chujian.model.StarProfileInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Guider {

    private static final String TAG = "Guider";
   // public String host = "192.168.1.96";
    // public String host = "192.168.1.242";
    private int HEARBEATTIMEOUT = 5;

    public String host = "10.0.0.1";

    public static final int MAIN_CACHE_LOAD = 0;
    public static final int GUIDE_CACHE_LOAD = 1;
    public static final int FF_CACHE_LOAD = 2;
    public static final int FOCUSER_CACHE_LOAD = 3;
    public static final int SCOPE_CACHE_LOAD = 4;
    public boolean mIsMainLoaded = false;
    public boolean mIsGuideLoaded = false;
    public boolean mIsFFLoaded = false;
    public boolean mIsFocuserLoaded = false;
    public boolean mIsScopeLoaded = false;
    public GuideDockStatus mGuideDockStatus = new GuideDockStatus();
    public boolean mIsDockStarVisible = false;
    private int DeviceDelayTime = 500;
    public int mSettingCurrentPosition = 0;
    public int mAstroLibraryCurrentFilter = 0;
    public final Object mLoadMainDeviceLock = new Object();
    public final Object mLoadGuideDeviceLock = new Object();
    public final Object mLoadFFDeviceLock = new Object();
    public final Object mLoadFocuserDeviceLock = new Object();
    public final Object mLoadScopeDeviceLock = new Object();
    public boolean mIsNetworkOnLine = false;
    //main device parameter
    public boolean mIsMainCapturing = false;
    public boolean mIsGuideCapturing = false;
    public String mMainCurrentTemp = "";
    public boolean mMainIsDeviceConnected = false;
    public int mMainFocalLength = 0;
    public int mMainGain = 0;
    public int mMainGainMode = 0;
    public boolean mMainLowNoiseMode = false;
    public String mMainCooling = "";
    public boolean mMainFan = false;
    public Double mMainTargetTemp = 0.0;
    public Double mMainMaxTemp = 0.0;
    public Double mMainMinTemp = 0.0;
    public int mMainExpData = 0;
    public int mMainExpNumber = 0;
    public int mMainCurrentResolution = 0;
    public int mMainBinning = 0;
    public String mMainCaptureMode = "";
    public double mCoordRa = 0;
    public double mCoordDec = 0;
    public boolean mIsScopeValid = false;
    public ArrayList<Integer> mMainExpRange = new ArrayList<>();
    public Integer mMainExpMin = 0;
    public Integer mMainExpMax = 0;
    public ArrayList<String> mMainExpDataList = new ArrayList<>();
    public ArrayList<Integer> mMainExpDataValueList = new ArrayList<>();
    public ArrayList<String> mMainTargetTempDataList = new ArrayList<>();
    public ArrayList<String> mMainBinningDataList = new ArrayList<>();
    public ArrayList<String> mMainResDataList = new ArrayList<>();
    public ArrayList<Integer> mMainFullSize = new ArrayList<>();
    public ArrayList<Integer> mGuideFullSize = new ArrayList<>();
    public int mGuidePushCurrentResolution = -1;
    public ArrayList<JSONObject> mPushResolutions = new ArrayList<>();
    public int mMainPushCurrentResolution = -1;

    private Double[] mLockedPosition = new Double[2];

    //guide device parameter

    public boolean mIsGuidingStarted = false;
    public int mGuideStarProfileMode = -1;
    public int mStarGuidingMode = Constant.STAR_IDLE;
    public boolean mGuideIsDeviceConnected = false;
    public int mGuideFocalLength = 0;
    public int mGuideGain = 0;
    public int mGuideExpData = 0;
    public int mGuideExpNumber = 0;
    public int mGuideBinning = 0;
    public double mGuideDitherScale = 0;
    public int mGuideMaxDec = 0;
    public int mGuideMaxRa = 0;
    public int mGuideDitherMode = 0;
    public boolean mGuideRaOnly = false;
    public int mGuideBinMax = 0;
    public int mGuiderExpMin = 0;
    public int mGuiderExpMax = 0;
    public  final Object msgLock = new Object();
    public ArrayList<String> mGuideExpDataList = new ArrayList<>();
    public ArrayList<Integer> mGuideExpDataValueList = new ArrayList<>();

    //filter wheel device parameter
    public boolean mFilterWheelIsDeviceConnected = false;
    public boolean mFilterWheelUnidirectional = false;
    public int mFilterWheelSlotNumber = 0;
    public int mFilterWheelPosition = 0;
    public int mFilterWheelSlotNumberMax = 0;

    //focuser device parameter
    public boolean mFocuserIsDeviceConnected = false;
    public boolean mFocuserIsMoving = false;
    public Integer mFocuserPosition = null;
    public boolean mIsFocuserArriveTargetPosition = true;
    public boolean mIsCurrentPositionUpMaxLevel = false;
    public Integer mFocuserTargetPosition = null;
    public Boolean mFocuserReversed = null;
    public Integer mFocuserPositionMax = null;
    public Boolean mFocuserMode = null;
    public Integer mFocuserCoarseStep = null;
    public Integer mFocuserFineStep = null;
    public Boolean mFocuserBeep = null;
    public Double mFocuserTemperature = null;
    public ArrayList<Integer> mFocuserRangeMaxList = new ArrayList<>();
    public ArrayList<Integer> mFocuserRangePositionList = new ArrayList<>();
    public ArrayList<Integer> mFocuserRangeStep = new ArrayList<>();

    //scope device parameter
    public boolean mScopeIsDeviceConnected = false;
    public boolean mScopePark = false;
    public int mMountSpeedChangedValue = 0;
    public int mScopeBaudrate = 0;
    public boolean mScopeTracking = false;
    public String mScopeTrackingRate = "";
    public ArrayList<String> mScopeGuidingSpeedList = new ArrayList<>();
    public ArrayList<String> mScopeBaudrateList = new ArrayList<>();

    public boolean mDcOneStatus = false;
    public boolean mDcTwoStatus = false;
    public boolean mDcThreeStatus = false;
    public boolean mDcFourStatus = false;

    public boolean mCancelDialog = false;
    private int mId = 0;
    private final Object mCondition = new Object();
    private final Object mLock = new Object();
    public  final Object mClickLock = new Object();
    public  final Object mHeartLock = new Object();
    public  final Object mMsgWriteLock = new Object();
    private LinkedList<JSONObject> mResponseQueue = new LinkedList<>();
    private boolean mCancelWaitFlag = false;
    private SocketIO io = new SocketIO();
    private AtomicBoolean mAccumActive = new AtomicBoolean(false);
    private double mSettlePx = 0.0;
    private Accum mAccumRa = new Accum();
    private Accum mAccumDec = new Accum();
    private Thread mWorker;
    private Thread mWriter;
    private Thread mHeartbeat;
    public final String DEVICE_LABEL = "device_label";/*新的设备加入进来的时候，只刷新对应label的设备*/
    public final String START_DRIVER = "start_driver"; /*用于区分是main还是guide发起的start driver*/
    public final String SELECTED_DEVICE = "connected_device"; /*只更新连接有变化的设备的ui*/

    public String mGainChangedDevice = "";
    public int mGainChangedValue = 0;
    public String mResChangedDevice = "";
    public int mResChangedValue = 0;
    public String mExposureChangedDevice = "";
    public int mExposureChangedValue = 0;
    public String mBinningChangedDevice = "";
    public int mBinningChangedValue = 0;
    public String mTargetTempChangedDevice = "";
    public double mTargetTempChangedValue = 0;
    public int mDcOutputIndex = 0;
    public boolean mDcOutputValue = false;

    public ArrayList<Float> mValuesDX = new ArrayList<Float>();
    public ArrayList<Float> mValuesDY = new ArrayList<Float>();
    public ArrayList<Float> mValuesStarMass = new ArrayList<Float>();
    public ArrayList<Float> mValuesSNR = new ArrayList<Float>();
    public ArrayList<Float> mValuesRADistanceRaw = new ArrayList<Float>();
    public ArrayList<Float> mValuesDecDistanceRaw = new ArrayList<Float>();
    public ArrayList<Float> mValuesDECDuration = new ArrayList<Float>();
    public ArrayList<Float> mValuesRADuration = new ArrayList<Float>();
    private int mIndexChart;
    public float maxisMaximum = 60f;

    private float mScaleD = 1;
    private float mScaleStarMass = 100000;
    private float mScaleSNR = 10;
    private float mScaleDistanceRaw = 1;
    private int mScaleDuration = 20;

    public StarProfileInfo mStarProfileInfo = new StarProfileInfo();
    private ArrayList<AllDevice> mCCDSAllDevices = new ArrayList<AllDevice>();
    public String mCapturedDevice = "";
    public String mCapturingDevice = "";
    public CaptureFrameSaved mCaptureFrameSaved = new CaptureFrameSaved();
    public Calibrating mCalibrating = new Calibrating();
    public StarLost mStarLost = new StarLost();
    public boolean mIsMainRtspSource = false;
    public boolean mIsGuideRtspSource = false;
    public boolean mIsGuideStartStreamDirectly = false;
    public int mRtspResIndex = -1;
    public String mRtspResChangeDevice = "";
    public boolean isRtspReady = false;
    public String mCalibrationStarted = "";
    public String mCalibrationDone = "";
    public String mCalibrationDataFlipped = "";
    public int mCapturingFrame = -1;
    public String mAlertType = "";
    public String mAlertMsg = "";
    public FilterWheelName mFilterWheelName = new FilterWheelName();
    private LinkedList<String> mMessageQueue = new LinkedList<>();
    public CopyOnWriteArrayList<Double> mGuidingDithered = new CopyOnWriteArrayList<Double>();
    private ArrayList<Handler> mHandlerList = new ArrayList<Handler>();
    public CopyOnWriteArrayList<Double> mStarSelected = new CopyOnWriteArrayList<Double>();
    public CopyOnWriteArrayList<String> mCalibrationFailed = new CopyOnWriteArrayList<String>();
    public CopyOnWriteArrayList<AllDevice> mTelescopesAllDevices = new CopyOnWriteArrayList<AllDevice>();
    public CopyOnWriteArrayList<AllDevice> mFilterWheelsAllDevices = new CopyOnWriteArrayList<AllDevice>();
    public CopyOnWriteArrayList<AllDevice> mFocusersAllDevices = new CopyOnWriteArrayList<AllDevice>();
    public ConcurrentHashMap<String, String> mCurrentDevices = new ConcurrentHashMap<String, String>();
    public ConcurrentHashMap<String, String> mSelectedDevices = new ConcurrentHashMap<String, String>();
    public ConcurrentHashMap<String, Boolean> mDevicesConnected = new ConcurrentHashMap<String, Boolean>();
    public String mCurrentConnectedDevice = "";
    public ConcurrentHashMap<String, String> mWorkStates = new ConcurrentHashMap<String, String>();
    public ConcurrentHashMap<String, String> mAppInfo = new ConcurrentHashMap<String, String>();


    public Chart mChartData = new Chart();
    private int mPortOffset = 0;
    public boolean mIsDxDyVisibility = true;
    public boolean mIsRaDecVisibility = true;
    public boolean mIsSnrVisibility = true;
    public boolean mIsMassVisibility = true;

    public boolean mIsMainDxDyVisibility = true;
    public boolean mIsMainRaDecVisibility = true;
    public boolean mIsMainSnrVisibility = true;
    public boolean mIsMainMassVisibility = true;

    Settling mSettling = new Settling();

    private String mGuidingState = "";

    private String mSettleState = "";

    private int mHeartBeatCnt = 0;

    private String mAppState = "";

    public String mCaptureState = "";
    public String mAppInitState = "";
    public int mAvgDist = 0;
    public GuideStats mStats = new GuideStats();

    private SettleDone mSettleDone = new SettleDone();
    private SettleProgress mSettle;

    private Timer mFocuserCurrentPositionChangedTimer;
    private FocuserCurrentPositionChangedTask mFocuserCurrentPositionChangedTask;

    private void resetNetworkStatus(){
        mWorkStates.clear();
        mRtspResChangeDevice = "";
        mGuideDockStatus.clearDockStatus();
        mPushResolutions.clear();
        mCCDSAllDevices.clear();
        mTelescopesAllDevices.clear();
        mFilterWheelsAllDevices.clear();
        mFocusersAllDevices.clear();
        mCurrentDevices.clear();
        mSelectedDevices.clear();
        mDevicesConnected.clear();
        clearDcStatus();
        resetMainDeviceConnectData();
        resetGuideDeviceConnectData();
        resetScopeDeviceConnectData();
        resetFilterWheelDeviceConnectData();
        resetFocuserDeviceConnectData();
        clearChartInfoData();
    }

    public void resetMainDeviceConnectData(){
        mMainCurrentTemp = "";
        mIsMainRtspSource = false;
        mIsMainLoaded = false;
        mMainIsDeviceConnected = false;
        mMainFocalLength = 0;
        mMainGain = 0;
        mMainGainMode = -1;
        mMainLowNoiseMode = false;
        mMainCooling = "";
        mMainFan = false;
        mMainTargetTemp = 0.0;
        mMainExpData = 0;
        mMainExpNumber = 0;
        mMainCurrentResolution = 0;
        mMainBinning = 0;
        mMainCaptureMode = "";
        mMainExpRange.clear();
        mMainExpDataList.clear();
        mMainExpDataValueList.clear();
        mMainTargetTempDataList.clear();
        mMainBinningDataList.clear();
        mMainResDataList.clear();
        mMainFullSize.clear();
    }

    public void resetGuideDeviceConnectData(){
        mStarGuidingMode = Constant.STAR_IDLE;
        mGuidingState = "";
        mIsGuideCapturing = false;
        mIsGuideStartStreamDirectly = false;
        mIsGuideRtspSource = false;
        mIsGuidingStarted = false;
        mGuideStarProfileMode = -1;
        mIsGuideLoaded = false;
        mGuideIsDeviceConnected = false;
        mGuideFocalLength = 0;
        mGuideGain = 0;
        mGuideExpData = 0;
        mGuideExpNumber = 0;
        mGuideBinning = 0;
        mGuideDitherScale = 0;
        mGuideMaxDec = 0;
        mGuideMaxRa = 0;
        mGuideDitherMode = 0;
        mGuideRaOnly = false;
        mGuideBinMax = 0;
        mGuideExpDataList.clear();
        mGuideExpDataValueList.clear();
        mGuideFullSize.clear();
    }

    public void resetScopeDeviceConnectData(){
        mIsScopeLoaded = false;
        mScopeIsDeviceConnected = false;
        mScopePark = false;
        mMountSpeedChangedValue = 0;
        mScopeBaudrate = 0;
        mScopeTracking = false;
        mScopeTrackingRate = "";
        mScopeGuidingSpeedList.clear();
        mScopeBaudrateList.clear();
    }

    public void resetFilterWheelDeviceConnectData(){
        mIsFFLoaded = false;
        mFilterWheelIsDeviceConnected = false;
        mFilterWheelUnidirectional = false;
        mFilterWheelSlotNumber = 0;
        mFilterWheelPosition = 0;
        mFilterWheelSlotNumberMax = 0;
    }

    public void resetFocuserDeviceConnectData(){
        mFocuserRangeMaxList.clear();
        mFocuserRangePositionList.clear();
        mFocuserRangeStep.clear();
        mIsCurrentPositionUpMaxLevel = false;
        mIsFocuserLoaded = false;
        mIsFocuserArriveTargetPosition = true;
        mFocuserIsMoving = false;
        mFocuserIsDeviceConnected = false;
        mFocuserPosition = null;
        mFocuserTargetPosition = null;
        mFocuserReversed = null;
        mFocuserPositionMax = null;
        mFocuserMode = null;
        mFocuserCoarseStep = null;
        mFocuserFineStep = null;
        mFocuserBeep = null;
        mFocuserTemperature = null;
    }

    public void updateDeviceStatus(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                mPushResolutions = getPushResolutions();
            }
        }.start();
        connectMainDevice();
        connectGuideDevice();
        connectScopeDevice();
        connectFilterWheelDevice();
        connectFocuserDevice();
    }

    private void initLockPosition(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                synchronized (msgLock){
                    Double[] lockPosition = getLockPosition();
                    if (lockPosition.length != 0){
                        mLockedPosition[0] = lockPosition[0];
                        mLockedPosition[1] = lockPosition[1];
                        mStarSelected.clear();
                        mStarSelected.add(lockPosition[0]);
                        mStarSelected.add(lockPosition[1]);
                    }
                }
            }
        }.start();
    }

    public Double[] getLockedPosition(){
        synchronized (msgLock){
            return mLockedPosition;
        }
    }

    public ArrayList<Integer> getMainFullSize(){
        synchronized (msgLock){
            return mMainFullSize;
        }
    }
    public ArrayList<Integer> getGuideFullSize(){
        synchronized (msgLock){
            return mGuideFullSize;
        }
    }

    public ArrayList<Integer> getMainExpDataValueList(){
        synchronized (msgLock){
            return mMainExpDataValueList;
        }
    }

    public ArrayList<String> getMainExpDataList(){
        synchronized (msgLock){
            return mMainExpDataList;
        }
    }

    public ArrayList<JSONObject> getMainLocalPushResolutions(){
        synchronized (msgLock){
            return mPushResolutions;
        }
    }

    public ArrayList<JSONObject> getGuideLocalPushResolutions(){
        synchronized (msgLock){
            return mPushResolutions;
        }
    }

    public int getGuideLocalPushCurrentResolution(){
        return mGuidePushCurrentResolution;
    }

    public int getMainLocalPushCurrentResolution(){
        return mMainPushCurrentResolution;
    }

    private void clearMainLocalPushResolutions(){
        mMainPushCurrentResolution = -1;
    }
    private void clearGuideLocalPushResolutions(){
        mGuidePushCurrentResolution = -1;
    }
    public void connectMainDevice(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                synchronized (mLoadMainDeviceLock) {
                    try {
                        mLoadMainDeviceLock.wait();
                        mIsMainLoaded = true;
                        for (Handler handler : mHandlerList) {
                            if (handler != null) {
                                Message msg = handler.obtainMessage();
                                msg.what = TpConst.MSG_PARAMETER_ALL_LOAD;
                                msg.arg1 = MAIN_CACHE_LOAD;

                                handler.sendMessage(msg);
                            }
                        }
                        checkAllDataLoaded();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (getRtspSource()){
                    mIsMainRtspSource = true;
                    mIsGuideRtspSource = false;
                }else {
                    mIsMainRtspSource = false;
                    mIsGuideRtspSource = true;
                }
                connectMainDeviceParameter();
                synchronized (mLoadMainDeviceLock) {
                    mLoadMainDeviceLock.notify();
                }
            }
        }.start();
    }

    public void connectMainDeviceParameter(){
        mMainIsDeviceConnected = getDeviceConnected("main");
        Log.e(TAG, "run: Event"+mMainIsDeviceConnected );
        mMainPushCurrentResolution = getPushResolution("main");
        if (mMainIsDeviceConnected){
            try {
                Thread.sleep(DeviceDelayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mMainCaptureMode = getCaptureMode();
            mMainResDataList = getResolutions("main");
            mMainCurrentResolution = getResolution("main");
            mMainFocalLength = getFocalLength("main");
            int binMax = getBinningMax("main");
            mMainBinningDataList.clear();
            for (int i = 1; i <= binMax;i++) {
                mMainBinningDataList.add(i+"x"+i);
            }
            mMainBinning = getBinning("main");
            mMainGain = getGain("main");
            mMainGainMode = getGainMode("main");
            mMainLowNoiseMode = getLowNoise("main");
            mMainCooling = getCooling("main");
            mMainFan = getFan("main");
            mMainTargetTemp = getTargetTemperature("main");

            ArrayList<Integer> exposures = getExposures("main");
            mMainExpRange = getRangeExposures("main");
            if (mMainExpRange.size() != 0){
                mMainExpMin = mMainExpRange.get(0);
                mMainExpMax = mMainExpRange.get(1);
            }
            mMainExpData = getExposure("main");
            synchronized (msgLock) {
                mMainExpDataList.clear();
                mMainExpDataValueList.clear();
                for (Integer it : exposures) {
                    mMainExpDataValueList.add(it);
                    mMainExpDataList.add(((double) it / 1000) + "s");
                }
                mMainExpNumber = mMainExpDataList.size();
            }

            ArrayList<Double> tempRange = getRangeTemperature("main");
            mMainTargetTempDataList.clear();
            if (tempRange.size() != 0) {
                mMainMinTemp = tempRange.get(0);
                mMainMaxTemp = tempRange.get(1);
                Double itemValue = mMainMinTemp;
                while (itemValue < tempRange.get(1)) {
                    mMainTargetTempDataList.add(itemValue.toString());
                    itemValue += 10f;
                }
                mMainTargetTempDataList.add(tempRange.get(1).toString());
                mMainTargetTempDataList.add(0,"Off");
            }
            Log.e(TAG, "run: loadmaindevice all parameter load" );
        }
    }
    public ArrayList<String> getmGuideExpDataList(){
        synchronized (msgLock){
            return mGuideExpDataList;
        }
    }

    public void connectGuideDevice(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                synchronized (mLoadGuideDeviceLock) {
                    try {
                        mLoadGuideDeviceLock.wait();
                        mIsGuideLoaded = true;
                        for (Handler handler : mHandlerList) {
                            if (handler != null) {
                                Message msg = handler.obtainMessage();
                                msg.what = TpConst.MSG_PARAMETER_ALL_LOAD;
                                msg.arg1 = GUIDE_CACHE_LOAD;
                                handler.sendMessage(msg);
                            }
                        }
                        checkAllDataLoaded();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (getRtspSource()){
                    mIsMainRtspSource = true;
                    mIsGuideRtspSource = false;
                }else {
                    mIsMainRtspSource = false;
                    mIsGuideRtspSource = true;
                }
                connectGuideDeviceParameter();
                synchronized (mLoadGuideDeviceLock) {
                    mLoadGuideDeviceLock.notify();
                }
            }
        }.start();
    }

    public void connectGuideDeviceParameter(){
        mGuideIsDeviceConnected = getDeviceConnected("guide");
        mGuidePushCurrentResolution = getPushResolution("guide");
        if (mGuideIsDeviceConnected) {
            try {
                Thread.sleep(DeviceDelayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            initLockPosition();
            mGuideGain = getGain("guide");
            mGuideDitherScale = getDitherScale();
            mGuideFocalLength = getFocalLength("guide");
            mGuideBinning = getBinning("guide");
            mGuideMaxRa = getScopeRaMax();
            mGuideMaxDec = getScopeDecMax();
            mGuideDitherMode = getDitherMode();
            mGuideRaOnly = getDitherRaOnly();
            mGuideBinMax = getBinningMax("guide");
            mGuideStarProfileMode = getStarProfileMode();
            ArrayList<Integer> exposures = getExposures("guide");
            mGuideExpData = getExposure("guide");
            ArrayList<Integer> expDataList = getRangeExposures("guide");
            if (expDataList.size() != 0){
                mGuiderExpMin = expDataList.get(0);
                mGuiderExpMax = expDataList.get(1);
            }
            synchronized (msgLock) {
                mGuideExpDataList.clear();
                mGuideExpDataValueList.clear();
                Log.e(TAG, "guiderun: " + mGuideExpDataList.size());
                for (Integer it : exposures) {
                    mGuideExpDataValueList.add(it);
                    mGuideExpDataList.add(((double) it / 1000) + "s");
                }
            }
        }
    }
    public void connectFilterWheelDevice(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                synchronized (mLoadFFDeviceLock) {
                    try {
                        mLoadFFDeviceLock.wait();
                        mIsFFLoaded = true;
                        for (Handler handler : mHandlerList) {
                            if (handler != null) {
                                Message msg = handler.obtainMessage();
                                msg.what = TpConst.MSG_PARAMETER_ALL_LOAD;
                                msg.arg1 = FF_CACHE_LOAD;
                                handler.sendMessage(msg);
                            }
                        }

                        checkAllDataLoaded();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                connectFFDeviceParameter();
                synchronized (mLoadFFDeviceLock) {
                    mLoadFFDeviceLock.notify();
                }
            }
        }.start();
    }

    public void connectFFDeviceParameter(){
        mFilterWheelIsDeviceConnected = getDeviceConnected("filterwheel");
        if (mFilterWheelIsDeviceConnected) {
            mFilterWheelSlotNumber = getFwSlotNum();
            mFilterWheelPosition = getFwPosition();
            mFilterWheelUnidirectional = getFwUnidirectional();
            mFilterWheelSlotNumberMax = getFwSlotNumMax();
        }
    }

    public void connectFocuserDevice(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                synchronized (mLoadFocuserDeviceLock) {
                    try {
                        mLoadFocuserDeviceLock.wait();
                        mIsFocuserLoaded = true;
                        for (Handler handler : mHandlerList) {
                            if (handler != null) {
                                Message msg = handler.obtainMessage();
                                msg.what = TpConst.MSG_PARAMETER_ALL_LOAD;
                                msg.arg1 = FOCUSER_CACHE_LOAD;
                                handler.sendMessage(msg);
                            }
                        }
                        checkAllDataLoaded();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                connectFocuserDeviceParameter();
                synchronized (mLoadFocuserDeviceLock) {
                    mLoadFocuserDeviceLock.notify();
                }
            }
        }.start();
    }

    public void connectFocuserDeviceParameter(){
        mFocuserIsDeviceConnected =  getDeviceConnected("focuser");
        if (mFocuserIsDeviceConnected){
            mFocuserPosition = getFocuserPosition();
            mFocuserTargetPosition = mFocuserPosition;
            mFocuserReversed = getFocuserReversed();
            mFocuserPositionMax = getFocuserMax();
            mFocuserMode = getFocuserMode();
            mFocuserCoarseStep = getFocuserStep(true);
            mFocuserFineStep = getFocuserStep(false);
            mFocuserBeep = getFocuserBeep();
            mFocuserTemperature = getFocuserTemperature();

            mFocuserRangeMaxList = rangeFocuserMax();
            mFocuserRangePositionList = rangeFocuserPosition();
            mFocuserRangeStep = rangeFocuserStep();
            Integer currentPosition = mFocuserPosition;
            Integer positionMax = mFocuserPositionMax;
            if (currentPosition != null && positionMax != null ){
                if (positionMax.compareTo(currentPosition) < 0){
                    mIsCurrentPositionUpMaxLevel = true;
                }
            }
        }
    }

    public void updateCoordData(){
        ArrayList<Double> coord = getScopeCoord();
        if (coord.size() != 0){
            mCoordRa = coord.get(0);
            mCoordDec = coord.get(1);
            mIsScopeValid = true;
        }else {
            mIsScopeValid = false;
        }
    }

    public void connectScopeDevice(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                synchronized (mLoadScopeDeviceLock) {
                    try {
                        mLoadScopeDeviceLock.wait();
                        mIsScopeLoaded = true;
                        for (Handler handler : mHandlerList) {
                            if (handler != null) {
                                Message msg = handler.obtainMessage();
                                msg.what = TpConst.MSG_PARAMETER_ALL_LOAD;
                                msg.arg1 = SCOPE_CACHE_LOAD;

                                handler.sendMessage(msg);
                            }
                        }
                        checkAllDataLoaded();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                connectScopeDeviceParameter();
                synchronized (mLoadScopeDeviceLock) {
                    mLoadScopeDeviceLock.notify();
                }
            }
        }.start();
    }

    public void connectScopeDeviceParameter(){
        mScopeIsDeviceConnected = getDeviceConnected("mount");
        if (mScopeIsDeviceConnected){
            updateCoordData();
            mScopePark = getScopePark();
            mScopeBaudrate = getScopeBaudRate();
            mMountSpeedChangedValue = getScopeSpeed();
            mScopeBaudrateList.clear();
            ArrayList<String> baudrateList = getScopeBaudRates();
            mScopeBaudrateList.addAll(baudrateList);
            mScopeTracking = getScopeTracking();
            mScopeTrackingRate = getScopeTrackMode();
            mScopeGuidingSpeedList = getScopeSpeeds();
            Log.e(TAG, "run:get_scope_coord start" );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getDcStatus(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    mDcOneStatus = getDcOutput(1);
                    mDcTwoStatus = getDcOutput(2);
                    mDcThreeStatus = getDcOutput(3);
                    mDcFourStatus = getDcOutput(4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void clearDcStatus(){
        mDcOneStatus = false;
        mDcTwoStatus = false;
        mDcThreeStatus = false;
        mDcFourStatus = false;
    }

    private Guider() {
    }

    private static class GuiderHolder {
        private static Guider instance = new Guider();
    }

    public static Guider getInstance() {
        return GuiderHolder.instance;
    }

    private HashMap<String,String> mAlertMaps = new HashMap<String,String>(){
        {
            put("Failed to start camera push mode. Operation failed ", App.INSTANCE.getString(R.string.can_not_start_camera_push_mode));
            put("Error: Bad temperature value! Range is [-30.0, 40.0] [C]. ", App.INSTANCE.getString(R.string.temperature_value_invalid));
            put("Failed to slew. ", App.INSTANCE.getString(R.string.can_not_goto_target));
            put("Requested exposure value (0) seconds out of bounds [0.000105,1000]. ", App.INSTANCE.getString(R.string.exposure_time_invalid));
        }
    };
    private boolean isTerminate() {
        return io.isTerminate();
    }

    private void setIsTerminate(boolean value) throws IOException {
        io.setTerminate(value);
    }

    private boolean isConnected() {
        return io.isConnected();
    }

    private boolean isGuiding() {
        String value = mAppState;
        return value.equals("Guiding") | value.equals("LostLock");
    }

    public void registerHandlerListener(Handler handler) {
        if (!mHandlerList.contains(handler)) {
            mHandlerList.add(handler);
        }
    }

    public void unregisterHandlerListener(Handler handler) {
        if (mHandlerList.contains(handler)) {
            mHandlerList.remove(handler);
        }
    }

    public String getGuidingState() {
        synchronized (mLock) {
            return mGuidingState;
        }
    }

    public void setGuidingState(String value) {
        synchronized (mLock) {
            mGuidingState = value;
        }
    }

    public String getSettleState() {
        synchronized (mLock) {
            return mSettleState;
        }
    }

    public void setSettleState(String value) {
        synchronized (mLock) {
            mSettleState = value;
        }
    }

    public void setHeartBeatTimeOut(){
        mHeartBeatCnt = HEARBEATTIMEOUT;
    }

    private int getHeartBeatCnt() {
        synchronized (mClickLock) {
            return mHeartBeatCnt;
        }
    }

    private void addHeartBeatCnt() {
        synchronized (mClickLock) {
            mHeartBeatCnt++;
        }
    }
    private void clearHeartBeatCnt() {
        synchronized (mClickLock) {
            mHeartBeatCnt = 0;
        }
    }



    public void setAppState(String value) {
        synchronized (mLock) {
            mAppState = value;
        }
    }

    public GuideStats getGuideStats() {
        checkConnected();
        synchronized (mLock) {
            mStats.hypot();
            return mStats;
        }
    }

    private Runnable _writer = new Runnable() {
        @Override
        public void run() {
            while (!isTerminate()) {
                synchronized (mMsgWriteLock){
                    if (mMessageQueue.size() != 0){
                        try {
                            io.writeLine(mMessageQueue.pop());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private Runnable _worker = new Runnable() {
        @Override
        public void run() {
            while (!isTerminate()) {
                String readLine = io.readLine();
                if ("".equals(readLine) && !isTerminate()) {
                    //server disconnected!
                    //todo
                    Log.e(TAG, "run: 退出消息接收" + isTerminate() + readLine.toString() + " server disconnected!");
                    break;
                }
                try {
                    JSONObject jsonObject = new JSONObject(readLine);
                    LogUtil.writeLogtoFile(jsonObject.toString());
                    Log.i(TAG, ""+jsonObject.toString());
                    if (jsonObject.has("jsonrpc")) {
                        //response
                        synchronized (mCondition) {
                            mResponseQueue.add(jsonObject);
                            mCondition.notifyAll();
                        }
                    } else {
                        //event
                        handleEvent(jsonObject);
                    }

                } catch (Exception e) {

                }
            }

            Log.e(TAG, "run: 退出消息接收" + isTerminate());
        }
    };

    private Runnable _heartbeat = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if (!isTerminate()) {
                        if (getHeartBeatCnt() < HEARBEATTIMEOUT){
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    addHeartBeatCnt();
                                    setKeepAliveMsg();
                                    clearHeartBeatCnt();
                                }
                            }.start();
                        }else {
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    if (mIsNetworkOnLine) {
                                        mIsNetworkOnLine = false;
                                        resetNetworkStatus();
                                        clearGuideLocalPushResolutions();
                                        clearMainLocalPushResolutions();
                                        for (Handler handler : mHandlerList) {
                                            if (handler != null) {
                                                Message msg = handler.obtainMessage();
                                                msg.what = TpConst.MSG_CONNECTION_LOST;
                                                handler.sendMessage(msg);
                                            }
                                        }
                                        LogUtil.writeConnectLostFile("heart beat time out connect lost");
                                    }
                                  //  Log.e(TAG, "run: HeartBeatCnt is Thread.sleep(2003)"+HeartBeatCnt);
                                    reconnectNetwork();
                                }
                            }.start();
                        }
                    }else {
                      //  break;
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable _socketReconnect = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            try {
                connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void reconnectNetwork(){
        try {
            Log.e(TAG, "disconnect network");
            disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopHeartBeat();
        try {
            Log.e(TAG, "reconnect network");
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stopHeartBeat() {
        clearHeartBeatCnt();
    }

    public void startHeartBeat() {
        synchronized (mHeartLock) {
            stopHeartBeat();
            if (mHeartbeat == null) {
                Log.e(TAG, "startHeartBeat: heartbeat == null");
                mHeartbeat = new Thread(_heartbeat);
                mHeartbeat.start();
            }
        }
    }

    private boolean isFailed(JSONObject resp) {
        return resp.has("error");
    }

    public void connect() throws IllegalStateException, IOException {
        if (isConnected()) {
            throw new IllegalStateException("Already connected! $host:${7624 + portOffset}");
        }
        LogUtil.writeLogtoFile("try to connect " + host);
        logi(TAG, "try to connect " + host);
        try {
          //  startHeartBeat();
            io.connect(host, 4400 + mPortOffset);
            setIsTerminate(false);
            mCancelWaitFlag = false;
            mWorker = new Thread(_worker);
            mWorker.start();
            mMessageQueue.clear();
            mWriter = new Thread(_writer);
            mWriter.start();
            loge(TAG, "connect!");
        }catch (Exception e){
            e.printStackTrace();
            if (mIsNetworkOnLine) {
                mIsNetworkOnLine = false;
                clearMainLocalPushResolutions();
                clearGuideLocalPushResolutions();
                for (Handler handler : mHandlerList) {
                    if (handler != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_CONNECTION_LOST;
                        handler.sendMessage(msg);
                    }
                }
                LogUtil.writeConnectLostFile("socket error and connect lost");
                resetNetworkStatus();
            }
            new Thread(_socketReconnect).start();;
        }
    }

    public void disconnect() throws IllegalStateException, InterruptedException, IOException {
        if (!isConnected()) {
            throw new IllegalStateException("Already disconnected!");
        }
/*
        if (worker.isAlive()){
            worker.join();
        }
*/

        try {
            setIsTerminate(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        io.disconnect();
        mId = 0;

        mCancelWaitFlag = true;
        synchronized(mCondition) {
            mCondition.notifyAll();
        }
        mAvgDist = 0;
        loge(TAG, "disconnect");
    }

    private boolean isResponseCorrect(String JsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(JsonString);
        int id = jsonObject.getInt("id");

        if (mCancelWaitFlag) return false;
        if (mResponseQueue.size() <= 0) return true;
        JSONObject resp = mResponseQueue.getFirst();

        try {
            Log.i(TAG,"isResponseCorrect: 当前id是：" + id + "当前方法是：" + jsonObject.getString("method") + "   响应的result 是:"+
                    resp.toString());
            return id != resp.getInt("id");
        }catch (Exception e){
            LogUtil.writeIdNullToFile(resp+"");
            mResponseQueue.pop();
            e.printStackTrace();
            return false;
        }
    }


    private <T> String makeJsonRpc(String method, T params) throws JSONException {
        JSONObject req = new JSONObject();
        req.put("method", method);
        req.put("id", mId++);

        if (params != null) {
            if ((params instanceof JSONObject)|(params instanceof JSONArray)){
                req.put("params", params);
            } else{
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(params);
                req.put("params", jsonArray);
            }
        }
        logi(TAG, req.toString());
        return req.toString();
    }

    /***
     * param带多个格式的时候，以jsonObject带name的形式包装
     * 例如：
     * {"method": "dither", "params": {"amount": 10, "raOnly": false, "settle": {"pixels": 1.5, "time": 8, "timeout": 30}}, "id": 42}
     * 注意：
     *  对于optional形式的参数，如果没有传递数据，建议使用第二种方案
     */
    private <T> JSONObject call(String method, T params) throws JSONException, InterruptedException, GuiderException,IllegalStateException {
        String makeJsonRpc = "";
        synchronized (mMsgWriteLock){
            makeJsonRpc = makeJsonRpc(method, params);
            LogUtil.writeLogtoFile(makeJsonRpc);
            mMessageQueue.add(makeJsonRpc);
        }
        // makeJsonRpc = makeJsonRpc(method, params);
        JSONObject resp = new JSONObject();
        // io.writeLine(makeJsonRpc);
        synchronized(mCondition) {
            while (isResponseCorrect(makeJsonRpc)) {
                mCondition.wait();
            }

            JSONObject jsonObject = new JSONObject(makeJsonRpc);
            int id = jsonObject.getInt("id");
            if (mResponseQueue.size() != 0)
                resp = mResponseQueue.pop();
            Log.d(TAG, "id 是" + id + "响应的信息是" + resp.toString());
        }
        if (isFailed(resp))
            throw new GuiderException(resp.getString("error"));
        if (mCancelWaitFlag) {
            resp = new JSONObject();
        }
        return resp;
    }

    private void handleEvent(JSONObject ev) throws JSONException {
        switch (ev.getString("Event")) {
            case "AppInfo" : {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            setAppTime(DateUtil.getCurrentTime());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                clearHeartBeatCnt();
                updateDeviceStatus();
                getDcStatus();
                mIsNetworkOnLine = true;
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_CONNECTION_SUCCESS;
                        handler.sendMessage(msg);
                    }
                }
                mAppInfo.clear();
                try {
                    mAppInfo.put("AppVersion",ev.getString("AppVersion"));
                    mAppInfo.put("ProtocolVersion",ev.getInt("ProtocolVersion")+"");
                    mAppInfo.put("OverlapSupport",ev.getBoolean("OverlapSupport")+"");
                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_APP_INFO;
                            handler.sendMessage(msg);
                        }
                    }

                }catch (JSONException exp) {
                    exp.printStackTrace();
                }
                break;
            }
            case "AllDevices" : {
                mCCDSAllDevices.clear();
                mTelescopesAllDevices.clear();
                mFilterWheelsAllDevices.clear();
                mFocusersAllDevices.clear();
                try {
                    JSONArray ccdsList = ev.getJSONArray("CCDs");
                    for (int i = 0;i < ccdsList.length();i++){
                        JSONObject p = ccdsList.getJSONObject(i);
                        JSONArray deviceList = p.getJSONArray("Devices");
                        ArrayList<Device> devices = new ArrayList<Device>();
                        for (int j = 0;j < deviceList.length();j++){
                            JSONObject d = deviceList.getJSONObject(j);
                            devices.add(new Device(d.getString("name")));
                        }
                        AllDevice device = new AllDevice(p.getString("Label"),devices,p.getBoolean("Configured"));
                        mCCDSAllDevices.add(device);
                    }

                    JSONArray telescopesList = ev.getJSONArray("Telescopes");
                    for (int i = 0;i < telescopesList.length();i++){
                        JSONObject p = telescopesList.getJSONObject(i);
                        JSONArray deviceList = p.getJSONArray("Devices");
                        ArrayList<Device> devices = new ArrayList<Device>();
                        for (int j = 0;j < deviceList.length();j++){
                            JSONObject d = deviceList.getJSONObject(j);
                            devices.add(new Device(d.getString("name")));
                        }
                        AllDevice device = new AllDevice(p.getString("Label"),devices,p.getBoolean("Configured"));
                        mTelescopesAllDevices.add(device);
                    }

                    JSONArray filterWheelsList = ev.getJSONArray("Filter Wheels");
                    for (int i = 0;i < filterWheelsList.length();i++){
                        JSONObject p = filterWheelsList.getJSONObject(i);
                        JSONArray deviceList = p.getJSONArray("Devices");
                        ArrayList<Device> devices = new ArrayList<Device>();
                        for (int j = 0;j < deviceList.length();j++){
                            JSONObject d = deviceList.getJSONObject(j);
                            devices.add(new Device(d.getString("name")));
                        }
                        AllDevice device = new AllDevice(p.getString("Label"),devices,p.getBoolean("Configured"));
                        mFilterWheelsAllDevices.add(device);
                    }

                    JSONArray focusersList = ev.getJSONArray("Focusers");
                    for (int i = 0;i < focusersList.length();i++){
                        JSONObject p = focusersList.getJSONObject(i);
                        JSONArray deviceList = p.getJSONArray("Devices");
                        ArrayList<Device> devices = new ArrayList<Device>();
                        for (int j = 0;j < deviceList.length();j++){
                            JSONObject d = deviceList.getJSONObject(j);
                            devices.add(new Device(d.getString("name")));
                        }
                        AllDevice device = new AllDevice(p.getString("Label"),devices,p.getBoolean("Configured"));
                        mFocusersAllDevices.add(device);
                    }
                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_ALL_DEVICES;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (JSONException exp) {
                    exp.printStackTrace();
                }
                break;
            }
            case "CurrentDevices" : {
                mCurrentDevices.clear();
                mSelectedDevices.clear();
                JSONObject currentDevices = ev.getJSONObject("Devices");
                try {
                    mCurrentDevices.put("main",currentDevices.getString("main"));
                    mCurrentDevices.put("guide",currentDevices.getString("guide"));
                    mCurrentDevices.put("mount",currentDevices.getString("mount"));
                    mCurrentDevices.put("filterwheel",currentDevices.getString("filterwheel"));
                    mCurrentDevices.put("focuser",currentDevices.getString("focuser"));

                    mSelectedDevices.put(START_DRIVER,"");
                    mSelectedDevices.put(DEVICE_LABEL,"");
                    mSelectedDevices.put(SELECTED_DEVICE,"");
                    mSelectedDevices.put("main",currentDevices.getString("main"));
                    mSelectedDevices.put("guide",currentDevices.getString("guide"));
                    mSelectedDevices.put("mount",currentDevices.getString("mount"));
                    mSelectedDevices.put("filterwheel",currentDevices.getString("filterwheel"));
                    mSelectedDevices.put("focuser",currentDevices.getString("focuser"));

                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_CURRENT_DEVICES;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (JSONException exp) {
                    exp.printStackTrace();
                }
                break;
            }
            case "AppState" : {
                mWorkStates.clear();
                String state = ev.getString("State");
                if (state.equals("Work")){
                    JSONObject workState = ev.getJSONObject("WorkState");
                    try {
                        mWorkStates.put("main",workState.getString("main"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        mWorkStates.put("guide",workState.getString("guide"));
                        if (mWorkStates.get("guide").equals("CaptureLooping")){
                            mIsGuideStartStreamDirectly = true;
                            mIsGuideCapturing = true;
                        }
                        if (mWorkStates.get("guide").equals("Capturing")){
                            mIsGuideCapturing = true;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        mWorkStates.put("mount",workState.getString("mount"));
                        String mountStatus = workState.getString("mount");
                        switch (mountStatus){
                            case "StarSelect":
                            {
                                mStarGuidingMode = Constant.STAR_SELECTED;
                                break;
                            }
                            case "Calibrating":
                            {
                                mStarGuidingMode = Constant.STAR_CALIBRATING;
                                break;
                            }
                            case "Guiding":
                            {
                                mStarGuidingMode = Constant.STAR_GUIDING;
                                break;
                            }
                            case "LostLock":
                            {
                                mStarGuidingMode = Constant.STAR_LOST;
                                break;
                            }
                            default:{
                                break;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        mWorkStates.put("filterwheel",workState.getString("filterwheel"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        mWorkStates.put("focuser",workState.getString("focuser"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        mWorkStates.put("task",workState.getString("task"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                mAppInitState = state;
                mAppState = state;
                if (isGuiding()) {
                    mAvgDist = 0;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_APP_STATE;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "NewDevice" : {
                updateDevices(ev);
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_NEW_DEVICE;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "RemoveDevice" : {
                updateDevices(ev);
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_REMOVE_DEVICE;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "DeviceSelected" : {
                mSelectedDevices.put(ev.getString("device"),ev.getString("name"));
                mSelectedDevices.put(SELECTED_DEVICE,ev.getString("device"));
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_DEVICE_SELECTED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "DeviceConnected" : {
                mDevicesConnected.put(ev.getString("device"),ev.getBoolean("connect"));
                mCurrentConnectedDevice = ev.getString("device");
                if (mCurrentConnectedDevice.equals("main")){
                    mMainCurrentTemp = "";
                    mMainIsDeviceConnected = ev.getBoolean("connect");
                    connectMainDevice();
                    if (!mMainIsDeviceConnected){
                        clearMainLocalPushResolutions();
                        resetMainDeviceConnectData();
                    }
                }else if (mCurrentConnectedDevice.equals("guide")){
                    mGuideIsDeviceConnected = ev.getBoolean("connect");
                    connectGuideDevice();
                    if (!mGuideIsDeviceConnected){
                        clearChartInfoData();
                        clearGuideLocalPushResolutions();
                        resetGuideDeviceConnectData();
                    }
                }else if (mCurrentConnectedDevice.equals("mount")){
                    mScopeIsDeviceConnected = ev.getBoolean("connect");
                    connectScopeDevice();
                    if (!mScopeIsDeviceConnected){
                        clearChartInfoData();
                        resetScopeDeviceConnectData();
                    }
                }else if (mCurrentConnectedDevice.equals("filterwheel")){
                    mFilterWheelIsDeviceConnected = ev.getBoolean("connect");
                    connectFilterWheelDevice();
                    if (!mFilterWheelIsDeviceConnected){
                        resetFilterWheelDeviceConnectData();
                    }
                }else if (mCurrentConnectedDevice.equals("focuser")){
                    mFocuserIsDeviceConnected = ev.getBoolean("connect");
                    connectFocuserDevice();
                    if (!mFocuserIsDeviceConnected){
                        resetFocuserDeviceConnectData();
                    }
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_DEVICE_CONNECTED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "CaptureStarted" : {
                mCaptureState = "Started";
                mCapturedDevice = ev.getString("device");
                if (mCapturedDevice.equals("guide")){
                    mIsGuideCapturing = true;
                }
                break;
            }
            case "CaptureStopped" : {
                mCaptureState = "Stopped";
                mAppState = "Stopped";
                mCapturedDevice = ev.getString("device");
                if (mCapturedDevice.equals("guide")){
                    mIsGuideCapturing = false;
                }
                break;
            }
            case "CaptureLooping" : {
                mCaptureState = "Looping";
                mAppState = "Looping";
                //isRtspReady = true
                mCapturingDevice = ev.getString("device");
                mCapturingFrame = ev.getInt("frame");
                if (mCapturingDevice.equals("guide")){
                    mIsGuideStartStreamDirectly = true;
                    mIsGuideCapturing = true;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_CAPTURE_LOOPING;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "CaptureFrameSaved" : {
                mCaptureFrameSaved.mDevice = ev.getString("device");
                mCaptureFrameSaved.mFileName = ev.getString("filename");
                mCaptureFrameSaved.mSuccess = ev.getBoolean("success");
                mCaptureFrameSaved.mFileSize = ev.getDouble("filesize");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_CAPTURE_FRAME_SAVED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "RtspSourceChanged" : {
                if(ev.getBoolean("source")){
                    mIsMainRtspSource = true;
                    mIsGuideRtspSource = false;
                }else {
                    mIsMainRtspSource = false;
                    mIsGuideRtspSource = true;
                }

                mIsGuideStartStreamDirectly = false;
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_RTSP_SOURCE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "RtspResChanged" : {
                mIsGuideStartStreamDirectly = false;
                mRtspResChangeDevice = ev.getString("device");
                mRtspResIndex = ev.getInt("res");
                if (mRtspResChangeDevice.equals("main")){
                    mMainPushCurrentResolution = mRtspResIndex;
                }else if (mRtspResChangeDevice.equals("guide")){
                    mGuidePushCurrentResolution = mRtspResIndex;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_RTSP_RES_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "RtspReady" : {
                mIsGuideStartStreamDirectly = true;
                isRtspReady = true;
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_RTSP_READY;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "CaptureModeChanged" : {
                mMainCaptureMode = ev.getString("mode");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_CAPTURE_MODE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "CalibrationStart" : {
                mStarGuidingMode = Constant.STAR_CALIBRATION_START;
                mIsGuidingStarted = true;
                mAppState = "Calibrating";
                mCalibrationStarted = ev.getString("Mount");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_CALIBRATION_STARTED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "CalibrationDone" : {
                mCalibrationDone = ev.getString("Mount");
                break;
            }
            case "CalibrationFailed" : {
                mStarGuidingMode = Constant.STAR_CALIBRATION_FAILED;
                mCalibrationFailed.clear();
                mCalibrationFailed.add(ev.getString("Mount"));
                mCalibrationFailed.add(ev.getString("Reason"));

                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_CALIBRATION_FAILED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "CalibrationDataFlipped" : {
                mCalibrationDataFlipped = ev.getString("Mount");
                break;
            }
            case "Calibrating" : {
                mStarGuidingMode = Constant.STAR_CALIBRATING;
                mIsGuidingStarted = true;
                try {
                    mCalibrating.mMount = ev.getString("Mount");
                    mCalibrating.mDir = ev.getString("dir");
                    mCalibrating.mDist = ev.getDouble("dist");
                    mCalibrating.mDx = ev.getDouble("dx");
                    mCalibrating.mDy = ev.getDouble("dy");
                    mCalibrating.mStep = ev.getInt("step");
                    Pos pos = new Pos(ev.getJSONArray("pos").getDouble(0),ev.getJSONArray("pos").getDouble(1));
                    mCalibrating.mPos = pos;
                    mCalibrating.mState = ev.getString("State");

                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_CALIBRATING;
                            handler.sendMessage(msg);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case "StarSelected" : {
                mStarGuidingMode = Constant.STAR_SELECTED;
                try {
                    mStarSelected.clear();
                    mStarSelected.add(ev.getDouble("X"));
                    mStarSelected.add(ev.getDouble("Y"));

                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_STAR_SELECTED;
                            handler.sendMessage(msg);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case "StarLost" : {
                mStarGuidingMode = Constant.STAR_LOST;
                try {
                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_STAR_LOST;
                            handler.sendMessage(msg);
                        }
                    }
                    mStarLost.mFrame = ev.getInt("Frame");
                    mStarLost.mTime = ev.getDouble("Time");
                    mStarLost.mStarMass = ev.getDouble("StarMass");
                    mStarLost.mSNR = ev.getDouble("SNR");
                    mStarLost.mAvgDist = ev.getDouble("AvgDist");
                    mStarLost.mErrorCode = ev.getInt("ErrorCode");
                    mStarLost.mStatus = ev.getString("Status");
                    mAvgDist = ev.getInt("AvgDist");
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case "FilterWheelUnidirectionalChanged" : {
                mFilterWheelUnidirectional = ev.getBoolean("enable");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FILTER_WHEEL_UNIDIRECTIONAL_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FilterWheelSlotNumberChanged" : {
                mFilterWheelSlotNumber = ev.getInt("slot");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FILTER_WHEEL_SLOT_NUMBER_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FilterWheelPositionChanged" : {
                mFilterWheelPosition = ev.getInt("position");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FILTER_WHEEL_POSITION_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FilterWheelNameChanged" : {
                mFilterWheelName.mName = ev.getString("name");
                mFilterWheelName.mIndex = ev.getInt("index");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FILTER_WHEEL_NAME_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "GuidingStarted" : {
                mStarGuidingMode = Constant.STAR_GUIDING_STARTED;
                mIsGuidingStarted = true;
                mAccumActive.set(true);
                mAccumRa.reset();
                mAccumDec.reset();
                mStats = accumGetStats(mAccumRa, mAccumDec);
                mGuidingState = "GuidingStarted";
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_GUIDING_STARTED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "GuidingPaused" : {
                mGuidingState = "GuidingPaused";
                break;
            }
            case "ResolutionChanged" : {
                mResChangedDevice = ev.getString("device");
                mResChangedValue = ev.getInt("value");
                if (mResChangedDevice.equals("main")){
                    mMainCurrentResolution = mResChangedValue;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.RESOLUTION_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "GuidingStopped" : {
                mStarGuidingMode = Constant.STAR_GUIDING_STOPPED;
                mIsGuidingStarted = false;
                mGuidingState = "GuidingStopped";
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_GUIDING_STOPPED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "GuidingResumed" : {
                mGuidingState = "GuidingResumed";
                break;
            }
            case "SettleStart" : {
                mSettleState = "SettleStart";
                mAccumActive.set(false) ; // exclude GuideStep messages from stats while settling

                break;
            }
            case "SettleDone" : {
                mSettleState = "SettleDone";
                mSettleDone.mDroppedFrames = ev.getInt("DroppedFrames");
                mSettleDone.mStatus = ev.getInt("Status");
                mSettleDone.mTotalFrames = ev.getInt("TotalFrames");
                mSettleDone.mError = ev.getString("Error");
                mAccumActive.set(true);
                mAccumRa.reset();
                mAccumDec.reset();
                GuideStats stats = accumGetStats(mAccumRa, mAccumDec);
                SettleProgress s = new SettleProgress();
                s.mDone = true;
                s.mStatus = ev.getInt("Status");
                s.mError = ev.getString("Error");

                mSettle = s;
                mStats = stats;
                break;
            }
            case "Settling" : {
                mSettleState = "Settling";
                SettleProgress s = new SettleProgress();
                s.mDistance = ev.getDouble("Distance");
                s.mSettlePx = mSettlePx;
                s.mTime = ev.getDouble("Time");
                s.mSettleTime = ev.getDouble("SettleTime");
                mSettle = s;

                mSettling.mSettleTime = ev.getDouble("SettleTime");
                mSettling.mDistance = ev.getDouble("Distance");
                mSettling.mTime = ev.getDouble("Time");
                mSettling.mStarLocked = ev.getBoolean("StarLocked");

                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_SETTLING;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "Alert" : {
                mAlertMsg = ev.getString("Msg");
                mAlertType = ev.getString("Type");
                if ("error".equals(mAlertType))
                {
                    Iterator<Map.Entry<String, String>> iterator = mAlertMaps.entrySet().iterator();
                    String showValue = "";
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        if (entry.getKey().contains(mAlertMsg)){
                            int index = mAlertMsg.indexOf("[");
                            showValue = entry.getValue();
                            if (index != -1){
                                showValue += mAlertMsg.substring(index,mAlertMsg.length());
                            }
                            String finalValue = showValue;
                            if (ActivityManager.getINSTANCE().getCurrentActivity() != null) {
                                ActivityManager.getINSTANCE().getCurrentActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(finalValue,true);
                                    }
                                });
                            }
                            break;
                        }
                    }
                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_ALERT;
                            handler.sendMessage(msg);
                        }
                    }
                }
                break;
            }
            case "GainChanged" : {
                mGainChangedDevice = ev.getString("device");
                mGainChangedValue = ev.getInt("value");
                if (mGainChangedDevice.equals("main")){
                    mMainGain = mGainChangedValue;
                }else if (mGainChangedDevice.equals("guide")){
                    mGuideGain = mGainChangedValue;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.GAIN_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "ExposureChanged" : {
                mExposureChangedDevice = ev.getString("device");
                mExposureChangedValue = ev.getInt("value");
                if (mExposureChangedDevice.equals("main")){
                    mMainExpData = mExposureChangedValue;
                   // updateMainExposureData();
                }else if (mExposureChangedDevice.equals("guide")){
                    mGuideExpData =  mExposureChangedValue;
                  //  updateGuideExposureData();
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.EXPOSURE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "BinningChanged" : {
                mBinningChangedDevice = ev.getString("device");
                mBinningChangedValue = ev.getInt("value");
                if (mBinningChangedDevice.equals("main")){
                    mMainBinning = mBinningChangedValue;
                }else if (mBinningChangedDevice.equals("guide")){
                    mGuideBinning = mBinningChangedValue;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.BINNING_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "TargetTemperatureChanged" : {
                mTargetTempChangedDevice = ev.getString("device");
                mTargetTempChangedValue = ev.getDouble("value");
                if (mTargetTempChangedDevice.equals("main")){
                    mMainTargetTemp = mTargetTempChangedValue;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.TARGET_TEMPERATURE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "MountSpeedChanged" : {
                mMountSpeedChangedValue = ev.getInt("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MOUNT_SPEED_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "GuidingDithered" : {
                mGuidingDithered.clear();
                mGuidingDithered.add(ev.getDouble("dx"));
                mGuidingDithered.add(ev.getDouble("dy"));
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.GUIDING_DITHERED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "GuideParamChanged" : {
                switch (ev.getString("Name")){
                    case "Frame" : {

                    }
                    break;
                    case "Time" : {

                    }
                    break;
                    case "Mount" : {

                    }
                    break;
                    case "dx" : {
                        mChartData.mDx = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "dy"  : {
                        mChartData.mDy = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "RADistanceRaw" : {
                        mChartData.mRADistanceRaw = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "DecDistanceRaw" : {
                        mChartData.mDEDDistanceRaw = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "RADistanceGuide" : {
                        mChartData.mRADistanceGuide = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "DecDistanceGuide" : {
                        mChartData.mDECDistanceGuide = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "RADuration" : {
                        mChartData.mRADuration = ev.getInt("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "RADirection" : {
                        mChartData.mRADirection = ev.getString("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "DECDuration" : {
                        mChartData.mDECDuration = ev.getInt("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "DECDirection" : {
                        mChartData.mDECDirection = ev.getString("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "StarMass" : {
                        mChartData.mStarMass = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "SNR" : {
                        mChartData.mSNR = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "HFD" : {
                        mChartData.mHFD = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "AvgDist" : {
                        mChartData.mAvgDist = ev.getDouble("Value");
                        guideParamChangedNotify();
                    }
                    break;
                    case "RALimited" : {

                    }
                    break;
                    case "DecLimited" : {

                    }
                    break;
                    case "ErrorCode" : {

                    }
                    break;
                }
                break;
            }
            case "Guiding" : {
                mStarGuidingMode = Constant.STAR_GUIDING;
                mIsGuidingStarted = true;
                if (mAccumActive.get()) {
                    mAccumRa.add(ev.getDouble("RADistanceRaw"));
                    mAccumDec.add(ev.getDouble("DECDistanceRaw"));
                    mStats = accumGetStats(mAccumRa, mAccumDec);
                }
                mAppState = "Guiding";
                mAvgDist = ev.getInt("AvgDist");
                handleChartData(ev);
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_GUIDING;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "LockPositionSet" : {
                initLockPosition();
                break;
            }
            case "SlewStarted" : {
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_SLEW_STARTED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "SlewStopped" : {
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_SLEW_STOPPED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "DCOutputChanged" : {
                mDcOutputIndex = ev.getInt("index");
                mDcOutputValue = ev.getBoolean("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_DC_OUTPUT_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocalLengthChanged" : {
                String device = ev.getString("device");
                if (device.equals("main")){
                    mMainFocalLength = ev.getInt("value");
                }else if (device.equals("guide")){
                    mGuideFocalLength = ev.getInt("value");
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCAL_LENGTH_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "CoolingChanged" : {
                String device = ev.getString("device");
                if (device.equals("main")){
                    if (ev.getBoolean("value")) {
                        mMainCooling = "open";
                    }else {
                        mMainCooling = "close";
                    }
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_COOLING_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FanChanged" : {
                String device = ev.getString("device");
                if (device.equals("main")){
                    mMainFan = ev.getBoolean("value");
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FAN_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "GainModeChanged" : {
                String device = ev.getString("device");
                if (device.equals("main")){
                    mMainGainMode = ev.getInt("value");
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_GAIN_MODE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "LowNoiseChanged" : {
                String device = ev.getString("device");
                if (device.equals("main")){
                    mMainLowNoiseMode = ev.getBoolean("value");
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_LOW_NOISE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "MountBaudrateChanged" : {
                mScopeBaudrate = ev.getInt("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_MOUNT_BAUD_RATE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "MountTrackingChanged" : {
                mScopeTracking = ev.getBoolean("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_MOUNT_TRACKING_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "MountTrackingModeChanged" : {
                mScopeTrackingRate = ev.getString("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_MOUNT_TRACKING_MODE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "MountRAMaxChanged" : {
                mGuideMaxRa = ev.getInt("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_MOUNT_RA_MAX_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "MountDECMaxChanged" : {
                mGuideMaxDec = ev.getInt("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_MOUNT_DEC_MAX_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "DitherScaleChanged" : {
                mGuideDitherScale = ev.getDouble("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_DITHER_SCALE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "DitherModeChanged" : {
                mGuideDitherMode = ev.getInt("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_DITHER_MODE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "DitherRAOnlyChanged" : {
                mGuideRaOnly = ev.getBoolean("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_DITHER_RA_ONLY_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "MountParkChanged" : {
                mScopePark = ev.getBoolean("value");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_MOUNT_PARK_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "StarProfileUpdate" : {
                mStarProfileInfo.mMode = ev.getString("Mode");
                mStarProfileInfo.mFWHM = ev.getDouble("FWHM");
                if (mStarProfileInfo.mFWHM != 0) {
                    mStarProfileInfo.mProfile = ev.getJSONArray("Profile");
                }

                if (mStarProfileInfo.mMode.equals("Mid row")){
                    mGuideStarProfileMode = 0;
                }else if (mStarProfileInfo.mMode.equals("Avg row")){
                    mGuideStarProfileMode = 1;
                }else if (mStarProfileInfo.mMode.equals("Avg col")){
                    mGuideStarProfileMode = 2;
                }
                mStarProfileInfo.mPeak = ev.getInt("Peak");
                mStarProfileInfo.mHFD = ev.getDouble("HFD");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_STAR_PROFILE_UPDATE;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocuserReversedChanged" : {
                mFocuserReversed = ev.getBoolean("reversed");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCUSER_REVERSED_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocuserMaxChanged" : {
                mFocuserPositionMax = ev.getInt("position");
                Log.e(TAG, "handleEvent: FocuserMaxChanged"+mFocuserTargetPosition );
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mFocuserRangePositionList = rangeFocuserPosition();
                    }
                }.start();
                Integer currentPosition = mFocuserPosition;
                Integer positionMax = mFocuserPositionMax;
                if (currentPosition != null && positionMax != null ){
                    if (positionMax.compareTo(currentPosition) < 0){
                        mIsCurrentPositionUpMaxLevel = true;
                    }else {
                        mIsCurrentPositionUpMaxLevel = false;
                    }
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCUSER_MAX_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocuserTargetPositionChanged" : {
                mFocuserTargetPosition = ev.getInt("position");
                mIsFocuserArriveTargetPosition = false;
                mFocuserIsMoving = true;
                Log.e(TAG, "handleEvent: FocuserTargetPositionChanged"+mFocuserTargetPosition );
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCUSER_TARGET_POSITION_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocuserCurrentPositionChanged" : {
                int currentPosition = ev.getInt("position");
                mFocuserPosition = currentPosition;

                Integer positionMax = mFocuserPositionMax;
                if (positionMax != null ){
                    if (positionMax.intValue() == currentPosition){
                        if (mIsCurrentPositionUpMaxLevel){
                            mIsCurrentPositionUpMaxLevel = false;
                            mIsFocuserArriveTargetPosition = true;
                            mFocuserIsMoving = false;
                        }
                    }else {
                    }
                }
                Integer targetPosition = mFocuserTargetPosition;
                if (targetPosition != null){
                    if (targetPosition == currentPosition){
                        mIsFocuserArriveTargetPosition = true;
                        mFocuserIsMoving = false;
                    }

                    if (Math.abs(currentPosition - targetPosition) == 0){
                        stopFocuserCurrentPositionChangedTask();
                    }else if (Math.abs(currentPosition - targetPosition) < 5){
                        startFocuserCurrentPositionChangedTask();
                    }
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCUSER_CURRENT_POSITION_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocuserModeChanged" : {
                String mode = ev.getString("mode");
                if (mode.equals("coarse")){
                    mFocuserMode = true;
                }else if (mode.equals("fine")){
                    mFocuserMode = false;
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCUSER_MODE_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocuserStepChanged" : {
                String mode = ev.getString("mode");
                if (mode.equals("coarse")){
                    mFocuserCoarseStep = ev.getInt("step");
                }else if (mode.equals("fine")){
                    mFocuserFineStep = ev.getInt("step");
                }
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCUSER_STEP_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
            case "FocuserBeepChanged" : {
                mFocuserBeep = ev.getBoolean("beep");
                for (Handler handler:mHandlerList){
                    if (handler != null){
                        Message msg = handler.obtainMessage();
                        msg.what = TpConst.MSG_FOCUSER_BEEP_CHANGED;
                        handler.sendMessage(msg);
                    }
                }
                break;
            }
        }
    }


    private void updateDevices(JSONObject ev) throws JSONException {
        JSONObject device = ev.getJSONObject("Device");
        String label = device.getString("Label");
        JSONArray deviceList = device.getJSONArray("Devices");
        ArrayList<Device> devices = new ArrayList<Device>();
        for (int i = 0; i < deviceList.length(); i++) {
            JSONObject d = deviceList.getJSONObject(i);
            devices.add(new Device(d.getString(("name"))));
        }
        int length = mCCDSAllDevices.size();
        for (int i = 0; i < length; i++) {
            if (mCCDSAllDevices.get(i).mLabel.equals(label)) {
                mCCDSAllDevices.get(i).mDevices.clear();
                mCCDSAllDevices.get(i).mDevices.addAll(devices);
                mCCDSAllDevices.get(i).mConfigured = device.getBoolean("Configured");
                String value = "";
                if (devices.size() != 0) {
                    value = devices.get(0).mName;
                    String selectDevice = value;
                    if (mSelectedDevices.get(START_DRIVER).equals("main")) {
                        mSelectedDevices.put(DEVICE_LABEL, "main");
                        mSelectedDevices.put("main", value);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    selectDevice("main", selectDevice);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else if (Objects.equals(mSelectedDevices.get(START_DRIVER), "guide")) {
                        mSelectedDevices.put(DEVICE_LABEL, "guide");
                        mSelectedDevices.put("guide", value);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    selectDevice("guide", selectDevice);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {

                    }
                } else {
                }
            }
        }
        length = mTelescopesAllDevices.size();
        for (int i = 0; i < length; i++) {
            if (mTelescopesAllDevices.get(i).mLabel.equals(label)) {
                mSelectedDevices.put(DEVICE_LABEL, "mount");
                mTelescopesAllDevices.get(i).mDevices.clear();
                mTelescopesAllDevices.get(i).mDevices.addAll(devices);
                mTelescopesAllDevices.get(i).mConfigured = device.getBoolean("Configured");
                if (devices.size() != 0) {
                    mSelectedDevices.put("mount", devices.get(0).mName);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                selectDevice("mount", devices.get(0).mName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    // mSelectedDevices.put("mount", label);
                }
            }
        }
        length = mFilterWheelsAllDevices.size();
        for (int i = 0; i < length; i++) {
            if (mFilterWheelsAllDevices.get(i).mLabel.equals(label)) {
                mSelectedDevices.put(DEVICE_LABEL, "filterwheel");
                mFilterWheelsAllDevices.get(i).mDevices.clear();
                mFilterWheelsAllDevices.get(i).mDevices.addAll(devices);

                mFilterWheelsAllDevices.get(i).mConfigured = device.getBoolean("Configured");
                if (devices.size() != 0) {
                    mSelectedDevices.put("filterwheel", devices.get(0).mName);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                selectDevice("filterwheel", devices.get(0).mName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    // mSelectedDevices.put("filterwheel", label);
                }
            }
        }
        length = mFocusersAllDevices.size();
        for (int i = 0; i < length; i++) {
            if (mFocusersAllDevices.get(i).mLabel.equals(label)) {
                mSelectedDevices.put(DEVICE_LABEL, "focuser");
                mFocusersAllDevices.get(i).mDevices.clear();
                mFocusersAllDevices.get(i).mDevices.addAll(devices);

                mFocusersAllDevices.get(i).mConfigured = device.getBoolean("Configured");
                if (devices.size() != 0) {
                    mSelectedDevices.put("focuser", devices.get(0).mName);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                selectDevice("focuser", devices.get(0).mName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    // mSelectedDevices.put("focuser", label);
                }
            }
        }
    }

    private GuideStats accumGetStats(Accum ra,Accum dec) {
        GuideStats stats =new GuideStats();
        stats.mRmsRa = ra.stdev();
        stats.mRmsDec = dec.stdev();
        stats.mPeakRa = ra.peek;
        stats.mPeakDec = dec.peek;
        return stats;
    }

    private void handleChartData(JSONObject ev) {
        try {
            mChartData.mDx = null;
            mChartData.mDx = ev.getDouble("dx");
        } catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mDy = null;
            mChartData.mDy = ev.getDouble("dy");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mRADistanceRaw = null;
            mChartData.mRADistanceRaw = ev.getDouble("RADistanceRaw");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mDEDDistanceRaw = null;
            mChartData.mDEDDistanceRaw = ev.getDouble("DECDistanceRaw");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mRADistanceGuide = null;
            mChartData.mRADistanceGuide = ev.getDouble("RADistanceGuide");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mDECDistanceGuide = null;
            mChartData.mDECDistanceGuide = ev.getDouble("DECDistanceGuide");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mRADuration = null;
            mChartData.mRADuration = ev.getInt("RADuration");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mRADirection = null;
            mChartData.mRADirection = ev.getString("RADirection");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mDECDuration = null;
            mChartData.mDECDuration = ev.getInt("DECDuration");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mDECDirection = null;
            mChartData.mDECDirection = ev.getString("DECDirection");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mStarMass = null;
            mChartData.mStarMass = ev.getDouble("StarMass");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }
        try {
            mChartData.mSNR = null;
            mChartData.mSNR = ev.getDouble("SNR");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mHFD = null;
            mChartData.mHFD = ev.getDouble("HFD");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }

        try {
            mChartData.mAvgDist = null;
            mChartData.mAvgDist = ev.getDouble("AvgDist");
        }  catch (JSONException exp) {
            exp.printStackTrace();
        }
        updateChartInfo();
    }

    private void guideParamChangedNotify(){
        updateChartInfo();
        for (Handler handler:mHandlerList){
            if (handler != null){
                Message msg = handler.obtainMessage();
                msg.what = TpConst.MSG_GUIDING;
                handler.sendMessage(msg);
            }
        }
    }

    private void checkConnected() throws IllegalStateException{
        if (!isConnected())
            throw new  IllegalStateException("Server disconnected!");
    }


    public void dither(
            int amout,
            Boolean raOnly,
            JSONObject settle
    ) throws JSONException, GuiderException {
        checkConnected();
        SettleProgress s =new SettleProgress();
/*        s.Distance = ditherPixels
        s.SettlePx = settlePixels
        s.SettleTime = settleTime*/
        s.mDistance = settle.getDouble("pixels");
        s.mSettlePx = settle.getDouble("pixels");
        s.mSettleTime = settle.getDouble("time");
        synchronized(mLock) {
            if (mSettle != null) {
                if (!mSettle.mDone)
                    throw new GuiderException("cannot guide while settling");
            }
            mSettle = s;
        }

        try {
            JSONArray params =new JSONArray();

            params.put(amout);
            params.put(raOnly);
            params.put(settle);
            call("dither", params);
        } catch (Exception e) {
            mSettle = null;
            e.printStackTrace();
        }
    }

    public Double[] autoFindStar(int[] roi) throws JSONException, GuiderException, InterruptedException {
        JSONObject params = new JSONObject();
        params.put("roi",roi);
        JSONObject resp = call("auto_find_star", params);
        Double[] doubleArray = new Double[2];
        try {
            JSONArray lockPosition = resp.getJSONArray("result");//TODO maybe crash
            doubleArray[0] = lockPosition.getDouble(0);
            doubleArray[1] = lockPosition.getDouble(1);
        } catch (JSONException exp) {
            Log.i(TAG, "getJSONArray failed!");
            if (resp.has("code")) {
                int code = resp.getInt("code");
                String message = resp.getString("message");
                String strerror = resp.getString("error");
                Log.e(
                        TAG,
                        "findStar: code = " + code + ", message = " + message + ", error = " + strerror
                );
            }
        }
        return doubleArray;
    }


    public Double[] findStarByPos(PointF pt ) throws JSONException, GuiderException, InterruptedException {
        JSONArray ptArry = new JSONArray();
        ArrayList<Double> targetPos = coordConvert(pt.x, pt.y);
        ptArry.put(targetPos.get(0));
        ptArry.put(targetPos.get(1));
        JSONObject param =new JSONObject();
        param.put("pos", ptArry);
        JSONObject resp = call("auto_find_star_by_pos", param);
        Double[] doubleArray = new Double[2];
        try {
            JSONArray lockPosition = resp.getJSONArray("result"); // TODO maybe crash
            doubleArray[0] = lockPosition.getDouble(0);
            doubleArray[1] = lockPosition.getDouble(1);
            Log.i(TAG, "starSelected: findStarByPos: x = " + doubleArray[0] + ", y = " + doubleArray[1]);
        } catch (JSONException exp) {
            exp.printStackTrace();
        }
        return doubleArray;
    }


    public Boolean setExposure(String device ,int newExp) throws JSONException, GuiderException, InterruptedException {
        if (!isConnected())
            return false;

        Log.d(TAG, "run: exposure setExposure");
        JSONObject param = new JSONObject();
        param.put("device", device);
        param.put("value", newExp);
        JSONObject resp = call("set_exposure", param);
        boolean retcode = false;
        try {
            retcode = resp.getBoolean("result");
            if (retcode)
                logi(TAG, "setExposure success!");
        } catch (JSONException exp) {
            Log.i(TAG, "getInt failed!");
            if (resp.has("code")) {
                int code = resp.getInt("code");
                String message = resp.getString("message");
                String strerror = resp.getString("error");
                Log.e(
                        TAG,
                        "setExposure: code = " + code + ", message = " + message + ", error = " + strerror
                );
            }
        }
        return retcode;
    }


    public int[] getCameraFrameSize() throws GuiderException, JSONException, InterruptedException {
        JSONObject resp = call("get_camera_frame_size", null);
        JSONArray jsonArray = resp.getJSONArray("result");
        return new int[]{
                jsonArray.getInt(0),
                jsonArray.getInt(1)
        };
    }

    public void guide(Double settlePixels,Double settleTime,Double settleTimeout) throws GuiderException, JSONException {
        checkConnected();
        SettleProgress s = new SettleProgress();
        s.mSettlePx = settlePixels;
        s.mSettleTime = settleTime;

        synchronized(mLock) {
            if (mSettle != null){
                if (!mSettle.mDone)
                    throw new GuiderException("cannot guide while settling");
            }

            mSettle = s;
        }

        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonSettle = new JSONObject();
            jsonSettle.put("pixels", settlePixels);
            jsonSettle.put("time", settleTime);
            jsonSettle.put("timeout", settleTimeout);

            jsonArray.put(jsonSettle);
            jsonArray.put(false);
            call("guide", jsonArray);

            mSettlePx = settlePixels;
        } catch (Exception e) {
            mSettle = null;
            e.printStackTrace();
        }
    }

    public Boolean getSettling() throws GuiderException, JSONException, InterruptedException {
        checkConnected();
        synchronized(mLock) {
            if (mSettle != null)
                return true;
        }
        JSONObject res = call("get_settling", null);
        boolean ret = res.getBoolean("result");
        if (ret) {
            SettleProgress s = new SettleProgress();
            s.mDistance = -1.0;
            mSettle = s;
        }
        return ret;
    }
    public SettleProgress checkSetting() throws GuiderException {
        checkConnected();
        synchronized(mLock) {
            if (mSettle != null){
                SettleProgress s = mSettle;
                if (mSettle.mDone) {
                    mSettle = null;
                }
                return s;
            } else {
                throw new GuiderException("not settling");
            }
        }
    }

    //

    /**
     * stop looping and guiding
     */
    public void stopCapture(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            call("stop_capture", param);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * start looping exposures
     */
    public void loop(int timeoutSeconds) throws JSONException, GuiderException, InterruptedException {
        checkConnected();
        if (mCaptureState.equals("Looping")) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("device", "guide");
        int exp = getExposure("device");
        call("start_capture", param);
        Thread.sleep(exp * 1000);
        for (int i = 0;i < timeoutSeconds;i++){
            if (mCaptureState.equals("Looping"))
                return;
            Thread.sleep((1 * 1000));
            checkConnected();
        }
        throw new GuiderException("timed-out waiting for guiding to start looping");
    }

    public double getPixelScale(String device ) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject res = call("get_pixel_scale", param);
            return res.getDouble("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1f;
    }

    public Boolean startDriver(String strDriver) throws JSONException, GuiderException, InterruptedException {
        JSONObject param =new JSONObject();
        param.put("driver", strDriver);
        JSONObject resp = call("start_single_driver", param);
        boolean retcode = false;
        try {
            retcode = resp.getBoolean("result");
            if (retcode)
                logi(TAG, "start_single_driver success!");
        } catch (JSONException exp) {
            Log.i(TAG, "start_single_driver failed!");
            if (resp.has("code")) {
                int code = resp.getInt("code");
                String message = resp.getString("message");
                String strerror = resp.getString("error");
                Log.e(
                        TAG,
                        "code = " + code + ", message = " + message + ", error = " + strerror
                );
            }
        }
        return retcode;
    }

    public Boolean stopDriver(String strDriver) throws JSONException, GuiderException, InterruptedException {
        JSONObject param = new JSONObject();
        param.put("driver", strDriver);
        JSONObject resp = call("stop_single_driver", param);
        Boolean retcode = false;
        try {
            retcode = resp.getBoolean("result");
            if (retcode)
                logi(TAG, "stop_single_driver success!");
        } catch (JSONException exp) {
            Log.i(TAG, "stop_single_driver failed!");
            if (resp.has("code")) {
                int code = resp.getInt("code");
                String message = resp.getString("message");
                String strerror = resp.getString("error");
                Log.e(
                        TAG,
                        "code = " + code + ", message = " + message + ", error = " + strerror
                );
            }
        }
        return retcode;
    }

    public Boolean shutdown() throws GuiderException, JSONException, InterruptedException {
        JSONObject resp = call("shutdown", null);
        Boolean retcode = false;
        try {
            retcode = resp.getBoolean("result");
            if (retcode)
                logi(TAG, "shutdown success!");
        } catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "shutdown failed!");
        }
        return retcode;
    }


    public void setLockPosition(Float x,Float y,Boolean exact) throws GuiderException, JSONException, InterruptedException {
        JSONArray params =new JSONArray();
        ArrayList targetPos = coordConvert(x, y);
        Log.e(TAG, "setLockPosition: " + targetPos.get(0) + " y is " + targetPos.get(1));
        params.put(targetPos.get(0));
        params.put(targetPos.get(1));
        params.put(exact);
        call("set_lock_position", params);
    }


    public ArrayList<Double> coordConvert(Float x,Float y) throws JSONException {
        boolean mode = getRtspSource();
        ArrayList<Double> targetPos = new ArrayList<Double>();
        int index = getGuideLocalPushCurrentResolution();
        ArrayList<Integer> fullSize =new ArrayList<Integer>();
        if (mode) {
            if (mMainFullSize.size() == 0) {
                mMainFullSize = getFullSize("main");
            }
            fullSize = mMainFullSize;
        } else {
            if (mGuideFullSize.size() == 0){
                mGuideFullSize = getFullSize("guide");
            }
            fullSize = mGuideFullSize;
        }
        JSONObject coordRes = mPushResolutions.get(index);
        Double resX = coordRes.getDouble("width");
        Double resY = coordRes.getDouble("height");
        Double reverY = y.doubleValue();
        Double scaleX = fullSize.get(0) / resX;
        Double scaleY = fullSize.get(1) / resY;
        if (scaleX <= scaleY) {
            Double newY = resY * scaleX;
            Double deltaY = (fullSize.get(1) - newY) / 2;
            Double targetX = (x / resX) * fullSize.get(0);
            Double targetY = reverY * scaleX + deltaY;
            targetPos.add(targetX);
            targetPos.add(targetY);
        } else {
            Double newX = resX * scaleY;
            Double deltaX = (fullSize.get(0) - newX) / 2;
            Double targetX = x * scaleY + deltaX;
            Double targetY = (reverY / resY) * fullSize.get(1);
            targetPos.add(targetX);
            targetPos.add(targetY);
        }
        return targetPos;
    }

    public void getAppState() throws GuiderException, JSONException, InterruptedException {
        JSONObject res = call("get_app_state", null);
        String st = res.getString("result");
        mAppState = st;
    }

    public int getPushResolution(String device) {
        int index = -1;
        JSONObject param = new JSONObject();
        try {
            param.put("device", device);
            JSONObject resp = call("get_push_resolution", param);
            index = resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return index;
    }

    public ArrayList<JSONObject>  getPushResolutions(){
        ArrayList<JSONObject> resolutions = new ArrayList<JSONObject>();
        try {
            JSONObject resp = call("get_push_resolutions", null);
            JSONArray list = resp.getJSONArray("result");//TODO maybe crash
            for (int i = 0;i < list.length();i++) {
                JSONObject p = list.getJSONObject(i);
                resolutions.add(p);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return resolutions;
    }

    public Boolean setPushResolution(String device,int index) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("device", device);
        param.put("value", index);
        try {
            JSONObject resp = call("set_push_resolution", param);
            return resp.getBoolean("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean setRtspSource(Boolean bMain) {
        try {
            Log.e(TAG, "setRtspSource: guidestream");
            JSONObject param = new JSONObject();
            param.put("value",bMain);
            JSONObject resp = call("set_rtsp_source", param);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getRtspSource() {
        boolean mainMode = false;
        try {
            JSONObject res = call("get_rtsp_source", null);
            mainMode = res.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return mainMode;
    }

    public boolean captureSingleFrame(int exposure,String device) {
        try {
            if (mCaptureState.equals("Looping")) {
                stopCapture(device);
            }
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("exposure", exposure);
            jsonObject.put("subframe", null);
            jsonObject.put("device", device);
            JSONObject res = call("capture_single_frame", jsonObject);
            LogUtil.writeWorkStatusToFile("capture single frame the right response is "+res.getBoolean("result"));
            return res.getBoolean("result");
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.writeWorkStatusToFile("capture single frame throw the exception return the false"+e.toString());
            return false;
        }
    }

    public int getExposure(String device) {
        JSONObject resp;
        int exp = 1;
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            resp = call("get_exposure", param);
            exp = resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }

        return exp;
    }

    public Boolean guidePulse(int amount,String direction) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount);
        jsonObject.put("direction", direction);
        try {
            JSONObject resp = call("guide_pulse", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean guideMove(Boolean start,String direction) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("start", start);
        jsonObject.put("direction", direction);
        try {
            JSONObject resp = call("guide_move", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String getCaptureMode(){
        try {
            JSONObject resp = call("get_capture_mode", null);
            return resp.getString("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public Boolean setCaptureMode(String mode){
        try {
            JSONObject param = new JSONObject();
            param.put("mode",mode);
            JSONObject resp = call("set_capture_mode", param);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public int getGain(String device){
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_gain", param);
            return resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setGain(String device,int value) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("device", device);
        jsonObject.put("value", value);
        try {
            JSONObject resp = call("set_gain", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public int getGainMode(String device){
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_gain_mode", param);
            return resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setGainMode(String device,int value) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("device", device);
        jsonObject.put("value", value);
        try {
            JSONObject resp = call("set_gain_mode", jsonObject);
            return resp.getBoolean("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public int getBinning(String device){
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_binning", param);
            return resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public int getBinningMax(String device){
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_binning_max", param);
            return resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }


    public Boolean setBinning(String device,int value) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("device", device);
        jsonObject.put("value", value);
        try {
            JSONObject resp = call("set_binning", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public Double getTargetTemperature(String device){
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_target_temperature", param);
            return resp.getJSONObject("result").getDouble("temperature");
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public Double getTemperature(String device){
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_temperature", param);
            return resp.getJSONObject("result").getDouble("temperature");
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Double> getRangeTemperature(String device){
        ArrayList<Double> rangeTem =new ArrayList<Double>();
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject res = call("range_temperature", param);
            JSONObject resp = res.getJSONObject("result");
            rangeTem.add(resp.getDouble("min"));
            rangeTem.add(resp.getDouble("max"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return rangeTem;
    }


    public Boolean setTargetTemperature(String device,Float value) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("device", device);
        jsonObject.put("value", value);
        try {
            JSONObject resp = call("set_target_temperature", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getScopeSpeeds(){
        ArrayList<String> scopeSpeeds =new ArrayList<String>();
        try {
            JSONObject resp = call("get_scope_speeds", null);
            JSONArray list = resp.getJSONArray("result");//TODO maybe crash
            for (int i = 0;i < list.length();i++) {
                String p = list.getJSONObject(i).getString("value");
                scopeSpeeds.add(p);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return scopeSpeeds;
    }

    public int getScopeSpeed(){
        try {
            JSONObject resp = call("get_scope_speed", null);
            return resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setScopeSpeed(int speed){
        try {
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("value", speed);
            JSONObject resp = call("set_scope_speed", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Double[] getLockPosition(){
        Double[] doubleArray = new Double[2];
        try {
            JSONObject resp = call("get_lock_position", null);
            JSONArray lockPosition = resp.getJSONArray("result");//TODO maybe crash
            doubleArray[0] = lockPosition.getDouble(0);
            doubleArray[1] = lockPosition.getDouble(1);
        } catch (Exception e){
            e.printStackTrace();
        }
        return doubleArray;
    }

    public int getFwSlotNum(){
        try {
            JSONObject resp = call("get_fw_slotnum", null);
            return resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setFwSlotNum(int slot){
        try {
            JSONObject param = new JSONObject();
            param.put("value",slot);
            JSONObject resp = call("set_fw_slotnum", param);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public int getFwPosition(){
        try {
            JSONObject resp = call("get_fw_position", null);
            return resp.getInt("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setFwPosition(int position){
        try {
            JSONObject param = new JSONObject();
            param.put("value",position);
            JSONObject resp = call("set_fw_position", param);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public Boolean getFwUnidirectional(){
        try {
            JSONObject resp = call("get_fw_unidirectional", null);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setFwUnidirectional(Boolean enable){
        try {
            JSONObject param = new JSONObject();
            param.put("value",enable);
            JSONObject resp = call("set_fw_unidirectional", param);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String getFwName(int index){
        try {
            JSONObject param = new JSONObject();
            param.put("index",index);
            JSONObject resp = call("get_fw_name", param);
            return resp.getString("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public Boolean setFwName(int index,String name) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("index", index);
        jsonObject.put("name", name);
        try {
            JSONObject resp = call("set_fw_name", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /////////////////////有返回值
    public Boolean startCapture(String device){
        try {
            Log.e(TAG, "startCapture: guidestream");
            Log.d(TAG, "run: exposure startCapture");
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("start_capture", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean guideCoord(Boolean start,Double ra,Double dec) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("start", start);
        jsonObject.put("ra", ra);
        jsonObject.put("dec", dec);
        try {
            JSONObject resp = call("guide_coord", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean calibrateFw(){
        try {
            JSONObject resp = call("calibrate_fw", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean selectDevice(String device,String name) throws JSONException {
        switch (device){
            case "main":{
                SharedPreferencesUtil.getInstance().putString(Constant.SP_MAIN_CAMERA_ID,name);
                break;
            }
            case "guide":{
                SharedPreferencesUtil.getInstance().putString(Constant.SP_GUIDE_CAMERA_ID,name);
                break;
            }
            case "mount":{
                SharedPreferencesUtil.getInstance().putString(Constant.SP_TELESCOPE_ID,name);
                break;
            }
            case "filterwheel":{
                SharedPreferencesUtil.getInstance().putString(Constant.SP_FILTER_WHEEL_ID,name);
                break;
            }
            case "focuser":{
                SharedPreferencesUtil.getInstance().putString(Constant.SP_FOCUSER_ID,name);
                break;
            }
        }
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("device", device);
        jsonObject.put("name", name);
        try {
            JSONObject resp = call("select_device", jsonObject);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean getDeviceConnected(String device){
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_device_connected", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean setDeviceConnected(String device,Boolean connected) throws JSONException {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("device", device);
        jsonObject.put("connected", connected);
        try {
            JSONObject resp = call("set_device_connected", jsonObject);
            boolean result = resp.getBoolean("result");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<String> getResolutions(String device) {
        ArrayList<String> resolutions =new ArrayList<String>();
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_resolutions", param);
            JSONArray list = resp.getJSONArray("result");//TODO maybe crash
            for (int i = 0;i < list.length();i++){
                JSONObject p = list.getJSONObject(i);
                resolutions.add(p.getString("width") + "x" + p.getString("height"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resolutions;
    }

    public int getResolution(String device) {
        int index = -1;
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_resolution", param);
            index = resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    public Boolean setResolution(String device,int value) throws JSONException {
        JSONObject param =new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_resolution", param);
            return resp.getBoolean("result");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<Integer> getFullSize(String device) {
        ArrayList<Integer> fullSize =new ArrayList<Integer>();
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject res = call("get_fullsize", param);
            JSONObject resp = res.getJSONObject("result");
            fullSize.add(resp.getInt("width"));
            fullSize.add(resp.getInt("height"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullSize;
    }
    public ArrayList<Integer> getExposures(String device) {
        ArrayList<Integer> exposures =new ArrayList<Integer>();
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_exposures", param);
            JSONArray res = resp.getJSONArray("result");//TODO maybe crash
            for (int i = 0;i < res.length();i++){
                Integer p = res.getInt(i);
                exposures.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exposures;
    }
    public ArrayList<Integer> getRangeExposures(String device) {
        ArrayList<Integer> exposures =new ArrayList<Integer>();
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("range_exposure", param);
            JSONObject res = resp.getJSONObject("result");//TODO maybe crash
            exposures.add(res.getInt("min"));
            exposures.add(res.getInt("max"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exposures;
    }
    public int  getExposureTimelapse() {
        int index = -1;
        try {
            JSONObject resp = call("get_exposure_timelapse", null);
            index = resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
    public Boolean setExposureTimelapse(int value) {
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_exposure_timelapse", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getAExposureMin() {
        try {
            JSONObject resp = call("get_aexposure_min", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public Boolean setAExposureMin(int value) {
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_aexposure_min", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getAExposureMax() {
        try {
            JSONObject resp = call("get_aexposure_max", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public Boolean setAExposureMax(int value) {
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_aexposure_max", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getAExposureTargetSnr() {
        try {
            JSONObject resp = call("get_aexposure_targetsnr", null);
            return resp.getDouble("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1f;
    }
    public Boolean setAExposureTargetSnr(Float value) {
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_aexposure_targetsnr", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getCooling(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_cooling", param);
            if (resp.getBoolean("result"))
                return "open";
            else return "close";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
    public Boolean setCooling(String device,Boolean value) throws JSONException {
        JSONObject param =new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_cooling", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getCoolingPower(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_cooling_power", param);
            return resp.getDouble("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1f;
    }
    public Boolean setCoolingPower(String device,Float value) throws JSONException {
        JSONObject param =new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_cooling_power", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getFan(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_fan", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean setFan(String device,Boolean value) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_fan", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getHeating(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_heating", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean setHeating(String device,int value) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_heating", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getDitherScale() {
        try {
            JSONObject resp = call("get_dither_scale", null);
            return resp.getDouble("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1f;
    }
    public Boolean setDitherScale(Float value) {
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_dither_scale", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int  getDitherMode() {
        try {
            JSONObject resp = call("get_dither_mode", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public Boolean setDitherMode(int value) {
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_dither_mode", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getDitherRaOnly() {
        try {
            JSONObject resp = call("get_dither_raonly", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean setDitherRaOnly(Boolean value) {
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_dither_raonly", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getFocalLength(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_focal_length", param);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public Boolean setFocalLength(String device,int value) throws JSONException {
        switch (device){
            case "main":{
                SharedPreferencesUtil.getInstance().putString(Constant.SP_FOCAL_LENGTH_MAIN_ID,value+"");
                break;
            }
            case "guide":{
                SharedPreferencesUtil.getInstance().putString(Constant.SP_FOCAL_LENGTH_GUIDE_ID,value+"");
                break;
            }
        }
        JSONObject param = new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_focal_length", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getPixelSize(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_pixel_size", param);
            return resp.getDouble("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1f;
    }
    public Boolean setPixelSize(String device,Float value) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_pixel_size", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getLowNoise(String device) {
        try {
            JSONObject param = new JSONObject();
            param.put("device",device);
            JSONObject resp = call("get_lownoise", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean setLowNoise(String device, Boolean value) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("device", device);
        param.put("value", value);
        try {
            JSONObject resp = call("set_lownoise", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getDeNoiseMethod(){
        try {
            JSONObject resp = call("get_denoise_method", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setDeNoiseMethod(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_denoise_method", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getScopeRaMax(){
        try {
            JSONObject resp = call("get_scope_ra_max", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setScopeRaMax(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_scope_ra_max", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getScopeDecMax(){
        try {
            JSONObject resp = call("get_scope_dec_max", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setScopeDecMax(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_scope_dec_max", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getScopeBaudRates(){
        ArrayList<String> scopeBaudRates =new ArrayList<String>();
        try {
            JSONObject resp = call("get_scope_baudrates", null);
            JSONArray list = resp.getJSONArray("result");//TODO maybe crash
            for (int i = 0;i < list.length();i++){
                String p = list.getJSONObject(i).getString("value");
                scopeBaudRates.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scopeBaudRates;
    }

    public int getScopeBaudRate(){
        try {
            JSONObject resp = call("get_scope_baudrate", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setScopeBaudRate(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_scope_baudrate", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean getScopeTracking(){
        try {
            JSONObject resp = call("get_scope_tracking", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setScopeTracking(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_scope_tracking", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getScopeTrackMode(){
        try {
            JSONObject resp = call("get_scope_track_mode", null);
            return resp.getString("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Boolean setScopeTrackMode(String value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_scope_track_mode", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean getScopePark(){
        try {
            JSONObject resp = call("get_scope_park", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setScopePark(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_scope_park", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<Double> getScopePos(){
        ArrayList<Double> scopePos =new ArrayList<Double>();
        try {
            JSONObject resp = call("get_scope_pos", null);
            scopePos.add(resp.getDouble("longitude"));
            scopePos.add(resp.getDouble("latitude"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scopePos;
    }

    public Boolean setScopePos(Float longitude,Float latitude) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("longitude", longitude);
        param.put("latitude", latitude);
        try {
            JSONObject resp = call("set_scope_pos", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Double> getScopeCoord(){
        ArrayList<Double> scopeCoord =new ArrayList<Double>();
        try {
            JSONObject resp = call("get_scope_coord", null);
            JSONObject res = resp.getJSONObject("result");
            scopeCoord.add(res.getDouble("ra"));
            scopeCoord.add(res.getDouble("dec"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scopeCoord;
    }

    public Boolean fwCalibrate(){
        try {
            JSONObject resp = call("fw_calibrate", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getFwSlotNumMax(){
        try {
            JSONObject resp = call("get_fw_slotnum_max", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Integer getFocuserPosition(){
        try {
            JSONObject resp = call("get_focuser_position", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean setFocuserPosition(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_focuser_position", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Boolean getFocuserReversed(){
        try {
            JSONObject resp = call("get_focuser_reversed", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean setFocuserReversed(boolean reversed){
        try {
            JSONObject param = new JSONObject();
            param.put("value",reversed);
            JSONObject resp = call("set_focuser_reversed", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getFocuserMax(){
        try {
            JSONObject resp = call("get_focuser_max", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean setFocuserMax(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_focuser_max", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getFocuserMode(){
        try {
            JSONObject resp = call("get_focuser_mode", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean setFocuserMode(boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_focuser_mode", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getFocuserStep(boolean mode){
        try {
            JSONObject param = new JSONObject();
            param.put("mode",mode);
            JSONObject resp = call("get_focuser_step", param);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean setFocuserStep(boolean mode,int value){
        try {
            JSONObject param = new JSONObject();
            param.put("mode",mode);
            param.put("value",value);
            JSONObject resp = call("set_focuser_step", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Boolean getFocuserBeep(){
        try {
            JSONObject resp = call("get_focuser_beep", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean setFocuserBeep(boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_focuser_beep", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean focuserMovePulse(boolean direction){
        try {
            JSONObject param = new JSONObject();
            param.put("direction",direction);
            JSONObject resp = call("focuser_move_pulse", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Boolean focuserMove(boolean direction){
        try {
            JSONObject param = new JSONObject();
            param.put("direction",direction);
            JSONObject resp = call("focuser_move", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Boolean focuserMoveStop(){
        try {
            mIsFocuserArriveTargetPosition = true;
            JSONObject resp = call("focuser_move_stop", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Double getFocuserTemperature(){
        try {
            JSONObject resp = call("get_focuser_temperature", null);
            return resp.getDouble("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> rangeFocuserMax() {
        ArrayList<Integer> result =new ArrayList<Integer>();
        try {
            JSONObject res = call("range_focuser_max", null);
            JSONObject resp = res.getJSONObject("result");
            result.add(resp.getInt("min"));
            result.add(resp.getInt("max"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public ArrayList<Integer> rangeFocuserPosition() {
        ArrayList<Integer> result =new ArrayList<Integer>();
        try {
            JSONObject res = call("range_focuser_position", null);
            JSONObject resp = res.getJSONObject("result");
            result.add(resp.getInt("min"));
            result.add(resp.getInt("max"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public ArrayList<Integer> rangeFocuserStep() {
        ArrayList<Integer> result =new ArrayList<Integer>();
        try {
            JSONObject res = call("range_focuser_step", null);
            JSONObject resp = res.getJSONObject("result");
            result.add(resp.getInt("min"));
            result.add(resp.getInt("max"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public JSONObject getStarImage(int size){
        try {
            JSONObject param = new JSONObject();
            param.put("size",size);
            JSONObject resp = call("get_star_image", size);
            return resp.getJSONObject("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public Boolean setStarMinHfd(Float value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_star_min_hfd", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public double getStarMinHfd(){
        try {
            JSONObject resp = call("get_star_min_hfd", null);
            return resp.getDouble("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1f;
    }

    public Boolean setStarMinSnr(Float value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_focuser_max_step", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public double getStarMinSnr(){
        try {
            JSONObject resp = call("get_star_min_snr", null);
            return resp.getDouble("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1f;
    }

    public Boolean setMultiStar(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_multi_star", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean getMultiStar(){
        try {
            JSONObject resp = call("get_multi_star", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setStarSataDu(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_star_satadu", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean getStarSataDu(){
        try {
            JSONObject resp = call("get_star_satadu", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getStarSaturation(){
        try {
            JSONObject resp = call("get_star_saturation", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setStarSaturation(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_star_saturation", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getBeepForLostStar(){
        try {
            JSONObject resp = call("get_beep_for_lost_star", null);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setBeepForLostStar(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_beep_for_lost_star", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getStarSearchRegion(){
        try {
            JSONObject resp = call("get_star_search_region", null);
            return resp.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setStarSearchRegion(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_star_satadu", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAutoSelectDownSample(){
        try {
            JSONObject resp = call("get_auto_select_downsample", null);
            return resp.getInt("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean setAutoSelectDownSample(int value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_auto_select_downsample", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject getMassChangeThreshold(){
        try {
            JSONObject resp = call("get_mass_change_threshold", null);
            return resp.getJSONObject("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public Boolean setMassChangeThreshold(Boolean enable,Float value) throws JSONException {
        JSONObject param =new JSONObject();
        param.put("enable", enable);
        param.put("value", value);
        try {
            JSONObject resp = call("set_star_satadu", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean guide(
            JSONObject settle,
            Boolean recalibrate
    ) {
        try {
            JSONObject params =new JSONObject();

            params.put("settle",settle);
            params.put("recalibrate",recalibrate);
            JSONObject resp = call("guide", params);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public Boolean getGuidePaused(){
        try {
            JSONObject resp = call("get_guide_paused", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setGuidePaused(Boolean paused,String type) throws JSONException {
        JSONObject param =new JSONObject();
        param.put("paused", paused);
        param.put("type", type);
        try {
            JSONObject resp = call("set_guide_paused", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean resetGuide(){
        try {
            JSONObject resp = call("reset_guide", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean stopGuide(){
        try {
            JSONObject resp = call("stop_guide", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getLockShiftEnabled(){
        try {
            JSONObject resp = call("get_lock_shift_enabled", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setLockShiftEnabled(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_star_satadu", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject getLockShiftParams(){
        try {
            JSONObject resp = call("get_lock_shift_params", null);
            return resp.getJSONObject("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public Boolean setLockShiftParams(JSONArray rate,String units,String axes) throws JSONException {
        JSONObject param =new JSONObject();
        param.put("rate", rate);
        param.put("units", units);
        param.put("axes", axes);
        try {
            JSONObject resp = call("set_lock_shift_params", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getGuideUseSubFrames(){
        try {
            JSONObject resp = call("get_guide_use_subframes", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setGuideUseSubFrames(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_star_satadu", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getGuideOutputEnabled(){
        try {
            JSONObject resp = call("get_guide_output_enabled", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setGuideOutputEnabled(Boolean value){
        try {
            JSONObject param = new JSONObject();
            param.put("value",value);
            JSONObject resp = call("set_star_satadu", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String  getDecGuideMode(){
        try {
            JSONObject resp = call("get_dec_guide_mode", null);
            return resp.getString("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Boolean setDecGuideMode(String mode){
        try {
            JSONObject param = new JSONObject();
            param.put("mode",mode);
            JSONObject resp = call("set_dec_guide_mode", param);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getCalibrated(){
        try {
            JSONObject resp = call("get_calibrated", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject getCalibrationData(){
        try {
            JSONObject resp = call("get_calibration_data", null);
            return resp.getJSONObject("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public Boolean clearCalibration() {
        try {
            JSONObject resp = call("clear_calibration", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean flipCalibration(){
        try {
            JSONObject resp = call("flip_calibration", null);
            return resp.getBoolean("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getAlgoParamNames(String axis){
        ArrayList<String> names =new ArrayList<String>();
        try {
            JSONObject param = new JSONObject();
            param.put("axis",axis);
            JSONObject resp = call("get_algo_param_names", param);
            JSONArray res = resp.getJSONArray("result");
            for (int i = 0;i < res.length();i++){
                String p = res.getString(i);
                names.add(p);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    public double getAlgoParam(String axis, String name) throws JSONException {
        JSONObject param =new JSONObject();
        param.put("axis", axis);
        param.put("name", name);
        try {
            JSONObject resp = call("get_algo_param", param);
            return resp.getDouble("result");
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return -1f;
    }

    public Boolean setAlgoParam(String axis,String name,Float value) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("axis", axis);
        param.put("name", name);
        param.put("value", value);
        try {
            JSONObject resp = call("get_dec_guide_mode", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean setAppTime(String time) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("value", time);
        try {
            JSONObject resp = call("set_app_time", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean setKeepAliveMsg(){
        try{
            JSONObject resp = call("keep_alive",null);
            return resp.getBoolean("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getDcOutput(int index) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("index", index);
        try {
            JSONObject resp = call("get_dc_output", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean setDcOutput(int index,boolean value) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("index", index);
        param.put("value", value);
        try {
            JSONObject resp = call("set_dc_output", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getStarProfileMode(){
        try{
            JSONObject resp = call("get_star_profile_mode",null);
            return resp.getInt("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public boolean setStarProfileMode(int mode){
        try {
            JSONObject param = new JSONObject();
            param.put("value", mode);
            JSONObject resp = call("set_star_profile_mode", param);
            return resp.getBoolean("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////

    public ArrayList<AllDevice> getAllDevices(String device) {
        ArrayList<AllDevice> devices =new ArrayList<AllDevice>();
        if (device == "CCDS") {
            devices.addAll(mCCDSAllDevices);
        } else if (device == "Telescopes") {
            devices.addAll(mTelescopesAllDevices);
        } else if (device == "FilterWheels") {
            devices.addAll(mFilterWheelsAllDevices);
        } else if (device == "Focusers") {
            devices.addAll(mFocusersAllDevices);
        }
        return devices;
    }

    public void clearChartInfoData(){
        mIndexChart = 0;
        mValuesDX.clear();
        mValuesDY.clear();
        mValuesSNR.clear();
        mValuesStarMass.clear();
        mValuesDECDuration.clear();
        mValuesRADuration.clear();
        mValuesRADistanceRaw.clear();
        mValuesDecDistanceRaw.clear();
    }

    private void updateChartInfo(){
        if (mIndexChart > maxisMaximum + 1) {
            mIndexChart = (int)maxisMaximum + 1;
            mValuesDX.remove(0);
            mValuesDY.remove(0);
            mValuesSNR.remove(0);
            mValuesStarMass.remove(0);
            mValuesDECDuration.remove(0);
            mValuesRADuration.remove(0);
            mValuesRADistanceRaw.remove(0);
            mValuesDecDistanceRaw.remove(0);
        }
        mIndexChart++;
        int maxScale = 4;
        if (mChartData.mDx != null) {
            if (mChartData.mDx.floatValue()/mScaleD > maxScale){
                mScaleD = mChartData.mDx.floatValue() / maxScale;
            }
            mValuesDX.add(mChartData.mDx.floatValue()/mScaleD);
        }
        if (mChartData.mDy != null) {
            if (mChartData.mDy.floatValue()/mScaleD > maxScale){
                mScaleD = mChartData.mDy.floatValue() / maxScale;
            }
            mValuesDY.add(mChartData.mDy.floatValue()/mScaleD);
        }
        if (mChartData.mStarMass != null) {
            if (mChartData.mStarMass.floatValue()/mScaleStarMass > maxScale){
                mScaleStarMass = mChartData.mStarMass.floatValue() / maxScale;
            }
            mValuesStarMass.add(mChartData.mStarMass.floatValue()/mScaleStarMass);
        }
        if (mChartData.mSNR != null) {
            if (mChartData.mSNR.floatValue()/mScaleSNR > maxScale){
                mScaleSNR = mChartData.mSNR.floatValue() / maxScale;
            }
            mValuesSNR.add(mChartData.mSNR.floatValue()/mScaleSNR);
        }
        if (mChartData.mRADistanceRaw != null) {
            if (mChartData.mRADistanceRaw.floatValue()/mScaleDistanceRaw > maxScale){
                mScaleDistanceRaw = mChartData.mRADistanceRaw.floatValue() / maxScale;
            }
            mValuesRADistanceRaw.add(mChartData.mRADistanceRaw.floatValue()/mScaleDistanceRaw);
        }
        if (mChartData.mDEDDistanceRaw != null) {
            if (mChartData.mDEDDistanceRaw.floatValue()/mScaleDistanceRaw > maxScale){
                mScaleDistanceRaw = mChartData.mDEDDistanceRaw.floatValue() / maxScale;
            }
            mValuesDecDistanceRaw.add(mChartData.mDEDDistanceRaw.floatValue()/mScaleDistanceRaw);
        }
        int duration = 0;

        if (mChartData.mRADuration != null){
            if (mChartData.mRADuration /mScaleDuration > maxScale){
                mScaleDuration = mChartData.mRADuration / maxScale;
            }
            if (mChartData.mRADirection.equals("West") ) {
                duration = mChartData.mRADuration;
            }
            else {
                duration = -mChartData.mRADuration;
            }
        }
        mValuesRADuration.add((float) duration/mScaleDuration);
        duration = 0;
        if (mChartData.mDECDuration != null){
            if (mChartData.mDECDuration /mScaleDuration > maxScale){
                mScaleDuration = mChartData.mDECDuration / maxScale;
            }
            if (mChartData.mDECDirection.equals("North") ) {
                duration = mChartData.mDECDuration;
            }
            else duration = -mChartData.mDECDuration;
        }

        mValuesDECDuration.add((float)duration / mScaleDuration);
    }


    public boolean isAllDevicesLoaded(){
        return mIsMainLoaded && mIsGuideLoaded && mIsFFLoaded && mIsScopeLoaded && mIsFocuserLoaded;
    }

    private void checkAllDataLoaded(){
        if (isAllDevicesLoaded()){
            for (Handler handler:mHandlerList){
                if (handler != null){
                    Message msg = handler.obtainMessage();
                    msg.what = TpConst.MSG_ALL_CACHE_LOADED;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private class FocuserCurrentPositionChangedTask extends TimerTask {
        @Override
        public void run() {
            Integer currentPosition = mFocuserPosition;
            Integer targetPosition = mFocuserTargetPosition;
            if ( (currentPosition != null) && (targetPosition != null)){
                if (currentPosition.compareTo(targetPosition) != 0){
                    mFocuserPosition = getFocuserPosition();
                    mIsFocuserArriveTargetPosition = true;
                    mFocuserIsMoving = false;
                    for (Handler handler:mHandlerList){
                        if (handler != null){
                            Message msg = handler.obtainMessage();
                            msg.what = TpConst.MSG_FOCUSER_CURRENT_POSITION_CHANGED;
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }
    private void startFocuserCurrentPositionChangedTask(){
        stopFocuserCurrentPositionChangedTask();
        mFocuserCurrentPositionChangedTimer = new Timer();
        mFocuserCurrentPositionChangedTask = new FocuserCurrentPositionChangedTask();
        mFocuserCurrentPositionChangedTimer.schedule(mFocuserCurrentPositionChangedTask,2000);
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
