package com.tum.orange.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tum.orange.MyCustomView.MyCustomMarkerView;
import com.tum.orange.javabean.MyDataBean;
import com.tum.orange.tum_lmt.DataResultShowActivity;
import com.tum.orange.tum_lmt.R;

import java.util.ArrayList;

/**
 * data with chart display
 * Created by Orange on 07.10.2016.
 */

public class Fragment_Data_ChartDisplay extends Fragment {
    private View view;
    private LineChart mChart;
    private DataResultShowActivity mActivity;
    private ArrayList<MyDataBean> myDataBeanArrayList;
    private MyCustomMarkerView myCustomMarkerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_data_chartdisplay, container, false);
            mChart = (LineChart) view.findViewById(R.id.fragment_data_chartDisplay);
            myCustomMarkerView = new MyCustomMarkerView(mActivity, R.layout
                    .mycustommarkerview);
            initChartView();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (DataResultShowActivity) context;
        myDataBeanArrayList = mActivity.mDataList;
    }


    private void initChartView() {
        mChart.setDescription("Delay Count");
        mChart.setTouchEnabled(true);
        // enable Drag
        mChart.setDragEnabled(true);
        // enable Scale
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        // 设置图表的背景颜色
        mChart.setBackgroundColor(Color.WHITE);
        //一条数据线
        LineData data = new LineData();
        // 数据显示的颜色
        data.setValueTextColor(Color.BLACK);
        LineDataSet dataSet = createLineDataSet();
        data.addDataSet(dataSet);

        for (int i = 0; i < myDataBeanArrayList.size(); i++) {
            data.addXValue(String.valueOf(i));
            String value = myDataBeanArrayList.get(i).getCurrentMeasure();
            float yValue = Float.parseFloat(value);
            //System.out.println("yValue:" + yValue);
            // float m= Float.parseFloat("30.00");
            Entry entry = new Entry(yValue, i);
            dataSet.addEntry(entry);

        }
        mChart.setData(data);
        // 图表的注解(只有当数据集存在时候才生效)
        Legend l = mChart.getLegend();
        // 可以修改图表注解部分的位置
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        // 线性，也可是圆
        l.setForm(Legend.LegendForm.LINE);
        // 颜色
        l.setTextColor(Color.BLACK);
        // x坐标轴
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        // 几个x坐标轴之间才绘制？
        xl.setSpaceBetweenLabels(5);
        // 如果false，那么x坐标轴将不可见
        xl.setEnabled(true);
        // 将X坐标轴放置在底部，默认是在顶部。
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 图表左边的y坐标轴线
        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setTextColor(Color.BLACK);
        // 最大值
        leftAxis.setAxisMaxValue(500f);
        // 最小值
        leftAxis.setAxisMinValue(0f);
        // 不一定要从0开始
        leftAxis.setStartAtZero(true);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = mChart.getAxisRight();
        // 不显示图表的右边y坐标轴线
        rightAxis.setEnabled(false);
        // 初始化数据集，添加一条统计折线，可以简单的理解是初始化y坐标轴线上点的表征
        mChart.setVisibleXRangeMaximum(5);
        mChart.setDrawMarkerViews(true);
        mChart.setMarkerView(myCustomMarkerView);
    }

    private LineDataSet createLineDataSet() {
        LineDataSet set = new LineDataSet(null, "Delay Value (ms)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(ColorTemplate.getHoloBlue());

        set.setCircleColor(Color.BLACK);
        set.setLineWidth(8f);
        set.setCircleSize(5f);
        set.setFillAlpha(128);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.RED);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        return set;


    }
}
