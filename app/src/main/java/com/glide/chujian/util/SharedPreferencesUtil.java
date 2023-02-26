package com.glide.chujian.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil mPrefsUtil;
    public Context mContext;
    public SharedPreferences mPrefs;
    public SharedPreferences.Editor mEditor;

    public synchronized static SharedPreferencesUtil getInstance() {
        return mPrefsUtil;
    }

    public static void init(Context context, String prefsname, int mode) {
        mPrefsUtil = new SharedPreferencesUtil();
        mPrefsUtil.mContext = context;
        mPrefsUtil.mPrefs = mPrefsUtil.mContext.getSharedPreferences(prefsname, mode);
        mPrefsUtil.mEditor = mPrefsUtil.mPrefs.edit();
    }

    private SharedPreferencesUtil() {
    }

    public String getString(String key, String defaultVal) {
        return this.mPrefs.getString(key, defaultVal);
    }
    public String getString(String key) {
        return this.mPrefs.getString(key, null);
    }

    public SharedPreferencesUtil putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.apply();
        return this;
    }

    public Double getDouble(String key, Double defaultVal) {
        String value = this.mPrefs.getString(key, defaultVal+"");
        return Double.parseDouble(value);
    }

    public SharedPreferencesUtil putDouble(String key, Double value) {
        return putString(key, value+"");
    }

    public void commit() {
        mEditor.commit();
    }

    public List<String> readSearchHistory(String historyKey){
        String value = this.mPrefs.getString(historyKey, "");
        String[] historys = value.split(";");
        List<String> list = new ArrayList<String>();
        if(historys.length > 0){
            for (int i = 0; i < historys.length; i++) {
                if(historys[i] != null && historys[i].length()>0){
                    list.add(historys[i]);
                }
            }
        }
        return list;
    }

    public void deleteSearchHistory(String historyKey){
        mEditor.putString(historyKey, "");
        mEditor.apply();
    }

    public void insertSearchHistory(String historyKey,String value){
        String lastValue = this.mPrefs.getString(historyKey, "");
        String saveValue = lastValue +";"+value;
        mEditor.putString(historyKey, saveValue);
        mEditor.apply();
    }

}
