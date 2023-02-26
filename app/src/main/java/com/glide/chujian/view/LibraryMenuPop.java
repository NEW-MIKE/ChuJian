package com.glide.chujian.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ToggleButton;

import com.glide.chujian.AstroLibraryActivity;
import com.glide.chujian.databinding.LibraryMenuLayoutBinding;

import java.util.ArrayList;

public class LibraryMenuPop extends PopupWindow {
    private Context mContext;
    private LibraryMenuLayoutBinding binding;

    private ArrayList<ToggleButton> mMenuViewList = new ArrayList<ToggleButton>();
    public LibraryMenuPop(Context mContext) {
        this.mContext = mContext;
        binding = LibraryMenuLayoutBinding.inflate(LayoutInflater.from(mContext));
        setContentView(binding.getRoot());
        setFocusable(true);
        setOutsideTouchable(true);
        binding.filter0Tbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleChooseView(binding.filter0Tbtn);
                ((AstroLibraryActivity)mContext).filter0();
            }
        });
        binding.filter1Tbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleChooseView(binding.filter1Tbtn);
                ((AstroLibraryActivity)mContext).filter1();
            }
        });

        mMenuViewList.add(binding.filter0Tbtn);
        mMenuViewList.add(binding.filter1Tbtn);
    }

    public void setCurrentFilter(int currentFilter){
        switch (currentFilter){
            case 0:{
                binding.filter0Tbtn.setChecked(true);
                break;
            }
            case 1:{
                binding.filter1Tbtn.setChecked(true);
                break;
            }
        }
    }

    public void singleChooseView(ToggleButton toggleButton){
        for (ToggleButton toggleButton1:mMenuViewList){
            if (toggleButton1 != toggleButton){
                toggleButton1.setChecked(false);
            }
        }
    }
}
