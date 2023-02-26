package com.glide.chujian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.glide.chujian.R;

import java.util.ArrayList;
import java.util.List;
/*
 * 这个要设计出来的效果是，能够根据具体的点位，画出折线图还有就是竖直的那一根线，虚线，还有线上的值，坐标轴的值。
 * 我的需求是，给定一组数据，然后把这一组数据通通画在画布上面，采集的包括坐标轴的显示，画的坐标轴，包括横坐标，纵坐标，还有对应的虚线，
 * 出现在坐标轴里面的东西，通通的都给画出来。设计思路是：将坐标轴的文字的宽高给设计好，然后得出可供折线显示的区域的，根据得到的数据的开始和结束的数据，
 * 计算每一个像素点的位置，最终的这些数据，统统转化为像素点，然后把这些像素点连起来，绘制出来。
 *
 * */


public class BrokenLineView extends View {

    private boolean mIsShowXAxis = true;
    private boolean mIsShowYAxis = true;
    //xy坐标轴颜色
    private int mXYLineColor = 0;
    /**
     * xy坐标轴线的宽度
     */
    private int mXYLineWidth = 1;
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
    private int mLinecolor = 0;
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
    private int bgcolor = 0;
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
    /**
     * 画最大值的画笔
     */
    private Paint maxLinePaint;
    private int width;
    private int height;
    /**
     * 绘制Y 轴 虚线的画笔
     */
    private Paint yEffectPaint;
    /**
     * x轴的原点坐标
     */
    private int xOrigin;
    private int xRightOrigin;
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
     * 当前选中点
     */
    private int selectIndex = 1;

    private boolean hasMaxLine = false;
    private double maxLinePos = 0;
    private String maxLineValue = "";

    private boolean isShowXAxisName = false;
    private String xAxisName = "";

    private boolean isShowXEffectLine = false;
    /**
     * X轴刻度文本对应的最大矩形，为了选中时，在x轴文本画的框框大小一致
     * 这个可以用来显示坐标，全部根据自己的需求来定义
     */
    private Rect xValueRect;

    /**
     * X轴文本的高度
     */
    private int xTextHeight;

    public BrokenLineView(Context context) {
        this(context, null);
    }

    public BrokenLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrokenLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        initPaint();
        //initData();
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
        Rect tempRect = getTextBounds("06 时间/h", xyPaint);
        xRightOrigin = tempRect.width();
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
        mLinePaint.setColor(Color.parseColor("#ffffff"));
        mLinePaint.setStyle(Paint.Style.STROKE);

        showPaint = new Paint();
        showPaint.setAntiAlias(true);
        showPaint.setStrokeWidth(mXYLineWidth);
        showPaint.setStrokeCap(Paint.Cap.ROUND);
        showPaint.setColor(Color.parseColor("#ffffff"));
        showPaint.setStyle(Paint.Style.FILL);
        showPaint.setTextSize(mXyTextSize);

        maxLinePaint = new Paint();
        maxLinePaint.setColor(getResources().getColor(R.color.green));
        maxLinePaint.setPathEffect(new DashPathEffect(new float[]{8,8},0));
        maxLinePaint.setStrokeJoin(Paint.Join.ROUND);//设置画笔图形接触时笔迹的形状
        maxLinePaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔离开画板时笔迹的形状
        maxLinePaint.setStrokeWidth(2);

