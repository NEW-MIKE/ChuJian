package com.glide.chujian.util;

import java.text.DecimalFormat;

public class StringUtil {
    public static String formatDoubleStringSize(Double value,int size){
        String newValue = String.format("%.2f", value);
        int delta = size - newValue.length();
        String output = "";
        for (int i = 0;i < delta;i++){
            output +="0";
        }
        output += newValue;
        return output;
    }

    public static String formatTargetTemperature(Double value){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String strPrice = decimalFormat.format(value);//返回字符串
        return strPrice;
    }
}
