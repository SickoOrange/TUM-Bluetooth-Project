package com.tum.orange.bluetoothmanagement;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.tum.orange.constants.ConstansForBluetoothService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * this Thread runs during a connection with a remote device
 * it handles all incoming and outgoing transmissions
 * Created by Orange on 2016/8/29.
 */
public class ConnectedThread extends Thread {
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private Handler fragment_data_handler;


    public ConnectedThread(BluetoothSocket mSocket, Handler fragment_data_handler) {
        this.mSocket = mSocket;
        this.fragment_data_handler = fragment_data_handler;
        InputStream tmpin = null;
        OutputStream tmpout = null;
        //get the bluetooth input and output streams
        if (mSocket != null && mSocket.isConnected()) {
            try {
                tmpin = mSocket.getInputStream();
                tmpout = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mInputStream = tmpin;
        mOutputStream = tmpout;
        System.out.println("开启监听线程...");
        fragment_data_handler.obtainMessage(ConstansForBluetoothService.MESSAGE_WRITE_STREAM, mOutputStream).sendToTarget();
    }


    /**
     * keep listening to the Inputstream while connected
     */
    @Override
    public void run() {
        byte buffer[] = new byte[1024];
        int n_bytes;
        String recieveMessage;
        System.out.println("开始准备接受数据...");
        while (true) {
            if (!mSocket.isConnected()) {
                break;
            }

            try {

                n_bytes = mInputStream.read(buffer);
                System.out.println("readbyte:" + n_bytes);
                if (n_bytes >= 1 && n_bytes <= 10) {
                    recieveMessage = new String(buffer, 0, n_bytes);
                    System.out.println("recieveMessage from TargetDevice:" + recieveMessage);
                    fragment_data_handler.obtainMessage(ConstansForBluetoothService.MESSAGE_READ, recieveMessage).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("接收数据异常...");
                try {
                    mOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
