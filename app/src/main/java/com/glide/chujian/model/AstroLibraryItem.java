package com.glide.chujian.model;

import java.util.ArrayList;

public class AstroLibraryItem {
    public int mId;
    public String mStarName;
    public String mRA;
    public String mDEC;
    public String mAlt;
    public Float mRaNumber;
    public Float mDecNumber;
    public Float mMag;
    public String mBrt;
    public Integer mIsFavorite;
    public ArrayList<Double> mChartData;
    public Double mDirectionAngle = 0.0;
    public ArrayList<Double> mMaxLocation;

    public AstroLibraryItem(int id, String starName, String RA, String DEC, String alt, Float ra_number, Float dec_number, Float mag, String brt, Integer isFavorite, ArrayList<Double> chartData, Double direction_angle, ArrayList<Double> max_location) {
        mId = id;
        mStarName = starName;
        this.mRA = RA;
        this.mDEC = DEC;
        mAlt = alt;
        this.mRaNumber = ra_number;
        this.mDecNumber = dec_number;
        this.mMag = mag;
        this.mBrt = brt;
        this.mIsFavorite = isFavorite;
        this.mChartData = chartData;
        this.mDirectionAngle = direction_angle;
        this.mMaxLocation = max_location;
    }

}
