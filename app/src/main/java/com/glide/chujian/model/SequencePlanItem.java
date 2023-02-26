package com.glide.chujian.model;

public class SequencePlanItem {
    public String mPlanName;
    public String mExposeTime;
    public String mBinName;
    public String mTotalNumber;
    public String mFilterType;
    public int mItemType;
    public Boolean mIsSelected;

    public SequencePlanItem(String planName, String exposeTime, String binName, String totalNumber, String filterType, int itemType, Boolean isSelected) {
        this.mPlanName = planName;
        this.mExposeTime = exposeTime;
        this.mBinName = binName;
        this.mTotalNumber = totalNumber;
        this.mFilterType = filterType;
        this.mItemType = itemType;
        this.mIsSelected = isSelected;
    }

    public SequencePlanItem(int itemType) {
        this("","","","","",itemType,false);
    }

    public SequencePlanItem(String exposeTime, String binName, String totalNumber, Boolean isSelected) {
        this("",exposeTime,binName,totalNumber,"",0,isSelected);
    }
}
