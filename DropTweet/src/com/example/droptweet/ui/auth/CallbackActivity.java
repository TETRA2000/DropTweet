package com.example.droptweet.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.droptweet.Const;
import com.example.droptweet.ui.MainActivity;
import com.example.droptweet.ui.twitter.Account;
import com.example.droptweet.ui.twitter.AccountManager;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.os.*;
import org.apache.http.client.protocol.*;
import com.example.droptweet.ui.twitter.*;
import twitter4j.auth.*;
import java.sql.*;
import android.util.*;

public class CallbackActivity extends Activity {
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
		
		Toast.makeText(this, "callback", Toast.LENGTH_LONG).show();

        Uri uri = getIntent().getData();
		
		Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
		
        if(uri!=null &&
                uri.toString().startsWith("callback://CallbackActivity")){
            String verifier = uri.getQueryParameter("oauth_verifier");
            
			RequestTokenPair pair = new RequestTokenPair(AuthActivity._req, verifier);
			TokenTask task = new TokenTask();
			task.execute(pair);
           
        }
    }
	
	class TokenTask extends AsyncTask<RequestTokenPair, Integer, AccessToken>
	{

		@Override
		protected AccessToken doInBackground(RequestTokenPair[] param)
		{
			RequestToken token = param[0].token;
			String verifier = param[0].verifier;
			
			try
			{
				return AuthActivity._oauth.getOAuthAccessToken(token, verifier);
			}
			catch (TwitterException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public void onPostExecute(AccessToken token) {
			if(token != null) {

				//set token
				Twitter twitter = TwitterFactory.getSingleton();
				twitter.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
				twitter.setOAuthAccessToken(token);

				//save token
				Account account = new Account(token.getScreenName(), token.getToken(), token.getTokenSecret());
				AccountManager manager = new AccountManager(CallbackActivity.this);
				manager.setAccount(account);

				Toast.makeText(CallbackActivity.this, "success", Toast.LENGTH_LONG).show();

				//go to MainActivity
				Intent intent = new Intent(CallbackActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(CallbackActivity.this, "failed", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
}
