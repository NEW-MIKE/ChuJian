package com.glide.chujian.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.glide.chujian.App;

public class NavigationUtils {

    public static int getNavigationBarHeightVisible(Activity activity){
        if (hasNavigationBar(App.INSTANCE) || isShowNav(activity)){
            return getNavigationBarHeight();
        }
        return 0;
    }

    private static int getNavigationBarHeight() {
        int result = 0;
        int resourceId = App.INSTANCE.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = App.INSTANCE.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取虚拟导航栏(NavigationBar)是否显示
     * @return true 表示虚拟导航栏显示，false 表示虚拟导航栏未显示
     */
    private static boolean hasNavigationBar(Context context){
        if (getNavigationBarHeight() == 0){
            return false;
        }
        if (RomUtils.checkIsHuaweiRom() && isHuaWeiHideNav(context)){
            return false;
        }
        if (RomUtils.checkIsMiuiRom() && isMiuiFullScreen(context)){
            return false;
        }
        if (RomUtils.checkIsVivoRom() && isVivoFullScreen(context)){
            return false;
        }
        return isHasNavigationBar(context);
    }
    /**
     * @return 是否显示底部导航
     */
    public static boolean isShowNav(Activity activity) {
        boolean flag = false;
        View content = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if ( null  != content) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
/*
            if (!isLandscape()) {
            } else {
                int right = content.getRight();
                if (right != point.y) {
                    flag = true;
                }
            }
*/
            int bottom = content.getBottom();// 页面的底部
            if (bottom != point.y) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 华为手机是否隐藏了虚拟导航栏
     * @return true 表示隐藏了，false 表示未隐藏
     */
    private static boolean isHuaWeiHideNav(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Settings.System.getInt(context.getContentResolver(), "navigationbar_is_min", 0) !=0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(), "navigationbar_is_min", 0) !=0;
        }
    }

    /**
     * 小米手机是否开启手势操作
     * @return true 表示使用的是手势，false 表示使用的是虚拟导航栏(NavigationBar)，默认是false
     */
    private static boolean isMiuiFullScreen(Context context)
    {
        return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }

    /**
     * Vivo手机是否开启手势操作
     * @return true 表示使用的是手势，false 表示使用的是虚拟导航栏(NavigationBar)，默认是false
     */
    private static boolean isVivoFullScreen(Context context)
    {
        return Settings.Secure.getInt(context.getContentResolver(), "navigation_gesture_on", 0) != 0;
    }

    /**
     * 根据屏幕真实高度与显示高度，判断虚拟导航栏是否显示
     * @return true 表示虚拟导航栏显示，false 表示虚拟导航栏未显示
     */
    private static boolean isHasNavigationBar(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = Resources.getSystem().getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realDisplayMetrics);
        }
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        display.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        // 部分无良厂商的手势操作，显示高度 + 导航栏高度，竟然大于物理高度，对于这种情况，直接默认未启用导航栏
        if (displayHeight > displayWidth) {
            if (displayHeight + getNavigationBarHeight() > realHeight) {
                return false;
            }
        } else {
            if (displayWidth + getNavigationBarHeight() > realWidth) {
                return false;
            }
        }
        return (realWidth - displayWidth > 0 || realHeight - displayHeight > 0);
    }
}