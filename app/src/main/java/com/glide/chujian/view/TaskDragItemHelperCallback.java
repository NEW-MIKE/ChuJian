package com.glide.chujian.view;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.adapter.TaskPlanAdapter;
import com.glide.chujian.model.SequencePlanItem;


import java.util.ArrayList;
import java.util.Collections;

public class TaskDragItemHelperCallback extends ItemTouchHelper.Callback {
    private ArrayList<SequencePlanItem> mData;
    private TaskPlanAdapter mAdapter;

    public TaskDragItemHelperCallback(ArrayList<SequencePlanItem> data, TaskPlanAdapter adapter) {
        this.mData = data;
        this.mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int flags = 0;
        if (((TaskPlanAdapter.NormalHolder)viewHolder).add.getVisibility() == View.VISIBLE){
            flags = makeMovementFlags(0, 0);
        }
        else if (recyclerView.getLayoutManager() instanceof GridLayoutManager){
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int swipeFlags = 0;
            flags = makeMovementFlags(dragFlags, swipeFlags);
        }else {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = 0;
            flags = makeMovementFlags(dragFlags, swipeFlags);
        }

        return flags;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

        int fromPosition = viewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        if (((TaskPlanAdapter.NormalHolder)target).add.getVisibility() == View.VISIBLE){
            return true;
        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition;i++){
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition;i--){
                Collections.swap(mData, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_DRAG){
            viewHolder.itemView.setScaleX(1.1f);
            viewHolder.itemView.setScaleY(1.1f);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
    }
}
