package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.glide.chujian.R;

public class LoadingDialog extends Dialog {
    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;
        private String mMessage;
        private boolean mIsShowMessage = true;
        private boolean mIsCancelable = false;
        private boolean mIsCancelOutside = false;

        public Builder setmMessage(String mMessage) {
            this.mMessage = mMessage;
            return this;
        }

        public final Builder setmIsShowMessage(boolean isShowMessage) {
            this.mIsShowMessage = isShowMessage;
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

        public final LoadingDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_loading, null);
            LoadingDialog loadingDialog = new LoadingDialog(mContext, R.style.LoadingDialog);
            TextView msgText = view.findViewById(R.id.tipTextView);
            if (this.mIsShowMessage) {
                msgText.setText(this.mMessage);
            } else {
                msgText.setVisibility(View.GONE);
            }

            loadingDialog.setContentView(view);
            loadingDialog.setCancelable(this.mIsCancelable);
            loadingDialog.setCanceledOnTouchOutside(this.mIsCancelOutside);
            return loadingDialog;
        }

        public Builder(Context context) {
            super();
            this.mIsShowMessage = true;
            this.mContext = context;
        }
    }
}
