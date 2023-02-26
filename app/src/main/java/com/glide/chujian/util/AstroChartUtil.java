package com.glide.chujian.util;

import static java.lang.Math.*;

import android.util.Log;

import com.glide.chujian.model.DateItem;

import java.util.ArrayList;

public class AstroChartUtil {
    private DateItem mDateItem = DateUtil.getUTCDateTime();
    public static double ut2jd(DateItem ut)
    {
        if (ut.mMonth <= 2)
        {
            ut.mYear -= 1;
            ut.mMonth += 12;
        }
        double jd = floor(365.25 * ut.mYear) + floor(30.6001 * (ut.mMonth + 1)) + ut.mDay + ut.mHour / 24.f + ut.mMin / 1440.f + ut.mSec / 86400.f + 1720981.5;
        return jd;
    }

    public static double siderealg2jd(double sg)
    {
        double tjd = (sg / 24 - 0.671262) / 1.002737909351;
        double jd = tjd + 2440000.5;
        return jd;
    }

    public static double jd2siderealg(double jd)
    {
        double tjd = jd - 2440000.5;
        double sg = 24 * (0.671262 + 1.002737909351 * tjd);
        return sg;
    }

    public static Double[] alt_az(double lat, double lon, double ra, double dec, DateItem ut)
    {
        Double[] altaz = new Double[2];
        double jd = ut2jd(ut);
        double s = jd2siderealg(jd) + lon / 15;
        double t = s - ra / 15;
        t *= 15;
        double sinh = sin(lat * PI / 180) * sin(dec * PI / 180) + cos(lat * PI / 180) * cos(dec * PI / 180) * cos(t * PI / 180);
        double h = asin(sinh) * 180 / PI;
        double cosa = (sin(dec * PI / 180) - sin(lat * PI / 180) * sin(h * PI / 180)) / (cos(lat * PI / 180) * cos(h * PI / 180));
        double a = acos(cosa) * 180 / PI;
        if (sin(t * PI / 180) >= 0)
            a = 360 - a;

        altaz[0] = a;
        altaz[1] = h;
        return altaz;
    }

    public static ArrayList<Double> altmax_dh(String name , double lat, double lon, double ra, double dec, DateItem ut1, DateItem ut2)
    {
        ArrayList<Double> altmax_dh = new ArrayList<>();
        double jd1 = ut2jd(ut1);
        double jd2 = ut2jd(ut2);
        double t1 = jd2siderealg(jd1) + lon / 15 - ra / 15;
        double t2 = jd2siderealg(jd2) + lon / 15 - ra / 15;
        double t;
        int x1 = (int)(t1 / 24);
        int x2 = (int)(t2 / 24);
        if (x2 == x1)
        {
            if (t1 == x1 * 24)
                t = t1;
            else if (t2 == x2 * 24)
                t = t2;
            else
                return null;
        }
        else
            t = (x1 + 1) * 24;
        double sg = t + ra / 15 - lon / 15;
        double dh = siderealg2jd(sg) - jd1;
        DateItem utdh = new DateItem();
        dh *= 24;
        utdh.mHour = (int)dh;
        dh -= utdh.mHour;
        dh *= 60;
        utdh.mMin = (int)dh;
        dh -= utdh.mMin;
        utdh.mSec = (int)(dh * 60);
        altmax_dh.add((double) (utdh.mHour * 60 * 60 +utdh.mMin * 60 + utdh.mSec));
        altmax_dh.add(90 - abs(lat - dec));
        if (name.equals("M1")){
            Log.e("TAG", "altmax_dh: "+"altmax_dh"+altmax_dh.get(0)+":"+altmax_dh.get(1));
        }
        return altmax_dh;
    }

    public ArrayList<Double> HighDegreeAngle(double lat, double lon, double ra, double dec){
        ArrayList<Double> mAltazList = new ArrayList<>();
        for (int i = 10;i < 23;i++){
            DateItem ut = new DateItem(mDateItem.mYear, mDateItem.mMonth, mDateItem.mDay,i,0,0);
            Double[] temp = alt_az(lat, lon, ra, dec, ut);
            mAltazList.add(temp[1]);
        }
        return mAltazList;
    }
    public Double DirectionDegreeAngle(double lat,double lon,double ra,double dec){
        DateItem ut = new DateItem(mDateItem.mYear, mDateItem.mMonth, mDateItem.mDay, mDateItem.mHour, mDateItem.mMin, mDateItem.mSec);
        Double[] temp = alt_az(lat, lon, ra, dec, ut);
        return temp[0];
    }

/*    public int getTargetTimePositionByMs(double lat, double lon, double ra, double dec, DateItem ut1, DateItem ut2){
        DateItem dateT = altmax_dh(lat,lon,ra,dec,ut1,ut2);
        if (dateT == null) return 0;
        return dateT.hour * 60 * 60 +dateT.min * 60 + dateT.sec;
    }*/
}
