package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;

import com.glide.chujian.ScreenMainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpScreenMainHandler extends Handler {
    private final WeakReference<ScreenMainActivity> mOwner;

    public TpScreenMainHandler(ScreenMainActivity owner) {
        mOwner = new WeakReference<ScreenMainActivity>(owner);
    }

    @Override
    public void handleMessage(Message msg) {
        ScreenMainActivity player = mOwner.get();
        if(player == null)return;
        switch (msg.what) {
            case TpConst.MSG_APP_INFO:
                player.startLoadAllDataCache();
                break;
            case TpConst.MSG_ALL_CACHE_LOADED:
                player.finishLoadAllDataCache();
                break;
            case TpConst.MSG_CAPTURE_FRAME_SAVED:
                //player.handleFrameSaved();
                break;
            case TpConst.MSG_CURRENT_DEVICES:
              //  player.loadCurrentDevices();
                break;
            case TpConst.MSG_APP_STATE:
                player.enterMainFromWorkState();
                break;
            case TpConst.MSG_UPDATE:
                player.updateVideoRender(msg.arg1, msg.arg2);
                break;
            case TpConst.MSG_ERROR:
                player.warnError();
                break;
            case TpConst.MSG_RTSP_RES_CHANGED:
                player.rtspResChangedEvent();
                break;
            case TpConst.MSG_RTSP_SOURCE_CHANGED:
                player.updateRtspSource();
                break;
            case TpConst.MSG_CAPTURE_MODE_CHANGED:
                player.syncCaptureMode();
                break;
            case TpConst.MSG_RTSP_READY:
                player.rtspReadyEvent();
                break;
            case TpConst.MSG_DEVICE_SELECTED:
                player.formatCurrentDeviceShowInfo();
                break;
            case TpConst.MSG_CAPTURE_LOOPING:
                player.captureLooping();
                break;
            case TpConst.MSG_DEVICE_CONNECTED:
                player.deviceConnectEvent();
                player.manageChartCacheView();
                break;
            case TpConst.MSG_CONNECTION_LOST:
                player.connectionLostEvent();
                player.finishLoadAllDataCache();
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
            case TpConst.PICTURE_DOWNLOADING:
               // player.pictureDownloading(msg.arg1,msg.arg2);
                break;
            case TpConst.MSG_CONNECTION_SUCCESS:
                player.connectionSuccessEvent();
                player.updateChartInformationView();
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
            case TpConst.MSG_COOLING_CHANGED:
                player.coolingChangedEvent();
                break;
            case TpConst.MSG_PARAMETER_ALL_LOAD:
                if (msg.arg1 == Guider.MAIN_CACHE_LOAD) {
                    player.parameterLoadSuccess();
                }
                break;
            case TpConst.MSG_STAR_LOST:
                player.manageScopeStatus();
                player.starLostEvent();
                break;
            case TpConst.MSG_CALIBRATION_FAILED:
                player.manageScopeStatus();
                player.calibrationFailedEvent();
                break;
            case TpConst.MSG_GUIDING:
                player.manageScopeStatus();
                player.chartVisibleIndicator();
                player.updateChartInformationView();
                break;
            case TpConst.MSG_CALIBRATING:
            case TpConst.MSG_SETTLING:
            case TpConst.MSG_CALIBRATION_STARTED:
            case TpConst.MSG_GUIDING_STOPPED:
            case TpConst.MSG_GUIDING_STARTED:
                player.manageScopeStatus();
                break;
            default:
                break;
        }
    }
}
