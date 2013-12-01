package com.example.droptweet.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.droptweet.R;

import twitter4j.TwitterException;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

public class AuthActivity extends Activity implements Runnable {
    public static final String CONSUMER_KEY = "";
    public static final String CONSUMER_SECRET = "";

    public static RequestToken _req;
    public static OAuthAuthorization _oauth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        findViewById(R.id.button_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(AuthActivity.this).start();
            }
        });
    }

    @Override
    public void run() {
        Configuration conf = ConfigurationContext.getInstance();

        _oauth = new OAuthAuthorization(conf);
        _oauth.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        try {
            _req = _oauth.getOAuthRequestToken("callback://CallBackActivity");
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        String _uri;
        _uri = _req.getAuthorizationURL();
        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(_uri)), 0);
    }
}