package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.fragment.MiscFragment;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpSettingMiscHandler extends Handler {
    private final WeakReference<MiscFragment> mOwner;

    public TpSettingMiscHandler(MiscFragment owner) {
        mOwner = new WeakReference<MiscFragment>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        MiscFragment player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_CONNECTION_LOST:
                player.connectionLostEvent();
                break;
            case TpConst.MSG_DEVICE_CONNECTED:
                player.deviceConnectEvent();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                player.parameterLoadSuccess();
                break;
            default:
                break;
        }
    }
}
