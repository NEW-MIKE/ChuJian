package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.MeetActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpSplashHandler extends Handler {
    private final WeakReference<MeetActivity> mOwner;

    public TpSplashHandler(MeetActivity owner) {
        mOwner = new WeakReference<MeetActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        MeetActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            default:
                break;
        }
    }
}
