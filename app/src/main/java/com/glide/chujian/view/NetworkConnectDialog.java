package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.glide.chujian.R;

public class NetworkConnectDialog extends Dialog {

    public NetworkConnectDialog(@NonNull Context context) {
        super(context);
    }

    public NetworkConnectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnNormalClickListener{
        void sure();
        void cancel();
    }

    public static class Builder {
        private OnNormalClickListener mListener;
        private Context mContext;
        private String mMessageCancelContent = "";
        private String mMessageConfirmContent = "";
        private boolean mIsCancelable = false;
        private boolean mIsCancelOutside = false;
        private TextView mDeleteTv;

        public NetworkConnectDialog.Builder setOnNormalClickListener(OnNormalClickListener onListener){
            this.mListener = onListener;
            return this;
        }


        public NetworkConnectDialog.Builder setmMessageCancelContent(String msg){
            this.mMessageCancelContent = msg;
            return this;
        }

        public NetworkConnectDialog.Builder setmMessageConfirmContent(String msg){
            this.mMessageConfirmContent = msg;
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

        public final NetworkConnectDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_network_connect_layout, null);
            NetworkConnectDialog loadingDailog = new NetworkConnectDialog(mContext, R.style.LoadingDialog);
            TextView msgTv = (TextView)view.findViewById(R.id.msg_tv);
           // msgTv.setText(messageContent);
            mDeleteTv =  view.findViewById(R.id.delete_btn);
            if (!mMessageCancelContent.equals("")) {
                mDeleteTv.setText(mMessageConfirmContent);
            }
            mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.sure();
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
