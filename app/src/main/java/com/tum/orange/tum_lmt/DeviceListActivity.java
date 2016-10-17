package com.tum.orange.tum_lmt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.mingle.widget.ShapeLoadingDialog;
import com.tum.orange.adapter.MyExpandableAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {
//20:16:04:11:03:12  HC-05
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1000;
    private Toolbar my_toolbar_in_devicelist;
    private ActionBar actionBar;
    private ExpandableListView deviceListView;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> bondedDevices = new HashSet<BluetoothDevice>();
    private Set<BluetoothDevice> discoverDevices = new HashSet<BluetoothDevice>();
    //  private List<Set<BluetoothDevice>> list;
    private Map<String, Set<BluetoothDevice>> deviceMap = new HashMap<String, Set<BluetoothDevice>>();
    private String[] groupName = new String[]{"Paired Devices", "Other Available Devices"};
    private MyExpandableAdapter myExpandableAdapter;
    private ShapeLoadingDialog loadingDialog;


    //Paired Devices or Other Available Devices to Scanning for devices
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        loadingDialog = new ShapeLoadingDialog(this);
        loadingDialog.setLoadingText("searching a new Device...");
        loadingDialog.setCanceledOnTouchOutside(false);

        my_toolbar_in_devicelist = (Toolbar) findViewById(R.id.my_toolbar_in_devicelist);
        setSupportActionBar(my_toolbar_in_devicelist);

        /**
         * 重写Toolbar的NavigationButton的监听事件，覆盖默认实现，避免MainActivity重复创建
         * 重写后 按钮点击行为跟Back键行为一致
         */
        my_toolbar_in_devicelist.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select a device to connect");
        }

        /**
         * MVC Module to design the expandable device ListView
         * M Module
         * V View
         * C Controller
         */
        initView();
        initData();
        initController();
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoverReceiver, intentFilter);
        //更次重新扫描已经配对的设备
        bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        myExpandableAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void initView() {
        deviceListView = (ExpandableListView) findViewById(R.id.expandable_device_ListView);
        deviceListView.setOnChildClickListener(this);
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
        myExpandableAdapter = new MyExpandableAdapter(this, groupName, deviceMap);
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
        // bluetoothAdapter.startDiscovery();

        //Determine whether you have been granted a particular permission.
        int hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.startDiscovery();
            return;
        }

        //Requests permissions to be granted to this application.
        //in order to support API 23 Android 6.0; We must do this
        ActivityCompat.requestPermissions(DeviceListActivity.this,
                new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSIONS);

    }

    /**
     * Callback for the result from requesting permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bluetoothAdapter.startDiscovery();
                } else {
                    Toast.makeText(this,
                            "no permissions",
                            Toast.LENGTH_LONG).show();
                    bluetoothAdapter.cancelDiscovery();
                }
                return;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_in_devicelist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            //Start to discover the new Device
            Discovering();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * must register a BroadcastReceiver for the ACTION_FOUND Intent in order to receive information
     * about each device discovered. For each device, the system will broadcast the ACTION_FOUND Intent.
     * This Intent carries the extra fields EXTRA_DEVICE and EXTRA_CLASS,
     * containing a BluetoothDevice and a BluetoothClass,
     */
    public final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //添加重复判定，防止每次搜索的时候 添加重复的设备
                discoverDevices.add(device);
                /*int count=discoverDevices.size();
                if (count!=0) {
                    BluetoothDevice[] deviceList = (BluetoothDevice[]) discoverDevices.toArray();
                    for (int i = 0; i <count; i++) {
                        if (deviceList[i].getAddress() != device.getAddress()) {
                            discoverDevices.add(device);
                        }
                    }
                }*/
                //针对HC05 判空，有些设备没有Name 导致空指针异常!
                if (device.getName() == null) {
                    Toast.makeText(getApplicationContext(), "A Device has been found: " + device.getAddress(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "A Device has been found: " + device.getName(), Toast.LENGTH_SHORT).show();
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


    /**
     * Unregister a previously registered BroadcastReceiver.
     * All filters that have been registered for this BroadcastReceiver will be removed.
     */
    @Override
    protected void onPause() {
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(discoverReceiver);
        super.onPause();
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Object[] objects = deviceMap.get(groupName[groupPosition]).toArray();
        BluetoothDevice object = (BluetoothDevice) objects[childPosition];
        System.out.println(object.getName() + "..." + object.getAddress());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("DEVICE_INFO", object);
        // bundle.putStringArray("DEVICE_INFO", new String[]{object.getName(), object.getAddress()});
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
}
