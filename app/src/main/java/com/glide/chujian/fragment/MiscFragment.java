package com.glide.chujian.fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.databinding.FragmentMiscBinding;
import com.glide.chujian.util.TpSettingMiscHandler;
import com.glide.chujian.view.DeleteConfirmDialog;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class MiscFragment extends BaseFragment<FragmentMiscBinding>{
    private static String TAG = "MiscFragment";

    TpSettingMiscHandler mHandler = new TpSettingMiscHandler(this);

    private Timer mTimer;
    private MyTimerTask mTimerTask;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_misc;
    }

    @Override
    public void onDeviceSelectChanged() {
    }
    @Override
    public void waitDeviceStatusChange(Boolean status) {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        initData();
    }
    private void init(){
        initView();
        initData();
        initEvent();
    }

    private boolean isStringInteger(String stringToCheck, int radix) {
        if(stringToCheck.isEmpty()) return false;           //Check if the string is empty
        for(int i = 0; i < stringToCheck.length(); i++) {
            if(i == 0 && stringToCheck.charAt(i) == '-') {     //Check for negative Integers
                if(stringToCheck.length() == 1) return false;
                else continue;
            }
            if(Character.digit(stringToCheck.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    private Timer mConnectTimer;
    private ConnectingTimerTask mConnectTimerTask;
    private class ConnectingTimerTask extends TimerTask {
        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopIpConnecting();
                }
            });
        }
    }

    private void startIpConnectingTimer() {
        stopIpConnectingTimer();
        mConnectTimer = new Timer(true);
        mConnectTimerTask = new ConnectingTimerTask();
        mConnectTimer.schedule(mConnectTimerTask, 5000);
    }

    private void stopIpConnectingTimer() {
        if (mConnectTimer != null) {
            mConnectTimer.cancel();
            mConnectTimer = null;
        }
        if (mConnectTimerTask != null) {
            mConnectTimerTask.cancel();
            mConnectTimerTask = null;
        }
    }

    private void initEvent() {
        mActivity.showSoftStringInput(binding.ipInputEt,true,true);
        binding.ipInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String toString = s.toString();
                String[] value = toString.split("\\.");
                String b = toString.replace(".","");
                int no = (toString.length() - b.length());
                if (no != 3){
                    binding.ipConnectBtn.setVisibility(View.INVISIBLE);
                }else {
                    if (value.length == 4){
                        int i = 0;
                        for (;i < value.length;i++){
                            if (!isStringInteger(value[i],10)){
                                break;
                            }
                        }
                        if (i == value.length){
                            binding.ipConnectBtn.setVisibility(View.VISIBLE);
                        }
                    }else {
                        binding.ipConnectBtn.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        binding.ipConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuider.host = binding.ipInputEt.getText().toString();
                startIpConnecting();
                startIpConnectingTimer();
            }
        });

        binding.dcOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (mGuider.setDcOutput(1,!mGuider.mDcOneStatus)){
                                mGuider.mDcOneStatus = !mGuider.mDcOneStatus;
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDcOneStatus(mGuider.mDcOneStatus);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.dcTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (mGuider.setDcOutput(2,!mGuider.mDcTwoStatus)){
                                mGuider.mDcTwoStatus = !mGuider.mDcTwoStatus;
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDcTwoStatus(mGuider.mDcTwoStatus);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.dcThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (mGuider.setDcOutput(3,!mGuider.mDcThreeStatus)){
                                mGuider.mDcThreeStatus = !mGuider.mDcThreeStatus;
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDcThreeStatus(mGuider.mDcThreeStatus);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.dcFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (mGuider.setDcOutput(4,!mGuider.mDcFourStatus)){
                                mGuider.mDcFourStatus = !mGuider.mDcFourStatus;
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDcFourStatus(mGuider.mDcFourStatus);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        
        binding.shutdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteConfirmDialog.Builder mBuider = new DeleteConfirmDialog.Builder(mActivity);
                DeleteConfirmDialog dialog = mBuider
                        .setSureMsg(App.INSTANCE.getResources().getString(R.string.shutdown))
                        .setMainMsg(App.INSTANCE.getResources().getString(R.string.shutdown_main))
                        .create();
                mBuider.setOnDeleteListener(new DeleteConfirmDialog.OnDeleteListener() {
                    @Override
                    public void delete() {
                        dialog.dismiss();
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    if (mGuider.shutdown())
                                    {
                                        mGuider.setHeartBeatTimeOut();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                    @Override
                    public void cancel() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        binding.connectByIpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.backgroundTv11.getVisibility() == View.VISIBLE) {
                    stopIpConnectingTimer();
                    stopIpConnecting();
                    binding.backgroundTv11.setVisibility(View.GONE);
                }else {
                    binding.backgroundTv11.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void startIpConnecting(){
        binding.ipInputEt.setEnabled(false);
        binding.ipConnectBtn.setEnabled(false);
        binding.ipConnectBtn.setText("");
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void stopIpConnecting(){
        binding.ipInputEt.setEnabled(true);
        binding.ipConnectBtn.setEnabled(true);
        binding.ipConnectBtn.setText(App.INSTANCE.getResources().getString(R.string.network_connect));
        binding.progressBar.setVisibility(View.GONE);
    }

    private void initData() {
    }

    private void initView() {
        binding.ipInputEt.getText().clear();
        binding.ipInputEt.setText(mGuider.host);
        binding.ipInputEt.setSelection(mGuider.host.length());
        networkStatus(mGuider.mIsNetworkOnLine);
        setDcOneStatus(mGuider.mDcOneStatus);
        setDcTwoStatus(mGuider.mDcTwoStatus);
        setDcThreeStatus(mGuider.mDcThreeStatus);
        setDcFourStatus(mGuider.mDcFourStatus);
    }

    private void setDcOneStatus(boolean status){
        binding.dcOneBtn.setChecked(status);
    }
    private void setDcTwoStatus(boolean status){
        binding.dcTwoBtn.setChecked(status);
    }
    private void setDcThreeStatus(boolean status){
        binding.dcThreeBtn.setChecked(status);
    }
    private void setDcFourStatus(boolean status){
        binding.dcFourBtn.setChecked(status);
    }

    private void networkStatus(boolean isOnline){
        binding.backgroundTv11.setVisibility(View.GONE);
        if (isOnline){
            binding.backgroundTv3.setVisibility(View.VISIBLE);
            binding.shutdownBtn.setVisibility(View.VISIBLE);
            binding.connectByIpBtn.setVisibility(View.GONE);
            binding.ipConnectInfo.setVisibility(View.GONE);/*
            binding.ipInputEt.setVisibility(View.INVISIBLE);
            binding.ipConnectBtn.setVisibility(View.INVISIBLE);*/
            binding.ipConnectTv.setTextColor(App.INSTANCE.getResources().getColor(R.color.green));
            binding.ipConnectTv.setText(App.INSTANCE.getResources().getString(R.string.ip_connected));
        }else {
            binding.backgroundTv3.setVisibility(View.INVISIBLE);
            binding.shutdownBtn.setVisibility(View.INVISIBLE);
            binding.connectByIpBtn.setVisibility(View.VISIBLE);
            binding.ipConnectInfo.setVisibility(View.VISIBLE);/*
            binding.ipInputEt.setVisibility(View.VISIBLE);
            binding.ipConnectBtn.setVisibility(View.VISIBLE);*/
            binding.ipConnectTv.setTextColor(App.INSTANCE.getResources().getColor(R.color.red));
            binding.ipConnectTv.setText(App.INSTANCE.getResources().getString(R.string.ip_disconnect));
        }
        binding.shutdownBtn.setEnabled(isOnline);
        binding.dcOneBtn.setEnabled(isOnline);
        binding.dcTwoBtn.setEnabled(isOnline);
        binding.dcThreeBtn.setEnabled(isOnline);
        binding.dcFourBtn.setEnabled(isOnline);
    }
    public void connectionLostEvent(){
        networkStatus(mGuider.mIsNetworkOnLine);
    }

    public void deviceConnectEvent(){
        initView();
    }

    public void parameterLoadSuccess(){
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            stopIpConnectingTimer();
            stopIpConnecting();
            mGuider.unregisterHandlerListener(mHandler);
        }else{
            initView();
            mGuider.registerHandlerListener(mHandler);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGuider.unregisterHandlerListener(mHandler);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
        }
    }

    private void startTimer() {
        if (mTimer != null){
            stopTimer();
        }
        mTimer = new Timer(true);
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, 0, 500);
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
