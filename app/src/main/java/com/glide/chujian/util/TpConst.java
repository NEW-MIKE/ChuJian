package com.glide.chujian.util;

/**
 * Created by hyayh on 2017/11/29.
 */

public class TpConst {
    private static final int CONST = 0x0547;
    private static final int MSG_CONST = CONST + 0x21;//0x81 - 0x21  = 96
    private static final int RET_CONST = CONST + 0x81;
    public static final int MSG_APP_INFO = MSG_CONST;
    public static final int MSG_ALL_DEVICES = MSG_CONST + 1;
    public static final int MSG_CURRENT_DEVICES = MSG_CONST + 2;
    public static final int MSG_APP_STATE = MSG_CONST + 3;
    public static final int MSG_WORK_STATE = MSG_CONST + 4;
    public static final int MSG_NEW_DEVICE = MSG_CONST + 5;
    public static final int MSG_REMOVE_DEVICE = MSG_CONST + 6;
    public static final int MSG_DEVICE_SELECTED = MSG_CONST + 7;
    public static final int MSG_DEVICE_CONNECTED = MSG_CONST + 8;
    public static final int MSG_CAPTURE_STARTED = MSG_CONST + 9;
    public static final int MSG_CAPTURE_STOPPED = MSG_CONST + 10;
    public static final int MSG_CAPTURE_LOOPING = MSG_CONST + 11;
    public static final int MSG_CAPTURE_FRAME_SAVED = MSG_CONST + 12;
    public static final int MSG_RSTP_SOURCE_CHANGED = MSG_CONST + 13;
    public static final int MSG_RTSP_RES_CHANGED = MSG_CONST + 14;
    public static final int MSG_RTSP_READY = MSG_CONST + 15;
    public static final int MSG_CAPTURE_MODE_CHANGED = MSG_CONST + 16;
    public static final int MSG_CALIBRATION_STARTED = MSG_CONST + 17;
    public static final int MSG_CALIBRATION_DONE = MSG_CONST + 18;
    public static final int MSG_CALIBRATION_FAILED = MSG_CONST + 19;
    public static final int MSG_CALIBRATION_DATA_FLIPPED = MSG_CONST + 20;
    public static final int MSG_CALIBRATING = MSG_CONST + 21;
    public static final int MSG_STAR_SELECTED = MSG_CONST + 22;
    public static final int MSG_STAR_LOST = MSG_CONST + 23;
    public static final int MSG_GUIDING_STARTED = MSG_CONST + 24;
    public static final int MSG_GUIDING_PAUSED = MSG_CONST + 25;
    public static final int MSG_UPDATE = MSG_CONST + 26;
    public static final int MSG_GUIDING_STOPPED = MSG_CONST + 27;
    public static final int MSG_GUIDING_RESUMED = MSG_CONST + 28;
    public static final int MSG_GUIDING_DITHERED = MSG_CONST + 29;
    public static final int MSG_GUIDING = MSG_CONST + 30;
    public static final int MSG_FILTER_WHEEL_UNIDIRECTIONAL_CHANGED = MSG_CONST + 31;
    public static final int MSG_ERROR = MSG_CONST + 32;
    public static final int MSG_RESOLUTION = MSG_CONST + 33;
    public static final int MSG_FILTER_WHEEL_SLOT_NUMBER_CHANGED = MSG_CONST + 34;
    public static final int MSG_CHART_UPDATE = MSG_CONST + 35;
    public static final int MSG_SERVER_STATUS = MSG_CONST + 37;
    public static final int MSG_FILTER_WHEEL_POSITION_CHANGED = MSG_CONST + 38;
    public static final int MSG_FILTER_WHEEL_NAME_CHANGED = MSG_CONST + 39;
    public static final int MSG_RTSP_SOURCE_CHANGED = MSG_CONST + 41;
    public static final int MSG_SETTLE_START = MSG_CONST + 42;
    public static final int MSG_IMAGESAVED = MSG_CONST + 43;
    public static final int MSG_SETTLE_DONE = MSG_CONST + 44;
    public static final int MSG_SETTLING = MSG_CONST + 45;
    public static final int MSG_WORK_STATES = MSG_CONST + 46;
    public static final int MSG_ALERT = MSG_CONST + 47;
    public static final int MSG_CONNECTION_LOST = MSG_CONST + 48;
    public static final int MSG_SLEW_STARTED = MSG_CONST + 49;
    public static final int MSG_SLEW_STOPPED = MSG_CONST + 50;
    public static final int RESOLUTION_CHANGED = MSG_CONST + 51;
    public static final int GAIN_CHANGED = MSG_CONST + 52;
    public static final int EXPOSURE_CHANGED = MSG_CONST + 53;
    public static final int BINNING_CHANGED = MSG_CONST + 54;
    public static final int TARGET_TEMPERATURE_CHANGED = MSG_CONST + 55;
    public static final int MOUNT_SPEED_CHANGED = MSG_CONST + 56;
    public static final int GUIDING_DITHERED = MSG_CONST + 57;
    public static final int PICTURE_DOWNLOADING = MSG_CONST + 58;
    public static final int MSG_CONNECTION_SUCCESS = MSG_CONST + 59;
    public static final int MSG_DC_OUTPUT_CHANGED = MSG_CONST + 61;
    public static final int MSG_FOCAL_LENGTH_CHANGED = MSG_CONST + 62;
    public static final int MSG_COOLING_CHANGED = MSG_CONST + 63;
    public static final int MSG_FAN_CHANGED = MSG_CONST + 64;
    public static final int MSG_GAIN_MODE_CHANGED = MSG_CONST + 65;
    public static final int MSG_LOW_NOISE_CHANGED = MSG_CONST + 66;
    public static final int MSG_MOUNT_BAUD_RATE_CHANGED = MSG_CONST + 67;
    public static final int MSG_MOUNT_TRACKING_CHANGED = MSG_CONST + 68;
    public static final int MSG_MOUNT_TRACKING_MODE_CHANGED = MSG_CONST + 69;
    public static final int MSG_MOUNT_RA_MAX_CHANGED = MSG_CONST + 70;
    public static final int MSG_MOUNT_DEC_MAX_CHANGED = MSG_CONST + 71;
    public static final int MSG_DITHER_SCALE_CHANGED = MSG_CONST + 72;
    public static final int MSG_DITHER_MODE_CHANGED = MSG_CONST + 73;
    public static final int MSG_DITHER_RA_ONLY_CHANGED = MSG_CONST + 74;
    public static final int MSG_BROWSER_IMAGE_IS_VALID = MSG_CONST + 75;
    public static final int MSG_MOUNT_PARK_CHANGED = MSG_CONST + 76;
    public static final int MSG_PARAMETER_ALL_LOAD = MSG_CONST + 77;
    public static final int MSG_STAR_PROFILE_UPDATE = MSG_CONST + 78;
    public static final int MSG_ALL_CACHE_LOADED = MSG_CONST + 79;
    public static final int MSG_FOCUSER_REVERSED_CHANGED = MSG_CONST + 80;
    public static final int MSG_FOCUSER_MAX_CHANGED = MSG_CONST + 81;
    public static final int MSG_FOCUSER_TARGET_POSITION_CHANGED = MSG_CONST + 82;
    public static final int MSG_FOCUSER_CURRENT_POSITION_CHANGED = MSG_CONST + 83;
    public static final int MSG_FOCUSER_MODE_CHANGED = MSG_CONST + 84;
    public static final int MSG_FOCUSER_STEP_CHANGED = MSG_CONST + 85;
    public static final int MSG_FOCUSER_BEEP_CHANGED = MSG_CONST + 86;
    public static final int MSG_GUIDE_PARAM_CHANGED = RET_CONST + 2;
    public static final int MSG_STARTRECORD = RET_CONST + 6;
    public static final int MSG_REC_SUCCESS = RET_CONST + 7;
    public static final int MSG_REC_FAILED = RET_CONST + 8;
    public static final int MSG_CAP_SUCCESS = RET_CONST + 9;
    public static final int MSG_CAP_FAILED = RET_CONST + 10;
    public static final int MSG_CAP_PROCESSED = RET_CONST + 11;
    public static final int REQUEST_FILEVIEW = 0x01;
    public static final int DFT_MAX_PROGRESS = 1000;
    public static final String PREF_CONFIG = "Config_Info";
    public static final String PREF_PREFIX = "Config_Prefx";
    public static final String PREF_IMGTYPE = "Config_ImgType";
    public static final String PREF_IMGFMT = "Config_ImgFmt";
    public static final String PREF_IMGDIR = "Config_ImgDir";
    public static final String PREF_FR = "Config_fe";
    public static final String PREF_SAVEINSD = "Config_SaveLoc";
    public static final String PREF_SAVEWITHLAYER = "Config_SaveWithLayer";
    public static final String PREF_SAVEWITHBURN = "Config_SaveWithBurn";
    public static final String PREF_CALIRATE = "Config_CaliRate";
    public static final String PREF_CALIUNIT = "Config_CaliUnit";
    public static final String PREF_CALIINDEX = "Config_CaliIndex";
    public static final String PREF_CAMNAME = "camera_name";
    public static final String PREF_IMAGEINDEX = "image_index";
    public static final String PREF_ROI_WB = "roi_wb";
    public static final String PREF_ROI_AEP = "roi_exp";
    public static final String KEY_ID = "CameraID";
    public static final String STR_DEFAULTID = "CameraDemo";
    public static final String PREF_CALIB = "calibration_items";

