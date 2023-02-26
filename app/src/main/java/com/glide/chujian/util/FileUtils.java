package com.glide.chujian.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static String TAG = "FileUtils";
    public static int TYPE_PICTURE = 0;
    public static int TYPE_VIDEO = 1;
    public static int TYPE_OTHER = 2;

    private static List<File> mListFiles = new ArrayList<>();
    private static List<File> mListDirectories = new ArrayList<>();
    public static synchronized void loadAllFiles(File root) {
        mListFiles.clear();
        mListDirectories.clear();
        File files[] = root.listFiles();
        Log.e(TAG, "loadAllFiles: enter  1"+root.getAbsolutePath() );
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    mListDirectories.add(f);
                } else {
                    mListFiles.add(f);
                }
                Log.e(TAG, "loadAllFiles: enter"+f.getName() );
            }
        }
    }

    public static List<File> getListFiles(){
        return mListFiles;
    }

    private static String createPublicRootMOVIESPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    private static String createPublicRootPicturePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }
    private static String createPublicRootDownloadPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public static String createRootPath(int type){
        if (type == TYPE_VIDEO) {
            return createPublicRootMOVIESPath();
        }
        else if (type == TYPE_PICTURE){
            return createPublicRootDownloadPath();
            //return createPublicRootPicturePath();
        }
        else{
            return createPublicRootDownloadPath();
        }
    }

    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static boolean deleteFile(File file) throws IOException {
        return deleteFileOrDirectory(file);
    }

    public static boolean deleteFileOrDirectory(File file) throws IOException {
        try {
            if (file != null && file.isFile()) {
                return file.delete();
            }
            if (file != null && file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 删除空文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                }
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
}
