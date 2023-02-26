package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.glide.chujian.R;

public class DeleteConfirmDialog extends Dialog {

    public DeleteConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public DeleteConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnDeleteListener{
        void delete();
        void cancel();
    }

    public static class Builder {
        private OnDeleteListener mListener;
        private Context mContext;
        private String mCancelMsg = "";
        private String mSureMsg = "";
        private String mMainMsg = "";
        private boolean mIsCancelable = false;
        private boolean mIsCancelOutside = false;
        private TextView mDeleteTv;
        private TextView mCancelTv;

        public DeleteConfirmDialog.Builder setOnDeleteListener(OnDeleteListener onDeleteListener){
            this.mListener = onDeleteListener;
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
        public final Builder setCancelMsg(String msg) {
            mCancelMsg = msg;
            return this;
        }
        public final Builder setSureMsg(String msg) {
            mSureMsg = msg;
            return this;
        }
        public final Builder setMainMsg(String msg) {
            mMainMsg = msg;
            return this;
        }

        public final DeleteConfirmDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_delete_confirm_layout, null);
            DeleteConfirmDialog loadingDailog = new DeleteConfirmDialog(mContext, R.style.LoadingDialog);
            TextView msgTv = (TextView)view.findViewById(R.id.msg_tv);
            if (mMainMsg.equals("")) {
                msgTv.setText(mContext.getResources().getString(R.string.delete_confirm));
            }else {
                msgTv.setText(mMainMsg);
            }
            mDeleteTv = view.findViewById(R.id.delete_btn);
            if (!mSureMsg.equals("")){
                mDeleteTv.setText(mSureMsg);
            }
            mCancelTv = view.findViewById(R.id.cancel_btn);
            if (!mCancelMsg.equals("")){
                mCancelTv.setText(mCancelMsg);
            }
            mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.delete();
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
