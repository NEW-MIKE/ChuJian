package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.fragment.GuideCameraFragment;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpSettingGuideHandler extends Handler {
    private final WeakReference<GuideCameraFragment> mOwner;

    public TpSettingGuideHandler(GuideCameraFragment owner) {
        mOwner = new WeakReference<GuideCameraFragment>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        GuideCameraFragment player = mOwner.get();
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
            case TpConst.GAIN_CHANGED:
                player.gainChangedEvent();
                break;
            case TpConst.BINNING_CHANGED:
                player.binningChangedEvent();
                break;
            case TpConst.MSG_MOUNT_RA_MAX_CHANGED:
                player.mountRaMaxChangedEvent();
                break;
            case TpConst.MSG_MOUNT_DEC_MAX_CHANGED:
                player.mountDecMaxChangedEvent();
                break;
            case TpConst.MSG_DITHER_SCALE_CHANGED:
                player.ditherScaleChangedEvent();
                break;
            case TpConst.MSG_DITHER_MODE_CHANGED:
                player.ditherModeChangedEvent();
                break;
            case TpConst.MSG_DITHER_RA_ONLY_CHANGED:
                player.ditherRaOnlyChangedEvent();
                break;
            case TpConst.MSG_FOCAL_LENGTH_CHANGED:
                player.focalLengthChangedEvent();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                if (msg.arg1 == Guider.GUIDE_CACHE_LOAD) {
                    player.parameterLoadSuccess();
                }
                break;
            default:
                break;
        }
    }
}
