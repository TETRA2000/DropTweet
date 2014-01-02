package com.example.droptweet;

import android.app.Application;

import com.example.droptweet.twitter.AccountManager;
import com.example.droptweet.twitter.TwitterManager;

public class App extends Application {
    public void onCreate() {
        AccountManager manager = new AccountManager(this);
        if(manager.hasAccount()) {
            TwitterManager.init(manager.getAccount());
        }
    }
}
