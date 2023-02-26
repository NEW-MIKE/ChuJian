package com.glide.chujian.model;

public class GuideDockStatus {
    public Integer mGuideDockNum;
    public Integer mGuideDockMode;
    public Integer mGuideDockSelectedItem;
    public GuideDockStatus() {
        this.mGuideDockNum = 0;
        this.mGuideDockMode = 0;
        this.mGuideDockSelectedItem = 0;
    }

    public void clearDockStatus(){
        this.mGuideDockNum = 0;
        this.mGuideDockMode = 0;
        this.mGuideDockSelectedItem = 0;
    }
}
