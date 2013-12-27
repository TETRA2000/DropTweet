package com.example.droptweet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.droptweet.R;
import com.example.droptweet.SensorService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //サービスを起動
        Intent intent = new Intent(this, SensorService.class);
        startService(intent);

        setContentView(R.layout.activity_main);
    }
}
