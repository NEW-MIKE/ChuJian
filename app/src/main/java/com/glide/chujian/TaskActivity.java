package com.glide.chujian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.glide.chujian.adapter.SpacesItemDecoration;
import com.glide.chujian.adapter.TaskAdapterListener;
import com.glide.chujian.adapter.TaskPlanAdapter;
import com.glide.chujian.databinding.ActivityTaskBinding;
import com.glide.chujian.model.SequencePlanItem;
import com.glide.chujian.util.Guider;
import com.glide.chujian.util.TpTaskHandler;
import com.glide.chujian.util.CheckParamUtil;
import com.glide.chujian.view.DeleteConfirmDialog;
import com.glide.chujian.view.NormalInputFilter;
import com.glide.chujian.view.TaskDialogView;
import com.glide.chujian.view.TaskDragItemHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends BaseActivity {
    private ActivityTaskBinding binding;
    private ArrayList<SequencePlanItem> mData =new ArrayList<SequencePlanItem>();
    private TaskPlanAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ArrayAdapter<String> mSequenceModeAdapter;
    private TpTaskHandler mHandler;
    private Guider mGuider;

    @Override
    public void updateWifiName(String wifi_name) {
        if (wifi_name != null) {
            binding.bottom.leftTv.setText(wifi_name.replace("\"", ""));
        }
        if (wifi_name.equals("")){
            mGuider.setHeartBeatTimeOut();
            connectSuccess(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mHandler = new TpTaskHandler(this);
        mGuider = Guider.getInstance();
        mGuider.registerHandlerListener(mHandler);
        init();
    }

    private void init(){
        initView();
        initEvent();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TaskActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }
    private void initView(){
        connectSuccess(Guider.getInstance().mIsNetworkOnLine);
        mSequenceModeAdapter = new
                ArrayAdapter<String>(this, R.layout.simple_spinner_item);
        mSequenceModeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        List<String> sequenceMode = new ArrayList<>();
        sequenceMode.add("One by One");
        sequenceMode.add("Other");
        mSequenceModeAdapter.addAll(sequenceMode);
        binding.leftLayout.selectSequenceMode.setAdapter(mSequenceModeAdapter);


        mData.add(new SequencePlanItem(TaskPlanAdapter.TYPE_ADD));
        GridLayoutManager mlayoutManager =new GridLayoutManager(this,3);
        mlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.sequenceRv.setLayoutManager(mlayoutManager);
        binding.sequenceRv.addItemDecoration(new SpacesItemDecoration(18,3));
        mAdapter = new TaskPlanAdapter(mData,this);
        binding.sequenceRv.setAdapter(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(new TaskDragItemHelperCallback(mData,
                mAdapter
        ));
        mItemTouchHelper.attachToRecyclerView(binding.sequenceRv);

        mAdapter.setOnClickListener(new TaskAdapterListener() {
            @Override
            public void addItem() {
                TaskDialogView taskDialog =new TaskDialogView(TaskActivity.this, R.style.NormalDialogStyle,null,-1);
                taskDialog.setCanceledOnTouchOutside(true);
                taskDialog.setOnClickListener(new TaskDialogView.TaskDialogListener() {
                    @Override
                    public void setTaskBlock(SequencePlanItem sequencePlanItem) {
                        if (mData.size() > 0){
                            mData.remove(mData.size() - 1);
                        }
                        mData.add(sequencePlanItem);
                        mData.add(new SequencePlanItem(TaskPlanAdapter.TYPE_ADD));
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void editTaskBlock(SequencePlanItem sequencePlanItem, int position) {

                    }
                }).show();
            }

            @Override
            public void selectClick(int position) {
                mData.get(position).mIsSelected = !mData.get(position).mIsSelected;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void editItem(int position) {
                TaskDialogView taskDialog = new TaskDialogView(TaskActivity.this, R.style.NormalDialogStyle, mData.get(position),position);
                taskDialog.setCanceledOnTouchOutside(true);
                taskDialog.setOnClickListener(new TaskDialogView.TaskDialogListener() {
                    @Override
                    public void setTaskBlock(SequencePlanItem sequencePlanItem) {

                    }

                    @Override
                    public void editTaskBlock(SequencePlanItem sequencePlanItem, int position) {
                        mData.get(position).mPlanName = sequencePlanItem.mPlanName;
                        mData.get(position).mBinName = sequencePlanItem.mBinName;
                        mData.get(position).mExposeTime = sequencePlanItem.mExposeTime;
                        mData.get(position).mTotalNumber = sequencePlanItem.mTotalNumber;
                        mAdapter.notifyDataSetChanged();
                    }
                }).show();
            }

            @Override
            public void deleteItem(int position) {
                DeleteConfirmDialog.Builder mBuider = new DeleteConfirmDialog.Builder(TaskActivity.this);
                DeleteConfirmDialog dialog = mBuider.create();
                mBuider.setOnDeleteListener(new DeleteConfirmDialog.OnDeleteListener() {
                    @Override
                    public void delete() {
                        mData.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyItemRangeChanged(position, mData.size() - position);
                        dialog.dismiss();
                    }

                    @Override
                    public void cancel() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void exitActivity(){
        mGuider.unregisterHandlerListener(mHandler);
        finish();
    }
    private void initEvent(){
        binding.leftLayout.decvalueTv.setFilters(new InputFilter[]{new NormalInputFilter()});
        binding.leftLayout.selectSequenceMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.title.backGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });
        binding.title.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });

        binding.leftLayout.startGuidingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        binding.leftLayout.slewToTargetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        binding.leftLayout.centerTargetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        binding.leftLayout.powerOffUponFinishSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        binding.leftLayout.resetParkPositionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        binding.leftLayout.autoFocuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.leftLayout.restBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        binding.leftLayout.targetEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.leftLayout.targetEt.clearFocus();
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.leftLayout.targetEt.getWindowToken(), 0);
                return true;
            }
        });
        binding.leftLayout.targetEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    String text = binding.leftLayout.targetEt.getText().toString();
                    CheckParamUtil.isRangeLegal(TaskActivity.this,text,1.0,10000.0,1.0,"");
                }
            }
        });
        binding.leftLayout.delayDegreeTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.leftLayout.delayDegreeTv.clearFocus();
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.leftLayout.delayDegreeTv.getWindowToken(), 0);
                return true;
            }
        });
        binding.leftLayout.delayDegreeTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });
        updateBottomInfo("01:30:35","20");
    }

    private void updateBottomInfo(String finishTime,String totalFrame){
        binding.bottom.rightTv.setText("Est.finish time:   "+finishTime+"       Total Frame:    "+totalFrame);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN : {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View view = getCurrentFocus(); //获得当前聚焦控件
                                if (view instanceof EditText) {
                                    Rect rect = new Rect();
                                    view.getGlobalVisibleRect(rect); //获得控件在屏幕上的显示区域
                                    //判断：如果点击区域不在控件中
                                    if (!rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                                        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.leftLayout.targetEt.getWindowToken(), 0);
                                        view.clearFocus() ;//清除焦点
                                    }
                                }
                            }
                        });
                    }
                }.start();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public void connectSuccess(boolean isConnected){
        if (isConnected){
            binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_success));
        }else {
            binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_fail));
        }
    }
    public void connectionLostEvent(){
        mCheckConnectProcess.showConnectingDialog();
        connectSuccess(false);
    }
    public void connectionSuccessEvent(){
        mCheckConnectProcess.dismissConnectedDialog();
        connectSuccess(true);
    }

    public void startLoadAllDataCache(){
        mCheckConnectProcess.showDataLoadingDialog();
    }
    public void finishLoadAllDataCache(){
        mCheckConnectProcess.dismissDataLoadingDialog();
    }
}
