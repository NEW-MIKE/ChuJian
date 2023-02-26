package com.glide.chujian.manager;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class HistorgramChartManager {
    private String TAG = "HistorgramChartManager";
    private LineChart mLineChart;
    private YAxis mLeftAxis;
    private YAxis mRightAxis;

    public HistorgramChartManager(LineChart barChart) {
        this.mLineChart = barChart;
        mLeftAxis = mLineChart.getAxisLeft();
        mRightAxis = mLineChart.getAxisRight();
        initChart();
    }

    /**
     * 初始化Chart
     */
    private void initChart() {
        //不显示描述内容
        mLineChart.getDescription().setEnabled(false);

        mLineChart.setBackgroundColor(Color.TRANSPARENT);
        mLineChart.setBorderColor(Color.WHITE);
        mLineChart.setNoDataTextColor(Color.WHITE);
        //显示边界
        mLineChart.setDrawBorders(false);
        mLineChart.setTouchEnabled(false);
        //图例说明
        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);//不显示下方的图标
        //Y轴设置
        mRightAxis.setEnabled(false);
        mRightAxis.setDrawGridLines(false);//是否显示横向轴线
        mRightAxis.setDrawLabels(false);//是否显示右边Y轴上数字
        mRightAxis.setDrawAxisLine(false);//是否显示右边Y轴

        mLeftAxis.setDrawZeroLine(true);//是否显示X轴的零线
        mLeftAxis.setAxisMinimum(0f);
        mLeftAxis.setAxisMaximum(5000f);
        mLeftAxis.setDrawLabels(false);//是否显示左边的Y轴上的数字坐标
        mLeftAxis.setDrawAxisLine(false);//是否显示左边的Y轴坐标
        mLeftAxis.setDrawGridLines(false);//是否绘制Y轴上的网格线（背景里面的横线）
    }


    /**
     * 设置X轴坐标值
     *
     */
    public void setXAxis(float maxisMaximum) {
        XAxis xAxis = this.mLineChart.getXAxis();
        xAxis.setAxisMaximum(maxisMaximum);
        xAxis.setGranularity(1);//放缩的时候的最小值
        xAxis.setDrawLabels(false);//是否在顶部显示横坐标值
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);//是否显示顶部横线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);        //X轴所在位置   默认为上面
        xAxis.setDrawGridLines(false);//是否绘制X轴上的网格线（背景里面的竖线）
       // mLineChart.invalidate();
    }

    public void showCombinedChart(
            Float maxisMaximum, List<Entry> list) {
       // mLineChart.setEnabled(true);
        setXAxis(maxisMaximum);
        LineDataSet lineDataSet = new LineDataSet(list, "");
        lineDataSet.setDrawValues(false);//在点上显示数值 默认true
        lineDataSet.setDrawCircles(false);//在点上画圆 默认true
        LineData data = new LineData(lineDataSet);
        mLineChart.setData(data);
        mLineChart.invalidate();

        mLineChart.setEnabled(false);
    }
}