        yEffectPaint = new Paint();
        yEffectPaint.setColor(Color.parseColor("#ffffff"));
        yEffectPaint.setPathEffect(new DashPathEffect(new float[]{4,4},0));
        yEffectPaint.setStrokeJoin(Paint.Join.ROUND);//设置画笔图形接触时笔迹的形状
        yEffectPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔离开画板时笔迹的形状
        yEffectPaint.setStrokeWidth(1);
    }

    private void initData(){
        yValues.clear();
        yValues.add(new YValue(0,"0"));
        yValues.add(new YValue(30,"30"));
        yValues.add(new YValue(60,"60"));
        yValues.add(new YValue(90,"90"));
        xValues.clear();
        xValues.add(new XValue(0,"18"));
        xValues.add(new XValue(1,"19"));
        xValues.add(new XValue(2,"20"));
        xValues.add(new XValue(3,"21"));
        xValues.add(new XValue(4,"22"));
        xValues.add(new XValue(5,"23"));
        xValues.add(new XValue(6,"00"));
        xValues.add(new XValue(7,"01"));
        xValues.add(new XValue(8,"02"));
        xValues.add(new XValue(9,"03"));
        xValues.add(new XValue(10,"04"));
        xValues.add(new XValue(11,"05"));
        xValues.add(new XValue(12,"06"));
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
        mLinecolor = array.getColor(R.styleable.brokenLineView_dbz_line_color, mLinecolor);
        mContentColor = array.getColor(R.styleable.brokenLineView_dbz_content_color, mContentColor);
        bgcolor = array.getColor(R.styleable.brokenLineView_dbz_bg_color, bgcolor);
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
            xOrigin = dpToPx(15);//dp2是y轴文本距离左边，以及距离y轴的距离
//            xValueRect = getTextBounds("0000", xyTextPaint);
//            //X轴字高度
//            float textXHeight = xValueRect.height();
//            for (int i = 0; i < xValues.size(); i++) {//求取x轴文本最大的高度
//                Rect rect = getTextBounds(xValues.get(i).value, xyTextPaint);
//                if (rect.height() > textXHeight)
//                    textXHeight = rect.height();
//                if (rect.width() > xValueRect.width())
//                    xValueRect = rect;
//            }
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
        canvas.drawColor(bgcolor);
        drawXY(canvas);
        drawBrokenLine(canvas);
        drawMaxLine(canvas);
        drawXAxisName(canvas);
        drawXEffectLine(canvas);
        //   drawBrokenLineAndPoint(canvas);
    }

    /**
     * 绘制折线和折线点
     */
    private void drawBrokenLineAndPoint(Canvas canvas) {
        if (xValues.size() <= 0)
            return;
        //设置显示折线的图层
        int layer = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        drawBrokenLine(canvas);
        // drawBrokenPoint(canvas);
        // 将折线超出x轴坐标的部分截取掉
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(bgcolor);
        mLinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        RectF rectF = new RectF(0, 0, xOrigin, height);
        canvas.drawRect(rectF, mLinePaint);
        mLinePaint.setXfermode(null);
        //保存图层
        canvas.restoreToCount(layer);
    }

    public void setShowXEffectLine(boolean isShow){
        isShowXEffectLine = isShow;
    }

    private void drawXEffectLine(Canvas canvas){
        if (isShowXEffectLine) {
            for (int j = 1; j < yValues.size(); j++) {
                float deltaY = (float) (height - yOrigin - yOrigin) / (yValues.size() - 1);
                canvas.drawLine(xOrigin, (height - yOrigin) - deltaY * j, width - xRightOrigin, (height - yOrigin) - deltaY * j, yEffectPaint);
            }
        }
    }

    public void setXAxisName(boolean showXAxisName , String name){
        isShowXAxisName = showXAxisName;
        xAxisName = name;
    }

    private void drawXAxisName(Canvas canvas){
        if (isShowXAxisName){
            Rect maxTextRect = getTextBounds(xAxisName, xyPaint);
            int textHeight = maxTextRect.height();
            int lineStartX = xOrigin;
            int deltaX = (width - xOrigin - xOrigin)/(xValues.size()-1);
            int i = xValues.size() -1;
            canvas.drawText(xAxisName, lineStartX + deltaX * i -8, height - yOrigin/4, xyPaint);
        }
    }

    public void setTargetLineAndTitle(boolean hasMaxLine,double pos,String value){
        this.hasMaxLine = hasMaxLine;
        maxLinePos = pos;
        maxLineValue = value;
    }

    private void drawMaxLine(Canvas canvas){
        if (hasMaxLine){
            double maxPixelX = (width - xOrigin - xRightOrigin);
            double maxDataX = 0;
            if (lineValues.size() > 0){
                maxDataX = lineValues.get(lineValues.size() -1).pos - lineValues.get(0).pos;
            }
            float y = (float) ((height - yOrigin - yOrigin));
            float mXLocation = (float) (xOrigin + getLineXLocation(maxLinePos,maxDataX) * maxPixelX);
            canvas.drawLine(mXLocation, (float) (height - yOrigin), mXLocation, (float) (height - yOrigin - y), maxLinePaint);

            Rect maxTextRect = getTextBounds(maxLineValue, xyPaint);
            int width = maxTextRect.width();
            canvas.drawText(maxLineValue, mXLocation - width/2, (float) (height - yOrigin - y) -10, xyPaint);
        }
    }

    /**
     * 绘制折线
     */
    private void drawBrokenLine(Canvas canvas) {
        mLinePaint.setStyle(Paint.Style.STROKE);
        //绘制折线
        Path path = new Path();
        int deltaX = (width - xOrigin - xRightOrigin)/(xValues.size()-1);
        double maxPixelX = (width - xOrigin - xRightOrigin);
        double maxDataX = 0;
        float y = 0;
        if (lineValues.size() > 0){
            maxDataX = lineValues.get(lineValues.size() -1).pos - lineValues.get(0).pos;
            y = (float) ((height - yOrigin - yOrigin) * lineValues.get(0).num / yValues.get(yValues.size() - 1).num);
            if (y < 0) y = 0;
            path.moveTo(xOrigin, (float) (height - yOrigin - y));
        }
        for (int i = 1; i < lineValues.size(); i++) {
            y = (float) ((height - yOrigin - yOrigin) * lineValues.get(i).num / yValues.get(yValues.size() - 1).num);
            if (y < 0) y = 0;
            path.lineTo((float) (xOrigin + getLineXLocation(lineValues.get(i).pos,maxDataX) * maxPixelX), (float) (height - yOrigin - y));
        }
        canvas.drawPath(path, mLinePaint);
    }

    private double getLineXLocation(double location, double maxLength){
        return location / maxLength ;
    }
    public void showXAxis(boolean isShowAxisX){
        mIsShowXAxis = isShowAxisX;
    }

    public void showYAxis(boolean isShowAxisY){
        mIsShowYAxis = isShowAxisY;
    }

    public void setPaintColor(String color){
        xyPaint.setColor(Color.parseColor(color));
        mLinePaint.setColor(Color.parseColor(color));
        showPaint.setColor(Color.parseColor(color));
    }
    /**
     * 绘制XY坐标
     *
     * @param canvas
     */
    private void drawXY(Canvas canvas) {
        if (mIsShowXAxis) {
            canvas.drawLine(xOrigin, height - yOrigin, width - xRightOrigin, height - yOrigin, xyPaint);
            for (int i = 0; i < xValues.size(); i++) {
                int yLength = (int) (yOrigin * (1 - 0.1f) / (yValues.size() - 1));//y轴上面空出10%,计算出y轴刻度间距
                int lineStartX = xOrigin;
                int deltaX = (width - xOrigin - xRightOrigin)/(xValues.size()-1);
                float deltaY = (float)(height - yOrigin - yOrigin)/(yValues.size()-1);

                String text = xValues.get(i).value;
                canvas.drawText(text, lineStartX + deltaX * i, height - yOrigin/4, xyPaint);
            }
        }
        if (mIsShowYAxis) {
            canvas.drawLine(xOrigin, height - yOrigin, xOrigin, yOrigin, xyPaint);
        }
        for (int j = 0;j < yValues.size();j++){
            int yLength = (int) (yOrigin * (1 - 0.1f) / (yValues.size() - 1));//y轴上面空出10%,计算出y轴刻度间距
            int lineStartX = xOrigin;
            int deltaX = (width - xOrigin - xRightOrigin)/(xValues.size()-1);
            float deltaY = (float)(height - yOrigin - yOrigin)/(yValues.size()-1);

            String text = yValues.get(j).value;
            Rect rect = getTextBounds(text, xyPaint);
            canvas.drawLine(xOrigin, (height - yOrigin) - deltaY * j, xOrigin+10, (height - yOrigin) - deltaY *j , xyPaint);
            canvas.drawText(text, 0, text.length(), xOrigin - mXYLineWidth - dpToPx(2) - rect.width(), (height - yOrigin) - deltaY *j + rect.height() / 2, xyPaint);
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


    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        invalidate();
    }

    public void setxValue(List<XValue> xValues) {
        this.xValues = xValues;
    }

    public void setyValue(List<YValue> yValues) {
        this.yValues = yValues;
        invalidate();
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
        //显示值
        public String value;

        public LineValue(double num, double pos, String value) {
            this.num = num;
            this.pos = pos;
            this.value = value;
        }
    }
}