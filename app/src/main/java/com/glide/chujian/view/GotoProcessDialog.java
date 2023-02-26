package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.util.GuideUtil;

public class GotoProcessDialog extends Dialog {

    public GotoProcessDialog(@NonNull Context context) {
        super(context);
    }

    public GotoProcessDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnStopGotoListener{
        void stopGotoLocation();
    }

    public static class Builder {

        private OnStopGotoListener mListener;
        private Context mContext;
        private boolean mIsCancelable = false;
        private boolean mIsCancelOutside = false;
        private TextView mCurrentLocationTv;
        private TextView mTargetRaTv;
        private TextView mTargetDecTv;
        private TextView mCurrentRaTv;
        private TextView mCurrentDecTv;
        private String mTargetRaHeader = App.INSTANCE.getResources().getString(R.string.moving_to_target_location);
        private String mCurrentRaHeader = App.INSTANCE.getResources().getString(R.string.current_location);
        private String mTargetRa = "";
        private String mTargetDec = "";
        private String mCurrentRa = "";
        private String mCurrentDec = "";

        public GotoProcessDialog.Builder setOnStopGotoListener(OnStopGotoListener onStopGotoListener){
            this.mListener = onStopGotoListener;
            return this;
        }

        public GotoProcessDialog.Builder setCurrentRa(double location) {
            this.mCurrentRa = GuideUtil.DegreeToRa(location);
            return this;
        }
        public GotoProcessDialog.Builder setCurrentDec(double location) {
            this.mCurrentDec = GuideUtil.DegreeToDec(location);
            return this;
        }
        public GotoProcessDialog.Builder setTargetDec(double location) {
            this.mTargetDec = GuideUtil.DegreeToDec(location);
            return this;
        }
        public GotoProcessDialog.Builder setTargetRa(double location) {
            this.mTargetRa = GuideUtil.DegreeToRa(location);
            return this;
        }

        public Builder updateCurrentRa(double location,boolean isTarget){
            if (mCurrentRaTv != null){
                if (isTarget){
                    mCurrentRaTv.setText(mTargetRa);
                }else {
                    mCurrentRaTv.setText(GuideUtil.DegreeToRa(location));
                }
            }
            return this;
        }
        public Builder updateCurrentDec(double location,boolean isTarget){
            if (mCurrentDecTv != null){
                if (isTarget){
                    mCurrentDecTv.setText(mTargetDec);
                }else
                {
                    mCurrentDecTv.setText(GuideUtil.DegreeToDec(location));
                }
            }
            return this;
        }

        public final Builder setIsCancelable(boolean isCancelable) {
            this.mIsCancelable = isCancelable;
            return this;
        }

        public final Builder setIsCancelOutside(boolean isCancelOutside) {
            this.mIsCancelOutside = isCancelOutside;
            return this;
        }

        public final GotoProcessDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_goto_process, null);
            GotoProcessDialog loadingDialog = new GotoProcessDialog(mContext, R.style.LoadingDialog);
            mTargetRaTv = (TextView)view.findViewById(R.id.target_ra_value);
            mTargetDecTv = (TextView)view.findViewById(R.id.target_dec_value);
            mCurrentRaTv = (TextView)view.findViewById(R.id.current_ra_value);
            mCurrentDecTv = (TextView)view.findViewById(R.id.current_dec_value);
            TextView targetLocationTv = (TextView)view.findViewById(R.id.target_location_tv);
            mCurrentLocationTv = (TextView)view.findViewById(R.id.current_location_tv);
            Button stopGotoBtn = (Button) view.findViewById(R.id.stop_goto_btn);
            targetLocationTv.setText(mTargetRaHeader);
            mCurrentLocationTv.setText(mCurrentRaHeader);
            mTargetRaTv.setText(mTargetRa);
            mTargetDecTv.setText(mTargetDec);
            mCurrentRaTv.setText(mCurrentRa);
            mCurrentDecTv.setText(mCurrentDec);
            stopGotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.stopGotoLocation();
                    }
                }
            });
            loadingDialog.setContentView(view);
            loadingDialog.setCancelable(this.mIsCancelable);
            loadingDialog.setCanceledOnTouchOutside(this.mIsCancelOutside);
            return loadingDialog;
        }

        public Builder(Context context) {
            super();
            this.mContext = context;
        }
    }
}
