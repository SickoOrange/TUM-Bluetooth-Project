package com.tum.orange.bluetoothmanagement;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

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
    private static final int MESSAGE_READ = 10;
    private static final int MESSAGE_WRITE_STREAM = 11;

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
        fragment_data_handler.obtainMessage(MESSAGE_WRITE_STREAM, mOutputStream).sendToTarget();
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
                if (n_bytes >= 1) {
                    recieveMessage = new String(buffer, 0, n_bytes);
                    //System.out.println("recieveMessage from TargetDevice:" + recieveMessage);
                    fragment_data_handler.obtainMessage(MESSAGE_READ, recieveMessage).sendToTarget();
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


    /**
     * send a Message
     *
     * @param msg Message content
     */
    public void writeMessage(String msg) {
        if (msg != null && (msg.length() > 0)) {
            try {
                mOutputStream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
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
