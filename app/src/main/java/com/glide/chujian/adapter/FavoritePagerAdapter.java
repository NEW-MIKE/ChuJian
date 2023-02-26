package com.glide.chujian.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;
import com.glide.chujian.model.GotoSwipeItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FavoritePagerAdapter extends RecyclerView.Adapter<FavoritePagerAdapter.NormalHolder> {
    private ArrayList<GotoSwipeItem> mData;
    private Context mContext;

    public FavoritePagerAdapter(ArrayList<GotoSwipeItem> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.goto_favorite_viewpager_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        holder.itemImageView.setImageResource(mData.get(position).mImageId);
        holder.itemName.setText(mData.get(position).mItemName);
        Log.e("TAG", "onBindViewHolder: initView"+ mData.get(position).mItemName);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemName;
        public NormalHolder(@NotNull View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemImageView = (ImageView) itemView.findViewById(R.id.item_iv);
        }
    }
}
