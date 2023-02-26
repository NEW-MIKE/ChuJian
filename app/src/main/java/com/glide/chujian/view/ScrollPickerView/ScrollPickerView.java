package com.glide.chujian.view.ScrollPickerView;

import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.R;

public class ScrollPickerView extends RecyclerView {
    private String TAG = "ScrollPickerView";
    private Runnable mSmoothScrollTask;
    private boolean isMoving = false;
    private int mToPosition;
    private int mItemHeight;
    private int mItemWidth;
    private int mInitialY;
    private int mFirstLineY;
    private int mSecondLineY;
    private boolean mFirstVibrator;
    private int mSelectedIndex = 0;
    private Vibrator mVibrator;
    private SoundPool mSoundPool;
    public ScrollPickerView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new ScrollerLayoutManager(context));
        initTask();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_IDLE){
            if (getChildCount() > 2) {
                settingScroll();
            }
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        freshItemView();
        if (dy == 0){
            moveToCenterPosition(mToPosition);
        }
    }

    public void moveToCenterPosition(int position){
        int firstItem = getChildLayoutPosition(getChildAt(0));
        int lastItem = getChildLayoutPosition(getChildAt(getChildCount() - 1));
        if (position < firstItem) {
            scrollToPosition(position);
            mToPosition = position;
        } else if (position <= lastItem) {
            int groupCenterHeight = getHeight()/2;
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < getChildCount()) {
                int bottom = getChildAt(movePosition).getBottom();
                int top = getChildAt(movePosition).getTop();
                int viewCenterHeight = (bottom + top)/2;
                int dy = viewCenterHeight - groupCenterHeight;
                scrollBy(0, dy);
            }
        }else {
            scrollToPosition(position);
            mToPosition = position;
        }
    }
    private void startVibrator(){
        if (mFirstVibrator) {
            if (mVibrator.hasVibrator()) {
                mVibrator.vibrate(50);
            }
            else
            {
               // mSoundPool.play(1, 1f, 1f, 0, 0, 1f);
            }
        }
    }

    private int getScrollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        if (layoutManager == null) {
            return 0;
        }
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        if (firstVisibleChildView == null) {
            return 0;
        }
        int itemHeight = firstVisibleChildView.getHeight();
        return (position) * itemHeight - firstVisibleChildView.getTop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            processItemOffset();
            mSoundPool.pause(1);
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE){
            mFirstVibrator = true;
            if (((IOnScrollListener)getAdapter()) != null) {
                ((IOnScrollListener) getAdapter()).onScroll();
            }
        }
        return super.onTouchEvent(e);
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        measureSize();
        setMeasuredDimension(mItemWidth, mItemHeight * getVisibleItemNumber());
    }

    private void measureSize() {
        if (getChildCount() > 0) {
            if (mItemHeight == 0) {
                mItemHeight = getChildAt(0).getMeasuredHeight();
            }
            if (mItemWidth == 0) {
                mItemWidth = getChildAt(0).getMeasuredWidth();
            }

            if (mFirstLineY == 0 || mSecondLineY == 0) {
                mFirstLineY = mItemHeight;
                mSecondLineY = mItemHeight * 2;
            }
        }
    }

    private void processItemOffset() {
        mInitialY = getScrollYDistance();
        postDelayed(mSmoothScrollTask, 30);
    }

    private void initTask() {
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        AudioAttributes attr = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(attr)
                .setMaxStreams(10).build();
        mSoundPool.load(getContext(), R.raw.scroller,1);
        mSmoothScrollTask = new Runnable() {
            @Override
            public void run() {
                int newY = getScrollYDistance();
                if (mInitialY != newY) {
                    mInitialY = getScrollYDistance();
                    postDelayed(mSmoothScrollTask, 30);
                } else if (mItemHeight > 0) {
                    final int offset = mInitialY % mItemHeight;//离选中区域中心的偏移量
                    if (offset == 0) {
                        return;
                    }
                    if (offset >= mItemHeight / 2) {//滚动区域超过了item高度的1/2，调整position的值
                        smoothScrollBy(0, mItemHeight - offset);
                    } else if (offset < mItemHeight / 2) {
                        smoothScrollBy(0, -offset);
                    }
                }
            }
        };
    }


    private int getVisibleItemNumber() {
        IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
        if (operation != null) {
            return operation.getVisibleItemNumber();
        }
        return 3;
    }

    private int getItemSelectedOffset() {
        IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
        if (operation != null) {
            return operation.getSelectedItemOffset();
        }
        return 1;
    }

    private void updateView(View itemView, boolean isSelected) {
        IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
        if (operation != null) {
            operation.updateView(itemView, isSelected);
        }
    }

    private void settingScroll(){
        float itemViewY;
        for (int i = 0; i < getChildCount();i++) {
            itemViewY = (getChildAt(i).getTop() + mItemHeight / 2);
            if (mFirstLineY < itemViewY && itemViewY < mSecondLineY){
                IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
                if (operation != null) {
                    operation.onScrollIdle(getChildAt(i));
                }
            }
        }
    }

    private void freshItemView() {
        for (int i = 0; i < getChildCount(); i++) {
            float itemViewY = getChildAt(i).getTop() + mItemHeight / 2;
            updateView(getChildAt(i), mFirstLineY < itemViewY && itemViewY < mSecondLineY);
            if (mFirstLineY < itemViewY && itemViewY < mSecondLineY){
                if (mSelectedIndex != i){
                    mSelectedIndex = i;
                    if (mSelectedIndex == 1) {
                        startVibrator();
                    }
                }
            }
        }
    }
}
