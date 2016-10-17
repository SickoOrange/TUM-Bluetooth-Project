package com.tum.orange.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tum.orange.MyCustomView.ConnectResultView;
import com.tum.orange.constants.Constant;
import com.tum.orange.javabean.MyDataBean;
import com.tum.orange.tum_lmt.MainActivity;
import com.tum.orange.tum_lmt.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Fragment Data
 * Created by Orange on 2016/8/22.
 */
public class Fragment_Data extends Fragment {
    private View mViewContent; // 缓存视图内容
    private TextView tv_data;
    private String msgFromThread;
    private OutputStream mOutputStream;
    private LineChart mChart;
    private BluetoothDevice device;
    private float yMaxValue;
    private float yMinValue;
    private int xCount;
    private float Mean;
    private String Mean_String;

    private View connectingView;
    private View resultView;
    private ConnectResultView connectResultView;
    private AlertDialog initDialogOnState;
    private AlertDialog initDialogOnResult;
    private AlertDialog connectResultDialog;

    ArrayList<String> yMaxValue_array = new ArrayList<String>();
    ArrayList<String> yMinValue_array = new ArrayList<String>();
    ArrayList<String> Mean_String_array = new ArrayList<String>();
    ArrayList<String> std_String_array = new ArrayList<String>();
    /**
     * 定义一个handler对象 用于与activity进行交互 此时
     * activity拿到handler的对象 可以发送消息给fragment_data
     */
    public Handler fragment_data_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    System.out.println("得到消息");
                    break;
                case 2:
                    System.out.println("得到消息+22");
                    if (mOutputStream != null) {
                        try {
                            mOutputStream.write("hello world".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Constant.MESSAGE_READ:
                    msgFromThread = (String) msg.obj;
                    //tv_data.setText("recieveMessage from Thread:" + msgFromThread);
                    if (msgFromThread == null) {
                        System.out.println("msgFromThread is null, Exception!");
                        return;
                    }
                    float f = Float.parseFloat(msgFromThread);
                    //float f = Float.valueOf(msgFromThread);
                    // System.out.println(msgFromThread);
                    addEntry(f);
            /*        float f = 0.00f;
                    try {
                        float temp_f = Float.parseFloat(msgFromThread);
                        f = temp_f;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addEntry(f);*/
                    break;
                case Constant.MESSAGE_WRITE_STREAM:
                    mOutputStream = (OutputStream) msg.obj;
                    System.out.println("从线程中拿到了一个Outputstream:" + mOutputStream.hashCode());
                    break;
                case 1005:
                    device = (BluetoothDevice) msg.obj;
                    initDialogOnState.dismiss();
                    connectResultView.setSucceed(true);
                    initDialogOnResult.show();
                    connectResultView.setStart(true);
                    if (actionBar != null) {
                        actionBar.setTitle("Connected to " + device.getName());
                    }
                    break;
                case 1006:
                    initDialogOnState.dismiss();
                    connectResultView.setSucceed(false);
                    initDialogOnResult.show();
                    connectResultView.setStart(true);
                    if (actionBar != null) {
                        actionBar.setTitle("Connect failed");
                    }
                    break;
                case 1007:
                    //if (initDialogOnResult.isShowing()) {
                    //   initDialogOnResult.dismiss();
                    //}
                    //connectResultView.setSucceed(false);
                    //initDialogOnResult.show();
                    //connectResultView.setStart(true);
                    if (actionBar != null) {
                        actionBar.setTitle("No Connection");
                    }
                    break;
                case 1008:
                    initDialogOnState.show();
                    if (actionBar != null) {
                        actionBar.setTitle("connecting...");
                    }

                    break;
                case Constant.BUTTON_START:
                    if (mOutputStream == null) {
                        return;
                    }
                    writeMessage(Constant.START);
                    Toast.makeText(mActivity, "Start Measure", Toast
                            .LENGTH_SHORT).show();
                    break;
                case Constant.BUTTON_STOP:
                    if (mOutputStream == null) {
                        return;
                    }
                    writeMessage(Constant.STOP);
                    Toast.makeText(mActivity, "Stop Measure", Toast
                            .LENGTH_SHORT).show();
                    break;
                case Constant.BUTTON_RESET:
                    if (mOutputStream == null) {
                        return;
                    }
                    writeMessage(Constant.STOP);
                    Toast.makeText(mActivity, "Reset Measure", Toast
                            .LENGTH_SHORT).show();
                    break;
                case Constant.BUTTON_LOWER_SENS:
                    if (mOutputStream == null) {
                        return;
                    }
                    writeMessage(Constant.LOWER_SENS);
                    Toast.makeText(mActivity, "The threshold has been " +
                            "decreased by 1", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.BUTTON_HIGHER_SENS:
                    if (mOutputStream == null) {
                        return;
                    }
                    writeMessage(Constant.HIGHER_SENS);
                    Toast.makeText(mActivity, "The threshold has been " +
                            "increased by 1", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.BUTTON_SAVE_MSMT:
                    saveData();
                    break;
                case Constant.BUTTON_CLEAR:
                    mChart.clear();
                    mChart.invalidate();
                    initChartView();

                    maxValue.setText(Constant.CLEAR_TEXT);
                    minValue.setText(Constant.CLEAR_TEXT);
                    meanValue.setText(Constant.CLEAR_TEXT);
                    std_dev.setText(Constant.CLEAR_TEXT);
                    numberValue.setText(Constant.CLEAR_TEXT);
                    break;
                default:
                    break;
            }
        }
    };
    private ActionBar actionBar;
    private File tumParcelable;
    private File writeParcelableFile;
    private ObjectOutputStream serialazableOutputStream;
    private ArrayList<MyDataBean> mSerilazibleList = new ArrayList<MyDataBean>();
    private File writeSerializableFilePath;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("main", "OnAttach1");
        mActivity = (MainActivity) context;
        mActivity.setHandler(fragment_data_handler);
        actionBar = mActivity.getSupportActionBar();
        initDialog(mActivity);

    }

    private void initDialog(MainActivity mActivity) {
        LayoutInflater layoutInflater = mActivity.getLayoutInflater();
        connectingView = layoutInflater.inflate(R.layout.connecting, null);
        resultView = layoutInflater.inflate(R.layout.connect_result, null);
        connectResultView = (ConnectResultView) resultView.findViewById(R.id.connectResultView);
        initDialogOnState = new AlertDialog.Builder(mActivity).setView(connectingView)
                .setNegativeButton("OK", null).create();
        initDialogOnResult = new AlertDialog.Builder(mActivity).setView(resultView)
                .setNegativeButton("OK", null).create();
    }

    private double std;
    private TextView std_dev;
    private String std_String;
    private File tumDirector;
    private String root_director;
    private Calendar calendar;
    private File writeMeasureFilePath;
    private BufferedWriter bw;

    private void saveData() {
        calendar = Calendar.getInstance();
        String time = calendar.getTime().toString();
        time = time.replace(" MEZ ", " ");
        System.out.println(time);
        time = time.replace(" ", "_");
        System.out.println(time);
        time = time.replace(":", "_");
        System.out.println(time);
        System.out.println("Debug" + time.substring(4));

        LineDataSet save_file_set = (LineDataSet) mChart.getLineData().getDataSetByIndex(0);
        if (save_file_set == null) {
            Toast.makeText(mActivity.getApplicationContext(), "no data in current Chart, pls " +
                    "ensure you are connect to Bluetooth Device", Toast.LENGTH_SHORT).show();
            return;
        }

        // File measureFolder = new File(Environment.getExternalStorageDirectory().toString() +
        //   Constant.filePath);
        File serializableFolder = new File(Environment.getExternalStorageDirectory().toString() +
                Constant.serializablePath);
        // if (!measureFolder.exists()) {
        //       measureFolder.mkdir();
        // }
        if (!serializableFolder.exists()) {
            serializableFolder.mkdir();
        }
        // writeMeasureFilePath = new File(measureFolder,
        //       time.substring(4) + ".txt");
        writeSerializableFilePath = new File(serializableFolder,
                time.substring(4) + ".txt");

        try {
            // bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
            //       (writeMeasureFilePath)));
            serialazableOutputStream = new ObjectOutputStream(new FileOutputStream
                    (writeSerializableFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int entryCount = save_file_set.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            float entryValue = save_file_set.getEntryForIndex(i).getVal();
            String entryValueString = Float.toString(entryValue);
            String Pattern = (i + 1) + ".Measure:" + entryValueString + "  " + "Max=" +
                    yMaxValue_array.get(i) + "  Min=" + yMinValue_array.get(i)
                    + "  Mean=" + Mean_String_array.get(i) + "  Std_Dev=" + std_String_array.get(i);

           /* try {
                bw.write(Pattern);
                bw.newLine();
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            MyDataBean dataBean = new MyDataBean();
            dataBean.setIndex(i);
            dataBean.setCurrentMeasure(entryValueString);
            dataBean.setMax(yMaxValue_array.get(i));
            dataBean.setMin(yMinValue_array.get(i));
            dataBean.setMean(Mean_String_array.get(i));
            dataBean.setStd_Dev(std_String_array.get(i));
            mSerilazibleList.add(dataBean);
        }

        try {
            serialazableOutputStream.writeObject(mSerilazibleList);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//           bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Toast.makeText(mActivity.getApplicationContext(), "save file successfully", Toast
                .LENGTH_SHORT).show();

        /**
         *序列化测试代码
         try {
         ObjectInputStream in = new ObjectInputStream(new FileInputStream
         (writeSerializableFilePath));
         ArrayList<MyDataBean> mylist = (ArrayList<MyDataBean>) in.readObject();
         for (int i = 0; i < mylist.size(); i++) {
         System.out.println("mylist" + mylist.get(i).getCurrentMeasure());
         }
         } catch (IOException e) {
         e.printStackTrace();
         } catch (ClassNotFoundException e) {
         e.printStackTrace();
         }
         */


    }


    /**
     * send a Message
     *
     * @param msg Message content
     */
    private void writeMessage(String msg) {
        if (msg != null && (msg.length() > 0)) {
            try {
                mOutputStream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            System.out.println("发送成功");
        }
    }


    private MainActivity mActivity;
    private TextView minValue;
    private TextView meanValue;
    private TextView maxValue;
    private TextView numberValue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.fragment_data, null);
            minValue = (TextView) mViewContent.findViewById(R.id.minValue);
            meanValue = (TextView) mViewContent.findViewById(R.id.meanValue);
            maxValue = (TextView) mViewContent.findViewById(R.id.maxValue);
            numberValue = (TextView) mViewContent.findViewById(R.id.numberValue);
            std_dev = (TextView) mViewContent.findViewById(R.id.Std_Dev);
            mChart = (LineChart) mViewContent.findViewById(R.id.mChart);
            initChartView();
        }
        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }
        Log.e("main", "onCreateView1");
        return mViewContent;
    }

    private void initChartView() {
        mChart.setDescription("Delay Count");
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

        // 初始化数据集，添加一条统计折线，可以简单的理解是初始化y坐标轴线上点的表征
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
        yMaxValue = data.getYMax();
        yMinValue = data.getYMin();
        xCount = data.getXValCount();
        Mean = calculateMeanValue(set);
        std = calculateSTD(set, Mean);
        DecimalFormat df = new DecimalFormat(".00");
        Mean_String = df.format(Mean);
        std_String = df.format(std);


        yMaxValue_array.add(Float.toString(yMaxValue));
        yMinValue_array.add(Float.toString(yMinValue));
        Mean_String_array.add(Mean_String);
        std_String_array.add(std_String);

        //set text value
        maxValue.setText(Float.toString(yMaxValue) + "ms");
        minValue.setText(Float.toString(yMinValue) + "ms");
        meanValue.setText(Mean_String + "ms");
        std_dev.setText(std_String + "ms");
        numberValue.setText("" + xCount);


    }

    private float calculateMeanValue(LineDataSet set) {

        int length = set.getEntryCount();
        float meanValue = 0f;
        for (int i = 0; i < length; i++) {
            Entry entry = set.getEntryForXIndex(i);
            meanValue += entry.getVal();
        }
        meanValue = meanValue / length;
        return meanValue;
    }

    private double calculateSTD(LineDataSet set, float mean) {
        int length = set.getEntryCount();
        double std_Value = 0f;
        for (int i = 0; i < length; i++) {
            Entry entry = set.getEntryForXIndex(i);
            std_Value += (entry.getVal() - mean) * (entry.getVal() - mean);
        }
        std_Value = Math.sqrt(std_Value);
        std_Value = (Math.round(std_Value * 100) / 100.0);
        return std_Value;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("main", "onResume1");
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("main", "onCreate1");

    }

}

