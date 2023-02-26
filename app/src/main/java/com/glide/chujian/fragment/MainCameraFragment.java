package com.glide.chujian.fragment;

import static com.glide.chujian.util.DeviceUtil.*;
import static com.glide.chujian.util.DeviceUtil.parseDevices;
import static com.glide.chujian.util.GuideUtil.parseBinNumber;

import static java.lang.Math.pow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.glide.chujian.R;
import com.glide.chujian.databinding.FragmentMainCameraBinding;
import com.glide.chujian.model.GuiderModel.*;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.LogUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpSettingMainHandler;
import com.glide.chujian.util.CheckParamUtil;
import com.glide.chujian.view.MinSeekBar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainCameraFragment extends BaseFragment<FragmentMainCameraBinding> {
    private final String TAG = "MainCameraFragment";
    private ArrayAdapter<String> mMainCameraAdapter;
    private ArrayAdapter<String> mResolutionAdapter;
    private ArrayAdapter<String> mBinningAdapter;
    private ArrayList<ToggleButton> mToggleBtnList = new ArrayList<ToggleButton>();
    private int mExpMin = 0;
    private int mExpProgress = 0;

    private long mExpTimeMin;
    private long mExpTimeMax;
    private int mExpSliderMin;
    private int mExpSliderMax;
    private boolean mDeviceChangeStatus = false;//相机将要切换到的状态，用于定时弹出UI的判断

    private String mMainCurrentDevice = "";
    ArrayList<AllDevice> mCCDSAllDevices = new ArrayList<AllDevice>();
    ArrayList<String> mCCDSAllDevicesList = new ArrayList<String>();

    TpSettingMainHandler mHandler = new TpSettingMainHandler(this);

    private Timer mTimer;
    private MyTimerTask mTimerTask;
    private long mLastActionTime;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main_camera;
    }

    @Override
    public void onDeviceSelectChanged() {
        String device = mGuider.mSelectedDevices.get(mGuider.SELECTED_DEVICE);
        if (device == null)return;
        switch (device) {
            case "main": {
                loadMainCameraDeviceView();
            }
            break;
        }
    }

    @Override
    public void waitDeviceStatusChange(Boolean status) {
        mDeviceChangeStatus = status;
        LogUtil.writeDeviceConnectToFile("点击设备连接的时间点");
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
        loadMainCameraDeviceView();
    }

    private void init(){
        initView();
        initData();
        initEvent();
    }

    private void loadMainCameraDeviceView(){
        mCCDSAllDevices.clear();
        mCCDSAllDevicesList.clear();
        mCCDSAllDevices.addAll(mGuider.getAllDevices("CCDS"));
        mCCDSAllDevicesList.addAll(parseDevices(mCCDSAllDevices));

        if (mCCDSAllDevicesList.contains("None")){
            mCCDSAllDevicesList.remove("None");
            mCCDSAllDevicesList.add(0,"None");
        }

        ArrayList<String> dataList =new ArrayList<String>();
        dataList.addAll(mCCDSAllDevicesList);
        String item = mGuider.mSelectedDevices.get("guide");
        if (item == null){}
        else if (item.equals("None")) {
        } else {
            if (dataList.contains(item)) {
                dataList.remove(item);
            }
        }
        mMainCameraAdapter.clear();
        mMainCameraAdapter.notifyDataSetChanged();
        mMainCameraAdapter.addAll(dataList);
        mMainCameraAdapter.notifyDataSetChanged();

        String currentDevice = mGuider.mSelectedDevices.get("main");

        if (currentDevice == null){

        }
        else if (currentDevice.equals("None")){
            String main = SharedPreferencesUtil.getInstance().getString(Constant.SP_MAIN_CAMERA_ID,"");
            if (!main.equals("None") && !main.equals("") && dataList.contains(main) && !isLabel(mCCDSAllDevices, main)){
                mGuider.mSelectedDevices.put("main",main);
                currentDevice = main;
                int index = dataList.indexOf(currentDevice);
                if (index != -1) {
                    mMainCurrentDevice = currentDevice;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("main", mMainCurrentDevice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }
        mMainCurrentDevice = currentDevice;
        int index = dataList.indexOf(mMainCurrentDevice);
        if (index != -1) {
            binding.selectMainCamera.setSelection(index);
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mMainIsDeviceConnected){
                    setMainCameraViewStatus(mGuider.mIsNetworkOnLine,true);
                }else {
                    setMainCameraViewStatus(mGuider.mIsNetworkOnLine,false);
                }
            }
        });
    }

    private void loadMainCameraPropertyView(){
        if (mGuider.mMainIsDeviceConnected){
            loadMainCameraViewData();
        }else {
            resetMainCameraView();
        }
    }


    public void updateDevices(){
        loadMainCameraDeviceView();
    }

    private void initView(){
        binding.heatingSeekbar.setEnabled(false);
        mMainCameraAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mMainCameraAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mResolutionAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mResolutionAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mBinningAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mBinningAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        binding.selectMainCamera.setAdapter(mMainCameraAdapter);
        binding.selectResolution.setAdapter(mResolutionAdapter);
        binding.selectBining.setAdapter(mBinningAdapter);
    }

    private void initData(){
        mToggleBtnList.add(binding.hcgBtn);
        mToggleBtnList.add(binding.lcgBtn);
        mToggleBtnList.add(binding.hdrBtn);

        loadMainCameraDeviceView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent(){
        mActivity.showSoftInput(binding.focuseDegreeTv,true,false);
        mActivity.showSoftInput(binding.targetTempEt,false,true);
        binding.selectMainCamera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (mGuider.mMainIsDeviceConnected)return;
                String item = mMainCameraAdapter.getItem(position);
                if (item.equals("None")){
                    binding.mainCameraConnectBtn.setEnabled(false);
                }else {
                    binding.mainCameraConnectBtn.setEnabled(true);
                }
                if (Objects.equals(mGuider.mSelectedDevices.get("main"), item))return;
                if (isLabel(mCCDSAllDevices, item)) {
                    if (isLabelConfigured(mCCDSAllDevices, item)) {
                        mMainCurrentDevice = item;
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.selectDevice("main", mMainCurrentDevice);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        mMainCurrentDevice = item;
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.startDriver(item);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mGuider.mSelectedDevices.put(mGuider.START_DRIVER,"main");
                            }
                        }.start();
                    }
                } else {
                    mMainCurrentDevice = item;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("main", mMainCurrentDevice);
                            } catch (JSONException e) {
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
        binding.selectResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (i != mGuider.mMainCurrentResolution) {
                                mGuider.setResolution("main", i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        binding.selectBining.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            int binning = parseBinNumber(mBinningAdapter.getItem(position));
                            if (binning != mGuider.mMainBinning){
                                mGuider.setBinning("main",binning);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.mainCameraConnectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mLoadingDialog.show();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                boolean isMainCameraConnected = mGuider.mMainIsDeviceConnected;
                                waitDeviceStatusChange(!isMainCameraConnected);
                                if (isMainCameraConnected){
                                    mGuider.stopCapture( "main");
                                }else{
                                }
                                mIsConnectTimeOut = false;
                                boolean result = mGuider.setDeviceConnected("main",!isMainCameraConnected);
                                if (!result) {
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
        binding.focuseDegreeTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String input = binding.focuseDegreeTv.getText().toString();

                    if (input.equals("")){
                        binding.focuseDegreeTv.getText().clear();
                        binding.focuseDegreeTv.setText(mGuider.mMainFocalLength+"");
                        return;
                    }
/*                    if (!CheckParamUtil.isRangeLegal(mActivity,input,0.0,100000.0,1.0,"")){
                        binding.focuseDegreeTv.getText().clear();
                        binding.focuseDegreeTv.setText(mGuider.mMainFocalLength+"");
                        return;
                    }*/
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                int mainLength = 0;
                                if(!input.trim().equals("")){
                                    mainLength = Integer.parseInt(input);
                                }
                                mGuider.setFocalLength("main",mainLength);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }) ;
        binding.expSeekbar.setOnSeekBarChangeListener(new MinSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(AppCompatSeekBar seekBar, int progress, boolean fromUser) {
                long data = (long) (expSlider2Real(progress)/1000.0);
                mExpProgress = (int) data;
                binding.expDegreeTv.setText(mExpProgress +"");;
            }

            @Override
            public void onStartTrackingTouch(AppCompatSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(AppCompatSeekBar seekBar) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            mGuider.setExposure("main", mExpProgress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.gainSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.gainDegreeTv.setText(String.valueOf(i));;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            mGuider.setGain("main",seekBar.getProgress());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.hcgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.hcgBtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if(!mGuider.setGainMode("main",1)){
                                responseGainMode();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        binding.lcgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.lcgBtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if(!mGuider.setGainMode("main",0)){
                                responseGainMode();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        binding.hdrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.hdrBtn);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if(!mGuider.setGainMode("main",2)){
                                responseGainMode();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.lowNoiseSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (!mGuider.setLowNoise("main",binding.lowNoiseSwitch.isChecked())){
                                responseLowNoiseSwitch();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.coolingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            boolean b = binding.coolingSwitch.isChecked();
                            if (mGuider.setCooling("main",b)) {
                                if (b) {
                                    mGuider.mMainCooling = "open";
                                }else {
                                    mGuider.mMainCooling = "close";
                                }
                            }else {
                                responseCoolingSwitch();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        binding.fanSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = binding.fanSwitch.isChecked();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (!mGuider.setFan("main",b)){
                                responseFanSwitch();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
//Todo 暂时先不做
        binding.heatingSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.targetTempEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    String temperature = binding.targetTempEt.getText().toString();
                    if (temperature.trim().equals("")) {
                        binding.targetTempEt.setText(Double.parseDouble(String.format("%.2f",mGuider.mMainTargetTemp))+"");
                        return;
                    }
                    if (!CheckParamUtil.isRangeLegal(mActivity,temperature,mGuider.mMainMinTemp,mGuider.mMainMaxTemp,1.0,"℃")){
                        binding.targetTempEt.getText().clear();
                        binding.targetTempEt.setText(Double.parseDouble(String.format("%.2f",mGuider.mMainTargetTemp))+"");
                        return;
                    }
                    Double value = Double.valueOf(temperature);
                    String temp = String.format("%.2f",value);
                    String strPrice = Double.parseDouble(temp)+"";
                    binding.targetTempEt.getText().clear();
                    binding.targetTempEt.setText(strPrice);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (!strPrice.trim().equals(""))
                            {
                                try {
                                    if (mGuider.mMainCooling.equals("close")){
                                        if(mGuider.setCooling("main",true)){
                                            mGuider.mMainCooling = "open";
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    binding.coolingSwitch.setChecked(true);
                                                }
                                            });
                                            if(mGuider.setTargetTemperature("main",Float.valueOf(strPrice))){
                                            }
                                        }
                                    }else {
                                        if(mGuider.setTargetTemperature("main",Float.valueOf(strPrice))){
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
            }
        });
    }

    private void responseGainMode(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateGainMode(mGuider.mMainGainMode);
            }
        });
    }
    private void dealTypeConflict(ToggleButton toggleButton){
        toggleButton.setChecked(true);
        for (ToggleButton it:mToggleBtnList){
            if (toggleButton != it){
                if (it.isChecked()) it.setChecked(false);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            mGuider.unregisterHandlerListener(mHandler);
            mLoadingDialog.dismiss();
        }else{
            mGuider.registerHandlerListener(mHandler);
            loadMainCameraDeviceView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGuider.unregisterHandlerListener(mHandler);
    }

    public void deviceConnectEvent(){
        if (!mGuider.mCurrentConnectedDevice.equals("main")) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mMainIsDeviceConnected) {
                   /* binding.mainCameraConnectBtn.setChecked(true);
                    setMainCameraViewStatus(mGuider.mIsNetworkOnLine,true);*/
                } else {
                    binding.mainCameraConnectBtn.setChecked(false);
                    setMainCameraViewStatus(mGuider.mIsNetworkOnLine,false);
                }
            }
        });
    }

    private void updateGainCache(int gain){
        if (gain == -1)return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.gainSeekbar.setProgress(gain);
                binding.gainDegreeTv.setText(String.valueOf(gain));
            }
        });
    }

    private void updateExpView(){
        try {
            if (mGuider.mMainExpRange.size() != 0) {
                mExpMin = mGuider.mMainExpRange.get(0);
                int max = mGuider.mMainExpRange.get(1);
                if (mGuider.mMainCaptureMode.equals("Video")){
                    if (max > 5000){
                        max = 5000;
                    }
                    expSliderRange(mExpMin * 1000,((long)max) * 1000);
                    binding.expSeekbar.setBarMax(mExpSliderMax);
                    binding.expSeekbar.setBarMin(mExpSliderMin);
                    if (mGuider.mMainExpData > 5000){
                        int sliderProgress = real2ExpSlider(5000 * 1000);
                        binding.expSeekbar.setProgress(sliderProgress);
                        binding.expDegreeTv.setText("5000");
                    }else {
                        int sliderProgress = real2ExpSlider(mGuider.mMainExpData * 1000);
                        binding.expSeekbar.setProgress(sliderProgress);
                        binding.expDegreeTv.setText(mGuider.mMainExpData+"");
                    }
                }else if(mGuider.mMainCaptureMode.equals("Single")){
                    expSliderRange(mExpMin * 1000,((long)max) * 1000);
                    binding.expSeekbar.setBarMax(mExpSliderMax);
                    binding.expSeekbar.setBarMin(mExpSliderMin);
                    int sliderProgress = real2ExpSlider(mGuider.mMainExpData * 1000);
                    binding.expSeekbar.setProgress(sliderProgress);
                    binding.expDegreeTv.setText(mGuider.mMainExpData+"");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void gainChangedEvent(){
        if (mGuider.mGainChangedDevice.equals("main")){
            updateGainCache(mGuider.mGainChangedValue);
        }
    }
    public void expChangedEvent(){
        if (mGuider.mGainChangedDevice.equals("main")){
            updateExpView();
        }
    }

    public void resolutionChangedEvent(){
        if (mGuider.mResChangedDevice.equals("main")) {
            binding.selectResolution.setSelection(mGuider.mResChangedValue);
        }
    }

    public void binningChangedEvent(){
        if (mGuider.mBinningChangedDevice.equals("main")){
            int bin = mGuider.mBinningChangedValue;
            int index = mGuider.mMainBinningDataList.indexOf(bin+"x"+bin);
            if (index != -1) {
                binding.selectBining.setSelection(index);
            }
        }
    }

    public void targetTempChangedEvent(){
        if (mGuider.mTargetTempChangedDevice.equals("main")){
            binding.targetTempEt.getText().clear();
            binding.targetTempEt.setText(Double.parseDouble(String.format("%.2f",mGuider.mTargetTempChangedValue))+"");
        }
    }

    public void focalLengthChangedEvent(){
        binding.focuseDegreeTv.getText().clear();
        binding.focuseDegreeTv.setText(mGuider.mMainFocalLength+"");
    }

    public void coolingChangedEvent(){
        if (mGuider.mMainCooling.equals("open")) {
            binding.coolingSwitch.setChecked(true);
        }else if (mGuider.mMainCooling.equals("close")){
            binding.coolingSwitch.setChecked(false);
        }
    }

    public void fanChangedEvent(){
        binding.fanSwitch.setChecked(mGuider.mMainFan);
    }

    public void gainModeChangedEvent(){
        updateGainMode(mGuider.mMainGainMode);
    }

    public void parameterLoadSuccess(){
        loadMainCameraDeviceView();
        if (mGuider.mMainIsDeviceConnected) {
            stopTimer();
            binding.mainCameraConnectBtn.setChecked(true);
           // setMainCameraViewStatus(mGuider.isNetworkOnLine,true);
        } else {
            binding.mainCameraConnectBtn.setChecked(false);
            setMainCameraViewStatus(mGuider.mIsNetworkOnLine,false);
        }
    }

    public void lowNoiseChangedEvent(){
        binding.lowNoiseSwitch.setChecked(mGuider.mMainLowNoiseMode);
    }

    public void setMainCameraViewStatus(Boolean isNetworkOnline,Boolean connected){
        if (isNetworkOnline) {
            binding.selectMainCamera.setNorEnabled(!connected);
            if (mMainCurrentDevice.equals("None")){
                binding.mainCameraConnectBtn.setEnabled(false);
            }else {
                binding.mainCameraConnectBtn.setEnabled(true);
            }
        }else {
            binding.mainCameraConnectBtn.setEnabled(false);
            binding.selectMainCamera.setNorEnabled(false);
        }
        binding.mainCameraTv.setEnabled(isNetworkOnline);
        binding.selectResolution.setNorEnabled(connected);
        binding.selectBining.setNorEnabled(connected);
        binding.gainSeekbar.setEnabled(connected);
        binding.expSeekbar.setMinEnabled(connected);
        binding.expTv.setEnabled(connected);
        binding.expDegreeTv.setEnabled(connected);
        binding.focuseDegreeTv.setEnabled(connected);
        binding.hcgBtn.setEnabled(connected);
        binding.lcgBtn.setEnabled(connected);
        binding.hdrBtn.setEnabled(connected);
        binding.lowNoiseSwitch.setEnabled(connected);
        binding.coolingSwitch.setEnabled(connected);
        binding.fanSwitch.setEnabled(connected);
        binding.targetTempEt.setEnabled(connected);
     //   binding.heatingSeekbar.setEnabled(connected);
        binding.mainCameraConnectBtn.setChecked(connected);
        nameViewEnable(connected);
        loadMainCameraPropertyView();
    }

    private void nameViewEnable(boolean enable){
        binding.expUnitTv.setEnabled(enable);
        binding.resolutionTv.setEnabled(enable);
        binding.biningTv.setEnabled(enable);
        binding.focuseTv.setEnabled(enable);
        binding.focuseDegreeTv.setEnabled(enable);
        binding.focuseUnitTv.setEnabled(enable);
        binding.gainDegreeTv.setEnabled(enable);
        binding.gainTv.setEnabled(enable);
        binding.conversionGainTv.setEnabled(enable);
        binding.lowNoiseTv.setEnabled(enable);
        binding.coolingTv.setEnabled(enable);
        binding.fanTv.setEnabled(enable);
        binding.targetTempTv.setEnabled(enable);
        binding.targetTempEt.setEnabled(enable);
        binding.mainMmTv.setEnabled(enable);
        binding.targetTempDegreeTv.setEnabled(enable);
    }

    private void resetMainCameraView(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.gainSeekbar.setProgress(0);
                binding.expSeekbar.setProgress(0);
                mResolutionAdapter.clear();
                mResolutionAdapter.notifyDataSetChanged();
                mBinningAdapter.clear();
                mBinningAdapter.notifyDataSetChanged();
                binding.targetTempEt.getText().clear();
                binding.focuseDegreeTv.getText().clear();
                binding.gainDegreeTv.setText("");
                binding.expDegreeTv.setText("");
            }
        });
    }
    private void loadMainCameraViewData(){
        try {
            setCoolingStatus();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResolutionAdapter.clear();
                    mResolutionAdapter.addAll(mGuider.mMainResDataList);
                    mResolutionAdapter.notifyDataSetChanged();
                    if (mGuider.mMainCurrentResolution != -1){
                        binding.selectResolution.setSelection(mGuider.mMainCurrentResolution);
                    }
                    mBinningAdapter.clear();
                    mBinningAdapter.addAll(mGuider.mMainBinningDataList);
                    mBinningAdapter.notifyDataSetChanged();
                    if (mGuider.mMainBinning != -1){
                        for (int i = 0;i < mGuider.mMainBinningDataList.size();i++){
                            if (parseBinNumber(mGuider.mMainBinningDataList.get(i)) == mGuider.mMainBinning){
                                binding.selectBining.setSelection(i);
                            }
                        }
                    }
                    int gainMode = mGuider.mMainGainMode;
                    updateGainMode(gainMode);
                    updateGainCache(mGuider.mMainGain);
                    updateExpView();
                    binding.lowNoiseSwitch.setChecked(mGuider.mMainLowNoiseMode);

                    binding.focuseDegreeTv.setText("" + mGuider.mMainFocalLength);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void responseFanSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.fanSwitch.setChecked(mGuider.mMainFan);
            }
        });
    }
    private void responseCoolingSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mMainCooling.equals("open")) {
                    binding.coolingSwitch.setChecked(true);
                }else if (mGuider.mMainCooling.equals("close")){
                    binding.coolingSwitch.setChecked(false);
                }
            }
        });
    }

    private void responseLowNoiseSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.lowNoiseSwitch.setChecked(mGuider.mMainLowNoiseMode);
            }
        });
    }

    private void updateGainMode(int gainMode){
        if (gainMode == 0){
            binding.lcgBtn.setChecked(true);
            dealTypeConflict(binding.lcgBtn);
        }
        else if (gainMode == 1){
            binding.hcgBtn.setChecked(true);
            dealTypeConflict(binding.hcgBtn);
        }
        else if (gainMode == 2){
            binding.hdrBtn.setChecked(true);
            dealTypeConflict(binding.hdrBtn);
        }
    }
    private void setCoolingStatus(){
        String cooling = mGuider.mMainCooling;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isCooling = true;
                if (cooling.equals("open")) {
                    binding.coolingSwitch.setChecked(true);
                }else if (cooling.equals("close")){
                    binding.coolingSwitch.setChecked(false);
                }else {
                    isCooling = false;
                    binding.coolingSwitch.setChecked(false);
                }

                if (isCooling) {
                    binding.targetTempEt.getText().clear();
                    binding.targetTempEt.setText(Double.parseDouble(String.format("%.2f",mGuider.mMainTargetTemp))+"");
                }else {
                }
                binding.fanSwitch.setChecked(mGuider.mMainFan);
                binding.targetTempEt.setEnabled(isCooling);
                binding.coolingTv.setEnabled(isCooling);
                binding.fanTv.setEnabled(isCooling);
                binding.targetTempTv.setEnabled(isCooling);
                binding.mainMmTv.setEnabled(isCooling);
                binding.coolingSwitch.setEnabled(isCooling);
                binding.fanSwitch.setEnabled(isCooling);
            }
        });
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mLastActionTime > 5000) {
                mIsConnectTimeOut = true;
                if (!mDeviceChangeStatus){
                    stopTimer();
                }
            }else if (!mGuider.mMainIsDeviceConnected){
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


    private void expSliderRange(long srcMin, long srcMax)
    {
        mExpTimeMin = srcMin;
        mExpTimeMax = srcMax;
        mExpSliderMin = (int) (pow(1.0 * srcMin * srcMax * srcMax, 1.0 / 3.0) / 1000.0 + 1.0);
        mExpSliderMax = (int) (srcMax / 1000.0);
    }

    private int real2ExpSlider(long expTime)
    {
        double d = expTime / (mExpSliderMax * 1000.0);
        d = pow(d, 1.0 / 3.0);
        int r = (int) (mExpSliderMax * d);
        if (r < mExpSliderMin)
            r = mExpSliderMin;
        if (r > mExpSliderMax)
            r = mExpSliderMax;
        return r;
    }

    private long expSlider2Real(int sliderExpTime)
    {
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


}
