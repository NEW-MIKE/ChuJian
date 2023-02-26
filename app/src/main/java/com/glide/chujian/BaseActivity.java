package com.glide.chujian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.glide.chujian.db.AstroLibraryCache;
import com.glide.chujian.db.DBOpenHelper;
import com.glide.chujian.util.ActivityManager;
import com.glide.chujian.util.CheckConnectProcess;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.KeyboardStateObserver;
import com.glide.chujian.util.LogUtil;
import com.glide.chujian.util.NavigationUtils;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.util.SoftInputUtil;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.view.NumberPop;

import me.jessyan.autosize.AutoSizeCompat;

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG = "BaseActivity";

    private WifiChangeReceiver mWifiChangeReceiver;
    public CheckConnectProcess mCheckConnectProcess;
    private IntentFilter mIntentFilter;
    public NumberPop mNumberPop;
    public boolean mIsNumberInputShow = false;
    protected DBOpenHelper mDBOpenHelper;
    protected SQLiteDatabase mDb;
    public abstract void updateWifiName(String wifi_name);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDBOpenHelper = new DBOpenHelper(this, Constant.DB_NAME,null,1);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ActivityManager.getINSTANCE().setCurrentActivity(this);
        ActivityManager.getINSTANCE().addActivity(this);
        mCheckConnectProcess = new CheckConnectProcess(this);
        mWifiChangeReceiver = new WifiChangeReceiver();
        mWifiChangeReceiver.setActivity(this);
        mNumberPop = new NumberPop(this);
        mNumberPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mNumberPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mNumberPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getWindow().getDecorView().setY(0);
            }
        });
    }

    public void updateRaDecView() {

    }
    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        return super.getResources();
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mWifiChangeReceiver, mIntentFilter);
        KeyboardStateObserver.getKeyboardStateObserver(this).
                setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
                    @Override
                    public void onKeyboardShow() {
                    }
                    @Override
                    public void onKeyboardHide() {
                        View view = getCurrentFocus();
                        if (view instanceof EditText) {
                            view.clearFocus();
                        }
                    }
                });
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityManager.getINSTANCE().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
        mCheckConnectProcess.dismissConnectedDialog();
        mCheckConnectProcess.stopConnectTimer();
        ToastUtil.cancelToast();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNumberPop.dismiss();
        mIsNumberInputShow = false;
        unregisterReceiver(mWifiChangeReceiver);
    }

    public void locationEvent(Location location){
        if (AstroLibraryCache.getAstroLibraryCache().size() == 0) {
            if (location != null) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Log.e(TAG, "run: location.getLatitude()" + location.getLatitude());
                        SharedPreferencesUtil.getInstance().putDouble(Constant.SP_LATITUDE_ID, location.getLatitude());
                        SharedPreferencesUtil.getInstance().putDouble(Constant.SP_LONGITUDE_ID, location.getLongitude());
                        Log.e(TAG, "run: new AstroLibraryCache location != null" );
                        new AstroLibraryCache(App.INSTANCE).loadAstroLibraryCache(location.getLatitude(), location.getLongitude());
                    }
                }.start();
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        SharedPreferencesUtil.getInstance().putDouble(Constant.SP_LATITUDE_ID, (double) -1);
                        SharedPreferencesUtil.getInstance().putDouble(Constant.SP_LONGITUDE_ID, (double) -1);
                        Log.e(TAG, "run: new AstroLibraryCache location == null" );
                        new AstroLibraryCache(App.INSTANCE).loadAstroLibraryCache(-1, -1);
                    }
                }.start();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (ActivityManager.getINSTANCE().getActivitySize() > 1){
            super.onBackPressed();
        }else {
            Intent intent= new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getINSTANCE().removeActivity(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory: " );
        LogUtil.writeLowMemoryToFile("");
    }

    public void hideDiySoftInput(){
        mNumberPop.dismiss();
    }

    public void showSoftStringInput(EditText editTextView,boolean positive,boolean hasDot){
        editTextView.setShowSoftInputOnFocus(false);
        editTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mNumberPop.supportDotInput(hasDot);
                mNumberPop.supportNegativeInput(!positive);
                if (!mNumberPop.isShowing()) {
                    EditText editText = editTextView;
                    mNumberPop.showAtLocation(editText, Gravity.BOTTOM | Gravity.LEFT,
                            0,
                            0);
                    Rect rect = new Rect();
                    editText.getGlobalVisibleRect(rect);
                    int editTextMarginBottom = getWindow().getDecorView().getHeight() - rect.bottom;
                    int delta = mNumberPop.getContentView().getMeasuredHeight() - editTextMarginBottom + NavigationUtils.getNavigationBarHeightVisible(BaseActivity.this);
                    if (delta > 0) {
                        getWindow().getDecorView().setY(-delta);
                    }
                    mNumberPop.setNumberListener(new NumberPop.NumberListener() {
                        @Override
                        public void onChooseNum(String strNum) {
                            String strProductId = editText.getText().toString();
                            int selectionStart = editText.getSelectionStart();
                            String value = SoftInputUtil.formatStringSoftInput(strNum, strProductId, selectionStart);
                            editText.setText(value);
                            if (!strProductId.equals(value)){
                                selectionStart += 1;
                            }
                            editText.setSelection(selectionStart);
                        }

                        @Override
                        public void onDelNum() {
                            String strProductId = editText.getText().toString();
                            if (!TextUtils.isEmpty(strProductId)) {
                                int selectionStart = editText.getSelectionStart() - 1 ;
                                selectionStart = selectionStart < 0 ? 0 : selectionStart;
                                editText.setText(strProductId.substring(0, selectionStart)
                                        +strProductId.substring(editText.getSelectionStart()));
                                editText.setSelection(selectionStart);
                            }
                        }

                        @Override
                        public void onSureNum() {
                            editText.clearFocus();
                            getWindow().getDecorView().setY(0);
                        }
                    });
                }
                return false;
            }
        });
    }
    public void showSoftInput(EditText editTextView,boolean positive,boolean hasDot){
        editTextView.setShowSoftInputOnFocus(false);
        editTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mNumberPop.supportDotInput(hasDot);
                mNumberPop.supportNegativeInput(!positive);
                if (!mNumberPop.isShowing()) {
                    EditText editText = editTextView;
                    mNumberPop.showAtLocation(editText, Gravity.BOTTOM | Gravity.LEFT,
                            0,
                            0);
                    Rect rect = new Rect();
                    editText.getGlobalVisibleRect(rect);
                    int editTextMarginBottom = getWindow().getDecorView().getHeight() - rect.bottom;
                    int delta = mNumberPop.getContentView().getMeasuredHeight() - editTextMarginBottom + NavigationUtils.getNavigationBarHeightVisible(BaseActivity.this);
                    if (delta > 0) {
                        getWindow().getDecorView().setY(-delta);
                    }
                    mNumberPop.setNumberListener(new NumberPop.NumberListener() {
                        @Override
                        public void onChooseNum(String strNum) {
                            String strProductId = editText.getText().toString();
                            int selectionStart = editText.getSelectionStart();
                            String value = "";
                            if (positive && !hasDot) {
                                value = SoftInputUtil.formatPositiveNumberNoDot(strNum, strProductId, selectionStart);
                            }else if (positive && hasDot){
                                value = SoftInputUtil.formatPositiveNumberWithDot(strNum, strProductId, selectionStart);
                            }else if (hasDot){
                                value = SoftInputUtil.formatNegativeNumberWithDot(strNum, strProductId, selectionStart);
                            }else {
                                value = SoftInputUtil.formatNegativeNumberNoDot(strNum, strProductId, selectionStart);
                            }
                            editText.setText(value);
                            if (!strProductId.equals(value)){
                                selectionStart += 1;
                            }
                            editText.setSelection(selectionStart);
                        }

                        @Override
                        public void onDelNum() {
                            String strProductId = editText.getText().toString();
                            if (!TextUtils.isEmpty(strProductId)) {
                                int selectionStart = editText.getSelectionStart() - 1 ;
                                selectionStart = selectionStart < 0 ? 0 : selectionStart;
                                editText.setText(strProductId.substring(0, selectionStart)
                                        +strProductId.substring(editText.getSelectionStart()));
                                editText.setSelection(selectionStart);
                            }
                        }

                        @Override
                        public void onSureNum() {
                            editText.clearFocus();
                            getWindow().getDecorView().setY(0);
                        }
                    });
                }
                return false;
            }
        });
    }

    public void showSoftInputNoEvent(EditText editTextView,boolean positive,boolean hasDot){
        editTextView.setShowSoftInputOnFocus(false);
        mNumberPop.supportDotInput(hasDot);
        mNumberPop.supportNegativeInput(!positive);
        if (!mNumberPop.isShowing()) {
            EditText editText = editTextView;
            mNumberPop.showAtLocation(editText, Gravity.BOTTOM | Gravity.LEFT,
                    0,
                    0);
            mIsNumberInputShow = true;
            Rect rect = new Rect();
            editText.getGlobalVisibleRect(rect);
            int editTextMarginBottom = getWindow().getDecorView().getHeight() - rect.bottom;
            int delta = mNumberPop.getContentView().getMeasuredHeight() - editTextMarginBottom+ NavigationUtils.getNavigationBarHeightVisible(this);
            if (delta > 0) {
                getWindow().getDecorView().setY(-delta);
            }
            mNumberPop.setNumberListener(new NumberPop.NumberListener() {
                @Override
                public void onChooseNum(String strNum) {
                    String strProductId = editText.getText().toString();
                    int selectionStart = editText.getSelectionStart();
                    String value = "";
                    if (positive && !hasDot) {
                        value = SoftInputUtil.formatPositiveNumberNoDot(strNum, strProductId, selectionStart);
                    }else if (positive && hasDot){
                        value = SoftInputUtil.formatPositiveNumberWithDot(strNum, strProductId, selectionStart);
                    }else if (hasDot){
                        value = SoftInputUtil.formatNegativeNumberWithDot(strNum, strProductId, selectionStart);
                    }else {
                        value = SoftInputUtil.formatNegativeNumberNoDot(strNum, strProductId, selectionStart);
                    }
                    editText.setText(value);
                    if (!strProductId.equals(value)){
                        selectionStart += 1;
                    }
                    editText.setSelection(selectionStart);
                }

                @Override
                public void onDelNum() {
                    String strProductId = editText.getText().toString();
                    if (!TextUtils.isEmpty(strProductId)) {
                        int selectionStart = editText.getSelectionStart() - 1 ;
                        selectionStart = selectionStart < 0 ? 0 : selectionStart;
                        editText.setText(strProductId.substring(0, selectionStart)
                                +strProductId.substring(editText.getSelectionStart()));
                        editText.setSelection(selectionStart);
                    }
                }

                @Override
                public void onSureNum() {
                    editText.clearFocus();
                    getWindow().getDecorView().setY(0);
                    mIsNumberInputShow = false;
                }
            });
        }
    }
}