    public static final int REQ_PERMISSIONS = 100;
    public static final int REQ_CAMERA = 101;
    public static final int REQ_STORAGE = 102;
    public static final int REQ_LOCATION = 103;


    public enum SHAPE_TYPE {
        TYPE_NONE,
        TYPE_DOT,
        TYPE_LINE,
        TYPE_DOUBLELINE,
        TYPE_ARROWLINE,
        TYPE_CORNER,
        TYPE_RECTANGLE,
        TYPE_POLYGON,
        TYPE_CIRCLE,
        TYPE_TEXT,
        TYPE_SCALEBAR,
        TYPE_CALIBRATION,
        TYPE_FOUCSGRAPHIC;
    }

    public enum GRAPHIC_THICKNESS {
        THICKNESS_S(0),
        THICKNESS_M(1),
        THICKNESS_L(2),
        THICKNESS_XL(3),
        THICKNESS_XXL(4);
        private final int mCode;

        private GRAPHIC_THICKNESS(int code) {
            this.mCode = code;
        }

        public int getCode() {
            return mCode;
        }

        @Override
        public String toString() {
            return String.valueOf(this.mCode);
        }
    }

    public enum GRAPHIC_LINECOLOR {
        COLOR_LINE1(0),
        COLOR_LINE2(1),
        COLOR_LINE3(2),
        COLOR_LINE4(3),
        COLOR_LINE5(4),
        COLOR_LINE6(5),
        COLOR_LINE7(7);
        private final int mCode;

        private GRAPHIC_LINECOLOR(int code) {
            this.mCode = code;
        }

        public int getCode() {
            return mCode;
        }

        @Override
        public String toString() {
            return String.valueOf(this.mCode);
        }
    }

    public enum POINT_STATE {
        STATE_NORMAL,
        STATE_ADD,
        STATE_SUB;
    }

    public enum ePage {
        PAGE_NONE,
        PAGE_CAMLIST,
        PAGE_TOUPVIEW,
        PAGE_BROWSER,
        PAGE_INFO
    }

    public enum eState {
        STATE_UNCERTAIN,
        STATE_HIDE,
        STATE_SHOW
    }
}
