package com.glide.chujian.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {
    private String TAG = "DBOpenHelper";
    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "onUpgrade: oldVersion:"+oldVersion+"   newVersion" +newVersion);
        if (oldVersion == 1 && newVersion == 2) {
/*            String sql = "ALTER TABLE " + Constant.DB_TABLE_NAME + " ADD " + "isFavorite" + " INTEGER " + "DEFAULT 0";
            db.execSQL(sql);*/
    /*        String sqlIndex = "ALTER TABLE " + Constant.DB_TABLE_NAME + " ADD CONSTRAINT" + "uid" + "UNIQUE" ;
            db.execSQL(sqlIndex);*/
        }
    }
}
