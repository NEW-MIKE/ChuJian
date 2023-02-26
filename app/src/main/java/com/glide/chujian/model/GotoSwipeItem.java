package com.glide.chujian.model;

public class GotoSwipeItem {
    public String mItemName;
    public int mImageId;

    public GotoSwipeItem(String itemName, int imageId) {
        this.mItemName = itemName;
        this.mImageId = imageId;
    }

    public GotoSwipeItem() {
        this("",0);
    }
}
