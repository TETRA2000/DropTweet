package jp.tetra2000.droptweet.ui;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import jp.tetra2000.droptweet.twitter.AccountManager;
import jp.tetra2000.droptweet.ui.auth.AuthActivity;

public class FirstActivity extends Activity
{
    private AccountManager mManager;

	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);

        mManager = new AccountManager(this);

        if(!mManager.hasAccount()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
		else
		{
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}
}
