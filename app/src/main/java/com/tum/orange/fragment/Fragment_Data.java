package com.tum.orange.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tum.orange.tum_lmt.MainActivity;
import com.tum.orange.tum_lmt.R;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Fragment Data
 * Created by Orange on 2016/8/22.
 */
public class Fragment_Data extends Fragment {
    private View mViewContent; // 缓存视图内容
    private TextView tv_data;
    private String msgfromThread;
    private OutputStream mOutputStream;
    private LineChart mChart;
    private Button btn;
    private BluetoothDevice device;
    /**
     * 定义一个handler对象 用于与activity进行交互 此时
     * activity拿到handler的对象 可以发送消息给fragment_data
     */
    public Handler fragment_data_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                System.out.println("得到消息");
            } else if (msg.what == 2) {
                System.out.println("得到消息+22");
                if (mOutputStream != null) {
                    try {
                        mOutputStream.write("hello world".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (msg.what == 10) {
                msgfromThread = (String) msg.obj;
                System.out.println("recieveMessage from Thread:" + msgfromThread);
                //tv_data.setText("recieveMessage from Thread:" + msgfromThread);
                float f = Float.parseFloat(msgfromThread);
                addEntry(f);

            } else if (msg.what == 11) {
                mOutputStream = (OutputStream) msg.obj;
                System.out.println("从线程中拿到了一个Outputstream:" + mOutputStream.hashCode());
            } else if (msg.what == 1005) {
                device = (BluetoothDevice) msg.obj;
                mActivity.getSupportActionBar().setTitle("Connected to " + device.getName());
            } else if (msg.what == 1006 || msg.what == 1007) {
                mActivity.getSupportActionBar().setTitle("Disconnect");
            } else if (msg.what == 1008) {
                mActivity.getSupportActionBar().setTitle("connecting...");
            }
        }
    };
    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.fragment_data, null);
            mChart = (LineChart) mViewContent.findViewById(R.id.mChart);
            btn = (Button) mViewContent.findViewById(R.id.add);
            mChart.setDescription("Delay");
            mChart.setNoDataTextDescription("暂时尚无数据");

            mChart.setTouchEnabled(true);

            // 可拖曳
            mChart.setDragEnabled(true);

            // 可缩放
            mChart.setScaleEnabled(true);
            mChart.setDrawGridBackground(false);

            mChart.setPinchZoom(true);

            // 设置图表的背景颜色
            mChart.setBackgroundColor(Color.WHITE);

            LineData data = new LineData();

            // 数据显示的颜色
            data.setValueTextColor(Color.BLACK);

            // 先增加一个空的数据，随后往里面动态添加
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
            leftAxis.setAxisMaxValue(300f);

            // 最小值
            leftAxis.setAxisMinValue(0f);

            // 不一定要从0开始
            leftAxis.setStartAtZero(true);

            leftAxis.setDrawGridLines(true);

            YAxis rightAxis = mChart.getAxisRight();
            // 不显示图表的右边y坐标轴线
            rightAxis.setEnabled(false);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }
        //tv_data = (TextView) mViewContent.findViewById(R.id.tv_data);
        Log.e("main", "onCreateView1");
        return mViewContent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 显示Fragment的Tag信息s
        //tv_data.setVisibility(View.VISIBLE);
        //tv_data.setText("Fragment:" + getTag());
        System.out.println(getTag());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("main", "onDestroyView1");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("main", "OnAttach1");
        mActivity = (MainActivity) context;
        mActivity.setHandler(fragment_data_handler);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("main", "onCreate1");

    }

    private void addEntry(float delay) {
        LineData data = mChart.getData();

        // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。
        // 本例只有一个，那么就是第0条折线
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

        // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过
        // 此处代码。
        if (set == null) {
            set = createLineDataSet();
            data.addDataSet(set);
        }

        // 先添加一个x坐标轴的值
        // 因为是从0开始，data.getXValCount()每次返回的总是全部x坐标轴上总数量，所以不必多此一举的加1
        System.out.println(data.getXValCount());
        data.addXValue((data.getXValCount()) + "");

        // 生成随机测试数
        float f = (float) ((Math.random()) * 20 + 50);

        // set.getEntryCount()获得的是所有统计图表上的数据点总量，
        // 如从0开始一样的数组下标，那么不必多次一举的加1
        System.out.println(set.getEntryCount());
        Entry entry = new Entry(delay, set.getEntryCount());

        // 往linedata里面添加点。注意：addentry的第二个参数即代表折线的下标索引。
        // 因为本例只有一个统计折线，那么就是第一个，其下标为0.
        // 如果同一张统计图表中存在若干条统计折线，那么必须分清是针对哪一条（依据下标索引）统计折线添加。
        data.addEntry(entry, 0);

        // 像ListView那样的通知数据更新
        mChart.notifyDataSetChanged();

        // 当前统计图表中最多在x轴坐标线上显示的总量
        mChart.setVisibleXRangeMaximum(5);

        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // 将坐标移动到最新
        // 此代码将刷新图表的绘图
        mChart.moveViewToX(data.getXValCount() - 5);

        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }

    // 初始化数据集，添加一条统计折线，可以简单的理解是初始化y坐标轴线上点的表征
    private LineDataSet createLineDataSet() {

        LineDataSet set = new LineDataSet(null, "Delay Value");
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

    @Override
    public void onResume() {
        super.onResume();
        Log.e("main", "onResume1");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOutputStream.write("1".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("main", "onPause1");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("main", "onDestroy1");
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("main", "onDetach1");
    }

}

