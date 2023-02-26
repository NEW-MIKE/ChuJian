package com.glide.chujian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.glide.chujian.R;

import java.lang.reflect.Field;

public class MaxSpinner extends Spinner {

    private android.widget.ListPopupWindow mPopupWindow;
    private int mDefaultHeightNumber;
    private boolean mIsEnabled = true;
    public MaxSpinner(Context context) {
        super(context);
        init(context, null);
    }
    public MaxSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
        setListHeight();
        getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListHeight();
            }
        });
    }

    public void clearPopupView(){
        if (mPopupWindow != null){
            mPopupWindow.dismiss();
        }
    }

    public void setNorEnabled(boolean enable){
        mIsEnabled = enable;
        if (getAdapter() != null) {
            int number = getAdapter().getCount();
            if (number > 1) {
                setEnabled(mIsEnabled);
            } else {
                setEnabled(false);
            }
        }
    }
    private void setListHeight(){
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            mPopupWindow = (android.widget.ListPopupWindow) popup.get(this);
            int mVerticalOffset = getDropDownVerticalOffset();
            int number = getAdapter().getCount();
            //此处通过向下偏移量获取到单个item的高度
            if (mVerticalOffset > 0) {
                setEnabled(mIsEnabled);
                if (number > mDefaultHeightNumber){
                    mPopupWindow.setHeight(mVerticalOffset * mDefaultHeightNumber);
                }
                else if (number > 1){
                    mPopupWindow.setHeight(mVerticalOffset * number);
                }
                else {
                    setEnabled(false);
                    mPopupWindow.setHeight(0);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaxSpinner);
        try {
            mDefaultHeightNumber = ta.getInteger(R.styleable.MaxSpinner_mSpinnerMaxItemNumber,4);
        } finally {
            ta.recycle();
        }
    }

}