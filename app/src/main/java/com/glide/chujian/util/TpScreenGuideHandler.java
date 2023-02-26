package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.GuideActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpScreenGuideHandler extends Handler {
    private final WeakReference<GuideActivity> mOwner;

    public TpScreenGuideHandler(GuideActivity owner) {
        mOwner = new WeakReference<GuideActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        GuideActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_APP_INFO:
                player.startLoadAllDataCache();
                break;
            case TpConst.MSG_ALL_CACHE_LOADED:
                player.finishLoadAllDataCache();
                break;
            case TpConst.MSG_CURRENT_DEVICES:
              //  player.loadCurrentDevices();
                break;
            case TpConst.MSG_APP_STATE:
              //  player.appState();
                player.enterGuideFromWorkState();
                break;
            case TpConst.MSG_UPDATE:
                player.update(msg.arg1, msg.arg2);
                break;
            case TpConst.MSG_GUIDING:
                player.guidingEvent();
                player.chartVisibleIndicator();
                player.updateChartInformationView();
                break;
            case TpConst.MSG_ERROR:
                player.warnError();
                break;
            case TpConst.MSG_STAR_SELECTED:
                player.starSelected();
                break;
            case TpConst.MSG_RTSP_RES_CHANGED:
                player.rtspResChangedEvent();
                break;
            case TpConst.MSG_RTSP_SOURCE_CHANGED:
                player.updateRtspSource();
                break;
            case TpConst.MSG_CAPTURE_LOOPING:
                player.captureLoopingEvent();
                break;
            case TpConst.MSG_DEVICE_CONNECTED:
                player.deviceConnectEvent();
                break;
            case TpConst.MSG_RTSP_READY:
                player.rtspIsReadyEvent();
                break;
            case TpConst.MSG_CALIBRATING:
                player.calibratingEvent();
                break;
            case TpConst.MSG_SETTLING:
               // player.settingEvent();  该事件会与guiding交叉出现，相差的误差引起导星初期线的抖动，且该事件该如何使用，前期参考pc，目前不明确，暂时屏蔽
                break;
            case TpConst.MSG_CALIBRATION_STARTED:
                player.calibrationStartEvent();
                break;
            case TpConst.MSG_DEVICE_SELECTED:
                player.updateGuideDeviceTitle();
                break;
            case TpConst.MSG_STAR_LOST:
                player.starLostEvent();
                break;
            case TpConst.MSG_GUIDING_STOPPED:
                player.guidingStoppedEvent();
                break;
            case TpConst.MSG_GUIDING_STARTED:
                player.guidingStartedEvent();
                break;
            case TpConst.MSG_CONNECTION_LOST:
                player.connectionLostEvent();
                player.finishLoadAllDataCache();
                player.updateChartInformationView();
                break;
            case TpConst.MSG_CALIBRATION_FAILED:
                player.calibrationFailedEvent();
                break;
            case TpConst.GAIN_CHANGED:
                player.gainChangedEvent();
                break;
            case TpConst.EXPOSURE_CHANGED:
                player.expChangedEvent();
                break;
            case TpConst.GUIDING_DITHERED:
                player.guidingDithered();
                break;
            case TpConst.MSG_CONNECTION_SUCCESS:
                player.connectionSuccessEvent();
                break;
            case TpConst.MSG_SLEW_STARTED:
                player.startSlewEvent();
                break;
            case TpConst.MSG_SLEW_STOPPED:
                player.stopSlewEvent();
                break;
            case TpConst.MSG_ALERT:
                player.gotoErrorEvent();
                break;
            case TpConst.MSG_DITHER_SCALE_CHANGED:
                player.ditherScaleChangedEvent();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                if (msg.arg1 == Guider.GUIDE_CACHE_LOAD) {
                    player.parameterLoadSuccess();
                }
                break;
            case TpConst.MSG_STAR_PROFILE_UPDATE:
                player.starProfileUpdateEvent();
                break;
            default:
                break;
        }
    }
}
