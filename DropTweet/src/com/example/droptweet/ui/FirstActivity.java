package com.example.droptweet.ui;
import com.example.droptweet.Const;
import com.example.droptweet.R;
import com.example.droptweet.R.layout;
import com.example.droptweet.R.string;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.drawable.*;
import android.text.*;
import android.view.*;

public class FirstActivity extends Activity
{
    private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);

        mPref = getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);

		if (!getEulaState())
		{
			showEULADialog();

        }
		else if (isFirstLaunch())
		{
            // TODO Show tutorial

		}
		else if (hasAccount())
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

		//上書き
        if (!launched)
		{
            SharedPreferences.Editor edit = mPref.edit();
            edit.putBoolean(Const.KEY_LAUNCHED, true);
            edit.commit();
        }

        return !launched;
    }

    private boolean hasAccount()
	{
		return mPref.contains(Const.KEY_USER_NAME);
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
		
	}
}
