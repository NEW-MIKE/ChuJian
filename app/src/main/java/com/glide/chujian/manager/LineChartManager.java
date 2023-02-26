package com.glide.chujian.manager;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class LineChartManager {
    private String TAG = "LineChartManager";
    private LineChart mLineChart;
    private YAxis mLeftAxis;
    private YAxis mRightAxis;

    public LineChartManager(LineChart barChart) {
        this.mLineChart = barChart;
        mLeftAxis = mLineChart.getAxisLeft();
        mRightAxis = mLineChart.getAxisRight();
    }

    /**
     * 初始化Chart
     */
    private void initChart() {
        //不显示描述内容
        mLineChart.getDescription().setEnabled(true);

        Description description = new Description();
        description.setText("时间/h");
        description.setTextColor(Color.WHITE);
        mLineChart.setDescription(description);
       // mLineChart.setBackgroundColor(Color.TRANSPARENT);
        mLineChart.setBorderColor(Color.WHITE);
        mLineChart.setNoDataTextColor(Color.WHITE);
        //显示边界
        mLineChart.setDrawBorders(false);
        mLineChart.setTouchEnabled(false);
        //图例说明
     //   mLineChart.setExtraLeftOffset(ScreenUtil.androidAutoSizeDpToPx(15));
        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);//不显示下方的图标
        //Y轴设置
       //
        mRightAxis.setEnabled(false);
       // mLeftAxis.setPosition();
        mLeftAxis.enableGridDashedLine(4,4,0);
        mLeftAxis.setDrawZeroLine(true);//是否显示X轴的零线
        mLeftAxis.setAxisMinimum(0f);
        mLeftAxis.setTextColor(Color.WHITE);
        mLeftAxis.setAxisMaximum(90f);
        mLeftAxis.setLabelCount(3);
        mLeftAxis.setDrawLabels(true);//是否显示左边的Y轴上的数字坐标
        mLeftAxis.setDrawAxisLine(true);//是否显示左边的Y轴坐标
        mLeftAxis.setDrawGridLines(true);//是否绘制Y轴上的网格线（背景里面的横线）
    }


    /**
     * 设置X轴坐标值
     *
     */
    public void setXAxis(float maxisMaximum) {
        XAxis xAxis = this.mLineChart.getXAxis();
        xAxis.setAxisMaximum(maxisMaximum);
        xAxis.setGranularity(1);//放缩的时候的最小值
        xAxis.setLabelCount(12,false);
        final String[] xLabels = new String[]{
                "18","19","20","21","22","23",
                "00","01","02","03","04","05",
                "06"
        };
        final Integer[] xLabelsIndex = new Integer[]{
                0,1,2,3,4,5,6,7,8,9,10,11,12
        };
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
                Log.e(TAG, "getAxisLabel: value"+value );
                return label;
            }
        });
        xAxis.setDrawLabels(true);//是否在顶部显示横坐标值
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);//是否显示顶部横线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴所在位置   默认为上面
        xAxis.setDrawGridLines(false);//是否绘制X轴上的网格线（背景里面的竖线）
        mLineChart.invalidate();
    }

    public void showLineChart(
            Float maxisMaximum,ArrayList<Double> LineData,float x,Float max) {
        initChart();
        setXAxis(maxisMaximum);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < LineData.size(); i++) {
            entries.add(new Entry(i, LineData.get(i).floatValue()));
            if (i == (int)(x)){
                String time = (int)((x - ((int)x))*60)+"";
                Log.e(TAG, "target_location: x"+x +"max"+max+"i-->"+i+"time"+time);
                entries.add(new Entry(x, max));
            }
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setDrawValues(false);//在点上显示数值 默认true
        lineDataSet.setDrawCircles(false);//在点上画圆 默认true
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData data = new LineData(lineDataSet);
        mLineChart.setData(data);
        mLineChart.invalidate();
    }
}