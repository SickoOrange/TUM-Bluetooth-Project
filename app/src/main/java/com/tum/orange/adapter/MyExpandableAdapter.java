package com.tum.orange.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


import com.tum.orange.tum_lmt.R;

import java.util.Map;
import java.util.Set;

/**
 * Created by Orange on 2016/8/23.
 */
public class MyExpandableAdapter extends BaseExpandableListAdapter {
    private Map<String, Set<BluetoothDevice>> deviceMap;
    private String[] groupName;
    private Context mContext;


    public MyExpandableAdapter(Context mContext, String[] groupName, Map<String, Set<BluetoothDevice>> deviceMap) {
        this.deviceMap = deviceMap;
        this.groupName = groupName;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return groupName.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return deviceMap.get(groupName[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupName[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (deviceMap.get(groupName[groupPosition]).size() != 0) {

            Object[] objects = deviceMap.get(groupName[groupPosition]).toArray();

            return objects[childPosition];
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View mView;
        if (convertView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.group_expandable_list, null);
            TextView group_tv = (TextView) mView.findViewById(R.id.group_tv);
            group_tv.setText(getGroup(groupPosition).toString());
            return mView;
        }
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View cView;

        //here don't use convertView for reuse the child View
        //we need to draw a new childView every time for Data Update!!
        cView = LayoutInflater.from(mContext).inflate(R.layout.child_expandable_list, null);
        TextView device_name = (TextView) cView.findViewById(R.id.device_name);
        TextView device_mac = (TextView) cView.findViewById(R.id.device_mac);

        BluetoothDevice device_object = (BluetoothDevice) getChild(groupPosition, childPosition);
        System.out.println(device_object.getName() + ":::" + device_object.getAddress());
        device_name.setText(device_object.getName());
        device_mac.setText(device_object.getAddress());
        return cView;


        //return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
