package com.glide.chujian.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.glide.chujian.R;
import com.glide.chujian.util.ScreenUtil;

public class HistogramTick extends View {
    private static final String TAG = "HistogramTick";
    private static final int CLICK_TYPE_NULL = 0;
    private static final int CLICK_TYPE_LEFT = 1;
    private static final int CLICK_TYPE_RIGHT = 2;

    private float maxValue;
    private float minValue;
    private float leftValue, rightValue;//当前的数值
    private float defaultWidth = 0;
    private float defaultHeight = 0;
    private float clickX = 0;//按下瞬间的像素位置

    private boolean mIsBoth = false;
    private float mCurrentLeftBarPos, mCurrentRightBarPos;
    private int viewBarSize;
    private int containerWidth, containerHeight,lineMarginTop;
    private float startLeftBarX, startRightBarX;//左右按钮的开始x轴坐标值
    private float mDistance = 0;

    private int clickedType = CLICK_TYPE_NULL;//0 没有点击，1点击中左按钮，2点击中右按钮

    private Paint paintCenterLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintLeftBar = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintRightBar = new Paint(Paint.ANTI_ALIAS_FLAG);
    private OnChanged mOnChanged;

    public HistogramTick(Context context) {
        this(context, null);
    }

    public HistogramTick(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistogramTick(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewBarSize = ScreenUtil.androidAutoSizeDpToPx(30);

        paintCenterLine.setColor(getResources().getColor(R.color.histogram_line_color));
        paintLeftBar.setColor(getResources().getColor(R.color.histogram_left_bar_color));
        paintRightBar.setColor(getResources().getColor(R.color.histogram_right_bar_color));
        paintLeftBar.setStyle(Paint.Style.FILL);
        paintRightBar.setStyle(Paint.Style.FILL);
        paintLeftBar.setStrokeWidth(1);
        maxValue = 255;
        minValue = 0;
        leftValue = minValue;
        rightValue = maxValue;
        defaultWidth = ScreenUtil.androidAutoSizeDpToPx(100);
        defaultHeight = ScreenUtil.androidAutoSizeDpToPx(50);
        lineMarginTop = ScreenUtil.androidAutoSizeDpToPx(10);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }
    public void setMaxNumber(float maxNumber ){
        if (maxNumber < 0) {
            maxNumber = 0;
        }
        if (maxValue != maxNumber) {
            maxValue = maxNumber;
            rightValue = maxNumber;
            LOGE("setMaxNumber",maxNumber);
        }
    }
    public void setMinNumber(float minNumber){
        if (minNumber < 0) {
            minNumber = 0;
        }
        if (minValue != minNumber){
            minValue = minNumber;
            leftValue = minValue;
            LOGE("setMinNumber",minNumber);
        }
    }
    public void resetNumber(){
        leftValue = minValue;
        rightValue = maxValue;

        if (mOnChanged != null) {
            mOnChanged.onRightMovePosition(rightValue);
            mOnChanged.onLeftMovePosition(leftValue);
            mOnChanged.onRelease();
        }
        mCurrentLeftBarPos = (leftValue / (maxValue - minValue)) * (startRightBarX - startLeftBarX) + startLeftBarX;
        mCurrentRightBarPos = (rightValue / (maxValue - minValue)) * (startRightBarX - startLeftBarX) + startLeftBarX;
        postInvalidate();
    }

    public void setOnMoveBarChangeListener(OnChanged mOnChanged) {
        this.mOnChanged = mOnChanged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isClickedIcon(event)) {
                    postInvalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (clickedType == CLICK_TYPE_LEFT) {
                    if (mOnChanged != null) {
                        mOnChanged.onLeftMovePosition(leftValue);
                    }
                } else if (clickedType == CLICK_TYPE_RIGHT) {
                    if (mOnChanged != null) {
                        mOnChanged.onRightMovePosition(rightValue);
                    }
                }
                if (mOnChanged != null) {
                    mOnChanged.onRelease();
                }
                mDistance = 0;
                clickedType = CLICK_TYPE_NULL;
                break;
            case MotionEvent.ACTION_MOVE:
                if (handleMoveEvent(event)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isValueBothContain(float value,float leftDownSide,float leftUpSide,
                                       float rightDownSide,float rightUpSide){
        if (value > leftDownSide && value < leftUpSide && value >rightDownSide && value < rightUpSide){
            return true;
        }
        return false;
    }

    /**
     * 判断是否点击到按钮了
     *
     * @param event
     * @return
     */

    private boolean isClickedIcon(MotionEvent event) {
        float x = event.getX();
        float leftBarDownSide = mCurrentLeftBarPos - viewBarSize/2;
        float leftBarUpSide = mCurrentLeftBarPos + viewBarSize/2;
        float rightBarDownSide = mCurrentRightBarPos - viewBarSize/2;
        float rightBarUpSide = mCurrentRightBarPos + viewBarSize/2;
        clickX = x;
        if (isValueBothContain(x,leftBarDownSide,leftBarUpSide,rightBarDownSide,rightBarUpSide)){
            mIsBoth = true;
            return true;
        }else if (x > leftBarDownSide && x < leftBarUpSide){
            clickedType = CLICK_TYPE_LEFT;
            return true;
        }else if (x > rightBarDownSide && x < rightBarUpSide){
            clickedType = CLICK_TYPE_RIGHT;
            return true;
        }
        clickedType = CLICK_TYPE_NULL;
        return false;
    }

    /**
     * 滑动事件处理
     *
     * @param motionEvent
     */
    private boolean handleMoveEvent(MotionEvent motionEvent) {
        Log.d(TAG, "handleMoveEvent: start");
        float x = motionEvent.getX();
        mDistance = x - clickX;
        if (mIsBoth){
            if (mDistance > 0){
                mIsBoth = false;
                clickedType = CLICK_TYPE_RIGHT;
            }else if (mDistance < 0){
                mIsBoth = false;
                clickedType = CLICK_TYPE_LEFT;
            }
        }
        if (clickedType == CLICK_TYPE_LEFT) {
            if ((mCurrentLeftBarPos + mDistance) <= startLeftBarX){
                mCurrentLeftBarPos = startLeftBarX;
            }else if ((mCurrentLeftBarPos + mDistance) >= mCurrentRightBarPos){
                mCurrentLeftBarPos = mCurrentRightBarPos;
            }else {
                mCurrentLeftBarPos += mDistance;
                clickX = x;
            }
            leftValue = ((maxValue - minValue) * (mCurrentLeftBarPos - startLeftBarX) / (startRightBarX - startLeftBarX));

            if (mOnChanged != null) {
                mOnChanged.onCurrentPosition(leftValue);
            }
        } else if (clickedType == CLICK_TYPE_RIGHT) {
            if (mCurrentRightBarPos + mDistance >= startRightBarX){
                mCurrentRightBarPos = startRightBarX;
            }else if (mCurrentRightBarPos + mDistance <= mCurrentLeftBarPos){
                mCurrentRightBarPos = mCurrentLeftBarPos;
            }else {
                mCurrentRightBarPos += mDistance;
                clickX = x;
            }
            rightValue = ((maxValue - minValue) * (mCurrentRightBarPos - startLeftBarX) / (startRightBarX - startLeftBarX));

            if (mOnChanged != null) {
                mOnChanged.onCurrentPosition(rightValue);
            }
        }
        LOGE("handleMoveEvent",leftValue);
        postInvalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLeftBar(canvas);
        drawRightBar(canvas);
    }

    /**
     * 绘制左滑块
     *
     * @param canvas
     */
    private void drawLeftBar(Canvas canvas) {
        canvas.translate(0, containerHeight / 2 + lineMarginTop);
        canvas.drawLine(mCurrentLeftBarPos, containerHeight / 2, mCurrentLeftBarPos, -containerHeight / 2 + lineMarginTop, paintCenterLine);
        canvas.drawCircle(mCurrentLeftBarPos, 0.0F, viewBarSize / 2, paintLeftBar);
        LOGE("leftValue", leftValue);
    }

    /**
     * 绘制右滑块
     *
     * @param canvas
     */
    private void drawRightBar(Canvas canvas) {
        canvas.drawLine(mCurrentRightBarPos, containerHeight / 2, mCurrentRightBarPos, -containerHeight / 2 + lineMarginTop, paintCenterLine);
        canvas.drawCircle(mCurrentRightBarPos, 0.0F, viewBarSize / 2, paintRightBar);
        LOGE("rightValue", rightValue);
    }

    /**
     * 回调接口，回调左右值
     */
    public interface OnChanged {
        void onLeftMovePosition(float position);

        void onRightMovePosition(float position);

        void onRelease();

        void onCurrentPosition(float position);
    }

    /**
     * 重写onMeasure方法，设置wrap_content 时需要默认大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension((int) defaultWidth, (int) defaultHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension((int) defaultWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, (int) defaultHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        containerWidth = getWidth();
        containerHeight = getHeight();
        startLeftBarX = viewBarSize/2;//整个边界以中心点作为边界点
        startRightBarX = containerWidth - viewBarSize/2;
        mCurrentLeftBarPos = (leftValue / (maxValue - minValue)) * (startRightBarX - startLeftBarX) + startLeftBarX;
        mCurrentRightBarPos = (rightValue / (maxValue - minValue)) * (startRightBarX - startLeftBarX) + startLeftBarX;
        LOGE("leftValue",leftValue);
        LOGE("rightValue",rightValue);
        LOGE("mCurrentLeftBarPos",mCurrentLeftBarPos);
        LOGE("mCurrentRightBarPos",mCurrentRightBarPos);
    }
    private void LOGE(String name,float value){
        Log.e(TAG, "LOGE: "+ name + ":"+value);
    }
}
