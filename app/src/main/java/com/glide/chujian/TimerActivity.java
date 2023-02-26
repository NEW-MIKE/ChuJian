package com.glide.chujian;

import static com.glide.chujian.util.DateUtil.getEndTimeStamp;
import static com.glide.chujian.util.DateUtil.getStartTimeStamp;
import static com.glide.chujian.util.DateUtil.timeStampToDate;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.glide.chujian.adapter.FutureMsgAdapter;
import com.glide.chujian.adapter.ListSwipeAdapter;
import com.glide.chujian.adapter.SpacesItemDecorationLine;
import com.glide.chujian.databinding.ActivityTimerBinding;
import com.glide.chujian.db.DBOpenHelper;
import com.glide.chujian.model.AstroLibraryItem;
import com.glide.chujian.model.DateItem;
import com.glide.chujian.util.AstroChartUtil;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.DateUtil;
import com.glide.chujian.util.GuideUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimerActivity extends BaseActivity {

    private final String TAG = TimerActivity.class.getSimpleName();
    private ActivityTimerBinding binding;
    private ArrayList<String> mReceiveDataList = new ArrayList<>();
    private FutureMsgAdapter mFutureMsgAdapter;
    @Override
    public void updateWifiName(String wifi_name) {

    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TimerActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        init();
        binding.chooseDateValueTv.setText(DateUtil.getCurrentDateNumber());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.receiveFutureRv.setLayoutManager(mLayoutManager);
        binding.receiveFutureRv.addItemDecoration(new SpacesItemDecorationLine(this,18));
        mFutureMsgAdapter = new FutureMsgAdapter(mReceiveDataList,this);
        binding.receiveFutureRv.setAdapter(mFutureMsgAdapter);
    }
    public void insert(String futureDay,String msg){
        mDb = mDBOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("future_day",futureDay);
        contentValues.put("message",msg);
        mDb.insert(Constant.DB_TABLE_NAME,"future_day",contentValues);
        // 释放连接
        mDb.close();
    }
    private void init(){
        initEvent();
    }

    private void initEvent(){
        binding.chooseDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(TimerActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        binding.chooseDateValueTv.setText(getTime(date));
                    }
                }).build();
                pvTime.show();
            }
        });

        binding.sendMsgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.msgEd.getText().toString();
                String date = binding.chooseDateValueTv.getText().toString();
                if (!msg.trim().equals("") && !date.trim().equals("")){
                    insert(date,msg);
                }
                binding.msgEd.getText().clear();
            }
        });
        binding.messageModeSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.receiveFutureRv.setVisibility(View.INVISIBLE);
                binding.msgEd.setVisibility(View.VISIBLE);
                binding.sendMsgTv.setVisibility(View.VISIBLE);
            }
        });

        binding.messageModeReceiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMsgToday();
                mFutureMsgAdapter.notifyDataSetChanged();
                binding.msgEd.setVisibility(View.INVISIBLE);
                binding.receiveFutureRv.setVisibility(View.VISIBLE);
                binding.sendMsgTv.setVisibility(View.INVISIBLE);
            }
        });

        binding.msgEd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.msgEd.clearFocus();
                ((InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(binding.msgEd.getWindowToken(),0);
                return true;
            }
        });
    }

    public void loadMsgToday(){
        mDb = mDBOpenHelper.getWritableDatabase();
        Cursor cursor = mDb.query(Constant.DB_TABLE_NAME, new String[]{"future_day","message"}, null, null, null, null, null);
        cursor.moveToFirst();

        mReceiveDataList.clear();
        while (!cursor.isAfterLast()) {
            String date = cursor.getString(0);
            String msg = cursor.getString(1);
            if (DateUtil.getCurrentDateNumber().equals(date)) {
                mReceiveDataList.add(date + " : " + msg);
            }
            cursor.moveToNext();
        }
        mDb.close();
    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}