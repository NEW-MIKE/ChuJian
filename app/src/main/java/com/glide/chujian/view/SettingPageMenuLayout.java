package com.glide.chujian.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.glide.chujian.R;

import java.util.ArrayList;
import java.util.List;

public class SettingPageMenuLayout extends ViewGroup {
    private Context mContext;

    private int mMenuItemCount;
    // 菜单项的文本
    private int[] mItemTexts = {
        R.string.menu_main_camera,
        R.string.menu_guide_camera,
        R.string.menu_filter_wheel_camera,
        R.string.menu_auto_focuser_camera,
        R.string.menu_telescope_camera,
        R.string.menu_misc_camera
    };

    private Paint mPaint;
    private int mStatusHeight = 0;//状态栏高度

    private float mScale = 1.05f;

    private OnMenuItemClickListener mOnMenuItemClickListener;
    private RectF mCirclePathRect = new RectF(0f,0f,0f,0f);
    private Path path = new Path();
    private List<CheckableTextView> mTvList = new ArrayList<CheckableTextView>();
    private List<CheckableTextView> mIvList = new ArrayList<CheckableTextView>();
    private List<View> mViewList = new ArrayList<View>();
    private int[] mIvSelectList = {
            R.drawable.select_main_camera_bg_inset,
            R.drawable.select_guide_camera_bg_inset,
            R.drawable.select_filter_wheel_bg_inset,
            R.drawable.select_eaf_bg_inset,
            R.drawable.select_slew_bg_inset,
            R.drawable.select_misc_bg_inset
    };
    private int[] widthall = {50, 30, 10, -10, -30, -50};
    int mResWidth = 0;
    int mResHeight = 0;
    int mRadius = 0;

    public SettingPageMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //布局宽高尺寸设置为屏幕尺寸
        //设置该布局的大小
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        mResHeight = MeasureSpec.getSize(heightMeasureSpec);
        mResWidth = MeasureSpec.getSize(widthMeasureSpec);

        // 获得半径
        mRadius = (mResHeight / 2 - 2 * mStatusHeight);
        //设置item尺寸
        int childSize = (mRadius * 1 / 2) + 80;
        // menu item测量模式--精确模式
        int childMode = MeasureSpec.UNSPECIFIED;
        for (int i = 0; i < getChildCount();i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
    }


    private void DrawCirclePath(Canvas canvas) {
        canvas.save();
        canvas.translate(-200.0F, (float)(mResHeight / 2) + (float)10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4.0F);
        SweepGradient sweepGradient = new SweepGradient(0.0F, 0.0F, getResources().getColor(R.color.menu_circle_size_blue_color), getResources().getColor(R.color.menu_circle_center_blue_color));
        Matrix matrix = new Matrix();
        matrix.setRotate(-60.0F, 0.0F, 0.0F);
        sweepGradient.setLocalMatrix(matrix);
        mPaint.setShader((Shader)sweepGradient);
        float radius = (float)mRadius - (float)15;
        mCirclePathRect.left = 0.0F - radius;
        mCirclePathRect.top = 0.0F - radius;
        mCirclePathRect.right = radius;
        mCirclePathRect.bottom = radius;
        canvas.drawArc(mCirclePathRect, -55.0F, 55.0F, false, mPaint);
        canvas.drawArc(mCirclePathRect, -55.0F, 55.0F, false, mPaint);
        canvas.drawArc(mCirclePathRect, -55.0F, 55.0F, false, mPaint);
        SweepGradient sweepGradient1 = new SweepGradient(0.0F, 0.0F, getResources().getColor(R.color.menu_circle_center_blue_color), getResources().getColor(R.color.menu_circle_size_blue_color));
        Matrix matrix1 = new Matrix();
        matrix1.setRotate(-300.0F, 0.0F, 0.0F);
        sweepGradient1.setLocalMatrix(matrix1);
        mPaint.setShader((Shader)sweepGradient1);
        canvas.drawArc(mCirclePathRect, -0.0F, 55.0F, false, mPaint);
        canvas.drawArc(mCirclePathRect, -0.0F, 55.0F, false, mPaint);
        canvas.drawArc(mCirclePathRect, -0.0F, 55.0F, false, mPaint);
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawCirclePath(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left, top;
        int cWidth = mRadius * 1 / 2;
        int childCount = getChildCount();
        float tmp = mRadius - cWidth / 2;

        for(int i = 0; i < childCount; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != GONE) {
                left = (int)(mRadius * Math.cos(Math.toRadians(widthall[i]))) - 250;
                top = (int)(mRadius - (mResHeight / 2 - 2 * mStatusHeight) * Math.sin(Math.toRadians(widthall[i])) - mStatusHeight) - 30;
                if (i == childCount / 2) {
                    mCirclePathRect.right = (float)left;
                } else if (i == childCount - 1) {
                    mCirclePathRect.bottom = (float)top + (float)150;
                }

                child.layout(left, top, left + cWidth + 300, top + cWidth);
            }
        }
    }
    public interface OnMenuItemClickListener {
        void itemClick( View view, int pos);
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mOnMenuItemClickListener
    ) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    public void setMenuItemIconsAndTexts() {
        mMenuItemCount = mItemTexts.length;
        addMenuItems();
    }

