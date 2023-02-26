package com.glide.chujian.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration  extends RecyclerView.ItemDecoration {
    private final int mSpace;
    private final int mNumber;
    public SpacesItemDecoration(int space, int number) {
        this.mSpace = space;
        this.mNumber = number;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = this.mSpace;
        outRect.left = this.mSpace;
        if (parent.getChildAdapterPosition(view) < this.mNumber) {
            outRect.top = this.mSpace;
        }
        if (mNumber > 0) {
            if ((parent.getChildAdapterPosition(view) % mNumber) == (mNumber - 1)) {
                outRect.right = this.mSpace;
            }
        }
    }
}
