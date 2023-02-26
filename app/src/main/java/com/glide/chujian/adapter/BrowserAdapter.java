package com.glide.chujian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.App;
import com.glide.chujian.R;
import com.glide.chujian.model.BrowserInfo;

import java.util.ArrayList;

public class BrowserAdapter extends RecyclerView.Adapter<BrowserAdapter.NormalHolder> {
    private String TAG = "BrowserAdapter";
    private ArrayList<BrowserInfo> mData;
    private Context mContext;

    private int mEditType = -1;
    private BrowserAdapterListener mListener;

    public static final int TYPE_EDIT = 1;
    public static final int TYPE_NORMAL = 2;

    public BrowserAdapter(ArrayList<BrowserInfo> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    public void setOnClickListener(BrowserAdapterListener listener) {
        this.mListener = listener;
    }

    public void setEditStatus(boolean edited) {
        if (edited) {
            this.mEditType = TYPE_EDIT;
        } else {
            this.mEditType = TYPE_NORMAL;
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.browser_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        if (!mData.get(position).mIsFileType){
            holder.fileSelectCb.setVisibility(View.GONE);
        }
        else if (mEditType == TYPE_EDIT){
            if (!mData.get(position).mIsReturn)
                holder.fileSelectCb.setVisibility(View.VISIBLE);
        }
        else{
            holder.fileSelectCb.setVisibility(View.GONE);
        }

        if (mData.get(position).mIsSelected){
            holder.fileSelectCb.setChecked(true);
        }
        else{
            holder.fileSelectCb.setChecked(false);
        }
        holder.coverAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.fileSelectCb.getVisibility() == View.VISIBLE){
                    if (mListener != null){
                        mListener.checked(holder.getAdapterPosition());
                    }
                }else {
                    if (mListener != null){
                        if (!mData.get(holder.getAdapterPosition()).mIsReturn && !mData.get(holder.getAdapterPosition()).mIsFileType){
                            mListener.enterNext(holder.getAdapterPosition());
                        }
                        else if (mData.get(holder.getAdapterPosition()).mIsReturn){
                            mListener.backUp();
                        }else {
                            mListener.openFile(holder.getAdapterPosition());
                        }
                    }
                }
            }
        });
        String fileName = mData.get(position).mName;
        String[] types = fileName.split("\\.");
        if (types.length > 1){
            String type = types[types.length - 1];
            if (type.equals("mp4")){
                holder.previewImage.setBackground(App.INSTANCE.getDrawable(R.drawable.ic_video_normal));
            }else if (type.equals("fits")){
                holder.previewImage.setBackground(App.INSTANCE.getDrawable(R.drawable.ic_picture_normal));
            }
        }else if(types.length == 1){
            holder.previewImage.setBackground(App.INSTANCE.getDrawable(R.drawable.ic_folder_normal));
        }
        if (fileName.equals("..")){
            holder.previewImage.setBackground(App.INSTANCE.getDrawable(R.drawable.ic_folder_normal));
        }
        holder.itemTitle.setText(mData.get(position).mName);
        // holder.previewImage.setImageURI(Uri.fromFile(File(datas[position].path)))
        holder.fileSelectCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.checked(holder.getAdapterPosition());
                }
            }
        });
        holder.previewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    if (!mData.get(holder.getAdapterPosition()).mIsReturn && !mData.get(holder.getAdapterPosition()).mIsFileType){
                        mListener.enterNext(holder.getAdapterPosition());
                    }
                    else if (mData.get(holder.getAdapterPosition()).mIsReturn){
                        mListener.backUp();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public TextView itemTitle;
        public ImageView previewImage;
        public ToggleButton fileSelectCb;
        public View coverAll;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.title_tv);
            previewImage = (ImageView) itemView.findViewById(R.id.image_iv);
            fileSelectCb = (ToggleButton) itemView.findViewById(R.id.file_select_cb);
            coverAll = itemView.findViewById(R.id.cover_all);
        }
    }
}
