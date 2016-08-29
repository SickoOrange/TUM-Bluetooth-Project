package com.tum.orange.bluetoothmanagement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.Toast;

import com.tum.orange.tum_lmt.MainActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Orange on 2016/8/29.
 */
public class ConnectThread extends Thread {
    private BluetoothDevice device;
    private BluetoothSocket mSocket;
    private MainActivity mActivity;
    private String uuid;
    private Handler fragment_data_handler;
    private ConnectedThread connectedThread;

    public ConnectThread(MainActivity mActivity, BluetoothDevice device, String uuid, Handler fragment_data_handler) {
        this.device = device;
        this.uuid = uuid;
        this.mActivity = mActivity;
        this.fragment_data_handler = fragment_data_handler;
        BluetoothSocket tmp = null;
        //get a BluetoothSocket for a connection with the given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            System.out.println("正在连接");
            mSocket.connect();
            System.out.println("连接成功");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("连接失败");
            showConnectStateInMainUI("Connected failed. Can't connect to TargetDevice");
            //close the socket
            try {
                mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //connectionFailed();
            return;
        }
        System.out.println("连接成功+1");
        showConnectStateInMainUI("Connected Successfully");
        //start the connected Thread for listening the inputStream and send message to target device
        connectedThread = new ConnectedThread(mSocket,fragment_data_handler);
        connectedThread.start();
    }

    public void cancelConnect() {
        try {
            if (mSocket.isConnected()) {
                mSocket.close();
                mSocket = null;
                System.out.println("已经存在的连接被关闭");
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
