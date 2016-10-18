package com.tum.orange.bluetoothmanagement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.tum.orange.tum_lmt.MainActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Orange on 2016/8/29.
 */
public class ConnectThread extends Thread {
    private static final int CONNECT_SUCCESSFULLY = 1005;
    private static final int CONNECT_FAILED = 1006;
    private static final int CONNECT_REQUEST = 1008;
    private BluetoothDevice device;
    private BluetoothSocket mSocket;
    private MainActivity mActivity;
    private String uuid;
    private Handler fragment_data_handler;
    private ConnectedThread connectedThread;
    private String lastDeviceMAC;

    public ConnectThread(MainActivity mActivity, String MAC, BluetoothDevice device, String uuid,
                         Handler fragment_data_handler) {
        this.device = device;
        this.uuid = uuid;
        this.mActivity = mActivity;
        this.fragment_data_handler = fragment_data_handler;
        this.lastDeviceMAC = MAC;
        BluetoothSocket tmp = null;
        //get a BluetoothSocket for a connection with the given BluetoothDevice

        if (this.device == null) {
            this.device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lastDeviceMAC);
        }
        try {
            tmp = this.device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSocket = tmp;
    }


    @Override
    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            //System.out.println("connecting...");
            fragment_data_handler.obtainMessage(CONNECT_REQUEST).sendToTarget();
            mSocket.connect();
            //System.out.println("connect successfully");
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("connect failed");
            showConnectStateInMainUI("Connected failed. Can't connect to TargetDevice");
            fragment_data_handler.obtainMessage(CONNECT_FAILED).sendToTarget();

            //close the socket
            try {
                mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        fragment_data_handler.obtainMessage(CONNECT_SUCCESSFULLY, mSocket.getRemoteDevice())
                .sendToTarget();
        showConnectStateInMainUI("Connected Successfully");

        //start the connected Thread for listening the inputStream and send message to target device
        writeDeviceToPreference(mSocket);
        connectedThread = new ConnectedThread(mSocket, fragment_data_handler);
        connectedThread.start();
    }

    private void writeDeviceToPreference(BluetoothSocket mSocket) {
        String autoConnectDeviceMAC = mSocket.getRemoteDevice().getAddress();
        SharedPreferences preferences = mActivity.getSharedPreferences("com.tum.orange" +
                ".tum_lmt_preferences", mActivity.MODE_PRIVATE);
        preferences.edit().putString("last_connect_device", autoConnectDeviceMAC).apply();
    }

    public void cancelConnect() {
        try {
            if (mSocket.isConnected()) {
                mSocket.close();
                mSocket = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showConnectStateInMainUI(final String content) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, content, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
