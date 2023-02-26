package com.glide.chujian.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.glide.chujian.R;
import com.glide.chujian.databinding.TaskDialogLayoutBinding;
import com.glide.chujian.model.SequencePlanItem;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskDialogView extends Dialog {
    private Context mContext;
    private TaskDialogLayoutBinding binding;
    private int themeResId;
    private SequencePlanItem mSequencePlanItemEdit;
    private SequencePlanItem mSequencePlanItem;
    private int mPosition;
    private Integer[] mFieldList = {R.string.Light,R.string.Bias,R.string.Dark, R.string.Flat};
    private int mFieldIndex;
    private TaskDialogListener mListener;

    private ArrayAdapter mExposeTimeAdapterList;
    private ArrayAdapter mTotalAdapterList;
    private ArrayAdapter mBinTimeAdapterList;
    private ArrayList<ToggleButton> mToggleBtnList = new ArrayList<ToggleButton>();

    public TaskDialogView(@NonNull Context context, int themeResId,SequencePlanItem mSequencePlanItemEdit, int position) {
        super(context, themeResId);
        this.mContext = context;
        this.themeResId = themeResId;
        this.mSequencePlanItemEdit = mSequencePlanItemEdit;
        this.mPosition = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TaskDialogLayoutBinding.inflate(LayoutInflater.from(mContext));
        setContentView(binding.getRoot());
        init();
    }

    private void init(){
        this.mExposeTimeAdapterList = new ArrayAdapter(mContext, R.layout.task_panel_spinner_item);
        this.mTotalAdapterList = new ArrayAdapter(mContext, R.layout.task_panel_spinner_item);
        this.mBinTimeAdapterList = new ArrayAdapter(mContext, R.layout.task_panel_spinner_item);

        String[] expData = new String[]{"0.01s","0.02s","0.05s","0.1s","0.2s","0.5s","1.0s","2.0s","3.0s","4.0s","5.0s" ,"10.0s"};
        String[] totalData = new String[]{"1","2","5","10","15","20","50","100","200"};
        String[] binData = new String[]{"Off","1x1","2x2","4x4"};
        mExposeTimeAdapterList.addAll(Arrays.asList(expData));
        mExposeTimeAdapterList.setDropDownViewResource(R.layout.task_panel_spinner_dropdown_item);
        binding.selectExposureTime.setAdapter(mExposeTimeAdapterList);

        mTotalAdapterList.addAll(Arrays.asList(totalData));
        mTotalAdapterList.setDropDownViewResource(R.layout.task_panel_spinner_dropdown_item);
        binding.selectTotal.setAdapter(mTotalAdapterList);

        mBinTimeAdapterList.addAll(Arrays.asList(binData));
        mBinTimeAdapterList.setDropDownViewResource(R.layout.task_panel_spinner_dropdown_item);
        binding.selectBin.setAdapter(mBinTimeAdapterList);

        binding.selectExposureTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSequencePlanItem.mExposeTime = mExposeTimeAdapterList.getItem(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.selectTotal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSequencePlanItem.mTotalNumber = mTotalAdapterList.getItem(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.selectBin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSequencePlanItem.mBinName = mBinTimeAdapterList.getItem(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mToggleBtnList.add(binding.lightTbtn);
        mToggleBtnList.add(binding.biasTbtn);
        mToggleBtnList.add(binding.darkTbtn);
        mToggleBtnList.add(binding.flatTbtn);


        if (mSequencePlanItemEdit != null){
            mSequencePlanItem = mSequencePlanItemEdit;
            int size = mFieldList.length;
            for (int i = 0; i < size;i++){
                if (mContext.getResources().getString(mFieldList[i]) == mSequencePlanItemEdit.mPlanName){
                    mToggleBtnList.get(i).setChecked(true);
                    mFieldIndex = i;
                }
            }
            for (int i = 0;i < mExposeTimeAdapterList.getCount();i++){
                if (mExposeTimeAdapterList.getItem(i).toString() == mSequencePlanItemEdit.mExposeTime){
                    binding.selectExposureTime.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < mTotalAdapterList.getCount();i++){
                if (mTotalAdapterList.getItem(i).toString() == mSequencePlanItemEdit.mTotalNumber){
                    binding.selectTotal.setSelection(i);
                    break;
                }
            }
            for (int i = 0;i < mBinTimeAdapterList.getCount();i++){
                if (mBinTimeAdapterList.getItem(i).toString() == mSequencePlanItemEdit.mBinName){
                    binding.selectBin.setSelection(i);
                    break;
                }
            }
        }
        else{
            mSequencePlanItem = new SequencePlanItem("0.01s", "Off", "1",true);
            binding.lightTbtn.setChecked(true);
        }

        binding.enterTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSequencePlanItemEdit != null){
                    mSequencePlanItem.mPlanName = mContext.getResources().getString(mFieldList[mFieldIndex]);
                    if (mListener != null) {
                        mListener.editTaskBlock(mSequencePlanItem, mPosition);
                    }
                }
                else {
                    mSequencePlanItem.mPlanName = mContext.getResources().getString(mFieldList[mFieldIndex]);
                    if (mListener != null) {
                        mListener.setTaskBlock(mSequencePlanItem);
                    }
                }
                dismiss();
            }
        });

        binding.cancelTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.lightTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFieldIndex = 0;
                dealTypeConflict(binding.lightTbtn);
            }
        });
        binding.biasTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFieldIndex = 1;
                dealTypeConflict(binding.biasTbtn);
            }
        });
        binding.darkTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFieldIndex = 2;
                dealTypeConflict(binding.darkTbtn);
            }
        });
        binding.flatTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFieldIndex = 3;
                dealTypeConflict(binding.flatTbtn);
            }
        });
    }

    public TaskDialogView setOnClickListener(TaskDialogListener listener){
        this.mListener = listener;
        return this;
    }

    private void dealTypeConflict(ToggleButton toggleButton){
        toggleButton.setChecked(true);
        for (ToggleButton toggleButton1:mToggleBtnList){
            if (toggleButton1 != toggleButton){
                toggleButton1.setChecked(false);
            }
        }
    }
    public interface TaskDialogListener {
        void setTaskBlock(SequencePlanItem sequencePlanItem);

        void editTaskBlock(SequencePlanItem sequencePlanItem, int position);
    }
}
