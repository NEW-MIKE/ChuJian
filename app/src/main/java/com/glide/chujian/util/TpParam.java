package com.glide.chujian.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hyayh on 2017/11/28.
 */

public class TpParam implements Parcelable {
    public int iMin;// 参数的最小值
    public int iMax;// 参数的最大值
    public int iDefault;// 参数的默认值
    public int iCurPos;// bar参数位置值
    public int iCurValue;// 当前值
    public float fCoef;
    public boolean bIsAble;// 是否支持该参数 true 支持 false 不支持

    /**
     * 计算当前bar的位置
     * 必须保证imax imin icurValue
     *
     * @param param bar 参数
     * @return
     */
    public int calcuPos() {
        fCoef = (iMax - iMin) / 1000.0f;
        iCurPos = (int) ((iCurValue - iMin) / fCoef);
        return iCurPos;
    }

    @Override
    public String toString() {
        return "TpParam{" +
                "imin=" + iMin +
                ", imax=" + iMax +
                ", idefault=" + iDefault +
                ", icurPos=" + iCurPos +
                ", icurValue=" + iCurValue +
                ", fcoef=" + fCoef +
                ", bisAble=" + bIsAble +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(iMin);// 参数的最小值
        out.writeInt(iMax);// 参数的最大值
        out.writeInt(iDefault);// 参数的默认值
        out.writeInt(iCurPos);// bar参数位置值
        out.writeInt(iCurValue);// 当前值
        out.writeFloat(fCoef);
        out.writeBoolean(bIsAble);// 是否支持该参数 true 支持 false 不支持
    }

    public static final Creator<TpParam> CREATOR = new Creator<TpParam>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        @Override
        public TpParam createFromParcel(Parcel in) {
            TpParam r = new TpParam();
            r.readFromParcel(in);
            return r;
        }


        @Override
        public TpParam[] newArray(int size) {
            return new TpParam[size];
        }
    };


    public void readFromParcel(@androidx.annotation.NonNull Parcel in) {
        iMin = in.readInt();
        iMax = in.readInt();
        iDefault = in.readInt();
        iCurPos = in.readInt();
        iCurValue = in.readInt();
        fCoef = in.readFloat();
        bIsAble = in.readBoolean();
    }
}