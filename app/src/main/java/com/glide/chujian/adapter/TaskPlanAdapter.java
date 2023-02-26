package com.glide.chujian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;
import com.glide.chujian.model.SequencePlanItem;

import java.util.ArrayList;

public class TaskPlanAdapter extends RecyclerView.Adapter<TaskPlanAdapter.NormalHolder> {

    public static final int TYPE_LIST = 0;
    public static final int TYPE_ADD = 1;
    public static final int TYPE_SELECT = 2;
    public static final int TYPE_UNSELECT = 3;

    private ArrayList<SequencePlanItem> mData;
    private Context mContext;
    private TaskAdapterListener mListener;

    public TaskPlanAdapter(ArrayList<SequencePlanItem> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    public void setOnClickListener(TaskAdapterListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.screen_task_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        if (mData.get(holder.getAdapterPosition()).mItemType == TYPE_ADD){
            holder.itemTypeName.setVisibility(View.GONE);
            holder.checkSelectCb.setVisibility(View.GONE);
            holder.exposeTime.setVisibility(View.GONE);
            holder.bin.setVisibility(View.GONE);
            holder.total.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.GONE);
            holder.editBtn.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
            holder.cover_all.setVisibility(View.VISIBLE);
            holder.exposeTimeName.setVisibility(View.GONE);
            holder.binName.setVisibility(View.GONE);
            holder.totalName.setVisibility(View.GONE);
            holder.mItemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.screen_task_add_item_style,null));

        }
        else{
            holder.itemTypeName.setVisibility(View.VISIBLE);
            holder.itemTypeName.setText(mData.get(holder.getAdapterPosition()).mPlanName);
            holder.exposeTime.setText(mData.get(holder.getAdapterPosition()).mExposeTime);
            holder.bin.setText(mData.get(holder.getAdapterPosition()).mBinName);
            holder.total.setText(mData.get(holder.getAdapterPosition()).mTotalNumber);
            holder.checkSelectCb.setVisibility(View.GONE);
            holder.exposeTime.setVisibility(View.VISIBLE);
            holder.bin.setVisibility(View.VISIBLE);
            holder.total.setVisibility(View.VISIBLE);
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.editBtn.setVisibility(View.VISIBLE);
            holder.add.setVisibility(View.GONE);
            holder.cover_all.setVisibility(View.VISIBLE);

            holder.exposeTimeName.setVisibility(View.VISIBLE);
            holder.binName.setVisibility(View.VISIBLE);
            holder.totalName.setVisibility(View.VISIBLE);

            if (mData.get(holder.getAdapterPosition()).mIsSelected) {
                holder.mItemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.screen_task_item_checked_style,null));

            }
            else{
                holder.mItemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.screen_task_item_normal_style,null));
            }
        }

        holder.cover_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    if (holder.add.getVisibility() == View.VISIBLE) {
                        mListener.addItem();
                    }
                    else{
                        mListener.selectClick(holder.getAdapterPosition());
                    }
                }
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.deleteItem(holder.getAdapterPosition());
                }
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.editItem(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public TextView itemTypeName;
        public CheckBox checkSelectCb;
        public TextView exposeTime;
        public TextView bin;
        public TextView total;
        public Button deleteBtn;
        public ImageView add;
        public FrameLayout cover_all;
        public ConstraintLayout mItemLayout;
        public Button editBtn;
        public TextView exposeTimeName;
        public TextView binName;
        public TextView totalName;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            itemTypeName = (TextView) itemView.findViewById(R.id.item_type_name_tv);
            checkSelectCb = (CheckBox) itemView.findViewById(R.id.check_select_cb);
            exposeTime = (TextView) itemView.findViewById(R.id.expose_time_tv);
            bin = (TextView) itemView.findViewById(R.id.bin_tv);
            total = (TextView) itemView.findViewById(R.id.total_tv);
            deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
            add = (ImageView) itemView.findViewById(R.id.add_iv);
            cover_all = (FrameLayout) itemView.findViewById(R.id.cover_all);
            mItemLayout = (ConstraintLayout) itemView.findViewById(R.id.task_item_layout);
            editBtn = (Button) itemView.findViewById(R.id.edit_btn);
            exposeTimeName = (TextView) itemView.findViewById(R.id.expose_time_name_tv);
            binName = (TextView) itemView.findViewById(R.id.bin_name_tv);
            totalName = (TextView) itemView.findViewById(R.id.total_name_tv);
        }

    }
}
