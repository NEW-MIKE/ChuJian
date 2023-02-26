package com.glide.chujian.view.ScrollPickerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScrollPickerAdapter<T> extends RecyclerView.Adapter<ScrollPickerAdapter.ScrollPickerAdapterHolder> implements IPickerViewOperation,IOnScrollListener{
    private List<T> mDataList;
    private Context mContext;
    private OnClickListener mOnItemClickListener;
    private OnScrollListener mOnScrollListener;
    private int mSelectedItemOffset;
    private int mItemType = -1;
    private int mVisibleItemNum = 3;
    private IViewProvider mViewProvider;
    private int mLineColor;
    private int maxItemH = 0;
    private int maxItemW = 0;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private static int DELAY = 5000;

    public static int DIALOG_TYPE = 0;
    public static int NORMAL_TYPE = 1;

    int index = 0;
    private ScrollPickerAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ScrollPickerAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mViewProvider == null) {
            if (mItemType == DIALOG_TYPE){
                mViewProvider = new DialogItemViewProvider();
            }
            else if (mItemType == NORMAL_TYPE){
                mViewProvider = new NormalItemViewProvider();
            }
            else {
                mViewProvider = new DefaultItemViewProvider();
            }
            mViewProvider.setOnClickListener(new com.glide.chujian.view.ScrollPickerView.OnScrollEventListener() {
                @Override
                public void onSingleClick(@Nullable View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onSingleClick(v);
                    }
                }

                @Override
                public void onSingleTouch(@Nullable View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onSingleTouch(v);
                    }
                }

                @Override
                public void onLongClick(@Nullable View v) {
                    if (mOnItemClickListener != null) {
                        stopTimer();
                        mOnItemClickListener.onLongClick(v);
                    }
                }
            });
        }

        return new ScrollPickerAdapterHolder(LayoutInflater.from(mContext).inflate(mViewProvider.resLayout(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollPickerAdapterHolder holder, int position) {
        mViewProvider.onBindView(holder.itemView, mDataList.get(position));
        holder.itemView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mOnScrollListener.onScroll();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getSelectedItemOffset() {
        return mSelectedItemOffset;
    }

    @Override
    public int getVisibleItemNumber() {
        return mVisibleItemNum;
    }

    @Override
    public int getLineColor() {
        return mLineColor;
    }

    @Override
    public void updateView(View itemView, boolean isSelected) {
        mViewProvider.updateView(itemView, isSelected);
        adaptiveItemViewSize(itemView);
    }

    private void adaptiveItemViewSize(View itemView) {
        int h = itemView.getHeight();
        if (h > maxItemH) {
            maxItemH = h;
        }

        int w = itemView.getWidth();
        if (w > maxItemW) {
            maxItemW = w;
        }

        itemView.setMinimumHeight(maxItemH);
        itemView.setMinimumWidth(maxItemW);
    }

    @Override
    public void onScroll() {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll();
            stopTimer();
        }
    }

    @Override
    public void onScrollIdle(@Nullable View itemView) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrolled(itemView);
            startTimer();
        }
    }

    static class ScrollPickerAdapterHolder extends RecyclerView.ViewHolder {
        private View itemView;

        private ScrollPickerAdapterHolder(@NonNull View view) {
            super(view);
            itemView = view;
        }
    }

    public interface OnClickListener {
        void onLongClick(View v);
        void onSingleTouch(View v);
        void onSingleClick(View v);
    }

    public interface OnScrollListener {
        void onScrolled(View currentItemView);
        void onScroll();
        void hideCover();
    }

    public static class ScrollPickerAdapterBuilder<T> {
        private ScrollPickerAdapter mAdapter;

        public ScrollPickerAdapterBuilder(Context context) {
            mAdapter = new ScrollPickerAdapter<T>(context);
        }


        public ScrollPickerAdapterBuilder<T> selectedItemOffset(int offset) {
            mAdapter.mSelectedItemOffset = offset;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setViewType(int type) {
            mAdapter.mItemType = type;
            return this;
        }


        public ScrollPickerAdapterBuilder<T> setDataList(List<T> list) {
            mAdapter.mDataList.clear();
            mAdapter.mDataList.addAll(list);
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setOnClickListener(OnClickListener listener) {
            mAdapter.mOnItemClickListener = listener;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> visibleItemNumber(int num) {
            mAdapter.mVisibleItemNum = num;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setItemViewProvider(IViewProvider viewProvider) {
            mAdapter.mViewProvider = viewProvider;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setDivideLineColor(String colorString) {
            mAdapter.mLineColor = Color.parseColor(colorString);
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setOnScrolledListener(OnScrollListener listener) {
            mAdapter.mOnScrollListener = listener;
            return this;
        }

        public ScrollPickerAdapter build() {
            adaptiveData(mAdapter.mDataList);
            mAdapter.notifyDataSetChanged();
            return mAdapter;
        }

        private void adaptiveData(List list) {
            int visibleItemNum = mAdapter.mVisibleItemNum;
           // int selectedItemOffset = mAdapter.mSelectedItemOffset;
            int selectedItemOffset = 1;
            for (int i = 0; i < selectedItemOffset; i++) {
                list.add(0, null);
            }

            for (int i = 0; i < visibleItemNum - selectedItemOffset - 1; i++) {
                list.add(null);
            }
        }
    }
    public void startTimer(){
        if (mItemType != NORMAL_TYPE)return;
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mOnScrollListener != null) {
                        mOnScrollListener.hideCover();
                    }
                    stopTimer();
                }
            };
            if(mTimer != null && mTimerTask != null )
                mTimer.schedule(mTimerTask, DELAY);
        }
    }

    public void stopTimer(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

}
