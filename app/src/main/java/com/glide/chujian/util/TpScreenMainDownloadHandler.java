package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.ScreenMainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpScreenMainDownloadHandler extends Handler {
    private final WeakReference<ScreenMainActivity> mOwner;

    public TpScreenMainDownloadHandler(ScreenMainActivity owner) {
        mOwner = new WeakReference<ScreenMainActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        ScreenMainActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_CAPTURE_FRAME_SAVED:
                player.handleFrameSaved();
                break;
            case TpConst.PICTURE_DOWNLOADING:
                player.pictureDownloading(msg.arg1,msg.arg2);
                break;
            default:
                break;
        }
    }
}
