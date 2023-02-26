package com.glide.chujian.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.glide.chujian.R;
import com.glide.chujian.ScreenMainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import jcifs.smb.SmbFile;

public class FileLoader extends AsyncTaskLoader<Bundle> {
    private final String TAG = "FILELOADER";
    private final String mSmbRootFolder = "smb://"+ Guider.getInstance().host+"/AstroBox/images/";
    private Bundle mBundle;
    public static final String KEY_RESULT = "result";
    public static final String KEY_SUCCESS = "success";
    private File mDestFile;
    private static Handler mPreviewHandler = null;

    private boolean mIsCanceled = false;
    public void setPreviewHandler(Handler handler) {
        mPreviewHandler = handler;
    }

    public FileLoader(Context context, Bundle bundle) {
        super(context);
        this.mBundle = bundle;
        Log.e(TAG, "FileLoader: loadInBackground" );
    }

    public void cancelDownload(boolean cancel){
        mIsCanceled = cancel;
        Log.e(TAG, "cancelDownload: "+cancel );
        if (mDestFile != null){
            if (mDestFile.exists()) {
                mDestFile.delete();
            }
        }
        cancelLoadInBackground();
    }
    @Override
    public Bundle loadInBackground() {
        try {
            Log.e(TAG, "loadInBackground: Loader start the background " );
            SmbFile sFolder = new SmbFile(mSmbRootFolder);
            Log.e(TAG, "loadInBackground: "+sFolder.isDirectory() );
            if (sFolder.isDirectory())
            {
                Log.e(TAG, "loadInBackground: "+sFolder.isDirectory() );
                SmbFile[] files = sFolder.listFiles();
                if (files.length > 0)
                {
                    Log.e(TAG, "loadInBackground: "+files.length );
                    String fileName = files[0].getPath();
                    for (SmbFile sfile : files) {
                        fileName = sfile.getPath();
                    }
                    {
                        String downloadName = mBundle.getString(ScreenMainActivity.FRAME_SAVED_NAME);

                        Log.e(TAG, "loadInBackground: "+downloadName );
                        SmbFile sFile = new SmbFile(mSmbRootFolder +downloadName);  // Download the last file
                        boolean bExist = false;
                        int length = mBundle.getInt(ScreenMainActivity.FRAME_LENGTH);
                        int hasReadLength = 0;
                        if (sFile.exists() && sFile.isFile() && sFile.canRead()) {
                            BufferedInputStream inBuf = new BufferedInputStream(sFile.getInputStream());
                            mDestFile = new File(mBundle.getString(ScreenMainActivity.FRAME_SAVED_PATH) + sFile.getName());
                            OutputStream out = new FileOutputStream(mDestFile);
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = inBuf.read(buf)) > 0) {
                                if (mIsCanceled){
                                    mBundle.putString(KEY_RESULT, downloadName);
                                    mBundle.putBoolean(KEY_SUCCESS,false);
                                    return mBundle;
                                }
                                out.write(buf, 0, len);
                                if (mPreviewHandler != null) {
                                    hasReadLength+=len;
                                    Message Msg = mPreviewHandler.obtainMessage();
                                    Msg.what = TpConst.PICTURE_DOWNLOADING;
                                    Msg.arg1 = hasReadLength;
                                    Msg.arg2 = length;
                                    mPreviewHandler.sendMessage(Msg);
                                }else {
                                    Log.e(TAG, "updateFrame: previewHandler is null" );
                                }
                            }
                            out.close();
                            mBundle.putString(KEY_RESULT, downloadName);
                            mBundle.putBoolean(KEY_SUCCESS,true);
                        }
                        else {
                            mBundle.putBoolean(KEY_SUCCESS,false);
                            mBundle.putString(KEY_RESULT, getContext().getString(R.string.canread) + " : " + sFile.canRead());
                        }

                        return mBundle;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mBundle.putBoolean(KEY_SUCCESS,false);
            mBundle.putString(KEY_RESULT, e.toString());
            return mBundle;
        }
        return null;
    }
}
