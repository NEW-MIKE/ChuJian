package com.glide.chujian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.glide.chujian.R;
import com.glide.chujian.manager.LineChartManager;
import com.glide.chujian.model.AstroLibraryItem;
import com.glide.chujian.model.DateItem;
import com.glide.chujian.util.AstroChartUtil;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.DateUtil;
import com.glide.chujian.util.ScreenUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.view.BrokenLineView;
import com.glide.chujian.view.TargetLocationView;

import java.util.ArrayList;
import java.util.List;

public class ListSwipeAdapter extends RecyclerView.Adapter<ListSwipeAdapter.NormalHolder> implements Filterable {
    private String TAG = "ListSwipeAdapter";
    private ArrayList<AstroLibraryItem> mData;
    private Context mContext;
    private ArrayList<AstroLibraryItem> mFilterData;
    private ListSwipeAdapterListener mListener;
    private String mRightTopMsg = "";
    private double mLongitude;
    private double mLatitude;
    private DateItem mDateItem;
    private boolean mIsScopeOnline;
    public ListSwipeAdapter(ArrayList<AstroLibraryItem> data,boolean isScopeOnline, Context context) {
        this.mData = data;
        this.mContext = context;
        mFilterData = new ArrayList<>(data);
        mIsScopeOnline = isScopeOnline;
        mLongitude = SharedPreferencesUtil.getInstance().getDouble(Constant.SP_LONGITUDE_ID, (double) -1);
        mLatitude = SharedPreferencesUtil.getInstance().getDouble(Constant.SP_LATITUDE_ID, (double) -1);
    }

    public void setScopeOnlineStatus(boolean isScopeOnline){
        if (mIsScopeOnline != isScopeOnline) {
            mIsScopeOnline = isScopeOnline;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.library_item_layout, parent, false);
        return new NormalHolder(view);
    }

