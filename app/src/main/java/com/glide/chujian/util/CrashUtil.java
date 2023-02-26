package com.glide.chujian.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashUtil {
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat mLogfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    private static String MYLOGFILEName = "crash.txt";

    public static void writeLogtoFile(String text) {
        Date nowtime = new Date();
        String needWriteFile = mLogfile.format(nowtime);
        String needWriteMessage = myLogSdf.format(nowtime) + "    " + text;
        File dirsFile = new File(Constant.PATH_CRASH);
        if (!dirsFile.exists()){
            dirsFile.mkdirs();
        }
        //Log.i("创建文件","创建文件");
        File file = new File(dirsFile.toString(), needWriteFile+MYLOGFILEName);// MYLOG_PATH_SDCARD_DIR
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
