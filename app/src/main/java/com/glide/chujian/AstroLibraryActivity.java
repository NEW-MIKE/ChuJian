package com.glide.chujian;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.glide.chujian.adapter.ItemTouchHelperExtension;
import com.glide.chujian.adapter.ListSwipeAdapter;
import com.glide.chujian.adapter.ListSwipeAdapterListener;
import com.glide.chujian.adapter.ListSwipeTouchHelperCallBack;
import com.glide.chujian.adapter.SearchHistoryAdapter;
import com.glide.chujian.adapter.SearchHistoryAdapterListener;
import com.glide.chujian.adapter.SpacesItemDecorationLine;
import com.glide.chujian.databinding.ActivityAstroLibraryBinding;
import com.glide.chujian.db.AstroLibraryCache;
import com.glide.chujian.db.DBOpenHelper;
import com.glide.chujian.model.AstroLibraryItem;
import com.glide.chujian.util.AstroLibraryHandler;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.Guider;
import com.glide.chujian.util.ScreenUtil;
import com.glide.chujian.util.SharedPreferencesUtil;
import com.glide.chujian.view.LibraryMenuPop;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AstroLibraryActivity extends BaseActivity{
    private final String TAG = "AstroLibraryActivity";
    private ActivityAstroLibraryBinding binding;
    private ArrayList<AstroLibraryItem> mGuideData = new ArrayList<>();
    private ArrayList<ToggleButton> mToggleViewList =new ArrayList<>();
    private ItemTouchHelperExtension mItemTouchHelper;
    private ListSwipeAdapter mAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private ArrayList<String> mSearchHistoryData = new ArrayList<>();
    private LibraryMenuPop mLibraryMenuPop;
    private Guider mGuider;
    private DBOpenHelper mDBOpenHelper;
    private SQLiteDatabase mDb;
    private AstroLibraryHandler mAstroLibraryHandler;
    private UpdateTimerTask mUpdateTimerTask;
    private Timer mUpdateTimer;
    private boolean mIsEnterSearch = false;
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

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void startUpdateTimer() {
        stopUpdateTimer();
        mUpdateTimer = new Timer(true);
        mUpdateTimerTask = new UpdateTimerTask();
        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000 * 60);
    }

    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUpdateTimer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAstroLibraryBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mGuider = Guider.getInstance();
        mAstroLibraryHandler = new AstroLibraryHandler(this);
        init();

        mDBOpenHelper = new DBOpenHelper(AstroLibraryActivity.this,Constant.DB_NAME,null,1);
        mDb = mDBOpenHelper.getWritableDatabase();
        mGuideData.clear();
        mGuideData.addAll(AstroLibraryCache.getAstroLibraryCache());

        new Thread(){
            @Override
            public void run() {
                super.run();
                mGuider.updateCoordData();
            }
        }.start();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.ListSwipeRv.setLayoutManager(mLayoutManager);
        binding.ListSwipeRv.addItemDecoration(new SpacesItemDecorationLine(this,18));
        mAdapter = new ListSwipeAdapter(mGuideData,mGuider.mScopeIsDeviceConnected,this);
        binding.ListSwipeRv.setAdapter(mAdapter);
        manageGotoStatus();
        mAdapter.setOnClickListener(new ListSwipeAdapterListener() {
            @Override
            public void addToFavorite(int id, String name) {
                for (int i = 0; i < mGuideData.size(); i++){
                    if ((mGuideData.get(i).mId == id)){
                        Log.e(TAG, "addToFavorite: mGuideData.get(i).mId"+mGuideData.get(i).mId+"  :" +id);
                        mGuideData.get(i).mIsFavorite = 1 - mGuideData.get(i).mIsFavorite;
                        update(id, mGuideData.get(i).mIsFavorite);
                    }
                }

                mAdapter.notifyDataSetChanged();
                mItemTouchHelper.closeOpened();

                if (binding.rightLayout.favoriteBtn.isChecked() && !mIsEnterSearch) {
                    mAdapter.filterFavorite();
                }
            }

            @Override
            public void goToTarget(int i) {
                mItemTouchHelper.closeOpened();
                mAdapter.notifyDataSetChanged();

                Intent intent = new Intent();
                intent.putExtra("target_ra", mGuideData.get(i).mRaNumber.doubleValue());
                intent.putExtra("target_dec", mGuideData.get(i).mDecNumber.doubleValue());
                Log.e(TAG, "goToTarget: "+ mGuideData.get(i).mRaNumber.doubleValue()+":"+ mGuideData.get(i).mDecNumber.doubleValue());
                setResult(100,intent);
                exitActivity();
            }
        });

        mItemTouchHelper = new ItemTouchHelperExtension(new ListSwipeTouchHelperCallBack(mGuideData, mAdapter));
        mItemTouchHelper.attachToRecyclerView(binding.ListSwipeRv);
    }

    private void exitActivity(){
        mGuider.unregisterHandlerListener(mAstroLibraryHandler);
        finish();
    }


    private void updateHistoryRv(){
        mSearchHistoryData.clear();
        mSearchHistoryData.addAll(SharedPreferencesUtil.getInstance().readSearchHistory(Constant.SP_SEARCH_HISTORY_ID));
        if (mSearchHistoryAdapter != null){
            mSearchHistoryAdapter.notifyDataSetChanged();
        }
    }
    private void initData(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.historySearchListRv.setLayoutManager(mLayoutManager);
        binding.historySearchListRv.addItemDecoration(new SpacesItemDecorationLine(this,1));
        mSearchHistoryData.addAll(SharedPreferencesUtil.getInstance().readSearchHistory(Constant.SP_SEARCH_HISTORY_ID));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mSearchHistoryData,this);
        binding.historySearchListRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.setOnClickListener(new SearchHistoryAdapterListener() {
            @Override
            public void chooseItem(int position) {
                binding.librarySearchSv.setText(mSearchHistoryData.get(position));
                setSearchHistoryState(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGuider.registerHandlerListener(mAstroLibraryHandler);
        setCurrentFilter(mGuider.mAstroLibraryCurrentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdateTimer();
    }
    private void init(){
        initView();
        initEvent();
        initData();
    }
    public void update(int id,int isFavorite){
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("isFavorite",isFavorite);
        db.update(Constant.DB_TABLE_NAME,contentValues,"uid=?",new String[]{id+""});
        // 释放连接
        db.close();
    }

    private void setCurrentFilter(int currentFilter){
        mGuider.mAstroLibraryCurrentFilter = currentFilter;
        Log.e(TAG, "setCurrentFilter: "+currentFilter);
        switch (currentFilter){
            case 0:{
                mAdapter.getFilter().filter("");
                break;
            }
            case 1:{
                binding.rightLayout.favoriteBtn.setChecked(true);
                mAdapter.filterFavorite();
                break;
            }
            case 2:{
                binding.rightLayout.filter1Btn.setChecked(true);
                mAdapter.getFilter().filter("30");
                break;
            }
            case 3:{
                binding.rightLayout.filter2Btn.setChecked(true);
                mAdapter.getFilter().filter("A");
                break;
            }
            case 4:{
                binding.rightLayout.filter3Btn.setChecked(true);
                mAdapter.getFilter().filter("IC45");
                break;
            }
            case 5:{
                mLibraryMenuPop.setCurrentFilter(0);
                mAdapter.getFilter().filter("a");
                break;
            }
            case 6:{
                mLibraryMenuPop.setCurrentFilter(1);
                mAdapter.getFilter().filter("4");
                break;
            }
        }
    }


    private void initView(){
        connectSuccess(Guider.getInstance().mIsNetworkOnLine);
        mLibraryMenuPop = new LibraryMenuPop(this);
        mLibraryMenuPop.setWidth(ScreenUtil.androidAutoSizeDpToPx(100));
        mLibraryMenuPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mToggleViewList.add(binding.rightLayout.favoriteBtn);
        mToggleViewList.add(binding.rightLayout.filter1Btn);
        mToggleViewList.add(binding.rightLayout.filter2Btn);
        mToggleViewList.add(binding.rightLayout.filter3Btn);
    }

    private void initEvent(){
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
        binding.title.menuTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLibraryMenuPop.showAtLocation(binding.title.menuTv, Gravity.TOP | Gravity.RIGHT,
                        binding.rightLayout.libraryRightSide.getWidth()+ScreenUtil.androidAutoSizeDpToPx(getResources().getInteger(R.integer.pop_margin_side)),
                        binding.title.title.getHeight()+ScreenUtil.androidAutoSizeDpToPx(getResources().getInteger(R.integer.pop_margin_side)));
            }
        });

        binding.librarySearchSv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = binding.librarySearchSv.getText().toString();
                if (!hasFocus) {
                    if (!SharedPreferencesUtil.getInstance().readSearchHistory(Constant.SP_SEARCH_HISTORY_ID).contains(text)) {
                        SharedPreferencesUtil.getInstance().insertSearchHistory(Constant.SP_SEARCH_HISTORY_ID, text);
                    }
                }
            }
        });
        binding.librarySearchSv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.getFilter().filter(charSequence);
                if (charSequence.toString().trim().equals("")) {
                    setSearchHistoryState(true);
                    binding.ListSwipeRv.setVisibility(View.GONE);
                    updateHistoryRv();
                }else {
                    setSearchHistoryState(false);
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
                                    binding.ListSwipeRv.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }.start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.librarySearchSv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                binding.librarySearchSv.clearFocus();
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.librarySearchSv.getWindowToken(),0);
                return true;
            }
        });

        binding.rightLayout.findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.librarySearchSv.getText().clear();
                setSearchState(true);
            }
        });

        binding.rightLayout.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealFiltersConflict(binding.rightLayout.favoriteBtn);
                if (binding.rightLayout.favoriteBtn.isChecked()) {
                    setCurrentFilter(1);
                } else {
                    setCurrentFilter(0);
                }
            }
        });

        binding.rightLayout.filter1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealFiltersConflict(binding.rightLayout.filter1Btn);
                if (binding.rightLayout.filter1Btn.isChecked()) {
                    setCurrentFilter(2);
                } else {
                    setCurrentFilter(0);
                }
            }
        });
        binding.rightLayout.filter2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                    }
                }.start();
                dealFiltersConflict(binding.rightLayout.filter2Btn);
                if (binding.rightLayout.filter2Btn.isChecked()) {
                    setCurrentFilter(3);
                } else {
                    setCurrentFilter(0);
                }
            }
        });

        binding.rightLayout.filter3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                    }
                }.start();
                dealFiltersConflict(binding.rightLayout.filter3Btn);
                if (binding.rightLayout.filter3Btn.isChecked()) {
                    setCurrentFilter(4);
                } else {
                    setCurrentFilter(0);
                }
            }
        });

        binding.exitSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchState(false);
                setCurrentFilter(mGuider.mAstroLibraryCurrentFilter);
            }
        });

        binding.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.librarySearchSv.getText().clear();
                setSearchHistoryState(true);
            }
        });

        binding.deleteHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.getInstance().deleteSearchHistory(Constant.SP_SEARCH_HISTORY_ID);

                mSearchHistoryData.clear();
                if (mSearchHistoryAdapter != null){
                    mSearchHistoryAdapter.notifyDataSetChanged();
                }
                setSearchHistoryState(true);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.librarySearchSv.getWindowToken(), 0);
        if (getCurrentFocus() instanceof EditText) {
            getCurrentFocus().clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setSearchHistoryView(boolean isSearchState,boolean hasSearchData,boolean hasHistoryData){
        if (isSearchState){
            if (hasSearchData){
                binding.historyContainer.setVisibility(View.GONE);
            }else {
                if (hasHistoryData){
                    binding.historyContainer.setVisibility(View.VISIBLE);
                }else {
                    binding.historyContainer.setVisibility(View.GONE);
                }
            }
        }else {
            binding.historyContainer.setVisibility(View.GONE);
        }
    }

    private void setSearchHistoryState(boolean isSearchState){
        boolean hasSearchData = false,hasHistoryData = false;
        if (!binding.librarySearchSv.getText().toString().trim().equals("")){
            hasSearchData = true;
        }
        if (mSearchHistoryData.size() != 0){
            hasHistoryData = true;
        }
        setSearchHistoryView(isSearchState,hasSearchData,hasHistoryData);
    }

    private void setSearchState(boolean isEnterSearch){
        mIsEnterSearch = isEnterSearch;
        setSearchHistoryState(isEnterSearch);
        if (isEnterSearch){
            binding.rightLayoutContainer.setVisibility(View.GONE);
            binding.searchContainer.setVisibility(View.VISIBLE);
            binding.ListSwipeRv.setVisibility(View.GONE);
        }else {
            binding.rightLayoutContainer.setVisibility(View.VISIBLE);
            binding.searchContainer.setVisibility(View.GONE);
            binding.ListSwipeRv.setVisibility(View.VISIBLE);
        }
    }

    public void filter0(){
        setCurrentFilter(5);
        dealFiltersConflict(null);
    }
    public void filter1(){
        setCurrentFilter(6);
        dealFiltersConflict(null);
    }


    private void dealFiltersConflict(ToggleButton toggleButton) {
        for (ToggleButton it:mToggleViewList){
            if (it != toggleButton) {
                it.setChecked(false);
            }
        }
        if (toggleButton != null){
            mLibraryMenuPop.singleChooseView(null);
        }
    }

    public static void actionStart(Activity context) {
        Intent intent = new Intent(context, AstroLibraryActivity.class);
        context.startActivityForResult(intent,100);
        ((Activity)context).overridePendingTransition(0, 0);
    }

    public void connectionLostEvent(){
        mCheckConnectProcess.showConnectingDialog();
        manageGotoStatus();
        connectSuccess(false);
    }

    public void manageGotoStatus(){
        if (mAdapter != null) {
            if (mGuider.mScopeIsDeviceConnected){
                if (mGuider.mIsGuidingStarted){
                    mAdapter.setScopeOnlineStatus(false);
                }else {
                    mAdapter.setScopeOnlineStatus(true);
                }
            }else {
                mAdapter.setScopeOnlineStatus(false);
            }
        }
    }

    public void connectionSuccessEvent(){
        mCheckConnectProcess.dismissConnectedDialog();
        manageGotoStatus();
        connectSuccess(true);
    }

    public void connectSuccess(boolean isConnected){
        if (isConnected){
            binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_success));
        }else {
            binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_fail));
        }
    }


    public void startLoadAllDataCache(){
        mCheckConnectProcess.showDataLoadingDialog();
    }
    public void finishLoadAllDataCache(){
        mCheckConnectProcess.dismissDataLoadingDialog();
        manageGotoStatus();
    }

    public void deviceConnectEvent(){
        if (!mGuider.mCurrentConnectedDevice.equals("mount")) {
            return;
        }
        manageGotoStatus();
    }
}
