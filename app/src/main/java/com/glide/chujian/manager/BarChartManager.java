package com.glide.chujian.manager;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class BarChartManager {
    private BarChart mBarChart;
    private YAxis mLeftAxis;
    private YAxis mRightAxis;

    public BarChartManager(BarChart barChart) {
        this.mBarChart = barChart;
        mLeftAxis = mBarChart.getAxisLeft();
        mRightAxis = mBarChart.getAxisRight();
    }

    /**
     * 初始化Chart
     */
    private void initChart() {
        //不显示描述内容
        mBarChart.getDescription().setEnabled(false);

        mBarChart.setBackgroundColor(Color.TRANSPARENT);
        mBarChart.setBorderColor(Color.WHITE);
        mBarChart.setNoDataTextColor(Color.WHITE);
        //显示边界
        mBarChart.setDrawBorders(false);
        mBarChart.setTouchEnabled(false);
        //图例说明
        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);//不显示下方的图标
        //Y轴设置
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
        XAxis xAxis = this.mBarChart.getXAxis();
        xAxis.setAxisMaximum(maxisMaximum);
        xAxis.setGranularity(1);//放缩的时候的最小值
        xAxis.setLabelCount(11,true);
        int item = (int)(maxisMaximum * 0.1);
        final String[] xLabels = new String[]{
                "0",
                (int)(maxisMaximum * 0.1)+"",
                (int)(maxisMaximum * 0.2)+"",
                (int)(maxisMaximum * 0.3)+"",
                (int)(maxisMaximum * 0.4)+"",
                (int)(maxisMaximum * 0.5)+"",
                (int)(maxisMaximum * 0.6)+"",
                (int)(maxisMaximum * 0.7)+"",
                (int)(maxisMaximum * 0.8)+"",
                (int)(maxisMaximum * 0.9)+"",
                (int)maxisMaximum+""
        };
/*        final String[] xLabels = new String[]{
                "0",
                item+"",
                item * 2+"",
                item * 3+"",
                item * 4+"",
                item * 5+"",
                item * 6+"",
                item * 7+"",
                item * 8+"",
                item * 9+"",
                (int)maxisMaximum+""
        };*/
        final Integer[] xLabelsIndex = new Integer[]{
                0,
                (int)((maxisMaximum-0.5)* 0.1),
                (int)((maxisMaximum-0.5)* 0.2),
                (int)((maxisMaximum-0.5)* 0.3),
                (int)((maxisMaximum-0.5)* 0.4),
                (int)((maxisMaximum-0.5)* 0.5),
                (int)((maxisMaximum-0.5)* 0.6),
                (int)((maxisMaximum-0.5)* 0.7),
                (int)((maxisMaximum-0.5)* 0.8),
                (int)((maxisMaximum-0.5)* 0.9),
                (int)maxisMaximum,
        };
/*        final Integer[] xLabelsIndex = new Integer[]{
                0,
                item,
                item * 2,
                item * 3,
                item * 4,
                item * 5,
                item * 6,
                item * 7,
                item * 8,
                item * 9,
                (int)maxisMaximum,
        };*/
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int i = 0;
                String label = "";
                for (;i < xLabelsIndex.length;i++){
                    if ((int)value == xLabelsIndex[i]){
                        label = xLabels[i];
                    }
                }
                return label;
            }
        });
        xAxis.setDrawLabels(true);//是否在顶部显示横坐标值
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);//是否显示顶部横线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);        //X轴所在位置   默认为上面
        xAxis.setDrawGridLines(false);//是否绘制X轴上的网格线（背景里面的竖线）
        mBarChart.invalidate();
    }

    public void showCombinedChart(
            Float maxisMaximum, List<BarEntry> list) {
        initChart();
        setXAxis(maxisMaximum);
        BarDataSet barDataSet=new BarDataSet(list,"");
        barDataSet.setDrawValues(false);//是否在点上显示数值 默认true
        BarData barData=new BarData(barDataSet);
        mBarChart.setData(barData);
        mBarChart.invalidate();
    }
}