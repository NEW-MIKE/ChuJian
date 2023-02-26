package com.glide.chujian.model;

public class DateItem {
    public Integer mYear;
    public Integer mMonth;
    public Integer mDay;
    public Integer mHour;
    public Integer mMin;
    public Integer mSec;

    public DateItem() {
        this.mYear = 0;
        this.mMonth = 0;
        this.mDay = 0;
        this.mHour = 0;
        this.mMin = 0;
        this.mSec = 0;
    }

    public DateItem(Integer year, Integer month, Integer day, Integer hour, Integer min, Integer sec) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mHour = hour;
        this.mMin = min;
        this.mSec = sec;
    }
}
