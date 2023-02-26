package com.glide.chujian.manager;

import android.graphics.Color;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.glide.chujian.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

public class CombinedChartManager {
    private CombinedChart mCombinedChart;
    private YAxis mLeftAxis;
    private YAxis mRightAxis;
    private int mOffset;

    public CombinedChartManager(CombinedChart combinedChart) {
        this.mCombinedChart = combinedChart;
        mLeftAxis = mCombinedChart.getAxisLeft();
        mRightAxis = mCombinedChart.getAxisRight();
        mOffset = ScreenUtil.androidAutoSizeDpToPx(15);
    }

    /**
     * 初始化Chart
     */
    private void initChart() {
        //不显示描述内容
        mCombinedChart.getDescription().setEnabled(false);

        mCombinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.LINE
        });
        mCombinedChart.setBackgroundColor(Color.TRANSPARENT);
        mCombinedChart.setBorderColor(Color.WHITE);
        mCombinedChart.setNoDataTextColor(Color.WHITE);
        //显示边界
        mCombinedChart.setDrawBorders(true);
        //mCombinedChart.setTouchEnabled(false);
        mCombinedChart.setHighlightPerTapEnabled(false);
        mCombinedChart.setHighlightPerDragEnabled(false);
      //  mCombinedChart.setMinOffset(0);
       // mCombinedChart.setExtraOffsets(0,offset,0, 0);
        //图例说明
        Legend legend = mCombinedChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //Y轴设置
        mRightAxis.setDrawGridLines(false);
        mRightAxis.setDrawLabels(false);

        mLeftAxis.setAxisMinimum(-5f);
        mLeftAxis.setAxisMaximum(5f);
        mLeftAxis.setTextColor(Color.WHITE);
        mLeftAxis.setAxisLineColor(Color.WHITE);
        mLeftAxis.setGridColor(Color.WHITE);
    }

    public void setMaxAxis(float axis){
        mLeftAxis.setAxisMinimum(-axis);
        mLeftAxis.setAxisMaximum(axis);
    }

    /**
     * 设置X轴坐标值
     *
     */
    public void setXAxis(float maxisMaximum) {
        XAxis xAxis = this.mCombinedChart.getXAxis();
        xAxis.setAxisMaximum(maxisMaximum);
        xAxis.setGranularity(1.0F);
        xAxis.setDrawLabels(false);
        mCombinedChart.invalidate();
    }

    /**
     * 得到折线图(多条)
     *
     * @param lineChartYs 折线Y轴值
     * @param lineNames   折线图名字
     * @param lineColors  折线颜色
     * @return
     */
    private LineData getLineData(List<List<Float>> lineChartYs, List<String> lineNames, List<Integer> lineColors) {
        LineData lineData = new LineData();

        for (int i = 0; i < lineChartYs.size(); i++) {
            ArrayList<Entry> yValues = new ArrayList<>();
            for (int j = 0; j < lineChartYs.get(i).size(); j++) {
                if (lineChartYs.get(i).get(j) != null) {
                    yValues.add(new Entry(j, lineChartYs.get(i).get(j)));
                }
            }
            LineDataSet dataSet = new LineDataSet(yValues, lineNames.get(i));
            dataSet.setColor(lineColors.get(i));
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            lineData.addDataSet(dataSet);
        }
        return lineData;
    }

    /**
     * 得到柱状图(多条)
     *
     * @param barChartYs Y轴值
     * @param barNames   柱状图名字
     * @param barColors  柱状图颜色
     * @return
     */

    private BarData getBarData(List<List<Float>> barChartYs, List<String> barNames, List<Integer> barColors) {
        List<IBarDataSet> lists = new ArrayList<>();
        for (int i = 0; i < barChartYs.size(); i++) {
            ArrayList<BarEntry> entries = new ArrayList<>();

            for (int j = 0; j < barChartYs.get(i).size(); j++) {
                if (barChartYs.get(i).get(j) != null) {
                    entries.add(new BarEntry(j, barChartYs.get(i).get(j)));
                }
            }
            BarDataSet barDataSet = new BarDataSet(entries, barNames.get(i));

            barDataSet.setColor(barColors.get(i));
            barDataSet.setDrawValues(false);
            lists.add(barDataSet);
        }
        BarData barData = new BarData(lists);

        int amount = barChartYs.size(); //需要显示柱状图的类别 数量
        float groupSpace = 0.12f; //柱状图组之间的间距
        float barSpace = (float) ((1 - 0.12) / amount / 10); // x4 DataSet
        float barWidth = (float) ((1 - 0.12) / amount / 10 * 9); // x4 DataSet

        // (0.2 + 0.02) * 4 + 0.12 = 1.00 即100% 按照百分百布局
        //柱状图宽度
        barData.setBarWidth(barWidth);
        //(起始点、柱状图组间距、柱状图之间间距)
        barData.groupBars(0, groupSpace, barSpace);
        return barData;
    }


    /**
     * 显示混合图(柱状图+折线图)
     *
     * @param maxisMaximum X轴坐标
     * @param barChartYs  柱状图Y轴值
     * @param lineChartYs 折线图Y轴值
     * @param barNames    柱状图名字
     * @param lineNames   折线图名字
     * @param barColors   柱状图颜色
     * @param lineColors  折线图颜色
     */

    public void showCombinedChart(
            Float maxisMaximum, List<List<Float>> barChartYs, List<List<Float>> lineChartYs,
            List<String> barNames, List<String> lineNames, List<Integer> barColors, List<Integer> lineColors) {
        initChart();
        setXAxis(maxisMaximum);

        CombinedData combinedData = new CombinedData();

        combinedData.setData(getBarData(barChartYs, barNames, barColors));
        combinedData.setData(getLineData(lineChartYs, lineNames, lineColors));

        mCombinedChart.setData(combinedData);
        mCombinedChart.invalidate();
    }
}