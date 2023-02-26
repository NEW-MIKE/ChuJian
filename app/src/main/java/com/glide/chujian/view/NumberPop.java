package com.glide.chujian.view;

import static java.lang.Math.abs;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.glide.chujian.BaseActivity;
import com.glide.chujian.databinding.PopupNumberBinding;

public class NumberPop extends PopupWindow {
    private String TAG = "NumberPop";
    private PopupNumberBinding binding;
    private BaseActivity mActivity;

    private NumberListener mListener = null;

    public void setNumberListener(NumberListener listener) {
        mListener = listener;
    }

    public interface NumberListener {
        //选择数字事件
        void onChooseNum(String strNum);

        //删除数字事件
        void onDelNum();

        //确认数字事件
        void onSureNum();
    }
    public NumberPop(BaseActivity mActivity) {
        this.mActivity = mActivity;
        binding = PopupNumberBinding.inflate(LayoutInflater.from(mActivity));
        binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        setContentView(binding.getRoot());
        setOutsideTouchable(true);
        setFocusable(false);
        setTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        initEvent();
    }

    public void supportNegativeInput(boolean value){
        binding.numberMinUs.setEnabled(value);
    }

    public void supportDotInput(boolean value){
        binding.numberDot.setEnabled(value);
    }
    private void initEvent(){
        binding.number0Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number0Tv.getText().toString());
                }
            }
        });
        binding.number1Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number1Tv.getText().toString());
                }
            }
        });
        binding.number2Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number2Tv.getText().toString());
                }
            }
        });
        binding.number3Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number3Tv.getText().toString());
                }
            }
        });
        binding.number4Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number4Tv.getText().toString());
                }
            }
        });
        binding.number5Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number5Tv.getText().toString());
                }
            }
        });
        binding.number6Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number6Tv.getText().toString());
                }
            }
        });
        binding.number7Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number7Tv.getText().toString());
                }
            }
        });
        binding.number8Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number8Tv.getText().toString());
                }
            }
        });
        binding.number9Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.number9Tv.getText().toString());
                }
            }
        });
        binding.numberDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.numberDot.getText().toString());
                }
            }
        });
        binding.numberMinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseNum(binding.numberMinUs.getText().toString());
                }
            }
        });
        binding.numberDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDelNum();
                }
            }
        });
        binding.numberSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSureNum();
                    dismiss();
                }
            }
        });
    }
}
