package com.example.droptweet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.droptweet.twitter.Account;
import com.example.droptweet.twitter.AccountManager;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";
	
	private static final float THRESHOLD = 2.5f;
	private static final long MIN_FALL_TIME = 247000000; // 0.30m
	private static final double GRAVITY = 9.8f;
	
	private SensorManager mSensorManager;
	private Sensor mAccel;
	
	private PowerManager.WakeLock mWakeLock;
	
	private boolean mFallFlag;
	private long mFallStartTime; // nano seconds
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		if(mWakeLock == null || !mWakeLock.isHeld()) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));
		}
		
		if(!setUpSensor()) {
            Log.d(TAG, "no accelerometer");
            this.stopSelf();
        }
        
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);

        Log.d(TAG, "service started successfully");
        
        return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		mSensorManager.unregisterListener(this, mAccel);
		
		mWakeLock.release();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
    private boolean setUpSensor() {
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        return mAccel != null;
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		
		// x, y, z values
		if(values[0] <= THRESHOLD && values[1] <= THRESHOLD && values[2] <= THRESHOLD) {
			// falling
			
			if(!mFallFlag) {
				mFallStartTime = event.timestamp;
				mFallFlag = true;
			}
			
		} else {
			if(mFallFlag) {
				// landed
				
				mFallFlag = false;
				
				long fallTime = event.timestamp - mFallStartTime;
				
				if(fallTime >= MIN_FALL_TIME)
					Toast.makeText(this, fallTime + "ns: " + getHeight(fallTime) + "m", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * 
	 * @param time time in nano seconds
	 * @return fall height in meter
	 */
	private double getHeight(long time) {
		return 0.5 * GRAVITY * Math.pow(0.000000001 * time, 2);
	}

    class TweetTask extends AsyncTask<Float, Integer, Float> {
        private String TAG = "TweetTask";

        @Override
        protected Float doInBackground(Float... heights) {
            AccountManager manager = new AccountManager(SensorService.this);
            Account account = manager.getAccount();

            if(account==null)
                return null;

            float height = heights[0];

            Twitter twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(Const.KEY_TOKEN, Const.KEY_TOKEN_SECRET);
            twitter.setOAuthAccessToken(new AccessToken(account.token, account.secret));

            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.tweet_pre));
            builder.append(height);
            builder.append(getString(R.string.tweet_suf));
            builder.append(" ");
            builder.append(Const.HASH_TAG);

            try {
                twitter.updateStatus(builder.toString());
                return height;
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.d(TAG, "failed to tweet");
                return null;
            }
        }

        @Override
        public void onPostExecute(Float height) {
            // TODO 通知
        }
    }
}
