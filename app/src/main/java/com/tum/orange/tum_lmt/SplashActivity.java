package com.tum.orange.tum_lmt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tum.orange.CircleProgress.CircleProgress;

public class SplashActivity extends AppCompatActivity {

    private CircleProgress circle_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /**

         circle_progress = (CircleProgress) findViewById(R.id.circle_progress);
         circle_progress.setDuration(2000);
         circle_progress.startAnim();
         *
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, LoadingActivity.class));
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
