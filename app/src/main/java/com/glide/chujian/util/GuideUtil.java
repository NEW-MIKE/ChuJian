package com.glide.chujian.util;

import static java.lang.Math.abs;

import java.nio.ByteBuffer;

public class GuideUtil {
    private static String TAG = "GuideUtil";
    public static ByteBuffer byte2Byffer(byte[] byteArray) {
        //初始化一个和byte长度一样的buffer
        ByteBuffer buffer= ByteBuffer.allocate(byteArray.length);
        // 数组放到buffer中
        buffer.put(byteArray);
        //重置 limit 和postion 值 否则 buffer 读取数据不对
        buffer.flip();
        return buffer;
    }

    public static double RaToDegree(int hour,int minute,int second)
    {
        double degree = hour + ((double)minute) / 60 + ((double)second) / (60 * 60);
        return degree;
    }

    public static String DegreeToRa(double degree)
    {
        String ra = "";
        int hour = (int)degree;
        if (hour < 10){
            ra += "0"+hour+"h ";
        }else {
            ra += ""+hour+"h ";
        }
        int minute = (int)((degree - hour) * 60);
        if (minute < 10){
            ra += "0"+minute+"m ";
        }else {
            ra += ""+minute+"m ";
        }
        int second = (int)((((degree - hour) * 60) - minute) * 60);
        if (second < 10){
            ra += "0"+second+"s";
        }else {
            ra += ""+second+"s";
        }
        return ra;
    }

    public static String DegreeToDec(double degree)
    {
        String dec = "";
        String underZero = "";
        int hour = (int)degree;
        if ((degree < 0)){
            underZero = "-";
        }else {
            underZero = "+";
        }
        int absHour = abs(hour);
        if (absHour < 10){
            dec += underZero + "0" + absHour+"° ";
        }else {
            dec += underZero + absHour+"° ";
        }
        int minute = abs((int)((degree - hour) * 60));
        if (minute < 10){
            dec += "0" + minute+"′ ";
        }else {
            dec += minute+"′ ";
        }
        int second = (int)((abs((degree - hour) * 60) - minute) * 60);
        if (second < 10){
            dec += "0" + second+"″";
        }else {
            dec += second+"″";
        }
        return dec;
    }

    public static double DecToDegree(int hour,int minute,int second)
    {
        double degree = abs(hour) + ((double)minute) / 60 + ((double)second) / (60 * 60);
        if (hour < 0){
            return -degree;
        }
        return degree;
    }

    public static int getIntNumberNoUnit(String input){
        int number = 0;
        try {
            number = Integer.parseInt(input.substring(0,input.length()-1));
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }

    public static float getFloatNumberNoUnit(String input){
        float number = 0;
        try {
            number = Float.parseFloat(input.substring(0,input.length()-1));
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }

    public static int parseBinNumber(String input){
        int number = 0;
        try {
            number = Integer.parseInt(input.substring(0,input.indexOf("x")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }

    public static int parseResNumber(String input){
        int number = 0;
        try {
            number = Integer.parseInt(input.substring(input.indexOf("x")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }

}

