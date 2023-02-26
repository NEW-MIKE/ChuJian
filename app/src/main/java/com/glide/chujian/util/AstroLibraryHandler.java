package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.AstroLibraryActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class AstroLibraryHandler extends Handler {
    private final WeakReference<AstroLibraryActivity> mOwner;

    public AstroLibraryHandler(AstroLibraryActivity owner) {
        mOwner = new WeakReference<AstroLibraryActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        AstroLibraryActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_APP_INFO:
                player.startLoadAllDataCache();
                break;
            case TpConst.MSG_ALL_CACHE_LOADED:
                if (msg.arg1 == Guider.SCOPE_CACHE_LOAD) {
                }
                player.finishLoadAllDataCache();
                break;
            case TpConst.MSG_CONNECTION_LOST:
                player.connectionLostEvent();
                player.finishLoadAllDataCache();
                break;
            case TpConst.MSG_DEVICE_CONNECTED:
                player.deviceConnectEvent();
                break;
            case TpConst.MSG_CONNECTION_SUCCESS:
                player.connectionSuccessEvent();
                break;
            case TpConst.MSG_STAR_LOST:
            case TpConst.MSG_CALIBRATION_FAILED:
            case TpConst.MSG_GUIDING:
            case TpConst.MSG_CALIBRATING:
            case TpConst.MSG_SETTLING:
            case TpConst.MSG_CALIBRATION_STARTED:
            case TpConst.MSG_GUIDING_STOPPED:
            case TpConst.MSG_GUIDING_STARTED:
                player.manageGotoStatus();
                break;
            default:
                break;
        }
    }
}
