package jp.tetra2000.droptweet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jp.tetra2000.droptweet.twitter.AccountManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AccountManager manager = new AccountManager(context);
        if(manager.hasAccount()) {
            Intent serviceIntent = new Intent(context, SensorService.class);
            context.startService(serviceIntent);
        }
    }
}
