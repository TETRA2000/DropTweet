package jp.tetra2000.droptweet.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import jp.tetra2000.droptweet.Const;
import jp.tetra2000.droptweet.R;
import jp.tetra2000.droptweet.twitter.AccountManager;
import jp.tetra2000.droptweet.ui.auth.AuthActivity;

public class FirstActivity extends Activity
{
    private SharedPreferences mPref;
    private AccountManager mManager;

	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mManager = new AccountManager(this);

		if (!getEulaState())
		{
			showEULADialog();

        }
        else if(!mManager.hasAccount()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
		else
		{
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

    private boolean getEulaState()
	{
        return mPref.getBoolean(Const.KEY_EULA_STATE, false);
    }

	private void setEulaState(boolean state)
	{
		SharedPreferences.Editor editor = mPref.edit();
		editor.putBoolean(Const.KEY_EULA_STATE, state);
		editor.commit();
	}

    private boolean isFirstLaunch()
	{
        // 起動したことがあるか
        boolean launched =  mPref.getBoolean(Const.KEY_LAUNCHED, false);
        return !launched;
    }

    private void setLaunchState(boolean state) {
        SharedPreferences.Editor edit = mPref.edit();
        edit.putBoolean(Const.KEY_LAUNCHED, state);
        edit.commit();
    }

    private void showEULADialog()
	{
		View layout = getLayoutInflater().inflate(R.layout.dialog_eula, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// EULA
		builder.setTitle(getString(R.string.eula));
		// EULA text
		builder.setView(layout);
		// Agree
		builder.setPositiveButton(getString(R.string.agree), new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					setEulaState(true);

                    // 認証画面へ
                    Intent intent = new Intent(FirstActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
				}
			});
		// Reject
		builder.setNegativeButton(getString(R.string.reject), new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					finish();
				}
			});
		// Cancel
		builder.setCancelable(true);
		builder.setOnCancelListener(new AlertDialog.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface p1)
				{
					finish();
				}
			});

		AlertDialog dialog = builder.create();
		dialog.show();
    }
}
