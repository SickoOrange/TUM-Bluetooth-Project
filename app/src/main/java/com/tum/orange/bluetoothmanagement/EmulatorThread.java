package com.tum.orange.bluetoothmanagement;

import android.os.Handler;

import com.tum.orange.constants.Constant;

import java.math.BigDecimal;
import java.util.Random;


/**
 * this Thread is created for the Emulator Mode
 * so that the device can automatic create random data
 * and show the data in the chart
 * Created by yinya on 2016/9/17.
 */
public class EmulatorThread extends Thread {
    private Handler handler;
    public boolean isFinished = true;

    public EmulatorThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Random random = new Random();
        random.setSeed(300);
        while (true) {

            if (!isFinished) {
                float Emulator_Data = (float) (Math.random() * 300);
                BigDecimal decimal = new BigDecimal(Emulator_Data);
                float Emulator_Data_FloatValue = decimal.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .floatValue();
                System.out.println(Emulator_Data);
                try {
                    Thread.currentThread().sleep(1000);
                    handler.obtainMessage(Constant.MESSAGE_READ, String.valueOf
                            (Emulator_Data_FloatValue)).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
