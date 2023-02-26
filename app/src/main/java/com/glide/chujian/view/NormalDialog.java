package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.glide.chujian.R;

public class NormalDialog extends Dialog {

    public NormalDialog(@NonNull Context context) {
        super(context);
    }

    public NormalDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnNormalClickListener{
        void sure();
        void cancel();
    }

    public static class Builder {
        private OnNormalClickListener mListener;
        private Context context;
        private String messageContent = "";
        private String messageCancelContent = "";
        private String messageConfirmContent = "";
        private boolean isCancelVisible = true;
        private boolean isCancelable = false;
        private boolean isCancelOutside = false;
        private Button deleteBtn;
        private Button cancelBtn;

        public NormalDialog.Builder setOnNormalClickListener(OnNormalClickListener onListener){
            this.mListener = onListener;
            return this;
        }

        public NormalDialog.Builder setMessageContent(String msg){
            this.messageContent = msg;
            return this;
        }

        public NormalDialog.Builder setMessageCancelContent(String msg){
            this.messageCancelContent = msg;
            return this;
        }

        public NormalDialog.Builder setMessageConfirmContent(String msg){
            this.messageConfirmContent = msg;
            return this;
        }

        public final Builder setCancelVisible(boolean isCancelVisible) {
            this.isCancelVisible = isCancelVisible;
            return this;
        }

        public final Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public final Builder setCancelOutside(boolean isCancelOutside) {
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        public final NormalDialog create() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_normal_layout, null);
            NormalDialog loadingDailog = new NormalDialog(context, R.style.LoadingDialog);
            TextView msgTv = (TextView)view.findViewById(R.id.msg_tv);
            msgTv.setText(messageContent);
            deleteBtn = (Button) view.findViewById(R.id.delete_btn);
            if (!messageCancelContent.equals("")) {
                deleteBtn.setText(messageConfirmContent);
            }
            cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
            if (!isCancelVisible){
                cancelBtn.setVisibility(View.GONE);
            }
            if (!messageConfirmContent.equals("")) {
                cancelBtn.setText(messageCancelContent);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.sure();
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.cancel();
                    }
                }
            });
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(this.isCancelable);
            loadingDailog.setCanceledOnTouchOutside(this.isCancelOutside);
            return loadingDailog;
        }

        public Builder(Context context) {
            super();
            this.context = context;
        }
    }
}
