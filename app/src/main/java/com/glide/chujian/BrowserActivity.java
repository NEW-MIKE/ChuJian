package com.glide.chujian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.glide.chujian.adapter.BrowserAdapter;
import com.glide.chujian.adapter.BrowserAdapterListener;
import com.glide.chujian.adapter.BrowserPathAdapter;
import com.glide.chujian.adapter.BrowserPathAdapterListener;
import com.glide.chujian.adapter.BrowserPictureAdapter;
import com.glide.chujian.adapter.SpacesItemDecoration;
import com.glide.chujian.databinding.ActivityBrowserBinding;
import com.glide.chujian.model.BrowserInfo;
import com.glide.chujian.util.Constant;
import com.glide.chujian.util.FileUtils;
import com.glide.chujian.util.Guider;
import com.glide.chujian.util.ToastUtil;
import com.glide.chujian.util.TpBrowserHandler;
import com.glide.chujian.view.DeleteConfirmDialog;
import com.glide.chujian.view.RecyclerViewPageChangeListenerHelper;

import java.io.File;
import java.util.ArrayList;

public class BrowserActivity extends BaseActivity {
    private String TAG = "BrowserActivity";
    private ActivityBrowserBinding binding;
    private ArrayList<BrowserInfo> mBrowserList = new ArrayList<BrowserInfo>();
    private ArrayList<String> mPathList =new ArrayList<String>();
    private String mPath = "";
    private String mFileType = "";
    private boolean mIsSurfaceHoldBound = false;
    private BrowserAdapter mBrowserAdapter;
    private BrowserPathAdapter mPathAdapter;
    private TpBrowserHandler mHandler  = new TpBrowserHandler(this);
    private MediaPlayer mMediaPlayer;
    private String mCurrentFileName = "";
    private boolean mIsPlaying;
    private ArrayList<Long> mFitsNameList = new ArrayList<>();
    private ArrayList<Long> mVideoNameList = new ArrayList<>();
    private ArrayList<String> mRootFileNameList = new ArrayList<>();
    private PagerSnapHelper mSnapHelper;
    private BrowserPictureAdapter mBrowserPictureAdapter;
    @Override
    public void updateWifiName(String wifi_name) {
        if (wifi_name != null) {
            binding.bottom.leftTv.setText(wifi_name.replace("\"", ""));
        }
        if (wifi_name.equals("")){
            Guider.getInstance().setHeartBeatTimeOut();
            connectSuccess(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrowserBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mRootFileNameList.add("videos");
        mRootFileNameList.add("images");
        mRootFileNameList.add("images_task");
        Guider.getInstance().registerHandlerListener(mHandler);
        init();
    }

    @Override
    public void onBackPressed() {
        if ((binding.viewContainer.getVisibility() == View.VISIBLE) || (binding.videoSurface.getVisibility() == View.VISIBLE)){
            hidePreviewItem();
        }else if(mPathList.size() > 1){
            backUpUpperLevel();
        }else {
            super.onBackPressed();
        }
    }

    public static void actionStart(Context context, String path) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra("path",path);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }
    private void init(){
        initBrowserList();
        mPath = getIntent().getStringExtra("path");
       // FileUtils.createDir(Constant.PATH_ROOT);
        FileUtils.createDir(Constant.PATH_VIDEOS);
        FileUtils.createDir(Constant.PATH_PICTURES);
        FileUtils.createDir(Constant.PATH_PICTURES_TASK);
        initView();
        initEvent();

        loadBrowserList();
    }

    private void initView(){
        mBrowserPictureAdapter = new BrowserPictureAdapter(mFitsNameList,this);

        LinearLayoutManager mPathLayoutManager = new LinearLayoutManager(this);
        mPathLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mSnapHelper = new PagerSnapHelper();
        binding.pictureRv.setLayoutManager(mPathLayoutManager);
        binding.pictureRv.setAdapter(mBrowserPictureAdapter);
        mSnapHelper.attachToRecyclerView(binding.pictureRv);

        binding.pictureRv.addOnScrollListener(new RecyclerViewPageChangeListenerHelper(mSnapHelper, new RecyclerViewPageChangeListenerHelper.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                binding.title.fileNameTv.setText(mFitsNameList.get(position)+".fits");
            }
        }));
        connectSuccess(Guider.getInstance().mIsNetworkOnLine);
        binding.title.statusTv.setText(App.INSTANCE.getResources().getString(R.string.folder_manager));
    }

    private void exitActivity(){
        Guider.getInstance().unregisterHandlerListener(mHandler);
        finish();
    }
    private void initEvent(){
        binding.title.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });

        binding.title.wholeBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });
        binding.title.deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteConfirmDialog.Builder mBuilder = new DeleteConfirmDialog.Builder(BrowserActivity.this);
                DeleteConfirmDialog dialog = mBuilder.create();
                mBuilder.setOnDeleteListener(new DeleteConfirmDialog.OnDeleteListener() {
                    @Override
                    public void delete() {
                        ArrayList<BrowserInfo> browserList =new ArrayList<BrowserInfo>();
                        for (BrowserInfo it: mBrowserList) {
                            if (it.mIsSelected){
                                new File(it.mPath).delete();
                            }
                            if (!it.mIsSelected || it.mIsReturn){
                                browserList.add(it);
                            }
                        }

                        mBrowserList.clear();
                        mBrowserList.addAll(browserList);
                        mBrowserAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        chooseSelectedFileNumber(true);
                    }

                    @Override
                    public void cancel() {
                        dialog.dismiss();
                    }
                });
                boolean hasFileSelected = false;
                for (BrowserInfo it: mBrowserList) {
                    if (it.mIsSelected){
                        hasFileSelected = true;
                        break;
                    }
                }
                if (hasFileSelected){
                    dialog.show();
                }else {
                    ToastUtil.showToast(getResources().getString(R.string.no_file_selected),false);
                }
            }
        });

        binding.title.exitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitEditStatus();
                chooseSelectedFileNumber(false);
            }
        });
        binding.title.editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPathList.size() > 1) {
                    binding.title.exitTv.setVisibility(View.VISIBLE);
                    binding.title.allChooseCb.setVisibility(View.VISIBLE);
                    binding.title.deleteTv.setVisibility(View.VISIBLE);
                    binding.title.editTv.setVisibility(View.INVISIBLE);
                    mBrowserAdapter.setEditStatus(true);
                    chooseSelectedFileNumber(true);
                }
            }
        });

        binding.title.allChooseCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (BrowserInfo it:mBrowserList){
                    if (!it.mIsReturn) {
                        it.mIsSelected = binding.title.allChooseCb.isChecked();
                    }
                }
                mBrowserAdapter.notifyDataSetChanged();
                chooseSelectedFileNumber(true);
            }
        });
        binding.title.viewCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePreviewItem();
            }
        });
    }

    private void chooseSelectedFileNumber(boolean isShowFileNumber){
        if (isShowFileNumber){
            int selectedNumber = 0;
            int totalNumber = mBrowserList.size() - 1;
            for (BrowserInfo it: mBrowserList) {
                if (it.mIsSelected && !it.mIsReturn){
                    selectedNumber ++;
                }
            }
            binding.bottom.rightTv.setText(getResources().getString(R.string.file_has_choosed)+": "+selectedNumber+"/"+totalNumber);
            binding.title.deleteTv.setEnabled(selectedNumber != 0);
        }else {
            for (BrowserInfo it:mBrowserList){
                it.mIsSelected = false;
            }
            mBrowserAdapter.notifyDataSetChanged();
            binding.bottom.rightTv.setText("");
        }
    }

    private void hidePreviewItem(){
        mIsPlaying = false;
        binding.viewContainer.setVisibility(View.GONE);
        binding.title.fileNameTv.setVisibility(View.GONE);
        binding.title.viewCloseBtn.setVisibility(View.GONE);
        binding.videoProgressSeekbar.setVisibility(View.GONE);
        binding.videoSurface.setVisibility(View.GONE);
        binding.title.backBtn.setVisibility(View.VISIBLE);
        binding.title.pathRv.setVisibility(View.VISIBLE);
        binding.title.editTv.setVisibility(View.VISIBLE);
        binding.title.statusTv.setVisibility(View.VISIBLE);
    }
    private void showPreviewItem(){
        binding.viewContainer.setVisibility(View.VISIBLE);
        binding.title.fileNameTv.setVisibility(View.VISIBLE);
        binding.title.viewCloseBtn.setVisibility(View.VISIBLE);
        binding.title.backBtn.setVisibility(View.GONE);
        binding.title.pathRv.setVisibility(View.GONE);
        binding.title.editTv.setVisibility(View.GONE);
        binding.title.statusTv.setVisibility(View.GONE);
    }
    private void showVideoItem(){
        binding.title.viewCloseBtn.setVisibility(View.VISIBLE);
        binding.videoProgressSeekbar.setVisibility(View.VISIBLE);
        binding.title.fileNameTv.setVisibility(View.VISIBLE);
        binding.videoSurface.setVisibility(View.VISIBLE);
        binding.title.backBtn.setVisibility(View.GONE);
        binding.title.pathRv.setVisibility(View.GONE);
        binding.title.editTv.setVisibility(View.GONE);
        binding.title.statusTv.setVisibility(View.GONE);
    }
    private void loadBrowserList(){
        mBrowserList.clear();
        mPathList.clear();
        mPathList.add("ToupTek");
        if (mPath.equals("root")){
            for (String item : mRootFileNameList){
                mBrowserList.add(new BrowserInfo(item));
            }
            mBrowserAdapter.notifyDataSetChanged();
            mPathAdapter.notifyDataSetChanged();
        }
        else{
            enter(mPath);
        }
    }
    public void enter(String fileType){
        mPathList.add(fileType);
        String path = "";
        if (fileType.equals("videos")){
            path = Constant.PATH_VIDEO_ROOT+File.separator+fileType+File.separator;
            mFileType = "mp4";
        }else if (fileType.equals("images")){
            path = Constant.PATH_PICTURES_ROOT+File.separator+fileType+File.separator;
            mFileType = "fits";
        }
       // Log.e(TAG, "enter: "+path );
        mBrowserList.clear();
        String absolutePath = "";
        mFitsNameList.clear();
        mVideoNameList.clear();
        File file = new File(path);
        FileUtils.loadAllFiles(file);
        mBrowserList.add(new BrowserInfo("..", true));
        if (FileUtils.getListFiles().size() != 0){
            File file0 = FileUtils.getListFiles().get(0);
            absolutePath = file0.getAbsolutePath().replaceAll(file0.getName(),"");
        }
        if (mFileType.equals("fits")) {
            for (File file1 : FileUtils.getListFiles()) {
                if (file1.getName().contains(mFileType)) {
                    mFitsNameList.add(Long.parseLong(file1.getName().split("\\.")[0]));
                }
            }
            Log.e(TAG, "enter: FileUtils.getmListFiles().size()"+FileUtils.getListFiles().size() );
            long temp = 0L;
            boolean swap;
            for (int i = mFitsNameList.size() - 1; i > 0; i--) {
                swap = false;
                for (int j = 0; j < i; j++) {
                    if (mFitsNameList.get(j) < mFitsNameList.get(j + 1)) {
                        temp = mFitsNameList.get(j);
                        mFitsNameList.set(j, mFitsNameList.get(j + 1));
                        mFitsNameList.set(j + 1, temp);
                        swap = true;
                    }
                }
                if (swap == false) {
                    break;
                }
            }
            for (int i = 0; i < mFitsNameList.size(); i++) {
                mBrowserList.add(new BrowserInfo(absolutePath + mFitsNameList.get(i) + "." + mFileType, mFitsNameList.get(i) + "." + mFileType, true));
            }
        }else if(mFileType.equals("mp4")){
            for (File file1 : FileUtils.getListFiles()) {
                if (file1.getName().contains(mFileType)) {
                    mVideoNameList.add(convertVideoNameToLong(file1.getName(),mFileType));
                }
            }
            long temp = 0L;
            boolean swap;
            for (int i = mVideoNameList.size() - 1; i > 0; i--) {
                swap = false;
                for (int j = 0; j < i; j++) {
                    if (mVideoNameList.get(j) < mVideoNameList.get(j + 1)) {
                        temp = mVideoNameList.get(j);
                        mVideoNameList.set(j, mVideoNameList.get(j + 1));
                        mVideoNameList.set(j + 1, temp);
                        swap = true;
                    }
                }
                if (swap == false) {
                    break;
                }
            }
            String name = "";
            for (int i = 0; i < mVideoNameList.size(); i++) {
                name = convertLongToVideoName(mVideoNameList.get(i),mFileType);
                mBrowserList.add(new BrowserInfo(absolutePath + name, name, true));
            }
        }
        mBrowserAdapter.setEditStatus(binding.title.deleteTv.getVisibility()==View.VISIBLE);
        mBrowserAdapter.notifyDataSetChanged();
        mPathAdapter.notifyDataSetChanged();

        chooseSelectedFileNumber(false);
    }
    private long convertVideoNameToLong(String name,String videoType){
        String tempName = name.replace("."+videoType,"");
        tempName = tempName.replace("-","");
        return Long.parseLong(tempName);
    }

    private String convertLongToVideoName(Long name,String videoType){
        String tempName = String.valueOf(name);
        char[] charArrayName = tempName.toCharArray();
        String newName = "";
        if (charArrayName.length > 4){
            newName += charArrayName[0];
            newName += charArrayName[1];
            newName += charArrayName[2];
            newName += charArrayName[3];
            newName += "-";
        }
        for (int i = 4;i < charArrayName.length;){
            newName += charArrayName[i];
            i++;
            newName += charArrayName[i];
            i++;
            if (i != charArrayName.length){
                newName += "-";
            }
        }
        newName += "." + videoType;
        return newName;
    }
    private void initBrowserList(){
        LinearLayoutManager mPathLayoutManager = new LinearLayoutManager(this);
        mPathLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.title.pathRv.setLayoutManager(mPathLayoutManager);
        mPathAdapter = new BrowserPathAdapter(mPathList,this);
        mPathAdapter.setOnClickListener(new BrowserPathAdapterListener() {
            @Override
            public void clickItem(int position) {
                while (mPathList.size() > position+1) {
                    if (mPathList.size() > 0) {
                        mPathList.remove(mPathList.size() - 1);
                    }
                }
                mPathAdapter.notifyDataSetChanged();

                if (mPathList.size() == 1){
                    mBrowserList.clear();
                    for (String item : mRootFileNameList){
                        mBrowserList.add(new BrowserInfo(item));
                    }
                    mBrowserAdapter.notifyDataSetChanged();
                }
            }
        });
        binding.title.pathRv.setAdapter(mPathAdapter);

        GridLayoutManager mLayoutManager =new GridLayoutManager(this,5);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.browserRv.setLayoutManager(mLayoutManager);
        binding.browserRv.addItemDecoration(new SpacesItemDecoration(getResources().getInteger(R.integer.browser_item_margin),5));
        mBrowserAdapter = new BrowserAdapter(mBrowserList,this);
        mBrowserAdapter.setOnClickListener(new BrowserAdapterListener() {
            @Override
            public void checked(int position) {
                mBrowserList.get(position).mIsSelected = !mBrowserList.get(position).mIsSelected;
                int index = 0;
                for( BrowserInfo item : mBrowserList){
                    if (item.mIsSelected){
                        index ++;
                    }
                    else
                    {
                        break;
                    }
                }
                if (index == mBrowserList.size()){
                    binding.title.allChooseCb.setChecked(true);
                }
                else
                {
                    binding.title.allChooseCb.setChecked(false);
                }
                mBrowserAdapter.notifyDataSetChanged();
                chooseSelectedFileNumber(true);
            }

            @Override
            public void enterNext(int position) {
                enter(mBrowserList.get(position).mName);
            }

            @Override
            public void backUp() {
                backUpUpperLevel();
            }

            @Override
            public void openFile(int position) {
                mCurrentFileName = mBrowserList.get(position).mName;
                binding.title.fileNameTv.setText(mCurrentFileName);
                String[] types = mCurrentFileName.split("\\.");
                if (types.length > 1){
                    String type = types[types.length - 1];
                    if (type.equals("mp4")){
                        showVideoItem();
                        play(Constant.PATH_VIDEOS + mCurrentFileName);
                    }else if (type.equals("fits")){
                        Long item = Long.parseLong(types[0]);
                        int index = mFitsNameList.indexOf(item);
                        binding.pictureRv.scrollToPosition(index);
                        mBrowserPictureAdapter.notifyDataSetChanged();
                        showPreviewItem();
                    }
                }
            }
        });
        binding.browserRv.setAdapter(mBrowserAdapter);
    }

    private void exitEditStatus(){
        binding.title.exitTv.setVisibility(View.INVISIBLE);
        binding.title.allChooseCb.setVisibility(View.INVISIBLE);
        binding.title.deleteTv.setVisibility(View.INVISIBLE);
        binding.title.editTv.setVisibility(View.VISIBLE);
        mBrowserAdapter.setEditStatus(false);
        binding.title.allChooseCb.setChecked(false);
    }
    private void backUpUpperLevel(){
        if (mPathList.size() > 1)
            mPathList.remove(mPathList.size() -1);
        mPathAdapter.notifyDataSetChanged();
        if (mPathList.size() == 1){
            mBrowserList.clear();
            for (String item : mRootFileNameList){
                mBrowserList.add(new BrowserInfo(item));
            }
            mBrowserAdapter.notifyDataSetChanged();

            mBrowserAdapter.setEditStatus(false);
        }

        exitEditStatus();
        chooseSelectedFileNumber(false);
    }
    private void play(String playPath){
        File file = new File(playPath);
        if (!file.exists()){
            ToastUtil.showToast(getResources().getString(R.string.video_path_error), false);
            return;
        }
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e(TAG, "onCompletion: mp4");
                    mIsPlaying = false;
                    hidePreviewItem();
                }
            });

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG, "onPrepared: mp4" );
                    mMediaPlayer.seekTo(0);
                    mMediaPlayer.start();
                    Log.e(TAG, "onPrepared: mp4"+mMediaPlayer.getDuration() );
                    int allDuration = mMediaPlayer.getDuration();
                    binding.videoProgressSeekbar.setMax(allDuration);
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                int currentDuration = 0;
                                mIsPlaying = true;
                                while (mIsPlaying){
                                    binding.videoProgressSeekbar.setProgress(currentDuration);
                                    currentDuration += 100;
                                    Log.e(TAG, "run: "+currentDuration );
                                    sleep(100);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            });
            binding.videoSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder holder) {
                    if (!mIsSurfaceHoldBound) {
                        mIsSurfaceHoldBound = true;
                    }
                    Log.e(TAG, "surfaceCreated: mp4" );
                    mMediaPlayer.setDisplay(binding.videoSurface.getHolder());
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

                }
            });
            mMediaPlayer.setDataSource(file.getAbsolutePath());
           // mMediaPlayer.prepareAsync();
            mMediaPlayer.prepare();

            Log.e(TAG, "play: mp4" );
        } catch (Exception e) {
            Log.e(TAG, "play1: mp4" );
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Guider.getInstance().unregisterHandlerListener(mHandler);
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

    public void connectSuccess(boolean isConnected){
        if (isConnected){
            binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_success));
        }else {
            binding.bottom.networkStatusTv.setBackground(getResources().getDrawable(R.drawable.ic_network_connect_fail));
        }
    }
}
