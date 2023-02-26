package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.view.GotoPop;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class GotoPopHandler extends Handler {
    private final WeakReference<GotoPop> mOwner;

    public GotoPopHandler(GotoPop owner) {
        mOwner = new WeakReference<GotoPop>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        GotoPop player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_SLEW_STARTED:
                player.startSlewEvent();
                break;
            case TpConst.MSG_SLEW_STOPPED:
                player.stopSlewEvent();
                break;
            case TpConst.MSG_ALERT:
                player.gotoErrorEvent();
                break;
            case TpConst.MSG_DEVICE_CONNECTED:
                player.deviceConnectEvent();
                break;
            case TpConst.MOUNT_SPEED_CHANGED:
                player.mountSpeedChangedEvent();
                break;
            default:
                break;
        }
    }
}
