package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpHandler extends Handler {
    private final WeakReference<MainActivity> mOwner;

    public TpHandler(MainActivity owner) {
        mOwner = new WeakReference<MainActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            default:
                break;
        }
    }
}
