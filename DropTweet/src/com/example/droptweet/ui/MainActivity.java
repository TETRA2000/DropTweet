package com.example.droptweet.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.droptweet.Const;
import com.example.droptweet.R;
import com.example.droptweet.SensorService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String flag =  getIntent().getStringExtra(Const.LAUNCH_FLAG);

        if(Const.FLAG_NOTIFICATION.equals(flag)) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } else {
            //サービスを起動
            Intent intent = new Intent(this, SensorService.class);
            startService(intent);
        }

        setContentView(R.layout.activity_main);
    }
}
