package com.glide.chujian.adapter;

public interface BrowserAdapterListener {
    void checked(int position);
    void enterNext(int position);
    void backUp();
    void openFile(int position);
}
