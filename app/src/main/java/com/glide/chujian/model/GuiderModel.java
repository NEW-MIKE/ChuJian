package com.glide.chujian.model;

import java.util.ArrayList;

public class GuiderModel {
    public static class Accum{
        public int n;
        public double a;
        public double q;
        public double peek;

        public void reset(){
            n = 0;
            a = 0.0;
            q = 0.0;
            peek = 0.0;
        }

        public void add(double x){
            double ax = Math.abs(x);
            if (ax > peek){
                peek = ax;
            }
            n += 1;
            double d = x - a;
            a += d / n;
            q += (x - a) * d;
        }

        public double mean(){
            return a;
        }

        public double stdev(){
            if (n < 1){
                return 0.0;
            }
            return Math.sqrt((q / n));
        }
    }

    public static class SettleProgress{
        public boolean mDone;
        public double mDistance;
        public double mSettlePx;
        public double mTime;
        public double mSettleTime;
        public int mStatus;
        public String mError;

        public SettleProgress() {
            mDone = false;
            mDistance = 0.0;
            mSettlePx = 0.0;
            mTime = 0.0;
            mSettleTime = 0.0;
            mStatus = 0;
            mError = "";
        }
    }

    public static class GuideStats {
        public double mRmsTot;
        public double mRmsRa;
        public double mRmsDec;
        public double mPeakRa;
        public double mPeakDec;

        public void hypot() {
            mRmsTot = Math.hypot(mRmsRa, mRmsDec);
        }
    }

    public static class Profile{
        public int mId;
        public String mName;
        public boolean mSelected;
    }

    public static class AllDevice{
        public String mLabel;
        public ArrayList<Device> mDevices;
        public boolean mConfigured;

        public AllDevice(String label, ArrayList<Device> devices, boolean configured) {
            mLabel = label;
            mDevices = devices;
            mConfigured = configured;
        }
    }
    public static class Device{
        public String mName;

        public Device(String name) {
            this.mName = name;
        }
    }

    public static class CurrentDevice{
        public String mName;
        public String mValue;
    }
    public static class CaptureFrameSaved{
        public String mDevice;
        public String mFileName;
        public double mFileSize;
        public boolean mSuccess;
    }
    public static class Pos{
        public double mPosX;
        public double mPosY;

        public Pos(double posx, double posy) {
            this.mPosX = posx;
            this.mPosY = posy;
        }
    }
    public static class FilterWheelName{
        public int mIndex;
        public String mName;
    }
    public static class Calibrating{
        public String mMount;
        public String mDir;
        public double mDist;
        public double mDx;
        public double mDy;
        public Pos mPos;
        public int mStep;
        public String mState;
    }
    public static class StarLost{
        public int mFrame;
        public double mTime;
        public double mStarMass;
        public double mSNR;
        public double mAvgDist;
        public int mErrorCode;
        public String mStatus;
    }
    public static class SettleDone{
        public int mStatus;
        public String mError;
        public int mTotalFrames;
        public int mDroppedFrames;
    }
    public static class Settling{
        public double mDistance;
        public double mTime;
        public double mSettleTime;
        public boolean mStarLocked;
    }

}
