package jp.tetra2000.droptweet.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import jp.tetra2000.droptweet.Const;
import jp.tetra2000.droptweet.R;
import jp.tetra2000.droptweet.twitter.AccountManager;

import java.util.Objects;

import twitter4j.TwitterException;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

public class AuthActivity extends Activity implements Runnable {
    public static RequestToken _req;
    public static OAuthAuthorization _oauth;

    private AccountManager mManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        mManager = new AccountManager(this);

        findViewById(R.id.button_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(AuthActivity.this).start();
            }
        });
    }

    protected void onResume() {
        super.onResume();

        if(mManager.hasAccount()) {
            // 認証後に戻るキーで戻ってきた場合
            finish();
        }
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
        protected void onPreExecute() {
            // TODO ダイアログを表示
        }

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
            // TODO ダイアログを破棄

            if(success) {
                String _uri;
                _uri = _req.getAuthorizationURL();
                startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(_uri)), 0);
            } else {
                // TODO エラーメッセージ
                finish();
            }
        }
    }
}
