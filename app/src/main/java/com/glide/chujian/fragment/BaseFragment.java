package com.glide.chujian.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.glide.chujian.BaseActivity;
import com.glide.chujian.util.Guider;
import com.glide.chujian.view.DeviceChangingDialog;

public abstract  class  BaseFragment<T extends ViewDataBinding> extends Fragment {
    private String TAG = "BaseFragment";
    public T binding;
    public BaseActivity mActivity;
    public Guider mGuider;
    public DeviceChangingDialog mLoadingDialog;
    public boolean mIsConnectTimeOut = false;

    public abstract int getLayoutId();

    public abstract void onDeviceSelectChanged();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: " );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity)requireActivity();
        mGuider = Guider.getInstance();
        if (mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = new DeviceChangingDialog.Builder(mActivity)
                .setIsCancelable(true)
                .setIsCancelOutside(false)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "event onStart: " );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "event onResume: " );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "event onDetach: " );
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mLoadingDialog.dismiss();
        Log.d(TAG, "event onHiddenChanged: " );
    }


    public abstract void waitDeviceStatusChange(Boolean status);
}
