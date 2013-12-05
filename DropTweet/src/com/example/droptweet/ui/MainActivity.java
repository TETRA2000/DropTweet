package com.example.droptweet.ui;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;

import com.example.droptweet.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);

        setContentView(R.layout.activity_main);
    }
}
