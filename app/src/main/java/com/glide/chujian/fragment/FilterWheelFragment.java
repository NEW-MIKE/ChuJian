package com.glide.chujian.fragment;

import static com.glide.chujian.util.DeviceUtil.*;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.glide.chujian.R;
import com.glide.chujian.adapter.SlotNumberAdapter;
import com.glide.chujian.adapter.SlotNumberAdapterListener;
import com.glide.chujian.adapter.SpacesItemDecoration;
import com.glide.chujian.databinding.FragmentFilterWheelBinding;
import com.glide.chujian.model.GuiderModel;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.ScreenUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpSettingWheelHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FilterWheelFragment extends BaseFragment<FragmentFilterWheelBinding> {
    private final String TAG = "FilterWheelFragment";
    private ArrayAdapter<String> mFilterWheelAdapter;
    private ArrayAdapter<String> mPositionAdapter;
    private ArrayAdapter<String> mSlotNumberAdapter;
    private SlotNumberAdapter mSlotItemAdapter;
    private String mFFCurrentDevice = "";
    private int mLocalFwCurrentNameIndex = 0;
    private String mLocalFwCurrentName = "";

    private ArrayList<GuiderModel.AllDevice> mFFAllDevices = new ArrayList();

    private ArrayList<String> mFFAllDevicesList = new ArrayList();

    private ArrayList<String> mFFAllPositionList = new ArrayList();

    private ArrayList<String> mFFAllSlotNumberList = new ArrayList();
    private int mSlotNumberMax;
    private int mSlotNumber;
    private int mSlotPosition;
    private boolean mDeviceChangeStatus = false;//相机将要切换到的状态，用于定时弹出UI的判断
    private Timer mTimer;
    private MyTimerTask mTimerTask;
    private long mLastActionTime;
    @NotNull
    private TpSettingWheelHandler mHandler = new TpSettingWheelHandler(this);
    private ArrayList<String> mSlotNameList = new ArrayList();

    @Override
    public int getLayoutId() {
        return  R.layout.fragment_filter_wheel;
    }

    @Override
    public void onDeviceSelectChanged() {
        String device = mGuider.mSelectedDevices.get(mGuider.SELECTED_DEVICE);
        if (device == null)return;
        switch (device) {
            case "filterwheel": {
                loadFFDeviceView();
            }
            break;
        }
    }

    public void parameterLoadSuccess(){
        loadFFDeviceView();
        if (mGuider.mFilterWheelIsDeviceConnected){
            stopTimer();
        }
        loadFFDevicePropertyView();
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
        mGuider.registerHandlerListener(mHandler);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGuider.unregisterHandlerListener(mHandler);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            mGuider.unregisterHandlerListener(mHandler);
        }
        else{
            mGuider.registerHandlerListener(mHandler);
            updateDevices();
        }
    }

    private void init(){
        initView();
        initEvent();
        initData();
    }

    private void initData(){
        loadFFDeviceView();
    }

    private void loadFFDeviceView(){
        mFFAllDevices.clear();
        mFFAllDevicesList.clear();
        mFFAllDevices.addAll(mGuider.getAllDevices("FilterWheels"));
        mFFAllDevicesList.addAll(parseDevices(mFFAllDevices));

        if (mFFAllDevicesList.contains("None")){
            mFFAllDevicesList.remove("None");
            mFFAllDevicesList.add(0,"None");
        }
        mFilterWheelAdapter.clear();
        mFilterWheelAdapter.notifyDataSetChanged();
        mFilterWheelAdapter.addAll(mFFAllDevicesList);
        mFilterWheelAdapter.notifyDataSetChanged();

        String currentDevice = mGuider.mSelectedDevices.get("filterwheel");

        if (currentDevice == null){

        }
        else if (currentDevice.equals("None")){
            String filterwheel = SharedPreferencesUtil.getInstance().getString(Constant.SP_FILTER_WHEEL_ID,"");
            if (!filterwheel.equals("None")  && !filterwheel.equals("")  && mFFAllDevicesList.contains(filterwheel) && !isLabel(mFFAllDevices, filterwheel)){
                mGuider.mSelectedDevices.put("filterwheel",filterwheel);
                currentDevice = filterwheel;
                int currentFFIndex = mFFAllDevicesList.indexOf(currentDevice);
                if (currentFFIndex != -1){
                    mFFCurrentDevice = currentDevice;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("filterwheel", mFFCurrentDevice);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }

        mFFCurrentDevice = currentDevice;
        int currentFFIndex = mFFAllDevicesList.indexOf(currentDevice);
        if (currentFFIndex != -1) {
            binding.selectFilterWheel.setSelection(currentFFIndex);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setFFViewStatus(mGuider.mIsNetworkOnLine,mGuider.mFilterWheelIsDeviceConnected);
            }
        });
    }

    private void loadFFDevicePropertyView(){
        if (mGuider.mFilterWheelIsDeviceConnected){
            loadFFDeviceViewData();
        }else {
            resetFFDeviceView();
        }
    }

    private void resetFFDeviceView(){
        mPositionAdapter.clear();
        mPositionAdapter.notifyDataSetChanged();
        mSlotNumberAdapter.clear();
        mSlotNumberAdapter.notifyDataSetChanged();
    }
    private void loadFFDeviceViewData(){
        mSlotNumber = mGuider.mFilterWheelSlotNumber;
        mSlotPosition = mGuider.mFilterWheelPosition;
        mFFAllPositionList.clear();
        for (int i = 1;i <= mSlotNumber;i++) {
            mFFAllPositionList.add(String.valueOf(i));
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPositionAdapter.clear();
                mPositionAdapter.addAll(mFFAllPositionList);
                mPositionAdapter.notifyDataSetChanged();
                int index = mFFAllPositionList.indexOf(String.valueOf(mSlotPosition));
                if (index != -1) {
                    binding.selectPosition.setSelection(index);
                }
            }
        });
        mSlotNumberMax = mGuider.mFilterWheelSlotNumberMax;
        mFFAllSlotNumberList.clear();
        for (int i = 1;i <= mSlotNumberMax;i++) {
            mFFAllSlotNumberList.add(String.valueOf(i));
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSlotNumberAdapter.clear();
                mSlotNumberAdapter.addAll(mFFAllSlotNumberList);
                mSlotNumberAdapter.notifyDataSetChanged();
                if (mSlotNumber != -1) {
                    binding.selectSlotNumber.setSelection(mSlotNumber - 1);
                };
            }
        });

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.unidirectionalSwitch.setChecked(mGuider.mFilterWheelUnidirectional);
            }
        });

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.filterWheelConnectBtn.setChecked(mGuider.mFilterWheelIsDeviceConnected);
            }
        });
    }
    private void initEvent(){
        binding.selectFilterWheel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mGuider.mFilterWheelIsDeviceConnected)return;
                String item = mFilterWheelAdapter.getItem(position);
                if (item.equals("None")){
                    binding.filterWheelConnectBtn.setEnabled(false);
                }else {
                    binding.filterWheelConnectBtn.setEnabled(true);
                }

                if (Objects.equals(mGuider.mSelectedDevices.get("filterwheel"), item))return;
                mFFCurrentDevice = item;
                if(isLabel(mFFAllDevices, item)){
                    if (isLabelConfigured(mFFAllDevices, item)){
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    mGuider.selectDevice("filterwheel",item);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }else
                    {
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
                }else{
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mGuider.selectDevice("filterwheel",item);
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

        binding.selectPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        int pst = Integer.parseInt(mPositionAdapter.getItem(position));
                        if (mSlotPosition != pst) {
                            mSlotPosition = pst;
                            mGuider.setFwPosition(pst);
                        }
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.selectSlotNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSlotNameList.clear();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        int number = Integer.parseInt(mSlotNumberAdapter.getItem(position));
                        if (number != mSlotNumber){
                            mSlotNumber = number;
                            mGuider.setFwSlotNum(number);
                        }
                        for (int i = 0;i <= position;i++){
                            mSlotNameList.add(mGuider.getFwName(i));
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSlotItemAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.filterWheelConnectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mLoadingDialog.show();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                boolean isFFConnected = mGuider.mFilterWheelIsDeviceConnected;
                                waitDeviceStatusChange(!isFFConnected);

                                mIsConnectTimeOut = false;
                                boolean result = mGuider.setDeviceConnected("filterwheel",!isFFConnected);

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

        binding.unidirectionalSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        if (!mGuider.setFwUnidirectional(binding.unidirectionalSwitch.isChecked())){
                            responseUnidirectionalSwitch();
                        }
                    }
                }.start();
            }
        });

        binding.calibratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mGuider.fwCalibrate();
                    }
                }.start();
            }
        });

        GridLayoutManager mlayoutManager = new GridLayoutManager(mActivity,4);
        mlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.slotNumberRv.setLayoutManager(mlayoutManager);
        binding.slotNumberRv.addItemDecoration(new SpacesItemDecoration(ScreenUtil.androidAutoSizeDpToPx(15),4));
        mSlotItemAdapter =new SlotNumberAdapter(mSlotNameList,mActivity);
        binding.slotNumberRv.setAdapter(mSlotItemAdapter);
        mSlotItemAdapter.setOnClickListener(new SlotNumberAdapterListener() {
            @Override
            public void editItem(int position, String name) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            mLocalFwCurrentNameIndex = position;
                            mLocalFwCurrentName = name;
                            mGuider.setFwName(position,name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public void updateFilterWheelUnidirectional(){
        binding.unidirectionalSwitch.setChecked(mGuider.mFilterWheelUnidirectional);
    }
    private void responseUnidirectionalSwitch(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.unidirectionalSwitch.setChecked(mGuider.mFilterWheelUnidirectional);
            }
        });
    }

    public void updateFilterWheelSlotNumber(){
        int index = mFFAllSlotNumberList.indexOf(String.valueOf(mGuider.mFilterWheelSlotNumber));
        if (index != -1){
            mSlotNumber = mGuider.mFilterWheelSlotNumber;
            binding.selectSlotNumber.setSelection(index);
            mFFAllPositionList.clear();
            for (int i = 1;i <= mGuider.mFilterWheelSlotNumber;i++) {
                mFFAllPositionList.add(String.valueOf(i));
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPositionAdapter.clear();
                    mPositionAdapter.addAll(mFFAllPositionList);
                    mPositionAdapter.notifyDataSetChanged();
                }
            });
        }
    }
    public void updateFilterWheelPosition(){
        int index = mFFAllPositionList.indexOf(String.valueOf(mGuider.mFilterWheelPosition));
        if (index != -1){
            mSlotPosition = index;
            binding.selectPosition.setSelection(index);
        }
    }
    public void updateFilterWheelName(){
        try {
            if (mGuider.mFilterWheelName.mIndex <= mSlotNameList.size()){
                if (!mSlotNameList.get(mGuider.mFilterWheelName.mIndex).equals(mGuider.mFilterWheelName.mName)) {
                    mSlotNameList.set(mGuider.mFilterWheelName.mIndex, mGuider.mFilterWheelName.mName);
                    if (mGuider.mFilterWheelName.mIndex != mLocalFwCurrentNameIndex || !mGuider.mFilterWheelName.mName.equals(mLocalFwCurrentName)) {
                        mSlotItemAdapter.notifyDataSetChanged();
                    }
                }
            }else {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateDevices(){
/*        if (Objects.equals(mGuider.mSelectedDevices.get(mGuider.device_label), "filterwheel")){
            loadFFDeviceView();
        }*/
        loadFFDeviceView();
    }

    public void deviceConnectEvent(){
        if (!mGuider.mCurrentConnectedDevice.equals("filterwheel")) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGuider.mFilterWheelIsDeviceConnected) {
                } else {
                    setFFViewStatus(mGuider.mIsNetworkOnLine,false);
                }
            }
        });
    }

    private void initView(){
        mFilterWheelAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mFilterWheelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        mPositionAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mPositionAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        mSlotNumberAdapter =
                new ArrayAdapter<String>(mActivity, R.layout.setting_spinner_item);
        mSlotNumberAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        binding.selectFilterWheel.setAdapter(mFilterWheelAdapter);
        binding.selectPosition.setAdapter(mPositionAdapter);
        binding.selectSlotNumber.setAdapter(mSlotNumberAdapter);
    }

    private void setFFViewStatus(Boolean isNetworkOnline,Boolean connected){
        if (isNetworkOnline) {
            if (mFFCurrentDevice.equals("None")){
                binding.filterWheelConnectBtn.setEnabled(false);
            }else {
                binding.filterWheelConnectBtn.setEnabled(true);
            }
            binding.selectFilterWheel.setNorEnabled(!connected);
        }else {
            binding.selectFilterWheel.setNorEnabled(false);
            binding.filterWheelConnectBtn.setEnabled(false);
        }
        binding.filterWheelTv.setEnabled(isNetworkOnline);
        binding.unidirectionalSwitch.setEnabled(connected);
        binding.selectPosition.setNorEnabled(connected);
        binding.selectSlotNumber.setNorEnabled(connected);
        binding.filterWheelConnectBtn.setChecked(connected);
        nameViewEnable(connected);
        if (connected) {
            binding.slotNumberRv.setVisibility(View.VISIBLE);
        }else {
            binding.slotNumberRv.setVisibility(View.INVISIBLE);
        }
        loadFFDevicePropertyView();
    }

    private void nameViewEnable(boolean enable){
        binding.unidirectionalTv.setEnabled(enable);
        binding.positionTv.setEnabled(enable);
        binding.slotNumberTv.setEnabled(enable);
        binding.calibratingTv.setEnabled(enable);
        binding.calibratingBtn.setEnabled(enable);
    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mLastActionTime > 5000) {
                mIsConnectTimeOut = true;
                if (!mDeviceChangeStatus){
                    stopTimer();
                }
            }else if (!mGuider.mFilterWheelIsDeviceConnected){
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
