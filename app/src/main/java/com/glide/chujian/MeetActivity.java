package com.glide.chujian;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.glide.chujian.databinding.ActivityMeetBinding;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.FileUtils;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpLib;

public class MeetActivity extends BaseActivity {
    private ActivityMeetBinding binding;
    private TpLib mLib;

    @Override
    public void updateWifiName(String wifi_name) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeetBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mLib = TpLib.getInstance();
        initEvent();
        FileUtils.createDir(Constant.PATH_VIDEOS);
        FileUtils.createDir(Constant.PATH_PICTURES);
        FileUtils.createDir(Constant.PATH_PICTURES_TASK);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initEvent(){
        binding.logInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.inputEt.getText().toString().equals(mLib.getName()) &&
                binding.passwordEt.getText().toString().equals(mLib.getPassword())){
                    MainActivity.actionStart(MeetActivity.this);
                    finish();
                }else {
                    ToastUtil.showToast("非法用户",false);
                }
            }
        });
    }

}