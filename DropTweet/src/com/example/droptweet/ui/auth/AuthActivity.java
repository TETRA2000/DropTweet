package com.example.droptweet.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.droptweet.Const;
import com.example.droptweet.R;

import java.util.Objects;

import twitter4j.TwitterException;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

public class AuthActivity extends Activity implements Runnable {
    public static RequestToken _req;
    public static OAuthAuthorization _oauth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);

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
        _oauth.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
        try {
            _req = _oauth.getOAuthRequestToken("callback://CallbackActivity");
        } catch (TwitterException e) {
            e.printStackTrace();
            // TODO エラーメッセージ
            finish();
            return;
        }

        String _uri;
        _uri = _req.getAuthorizationURL();
        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(_uri)), 0);
    }

    class AuthTask extends AsyncTask<Objects, Objects, Boolean> {

        @Override
        protected Boolean doInBackground(Objects... params) {
            Configuration conf = ConfigurationContext.getInstance();

            _oauth = new OAuthAuthorization(conf);
            _oauth.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
            try {
                _req = _oauth.getOAuthRequestToken("callback://CallbackActivity");
                return true;
            } catch (TwitterException e) {
                e.printStackTrace();
                // TODO エラーメッセージ
                finish();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                String _uri;
                _uri = _req.getAuthorizationURL();
                startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(_uri)), 0);
            } else {
                finish();
            }
        }
    }
}
