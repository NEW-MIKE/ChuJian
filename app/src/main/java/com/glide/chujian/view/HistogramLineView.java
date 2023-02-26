package com.glide.chujian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.glide.chujian.R;

import java.util.ArrayList;
import java.util.List;
public class HistogramLineView extends View {

    private boolean mIsShowXAxis = true;
    private boolean mIsShowYAxis = true;
    //xy坐标轴颜色
    private int mXYLineColor = 0;
    /**
     * xy坐标轴线的宽度
     */
    private int mXYLineWidth = 1;

    private boolean mIsDrawLine = true;
    /**
     * xy坐标轴文字颜色
     */
    private int mXyTextColor = 0;
    /**
     * xy坐标轴文字大小
     */
    private int mXyTextSize = dpToPx(13);
    /**
     * 折线图中折线的颜色
     */
    private int mLineColor = 0;
    /**
     * 折线图中点位信息颜色
     */
    private int mContentColor = 0;

    /**
     * x轴各个坐标点水平间距
     */
    private int interval = dpToPx(30);
    /**
     * 背景颜色
     */
    private int bgColor = 0;
    /**
     * 绘制XY轴坐标对应的画笔
     */
    private Paint xyPaint;
    /**
     * 绘制XY轴的文本对应的画笔
     */
    private Paint xyTextPaint;
    /**
     * 画折线对应的画笔
     */
    private Paint mLinePaint;
    /**
     * 画折线对应的显示数据画笔
     */
    private Paint showPaint;
    private int width;
    private int height;
    /**
     * x轴的原点坐标
     */
    private int xOrigin;
    /**
     * y轴的原点坐标
     */
    private int yOrigin;
    /**
     * 第一个点X的坐标
     */
    private float xInit;
    /**
     * x轴坐标对应的数据
     */
    private List<XValue> xValues = new ArrayList<>();
    /**
     * y轴坐标对应的数据
     */
    private List<YValue> yValues = new ArrayList<>();
    /**
     * 折线对应的数据
     */
    private List<LineValue> lineValues = new ArrayList<>();
    /**
     * X轴刻度文本对应的最大矩形，为了选中时，在x轴文本画的框框大小一致
     * 这个可以用来显示坐标，全部根据自己的需求来定义
     */
    private Rect xValueRect;

    /**
     * X轴文本的高度
     */
    private int xTextHeight;

    public HistogramLineView(Context context) {
        this(context, null);
    }

    public HistogramLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistogramLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        xyPaint = new Paint();
        xyPaint.setAntiAlias(true);
        // xyPaint.setStrokeWidth(mXYLineWidth);
        xyPaint.setStrokeWidth(1);
        //  xyPaint.setStrokeCap(Paint.Cap.ROUND);
        // xyPaint.setColor(mXYLineColor);
        xyPaint.setColor(Color.parseColor("#ffffff"));

        xyTextPaint = new Paint();
        xyTextPaint.setAntiAlias(true);
        xyTextPaint.setTextSize(mXyTextSize);
        xyTextPaint.setStrokeCap(Paint.Cap.ROUND);
        xyTextPaint.setColor(mXyTextColor);
        xyTextPaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        // mLinePaint.setStrokeWidth(mXYLineWidth);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setColor(Color.parseColor("#7fbecf"));
        mLinePaint.setStyle(Paint.Style.STROKE);

