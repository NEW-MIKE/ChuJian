package com.glide.chujian.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.glide.chujian.R;
import com.glide.chujian.util.ScreenUtil;

import org.jetbrains.annotations.NotNull;

public class TakePhotoView extends View {
    private String TAG = "TakePhotoView";
    private Paint mPaint;
    private int mBorderWidth;
    private long mDuration;
    private int mRadius;
    private int mCenterX;
    private int mCenterY;
    private StatusListener listenter;
    private float mMaxNumber;
    private float mUniteDegree;
    private float mCircleBlackWidth;
    private float mCircleBlackDrawRadius;
    private float mCircleRedDrawRadius;
    private float mCircleBlackMarginOutside;
    private float mRectCornerMarginOutside;
    private int mDegreeNumber;
    private RectF mProgressRect;
    private float mAnimatedDegree;
    private int mMode = 0;
    private String mDegreeText = "";
    @NotNull
    private Rect mRect;
    private double mRectWidth;
    private double mRectHeight;
    private boolean mIsEnabled = true;
    public static final int VIDEO_MODE_IDLE = 0;
    public static final int VIDEO_MODE_DOING = 1;
    public static final int PLAN_SHOOT_MODE_IDLE = 2;
    public static final int PLAN_SHOOT_MODE_DOING = 3;
    public static final int SINGLE_TRIGGER_MODE_IDLE = 4;
    public static final int SINGLE_TRIGGER_MODE_READY = 5;
    public static final int SINGLE_TRIGGER_MODE_DOING = 6;
    private static int DEFAULT_DIMENSION;
    public TakePhotoView(Context context) {
        super(context);
        init(context);
    }

    public void setNormalEnabled(boolean enabled){
        mIsEnabled = enabled;
        setEnabled(enabled);
        invalidate();
    }
    public TakePhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TakePhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private final void init(Context context) {
        DEFAULT_DIMENSION = ScreenUtil.dpToPx(150);
        this.mBorderWidth =  ScreenUtil.dpToPx(8);
        mCircleBlackWidth =  ScreenUtil.dpToPx(3);
        mCircleBlackMarginOutside = ScreenUtil.dpToPx(4);
        mCircleBlackDrawRadius = (mCircleBlackMarginOutside + mCircleBlackWidth/2);
        mRectCornerMarginOutside = ScreenUtil.dpToPx(4) + mCircleBlackMarginOutside;
        mCircleRedDrawRadius = mCircleBlackWidth + mCircleBlackMarginOutside;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mProgressRect  = new RectF(
                0f,
                0f,
                0f,
                0f
        );
        mRect = new Rect(0, 0, mRadius, mRadius);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mRadius = Math.min(w, h) / 2;
        this.mCenterX = w / 2;
        this.mCenterY = h / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            this.setMeasuredDimension(DEFAULT_DIMENSION, DEFAULT_DIMENSION);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            this.setMeasuredDimension(DEFAULT_DIMENSION, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            this.setMeasuredDimension(widthSize, DEFAULT_DIMENSION);
        } else {
            this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsEnabled) {
            this.switchMode(canvas);
        }else {
            SingleTriggerDisabled(canvas);
        }
    }

    private void switchMode(Canvas canvas) {
        switch(this.mMode) {
            case 0:
                this.SingleTriggerIdleMode(canvas);
                break;
            case 1:
                this.SingleTriggerReadyMode(canvas);
                break;
            case 2:
                this.SingleTriggeringMode(canvas);
                break;
            case 3:
                this.VideoIdleMode(canvas);
                break;
            case 4:
                this.VideoStartMode(canvas);
                break;
            case 5:
                this.PlanPhotoMode(canvas);
        }

    }

    public void SingleTriggerIdleMode(@NotNull Canvas canvas) {
        canvas.translate((float)this.mCenterX, (float)this.mCenterY);

        /*白色地圈*/

        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius, this.mPaint);

