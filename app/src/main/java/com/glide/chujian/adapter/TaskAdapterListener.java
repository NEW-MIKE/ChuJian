package com.glide.chujian.adapter;

public interface TaskAdapterListener {
    void addItem();
    void selectClick(int position);
    void editItem(int position);
    void deleteItem(int position);
}
