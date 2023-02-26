package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.TaskActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpTaskHandler extends Handler {
    private final WeakReference<TaskActivity> mOwner;

    public TpTaskHandler(TaskActivity owner) {
        mOwner = new WeakReference<TaskActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        TaskActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_APP_INFO:
                player.startLoadAllDataCache();
                break;
            case TpConst.MSG_ALL_CACHE_LOADED:
                player.finishLoadAllDataCache();
                break;
            case TpConst.MSG_CONNECTION_LOST:
                player.connectionLostEvent();
                player.finishLoadAllDataCache();
                break;
            case TpConst.MSG_CONNECTION_SUCCESS:
                player.connectionSuccessEvent();
                break;
            default:
                break;
        }
    }
}
