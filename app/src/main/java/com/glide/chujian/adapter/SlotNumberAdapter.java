package com.glide.chujian.adapter;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.chujian.BaseActivity;
import com.glide.chujian.R;

import java.util.ArrayList;

public class SlotNumberAdapter extends RecyclerView.Adapter<SlotNumberAdapter.NormalHolder> {
    private final String TAG = "SlotNumberAdapter";
    private ArrayList<String> mData;
    private BaseActivity mContext;

    private SlotNumberAdapterListener mListener;

    public SlotNumberAdapter(ArrayList<String> data, BaseActivity context) {
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.slot_number_item_layout, parent, false);
        return new NormalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: debug focus" );
        holder.slotItemNameTv.setText("Slot#"+(position +1)+": ");
        holder.slotItemEt.getText().clear();
        holder.slotItemEt.setText(mData.get(position));
        holder.slotItemEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e(TAG, "onCheckedChanged: cannotclick slotItemEt"+b );
                if (!b){
                    if (mListener != null){
                        mListener.editItem(holder.getAdapterPosition(),holder.slotItemEt.getText().toString());
                    }
                }
            }
        });
        holder.slotItemEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                holder.slotItemEt.clearFocus();
                Log.e(TAG, "onEditorAction: cannotclick 软键盘收起，清除焦点" );
                ((InputMethodManager)mContext.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(holder.slotItemEt.getWindowToken(),0);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public EditText slotItemEt;
        public TextView slotItemNameTv;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            slotItemEt = (EditText) itemView.findViewById(R.id.slot_item_et);
            slotItemNameTv = (TextView) itemView.findViewById(R.id.slot_item_name);
        }

    }

    public void setOnClickListener(SlotNumberAdapterListener listener) {
        this.mListener = listener;
    }

}
