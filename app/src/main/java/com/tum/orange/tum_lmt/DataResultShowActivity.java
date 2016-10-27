package com.tum.orange.tum_lmt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tum.orange.constants.Constant;
import com.tum.orange.fragment.Fragment_Data_BarChart;
import com.tum.orange.fragment.Fragment_Data_ChartDisplay;
import com.tum.orange.fragment.Fragment_Data_RecyclerView;
import com.tum.orange.javabean.MyDataBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DataResultShowActivity extends AppCompatActivity implements TabLayout
        .OnTabSelectedListener {

    private TabLayout tabLayout;
    public ArrayList<MyDataBean> mDataList;


    private Fragment_Data_RecyclerView fragment_data_recyclerView;
    private Fragment_Data_ChartDisplay fragment_data_chartDisplay;
    private File file;
    private BufferedWriter bw;
    private ActionBar supportActionBar;
    private Fragment_Data_BarChart fragment_data_barChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_result_show);
        mDataList = antiSerializable();
        initView();
        String path = Environment.getExternalStorageDirectory().toString() +
                Constant.filePath;
        file = new File(path);
        //file = new File(path, getFileName() );
        if (!file.exists()) {
            file.mkdir();
        }

    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.dataResult_toolBar);
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("Data Show");
        }
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("Data"), true);
        tabLayout.addTab(tabLayout.newTab().setText("LineChart"));
        tabLayout.addTab(tabLayout.newTab().setText("BarChart"));
        initDefaultFragment();
        tabLayout.setOnTabSelectedListener(this);
    }

    private void initDefaultFragment() {
        fragment_data_recyclerView = new Fragment_Data_RecyclerView();
        fragment_data_chartDisplay = new Fragment_Data_ChartDisplay();
        fragment_data_barChart = new Fragment_Data_BarChart();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                fragment_data_recyclerView)
                .commit();
    }

    public String getFileName() {
        Intent intent = getIntent();
        return intent.getExtras().getString("FileName");
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                System.out.println("this is first tab");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        fragment_data_recyclerView).commit();
                break;
            case 1:
                System.out.println("this is second tab");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        fragment_data_chartDisplay).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        fragment_data_barChart).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    public ArrayList<MyDataBean> antiSerializable() {
        String fileName = getFileName();
        String Path = Environment.getExternalStorageDirectory().toString();
        File antiSerializableFile = new File(Path + Constant.serializablePath, fileName);
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(antiSerializableFile));
            ArrayList<MyDataBean> list = (ArrayList<MyDataBean>) os.readObject();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Anti Serializable Failed!");
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_datashow_storange, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveInPhone) {
            System.out.println("Save----------------");
            save();
        }
        if (item.getItemId() == R.id.share) {
            share();
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        File filePath = new File(file, getFileName());
        if (!filePath.exists()) {
            Toast.makeText(DataResultShowActivity.this, "File not exist, pls first Export on the " +
                    "Phone", Toast.LENGTH_LONG
            ).show();
            return;
        } else {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "share the measurements"));
        }

    }


    private void save() {
        File filePath = new File(file, getFileName());
        if (!filePath.exists()) {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
                        (filePath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < mDataList.size(); i++) {
                MyDataBean dataBean = mDataList.get(i);
                int index = dataBean.getIndex() + 1;
                try {
                    bw.write("Index:" + index);
                    bw.newLine();
                    bw.write("CurrentMeasure:" + dataBean.getCurrentMeasure() + "ms");
                    bw.newLine();
                    bw.write("Min Value:" + dataBean.getMin() + "ms");
                    bw.newLine();
                    bw.write("Max Value:" + dataBean.getMax() + "ms");
                    bw.newLine();
                    bw.write("Mean Value:" + dataBean.getMean() + "ms");
                    bw.newLine();
                    bw.write("Std_Dev:" + dataBean.getStd_Dev() + "ms");
                    bw.newLine();
                    bw.write("===================================================");
                    bw.newLine();
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DataResultShowActivity.this, "Export failed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            try {
                bw.close();
                Toast.makeText(DataResultShowActivity.this, "Export successfully" + "\n" + "Path:" +
                        filePath
                                .toString(), Toast
                        .LENGTH_LONG)
                        .show();
                if (supportActionBar != null) {
                    supportActionBar.setTitle("Path:/TUM_ExportFolder");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(DataResultShowActivity.this, "file exist", Toast
                    .LENGTH_LONG)
                    .show();
        }
    }
}
