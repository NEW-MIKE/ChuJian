package com.glide.chujian.model;

public class ClickMenuItem {
    public String mValue;
    public boolean mIsEditable;

    public ClickMenuItem(String value, boolean isEditable) {
        this.mValue = value;
        this.mIsEditable = isEditable;
    }

    public ClickMenuItem(String value) {
        this(value,false);
    }
}
