package com.example.droptweet.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.droptweet.Const;
import com.example.droptweet.R;
import com.example.droptweet.twitter.AccountManager;
import com.example.droptweet.ui.auth.AuthActivity;

public class FirstActivity extends Activity
{
    private SharedPreferences mPref;
    private AccountManager mManager;

	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);

        mPref = getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
        mManager = new AccountManager(this);

		if (!getEulaState())
		{
			showEULADialog();

        }
		else if (isFirstLaunch())
		{
            showTutorialDialog();
		}
        else if(!mManager.hasAccount()) {
            startActivity(new Intent(FirstActivity.this, AuthActivity.class));
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
                    showTutorialDialog();
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
	
	private void showTutorialDialog() {
        View layout = getLayoutInflater().inflate(R.layout.dialog_tutorial, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // EULA
        builder.setTitle(getString(R.string.eula));
        // EULA text
        builder.setView(layout);
        // Agree
        builder.setPositiveButton(getString(R.string.login), new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2)
            {
                setLaunchState(true);
                startActivity(new Intent(FirstActivity.this, AuthActivity.class));
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
