package com.tum.orange.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tum.orange.MyCustomView.MyCustomMarkerView;
import com.tum.orange.constants.Constant;
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
    private TextView myText_1;
    private TextView myText_2;
    private TextView myText_3;
    private TextView myText_4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_data_chartdisplay, container, false);
            myText_1 = (TextView) view.findViewById(R.id.minValue_point);
            myText_1.setText(Constant.CLEAR_TEXT);
            myText_2 = (TextView) view.findViewById(R.id.meanValue_point);
            myText_2.setText(Constant.CLEAR_TEXT);
            myText_3 = (TextView) view.findViewById(R.id.maxValue_point);
            myText_3.setText(Constant.CLEAR_TEXT);
            myText_4 = (TextView) view.findViewById(R.id.Std_Dev_point);
            myText_4.setText(Constant.CLEAR_TEXT);


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
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        LineData data = new LineData();

        data.setValueTextColor(Color.BLACK);
        LineDataSet dataSet = createLineDataSet();
        data.addDataSet(dataSet);

        for (int i = 0; i < myDataBeanArrayList.size(); i++) {
            data.addXValue(String.valueOf(i));
            String value = myDataBeanArrayList.get(i).getCurrentMeasure();
            float yValue = Float.parseFloat(value);
            Entry entry = new Entry(yValue, i);
            dataSet.addEntry(entry);

        }
        mChart.setData(data);
        Legend l = mChart.getLegend();


        l.setForm(Legend.LegendForm.LINE);

        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);

        xl.setSpaceBetweenLabels(5);

        xl.setEnabled(true);

        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setTextColor(Color.BLACK);

        leftAxis.setAxisMaxValue(500f);

        leftAxis.setAxisMinValue(0f);

        leftAxis.setStartAtZero(true);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        mChart.setVisibleXRangeMaximum(5);
        mChart.setDrawMarkerViews(true);
        mChart.setMarkerView(myCustomMarkerView);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                int index=e.getXIndex();
                myText_1.setText( myDataBeanArrayList.get(index).getMin());
                myText_2.setText( myDataBeanArrayList.get(index).getMean());
                myText_3.setText( myDataBeanArrayList.get(index).getMax());
                myText_4.setText( myDataBeanArrayList.get(index).getStd_Dev());

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private LineDataSet createLineDataSet() {
        LineDataSet set = new LineDataSet(null, "Delay Value (ms)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

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
