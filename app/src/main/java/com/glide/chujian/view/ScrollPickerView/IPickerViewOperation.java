package com.glide.chujian.view.ScrollPickerView;

import android.view.View;

public interface IPickerViewOperation {
    int getSelectedItemOffset();

    int getVisibleItemNumber();

    int getLineColor();

    void updateView(View itemView, boolean isSelected);

    void onScrollIdle(View itemView);
}
