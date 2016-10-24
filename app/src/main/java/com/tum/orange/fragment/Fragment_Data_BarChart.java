package com.tum.orange.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.tum.orange.javabean.MyDataBean;
import com.tum.orange.tum_lmt.DataResultShowActivity;
import com.tum.orange.tum_lmt.R;

import java.util.ArrayList;

/**
 * Created by Orange on 21.10.2016.
 */

public class Fragment_Data_BarChart extends Fragment {

    private View view;
    private BarData mBarData;
    private BarChart mBarChart;
    private ArrayList<MyDataBean> mDataList;
    private float Max;
    private float Min;
    private int xLabelInterval;
    private DataResultShowActivity mActivity;
    private ArrayList<ArrayList<Float>> list;
    private Toast toast;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (DataResultShowActivity) context;
        mDataList = mActivity.mDataList;
        //get the max value and min value in all dataSet, that we measured before
        Max = Float.parseFloat(mDataList.get(mDataList.size() - 1).getMax());
        Min = Float.parseFloat(mDataList.get(mDataList.size() - 1).getMin());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_data_barchart, null);
            mBarChart = (BarChart) view.findViewById(R.id.bar_chart);
            mBarData = getBarData(10, 100);
            showBarChart(mBarChart, mBarData);
        }
        return view;
    }

    private BarData getBarData(int count, float range) {
        String minLabel;
        String maxLabel;
        int minLabelValue;
        int maxLabelValue;
        String xLabel = null;
        ArrayList<Float> leftValue = new ArrayList<>();
        ArrayList<Float> rightValue = new ArrayList<>();

        xLabelInterval = (int) ((Max - Min) / count);
        //x轴的数据集合
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
                minLabelValue = (int) (Min + (i * xLabelInterval));
                maxLabelValue = (int) Max+1;
                minLabel = String.valueOf(minLabelValue);
                maxLabel = String.valueOf(maxLabelValue);
                xLabel = minLabel + "-" + maxLabel + " [ms]";
                leftValue.add((float) minLabelValue);
                rightValue.add((float) maxLabelValue);

            } else {
                minLabelValue = (int) (Min + (i * xLabelInterval));
                maxLabelValue = (int) (Min + ((i + 1) * xLabelInterval));
                minLabel = String.valueOf(minLabelValue);
                maxLabel = String.valueOf(maxLabelValue);
                xLabel = minLabel + "-" + maxLabel + " [ms]";
                leftValue.add((float) minLabelValue);
                rightValue.add((float) maxLabelValue);
            }
            xValues.add(xLabel);
        }


        list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new ArrayList<Float>());
        }

        for (int i = 0; i < mDataList.size(); i++) {
            MyDataBean bean = mDataList.get(i);
            float current = Float.parseFloat(bean.getCurrentMeasure());
            for (int i1 = 0; i1 < count; i1++) {
                if (current >= leftValue.get(i1) && current <= rightValue.get(i1)) {
                    list.get(i1).add(current);
                    break;
                }
            }
        }


        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for (int i = 0; i < count; i++) {
            //float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
            float value = (list.get(i).size() / (float) mDataList.size()) * 100;
            yValues.add(new BarEntry(value, i));
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "Distribution Frequency [%]");
        barDataSet.setColor(Color.BLUE);

        /*ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets*/

        BarData barData = new BarData(xValues, barDataSet);

        return barData;
    }

    private void showBarChart(BarChart barChart, BarData barData) {
        barChart.setDrawBorders(false);  ////是否在折线图上添加边框

        barChart.setDescription("");// 数据描述

        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("You need to provide data for the chart.");

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.RED); // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(true); // 设置是否可以触摸

        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(true);// 是否可以缩放

        barChart.setPinchZoom(false);//

        barChart.setBackgroundColor(Color.WHITE);// 设置背景

        barChart.setDrawBarShadow(false);

        barChart.setData(barData); // 设置数据

        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.BLACK);// 颜色

//      X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置x轴数据是否跳转
        //xAxis.setLabelsToSkip(0);
        //xAxis.setSpaceBetweenLabels(2);
        barChart.getAxisRight().setEnabled(false);

       // barChart.getAxisLeft().setAxisMaxValue(100);
        barChart.getAxisLeft().setSpaceBottom(0);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                int count = list.get(e.getXIndex()).size();
                if (toast != null) {
                    toast.cancel();
                    toast = Toast.makeText(mActivity, count + " of " + mDataList.size() + " " +
                            "data in this interval", Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(mActivity, count + " of " + mDataList.size() + " " +
                            "data in this interval", Toast.LENGTH_SHORT);
                }
                toast.show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart.animateX(2500); // 立即执行的动画,x轴
        barChart.animateY(2500); // 立即执行的动画,y轴
    }
}
