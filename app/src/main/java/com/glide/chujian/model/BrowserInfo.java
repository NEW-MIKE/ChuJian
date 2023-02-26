package com.glide.chujian.model;

public class BrowserInfo {
    public Long mId;
    public String mTitle;
    public String mPath;
    public String mName;
    public Boolean mIsSelected;
    public Boolean mIsReturn;
    public Boolean mIsFileType;

    public BrowserInfo(Long id, String title, String path, String name, Boolean isSelected, Boolean isReturn, Boolean isFileType) {
        this.mId = id;
        this.mTitle = title;
        this.mPath = path;
        this.mName = name;
        this.mIsSelected = isSelected;
        this.mIsReturn = isReturn;
        this.mIsFileType = isFileType;
    }

    public BrowserInfo(String name) {
        this(0l,"","",name,false,false,false);
    }

    public BrowserInfo(String name, Boolean isReturn) {
        this(0l,"","",name,false,isReturn,false);
    }

    public BrowserInfo(String path, String name, Boolean isFileType) {
        this(0l,"",path,name,false,false,isFileType);
    }
}
