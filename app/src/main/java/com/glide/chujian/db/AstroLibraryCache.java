package com.glide.chujian.db;

import static com.glide.chujian.util.DateUtil.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.glide.chujian.model.AstroLibraryItem;
import com.glide.chujian.model.DateItem;
import com.glide.chujian.util.AstroChartUtil;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.DateUtil;
import com.glide.chujian.util.GuideUtil;

import java.util.ArrayList;

public class AstroLibraryCache {
    private String TAG = "AstroLibraryCache";
    private DBOpenHelper mDBLiteOpenHelper;
    private SQLiteDatabase mDb;
    private Context mContext;
    private static ArrayList<AstroLibraryItem> mAstroLibraryCache = new ArrayList<AstroLibraryItem>();

    public AstroLibraryCache(Context context) {
        this.mContext = context;
        mDBLiteOpenHelper = new DBOpenHelper(context, Constant.DB_NAME,null,1);
        mDb = mDBLiteOpenHelper.getWritableDatabase();
    }

    public void loadAstroLibraryCache(double lat,double lon){
        Cursor cursor = mDb.query(Constant.DB_TABLE_NAME, new String[]{"nameEN","ra","dec","isFavorite","vmag","brightness","uid"}, null, null, null, null, null);
        cursor.moveToFirst();
        mAstroLibraryCache.clear();

        long startTimeStamp = getStartTimeStamp();
        long endTimeStamp = getEndTimeStamp(startTimeStamp,12);
        DateItem ut1 = timeStampToDate(startTimeStamp);
        DateItem ut2 = timeStampToDate(endTimeStamp);
        int uid = 0;
        while (!cursor.isAfterLast()) {
            String nameEn = cursor.getString(0);
            Float ra = cursor.getFloat(1);
            Float dec = cursor.getFloat(2);
            int isFavorite = cursor.getInt(3);
            Float mag = cursor.getFloat(4);
            String brt = cursor.getString(5);
            uid = cursor.getInt(6);
            if (lat == -1){
                mAstroLibraryCache.add(new AstroLibraryItem(uid,nameEn, GuideUtil.DegreeToRa(ra),GuideUtil.DegreeToDec(dec),"",ra,dec,mag,brt,isFavorite,
                        new ArrayList<>(), (double) 0,
                        null));
            }else {
                mAstroLibraryCache.add(new AstroLibraryItem(uid,nameEn, GuideUtil.DegreeToRa(ra), GuideUtil.DegreeToDec(dec), "", ra, dec, mag, brt, isFavorite,
                        getHighDegreeAngle(lat, lon, ra * 15, dec), getDirectionDegreeAngle(lat, lon, ra * 15, dec),
                        AstroChartUtil.altmax_dh(nameEn, lat, lon, ra * 15, dec, ut1, ut2)));
            }
            cursor.moveToNext();
        }
        mDb.close();
    }

    private ArrayList<Double> getHighDegreeAngle(double lat, double lon, double ra, double dec){
        ArrayList<Double> mAltazList = new ArrayList<>();

        DateItem date = DateUtil.getUTCDateTime();
        for (int i = 10;i < 23;i++){
            DateItem ut = new DateItem(date.mYear,date.mMonth,date.mDay,i,0,0);
            Double[] temp = AstroChartUtil.alt_az(lat, lon, ra, dec, ut);
            mAltazList.add(temp[1]);
        }
        return mAltazList;
    }
    private Double getDirectionDegreeAngle(double lat, double lon, double ra, double dec){
        DateItem ut = DateUtil.getUTCDateTime();
        Double[] temp = AstroChartUtil.alt_az(lat, lon, ra, dec, ut);
        return temp[0];
    }

    public static ArrayList<AstroLibraryItem> getAstroLibraryCache(){
        return mAstroLibraryCache;
    }
}
