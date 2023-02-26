package com.glide.chujian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.model.ClickMenuItem;
import com.glide.chujian.util.ScreenUtil;

import java.util.ArrayList;

public class ClickMenuAdapter extends RecyclerView.Adapter<ClickMenuAdapter.NormalHolder> {
    private ArrayList<ClickMenuItem> mData;
    private Context mContext;
    private int mIndex = -1;
    private String mCustomContent = App.INSTANCE.getResources().getString(R.string.custom);

    private ClickMenuAdapterListener mListener;

    public void setSelectedIndex(int i){
        mIndex = i;
    }

    public ClickMenuAdapter(ArrayList<ClickMenuItem> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    public void setOnClickListener(ClickMenuAdapterListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_pop_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        if (position == mData.size() - 1){
            if (mData.get(position).mIsEditable) {
                holder.menu_pop_item_tv.setText(mCustomContent +"\n"+ mData.get(position).mValue);
            }else {
                holder.menu_pop_item_tv.setText(mData.get(position).mValue);
            }
        }else {
            holder.menu_pop_item_tv.setText(mData.get(position).mValue);
        }
        if (mIndex == position){
            holder.menu_pop_item_tv.setTextSize(ScreenUtil.androidAutoSizeDpToPx(10));
            holder.menu_pop_item_tv.setTextColor(mContext.getResources().getColor(R.color.all_blue_color));
        }else {
            holder.menu_pop_item_tv.setTextSize(ScreenUtil.androidAutoSizeDpToPx(9));
            holder.menu_pop_item_tv.setTextColor(mContext.getResources().getColor(R.color.normal_text_color));
        }
        holder.menu_pop_item_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mIndex = holder.getAdapterPosition();
                    mListener.chooseItem(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            }
        });
        holder.menu_pop_item_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null){
                    //index = holder.getAdapterPosition();
                    mIndex = holder.getAdapterPosition();
                    if ((holder.getAdapterPosition() == mData.size() - 1) && mData.get(mIndex).mIsEditable){
                        mListener.editAction();
                    }
                    notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public TextView menu_pop_item_tv;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            menu_pop_item_tv = (TextView) itemView.findViewById(R.id.menu_pop_item_tv);
        }
    }
}
