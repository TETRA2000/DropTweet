package jp.tetra2000.droptweet.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import jp.tetra2000.droptweet.R;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
