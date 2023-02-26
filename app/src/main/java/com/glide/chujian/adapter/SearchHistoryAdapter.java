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

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.NormalHolder> {
    private String TAG = "BrowserAdapter";
    private ArrayList<String> mData;
    private Context mContext;

    private int mEditType = -1;
    private SearchHistoryAdapterListener mListener;

    public static final int TYPE_EDIT = 1;
    public static final int TYPE_NORMAL = 2;

    public SearchHistoryAdapter(ArrayList<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public void setOnClickListener(SearchHistoryAdapterListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_search_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        holder.history_search_item_tv.setText(mData.get(position));
        holder.history_search_item_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.chooseItem(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public TextView history_search_item_tv;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            history_search_item_tv = (TextView) itemView.findViewById(R.id.search_item_tv);
        }

    }
}
