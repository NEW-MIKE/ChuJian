package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.fragment.FocuserFragment;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpSettingFocuserHandler extends Handler {
    private final WeakReference<FocuserFragment> mOwner;

    public TpSettingFocuserHandler(FocuserFragment owner) {
        mOwner = new WeakReference<FocuserFragment>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        FocuserFragment player = mOwner.get();
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
            case TpConst.MSG_FOCUSER_REVERSED_CHANGED:
                player.reversedEvent();
                break;
            case TpConst.MSG_FOCUSER_MAX_CHANGED:
                player.maxStepEvent();
                break;
            case TpConst.MSG_FOCUSER_TARGET_POSITION_CHANGED:
                player.targetPositionEvent();
                break;
            case TpConst.MSG_FOCUSER_CURRENT_POSITION_CHANGED:
                player.currentPositionEvent();
                break;
            case TpConst.MSG_FOCUSER_STEP_CHANGED:
                player.coarseStepEvent();
                player.fineStepEvent();
                break;
            case TpConst.MSG_FOCUSER_BEEP_CHANGED:
                player.beepEvent();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                if (msg.arg1 == Guider.FOCUSER_CACHE_LOAD) {
                    player.parameterLoadSuccess();
                }
                break;
            default:
                break;
        }
    }
}
