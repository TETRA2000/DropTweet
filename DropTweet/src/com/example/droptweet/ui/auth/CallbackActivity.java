package com.example.droptweet.ui.auth;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import com.example.droptweet.ui.twitter.*;
import twitter4j.*;
import com.example.droptweet.*;
import android.content.*;
import com.example.droptweet.ui.*;
import android.widget.*;

public class CallbackActivity extends Activity {
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		Toast.makeText(this, "callback", Toast.LENGTH_LONG).show();

        AccessToken token = null;

        Uri uri = getIntent().getData();
		
		Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
		
        if(uri!=null &&
                uri.toString().startsWith("callback://CallbackActivity")){
            String verifier = uri.getQueryParameter("oauth_verifier");
            try
            {
                token = AuthActivity._oauth.getOAuthAccessToken(AuthActivity._req, verifier);
				
				//set token
				Twitter twitter = TwitterFactory.getSingleton();
				twitter.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
				twitter.setOAuthAccessToken(token);
				
				//save token
				Account account = new Account(token.getScreenName(), token.getToken(), token.getTokenSecret());
				AccountManager manager = new AccountManager(this);
				manager.setAccount(account);
				
				Toast.makeText(this, "success", Toast.LENGTH_LONG).show();
				
				//go to MainActivity
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
            }
            catch (TwitterException e)
            {
                e.printStackTrace();
                //TODO err msg
				Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
