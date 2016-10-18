package com.tum.orange.bluetoothmanagement;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.tum.orange.constants.Constant;

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
        InputStream tempIn = null;
        OutputStream tempOut = null;
        //get the bluetooth input and output streams
        if (mSocket != null && mSocket.isConnected()) {
            try {
                tempIn = mSocket.getInputStream();
                tempOut = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mInputStream = tempIn;
        mOutputStream = tempOut;
        //System.out.println("start for listen...");
        fragment_data_handler.obtainMessage(Constant.MESSAGE_WRITE_STREAM, mOutputStream)
                .sendToTarget();
    }


    /**
     * keep listening to the InputStream while connected
     */
    @Override
    public void run() {
        byte buffer[] = new byte[1024];
        int n_bytes;
        String receiveMessage;
        while (true) {
            if (!mSocket.isConnected()) {
                break;
            }
            try {
                n_bytes = mInputStream.read(buffer);
                if (n_bytes > 1 && n_bytes <= 10) {
                    receiveMessage = new String(buffer, 0, n_bytes);
                    //Log.e("TEST","receiveMessage.length" + receiveMessage.length());
                    if (receiveMessage.length() > 1) {
                        fragment_data_handler.obtainMessage(Constant.MESSAGE_READ, receiveMessage)
                                .sendToTarget();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Receive Exception");
                try {
                    mOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
