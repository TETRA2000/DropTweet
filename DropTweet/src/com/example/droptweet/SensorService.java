package com.example.droptweet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.droptweet.twitter.Account;
import com.example.droptweet.twitter.AccountManager;
import com.example.droptweet.twitter.TwitterManager;
import com.example.droptweet.ui.MainActivity;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";
	
	private static final float THRESHOLD = 1.5f;
	private static final long MIN_FALL_TIME = 247000000; // 0.30m
	private static final double GRAVITY = 9.8f;
	
	private SensorManager mSensorManager;
	private Sensor mAccel;

    private SharedPreferences mPref;
	
	private PowerManager.WakeLock mWakeLock;
	
	private NotificationManager mNotifManager;
	
	private boolean mFallFlag;
	private long mFallStartTime; // nano seconds
	
	@Override
	public void onCreate() {
		mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mPref = getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
	}
	
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
				
				if(fallTime >= MIN_FALL_TIME) {
                    float height = getHeight(fallTime);
                    Toast.makeText(this, fallTime + "ns: " + height + "m", Toast.LENGTH_LONG).show();

                    TweetTask tweetTask = new TweetTask();
                    tweetTask.execute(height);
                }
			}
		}
	}
	
	/**
	 * 
	 * @param time time in nano seconds
	 * @return fall height in meter
	 */
	private float getHeight(long time) {
		return (float) (0.5 * GRAVITY * Math.pow(0.000000001 * time, 2));
	}

    private int getDropCount() {
        return mPref.getInt(Const.KEY_DROP_COUNT, 0);
    }

    private void setDropCount(int count) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(Const.KEY_DROP_COUNT, count);
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

            Twitter twitter = TwitterManager.getTwitter();

            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.tweet_format, height, 0));
            builder.append(" ");
            builder.append(getString(R.string.hash_tag));

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
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SensorService.this);

            Intent intent = new Intent(SensorService.this, MainActivity.class);
            intent.putExtra(Const.LAUNCH_FLAG, Const.FLAG_NOTIFICATION);
            PendingIntent pendingIntet =
                    PendingIntent.getActivity(SensorService.this, 274, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			builder
                .setContentIntent(pendingIntet)
				.setSmallIcon(android.R.drawable.ic_dialog_alert)
				.setContentTitle(getString(R.string.notification_title))
				.setContentText(getString(R.string.notification_format, height));
			
			// idはテスト
			mNotifManager.notify(274, builder.build());
        }
    }
}
