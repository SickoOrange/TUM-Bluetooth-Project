package com.tum.orange.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tum.orange.constants.Constant;
import com.tum.orange.javabean.MyDataBean;
import com.tum.orange.tum_lmt.DataResultShowActivity;
import com.tum.orange.tum_lmt.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Data Show with Card View in Material Design
 * Created by Orange on 29.09.2016.
 */

public class Fragment_Data_RecyclerView extends Fragment {

    private View view;
    private int Tag;
    private DataResultShowActivity mActivity;
    private String fileName;
    ArrayList<MyDataBean> mDataList;
    private int mDataCount;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        System.out.println("OnCreateView" + Tag);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_data_recyclerview, container, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            initRecyclerView(recyclerView);
            mDataCount = mDataList.size();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    /**
     * init RecyclerView
     *
     * @param recyclerView
     */
    private void initRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(new MyRecyclerViewAdapter(mActivity));
    }

    /**
     * init the adapter for RecyclerView
     */
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter
            .MyViewHolder> {
        private LayoutInflater inflater;

        public MyRecyclerViewAdapter(Context mContext) {
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // view = inflater.inflate(R.layout.recyclerviewholder, parent, false);
            View view = inflater.inflate(R.layout.data_cardview, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            System.out.println("Position......." + position);
            MyDataBean dataBean = mDataList.get(position);
            System.out.println(dataBean.getCurrentMeasure());
            holder.viewHolder_Text_1.setText(String.valueOf(dataBean.getIndex()));
            holder.viewHolder_Text_2.setText("Current: " + String.valueOf(dataBean
                    .getCurrentMeasure()) + "ms");
            holder.viewHolder_Text_3.setText("Min:" + String.valueOf(dataBean.getMin()) + "ms");
            holder.viewHolder_Text_4.setText("Max:" + String.valueOf(dataBean.getMax()) + "ms");
            holder.viewHolder_Text_5.setText("Mean:" + String.valueOf(dataBean.getMean()) + "ms");
            holder.viewHolder_Text_6.setText("Std_Dev:" + String.valueOf(dataBean.getStd_Dev()) +
                    "ms");
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView viewHolder_Text_1;
            TextView viewHolder_Text_2;
            TextView viewHolder_Text_3;
            TextView viewHolder_Text_4;
            TextView viewHolder_Text_5;
            TextView viewHolder_Text_6;

            MyViewHolder(View view) {
                super(view);
                viewHolder_Text_1 = (TextView) view.findViewById(R.id.viewHolder_Text_1);
                viewHolder_Text_2 = (TextView) view.findViewById(R.id.viewHolder_Text_2);
                viewHolder_Text_3 = (TextView) view.findViewById(R.id.viewHolder_Text_3);
                viewHolder_Text_4 = (TextView) view.findViewById(R.id.viewHolder_Text_4);
                viewHolder_Text_5 = (TextView) view.findViewById(R.id.viewHolder_Text_5);
                viewHolder_Text_6 = (TextView) view.findViewById(R.id.viewHolder_Text_6);
            }
        }
    }

    /**
     * Called when a fragment is first attached to its context.
     * antiSerializable and give fragment the activity context
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (DataResultShowActivity) context;
        // mDataList = antiSerializable(mActivity);
        mDataList = mActivity.mDataList;
    }


    /**
     * this method return a dataList from serializable data
     *
     * @param mActivity
     * @return dataList
     */
    private ArrayList<MyDataBean> antiSerializable(DataResultShowActivity mActivity) {
        fileName = mActivity.getFileName();
        Log.e("Tag", fileName);
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
