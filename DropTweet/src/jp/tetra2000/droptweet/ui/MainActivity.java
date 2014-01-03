package jp.tetra2000.droptweet.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import jp.tetra2000.droptweet.Const;
import jp.tetra2000.droptweet.R;
import jp.tetra2000.droptweet.SensorService;

public class MainActivity extends Activity {
    private SharedPreferences mPref;
    private TextView[] mTvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String flag =  getIntent().getStringExtra(Const.LAUNCH_FLAG);

        if(Const.FLAG_NOTIFICATION.equals(flag)) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } else {
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

        mPref = getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int dropCount = mPref.getInt(Const.KEY_DROP_COUNT, 0);

        updateCounter(mTvs, dropCount);
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
