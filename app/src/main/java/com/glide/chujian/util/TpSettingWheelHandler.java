package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.fragment.FilterWheelFragment;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpSettingWheelHandler extends Handler {
    private final WeakReference<FilterWheelFragment> mOwner;

    public TpSettingWheelHandler(FilterWheelFragment owner) {
        mOwner = new WeakReference<FilterWheelFragment>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        FilterWheelFragment player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_FILTER_WHEEL_NAME_CHANGED:
                player.updateFilterWheelName();
                break;
            case TpConst.MSG_FILTER_WHEEL_POSITION_CHANGED:
                player.updateFilterWheelPosition();
                break;
            case TpConst.MSG_FILTER_WHEEL_SLOT_NUMBER_CHANGED:
                player.updateFilterWheelSlotNumber();
                break;
            case TpConst.MSG_FILTER_WHEEL_UNIDIRECTIONAL_CHANGED:
                player.updateFilterWheelUnidirectional();
                break;
            case TpConst.MSG_NEW_DEVICE:
                player.updateDevices();
                break;
            case TpConst.MSG_DEVICE_CONNECTED:
                player.deviceConnectEvent();
                break;
            case TpConst.MSG_DEVICE_SELECTED:
                player.onDeviceSelectChanged();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                if (msg.arg1 == Guider.FF_CACHE_LOAD) {
                    player.parameterLoadSuccess();
                }
                break;
            default:
                break;
        }
    }
}
