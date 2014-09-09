package jp.tetra2000.droptweet;

import jp.tetra2000.droptweet.twitter.AccountManager;
import jp.tetra2000.droptweet.twitter.TwitterManager;
import android.app.Application;


public class App extends Application {
	public static volatile Boolean serviceRunning = false;
	
    public void onCreate() {
        AccountManager manager = new AccountManager(this);
        if(manager.hasAccount()) {
            TwitterManager.init(manager.getAccount());
        }
    }
}
