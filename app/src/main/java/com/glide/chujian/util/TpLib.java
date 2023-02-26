package com.glide.chujian.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpLib {
    private String TAG = "TpLib";
    private static String mPictureUrl = "";
    private static TpLib sInstance;
    private static Handler mPreviewHandler = null;

    public synchronized boolean OpenCamera(String url) {
        return 0 == openDevice(url);
    }

    public static TpLib getInstance() {
        synchronized (TpLib.class) {
            if (sInstance == null) {
                sInstance = new TpLib();
            }
        }
        return sInstance;
    }

    public void ImageCallBack(int width, int height) {
        if (mPreviewHandler != null) {
            Message Msg = mPreviewHandler.obtainMessage();
            Msg.what = TpConst.MSG_UPDATE;
            Msg.arg1 = width;
            Msg.arg2 = height;
            mPreviewHandler.sendMessage(Msg);
        }else {
            Log.e(TAG, "updateFrame: previewHandler is null" );
        }
    }

    public void ErrorCallBack(int type) {
        if (mPreviewHandler != null) {
            Message Msg = mPreviewHandler.obtainMessage();
            Msg.what = TpConst.MSG_ERROR;
            mPreviewHandler.sendMessage(Msg);
        }
    }

    public void setPreviewHandler(Handler handler) {
        mPreviewHandler = handler;
    }

    static {
        System.loadLibrary("jnilib");
    }

    public native void init();

    private native void fini();

    public native boolean restartCamera();

    public native boolean startCameraStream();

    public native boolean stopCameraStream();

    public native String getName();
    public native String getPassword();
    public void updateJFrame(ByteBuffer directBuffer){
        LOGE("updateJFrame: directBuffer size :"+directBuffer.capacity());
        updateFrame(directBuffer,directBuffer.capacity());
    }

    private native void updateFrame(ByteBuffer directBuffer,int size);

    public int[] updateJPictureFrame(ByteBuffer directBuffer,String url){
        LOGE("updateJPictureFrame: directBuffer size :"+directBuffer.capacity());
        return updatePictureFrame(directBuffer,url,directBuffer.capacity());
    }

    private native int[] updatePictureFrame(ByteBuffer directBuffer,String url,int sizej);

    public int[] updateJVideoFrame(ByteBuffer directBuffer){
        LOGE("updateJVideoFrame: directBuffer size :"+directBuffer.capacity());
        return updateVideoFrame(directBuffer,directBuffer.capacity());
    }

    private native int[] updateVideoFrame(ByteBuffer directBuffer,int size);

    private native void clearNativeFrame(ByteBuffer directBuffer,int size);

    private native void clearMainNativeFrame(ByteBuffer directBuffer,int size);

    public void clearFrame(ByteBuffer directBuffer,int size){
        LOGE("clearFrame: directBuffer size :"+directBuffer.capacity() +"  size is :"+size);
        clearNativeFrame(directBuffer,size);
    }

    public void clearMainFrame(ByteBuffer directBuffer,int size){
        LOGE("clearMainFrame: directBuffer size :"+directBuffer.capacity() +"  size is :"+size);
        clearMainNativeFrame(directBuffer,size);
        mPictureUrl = "";
    }

    public native void releaseCamera();

    public native boolean isAlive();

    private native int openDevice(String url);

    public  int[] loadFitsPicture(String url){
        if (mPictureUrl.equals(url)){
            return loadNativeFitsPicture(url,0);
        }else {
            mPictureUrl = url;
            return loadNativeFitsPicture(url,1);
        }
    }

    private native int[] loadNativeFitsPicture(String url,int newPicUrl);

    public native boolean startRecordVideo(String url);

    public native boolean stopRecordVideo();

    public void updateJBrowserPictureFrame(ByteBuffer directBuffer,String url){
        LOGE("updateJBrowserPictureFrame: directBuffer size :"+directBuffer.capacity());
        updateBrowserPictureFrame(directBuffer,url,directBuffer.capacity());
    }

    private native void updateBrowserPictureFrame(ByteBuffer directBuffer,String url,int size);

    public native int[] loadBrowserFitsPicture(String url);

    public native void setHistHigh(int high);

    public native void setHistLow(int low);

    private void LOGE(String msg){
        Log.e(TAG, ""+msg);
    }
}
