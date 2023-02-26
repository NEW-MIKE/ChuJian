package com.glide.chujian.util;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ActivityManager {
    private static final String TAG = "MyActivityManager";
    public static ActivityManager INSTANCE = new ActivityManager();
    private WeakReference<Activity> mCurrentActivityWeakRef;

    private ArrayList<Activity> mActivities = new ArrayList<>();

    public void addActivity(Activity activity){
        mActivities.add(activity);
    }

    public void removeActivity(Activity activity){
        mActivities.remove(activity);
    }

    public int getActivitySize(){
        return mActivities.size();
    }
    public ActivityManager() {
    }

    public static ActivityManager getINSTANCE() {
        return INSTANCE;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (mCurrentActivityWeakRef != null) {
            currentActivity = mCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        mCurrentActivityWeakRef = new WeakReference(activity);
    }
}