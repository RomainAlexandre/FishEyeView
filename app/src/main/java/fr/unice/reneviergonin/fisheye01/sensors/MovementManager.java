package fr.unice.reneviergonin.fisheye01.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Class which is used to manage sensors
 * Can pause/resume and start/stop sensor
 * Normal flow :
 * - MUST call isSupported to make sure we can use the needed sensor and set app
 * context and sensor type used to retrieve sensor
 * - call start to register listener for callback and start listening
 * - call pause/resume if don't want to free resources
 * - call stop to free completely stop listening. Will have to call start again
 */
public abstract class MovementManager {

	public interface SensorListener {
	}

	protected Context context = null;

	private Sensor sensor;
	private SensorManager sensorManager;

	protected SensorListener listener;
	protected SensorEventListener sensorEventListener;

	/** indicates whether or not Accelerometer Sensor is supported */
	private Boolean supported;
	/** indicates whether or not Accelerometer Sensor is running */
	private boolean running = false;

	private String name;
	private int sensorType;

	/**
	 * @param c
	 *            application context
	 * @param type
	 *            sensor type
	 * @param name
	 *            used for log
	 */
	public MovementManager(Context c, int type, String name) {
		this.name = name;
		this.context = c;
		this.sensorType = type;
		if (context != null) {
			sensorManager = (SensorManager) context
			        .getSystemService(Context.SENSOR_SERVICE);
			supported = (sensorManager.getDefaultSensor(sensorType) != null);
		} else {
			supported = false;
		}
		sensorEventListener = defineSensorEventListener();
	}

	/**
	 * Registers a listener and start listening
	 * 
	 * @param sensorListener
	 *            callback for sensor custom events
	 */
	public void startListening(SensorListener sensorListener) {
		sensorManager = (SensorManager) context
		        .getSystemService(Context.SENSOR_SERVICE);

		sensor = sensorManager.getDefaultSensor(sensorType);
		if (sensor == null)
			return;
		// Register Listener
		running = sensorManager.registerListener(sensorEventListener, sensor,
		        SensorManager.SENSOR_DELAY_GAME);
		listener = sensorListener;
		Log.d("FISHEYE/sensor", "Started " + name + " Listening");
	}

	/**
	 * Stop listening
	 */
	public void stopListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
				Log.d("FISHEYE/sensor", "Unregistered from " + name
				        + ", destroying");
				sensorManager = null;
				listener = null;
				sensor = null;
			}
		} catch (Exception e) {
			Log.e("FISHEYE/accel", "Error" + e.getMessage());
		}
	}

	/**
	 * Puts listening to pause
	 */
	public void pauseListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
				Log.d("FISHEYE/sensor", "Unregistered from " + name);
			}
		} catch (Exception e) {
			Log.e("FISHEYE/sensor", "Error" + e.getMessage());
		}
	}

	/**
	 * resume listening if listener has already been registered
	 */
	public void resumeListening() {
		if (listener == null || running)
			return;
		if (sensorManager == null || sensor == null) {
			startListening(listener);
		} else {
			running = sensorManager.registerListener(sensorEventListener,
			        sensor, SensorManager.SENSOR_DELAY_GAME);
			Log.d("FISHEYE/sensor", "Resumed " + name + " Listening");
		}
	}

	/**
	 * Returns true if the needed sensor is available
	 */
	public boolean isSupported() {
		return supported;
	}

	/**
	 * Returns true if the manager is listening
	 */
	public boolean isListening() {
		return running;
	}

	protected abstract SensorEventListener defineSensorEventListener();

}
