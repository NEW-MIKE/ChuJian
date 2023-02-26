package com.glide.chujian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.NormalHolder> {
    private ArrayList<String> mData;
    private Context mContext;

    public LogAdapter(ArrayList<String> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.logitem, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        holder.logItemTv.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        TextView logItemTv;
        public NormalHolder(@NotNull View itemView) {
            super(itemView);
            logItemTv = (TextView) itemView.findViewById(R.id.log_item_tv);
        }
    }
}
