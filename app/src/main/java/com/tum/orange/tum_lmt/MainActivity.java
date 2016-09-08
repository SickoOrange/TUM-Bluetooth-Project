package com.tum.orange.tum_lmt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.tum.orange.bluetoothmanagement.ConnectThread;
import com.tum.orange.bluetoothmanagement.ConnectedThread;
import com.tum.orange.constants.ConstansForBluetoothService;
import com.tum.orange.fragment.Fragment_Data;
import com.tum.orange.fragment.MyPreferenceFragment;

import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {

    private static final int CONNECT_DIS = 1007;

    String app_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private FragmentTabHost mTabHost;
    private Toolbar my_toolbar;
    private ActionBar actionBar;
    private Snackbar snackbar;
    public Handler fragment_data_handler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mImages[] = {
            R.drawable.tab_center,
            R.drawable.tab_counter,
    };
    private Class[] aClass = {Fragment_Data.class, MyPreferenceFragment.class};

    // Fragment Tag
    private String mFragmentTags[] = {
            "0",
            "1",
    };
    private BluetoothDevice resultDevice;
    private BluetoothAdapter adapter;
    private BluetoothSocket btSocket;
    private Boolean isStarted = false;

    public void setHandler(Handler handler) {
        fragment_data_handler = handler;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_view();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        registerReceiver(connectStateReceiver, filter);

        // ---- check permissions android 6.0 update ----
        //for write external Store
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }


    }

    private void init_view() {
        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        my_toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.menu_overflow));
        setSupportActionBar(my_toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("No Connection");


        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线
        for (int i = 0; i < mImages.length; i++) {
            // Tab按钮添加文字和图片
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mFragmentTags[i]).setIndicator(getImageView(i));
            // 添加Fragment
            mTabHost.addTab(tabSpec, aClass[i], null);
            // 设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.color_tabHost_bkg);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (Integer.parseInt(tabId) == 1) {
                    System.out.println("1被按下了");
                    Message msg = new Message();
                    msg.what = 1;
                    fragment_data_handler.sendMessage(msg);
                }
            }
        });
    }

    private View getImageView(int index) {
        View view = getLayoutInflater().inflate(R.layout.view_tab_indicator, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_iv_image);
        imageView.setImageResource(mImages[index]);
        return view;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_device:
                startActivityForResult(new Intent(getApplicationContext(), DeviceListActivity.class), ConstansForBluetoothService.REQUEST_DEVICE_INFO);
                return true;
            case R.id.start:
                fragment_data_handler.obtainMessage(ConstansForBluetoothService.BUTTON_START).sendToTarget();
                return true;
            case R.id.stop:

                fragment_data_handler.obtainMessage(ConstansForBluetoothService.BUTTON_STOP).sendToTarget();
                isStarted = false;
                return true;
            case R.id.reset:
                fragment_data_handler.obtainMessage(ConstansForBluetoothService.BUTTON_RESET).sendToTarget();
                isStarted = false;
                return true;
            case R.id.lower_Sens:
                fragment_data_handler.obtainMessage(ConstansForBluetoothService.BUTTON_LOWER_SENS).sendToTarget();
                return true;
            case R.id.higher_Sens:
                fragment_data_handler.obtainMessage(ConstansForBluetoothService.BUTTON_HIGHER_SENS).sendToTarget();
                return true;
            case R.id.save_Msmt:
                fragment_data_handler.obtainMessage(ConstansForBluetoothService.BUTTON_SAVE_MSMT).sendToTarget();
                return true;
            case R.id.clear:
                //clear the chart
                fragment_data_handler.obtainMessage(ConstansForBluetoothService.BUTTON_CLEAR).sendToTarget();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * set the icon in the overfloe
     *
     * @param menu
     * @return
     */
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * Dispatch incoming device result to the correct fragment
     * connect the remote Bluetooth device with the incoming device result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle bundle = data.getExtras();
                resultDevice = bundle.getParcelable("DEVICE_INFO");
                System.out.println("main activity::" + resultDevice.getName() + ": " + resultDevice.getAddress());
                connectToRemoteDevice(resultDevice);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToRemoteDevice(BluetoothDevice resultDevice) {
        if (mConnectThread != null) {
            mConnectThread.cancelConnect();
            mConnectThread = null;
        }
        mConnectThread = new ConnectThread(MainActivity.this, resultDevice, app_UUID, fragment_data_handler);
        mConnectThread.start();


    }

    @Override
    public void onBackPressed() {
        showSnackBar(my_toolbar, "Do you want to finish APP?");
    }

    private void showSnackBar(View view, String content) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT).setAction("yes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                }
            });
        }
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        System.out.println("被销毁了啦");
        unregisterReceiver(connectStateReceiver);
        //优雅的关闭线程连接
        if (mConnectThread != null) {
            mConnectThread.cancelConnect();
        }
        super.onDestroy();
    }

    public final BroadcastReceiver connectStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    System.out.println("ACTION_ACL_CONNECTED");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    System.out.println("ACTION_ACL_DISCONNECTED");
                    fragment_data_handler.obtainMessage(CONNECT_DIS).sendToTarget();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                    System.out.println("ACTION_ACL_DISCONNECT_REQUESTED");
                    break;
                default:
                    break;

            }
        }
    };

}
