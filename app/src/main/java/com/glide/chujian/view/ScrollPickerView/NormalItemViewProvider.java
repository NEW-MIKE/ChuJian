package com.glide.chujian.view.ScrollPickerView;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.util.ScreenUtil;

public class NormalItemViewProvider implements IViewProvider<String> {
    private OnScrollEventListener mListener = null;
    @Override
    public int resLayout() {
        return R.layout.scroll_picker_normal_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable String text) {
        TextView tv = view.findViewById(R.id.tv_content);
        tv.setText(text);
        view.setTag(text);
        tv.setTag(text);
        tv.setTextSize(ScreenUtil.spToPx(4f));
    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_content);
        int textSize = (isSelected ? 4 : 3);
        tv.setTextSize(ScreenUtil.spToPx(textSize));
        tv.setTextColor(App.INSTANCE.getResources().getColor(R.color.normal_text_color));
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null){
                    mListener.onLongClick(tv);
                }
                return false;
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onSingleClick(tv);
                }
            }
        });

        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if (mListener != null){
                        mListener.onSingleTouch(tv);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void setOnClickListener(OnScrollEventListener listener) {
        mListener = listener;
    }
}
