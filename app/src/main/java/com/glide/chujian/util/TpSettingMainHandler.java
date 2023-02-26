package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.fragment.MainCameraFragment;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpSettingMainHandler extends Handler {
    private final WeakReference<MainCameraFragment> mOwner;

    public TpSettingMainHandler(MainCameraFragment owner) {
        mOwner = new WeakReference<MainCameraFragment>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        MainCameraFragment player = mOwner.get();
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
            case TpConst.RESOLUTION_CHANGED:
                player.resolutionChangedEvent();
                break;
            case TpConst.GAIN_CHANGED:
                player.gainChangedEvent();
                break;
            case TpConst.EXPOSURE_CHANGED:
                player.expChangedEvent();
                break;
            case TpConst.BINNING_CHANGED:
                player.binningChangedEvent();
                break;
            case TpConst.TARGET_TEMPERATURE_CHANGED:
                player.targetTempChangedEvent();
                break;
            case TpConst.MSG_FOCAL_LENGTH_CHANGED:
                player.focalLengthChangedEvent();
                break;
            case TpConst.MSG_COOLING_CHANGED:
                player.coolingChangedEvent();
                break;
            case TpConst.MSG_FAN_CHANGED:
                player.fanChangedEvent();
                break;
            case TpConst.MSG_GAIN_MODE_CHANGED:
                player.gainModeChangedEvent();
                break;
            case TpConst.MSG_LOW_NOISE_CHANGED:
                player.lowNoiseChangedEvent();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                if (msg.arg1 == Guider.MAIN_CACHE_LOAD) {
                    player.parameterLoadSuccess();
                }
                break;
            default:
                break;
        }
    }
}