    private int mMenuItemLayoutId = R.layout.settingpage_item_layout;
    private float yPosition = 0;
    private float mScaleDistance = 0;

    public void setCurrentPosition(int position){
        for (int i = 0 ; i < mIvList.size();i++) {
            CheckableTextView iv = mIvList.get(i);
            CheckableTextView tv = mTvList.get(i);
            if (i == position) {
                mScaleDistance = (mScale - 1f) / 2 * (iv.getMeasuredWidth() + tv.getMeasuredWidth());
                tv.setTranslationX(10);
                tv.setScaleX(mScale);
                tv.setScaleY(mScale);
                iv.setScaleX(mScale);
                iv.setScaleY(mScale);
                tv.setTextColor(getResources().getColor(R.color.setting_circle_menu_text_color_checked));
                tv.setCheckedd(true);
                iv.setCheckedd(true);
            } else {
                tv.setScaleX(1);
                tv.setScaleY(1);
                mIvList.get(i).setScaleX(1);
                mIvList.get(i).setScaleY(1);
                tv.setTranslationX(0);
                tv.setTextColor(getResources().getColor(R.color.setting_circle_menu_text_color_normal));
                tv.setCheckedd(false);
                mIvList.get(i).setCheckedd(false);
            }
        }
    }

    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        /**
         * 根据用户设置的参数，初始化view
         */
        mTvList.clear();
        mIvList.clear();
        mViewList.clear();
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View view = mInflater.inflate(mMenuItemLayoutId, this, false);
            final CheckableTextView iv = (CheckableTextView)view.findViewById(R.id.setting_item_image);
            final CheckableTextView tv = (CheckableTextView)view.findViewById(R.id.setting_item_text);
            if (iv != null) {
                iv.setBackgroundResource(mIvSelectList[i]);
            }
            mTvList.add(tv);
            mIvList.add(iv);
            if (tv != null) {
                tv.setText(getResources().getString(mItemTexts[i]));
            }

            view.findViewById(R.id.settingpage_item_layout).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        yPosition = motionEvent.getY(); //获取按下的位置
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        //   iv.setImageResource(R.mipmap.menu_ture);
                        float displacement = Math.abs(yPosition - motionEvent.getY());
                        //精确按下的位置做出响应
                        if (mOnMenuItemClickListener != null && displacement < 25) {
                            mOnMenuItemClickListener.itemClick(v, j);
                            int size = mTvList.size();
                            for (int index = 0;index < size;index ++) {
                                CheckableTextView it = mTvList.get(index);
                                if (it != tv) {
                                    if (it.isChecked()) {
                                        it.setScaleX(1);
                                        it.setScaleY(1);
                                        mIvList.get(index).setScaleX(1);
                                        mIvList.get(index).setScaleY(1);
                                        it.setTranslationX(0);
                                        it.setTextColor(getResources().getColor(R.color.setting_circle_menu_text_color_normal));
                                        it.setCheckedd(false);
                                        mIvList.get(index).setCheckedd(false);
                                    }
                                } else {

                                }
                            }

                            mScaleDistance = (mScale - 1f) / 2 * (iv.getWidth() + tv.getWidth());
                            tv.setScaleX(mScale);
                            tv.setScaleY(mScale);
                            iv.setScaleX(mScale);
                            iv.setScaleY(mScale);
                            tv.setTranslationX(mScaleDistance);
                            tv.setTextColor(getResources().getColor(R.color.setting_circle_menu_text_color_checked));
                            tv.setCheckedd(true);
                            iv.setCheckedd(true);

                        }
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_POINTER_UP) {
                        //   iv.setImageResource(R.mipmap.menu_ture);
                    }
                    return true;
                }
            });
            addView(view);
            mViewList.add(view);
        }
    }
}
