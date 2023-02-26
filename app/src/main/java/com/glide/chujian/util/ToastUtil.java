package com.glide.chujian.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.glide.chujian.App;
import com.glide.chujian.R;

public class ToastUtil {
    private static String TAG = "ToastUtil";
    public static Toast mToast = null;
    public static TextView textView = null;
    public static ViewGroup toastView = null;
    private static App mContext;
    public static void init(App application) {
        mContext = application;
    }
    public static void showToast(String content, boolean longTime) {
        Log.e(TAG, "showToast: "+content+mToast);
        int type = longTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        if (mToast == null) {
            mToast = Toast.makeText(mContext, content, type);
            toastView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout
                    .layout_toast, null, false);
            textView = (TextView) toastView.findViewById(R.id.message);
        } else {
            cancelToast();
            mToast = Toast.makeText(mContext, content, type);
        }
        mToast.setView(toastView);
        textView.setText(content);
        mToast.show();
    }

    public static void cancelToast(){
        Log.e(TAG, "cancelToast: showToast ");
        if(mToast !=null){
            mToast.cancel();
        }
    }
}
