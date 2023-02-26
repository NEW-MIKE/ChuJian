package com.glide.chujian.fragment;

import static com.glide.chujian.util.DeviceUtil.isLabel;
import static com.glide.chujian.util.DeviceUtil.isLabelConfigured;
import static com.glide.chujian.util.DeviceUtil.parseDevices;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.databinding.FragmentFocuserBinding;
import com.glide.chujian.model.GuiderModel;
import com.glide.chujian.util.CheckParamUtil;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpSettingFocuserHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FocuserFragment extends BaseFragment<FragmentFocuserBinding>  {
    private String TAG = "FocuserFragment";
    private ArrayAdapter<String> mFocuserAdapter;

    private boolean mDeviceChangeStatus = false;//相机将要切换到的状态，用于定时弹出UI的判断
    private Timer mTimer,mTemperatureTimer;
    private MyTimerTask mTimerTask;
    private FocuserTemperatureTask mTemperatureTask;
    private long mLastActionTime;
    private String mFocuserCurrentDevice = "";
    private boolean mFocuserArriveTargetPosition = true;
    ArrayList<GuiderModel.AllDevice> mCCDSAllDevices = new ArrayList<GuiderModel.AllDevice>();
    ArrayList<String> mCCDSAllDevicesList = new ArrayList<String>();
    TpSettingFocuserHandler mHandler = new TpSettingFocuserHandler(this);
    @Override
    public int getLayoutId() {
        return R.layout.fragment_focuser;
    }

    @Override
    public void waitDeviceStatusChange(Boolean status) {
        mDeviceChangeStatus = status;
        startTimer();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        initViewData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            stopFocuserTask();
            mGuider.unregisterHandlerListener(mHandler);
            mLoadingDialog.dismiss();
        }else{
            mGuider.registerHandlerListener(mHandler);
            mFocuserArriveTargetPosition = mGuider.mIsFocuserArriveTargetPosition;
            updateFocuserUi();
            if (mGuider.mFocuserIsDeviceConnected){
                startFocuserTask();
            }
        }
    }

    public void updateDevices(){
        updateFocuserUi();
    }

    public void deviceConnectEvent(){
        if (mGuider.mCurrentConnectedDevice.equals("focuser")) {
            if (!mGuider.mFocuserIsDeviceConnected){
                stopFocuserTask();
                updateFocuserViewStatus(mGuider.mIsNetworkOnLine, mGuider.mFocuserIsDeviceConnected);
            }else {
            }
        }
    }

    public void reversedEvent(){
        Boolean value = mGuider.mFocuserReversed;
        if (value != null){
            binding.reservedSwitch.setChecked(value);
        }
    }

    public void beepEvent(){
        Boolean value = mGuider.mFocuserBeep;
        if (value != null){
            binding.beepSwitch.setChecked(value);
        }
    }

    public void targetPositionEvent(){
        mFocuserArriveTargetPosition = mGuider.mIsFocuserArriveTargetPosition;
        checkGotoViewStatusByTargetPosition(mFocuserArriveTargetPosition);
        if (mGuider.mFocuserTargetPosition != null){
            binding.targetPositionEdit.getText().clear();
            binding.targetPositionEdit.setText(mGuider.mFocuserTargetPosition+"");
        }
    }
    public void currentPositionEvent(){
        mFocuserArriveTargetPosition = mGuider.mIsFocuserArriveTargetPosition;
        checkGotoViewStatusByTargetPosition(mFocuserArriveTargetPosition);
        if (mGuider.mFocuserPosition != null){
            if (mGuider.mIsFocuserLoaded) {
                binding.currentPositionValueTv.setText(mGuider.mFocuserPosition + "");
            }
        }
    }

    public void maxStepEvent(){
        if (mGuider.mFocuserPositionMax != null){
            binding.maxStepEdit.getText().clear();
            binding.maxStepEdit.setText(mGuider.mFocuserPositionMax+"");
        }
    }

    public void coarseStepEvent(){
        if (mGuider.mFocuserCoarseStep != null){
            binding.coarseStepEdit.getText().clear();
            binding.coarseStepEdit.setText(mGuider.mFocuserCoarseStep+"");
        }
    }

    public void fineStepEvent(){
        if (mGuider.mFocuserFineStep != null){
            binding.fineStepEdit.getText().clear();
            binding.fineStepEdit.setText(mGuider.mFocuserFineStep+"");
        }
    }

    private void updateFocuserUi(){
        updateFocuserDeviceUi();
        updateFocuserDeviceParameterUi();
    }

    private void updateFocuserDeviceUi(){
        mCCDSAllDevices.clear();
        mCCDSAllDevicesList.clear();
        mCCDSAllDevices.addAll(mGuider.getAllDevices("Focusers"));
        mCCDSAllDevicesList.addAll(parseDevices(mCCDSAllDevices));

        if (mCCDSAllDevicesList.contains("None")){
            mCCDSAllDevicesList.remove("None");
            mCCDSAllDevicesList.add(0,"None");
        }

        ArrayList<String> dataList =new ArrayList<String>();
        dataList.addAll(mCCDSAllDevicesList);

        mFocuserAdapter.clear();
        mFocuserAdapter.notifyDataSetChanged();
        mFocuserAdapter.addAll(dataList);
        mFocuserAdapter.notifyDataSetChanged();

        String currentDevice =  mGuider.mSelectedDevices.get("focuser");

        if (currentDevice == null){}
        else if (currentDevice.equals("None")){
            String focuser = SharedPreferencesUtil.getInstance().getString(Constant.SP_FOCUSER_ID,"");
            if (!focuser.equals("None")  && !focuser.equals("")  && dataList.contains(focuser) && !isLabel(mCCDSAllDevices, focuser)){
                mGuider.mSelectedDevices.put("focuser",focuser);
                currentDevice = focuser;
                int index = dataList.indexOf(currentDevice);
                if (index != -1){
                    mFocuserCurrentDevice = currentDevice;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("focuser", mFocuserCurrentDevice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }

        mFocuserCurrentDevice = currentDevice;
        int index = dataList.indexOf(currentDevice);
        if (index != -1) {
            binding.selectFocuser.setSelection(index);
        }
    }

    private void updateFocuserDeviceParameterUi(){
        updateFocuserViewStatus(mGuider.mIsNetworkOnLine,mGuider.mFocuserIsDeviceConnected);
    }

    private void updateFocuserViewStatus(Boolean isNetworkOnline, Boolean connected){
        updateFocuserWorkState(isNetworkOnline,connected);
        updateFocuserViewDataStatus(connected);
    }

    private void updateFocuserWorkState(Boolean isNetworkOnline, Boolean connected){
        binding.focuserTv.setEnabled(isNetworkOnline);
        if (isNetworkOnline) {
            if (mFocuserCurrentDevice.equals("None")){
                binding.focuserConnectBtn.setEnabled(false);
            }else {
                binding.focuserConnectBtn.setEnabled(true);
            }
            binding.selectFocuser.setNorEnabled(!connected);
        }else {
            binding.selectFocuser.setNorEnabled(false);
            binding.focuserConnectBtn.setEnabled(false);
        }
        binding.focuserConnectBtn.setChecked(connected);

        binding.reservedTv.setEnabled(connected);
        binding.reservedSwitch.setEnabled(connected);

        binding.temperatureTv.setEnabled(connected);

        binding.currentPositionTv.setEnabled(connected);

        binding.targetPositionTv.setEnabled(connected);

        binding.maxStepTv.setEnabled(connected);

        binding.coarseStepTv.setEnabled(connected);

        binding.fineStepTv.setEnabled(connected);

        binding.beepTv.setEnabled(connected);
        binding.beepSwitch.setEnabled(connected);
        checkFocuserIsMoving(mGuider.mFocuserIsMoving);
    }

    private void updateFocuserViewDataStatus(boolean isDeviceConnected){
        if (isDeviceConnected){
            Boolean value = mGuider.mFocuserReversed;
            if (value != null){
                binding.reservedSwitch.setChecked(value);
            }

            if (mGuider.mFocuserTemperature == null){
                binding.temperatureValueTv.setText("");
            }else {
                binding.temperatureValueTv.setText(mGuider.mFocuserTemperature+"℃");
            }

            if (mGuider.mFocuserPosition == null){
                binding.currentPositionValueTv.setText("");
                binding.targetPositionEdit.getText().clear();
            }else {
                binding.currentPositionValueTv.setText(mGuider.mFocuserPosition+"");
                binding.targetPositionEdit.getText().clear();
                binding.targetPositionEdit.setText(mGuider.mFocuserTargetPosition+"");
            }

            if (mGuider.mFocuserPositionMax == null){
                binding.maxStepEdit.getText().clear();
            }else {
                binding.maxStepEdit.getText().clear();
                binding.maxStepEdit.setText(mGuider.mFocuserPositionMax+"");
            }

            if (mGuider.mFocuserCoarseStep == null){
                binding.coarseStepEdit.getText().clear();
            }else {
                binding.coarseStepEdit.getText().clear();
                binding.coarseStepEdit.setText(mGuider.mFocuserCoarseStep +"");
            }
            if (mGuider.mFocuserFineStep == null){
                binding.fineStepEdit.getText().clear();
            }else {
                binding.fineStepEdit.getText().clear();
                binding.fineStepEdit.setText(mGuider.mFocuserFineStep +"");
            }
            value = mGuider.mFocuserBeep;
            if (value != null){
                binding.beepSwitch.setChecked(value);
            }
            checkGotoViewStatusByTargetPosition(mFocuserArriveTargetPosition);
        }else {
            checkGotoViewStatusByTargetPosition(true);
            binding.temperatureValueTv.setText("");
            binding.currentPositionValueTv.setText("");
            binding.targetPositionEdit.getText().clear();
            binding.maxStepEdit.getText().clear();
            binding.coarseStepEdit.getText().clear();
            binding.fineStepEdit.getText().clear();
        }
    }

    private void checkGotoViewStatusByTargetPosition(boolean isArriveTargetPosition){
        if (isArriveTargetPosition){
            binding.targetPositionGotoBtn.setTextOn(App.INSTANCE.getResources().getString(R.string.focuser_goto));
            binding.targetPositionGotoBtn.setTextOff(App.INSTANCE.getResources().getString(R.string.focuser_goto));
            binding.targetPositionGotoBtn.setChecked(false);
        }else {
            binding.targetPositionGotoBtn.setTextOn(App.INSTANCE.getResources().getString(R.string.focuser_stop));
            binding.targetPositionGotoBtn.setTextOff(App.INSTANCE.getResources().getString(R.string.focuser_stop));
            binding.targetPositionGotoBtn.setChecked(true);
        }
        if (!mGuider.mFocuserIsDeviceConnected){
            binding.targetPositionGotoBtn.setChecked(false);
        }
        binding.targetPositionGotoBtn.setEnabled(mGuider.mFocuserIsDeviceConnected);

        checkFocuserIsMoving(mGuider.mFocuserIsMoving);
    }

    private void checkFocuserIsMoving(boolean moving){
        boolean isEditEnabled = true;
        if (mGuider.mFocuserIsDeviceConnected){
            isEditEnabled = !moving;
        }else {
            isEditEnabled = false;
        }
        binding.targetPositionEdit.setEnabled(isEditEnabled);
        binding.maxStepEdit.setEnabled(isEditEnabled);
        binding.coarseStepEdit.setEnabled(isEditEnabled);
        binding.fineStepEdit.setEnabled(isEditEnabled);
    }

    private void initView(){
        mFocuserAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mFocuserAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        binding.selectFocuser.setAdapter(mFocuserAdapter);
    }

    private void initViewData(){
        updateFocuserUi();
    }

    private void initEvent(){
        mActivity.showSoftInput(binding.targetPositionEdit,true,false);
        mActivity.showSoftInput(binding.maxStepEdit,true,false);
        mActivity.showSoftInput(binding.coarseStepEdit,true,false);
        mActivity.showSoftInput(binding.fineStepEdit,true,false);
        binding.selectFocuser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mGuider.mFocuserIsDeviceConnected)return;
                String item = mFocuserAdapter.getItem(position);
                if (item.equals("None")){
                    binding.focuserConnectBtn.setEnabled(false);
                }else {
                    binding.focuserConnectBtn.setEnabled(true);
                }

                if (Objects.equals(mGuider.mSelectedDevices.get("focuser"), item))return;

                mFocuserCurrentDevice = item;
                if (isLabel(mCCDSAllDevices, item)) {
                    if (isLabelConfigured(mCCDSAllDevices, item)) {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.selectDevice("focuser", item);
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
                                mGuider.selectDevice("focuser", item);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.focuserConnectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mLoadingDialog.show();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                boolean isFocuserConnected = mGuider.mFocuserIsDeviceConnected;
                                waitDeviceStatusChange(!isFocuserConnected);

                                mIsConnectTimeOut = false;
                                if (!isFocuserConnected){
                                    mGuider.mIsFocuserLoaded = false;
                                }
                                boolean result = mGuider.setDeviceConnected("focuser",!isFocuserConnected);
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
        binding.reservedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (mGuider.setFocuserReversed(binding.reservedSwitch.isChecked()) == null){
                            responseReservedSwitch();
                        }
                    }
                }.start();
            }
        });
        binding.beepSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (mGuider.setFocuserBeep(binding.beepSwitch.isChecked()) == null){
                            responseBeepSwitch();
                        }
                    }
                }.start();
            }
        });
        binding.targetPositionGotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.targetPositionEdit.getText().toString();
                if (!value.equals("")) {
                    ArrayList<Integer> valueList = mGuider.mFocuserRangePositionList;
                    if (valueList.size() == 0)return;
                    if (!CheckParamUtil.isRangeLegalNoToast(mActivity,value,valueList.get(0).doubleValue(),valueList.get(1).doubleValue(),1.0,"")){
                        checkGotoViewStatusByTargetPosition(mFocuserArriveTargetPosition);
                        return;
                    }
                    int number = Integer.parseInt(value);
                    Integer targetPosition = mGuider.mFocuserTargetPosition;
                    if (targetPosition != null){
                        if (mFocuserArriveTargetPosition){
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    Boolean result = mGuider.setFocuserPosition(number);
                                    if (result == null){
                                        responseGotoBtnStatus();
                                    }else if (!result){
                                        responseGotoBtnStatus();
                                    }
                                }
                            }.start();
                        }else {
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    Boolean result = mGuider.focuserMoveStop();
                                    if (result == null){
                                        responseGotoBtnStatus();
                                    }else if (!result){
                                        responseGotoBtnStatus();
                                    }
                                }
                            }.start();
                        }
                        mFocuserArriveTargetPosition = !mFocuserArriveTargetPosition;
                    }
                }

                mGuider.mFocuserIsMoving = !mFocuserArriveTargetPosition;
                checkGotoViewStatusByTargetPosition(mFocuserArriveTargetPosition);
            }
        });
        binding.maxStepEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String maxStep = binding.maxStepEdit.getText().toString();
                    if (!maxStep.equals("")){
                        ArrayList<Integer> valueList = mGuider.mFocuserRangeMaxList;
                        if (valueList.size() == 0)return;
                        if (!CheckParamUtil.isRangeLegalInt(mActivity,maxStep,valueList.get(0).doubleValue(),valueList.get(1).doubleValue(),1.0,"")){
                            binding.maxStepEdit.getText().clear();
                            binding.maxStepEdit.setText(""+mGuider.mFocuserPositionMax);
                            return;
                        }
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                int step = Integer.parseInt(maxStep);
                                mGuider.setFocuserMax(step);
                            }
                        }.start();
                    }else {
                        binding.maxStepEdit.setText(""+mGuider.mFocuserPositionMax);
                    }
                }
            }
        });
        binding.targetPositionEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String value = binding.targetPositionEdit.getText().toString();
                    if (!value.equals("")){
                        ArrayList<Integer> valueList = mGuider.mFocuserRangePositionList;
                        if (valueList.size() == 0)return;
                        if (!CheckParamUtil.isRangeLegalInt(mActivity,value,valueList.get(0).doubleValue(),valueList.get(1).doubleValue(),1.0,"")){
                            binding.targetPositionEdit.getText().clear();
                            binding.targetPositionEdit.setText(""+mGuider.mFocuserTargetPosition);
                            return;
                        }
                    }else {
                        binding.targetPositionEdit.setText(""+mGuider.mFocuserTargetPosition);
                    }
                }
            }
        });

        binding.coarseStepEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String coarseStep = binding.coarseStepEdit.getText().toString();
                    if (!coarseStep.equals("")){
                        ArrayList<Integer> valueList = mGuider.mFocuserRangeStep;
                        if (valueList.size() == 0)return;
                        if (!CheckParamUtil.isRangeLegalInt(mActivity,coarseStep,valueList.get(0).doubleValue(),valueList.get(1).doubleValue(),1.0,"")){
                            binding.coarseStepEdit.getText().clear();
                            binding.coarseStepEdit.setText(""+mGuider.mFocuserCoarseStep);
                            return;
                        }
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                mGuider.setFocuserStep(true,Integer.parseInt(coarseStep));
                            }
                        }.start();
                    }else {
                        binding.coarseStepEdit.setText(""+mGuider.mFocuserCoarseStep);
                    }
                }
            }
        });

        binding.fineStepEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String value = binding.fineStepEdit.getText().toString();
                    if (!value.equals("")){
                        ArrayList<Integer> valueList = mGuider.mFocuserRangeStep;
                        if (valueList.size() == 0)return;
                        if (!CheckParamUtil.isRangeLegalInt(mActivity,value,valueList.get(0).doubleValue(),valueList.get(1).doubleValue(),1.0,"")){
                            binding.fineStepEdit.getText().clear();
                            binding.fineStepEdit.setText(""+mGuider.mFocuserFineStep);
                            return;
                        }
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                mGuider.setFocuserStep(false,Integer.parseInt(value));
                            }
                        }.start();
                    }else {
                        binding.fineStepEdit.setText(""+mGuider.mFocuserFineStep);
                    }
                }
            }
        });
    }

    @Override
    public void onDeviceSelectChanged() {
        String device = mGuider.mSelectedDevices.get(mGuider.SELECTED_DEVICE);
        if (device == null)return;
        switch (device) {
            case "focuser": {
                updateFocuserDeviceUi();
            }
            break;
        }
    }

    private void responseReservedSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Boolean value = mGuider.mFocuserReversed;
                if (value != null){
                    binding.reservedSwitch.setChecked(value);
                }
            }
        });
    }

    private void responseBeepSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Boolean value = mGuider.mFocuserBeep;
                if (value != null) {
                    binding.beepSwitch.setChecked(value);
                }
            }
        });
    }
    private void responseGotoBtnStatus(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFocuserArriveTargetPosition = true;
                mGuider.mFocuserIsMoving = !mFocuserArriveTargetPosition;
                checkGotoViewStatusByTargetPosition(mGuider.mIsFocuserArriveTargetPosition);
            }
        });
    }
    public void parameterLoadSuccess(){
        stopTimer();
        updateFocuserUi();
        if (mGuider.mFocuserIsDeviceConnected){
            startFocuserTask();
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mLastActionTime > 5000) {
                mIsConnectTimeOut = true;
                if (!mDeviceChangeStatus){
                    stopTimer();
                }
            }
            else if (!mGuider.mFocuserIsDeviceConnected){
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
    private void updateFocuserTemperatureView(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                mGuider.mFocuserTemperature = mGuider.getFocuserTemperature();
            }
        }.start();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mFocuserTemperature == null){
                    binding.temperatureValueTv.setText("");
                }else {
                    binding.temperatureValueTv.setText(mGuider.mFocuserTemperature+"℃");
                }
            }
        });
    }
    private class FocuserTemperatureTask extends TimerTask{
        @Override
        public void run() {
            updateFocuserTemperatureView();
        }
    }
    private void startFocuserTask(){
        stopFocuserTask();
        mTemperatureTimer = new Timer();
        mTemperatureTask = new FocuserTemperatureTask();
        mTemperatureTimer.schedule(mTemperatureTask,0,1000);
    }

    private void stopFocuserTask(){
        if (mTemperatureTask != null){
            mTemperatureTask.cancel();
            mTemperatureTask = null;
        }
        if (mTemperatureTimer != null){
            mTemperatureTimer.cancel();
            mTemperatureTimer = null;
        }
    }
}
