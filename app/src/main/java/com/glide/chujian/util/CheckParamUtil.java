package com.glide.chujian.util;

import android.app.Activity;

import com.glide.chujian.App;
import com.glide.chujian.R;

public class CheckParamUtil {
    public static boolean isRangeLegal(Activity activity,String text,Double start,Double end,Double scale,String unit){
        String value = text.trim();
        Double input;
        try {
            input = Double.valueOf(value) * scale;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        String localUnit = "";
        if (!unit.equals("")){
            localUnit = ""+unit+"";
        }
        if (text.equals("")){
            ToastUtil.showToast(App.INSTANCE.getResources().getString(R.string.legal_input_range)+"["+start/scale+", "+end/scale+"]"+localUnit, false);
            return false;
        }
        if (input >= start && input <= end){
            return true;
        }else {
            ToastUtil.showToast( App.INSTANCE.getResources().getString(R.string.legal_input_range)+"["+start/scale+", "+end/scale+"]"+localUnit, false);
        }
        return false;
    }
    public static boolean isRangeLegalInt(Activity activity,String text,Double start,Double end,Double scale,String unit){
        String value = text.trim();
        Double input;
        try {
            input = Double.valueOf(value) * scale;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        String localUnit = "";
        if (!unit.equals("")){
            localUnit = ""+unit+"";
        }
        int startValue = (int) (start/scale);
        int endValue = (int) (end/scale);
        if (text.equals("")){
            ToastUtil.showToast(App.INSTANCE.getResources().getString(R.string.legal_input_range)+"["+startValue+", "+endValue+"]"+localUnit, false);
            return false;
        }
        if (input >= start && input <= end){
            return true;
        }else {
            ToastUtil.showToast( App.INSTANCE.getResources().getString(R.string.legal_input_range)+"["+startValue+", "+endValue+"]"+localUnit, false);
        }
        return false;
    }

    public static boolean isRangeLegalNoToast(Activity activity,String text,Double start,Double end,Double scale,String unit){
        String value = text.trim();
        Double input;
        try {
            input = Double.valueOf(value) * scale;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        String localUnit = "";
        if (!unit.equals("")){
            localUnit = ""+unit+"";
        }
        if (text.equals("")){
            return false;
        }
        if (input >= start && input <= end){
            return true;
        }else {
        }
        return false;
    }
}
