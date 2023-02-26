package com.glide.chujian.util;

import android.app.Activity;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.view.LoadingDialog;
import com.glide.chujian.view.NetworkConnectDialog;

import java.util.Timer;
import java.util.TimerTask;

public class CheckConnectProcess{
    private String TAG = "CheckConnectProcess";
    private Timer mConnectTimer;
    private CheckConnectTask mCheckConnectTask;
    private long mCheckConnectTimeCnt;
    private NetworkConnectDialog.Builder mCheckConnectBuilder;
    private NetworkConnectDialog mCheckConnectDialog;
    private LoadingDialog loadingDialog,dataLoadingDialog;
    private Activity mActivity;
    private Guider mGuider;

    public CheckConnectProcess(Activity activity) {
        this.mActivity = activity;
        mGuider = Guider.getInstance();
    }

    private class CheckConnectTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mCheckConnectTimeCnt > 10000) {
                stopConnectTimer();
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCheckConnectBuilder = new NetworkConnectDialog.Builder(mActivity);
                            //  .setMessageConfirmContent(App.instance.getResources().getString(R.string.normal_sure));
                            mCheckConnectDialog = mCheckConnectBuilder.create();
                            if (!mGuider.mCancelDialog) {
                               // mCheckConnectDialog.show();
                            }
                            mCheckConnectDialog.show();
                            loadingDialog.dismiss();
                            mCheckConnectBuilder.setOnNormalClickListener(new NetworkConnectDialog.OnNormalClickListener() {
                                @Override
                                public void sure() {
                                    stopConnectTimer();
                                    mCheckConnectDialog.dismiss();
                                    mGuider.mCancelDialog = true;
                                }

                                @Override
                                public void cancel() {
                                    mCheckConnectDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public void startConnectTimer() {
        stopConnectTimer();
        mCheckConnectTimeCnt = System.currentTimeMillis();
        mConnectTimer = new Timer(true);
        mCheckConnectTask = new CheckConnectTask();
        mConnectTimer.schedule(mCheckConnectTask, 0, 1000);
    }

    public void stopConnectTimer() {
        if (mCheckConnectDialog != null){
            mCheckConnectDialog.dismiss();
        }
        if (mConnectTimer != null) {
            mConnectTimer.cancel();
            mConnectTimer = null;
        }

        if (mCheckConnectTask != null){
            mCheckConnectTask.cancel();
            mCheckConnectTask = null;
        }
    }

    public void showConnectingDialog(){
        if (loadingDialog == null){
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog = new LoadingDialog.Builder(mActivity)
                                .setmMessage(App.INSTANCE.getResources().getString(R.string.connecting))
                                .setmIsCancelable(true)
                                .setmIsCancelOutside(false)
                                .create();
                        if (!mGuider.mCancelDialog) {
                            //loadingDialog.show();
                        }

                        loadingDialog.show();
                        startConnectTimer();
                    }
                });
            }
        }else {
        }
    }
    public void showDataLoadingDialog(){
        if (dataLoadingDialog == null){
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataLoadingDialog = new LoadingDialog.Builder(mActivity)
                                .setmMessage(App.INSTANCE.getResources().getString(R.string.data_loading))
                                .setmIsCancelable(true)
                                .setmIsCancelOutside(false)
                                .create();

                        dataLoadingDialog.show();
                    }
                });
            }
        }else {
        }
    }

    public void dismissDataLoadingDialog(){
        if (dataLoadingDialog != null){
            dataLoadingDialog.dismiss();
            dataLoadingDialog = null;
        }
        else {
        }
    }

    public void dismissConnectedDialog(){
        stopConnectTimer();
        if (loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        else {
        }
    }

}