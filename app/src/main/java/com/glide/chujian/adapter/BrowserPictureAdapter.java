package com.glide.chujian.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.TpLib;
import com.glide.chujian.view.TpView;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BrowserPictureAdapter extends RecyclerView.Adapter<BrowserPictureAdapter.NormalHolder> {
    private String TAG = "BrowserPictureAdapter";
    private ArrayList<Long> mData;
    private Context mContext;
    public TpLib mLib;

    public BrowserPictureAdapter(ArrayList<Long> data, Context context) {
        this.mData = data;
        this.mContext = context;
        mLib = TpLib.getInstance();
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.browser_picture_view_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        String fileName = mData.get(position)+".fits";
        if (mLib != null) {
            int[] size = mLib.loadBrowserFitsPicture(Constant.PATH_PICTURES + fileName);
            int width = size[0];
            int height = size[1];
            holder.surface.mRender.setCurrentVideoSize(width, height) ;
            ByteBuffer mDirectBuffer = holder.surface.mRender.getDirectBuffer();
            if (mDirectBuffer != null){
                mLib.updateJBrowserPictureFrame(mDirectBuffer,Constant.PATH_PICTURES + fileName);
                holder.surface.requestRender();
            }else {
                Log.e(TAG, "onBindViewHolder:   mDirectBuffer is null");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public TpView surface;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            surface = (TpView) itemView.findViewById(R.id.surface);
        }
    }
}
