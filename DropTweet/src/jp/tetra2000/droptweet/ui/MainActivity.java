package jp.tetra2000.droptweet.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import jp.tetra2000.droptweet.App;
import jp.tetra2000.droptweet.Const;
import jp.tetra2000.droptweet.R;
import jp.tetra2000.droptweet.SensorService;
import jp.tetra2000.droptweet.twitter.AccountManager;

public class MainActivity extends Activity {
    private SharedPreferences mPref;
    private TextView[] mTvs;

    private AccountManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new AccountManager(this);

        String flag =  getIntent().getStringExtra(Const.LAUNCH_FLAG);

        if(Const.FLAG_NOTIFICATION.equals(flag)) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } else if(!App.serviceRunning) {
            //サービスを起動
            Intent intent = new Intent(this, SensorService.class);
            startService(intent);
        }

        setContentView(R.layout.activity_main);

        // カウンター用のTextViewを更新
        TextView tvs[] ={
                (TextView)findViewById(R.id.numA),
                (TextView)findViewById(R.id.numB),
                (TextView)findViewById(R.id.numC),
                (TextView)findViewById(R.id.numD),
        };
        mTvs = tvs;

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 設定画面でログアウトされた場合
        if(!mManager.hasAccount()) {
            finish();
        }

        int dropCount = mPref.getInt(Const.KEY_DROP_COUNT, 0);

        updateCounter(mTvs, dropCount);
    }

    @Override
    protected void onStop() {
        EasyTracker.getInstance().activityStop(this);

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }

        return false;
    }

    /**
     *
     * @param views 出力先TextViewの配列(桁の多きい方から順に)
     * @param num 変換する値(正の値に限る)
     */
    private void updateCounter(TextView[] views, int num) {
        int len = views.length;

        // 負の場合は正に変換
        if(num<0)
            num*=-1;

        // 桁が足りない部分は無視
        int sup = (int) Math.pow(10, len);
        num%=sup;

        String str = String.valueOf(num);

        int strLen = str.length();
        for(int i=0; i<len-strLen; i++)
            str = '0'+str;

        for(int i=0; i<len; i++)
            views[i].setText(str.charAt(i) + "");
    }
}
