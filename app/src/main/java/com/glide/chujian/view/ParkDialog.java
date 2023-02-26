package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.glide.chujian.R;

public class ParkDialog extends Dialog {

    public ParkDialog(@NonNull Context context) {
        super(context);
    }

    public ParkDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnNormalClickListener{
        void sure();
        void cancel();
    }

    public static class Builder {
        private OnNormalClickListener mListener;
        private Context mContext;
        private String mMessageContent = "";
        private String mMessageCancelContent = "";
        private String mMessageConfirmContent = "";
        private boolean mIsCancelVisible = true;
        private boolean mIsCancelable = false;
        private boolean mIsCancelOutside = false;
        private TextView mDeleteTv;
        private TextView mCancelTv;

        public ParkDialog.Builder setOnNormalClickListener(OnNormalClickListener onListener){
            this.mListener = onListener;
            return this;
        }

        public ParkDialog.Builder setmMessageContent(String msg){
            this.mMessageContent = msg;
            return this;
        }

        public ParkDialog.Builder setmMessageCancelContent(String msg){
            this.mMessageCancelContent = msg;
            return this;
        }

        public ParkDialog.Builder setmMessageConfirmContent(String msg){
            this.mMessageConfirmContent = msg;
            return this;
        }

        public final Builder setmIsCancelVisible(boolean isCancelVisible) {
            this.mIsCancelVisible = isCancelVisible;
            return this;
        }

        public final Builder setmIsCancelable(boolean isCancelable) {
            this.mIsCancelable = isCancelable;
            return this;
        }

        public final Builder setmIsCancelOutside(boolean isCancelOutside) {
            this.mIsCancelOutside = isCancelOutside;
            return this;
        }

        public final ParkDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_park_layout, null);
            ParkDialog loadingDailog = new ParkDialog(mContext, R.style.LoadingDialog);
            TextView msgTv = (TextView)view.findViewById(R.id.msg_tv);
            msgTv.setText(mMessageContent);
            mDeleteTv =  view.findViewById(R.id.delete_btn);
            if (!mMessageCancelContent.equals("")) {
                mDeleteTv.setText(mMessageConfirmContent);
            }
            mCancelTv =  view.findViewById(R.id.cancel_btn);
            if (!mIsCancelVisible){
                mCancelTv.setVisibility(View.GONE);
            }
            if (!mMessageConfirmContent.equals("")) {
                mCancelTv.setText(mMessageCancelContent);
            }
            mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.sure();
                    }
                }
            });
            mCancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.cancel();
                    }
                }
            });
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(this.mIsCancelable);
            loadingDailog.setCanceledOnTouchOutside(this.mIsCancelOutside);
            return loadingDailog;
        }

        public Builder(Context context) {
            super();
            this.mContext = context;
        }
    }
}
