package com.example.droptweet.ui.auth;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class CallbackActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccessToken token = null;

        Uri uri = getIntent().getData();
        if(uri!=null &&
                uri.toString().startsWith("callback://CallbackActivity")){
            String verifier = uri.getQueryParameter("oauth_verifier");
            try
            {
                token = AuthActivity._oauth.getOAuthAccessToken(AuthActivity._req, verifier);
            }
            catch (TwitterException e)
            {
                e.printStackTrace();
                //TODO err msg
                finish();
            }
        }
    }
}