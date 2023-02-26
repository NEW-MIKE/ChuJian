package com.glide.chujian.util;

import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtils {
    private String TAG = "RomUtils--->";

    public static double getEmuiVersion(){
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    private static String getSystemProperty(String propName){
        String line = "";
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop $propName");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (Exception ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public static boolean checkIsHuaweiRom(){
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    public static boolean checkIsMiuiRom(){
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static boolean checkIsMeizuRom(){
        String systemProperty = getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(systemProperty)) return false;
        else {
            return systemProperty.contains("flyme") || systemProperty.toLowerCase().contains("flyme");
        }
    }

    public static boolean checkIs360Rom(){
        return Build.MANUFACTURER.contains("QiKU") || Build.MANUFACTURER.contains("360");
    }

    public static boolean checkIsOppoRom(){
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo");
    }

    public static boolean checkIsVivoRom(){
        return Build.MANUFACTURER.contains("VIVO") || Build.MANUFACTURER.contains("vivo");
    }
}