        /*黑色内圈*/

        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(mCircleBlackWidth);
        this.mPaint.setColor(Color.BLACK);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius - mCircleBlackDrawRadius, this.mPaint);

    }

    public void SingleTriggerReadyMode(@NotNull Canvas canvas) {
        canvas.translate((float)this.mCenterX, (float)this.mCenterY);

        /*红色外底*/
        this.mPaint.setStyle(Paint.Style.FILL);
        // this.mPaint.setColor(Color.RED);
        this.mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius, this.mPaint);
        this.CircleBlackRectRed(canvas);
    }
    public void SingleTriggerDisabled(@NotNull Canvas canvas) {
        canvas.translate((float)this.mCenterX, (float)this.mCenterY);

        /*白色地圈*/

        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(getResources().getColor(R.color.disabled_color));
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius, this.mPaint);

        /*黑色内圈*/

        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(mCircleBlackWidth);
        this.mPaint.setColor(Color.BLACK);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius - mCircleBlackDrawRadius, this.mPaint);

    }

    public void SingleTriggeringMode(@NotNull Canvas canvas) {
        canvas.save();
        canvas.translate((float)this.mCenterX, (float)this.mCenterY);

        /*白色外底*/
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius, this.mPaint);
        this.CircleBlackRectRed(canvas);
        /*动态更新外部圆环状态*/
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(mCircleBlackMarginOutside);
        mPaint.setAntiAlias(true);
        this.mPaint.setColor(getResources().getColor(R.color.all_blue_color));
        float def = mCircleBlackMarginOutside/2;
        this.mProgressRect.top = -((float)this.mRadius) + def;
        this.mProgressRect.left = -((float)this.mRadius) + def;
        this.mProgressRect.bottom = (float)this.mRadius - def;
        this.mProgressRect.right = (float)this.mRadius - def;
        canvas.drawArc(this.mProgressRect, -90.0F, (float)this.mAnimatedDegree, false, this.mPaint);
        canvas.restore();

        if (mAnimatedDegree >= 360) {
            resetCircleStatus();
            if (listenter != null) {
                listenter.backToIdle();
            }
        }
    }

    private void CircleBlackRectRed(Canvas canvas) {
        /*黑色内部*/
        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius - mCircleBlackMarginOutside, this.mPaint);
        this.mRectWidth = (double)((float)this.mRadius - mRectCornerMarginOutside) / Math.sqrt(2.0D);

        /*内部方框*/
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mRect.top = (int)(-this.mRectWidth);
        this.mRect.left = (int)(-this.mRectWidth);
        this.mRect.bottom = (int)(this.mRectWidth);
        this.mRect.right = (int)(this.mRectWidth);
        this.mPaint.setColor(Color.RED);
        canvas.drawRect(this.mRect, this.mPaint);
    }

    private final void CircleBlackRectRedTwo(Canvas canvas) {
        /*黑色内部*/
        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius - mCircleBlackMarginOutside, this.mPaint);
      //  this.rect_width = (double)((float)this.mRadius - mRectCornerMarginOutside) / Math.sqrt(2.0D);
        this.mRectWidth = (double)((float)this.mRadius - mRectCornerMarginOutside) * 0.8;
        mRectHeight = (double)((float)this.mRadius - mRectCornerMarginOutside) * 0.6;
        /*内部方框*/
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mRect.top = (int)(-mRectHeight);
        this.mRect.left = (int)(-this.mRectWidth);
        this.mRect.bottom = (int)(mRectHeight);
        this.mRect.right = (int)(this.mRectWidth);
        this.mPaint.setColor(Color.RED);
        canvas.drawRect(this.mRect, this.mPaint);
    }

    public final void VideoIdleMode(@NotNull Canvas canvas) {
        this.SingleTriggerIdleMode(canvas);
        /*红色内圆*/
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.RED);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius - mCircleRedDrawRadius, this.mPaint);
    }

    public final void VideoStartMode(@NotNull Canvas canvas) {
        canvas.translate((float)this.mCenterX, (float)this.mCenterY);

        /*红色外底*/
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius, this.mPaint);
        this.CircleBlackRectRed(canvas);
    }

    public final void PlanPhotoMode(@NotNull Canvas canvas) {
        canvas.translate((float)this.mCenterX, (float)this.mCenterY);

        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0.0F, 0.0F, (float)this.mRadius, this.mPaint);
        this.CircleBlackRectRedTwo(canvas);

        /*动态更新外部圆环状态*/
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(mCircleBlackMarginOutside);
        this.mPaint.setColor(Color.GREEN);
        float def = mCircleBlackMarginOutside / 2;
        this.mProgressRect.top = -((float)this.mRadius) + def;
        this.mProgressRect.left = -((float)this.mRadius) + def;
        this.mProgressRect.bottom = (float)this.mRadius - def;
        this.mProgressRect.right = (float)this.mRadius - def;
        canvas.drawArc(this.mProgressRect, -90.0F, (float)this.mAnimatedDegree, false, this.mPaint);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.WHITE);
        this.mPaint.setTextSize(ScreenUtil.spToPx(8));
       // canvas.drawText(this.mDegreeText, (float)(-this.rect_width + mCircleBlackMarginOutside), (float)(this.rect_width * (double)2 / (double)10), this.mPaint);
        canvas.drawText(this.mDegreeText, (float)(-this.mRectWidth + mCircleBlackMarginOutside), (float) (mRectHeight / 4), this.mPaint);

        if (mAnimatedDegree >= 360) {
            resetCircleStatus();
            if (listenter != null) {
                listenter.backToIdle();
            }
        }
    }

    public void setDuration(double duration){
       // mDuration = (long) duration == 0?1: (long) duration;
        mDuration = (long) duration;
        Log.d(TAG, "clickCenterCircle: mDuration" + mDuration);
    }

    private final void startAnimn() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mUniteDegree);
        valueAnimator.setDuration(mDuration);
        valueAnimator.addUpdateListener((ValueAnimator.AnimatorUpdateListener)(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedDegree = (float) animation.getAnimatedValue();
                int num = 0;
                if ((mDegreeNumber-1) > 0){
                    num = mDegreeNumber-1;
                } else {
                    num = 0;
                }
                mAnimatedDegree += mUniteDegree * num;

                if (mAnimatedDegree > 360) {
                    if (listenter != null) {
                        listenter.backToIdle();
                    }
                }
                else {
                    if (mDegreeNumber > 9) {
                        if (mMaxNumber < 9) {
                            mDegreeText = ""+mDegreeNumber +"/0"+(int)mMaxNumber;
                        }
                        else {
                            mDegreeText = ""+mDegreeNumber +"/"+(int)mMaxNumber;
                        }
                    }
                    else {
                        if (mMaxNumber < 9){
                            mDegreeText = "0"+mDegreeNumber +"/0"+(int)mMaxNumber;
                        }
                        else {
                            mDegreeText = ""+mDegreeNumber +"/"+(int)mMaxNumber;
                        }
                    }
                    invalidate();
                }
            }
        }));
        valueAnimator.start();
    }

    public void setCircleDegree(int degree) {
        this.mDegreeNumber = degree;
        this.startAnimn();
    }


    public final void setMode(int modeSet) {
        if (modeSet == VIDEO_MODE_IDLE) {
            this.mMode = 3;
        } else if (modeSet == PLAN_SHOOT_MODE_IDLE) {
            this.mMode = 0;
        } else if (modeSet == SINGLE_TRIGGER_MODE_IDLE) {
            this.mMode = 0;
        } else if (modeSet == VIDEO_MODE_DOING) {
            this.mMode = 4;
        } else if (modeSet == SINGLE_TRIGGER_MODE_READY) {
            this.mMode = 1;
        } else if (modeSet == PLAN_SHOOT_MODE_DOING) {
            this.mMode = 5;
        } else if (modeSet == SINGLE_TRIGGER_MODE_DOING) {
            this.mMode = 2;
        }

        this.invalidate();
    }


    private final void resetCircleStatus() {
        this.mMaxNumber = 0;
        this.mUniteDegree = 0;
        this.mDegreeNumber = 0;
        this.mAnimatedDegree = 0;
    }

    public final void setCircleMaxNumber(int number) {
        this.mMaxNumber = number;
        this.mUniteDegree = 360 / this.mMaxNumber;
        this.mDegreeText = this.mMaxNumber < 9 ? "00/0" + number : "00/" + number;
    }

    public void setStatusListener(@NotNull StatusListener listener) {
        this.listenter = listener;
    }

    public interface StatusListener {
        void backToIdle();
    }
}