        showPaint = new Paint();
        showPaint.setAntiAlias(true);
        showPaint.setStrokeWidth(mXYLineWidth);
        showPaint.setStrokeCap(Paint.Cap.ROUND);
        showPaint.setColor(Color.parseColor("#ffffff"));
        showPaint.setStyle(Paint.Style.FILL);
        showPaint.setTextSize(mXyTextSize);
    }

    /**
     * 初始化自定义属性
     */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.brokenLineView);
        mXYLineColor = array.getColor(R.styleable.brokenLineView_dbz_xy_line_color, mXYLineColor);
        mXYLineWidth = (int) array.getDimension(R.styleable.brokenLineView_dbz_xy_line_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mXYLineWidth, getResources().getDisplayMetrics()));
        mXyTextColor = array.getColor(R.styleable.brokenLineView_dbz_xy_text_color, mXyTextColor);
        mXyTextSize = (int) array.getDimension(R.styleable.brokenLineView_dbz_xy_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mXyTextSize, getResources().getDisplayMetrics()));
        mLineColor = array.getColor(R.styleable.brokenLineView_dbz_line_color, mLineColor);
        mContentColor = array.getColor(R.styleable.brokenLineView_dbz_content_color, mContentColor);
        bgColor = array.getColor(R.styleable.brokenLineView_dbz_bg_color, bgColor);
        interval = (int) array.getDimension(R.styleable.brokenLineView_dbz_interval, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, interval, getResources().getDisplayMetrics()));
        array.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            //得到宽度高度
            width = getWidth();
            height = getHeight();
            int dp2 = dpToPx(2);
            int dp3 = dpToPx(3);
            xOrigin = dpToPx(15);
            // 测量X轴月份的高度
            xValueRect = getTextBounds("00月00日", xyTextPaint);
            xTextHeight = xValueRect.width(); // 因为是垂直显示所以宽度就是高度
            // 把宽度分成10 左右各占一个间距  总宽度 - 左右距离和中间的距离 除以10列
            interval = (width - ((width / 10) - getPaddingLeft() - getPaddingRight())) / 10;
            // 要把底部文本的高度留出来
            yOrigin = dpToPx(15);//(height - dp2 - xValueRect.height() - dp3 - mXYLineWidth) - xTextHeight;//dp3是x轴文本距离底边，dp2是x轴文本距离x轴的距离
            xInit = 0;//interval + xOrigin;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // canvas.drawColor(bgColor);
        drawXY(canvas);
        if (mIsDrawLine) {
            drawBrokenLine(canvas);
        }
    }

    public void setLineVisible(boolean isVisible){
        mIsDrawLine = isVisible;
    }
    /**
     * 绘制折线
     */
    private void drawBrokenLine(Canvas canvas) {
        mLinePaint.setStyle(Paint.Style.STROKE);
        //绘制折线
        Path path = new Path();
        int deltaX = (width - xOrigin - xOrigin)/(xValues.size()-1);
        double maxPixelX = (width - xOrigin - xOrigin);
        double maxDataX = 0;
        float y = 0;
        if (lineValues.size() > 0){
            maxDataX = lineValues.get(lineValues.size() -1).pos - lineValues.get(0).pos;
            y = (float) ((height - yOrigin - yOrigin) * lineValues.get(0).num / 5000f);
            if (y < 0) y = 0;
            path.moveTo(xOrigin, (float) (height - yOrigin - y));
        }
        for (int i = 1; i < lineValues.size(); i++) {
            y = (float) ((height - yOrigin - yOrigin) * lineValues.get(i).num / 5000f);
            if (y < 0) y = 0;
            path.lineTo((float) (xOrigin + getLineXLocation(lineValues.get(i).pos,maxDataX) * maxPixelX), (float) (height - yOrigin - y));
        }
        canvas.drawPath(path, mLinePaint);
    }

    private double getLineXLocation(double location, double maxLength){
        return location / maxLength ;
    }

    /**
     * 绘制XY坐标
     *
     * @param canvas
     */
    private void drawXY(Canvas canvas) {
        if (mIsShowXAxis) {
            canvas.drawLine(xOrigin, height - yOrigin, width - xOrigin, height - yOrigin, xyPaint);
            double maxXPixel = (width - xOrigin - xOrigin);
            double maxValueLength = 0;
            if (xValues.size() > 0){
                maxValueLength = xValues.get(xValues.size() - 1).num - xValues.get(0).num;
            }
            if (maxValueLength == 0)return;
            int lineStartX = xOrigin;
            for (int i = 0; i < xValues.size(); i++) {
                double pos = xValues.get(i).num / maxValueLength * maxXPixel;
                String text = xValues.get(i).value;
                Rect textRect = getTextBounds(text,xyPaint);
                int textWidth = textRect.width();
                canvas.drawText(text, (float) (lineStartX + pos) - textWidth / 2, height - yOrigin/4, xyPaint);
                canvas.drawLine((float) (lineStartX + pos), (float) (height - yOrigin), (float) (lineStartX + pos), height - yOrigin - 10, xyPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                // clickAction(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    public void setValue(List<LineValue> lineValues) {
        this.lineValues = lineValues;
        invalidate();
    }

    public void setValue(List<LineValue> lineValues, List<XValue> xValues, List<YValue> yValues) {
        this.lineValues = lineValues;
        this.xValues = xValues;
        this.yValues = yValues;
        invalidate();
    }
    public void setValue(List<LineValue> lineValues, List<XValue> xValues) {
        this.lineValues = lineValues;
        this.xValues = xValues;
        this.yValues = yValues;
        invalidate();
    }

    /**
     * 获取丈量文本的矩形
     *
     * @param text
     * @param paint
     * @return
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    /**
     * dp转化成为px
     *
     * @param dp
     * @return
     */
    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    public static class XValue {
        public double num;
        public String value;

        public XValue(double num, String value) {
            this.num = num;
            this.value = value;
        }
    }

    public static class YValue {
        public double num;
        public String value;

        public YValue(double num, String value) {
            this.num = num;
            this.value = value;
        }
    }

    public static class LineValue {
        //具体数值
        public double num;
        public double pos;

        public LineValue(double num, double pos) {
            this.num = num;
            this.pos = pos;
        }
    }
}