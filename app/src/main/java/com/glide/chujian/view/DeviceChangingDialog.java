package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.glide.chujian.R;

public class DeviceChangingDialog extends Dialog {
    public DeviceChangingDialog(@NonNull Context context) {
        super(context);
    }

    public DeviceChangingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;
        private boolean mIsShowMessage = true;
        private boolean mIsCancelable = false;
        private boolean mIsCancelOutside = false;

        public final Builder setIsShowMessage(boolean isShowMessage) {
            this.mIsShowMessage = isShowMessage;
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

        public final DeviceChangingDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_device_changing, null);
            DeviceChangingDialog loadingDailog = new DeviceChangingDialog(mContext, R.style.LoadingDialog);
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(this.mIsCancelable);
            loadingDailog.setCanceledOnTouchOutside(this.mIsCancelOutside);
            return loadingDailog;
        }

        public Builder(Context context) {
            super();
            this.mIsShowMessage = true;
            this.mContext = context;
        }
    }
}
