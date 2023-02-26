package com.glide.chujian.fragment;

import androidx.fragment.app.Fragment;

public class TabItem {
    public Class<? extends Fragment> mFragmentCls;

    public TabItem(Class<? extends Fragment> fragmentCls) {
        this.mFragmentCls = fragmentCls;
    }
}
