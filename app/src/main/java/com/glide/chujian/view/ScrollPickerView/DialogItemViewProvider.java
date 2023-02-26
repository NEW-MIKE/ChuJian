package com.glide.chujian.view.ScrollPickerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.util.ScreenUtil;

public class DialogItemViewProvider implements IViewProvider<String> {
    @Override
    public int resLayout() {
        return R.layout.scroll_picker_dialog_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable String text) {
        TextView tv = view.findViewById(R.id.tv_content);
        tv.setText(text);
        view.setTag(text);
        tv.setTag(text);
        tv.setTextSize(ScreenUtil.androidAutoSizeDpToPx(6));
    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_content);
        tv.setTextSize(ScreenUtil.androidAutoSizeDpToPx(6));
        tv.setTextColor(App.INSTANCE.getResources().getColor(R.color.normal_text_color));
    }

    @Override
    public void setOnClickListener(OnScrollEventListener listener) {

    }
}
