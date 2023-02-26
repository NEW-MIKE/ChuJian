package com.glide.chujian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;

import java.util.ArrayList;

public class FutureMsgAdapter extends RecyclerView.Adapter<FutureMsgAdapter.NormalHolder> {
    private String TAG = FutureMsgAdapter.class.getSimpleName();
    private ArrayList<String> mData;
    private Context mContext;

    public FutureMsgAdapter(ArrayList<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.future_msg_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        holder.future_msg_item_tv.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public TextView future_msg_item_tv;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            future_msg_item_tv = (TextView) itemView.findViewById(R.id.future_item_tv);
        }

    }
}
