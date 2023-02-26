package com.glide.chujian.fragment;

import static com.glide.chujian.util.DeviceUtil.isLabel;
import static com.glide.chujian.util.DeviceUtil.isLabelConfigured;
import static com.glide.chujian.util.DeviceUtil.parseDevices;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glide.chujian.AstroLibraryActivity;
import com.glide.chujian.R;
import com.glide.chujian.databinding.FragmentScopeBinding;
import com.glide.chujian.model.GuiderModel.*;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.DateUtil;
import com.glide.chujian.util.GuideUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpSettingScopeHandler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class ScopeFragment extends BaseFragment<FragmentScopeBinding>{
    private static String TAG = "ScopeFragment";
    private ArrayAdapter<String> mMountAdapter;
    private ArrayAdapter<String> mBaudRateAdapter;
    private ArrayAdapter<String> mGuidingSpeedAdapter;
    private ArrayList<ToggleButton> mModeToggleBtnList = new ArrayList<ToggleButton>();
    private ArrayList<ToggleButton> mParkToggleBtnList = new ArrayList<ToggleButton>();

    TpSettingScopeHandler mHandler = new TpSettingScopeHandler(this);

    private RaDecTask mRaDecTask;
    private Timer mRaDecTimer;
    private String mScopeCurrentDevice = "";
    ArrayList<AllDevice> mTelescopeAllDevices =new ArrayList<AllDevice>();
    ArrayList<String> mTelescopeAllDevicesList =new ArrayList<String>();
    ArrayList<String> mBaudRateList =new ArrayList<String>();
    ArrayList<String> mGuidingSpeedList =new ArrayList<String>();
    private boolean mDeviceChangeStatus = false;//相机将要切换到的状态，用于定时弹出UI的判断
    private Timer mTimer;
    private MyTimerTask mTimerTask;
    private long mLastActionTime;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_scope;
    }

    private void updateRaDecView(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mIsScopeValid){
                    binding.scopeRaValueTv.setText(GuideUtil.DegreeToRa(mGuider.mCoordRa));
                    binding.scopeDecValueTv.setText(GuideUtil.DegreeToDec(mGuider.mCoordDec));
                }else {
                    binding.scopeRaValueTv.setText("null");
                    binding.scopeDecValueTv.setText("null");
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
    public void onDeviceSelectChanged() {
        String device = mGuider.mSelectedDevices.get(mGuider.SELECTED_DEVICE);
        if (device == null)return;
        switch (device) {
            case "mount": {
                loadScopeDeviceView();
            }
            break;
        }
    }
    @Override
    public void waitDeviceStatusChange(Boolean status) {
        mDeviceChangeStatus = status;
        startTimer();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocation();
        loadScopeDeviceView();
        loadScopePropertyView();
    }

    private void setDeviceConnectedStatus(Boolean isNetworkOnline,boolean connected) {
        if (isNetworkOnline) {
            binding.selectMount.setNorEnabled(!connected);
            if (mScopeCurrentDevice.equals("None")){
                binding.mountConnectBtn.setEnabled(false);
            }else {
                binding.mountConnectBtn.setEnabled(true);
            }
            binding.mountConnectBtn.setChecked(connected);
        }else {
            binding.selectMount.setNorEnabled(false);
            binding.mountConnectBtn.setEnabled(false);
            binding.mountConnectBtn.setChecked(false);
        }
        binding.mountTv.setEnabled(isNetworkOnline);
        ////
        binding.selectBaudRate.setNorEnabled(connected);
      //  binding.uartTbtn.setEnabled(connected);
      //  binding.astroLibraryGoBtn.setEnabled(connected);
        binding.selectGuidingSpeed.setEnabled(connected);
        binding.trackingSwitch.setEnabled(connected);
        binding.siderealTbtn.setEnabled(connected);
        binding.solarTbtn.setEnabled(connected);
        binding.lunarTbtn.setEnabled(connected);
        binding.parkTbtn.setEnabled(connected);
        binding.unparkTbtn.setEnabled(connected);
        if (connected){
            binding.scopeLongitudeValueTv.setVisibility(View.VISIBLE);
            binding.scopeLatitudeValueTv.setVisibility(View.VISIBLE);
            binding.scopeRaValueTv.setVisibility(View.VISIBLE);
            binding.scopeDecValueTv.setVisibility(View.VISIBLE);
            binding.scopeTimeZoneValueTv.setVisibility(View.VISIBLE);
            binding.scopeTimeValueTv.setVisibility(View.VISIBLE);
        }else {
            binding.scopeLongitudeValueTv.setVisibility(View.INVISIBLE);
            binding.scopeLatitudeValueTv.setVisibility(View.INVISIBLE);
            binding.scopeRaValueTv.setVisibility(View.INVISIBLE);
            binding.scopeDecValueTv.setVisibility(View.INVISIBLE);
            binding.scopeTimeZoneValueTv.setVisibility(View.INVISIBLE);
            binding.scopeTimeValueTv.setVisibility(View.INVISIBLE);
        }
        nameViewEnable(connected);
        loadScopePropertyView();
    }

    private void nameViewEnable(boolean enable){
        binding.baudRateTv.setEnabled(enable);
        binding.astroLibraryTv.setEnabled(enable);
        binding.scopeLongitudeNameTv.setEnabled(enable);
        binding.scopeLongitudeValueTv.setEnabled(enable);
        binding.scopeLatitudeValueTv.setEnabled(enable);
        binding.scopeLatitudeNameTv.setEnabled(enable);
        binding.scopeLatitudeValueTv.setEnabled(enable);
        binding.scopeRaNameTv.setEnabled(enable);
        binding.scopeRaValueTv.setEnabled(enable);
        binding.scopeDecNameTv.setEnabled(enable);
        binding.scopeDecValueTv.setEnabled(enable);
        binding.scopeTimeZoneNameTv.setEnabled(enable);
        binding.scopeTimeZoneValueTv.setEnabled(enable);
        binding.scopeTimeNameTv.setEnabled(enable);
        binding.scopeTimeValueTv.setEnabled(enable);
        binding.guidingSpeedTv.setEnabled(enable);
        binding.trackingTv.setEnabled(enable);
        binding.trackingRateTv.setEnabled(enable);
        binding.parkTv.setEnabled(enable);
    }
    private void init(){
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        binding.selectMount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(mGuider.mScopeIsDeviceConnected)return;
                String item = mMountAdapter.getItem(position);
                if (item.equals("None")){
                    binding.mountConnectBtn.setEnabled(false);
                }else {
                    binding.mountConnectBtn.setEnabled(true);
                }
                if (Objects.equals(mGuider.mSelectedDevices.get("mount"), item))return;
                if (isLabel(mTelescopeAllDevices, item)) {
                    if (isLabelConfigured(mTelescopeAllDevices, item)) {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.selectDevice("mount", item);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.startDriver(item);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                } else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mScopeCurrentDevice = item;
                                mGuider.selectDevice("mount", item);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.selectBaudRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (mGuider.mScopeBaudrate != i) {
                            mGuider.setScopeBaudRate(i);
                        }
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.selectGuidingSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (mGuider.mMountSpeedChangedValue != i) {
                            mGuider.setScopeSpeed(i);
                        }
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //todo
        binding.mountConnectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mLoadingDialog.show();
                    new Thread(){
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void run() {
                            super.run();
                            try {
                                boolean isMountConnected = mGuider.mScopeIsDeviceConnected;
                                waitDeviceStatusChange(!isMountConnected);

                                mIsConnectTimeOut = false;
                                boolean result = mGuider.setDeviceConnected("mount",!isMountConnected);
                                if (result) {
                                    if (isMountConnected) {
                                        mGuider.stopGuide();
                                    }
                                } else {
                                    stopTimer();
                                    if (!mIsConnectTimeOut) {
                                        ToastUtil.showToast(getResources().getString(R.string.device_not_connect), false);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                return true;
            }
        });

        binding.uartTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        binding.astroLibraryGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AstroLibraryActivity.actionStart(mActivity);
            }
        });

        String[] zone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT).split(":");
        if (zone.length != 0) {
            binding.scopeTimeZoneValueTv.setText(zone[0]);
        }

        binding.trackingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setScopeTracking(binding.trackingSwitch.isChecked())){
                            responseTrackingSwitch();
                        }
                    }
                }.start();
            }
        });
        binding.siderealTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.siderealTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setScopeTrackMode("sidereal")){
                            responseTrackingMode();
                        }
                    }
                }.start();
            }
        });

        binding.solarTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.solarTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setScopeTrackMode("solar")){
                            responseTrackingMode();
                        }
                    }
                }.start();
            }
        });
        binding.lunarTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.lunarTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setScopeTrackMode("lunar")){
                            responseTrackingMode();
                        }
                    }
                }.start();
            }
        });

        binding.parkTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealParkConflict(binding.parkTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setScopePark(true)){
                            responseParkSwitch();
                        }
                    }
                }.start();
            }
        });

        binding.unparkTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealParkConflict(binding.unparkTbtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setScopePark(false)){
                            responseParkSwitch();
                        }
                    }
                }.start();
            }
        });

    }

    private void loadScopeDeviceView(){
        mTelescopeAllDevices.clear();
        mTelescopeAllDevicesList.clear();
        mTelescopeAllDevices.addAll(mGuider.getAllDevices("Telescopes"));
        mTelescopeAllDevicesList.addAll(parseDevices(mTelescopeAllDevices));

        if (mTelescopeAllDevicesList.contains("None")){
            mTelescopeAllDevicesList.remove("None");
            mTelescopeAllDevicesList.add(0,"None");
        }

        mMountAdapter.clear();
        mMountAdapter.notifyDataSetChanged();
        mMountAdapter.addAll(mTelescopeAllDevicesList);
        mMountAdapter.notifyDataSetChanged();
        String currentDevice = mGuider.mSelectedDevices.get("mount");

        if (currentDevice == null){

        }
        else if (currentDevice .equals("None")){
            String mount = SharedPreferencesUtil.getInstance().getString(Constant.SP_TELESCOPE_ID,"");
            if (!mount.equals("None") && !mount.equals("") && mTelescopeAllDevicesList.contains(mount) && !isLabel(mTelescopeAllDevices, mount)){
                mGuider.mSelectedDevices.put("mount",mount);
                currentDevice = mount;
                int mountIndex = mTelescopeAllDevicesList.indexOf(currentDevice);
                if (mountIndex != -1){
                    mScopeCurrentDevice = currentDevice;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("mount", mScopeCurrentDevice);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }

        mScopeCurrentDevice = currentDevice;
        int mountIndex = mTelescopeAllDevicesList.indexOf(mScopeCurrentDevice);
        if (mountIndex != -1) {
            binding.selectMount.setSelection(mountIndex);
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDeviceConnectedStatus(mGuider.mIsNetworkOnLine,mGuider.mScopeIsDeviceConnected);
            }
        });
    }

    private void resetScopeView(){
        mBaudRateAdapter.clear();
        mBaudRateAdapter.notifyDataSetChanged();
        mGuidingSpeedAdapter.clear();
        mGuidingSpeedAdapter.notifyDataSetChanged();
    }
    private void loadScopeViewData(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                mBaudRateList.clear();
                int baudrate = mGuider.mScopeBaudrate;
                mBaudRateList.addAll(mGuider.mScopeBaudrateList);

                ArrayList<String> guidingSpeedList = mGuider.mScopeGuidingSpeedList;

                mGuidingSpeedList.clear();
                for (String speed : guidingSpeedList){
                    mGuidingSpeedList.add(speed);
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBaudRateAdapter.clear();
                        mBaudRateAdapter.addAll(mBaudRateList);
                        if (baudrate != -1){
                            binding.selectBaudRate.setSelection(baudrate);
                        }
                        setTrackingMode(mGuider.mScopeTrackingRate);
                        mGuidingSpeedAdapter.clear();
                        mGuidingSpeedAdapter.addAll(mGuidingSpeedList);
                        mGuidingSpeedAdapter.notifyDataSetChanged();
                        binding.selectGuidingSpeed.setSelection(mGuider.mMountSpeedChangedValue);

                        binding.trackingSwitch.setChecked(mGuider.mScopeTracking);

                        if (mGuider.mIsScopeValid){
                            binding.scopeRaValueTv.setText(GuideUtil.DegreeToRa(mGuider.mCoordRa));
                            binding.scopeDecValueTv.setText(GuideUtil.DegreeToDec(mGuider.mCoordDec));
                        }else {
                            binding.scopeRaValueTv.setText("null");
                            binding.scopeDecValueTv.setText("null");
                        }
                        binding.parkTbtn.setChecked(mGuider.mScopePark);
                        binding.unparkTbtn.setChecked(!mGuider.mScopePark);
                        binding.mountConnectBtn.setChecked(mGuider.mScopeIsDeviceConnected);
                    }
                });


            }
        }.start();
    }

    private void responseTrackingSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.trackingSwitch.setChecked(mGuider.mScopeTracking);
            }
        });
    }

    private void responseParkSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.parkTbtn.setChecked(mGuider.mScopePark);
                binding.unparkTbtn.setChecked(!mGuider.mScopePark);
            }
        });
    }

    private void responseTrackingMode(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTrackingMode(mGuider.mScopeTrackingRate);
            }
        });
    }

    private void setTrackingMode(String mode){
        if (mode.equals("sidereal")){
            dealTypeConflict(binding.siderealTbtn);
            binding.siderealTbtn.setChecked(true);
        }else if(mode.equals("solar")){
            dealTypeConflict(binding.solarTbtn);
            binding.solarTbtn.setChecked(true);
        }else if(mode.equals("lunar")){
            dealTypeConflict(binding.lunarTbtn);
            binding.lunarTbtn.setChecked(true);
        }else{
        }
    }
    private void loadScopePropertyView(){
        if (mGuider.mScopeIsDeviceConnected){
            loadScopeViewData();
        }else {
            resetScopeView();
        }
    }
    private void initData() {
        getLocation();
        mModeToggleBtnList.add(binding.siderealTbtn);
        mModeToggleBtnList.add(binding.solarTbtn);
        mModeToggleBtnList.add(binding.lunarTbtn);

        mParkToggleBtnList.add(binding.parkTbtn);
        mParkToggleBtnList.add(binding.unparkTbtn);
        loadScopeDeviceView();
        loadScopePropertyView();
    }

    private void initView() {
        mMountAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mMountAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        mBaudRateAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mBaudRateAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        mGuidingSpeedAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mGuidingSpeedAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        binding.selectMount.setAdapter(mMountAdapter);

        binding.selectBaudRate.setAdapter(mBaudRateAdapter);

        binding.selectGuidingSpeed.setAdapter(mGuidingSpeedAdapter);
    }

    private void getLocation(){

        double longitude = SharedPreferencesUtil.getInstance().getDouble(Constant.SP_LONGITUDE_ID, (double) -1);
        double latitude = SharedPreferencesUtil.getInstance().getDouble(Constant.SP_LATITUDE_ID, (double) -1);
        if (longitude != -1){
            updateLocationUI(longitude,latitude);
        }
    }

    private void updateLocationUI(double Longitude,double Latitude){
        binding.scopeLongitudeValueTv.setText(DateUtil.translateLongitude(Longitude)+DateUtil.translateLocation(Longitude));
        binding.scopeLatitudeValueTv.setText(DateUtil.translateLatitude(Latitude)+DateUtil.translateLocation(Latitude));
    }

    private void dealTypeConflict(ToggleButton toggleButton){
        toggleButton.setChecked(true);
        for (ToggleButton it:mModeToggleBtnList){
            if (toggleButton != it){
                if (it.isChecked()) it.setChecked(false);
            }
        }
    }

    private void dealParkConflict(ToggleButton toggleButton){
        toggleButton.setChecked(true);
        for (ToggleButton it:mParkToggleBtnList){
            if (toggleButton != it){
                it.setChecked(false);
            }
        }
    }

    public void updateDevices(){
        loadScopeDeviceView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            stopRaDecTask();
            mGuider.unregisterHandlerListener(mHandler);
        }else{
            mGuider.registerHandlerListener(mHandler);
            updateDevices();
            if (mGuider.mScopeIsDeviceConnected){
                startRaDecTask();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGuider.unregisterHandlerListener(mHandler);
    }

    public void deviceConnectEvent(){
        if (!mGuider.mCurrentConnectedDevice.equals("mount")) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mScopeIsDeviceConnected) {
                    startRaDecTask();
                  //  setDeviceConnectedStatus(mGuider.mIsNetworkOnLine,true);
                } else {
                    stopRaDecTask();
                    setDeviceConnectedStatus(mGuider.mIsNetworkOnLine,false);
                }
            }
        });
        //  }
    }

    public void mountBaudrateChangedEvent(){
        binding.selectBaudRate.setSelection(mGuider.mScopeBaudrate);
    }

    public void mountTrackingChangedEvent(){
        binding.trackingSwitch.setChecked(mGuider.mScopeTracking);
    }
    public void mountTrackingModeChangedEvent(){
        setTrackingMode(mGuider.mScopeTrackingRate);
    }
    public void mountSpeedChangedEvent(){
        binding.selectGuidingSpeed.setSelection(mGuider.mMountSpeedChangedValue);
    }

    public void mountParkChangedEvent(){
        binding.parkTbtn.setChecked(mGuider.mScopePark);
        binding.unparkTbtn.setChecked(!mGuider.mScopePark);
    }

    public void parameterLoadSuccess(){
        loadScopeDeviceView();
        if (mGuider.mScopeIsDeviceConnected) {
            stopTimer();
            startRaDecTask();
            //setDeviceConnectedStatus(mGuider.isNetworkOnLine,true);
        } else {
            setDeviceConnectedStatus(mGuider.mIsNetworkOnLine,false);
        }
        loadScopePropertyView();
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mLastActionTime > 5000) {
                mIsConnectTimeOut = true;
                if (!mDeviceChangeStatus){
                    stopTimer();
                }
            }else if (!mGuider.mScopeIsDeviceConnected){
                if (!mDeviceChangeStatus) {
                    stopTimer();
                }
            }
        }
    }

    private void startTimer() {
        if (mTimer != null){
            stopTimer();
        }
        mTimer = new Timer(true);
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, 0, 500);
        mLastActionTime = System.currentTimeMillis();
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mLoadingDialog.dismiss();
        }
        if (mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

}
