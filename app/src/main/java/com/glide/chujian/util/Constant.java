package com.glide.chujian.util;

import java.io.File;

public class Constant {
    public static String PATH_LOG = FileUtils.createRootPath(FileUtils.TYPE_OTHER) + File.separator+"log";
    public static String PATH_CRASH = FileUtils.createRootPath(FileUtils.TYPE_OTHER) + File.separator+"crash";
    public static String PATH_PICTURES_ROOT = FileUtils.createRootPath(FileUtils.TYPE_PICTURE) + File.separator+"ToupTek";
    public static String PATH_VIDEO_ROOT = FileUtils.createRootPath(FileUtils.TYPE_VIDEO) + File.separator+"ToupTek";
    public static String PATH_PICTURES = PATH_PICTURES_ROOT  + File.separator+ "images" + File.separator;
    public static String PATH_VIDEOS = PATH_VIDEO_ROOT  + File.separator+ "videos" + File.separator;
    public static String PATH_PICTURES_TASK = PATH_PICTURES_ROOT  + File.separator+ "images_task" + File.separator;

    public static String CUSTOM_DATA = "";
    public static String NO_CUSTOM_DATA = " ";
    public static int LOCATION_CODE = 301;

    public static String SP_MAIN_CAMERA_ID = "main_camera_id";
    public static String SP_GUIDE_CAMERA_ID = "guide_camera_id";
    public static String SP_TELESCOPE_ID = "telescope_id";
    public static String SP_FILTER_WHEEL_ID = "filter_wheel_id";
    public static String SP_FOCUSER_ID = "focuser_id";
    public static String SP_FOCAL_LENGTH_MAIN_ID = "focal_length_main_id";
    public static String SP_FOCAL_LENGTH_GUIDE_ID = "focal_length_guide_id";
    public static String SP_MAIN_EXP_EDIT_ID = "main_exp_edit_id";
    public static String SP_GUIDE_EXP_EDIT_ID = "guide_exp_edit_id";
    public static String SP_MAIN_TEMP_EDIT_ID = "main_temp_edit_id";
    public static String SP_LATITUDE_ID = "latitude_id";
    public static String SP_LONGITUDE_ID = "longitude_id";
    public static String SP_DITHER_SCALE_ID = "dither_scale_id";
    public static String SP_SEARCH_HISTORY_ID = "search_history_id";
    public static String DB_NAME = "life_data.db";
    public static String DB_TABLE_NAME = "future_ask";
    public static String DB_TABLE_ME_TOPIC_NAME = "MeTopic";
    public static String DB_TABLE_ME_MODEL_NAME = "me_model";


    public static final int STAR_IDLE = 0;
    public static final int STAR_GUIDING = 1;
    public static final int STAR_GUIDING_STARTED = 2;
    public static final int STAR_GUIDING_STOPPED = 3;
    public static final int STAR_LOST = 4;
    public static final int STAR_CALIBRATING = 5;
    public static final int STAR_CALIBRATION_START = 6;
    public static final int STAR_CAPTURE_LOOPING = 7;
    public static final int STAR_SELECTED = 8;
    public static final int STAR_CALIBRATION_FAILED = 9;
}
