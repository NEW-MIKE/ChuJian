package com.glide.chujian.util;

import com.glide.chujian.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataBaseUtil {
    public static String DB_PATH = "/data/data/"+App.INSTANCE.getPackageName()+"/" + "databases/";
    public static String SRC_DB_FILE_NAME = Constant.DB_NAME;
    public static void copyDbFromAssert2Data() {
        File dir = new File(DB_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File datafile = new File(DB_PATH+SRC_DB_FILE_NAME);
        if (datafile.exists()){
            return;
        }
        String srcDbName = DB_PATH + SRC_DB_FILE_NAME;
        try {
            InputStream inputStream = App.INSTANCE.getResources().getAssets().open(SRC_DB_FILE_NAME);
            FileOutputStream fos = new FileOutputStream(srcDbName);
            byte[] buf = new byte[1024 * 8];
            int len = 0;
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
