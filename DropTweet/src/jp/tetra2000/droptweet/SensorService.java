package jp.tetra2000.droptweet;

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
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Locale;

import jp.tetra2000.droptweet.twitter.Account;
import jp.tetra2000.droptweet.twitter.AccountManager;
import jp.tetra2000.droptweet.twitter.TwitterManager;
import jp.tetra2000.droptweet.ui.MainActivity;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";
	
	private static final float THRESHOLD = 0.7f;
	private static final long MIN_FALL_TIME = 247000000; // 0.30m
	private static final double GRAVITY = 9.8f;
    private static final long MIN_SENSOR_INTERVAL =150000000;

    private boolean mBgFlag;
	
	private SensorManager mSensorManager;
	private Sensor mAccel;

    private SharedPreferences mPref;
	
	private PowerManager.WakeLock mWakeLock;
	
	private NotificationManager mNotifManager;

    private long mLastSensorTime;
	
	private boolean mFallFlag;
	private long mFallStartTime; // nano seconds
	
	private boolean mWatchingSensor;
	
	@Override
	public void onCreate() {
		mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		// update flag
		App.serviceRunning = true;
		
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //バックグラウンドで動作するか
        mBgFlag = preferences.getBoolean("run_in_background", false);

        if(mBgFlag) {
            // 再起動した場合、必要に応じてWakeLockを再確保
            if(mWakeLock == null || !mWakeLock.isHeld()) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));
                mWakeLock.acquire();
            }
        }
		
		if(!setUpSensor()) {
            this.stopSelf();
        } else if(!mWatchingSensor) {
            mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
            mWatchingSensor = true;
        }
        
        return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		mSensorManager.unregisterListener(this, mAccel);

        if(mBgFlag)
            mWakeLock.release();
        
        // update flag
        App.serviceRunning = false;
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
        long timestamp = event.timestamp;

        if(timestamp - mLastSensorTime > MIN_SENSOR_INTERVAL) {
            // センサーがスリープした場合

            // 落下判定を取り消し
            mFallFlag = false;

            mLastSensorTime = timestamp;
            return;
        } else {
            mLastSensorTime = timestamp;
        }
		
		// x, y, z values
        if(Math.abs(values[2]) <= THRESHOLD && Math.abs(values[0]) <= THRESHOLD && Math.abs(values[1]) <= THRESHOLD) {
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
        editor.commit();
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

            int dropCount = getDropCount();
            dropCount++;
			
			String countStr;
			
			Locale locale =
				getResources().getConfiguration().locale;
				
			if(!locale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
				switch (dropCount) {
					case 1:
						countStr = "the first time";
						break;
					case 2:
						countStr = "the second time";
						break;
					case 3:
						countStr = "the third time";
						break;
					default:
						countStr = "the " + dropCount + "th";
				}
			} else {
				countStr = dropCount + "";
			}

            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.tweet_format, countStr));
            builder.append(" ");
            builder.append(getString(R.string.hash_tag));

            try {
                twitter.updateStatus(builder.toString());
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }

            setDropCount(dropCount);
            return height;
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
