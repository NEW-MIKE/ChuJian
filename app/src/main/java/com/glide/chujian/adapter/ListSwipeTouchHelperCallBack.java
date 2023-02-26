package com.glide.chujian.adapter;

import android.graphics.Canvas;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.model.AstroLibraryItem;

import java.util.ArrayList;

public class ListSwipeTouchHelperCallBack extends ItemTouchHelperExtension.Callback {
    private ArrayList<AstroLibraryItem> mData;

    private ListSwipeAdapter mAdapter;

    public ListSwipeTouchHelperCallBack(ArrayList<AstroLibraryItem> data, ListSwipeAdapter adapter) {
        this.mData = data;
        this.mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = ItemTouchHelper.LEFT;
        int flags = makeMovementFlags(dragFlags, swipeFlags);
        return flags;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            ((ListSwipeAdapter.NormalHolder)viewHolder).swipLL.setTranslationX((int) dX);
        }
    }
}
