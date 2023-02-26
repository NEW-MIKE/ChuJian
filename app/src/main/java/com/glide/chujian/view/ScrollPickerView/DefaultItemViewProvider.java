package com.glide.chujian.view.ScrollPickerView;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glide.chujian.R;
import com.glide.chujian.util.ScreenUtil;

public class DefaultItemViewProvider implements IViewProvider<String> {
    @Override
    public int resLayout() {
        // return R.layout.scroll_picker_default_item_layout
        return R.layout.scroll_picker_dialog_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable String text) {
        TextView tv = view.findViewById(R.id.tv_content);
        tv.setText(text);
        view.setTag(text);
        tv.setTag(text);
        tv.setTextSize(ScreenUtil.spToPx(8f));
    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_content);
        tv.setTextSize(ScreenUtil.spToPx(6));
        tv.setTextColor(Color.parseColor(isSelected ? "#0E8211" : "#FFFFFF"));
    }

    @Override
    public void setOnClickListener(OnScrollEventListener listener) {

    }
}
