package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.view.FocuserPop;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class FocuserPopHandler extends Handler {
    private final WeakReference<FocuserPop> mOwner;

    public FocuserPopHandler(FocuserPop owner) {
        mOwner = new WeakReference<FocuserPop>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        FocuserPop player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_FOCUSER_MODE_CHANGED:
                player.focuserModeEvent();
                break;
            case TpConst.MSG_FOCUSER_CURRENT_POSITION_CHANGED:
            case TpConst.MSG_FOCUSER_TARGET_POSITION_CHANGED:
                player.focuserCurrentStepEvent();
                break;
            default:
                break;
        }
    }
}
