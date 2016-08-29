package com.tum.orange.tum_lmt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.tum.orange.bluetoothmanagement.ConnectThread;
import com.tum.orange.bluetoothmanagement.ConnectedThread;
import com.tum.orange.fragment.Fragment_Data;
import com.tum.orange.fragment.Fragment_Setting;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_DEVICE_INFO = 1001;
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
    private Class[] aClass = {Fragment_Data.class, Fragment_Setting.class};

    // 标题
    private String mFragmentTags[] = {
            "0",
            "1",
    };
    private BluetoothDevice resultDevice;
    private BluetoothAdapter adapter;
    private BluetoothSocket btSocket;

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


    }

    private void init_view() {
        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("connecting to bluetooth...");

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_device:
                startActivityForResult(new Intent(getApplicationContext(), DeviceListActivity.class), REQUEST_DEVICE_INFO);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
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
        mConnectThread.cancelConnect();
        super.onDestroy();
    }
}
