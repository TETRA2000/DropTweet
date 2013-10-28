package com.example.droptweet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class SensorService extends Service implements SensorEventListener {
	
	private static final float THRESHOLD = 2.5f;
	private static final long MIN_FALL_TIME = 247000000; // 0.30m
	private static final double GRAVITY = 9.8f;
	
	private SensorManager mSensorManager;
	private Sensor mAccel;
	
	private boolean mFallFlag;
	private long mFallStartTime; // nano seconds
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
        if(!setUpSensor())
        	this.stopSelf();
        
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        
        return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		mSensorManager.unregisterListener(this, mAccel);
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
}
