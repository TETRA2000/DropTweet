package jp.tetra2000.droptweet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import jp.tetra2000.droptweet.R;
import jp.tetra2000.droptweet.SensorService;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {
        if("run_in_background".equals(preference.getKey())) {
            Intent intent = new Intent(this, SensorService.class);

            // サービスを再起動
            stopService(intent);
            startService(intent);

            Toast.makeText(this, getString(R.string.changed), Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
