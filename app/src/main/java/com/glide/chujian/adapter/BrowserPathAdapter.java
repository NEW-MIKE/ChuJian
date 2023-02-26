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


public class BrowserPathAdapter extends RecyclerView.Adapter<BrowserPathAdapter.NormalHolder> {
    private BrowserPathAdapterListener mListener = null;
    private Context mContext;
    private ArrayList<String> mData;

    public BrowserPathAdapter(ArrayList<String> data,Context context) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.browser_path_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        int mPosition = holder.getAdapterPosition();
        if (mPosition == 0) {
            holder.pathTv.setText(mData.get(mPosition));
        }
        else {
            holder.pathTv.setText(" / " + mData.get(mPosition));
        }
        holder.pathTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.clickItem(mPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        TextView pathTv;
        public NormalHolder(@NotNull View itemView) {
            super(itemView);
            pathTv = (TextView) itemView.findViewById(R.id.path_tv);
        }
    }

    public void setOnClickListener(BrowserPathAdapterListener listener) {
        mListener = listener;
    }
}
