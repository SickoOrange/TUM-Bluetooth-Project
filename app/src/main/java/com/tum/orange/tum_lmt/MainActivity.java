package com.tum.orange.tum_lmt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tum.orange.bluetoothmanagement.ConnectThread;
import com.tum.orange.bluetoothmanagement.ConnectedThread;
import com.tum.orange.constants.Constant;
import com.tum.orange.fragment.Fragment_Data;
import com.tum.orange.fragment.MyPreferenceFragment;

import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {

    private static final int CONNECT_DIS = 1007;

    String app_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private FragmentTabHost mTabHost;
    private Toolbar my_toolbar;
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
    private SharedPreferences auto_connect_sharedPreference;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment_Data fragment_data;
    private MyPreferenceFragment myPreferenceFragment;
    private ActionBarDrawerToggle toggle;

    public void setHandler(Handler handler) {
        fragment_data_handler = handler;
        getSharedPreferences("com.tum.orange.tum_lmt_preferences", MODE_PRIVATE).edit()
                .putBoolean("emulator_mode_preference", false).apply();
        auto_connect_sharedPreference = getSharedPreferences("com.tum.orange" +
                ".tum_lmt_preferences", MODE_PRIVATE);
        boolean auto_connect_mode = auto_connect_sharedPreference.getBoolean
                ("auto_connect_preference", false);
        String lastDeviceMAC = auto_connect_sharedPreference.getString("last_connect_device", null);
        System.out.println("com.tum.orange.tum_lmt_preferences:" + auto_connect_mode);
        if (auto_connect_mode) {
            if (lastDeviceMAC != null) {
                Toast.makeText(this, "the APP is auto connecting the bluetooth device now!",
                        Toast.LENGTH_SHORT).show();
                autoConnect(lastDeviceMAC);
            } else {
                Toast.makeText(this, "don't find any device that before connect successfully",
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Auto Connect Mode is disable!", Toast.LENGTH_SHORT).show();
        }
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .READ_EXTERNAL_STORAGE}, 1);
            }
        }


    }

    /**
     * auto connect the last bluetooth device
     * if last bluetooth device connect successfully
     */
    private void autoConnect(String MAC) {
        if (mConnectThread != null) {
            mConnectThread.cancelConnect();
            mConnectThread = null;
        }
        mConnectThread = new ConnectThread(MainActivity.this, MAC, null, app_UUID,
                fragment_data_handler);
        mConnectThread.start();

    }

    private void init_view() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        fragment_data = new Fragment_Data();
        myPreferenceFragment = new MyPreferenceFragment();
        //my_toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.menu_overflow));
        my_toolbar.setNavigationIcon(R.drawable.menu_overflow);
        setSupportActionBar(my_toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("No Connection");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toggle = new ActionBarDrawerToggle(this, drawerLayout, my_toolbar,
                R.string
                        .open, R.string.close);
        drawerLayout.addDrawerListener(toggle);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment_data)
                .commit();
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.nav_live_measurement:
                        if (myPreferenceFragment.isAdded()) {
                            getSupportFragmentManager().beginTransaction().hide
                                    (myPreferenceFragment)
                                    .show(fragment_data).commit();
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_data_representation:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_device:
                        System.out.println("start device");
                        startActivityForResult(new Intent(MainActivity.this, DeviceListActivity
                                        .class),
                                Constant.REQUEST_DEVICE_INFO);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_setting:
                        if (!myPreferenceFragment.isAdded()) {
                            getSupportFragmentManager().beginTransaction().hide(fragment_data)
                                    .add(R.id.frame_content, myPreferenceFragment).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().hide(fragment_data)
                                    .show(myPreferenceFragment).commit();
                        }
                        drawerLayout.closeDrawers();
                        break;

                }
                return false;
            }
        });


       /* Resources resource = getBaseContext().getResources();
        ColorStateList csl = resource.getColorStateList(R.color.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);*/


       /* mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线
        for (int i = 0; i < mImages.length; i++) {

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mFragmentTags[i]).setIndicator
                    (getImageView(i));

            mTabHost.addTab(tabSpec, aClass[i], null);

            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.color_tabHost_bkg);
        }*/
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
                startActivityForResult(new Intent(getApplicationContext(), DeviceListActivity
                        .class), Constant.REQUEST_DEVICE_INFO);
                return true;
            case R.id.start:
                fragment_data_handler.obtainMessage(Constant.BUTTON_START).sendToTarget();
                return true;
            case R.id.stop:

                fragment_data_handler.obtainMessage(Constant.BUTTON_STOP).sendToTarget();
                isStarted = false;
                return true;
            case R.id.reset:
                fragment_data_handler.obtainMessage(Constant.BUTTON_RESET).sendToTarget();
                isStarted = false;
                return true;
            case R.id.lower_Sens:
                fragment_data_handler.obtainMessage(Constant.BUTTON_LOWER_SENS).sendToTarget();
                return true;
            case R.id.higher_Sens:
                fragment_data_handler.obtainMessage(Constant.BUTTON_HIGHER_SENS).sendToTarget();
                return true;
            case R.id.save_Msmt:
                fragment_data_handler.obtainMessage(Constant.BUTTON_SAVE_MSMT).sendToTarget();
                return true;
            case R.id.clear:
                //clear the chart
                fragment_data_handler.obtainMessage(Constant.BUTTON_CLEAR).sendToTarget();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * set the icon in the overflow
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
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for " +
                            "overflow menu", e);
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
                System.out.println("main activity::" + resultDevice.getName() + ": " +
                        resultDevice.getAddress());
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
        mConnectThread = new ConnectThread(MainActivity.this, null, resultDevice, app_UUID,
                fragment_data_handler);
        mConnectThread.start();


    }

    @Override
    public void onBackPressed() {
        showSnackBar(my_toolbar, "Do you want to finish APP?");
    }

    private void showSnackBar(View view, String content) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT).setAction("yes", new
                    View.OnClickListener() {
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
        unregisterReceiver(connectStateReceiver);
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
