package com.tum.orange.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tum.orange.constants.Constant;
import com.tum.orange.javabean.MyDataBean;
import com.tum.orange.tum_lmt.MainActivity;
import com.tum.orange.tum_lmt.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Orange on 21.10.2016.
 */

public class Fragment_Data_Representation extends Fragment {

    private View mContainerView;
    private FrameLayout dataShow_content;
    private TabLayout tabs;
    private MainActivity mActivity;

    public ArrayList<MyDataBean> mDataList;


    private Fragment_Data_RecyclerView fragment_data_recyclerView;
    private Fragment_Data_ChartDisplay fragment_data_chartDisplay;
    private File file;
    private BufferedWriter bw;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mDataList = antiSerializable();
        String path = Environment.getExternalStorageDirectory().toString() +
                Constant.filePath;
        file = new File(path);
        //file = new File(path, getFileName() );
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        mContainerView = inflater.inflate(R.layout.fragment_data_representation, null);
        dataShow_content = (FrameLayout) mContainerView.findViewById(R.id.dataShow_content);
        tabs = (TabLayout) mContainerView.findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.addTab(tabs.newTab().setText("Data"), true);
        tabs.addTab(tabs.newTab().setText("LineChart"));
        tabs.addTab(tabs.newTab().setText("BarChart"));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initDefaultFragment();
        return mContainerView;
    }

    private void initDefaultFragment() {
        fragment_data_recyclerView = new Fragment_Data_RecyclerView();
        fragment_data_chartDisplay = new Fragment_Data_ChartDisplay();

        FragmentManager supportFragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment_data_recyclerView);
        transaction.commit();
    }


    public String getFileName() {
        //Intent intent = getIntent();
        //return intent.getExtras().getString("FileName");
        return null;
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
}
