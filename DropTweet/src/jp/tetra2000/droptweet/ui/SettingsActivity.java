package jp.tetra2000.droptweet.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.tetra2000.droptweet.R;
import jp.tetra2000.droptweet.SensorService;
import jp.tetra2000.droptweet.twitter.AccountManager;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EasyTracker.getInstance().activityStart(this);
    }


    @Override
    protected void onStop() {
        EasyTracker.getInstance().activityStop(this);

        super.onStop();
    }

    @Override
    public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if("run_in_background".equals(key)) {
            Intent intent = new Intent(this, SensorService.class);

            // サービスを再起動
            stopService(intent);
            startService(intent);

            Toast.makeText(this, getString(R.string.changed), Toast.LENGTH_SHORT).show();

        } else if("logout".equals(key)) {
            showLogoutDialog();
        } else if("copyright".equals(key)) {
            showCopyright();
        }

        return true;
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.logout));
        // text
        builder.setMessage(getString(R.string.are_you_ok));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ログアウト
                AccountManager manager = new AccountManager(SettingsActivity.this);
                manager.removeAccount();
				
				Intent intent = new Intent(SettingsActivity.this, SensorService.class);
				stopService(intent);

                finish();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 何もしない
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCopyright()
    {
        Intent intent = new Intent(this, CopyrightActivity.class);
        startActivity(intent);
    }

    private String getCopyright() {
        String langSuffix = "en";

//        String lang = Locale.getDefault().getLanguage();
//        if(lang.equals(Locale.JAPANESE.toString())) {
//            langSuffix = "ja";
//        }

        String fileName = "copyright-" + langSuffix + ".txt";

        AssetManager am = getAssets();

        StringBuilder builder = new StringBuilder();
        try {
            InputStream is = am.open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String line;
            while ((line=br.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
