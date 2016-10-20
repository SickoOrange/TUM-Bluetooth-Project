package com.tum.orange.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.mingle.widget.ShapeLoadingDialog;
import com.tum.orange.adapter.MyExpandableAdapter;
import com.tum.orange.tum_lmt.MainActivity;
import com.tum.orange.tum_lmt.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * show the device list in the fragment
 * Created by Orange on 20.10.2016.
 */

public class Fragment_DeviceList extends Fragment implements ExpandableListView
        .OnChildClickListener {
    //20:16:04:11:03:12  HC-05
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1000;
    private Toolbar my_toolbar_in_devicelist;
    private ActionBar actionBar;
    private ExpandableListView deviceListView;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> bondedDevices = new HashSet<BluetoothDevice>();
    private Set<BluetoothDevice> discoverDevices = new HashSet<BluetoothDevice>();
    //  private List<Set<BluetoothDevice>> list;
    private Map<String, Set<BluetoothDevice>> deviceMap = new HashMap<String,
            Set<BluetoothDevice>>();
    private String[] groupName = new String[]{"Paired Devices", "Other Available Devices"};
    private MyExpandableAdapter myExpandableAdapter;
    private ShapeLoadingDialog loadingDialog;


    private View mContainerView;
    private MainActivity mActivity;
    private FloatingActionButton floatingActionButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        loadingDialog = new ShapeLoadingDialog(mActivity);
        loadingDialog.setLoadingText("searching a new Device...");
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        mContainerView = inflater.inflate(R.layout.fragment_device_list, null);
        /**
         * MVC Module to design the expandable device ListView
         * M Module
         * V View
         * C Controller
         */
        initView();
        initData();
        initController();
        return mContainerView;
    }

    private void initView() {
        deviceListView = (ExpandableListView) mContainerView.findViewById(R.id
                .expandable_device_ListView);
        floatingActionButton = (FloatingActionButton) mContainerView.findViewById(R.id
                .device_search);
        deviceListView.setOnChildClickListener(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start to discover the new Device
                Discovering();
            }
        });
    }

    private void initData() {

        Querying();

        deviceMap.put("Paired Devices", bondedDevices);
        deviceMap.put("Other Available Devices", discoverDevices);
    }

    private void Querying() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bondedDevices = bluetoothAdapter.getBondedDevices();
    }

    private void initController() {
        myExpandableAdapter = new MyExpandableAdapter(mActivity, groupName, deviceMap);
        deviceListView.setAdapter(myExpandableAdapter);
        deviceListView.expandGroup(0);
        deviceListView.expandGroup(1);
    }

    /**
     * Start the remote device discovery process.
     * The discovery process usually involves an inquiry scan of about 12 seconds,
     * followed by a page scan of each new device to retrieve its Bluetooth name
     * asynchronous call, return immediately
     */
    private void Discovering() {
        bluetoothAdapter.cancelDiscovery();
        //Determine whether you have been granted a particular permission.
        int hasPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission
                .ACCESS_COARSE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            discoverDevices.clear();
            myExpandableAdapter.notifyDataSetChanged();
            bluetoothAdapter.startDiscovery();
            return;
        }

        //Requests permissions to be granted to this application.
        //in order to support API 23 Android 6.0; We must do this
        ActivityCompat.requestPermissions(mActivity,
                new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSIONS);
    }

    /**
     * Callback for the result from requesting permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    bluetoothAdapter.startDiscovery();
                } else {
                    Toast.makeText(mActivity,
                            "no permissions",
                            Toast.LENGTH_LONG).show();
                    bluetoothAdapter.cancelDiscovery();
                }
                return;
            }
        }
    }


    @Override
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(discoverReceiver, intentFilter);
        //Update for the paired device
        bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        myExpandableAdapter.notifyDataSetChanged();
        super.onResume();
    }

    /**
     * must register a BroadcastReceiver for the ACTION_FOUND Intent in order to receive information
     * about each device discovered. For each device, the system will broadcast the ACTION_FOUND
     * Intent.
     * This Intent carries the extra fields EXTRA_DEVICE and EXTRA_CLASS,
     * containing a BluetoothDevice and a BluetoothClass,
     */
    public final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Add a decision
                //Prevent duplicate devices from the new each search
                discoverDevices.add(device);

                if (device.getName() == null) {
                    Toast.makeText(mActivity, "A Device has been found: " + device
                            .getAddress(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "A Device has been found: " + device
                            .getName(), Toast.LENGTH_SHORT).show();
                }
                myExpandableAdapter.notifyDataSetChanged();
                // deviceListView.setAdapter(myExpandableAdapter);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                System.out.println("Discovery started");
                loadingDialog.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("Discovery finished");
                loadingDialog.dismiss();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        bluetoothAdapter.cancelDiscovery();
        mActivity.unregisterReceiver(discoverReceiver);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int
            childPosition, long id) {
        Object[] objects = deviceMap.get(groupName[groupPosition]).toArray();
        BluetoothDevice object = (BluetoothDevice) objects[childPosition];
        System.out.println(object.getName() + "..." + object.getAddress());
        mActivity.connectToRemoteDevice(object);
        return true;
    }
}
