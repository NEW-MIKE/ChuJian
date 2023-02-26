package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;
import com.glide.chujian.adapter.LogAdapter;

import java.util.ArrayList;

public class LogDialog extends Dialog {

    public LogDialog(@NonNull Context context) {
        super(context);
    }

    public LogDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;
        private String mMessageCancelContent = "";
        private String mMessageConfirmContent = "";
        private boolean mIsCancelable = false;
        private boolean mIsCancelOutside = false;
        private Button closeBtn ,clearBtn;
        private RecyclerView mLogRecyclerView;
        private LogAdapter mLogAdapter;
        private ArrayList<String> mLogDataList = new ArrayList<>();

        public LogDialog.Builder addAllMessageContent(ArrayList<String> msgList){
            mLogDataList.clear();
            mLogDataList.addAll(msgList);
            if (mLogAdapter != null){
                mLogAdapter.notifyDataSetChanged();
            }
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

        public final LogDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_log_layout, null);
            LogDialog loadingDailog = new LogDialog(mContext, R.style.LoadingDialog);
            mLogRecyclerView = (RecyclerView)view.findViewById(R.id.log_rv);
            LinearLayoutManager mlayoutManager = new LinearLayoutManager(mContext);
            mLogAdapter = new LogAdapter(mLogDataList, mContext);
            mLogRecyclerView.setLayoutManager(mlayoutManager);
            mLogRecyclerView.setAdapter(mLogAdapter);
            closeBtn = (Button) view.findViewById(R.id.close_btn);
            clearBtn = (Button) view.findViewById(R.id.clear_btn);
            if (!mMessageConfirmContent.equals("")) {
                closeBtn.setText(mMessageCancelContent);
            }
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDailog.dismiss();
                }
            });
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLogDataList.clear();
                    if (mLogAdapter != null){
                        mLogAdapter.notifyDataSetChanged();
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
