package com.glide.chujian.fragment;

import static com.glide.chujian.util.DeviceUtil.*;
import static com.glide.chujian.util.GuideUtil.parseBinNumber;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glide.chujian.R;
import com.glide.chujian.databinding.FragmentGuideCameraBinding;
import com.glide.chujian.model.GuiderModel;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.LogUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpSettingGuideHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GuideCameraFragment extends BaseFragment<FragmentGuideCameraBinding> {
    private ArrayAdapter<String> mGuidingCameraAdapter;
    private ArrayAdapter<String> mBiningAdapter;
    private ArrayList<ToggleButton> mModeToggleBtnList = new ArrayList<ToggleButton>();
    private String mGuideCurrentDevice = "";
    private TpSettingGuideHandler mHandler = new TpSettingGuideHandler(this);
    ArrayList<GuiderModel.AllDevice> mCCDSAllDevices = new ArrayList<GuiderModel.AllDevice>();
    ArrayList<String> mCCDSAllDevicesList = new ArrayList<String>();
    ArrayList<String> mBinningList = new ArrayList<String>();

    private boolean mDeviceChangeStatus = false;//相机将要切换到的状态，用于定时弹出UI的判断
    private Timer mTimer;
    private MyTimerTask mTimerTask;
    private long mLastActionTime;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_guide_camera;
    }

    @Override
    public void onDeviceSelectChanged() {
        String device = mGuider.mSelectedDevices.get(mGuider.SELECTED_DEVICE);
        if (device == null) return;
        switch (device) {
            case "guide": {
                loadGuideCameraDeviceView();
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
        loadGuideCameraDeviceView();
    }

    private void init() {
        initView();
        initData();
        initEvent();
        //setGuideCameraViewStatus(mGuider.mIsNetworkOnLine,false);
    }

    private void initView() {
        mGuidingCameraAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mGuidingCameraAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mBiningAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mBiningAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        binding.selectGuideCamera.setAdapter(mGuidingCameraAdapter);

        binding.selectBining.setAdapter(mBiningAdapter);
    }

    private void loadGuideCameraDeviceView() {
        mCCDSAllDevices.clear();
        mCCDSAllDevicesList.clear();
        mCCDSAllDevices.addAll(mGuider.getAllDevices("CCDS"));
        mCCDSAllDevicesList.addAll(parseDevices(mCCDSAllDevices));

        if (mCCDSAllDevicesList.contains("None")) {
            mCCDSAllDevicesList.remove("None");
            mCCDSAllDevicesList.add(0, "None");
        }

        ArrayList<String> dataList = new ArrayList<String>();
        dataList.addAll(mCCDSAllDevicesList);
        String item = mGuider.mSelectedDevices.get("main");
        if (item == null) {
        } else if (item.equals("None")) {
        } else {
            if (dataList.contains(item)) {
                dataList.remove(item);
            }
        }
        mGuidingCameraAdapter.clear();
        mGuidingCameraAdapter.notifyDataSetChanged();
        mGuidingCameraAdapter.addAll(dataList);
        mGuidingCameraAdapter.notifyDataSetChanged();

        String currentDevice = mGuider.mSelectedDevices.get("guide");

        if (currentDevice == null) {

        } else if (currentDevice.equals("None")) {
            String guide = SharedPreferencesUtil.getInstance().getString(Constant.SP_GUIDE_CAMERA_ID, "");
            if (!guide.equals("None") && !guide.equals("") && dataList.contains(guide) && !isLabel(mCCDSAllDevices, guide)) {
                mGuider.mSelectedDevices.put("guide", guide);
                currentDevice = guide;
                int index = dataList.indexOf(currentDevice);
                if (index != -1) {
                    mGuideCurrentDevice = currentDevice;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("guide", mGuideCurrentDevice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }
        mGuideCurrentDevice = currentDevice;
        int index = dataList.indexOf(mGuideCurrentDevice);
        if (index != -1) {
            binding.selectGuideCamera.setSelection(index);
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mGuideIsDeviceConnected) {
                    setGuideCameraViewStatus(mGuider.mIsNetworkOnLine, true);
                } else {
                    setGuideCameraViewStatus(mGuider.mIsNetworkOnLine, false);
                }
            }
        });
    }

    private void loadGuideCameraPropertyView() {
        if (mGuider.mGuideIsDeviceConnected) {
            loadGuideCameraViewData();
        } else {
            resetGuideCameraView();
        }
    }

    private void resetGuideCameraView() {
        mBiningAdapter.clear();
        mBiningAdapter.notifyDataSetChanged();
        binding.focuseDegreeTv.getText().clear();
        binding.gainSeekbar.setProgress(0);
        binding.gainDegreeTv.setText("");
        binding.maxRaDegreeTv.getText().clear();
        binding.maxDecDegreeTv.getText().clear();
        binding.ditherScaleEt.getText().clear();
    }

    private void loadGuideCameraViewData() {
        try {
            mBinningList.clear();
            for (int i = 1; i <= mGuider.mGuideBinMax; i++) {
                mBinningList.add(i + "x" + i);
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBiningAdapter.clear();
                    mBiningAdapter.addAll(mBinningList);

                    if (mGuider.mGuideBinning != -1) {
                        for (int i = 0; i < mBinningList.size(); i++) {
                            if (parseBinNumber(mBinningList.get(i)) == mGuider.mGuideBinning) {
                                binding.selectBining.setSelection(i);
                            }
                        }
                    }

                    binding.guideCameraConnectBtn.setChecked(mGuider.mGuideIsDeviceConnected);

                    updateGainCache(mGuider.mGuideGain);

                    binding.maxRaDegreeTv.getText().clear();
                    if (mGuider.mGuideMaxRa == -1) {
                        binding.maxRaDegreeTv.setText("0");
                    } else {
                        binding.maxRaDegreeTv.setText(String.valueOf(mGuider.mGuideMaxRa));
                    }

                    if (binding.maxRaDegreeTv.getText().toString().equals("")) {
                        binding.maxRaDegreeTv.setText("0");
                    }

                    binding.maxDecDegreeTv.getText().clear();
                    if (mGuider.mGuideMaxDec == -1) {
                        binding.maxDecDegreeTv.setText("0");
                    } else {
                        binding.maxDecDegreeTv.setText(String.valueOf(mGuider.mGuideMaxDec));
                    }

                    if (binding.maxDecDegreeTv.getText().toString().equals("")) {
                        binding.maxDecDegreeTv.setText("0");
                    }
                    binding.ditherScaleEt.getText().clear();
                    binding.ditherScaleEt.setText(String.format("%.1f", mGuider.mGuideDitherScale));

                    updateDitherMode(mGuider.mGuideDitherMode);

                    binding.focuseDegreeTv.setText("" + mGuider.mGuideFocalLength);
                    binding.raOnlySwitch.setChecked(mGuider.mGuideRaOnly);
                }
            });
        } catch (Exception e) {

        }
    }

    private void responseDitherModeSwitch() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDitherMode(mGuider.mGuideDitherMode);
            }
        });
    }

    private void responseRaOnlySwitch() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.raOnlySwitch.setChecked(mGuider.mGuideRaOnly);
            }
        });
    }

    private void updateDitherMode(int ditherMode) {
        if (ditherMode == 0) {
            binding.ramdomTbtn.setChecked(true);
            dealTypeConflict(binding.ramdomTbtn);
        } else {
            binding.spiralTbtn.setChecked(true);
            dealTypeConflict(binding.spiralTbtn);
        }
    }

    private void initData() {
        mModeToggleBtnList.add(binding.ramdomTbtn);
        mModeToggleBtnList.add(binding.spiralTbtn);

        loadGuideCameraDeviceView();
    }


    private void initEvent() {
        mActivity.showSoftInput(binding.focuseDegreeTv, true, false);
        mActivity.showSoftInput(binding.maxDecDegreeTv, true, false);
        mActivity.showSoftInput(binding.maxRaDegreeTv, true, false);
        mActivity.showSoftInput(binding.ditherScaleEt, true, true);
        binding.selectGuideCamera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (mGuider.mGuideIsDeviceConnected) return;
                String item = mGuidingCameraAdapter.getItem(position);
                if (item.equals("None")) {
                    binding.guideCameraConnectBtn.setEnabled(false);
                } else {
                    binding.guideCameraConnectBtn.setEnabled(true);
                }

                if (Objects.equals(mGuider.mSelectedDevices.get("guide"), item)) return;
                LogUtil.writeLogtoFile("binding.selectGuideCamera the item is" + item);
                mGuideCurrentDevice = item;
                if (isLabel(mCCDSAllDevices, item)) {
                    if (isLabelConfigured(mCCDSAllDevices, item)) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.selectDevice("guide", item);
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
                                try {
                                    mGuider.startDriver(item);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mGuider.mSelectedDevices.put(mGuider.START_DRIVER, "guide");
                            }
                        }.start();
                    }
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("guide", item);
                            } catch (Exception e) {
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
        binding.selectBining.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            int binning = parseBinNumber(mBiningAdapter.getItem(i));
                            if (binning != mGuider.mGuideBinning) {
                                mGuider.setBinning("guide", binning);
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
        binding.guideCameraConnectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mLoadingDialog.show();
                    new Thread() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void run() {
                            super.run();
                            try {
                                boolean isGuideCameraConnected = mGuider.mGuideIsDeviceConnected;
                                waitDeviceStatusChange(!isGuideCameraConnected);
                                if (isGuideCameraConnected) {
                                    mGuider.stopCapture("guide");
                                } else {
                                }
                                mIsConnectTimeOut = false;
                                boolean result = mGuider.setDeviceConnected("guide", !isGuideCameraConnected);
                                if (!result) {
                                    stopTimer();
                                    if (!mIsConnectTimeOut) {
                                        ToastUtil.showToast(getResources().getString(R.string.device_not_connect), false);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                return true;
            }
        });

        binding.gainSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.gainDegreeTv.setText(String.valueOf(i));
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
                            mGuider.setGain("guide", seekBar.getProgress());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
//todo
        binding.useSettingFromLastTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        //todo
        binding.ditheringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //mGuider.dither();
                    }
                }.start();
            }
        });

        binding.ramdomTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.ramdomTbtn);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setDitherMode(0)) {
                            responseDitherModeSwitch();
                        }
                    }
                }.start();
            }
        });
        binding.spiralTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealTypeConflict(binding.spiralTbtn);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setDitherMode(1)) {
                            responseDitherModeSwitch();
                        }
                    }
                }.start();
            }
        });

        binding.raOnlySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (!mGuider.setDitherRaOnly(binding.raOnlySwitch.isChecked())) {
                            responseRaOnlySwitch();
                        }
                    }
                }.start();
            }
        });

        binding.ditherScaleEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String ditherScaleInput = binding.ditherScaleEt.getText().toString();
                    if (ditherScaleInput.trim().equals("")){
                        binding.ditherScaleEt.getText().clear();
                        binding.ditherScaleEt.setText(String.format("%.1f", Double.valueOf(mGuider.mGuideDitherScale)) + "");
                        return;
                    }
                    String ditherScale = String.format("%.1f", Double.valueOf(ditherScaleInput));
                    if (!ditherScale.contains(".")) {
                        binding.ditherScaleEt.getText().clear();
                        binding.ditherScaleEt.setText(ditherScale + ".0");
                    } else {
                        binding.ditherScaleEt.getText().clear();
                        binding.ditherScaleEt.setText(ditherScale + "");
                    }
