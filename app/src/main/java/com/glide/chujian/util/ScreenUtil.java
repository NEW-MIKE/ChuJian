package com.glide.chujian.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ScreenUtil {
    private static DisplayMetrics sDM = Resources.getSystem().getDisplayMetrics();

    public static int getScreenWidth() {
        return sDM.widthPixels;
    }

    public static int getScreenHeight(){
        return sDM.heightPixels;
    }

    public static float getDensity(){
        return sDM.density;
    }

/*    fun dpToPx(dp: Int): Int {
        return dpToPx(dp.toFloat())
    }*/

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, sDM);
    }

    public static int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, sDM);
    }
    public static int sp2px(float spValue) {
        return (int) ((spValue * sDM.scaledDensity + 0.5f) * ((float)pxToDp(getScreenWidth())/1200f));
    }
    public static int pxToDp(int px) {
        return Math.round(px / getDensity());
    }

    public static int dpToPx(int dps) {
        return Math.round(Resources.getSystem().getDisplayMetrics().density * dps);
    }

    public static int androidAutoSizeDpToPx(int dp){
        return (int) (((float)pxToDp(getScreenWidth())/1200f) * (float) dpToPx(dp));
    }
}
