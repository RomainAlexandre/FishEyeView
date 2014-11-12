package fr.unice.reneviergonin.fisheye01.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Manage listening to accelerometer sensors and detect shake
 */
public class AccelerometerManager extends MovementManager {

	/** Accuracy configuration */
	private static float threshold = 0.2f;
	private static int interval = 200;

	public AccelerometerManager(Context c) {
		super(c, Sensor.TYPE_ACCELEROMETER, "ACCELEROMETER");
	}

	public interface AccelerometerListener extends SensorListener {
		public void onMove (float x, float y);
	}

	@Override
	public void startListening(SensorListener sensorListener) {
		super.startListening(sensorListener);
		((CustomSensorEventListener) sensorEventListener).resetData();
	}

    public void resetSensorData(){
        ((CustomSensorEventListener) sensorEventListener).resetData();
    }

	@Override
	protected SensorEventListener defineSensorEventListener() {
		return new CustomSensorEventListener();
	}

	/**
	 * The listener that listen to events from the accelerometer listener
	 */
	private class CustomSensorEventListener implements SensorEventListener {
		private long now = 0;
		private long lastUpdate = 0;

		private float x = 0;
		private float y = 0;
		private float lastX = 0;
		private float lastY = 0;
		private float force = 0;

		@Override
		public void onSensorChanged(SensorEvent event) {
			// use the event timestamp as reference so the precision won't
			// depend on the AccelerometerListener processing time
			now = event.timestamp;

			y = event.values[0];
            x = event.values[1];

			if (lastUpdate == 0) {
				lastUpdate = now;
				lastX = x;
				lastY = y;
				Log.d("FISHEYE", "No Motion detected");
			} else {
				float timeDiff = now - lastUpdate;
				if (timeDiff > 0) {
					force = Math.abs(x + y - lastX - lastY);
                    if(force>threshold && listener!=null) {
                        ((AccelerometerListener) listener).onMove(lastX - x, lastY - y);
                    }
					lastUpdate = now;
				} else {
					Log.d("FISHEYE", "No Motion detected");
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void resetData() {
			now = 0;
			lastUpdate = 0;
			x = 0;
			y = 0;
			lastX = 0;
			lastY = 0;
			force = 0;
		}
	}
}
