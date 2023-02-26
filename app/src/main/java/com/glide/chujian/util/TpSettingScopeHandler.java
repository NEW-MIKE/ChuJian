package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.fragment.ScopeFragment;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpSettingScopeHandler extends Handler {
    private final WeakReference<ScopeFragment> mOwner;

    public TpSettingScopeHandler(ScopeFragment owner) {
        mOwner = new WeakReference<ScopeFragment>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        ScopeFragment player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_NEW_DEVICE:
                player.updateDevices();
                break;
            case TpConst.MSG_DEVICE_CONNECTED:
                player.deviceConnectEvent();
                break;
            case TpConst.MSG_DEVICE_SELECTED:
                player.onDeviceSelectChanged();
                break;
            case TpConst.MSG_MOUNT_BAUD_RATE_CHANGED:
                player.mountBaudrateChangedEvent();
                break;
            case TpConst.MSG_MOUNT_TRACKING_CHANGED:
                player.mountTrackingChangedEvent();
                break;
            case TpConst.MSG_MOUNT_TRACKING_MODE_CHANGED:
                player.mountTrackingModeChangedEvent();
                break;
            case TpConst.MOUNT_SPEED_CHANGED:
                player.mountSpeedChangedEvent();
                break;
            case TpConst.MSG_MOUNT_PARK_CHANGED:
                player.mountParkChangedEvent();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                if (msg.arg1 == Guider.SCOPE_CACHE_LOAD) {
                    player.parameterLoadSuccess();
                }
                break;
            default:
                break;
        }
    }
}
