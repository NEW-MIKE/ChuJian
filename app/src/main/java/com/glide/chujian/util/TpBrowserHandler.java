package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.BrowserActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpBrowserHandler extends Handler {
    private final WeakReference<BrowserActivity> mOwner;

    public TpBrowserHandler(BrowserActivity owner) {
        mOwner = new WeakReference<BrowserActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        BrowserActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_APP_INFO:
                player.startLoadAllDataCache();
                break;
            case TpConst.MSG_ALL_CACHE_LOADED:
                player.finishLoadAllDataCache();
                break;
            case TpConst.MSG_ERROR:
               // player.warnError();
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
