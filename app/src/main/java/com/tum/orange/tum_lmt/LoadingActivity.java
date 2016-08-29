package com.tum.orange.tum_lmt;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


public class LoadingActivity extends AppCompatActivity {
    //if we use explicit action to turn on Bluetooth, we need this Filed as Reqestcode
    //private static final int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        registerReceiver(enableReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        init_Bluetooth();
    }

    private void init_Bluetooth() {
/**
 *Turn on the local Bluetooth adapter—do not use without explicit user action to turn on Bluetooth.
 *This powers on the underlying Bluetooth hardware, and starts all Bluetooth system services.
 *Bluetooth should never be enabled without direct user consent.
 *If you want to turn on Bluetooth in order to create a wireless connection,
 *you should use the ACTION_REQUEST_ENABLE Intent,
 *which will raise a dialog that requests user permission to turn on Bluetooth.
 *like this:
 **     Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
 *      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
 *
 *Important!!
 *The enable() method is provided only for applications that include a user interface for changing system settings,
 *such as a "power manager" app
 * Or use for the TUM Project
 */
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Toast.makeText(this, "this Device dont support Bluetooth!", Toast.LENGTH_LONG).show();
        } else {
            if (!adapter.isEnabled()) {

                adapter.enable();
                //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        finish();
                    }
                }, 2500);
            }
        }
    }

    /**
     * this is a BroadcastReceiver, used to listen fpr the ACTION_STATE_CHANGED broadcast intent,
     * which the system will broadcast whenever the Bluetooth state has changed,
     * like enable,disable,turning on or turning off
     * This broadcast contains the extra fields EXTRA_STATE and EXTRA_PREVIOUS_STATE,
     * containing the new and old Bluetooth states.
     * istening for this broadcast can be useful to detect changes made to the Bluetooth state while your app is running.
     */
    public final BroadcastReceiver enableReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int preState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
                int currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (currentState == BluetoothAdapter.STATE_ON && preState == BluetoothAdapter.STATE_TURNING_ON) {

                    loading();
                }

                if (currentState == BluetoothAdapter.STATE_OFF && preState == BluetoothAdapter.STATE_TURNING_ON) {
                    Toast.makeText(getApplicationContext(), "Bluetooth was not enabled due to an error!", Toast.LENGTH_LONG).show();
                }
                if (currentState == BluetoothAdapter.STATE_OFF && preState == BluetoothAdapter.STATE_TURNING_OFF) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is disable!", Toast.LENGTH_LONG).show();
                }

            }

        }
    };

    /**
     * this function is used for loading into MainActivity. when
     * the Bluetooth is successfully enable!
     */

    private void loading() {
        /**
         *
         new Thread(new Runnable() {
        @Override public void run() {
        try {
        Thread.sleep(1000);

        } catch (InterruptedException e) {
        e.printStackTrace();
        }
        }
        }).start();
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }
        }, 2500);
        Toast.makeText(getApplicationContext(), "enabling Bluetooth succeeds!", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStop() {
        unregisterReceiver(enableReceiver);
        super.onStop();
    }
}