    private String angleToString(Double angle){
        String angleDirection = "";
        if (angle > 345 || angle < 15){
            angleDirection = "北偏北";
        }else if( angle >= 15 && angle <= 75){
            angleDirection = "北偏东";
        }else if( angle >= 75 && angle <= 105){
            angleDirection = "北偏东";
        }else if( angle >= 105 && angle <= 165){
            angleDirection = "北偏南";
        }else if( angle >= 165 && angle <= 195){
            angleDirection = "北偏南";
        }else if( angle >= 195 && angle <= 255){
            angleDirection = "北偏西南";
        }else if( angle >= 255 && angle <= 285){
            angleDirection = "北偏西";
        }else if( angle >= 285 && angle <= 345){
            angleDirection = "北偏西北";
        }
        return mContext.getResources().getString(R.string.azimuth)+": "+angleDirection+(angle.intValue())+"°";
    }
    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        holder.swipLL.setTranslationX(0f);
        holder.starNameTv.setText(mFilterData.get(position).mStarName);
        holder.addToFavoriteTv.setChecked(mFilterData.get(position).mIsFavorite == 1);
        holder.goToTargetTv.setEnabled(mIsScopeOnline);
        holder.goToTargetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    if (mIsScopeOnline) {
                        mListener.goToTarget(holder.getAdapterPosition());
                    }
                }
            }
        });
        holder.addToFavoriteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.addToFavorite(mFilterData.get(holder.getAdapterPosition()).mId,
                            mFilterData.get(holder.getAdapterPosition()).mStarName);
                }
            }
        });

        AstroLibraryItem item = mFilterData.get(position);

        if (item.mBrt == null){
            mRightTopMsg = "MAG: "+item.mMag;
        }else {
            mRightTopMsg = "BRT: "+item.mBrt;
        }
        mDateItem = DateUtil.getUTCDateTime();
        String directionValue = "";
        if (mLongitude != -1) {
            Double[] temp = AstroChartUtil.alt_az(mLatitude, mLongitude, mFilterData.get(holder.getAdapterPosition()).mRaNumber * 15,
                    mFilterData.get(holder.getAdapterPosition()).mDecNumber, mDateItem);
            directionValue = "\n" + angleToString(temp[0]);
        }
        String text = mContext.getResources().getString(R.string.Scope_RA)+" " + item.mRA +"   " + "\n"+
                mContext.getResources().getString(R.string.Scope_DEC)+" "+item.mDEC + "\n"+
                mRightTopMsg+directionValue;
        holder.raDecValueTv.setText(text);
        LineChartManager mLineChartManager = new LineChartManager(holder.chart);

        List<BrokenLineView.XValue> xValues = new ArrayList();
        List<BrokenLineView.YValue> yValues = new ArrayList();
        List<BrokenLineView.LineValue> lineValues = new ArrayList();

        for (int i = 0; i <= 12; i++) {
            int trueNum = 0;
            if (i + 18 > 23){
                trueNum = (i + 18) - 24;
            }else {
                trueNum = i + 18;
            }
            if (trueNum < 10) {
                BrokenLineView.XValue xValue = new BrokenLineView.XValue(i, "0" + trueNum + "");
                xValues.add(xValue);
            }else {
                BrokenLineView.XValue xValue = new BrokenLineView.XValue(i, trueNum + "");
                xValues.add(xValue);
            }
        }

        BrokenLineView.YValue yValue = new BrokenLineView.YValue(0, 0 + "");
        yValues.add(yValue);
        BrokenLineView.YValue yValue1 = new BrokenLineView.YValue(30, 30 + "");
        yValues.add(yValue1);
        BrokenLineView.YValue yValue2 = new BrokenLineView.YValue(60, 60 + "");
        yValues.add(yValue2);
        BrokenLineView.YValue yValue3 = new BrokenLineView.YValue(90, 90 + "");
        yValues.add(yValue3);

        if (mFilterData.get(holder.getAdapterPosition()).mMaxLocation != null){
            double pos = (mFilterData.get(holder.getAdapterPosition()).mMaxLocation.get(0)/((22 - 10) * 60 * 60)) * 12 ;

            for (int i = 0; i < mFilterData.get(position).mChartData.size(); i++) {
                double value = mFilterData.get(position).mChartData.get(i);
                if ((pos > i -1) && (pos  < i)){
                    double max_value = mFilterData.get(holder.getAdapterPosition()).mMaxLocation.get(1);
                    BrokenLineView.LineValue lineValue = new BrokenLineView.LineValue(max_value, pos,max_value + "");
                    lineValues.add(lineValue);
                    holder.brokenLineView.setTargetLineAndTitle(true,pos,getTargetTimeValue(pos));
                }
                BrokenLineView.LineValue lineValue = new BrokenLineView.LineValue(value, i,value + "");
                lineValues.add(lineValue);
            }
        }else {
            for (int i = 0; i < mFilterData.get(position).mChartData.size(); i++) {
                double value = mFilterData.get(position).mChartData.get(i);
                BrokenLineView.LineValue lineValue = new BrokenLineView.LineValue(value, i,value + "");
                lineValues.add(lineValue);
            }
            holder.brokenLineView.setTargetLineAndTitle(false,0,"");
            holder.targetLocationView.setVisibility(View.INVISIBLE);
            mLineChartManager.showLineChart(12f, mFilterData.get(position).mChartData,-1, (float) 0);
        }
        holder.brokenLineView.setShowXEffectLine(true);
        holder.brokenLineView.setXAxisName(true,"时间/h");
        holder.brokenLineView.setValue(lineValues, xValues, yValues);
    }

    private String getTargetTimeValue(double pos){
        float deltaTime = (float) (pos + 18);
        int minute = (int)((deltaTime - ((int) deltaTime)) * 60);
        int deltaHour = (int)deltaTime;
        if (deltaHour >= 24){
            deltaHour = deltaHour - 24;
        }
        String time = "";
        if (deltaHour < 10){
            time = "0"+deltaHour;
        }else {
            time = ""+deltaHour;
        }
        if (minute < 10){
            time += ":0"+minute;
        }else {
            time += ":"+minute;
        }
        return time;
    }

    @Override
    public int getItemCount() {
        return mFilterData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterStr = charSequence.toString();

                ArrayList<AstroLibraryItem> filteredList = new ArrayList<>();
                if (filterStr.isEmpty()) {
                    filteredList.addAll(mData);
                } else if(filterStr.equals("30")){
                    for (AstroLibraryItem data : mData) {
                        if (data.mMaxLocation != null && data.mMaxLocation.get(1) > 30){
                            filteredList.add(data);
                        }
                    }
                }
                else {
                    for (AstroLibraryItem data : mData) {
                        if (data.mStarName.toLowerCase().contains(filterStr.toLowerCase())){
                            filteredList.add(data);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterData = (ArrayList<AstroLibraryItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void filterFavorite(){
        ArrayList<AstroLibraryItem> filterTempDatas = new ArrayList<AstroLibraryItem>();
        for (AstroLibraryItem data : mData) {
            if (data.mIsFavorite == 1){
                filterTempDatas.add(data);
            }
        }
        mFilterData = filterTempDatas;
        notifyDataSetChanged();
    }
    public void setOnClickListener( ListSwipeAdapterListener listener) {
        this.mListener = listener;
    }

    public class NormalHolder extends RecyclerView.ViewHolder implements Extension{

        public TextView goToTargetTv;
        public ToggleButton addToFavoriteTv;
        public TextView starNameTv;
        public TextView raDecValueTv;
        public ConstraintLayout swipLL;
        public LineChart chart;
        public ConstraintLayout viewListRepoActionContainer;
        public TargetLocationView targetLocationView;
        public FrameLayout libraryContainerLayout;
        public BrokenLineView brokenLineView;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            goToTargetTv = (TextView) itemView.findViewById(R.id.go_to_target);
            addToFavoriteTv = (ToggleButton) itemView.findViewById(R.id.add_to_favorite_tv);
            starNameTv = (TextView) itemView.findViewById(R.id.star_name_tv);
            raDecValueTv = (TextView) itemView.findViewById(R.id.ra_dec_tv);
            swipLL = (ConstraintLayout) itemView.findViewById(R.id.swipLL);
            viewListRepoActionContainer = (ConstraintLayout) itemView.findViewById(R.id.view_list_repo_action_container);
            chart = (LineChart) itemView.findViewById(R.id.chart);
            targetLocationView = (TargetLocationView) itemView.findViewById(R.id.max_location_v);
            libraryContainerLayout = (FrameLayout) itemView.findViewById(R.id.library_container_layout);
            brokenLineView = (BrokenLineView) itemView.findViewById(R.id.brokenLineView);
        }

        @Override
        public float getActionWidth() {
            return viewListRepoActionContainer.getWidth() + ScreenUtil.androidAutoSizeDpToPx(5);
        }
    }
}
