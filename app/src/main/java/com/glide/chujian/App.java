package com.glide.chujian;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.glide.chujian.util.CrashHandler;
import com.glide.chujian.util.DataBaseUtil;
import com.glide.chujian.util.LogUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.ToastUtil;

public class App extends Application {
    private String TAG = "Application";
    public static Application INSTANCE;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        CrashHandler.getInstance().init(this);
        ToastUtil.init(this);
        new Thread(){
            @Override
            public void run() {
                super.run();
                DataBaseUtil.copyDbFromAssert2Data();
               // new DBOpenHelper(INSTANCE, Constant.DB_NAME,null,1);
            }
        }.start();
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory: " );
        LogUtil.writeLowMemoryToFile("App");
    }
}
