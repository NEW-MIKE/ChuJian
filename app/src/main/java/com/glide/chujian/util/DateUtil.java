package com.glide.chujian.util;

import android.util.Log;

import com.glide.chujian.model.DateItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static String TAG = "DateUtil";
    /*函数功能是为了获取当前的指定时间对应于utc的时间，就直接转化为时间戳，从时间戳直接计算，时间戳的组成分为，我这个时区的年月日加上18时，得到一个时间戳。
     * 再加上一个12个小时，得到一个时间戳，再利用这两个时间戳减去时区的差距，得到两个新的时间戳，通过这两个时间戳得到两个时间日期*/
    public static DateItem getUTCDateTime() {
        DateItem date = new DateItem();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance() ;
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        date.mYear = cal.get(Calendar.YEAR);
        date.mMonth = cal.get(Calendar.MONTH)+1;
        date.mDay = cal.get(Calendar.DAY_OF_MONTH);
        date.mHour = cal.get(Calendar.HOUR_OF_DAY);
        date.mMin = cal.get(Calendar.MINUTE);
        date.mSec = cal.get(Calendar.SECOND);
       // Log.e(TAG, "getUTCTimeNumList: date.hour"+date.hour+"  date.min:"+date.min+"zoneOffset"+zoneOffset );
        return date ;
    }

    public static String getCurrentDateNumber(){
        DateItem date = new DateItem();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance() ;
        date.mYear = cal.get(Calendar.YEAR);
        date.mMonth = cal.get(Calendar.MONTH)+1;
        date.mDay = cal.get(Calendar.DAY_OF_MONTH);
        String mYear = date.mYear+"";
        String mMonth = date.mMonth+"";
        String mDay = date.mDay+"";

        if (date.mMonth < 10){
            mMonth = "0"+date.mMonth+"";
        }
        if (date.mDay < 10){
            mDay = "0"+date.mDay+"";
        }

        return date.mYear+"-"+mMonth+"-"+mDay ;
    }
    public static double getCurrentTimeSecond(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        Log.e(TAG, "getCurrentTimeSecond: "+hour+":"+minute+":"+second );
        return (double) hour * 60 * 60 + (double) minute * 60 + (double) second;
    }
    private static String getCurrentStampDate(int targetHour) {
        String dateStr = "";
        String months = "", days = "", hours = "", sec = "", minutes = "",STargetHour = "";
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        if ( targetHour < 10){
            STargetHour = "0"+targetHour;
        }else {
            STargetHour = targetHour +"";
        }
        if (month < 10) {
            months = "0" + String.valueOf(month);
        }
        else {
            months = String.valueOf(month);
        }
        if (minute < 10) {
            minutes = "0" + String.valueOf(minute);
        }
        else {
            minutes = String.valueOf(minute);
        }
        if (day < 10) {
            days = "0" + String.valueOf(day);
        }
        else {
            days = String.valueOf(day);
        }
        if (hour < 10) {
            hours = "0" + String.valueOf(hour);
        }
        else {
            hours = String.valueOf(hour);
        }
        if (second < 10) {
            sec = "0" + String.valueOf(second);
        }
        else {
            sec = String.valueOf(second);
        }
        dateStr = ""+year+"-"+months+"-"+days+" "+STargetHour+":00:00";
        return dateStr;
    }

    public static long getStartTimeStamp(){
        long startTimeStamp = 0;
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getCurrentStampDate(18));
        }catch (Exception e){
            e.printStackTrace();
        }
        if (date1 != null) {
            long timestamp = date1.getTime();
            startTimeStamp = timestamp - getUTCOffset();
        }
        return startTimeStamp;
    }
    public static long getEndTimeStamp(long startTimeStamp,long offsetHour){
        long endTimeStamp = startTimeStamp + hourToTimestamp(offsetHour);
        return endTimeStamp;
    }
    public static DateItem timeStampToDate(long timeStamp) {
        DateItem date = new DateItem();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String str = format.format(timeStamp);
        String[] split = str.split("-");
        date.mYear = Integer.valueOf(split[0]);
        date.mMonth = Integer.valueOf(split[1]);
        date.mDay = Integer.valueOf(split[2]);
        date.mHour = Integer.valueOf(split[3]);
        date.mMin = Integer.valueOf(split[4]);
        date.mSec = Integer.valueOf(split[5]);
        return date;
    }

    public static int getUTCOffset() {
        DateItem date = new DateItem();
        StringBuffer UTCTimeBuffer = new StringBuffer();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance() ;
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        int utcOffset = (zoneOffset + dstOffset);
        return utcOffset;
    }

    public static long hourToTimestamp(long hour){
        return hour * 60 * 60 * 1000;
    }

    public static String translateLocation(double param) {
        double location = Math.abs(param);
        String mLocation = "";
        int degree = (int)location;
        int minute = (int)((location - (double)degree) * (double)60);
        int second = (int)(((location - (double)degree) * (double)60 - (double)minute) * (double)60);
        mLocation = mLocation + degree + "° ";
        if (minute < 10) {
            mLocation = mLocation + '0' + minute + "' ";
        } else {
            mLocation = mLocation + minute + "' ";
        }

        if (second < 10) {
            mLocation = mLocation + '0' + second + "” ";
        } else {
            mLocation = mLocation + second + "” ";
        }

        return mLocation;
    }

    public static String translateLongitude( double param) {
        return param > (double)0 ? "E " : "W ";
    }

    public static String translateLatitude( double param) {
        return param > (double)0 ? "N " : "S ";
    }

    public static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
