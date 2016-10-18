package com.tum.orange.tum_lmt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tum.orange.constants.Constant;
import com.tum.orange.fragment.Fragment_Data_ChartDisplay;
import com.tum.orange.fragment.Fragment_Data_RecyclerView;
import com.tum.orange.javabean.MyDataBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class DataShowActivity extends AppCompatActivity {

    private ArrayList<Fragment> mFragmentList;
    public ArrayList<MyDataBean> myDataBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_show);
        initView();
        myDataBeanArrayList = antiSerializable();
    }

    private void initView() {
        mFragmentList = new ArrayList<Fragment>();
        Fragment_Data_RecyclerView view1 = new Fragment_Data_RecyclerView();
        Fragment_Data_ChartDisplay view2 = new Fragment_Data_ChartDisplay();

        mFragmentList.add(view1);
        mFragmentList.add(view2);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.vp_view);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText("CardView"));
        mTabLayout.addTab(mTabLayout.newTab().setText("ChartView"));

        //mTabLayout.setupWithViewPager(mViewPager);
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    public String getFileName() {
        Intent intent = getIntent();
        return intent.getExtras().getString("FileName");
    }

    public class MyFragmentAdapter extends FragmentPagerAdapter {

        MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Hello";
        }
    }

    public ArrayList<MyDataBean> antiSerializable() {
        String fileName = getFileName();
        Log.e("Tag", fileName);
        String Path = Environment.getExternalStorageDirectory().toString();
        File antiSerializableFile = new File(Path + Constant.serializablePath, fileName);
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(antiSerializableFile));
            ArrayList<MyDataBean> list = (ArrayList<MyDataBean>) os.readObject();
            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
