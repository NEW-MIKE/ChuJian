package com.glide.chujian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiChangeReceiver extends BroadcastReceiver {
    private String TAG = "WifiChangeReceiver";
    private BaseActivity mActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction())
        {
            case WifiManager.NETWORK_STATE_CHANGED_ACTION: {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    Log.e(TAG, "连接到网络 " + wifiInfo.getSSID());
                    if (mActivity != null) {
                        mActivity.updateWifiName(App.INSTANCE.getResources().getString(R.string.network)+": " + wifiInfo.getSSID());
                    }
                }else {
                    Log.e(TAG, "网络断开 ");
                    mActivity.updateWifiName("");
                }
            }
            break;
        }
    }


    public void setActivity(BaseActivity activity)  //此处新写方法，用来获取Activity
    {
        mActivity = activity; //将传入的activity强转未MainActivity
    }
}