/*                    if (!CheckParamUtil.isRangeLegal(mActivity,ditherScale,0.1,100000.0,1.0,"")){
                        binding.ditherScaleEt.getText().clear();
                        binding.ditherScaleEt.setText(String.format("%.1f", mGuider.mGuideDitherScale));
                        return;
                    }*/
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (!ditherScale.equals("")) {
                                mGuider.setDitherScale(Float.valueOf(ditherScale));
                            }
                        }
                    }.start();
                }
            }
        });
        binding.focuseDegreeTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String input = binding.focuseDegreeTv.getText().toString();

                    if (input.equals("")) {
                        binding.focuseDegreeTv.getText().clear();
                        binding.focuseDegreeTv.setText(mGuider.mGuideFocalLength + "");
                        return;
                    }
/*                        if (!CheckParamUtil.isRangeLegal(mActivity,input,0.0,100000.0,1.0,"")){
                            binding.focuseDegreeTv.getText().clear();
                            binding.focuseDegreeTv.setText(mGuider.mGuideFocalLength+"");
                            return;
                        }*/
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                int guideLength = 0;
                                if (!input.trim().equals("")) {
                                    guideLength = Integer.parseInt(input);
                                }
                                mGuider.setFocalLength("guide", guideLength);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

        binding.maxRaDegreeTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String maxRa = binding.maxRaDegreeTv.getText().toString();
                    try {
                /*        if (!checkParamUtil.isRangeLegal(mActivity,maxRa,0.0,10000.0)){
                            binding.maxRaDegreeTv.getText().clear();
                            return;
                        }*/
                        if (!maxRa.trim().equals("")) {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    mGuider.setScopeRaMax(Integer.parseInt(maxRa));
                                }
                            }.start();
                        }else {
                            binding.maxRaDegreeTv.getText().clear();
                            if (mGuider.mGuideMaxRa == -1) {
                                binding.maxRaDegreeTv.setText("0");
                            } else {
                                binding.maxRaDegreeTv.setText(String.valueOf(mGuider.mGuideMaxRa));
                            }

                            if (binding.maxRaDegreeTv.getText().toString().equals("")) {
                                binding.maxRaDegreeTv.setText("0");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.maxDecDegreeTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String maxDec = binding.maxDecDegreeTv.getText().toString();
                    try {
                  /*      if (!checkParamUtil.isRangeLegal(mActivity,maxDec,0.0,10000.0)){
                            return;
                        }*/
                        if (!maxDec.trim().equals("")) {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    mGuider.setScopeDecMax(Integer.parseInt(maxDec));
                                }
                            }.start();
                        }else {
                            binding.maxDecDegreeTv.getText().clear();
                            if (mGuider.mGuideMaxDec == -1) {
                                binding.maxDecDegreeTv.setText("0");
                            } else {
                                binding.maxDecDegreeTv.setText(String.valueOf(mGuider.mGuideMaxDec));
                            }

                            if (binding.maxDecDegreeTv.getText().toString().equals("")) {
                                binding.maxDecDegreeTv.setText("0");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void dealTypeConflict(ToggleButton toggleButton) {
        toggleButton.setChecked(true);
        for (ToggleButton it : mModeToggleBtnList) {
            if (toggleButton != it) {
                it.setChecked(false);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mGuider.unregisterHandlerListener(mHandler);
            mLoadingDialog.dismiss();
        } else {
            mGuider.registerHandlerListener(mHandler);
            loadGuideCameraDeviceView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGuider.unregisterHandlerListener(mHandler);
    }

    public void updateDevices() {
/*        if (Objects.equals(mGuider.mSelectedDevices.get(mGuider.device_label), "guide")){
            loadGuideCameraDeviceView();
        }*/
        loadGuideCameraDeviceView();
    }


    public void deviceConnectEvent() {
        if (!mGuider.mCurrentConnectedDevice.equals("guide")) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mGuideIsDeviceConnected) {
/*                    binding.guideCameraConnectBtn.setChecked(true);
                    setGuideCameraViewStatus(mGuider.mIsNetworkOnLine,true);*/
                } else {
                    binding.guideCameraConnectBtn.setChecked(false);
                    setGuideCameraViewStatus(mGuider.mIsNetworkOnLine, false);
                }
            }
        });
    }

    private void updateGainCache(int gain) {
        if (gain == -1) return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.gainSeekbar.setProgress(gain);
                binding.gainDegreeTv.setText(String.valueOf(gain));
            }
        });
    }

    public void gainChangedEvent() {
        if (mGuider.mGainChangedDevice.equals("guide")) {
            updateGainCache(mGuider.mGainChangedValue);
        }
    }

    public void binningChangedEvent() {
        if (mGuider.mBinningChangedDevice.equals("guide")) {
            int bin = mGuider.mBinningChangedValue;
            int index = mBinningList.indexOf(bin + "x" + bin);
            if (index != -1) {
                binding.selectBining.setSelection(index);
            }
        }
    }

    public void parameterLoadSuccess() {
        loadGuideCameraDeviceView();
        if (mGuider.mGuideIsDeviceConnected) {
            stopTimer();
            binding.guideCameraConnectBtn.setChecked(true);
            // setGuideCameraViewStatus(mGuider.isNetworkOnLine,true);
        } else {
            binding.guideCameraConnectBtn.setChecked(false);
            setGuideCameraViewStatus(mGuider.mIsNetworkOnLine, false);
        }
    }

    public void focalLengthChangedEvent() {
        binding.focuseDegreeTv.getText().clear();
        binding.focuseDegreeTv.setText(mGuider.mGuideFocalLength + "");
    }

    public void mountRaMaxChangedEvent() {
        binding.maxRaDegreeTv.getText().clear();
        binding.maxRaDegreeTv.setText(mGuider.mGuideMaxRa + "");
    }

    public void mountDecMaxChangedEvent() {
        binding.maxDecDegreeTv.getText().clear();
        binding.maxDecDegreeTv.setText(mGuider.mGuideMaxDec + "");
    }

    public void ditherScaleChangedEvent() {
        binding.ditherScaleEt.getText().clear();
        binding.ditherScaleEt.setText(String.format("%.1f", mGuider.mGuideDitherScale));
    }

    public void ditherModeChangedEvent() {
        updateDitherMode(mGuider.mGuideDitherMode);
    }

    public void ditherRaOnlyChangedEvent() {
        binding.raOnlySwitch.setChecked(mGuider.mGuideRaOnly);
    }

    public void setGuideCameraViewStatus(Boolean isNetworkOnline, Boolean connected) {
        if (isNetworkOnline) {
            if (mGuideCurrentDevice.equals("None")) {
                binding.guideCameraConnectBtn.setEnabled(false);
            } else {
                binding.guideCameraConnectBtn.setEnabled(true);
            }
            binding.selectGuideCamera.setNorEnabled(!connected);
        } else {
            binding.selectGuideCamera.setNorEnabled(false);
            binding.guideCameraConnectBtn.setEnabled(false);
        }
        binding.guideCameraTv.setEnabled(isNetworkOnline);
        binding.selectBining.setNorEnabled(connected);
        binding.focuseDegreeTv.setEnabled(connected);
        binding.gainSeekbar.setEnabled(connected);
        binding.maxRaDegreeTv.setEnabled(connected);
        binding.maxDecDegreeTv.setEnabled(connected);
        // binding.useSettingFromLastTimeSwitch.setEnabled(connected);
        binding.ditheringSwitch.setEnabled(connected);
        binding.ditherScaleEt.setEnabled(connected);
        binding.ramdomTbtn.setEnabled(connected);
        binding.spiralTbtn.setEnabled(connected);
        binding.raOnlySwitch.setEnabled(connected);
        binding.guideCameraConnectBtn.setChecked(connected);
        nameViewEnable(connected);
        loadGuideCameraPropertyView();
    }

    private void nameViewEnable(boolean enable) {
        binding.biningTv.setEnabled(enable);
        binding.focuseTv.setEnabled(enable);
        binding.focuseDegreeTv.setEnabled(enable);
        binding.focuseUnitTv.setEnabled(enable);
        binding.gainDegreeTv.setEnabled(enable);
        binding.gainTv.setEnabled(enable);
        binding.maxRaDegreeTv.setEnabled(enable);
        binding.maxRaTv.setEnabled(enable);
        binding.maxRaUnitTv.setEnabled(enable);
        binding.maxDecDegreeTv.setEnabled(enable);
        binding.maxDecTv.setEnabled(enable);
        binding.maxDecUnitTv.setEnabled(enable);
        //  binding.useSettingFromLastTimeTv.setEnabled(enable);
        binding.ditheringTv.setEnabled(enable);
        binding.ditherScaleEt.setEnabled(enable);
        binding.ditherScaleTv.setEnabled(enable);
        binding.modeTv.setEnabled(enable);
        binding.raonlyTv.setEnabled(enable);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mLastActionTime > 5000) {
                mIsConnectTimeOut = true;
                if (!mDeviceChangeStatus) {
                    stopTimer();
                }
            } else if (!mGuider.mGuideIsDeviceConnected) {
                if (!mDeviceChangeStatus) {
                    stopTimer();
                }
            }
        }
    }

    private void startTimer() {
        if (mTimer != null) {
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
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

}
