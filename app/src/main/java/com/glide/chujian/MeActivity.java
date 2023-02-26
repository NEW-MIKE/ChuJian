package com.glide.chujian;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.glide.chujian.adapter.FutureMsgAdapter;
import com.glide.chujian.adapter.SpacesItemDecorationLine;
import com.glide.chujian.databinding.ActivityMeBinding;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.DateUtil;

import java.util.ArrayList;

public class MeActivity extends BaseActivity{
    private final static String TAG = MeActivity.class.getSimpleName();
    private ActivityMeBinding binding;
    private ArrayAdapter<String> mMeChoiceAdapter;
    private ArrayList<String> mTopicNameList = new ArrayList<>();
    private ArrayList<String> mTopicContentList = new ArrayList<>();
    private String mTopicType = "";
    private FutureMsgAdapter mFutureMsgAdapter;
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MeActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }

    @Override
    public void updateWifiName(String wifi_name) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        init();
        loadTopicNameList();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.topicRv.setLayoutManager(mLayoutManager);
        binding.topicRv.addItemDecoration(new SpacesItemDecorationLine(this,18));
        mFutureMsgAdapter = new FutureMsgAdapter(mTopicContentList,this);
        binding.topicRv.setAdapter(mFutureMsgAdapter);
        if (mTopicNameList.size() != 0){
            loadTopicContentList(mTopicNameList.get(0));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void init(){
        initView();
        initEvent();
    }

    private void initView(){
        mMeChoiceAdapter =
                new ArrayAdapter<String>(this, R.layout.setting_spinner_item);
        mMeChoiceAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        binding.selectChoice.setAdapter(mMeChoiceAdapter);
    }


    private void initEvent(){
        binding.selectChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTopicType = mMeChoiceAdapter.getItem(position);
                loadTopicContentList(mTopicType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.addTopicNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = binding.topicEditEt.getText().toString();
                if (!topicName.trim().equals("")){
                    if (!mTopicNameList.contains(topicName)) {
                        mTopicNameList.add(topicName);
                        insertTopicName(topicName);
                    }
                }
                binding.topicEditEt.getText().clear();
                loadTopicNameList();
            }
        });
        binding.topicEditEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.topicEditEt.clearFocus();
                Log.e(TAG, "onEditorAction: cannotclick 软键盘收起，清除焦点" );
                ((InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.topicEditEt.getWindowToken(),0);
                return true;
            }
        });
        binding.topicContentEditEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.topicContentEditEt.clearFocus();
                Log.e(TAG, "onEditorAction: cannotclick 软键盘收起，清除焦点" );
                ((InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.topicContentEditEt.getWindowToken(),0);
                return true;
            }
        });
        binding.editTopicContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = binding.topicContentEditEt.getText().toString();
                if (!mTopicType.equals("") && !content.equals("")) {
                    editTopicContent(mTopicType, content);
                    loadTopicContentList(mTopicType);
                }
                binding.topicContentEditEt.getText().clear();
            }
        });
    }

    private void insertTopicName(String topicName){
        mDb = mDBOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("topicname",topicName);
        mDb.insert(Constant.DB_TABLE_ME_TOPIC_NAME,"nullback",contentValues);
        mDb.close();
    }
    private void editTopicContent(String type,String content){
        mDb = mDBOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("type",type);
        contentValues.put("content",content);
        mDb.insert(Constant.DB_TABLE_ME_MODEL_NAME,"nullback",contentValues);
        mDb.close();
    }

    private void loadTopicNameList(){
        mDb = mDBOpenHelper.getWritableDatabase();
        Cursor cursor = mDb.query(Constant.DB_TABLE_ME_TOPIC_NAME, new String[]{"topicname"}, null, null, null, null, null);
        cursor.moveToFirst();

        mTopicNameList.clear();
        while (!cursor.isAfterLast()) {
            String topic = cursor.getString(0);
            mTopicNameList.add(topic);
            cursor.moveToNext();
        }
        mDb.close();
        mMeChoiceAdapter.clear();
        mMeChoiceAdapter.addAll(mTopicNameList);
        mMeChoiceAdapter.notifyDataSetChanged();
    }
    private void loadTopicContentList(String type){
        mDb = mDBOpenHelper.getWritableDatabase();
        Cursor cursor = mDb.query(Constant.DB_TABLE_ME_MODEL_NAME, new String[]{"type","content"}, null, null, null, null, null);
        cursor.moveToFirst();
        mTopicContentList.clear();
        mTopicNameList.clear();
        while (!cursor.isAfterLast()) {
            String typeL = cursor.getString(0);
            String content = cursor.getString(1);
            if (type.equals(typeL)) {
                mTopicContentList.add(content);
            }
            cursor.moveToNext();
        }
        mDb.close();
        mFutureMsgAdapter.notifyDataSetChanged();
    }
}
