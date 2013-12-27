package com.example.droptweet.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.droptweet.twitter.Account;
import com.example.droptweet.twitter.AccountManager;
import com.example.droptweet.twitter.RequestTokenPair;
import com.example.droptweet.twitter.TwitterManager;
import com.example.droptweet.ui.MainActivity;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class CallbackActivity extends Activity {
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
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

				//save token
				Account account = new Account(token.getScreenName(), token.getToken(), token.getTokenSecret());
				AccountManager manager = new AccountManager(CallbackActivity.this);
				manager.setAccount(account);

                //init TwitterManager
                TwitterManager.init(account);

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
