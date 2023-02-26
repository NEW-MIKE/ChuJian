package com.glide.chujian.util;

import android.util.Log;

import com.glide.chujian.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static SimpleDateFormat mMyLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");// 日志的输出格式
    private static SimpleDateFormat mLogFile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    private static String MYLOGFILEName = "Log.txt";
    public static void logi(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.i(tag,msg);
        }
    }
    public static void loge(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.e(tag,msg);
        }
    }

    public static void writeConnectLostFile(String text){
        writeContentToFile(text,"connectlost.txt");
    }

    public static void writeWorkStatusToFile(String text){
        writeContentToFile(text,"workstatus.txt");
    }
    public static void writeDeviceConnectToFile(String text){
        writeContentToFile(text,"deviceConnect.txt");
    }
    public static void writeConstraintToFile(String text){
        writeContentToFile(text,"constraint.txt");
    }
    public static void writeLowMemoryToFile(String text){
        writeContentToFile(text,"lowmemory.txt");
    }
    public static void writeGuideFps0ToFile(String text){
        writeContentToFile(text,"fps0.txt");
    }
    public static void writeIdNullToFile(String text){
        writeContentToFile(text,"IdNull.txt");
    }
    public static void writeWarnErrorToFile(String text){
        writeContentToFile(text,"warnerror.txt");
    }
    public static void writeLibToFile(String text){
        writeContentToFile(text,"lib.txt");
    }
    public static void writeDebugFile(String text){
        writeContentToFile(text,"debug.txt");
    }
    public static void writeTouchDelayFile(String text){
        writeTrueContentToFile(text,"touchdelay.txt");
    }

    public static void writeContentToFile(String text,String fileName) {
        writeTrueContentToFile(text, fileName);
    }

    public static void writeLogtoFile(String text) {
        writeContentToFile(text,MYLOGFILEName);
    }

    private static void writeTrueContentToFile(String text,String fileName) {
        Date nowtime = new Date();
        String needWriteFile = mLogFile.format(nowtime);
        String needWriteMessage = mMyLogSdf.format(nowtime) + "    " + text;
        File dirsFile = new File(Constant.PATH_LOG);
        if (!dirsFile.exists()){
            dirsFile.mkdirs();
        }
        //Log.i("创建文件","创建文件");
        File file = new File(dirsFile.toString(), needWriteFile+fileName);// MYLOG_PATH_SDCARD_DIR
        if (!file.exists()) {
            try {
                //在指定的文件夹中创建文件
                file.createNewFile();
            } catch (Exception e) {
            }
        }

        try {
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
