package in.ac.iitb.cse.cartsbusboarding.acc;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import in.ac.iitb.cse.cartsbusboarding.R;

/**
 * Main Listener that receives the accelerometer data
 */
public class AccListener implements SensorEventListener {
    private final String _Classname = AccListener.class.getSimpleName();
    private final SensorManager sensorManager;
    private final Sensor sensor;
    /**
     * data: Most recent acceleration value
     */
    private AccData data;
    /**
     * localBuffer contains acceleration values and is cleared externally
     */
    private ConcurrentLinkedQueue<AccData> localBuffer;
    /**
     * Flag indicates that we need to empty the buffer after returning it
     */
    private boolean mustClearBufferNow;
    private Context mContext;

    AccListener(Context context) {
        mContext = context;
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() < 1) {
            Toast.makeText(mContext, R.string.accelerometer_not_found, Toast.LENGTH_LONG).show();
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //this.sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        this.sensorManager.registerListener(this, sensor, getSensorSpeed());
        localBuffer = new ConcurrentLinkedQueue();
        clearBuffer();
        Log.v(_Classname, "SensorList: " + sensorList.toString());
        Log.v(_Classname, "Sensor MinDelay: " + sensor.getMinDelay() + " microseconds");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
        data = new AccData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);

        //If update is called before returning buffer, it will send an empty buffer to calling method
        if (mustClearBufferNow) {
            clearBuffer();
        }
        localBuffer.offer(data);
        //localBuffer.add(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Returns the most recent acceleration value
     *
     * @return most recent acc value
     */
    public AccData getCurrentData() {
        return data;
    }

    /**
     * getDataList returns localBufferData
     * Flag indicates that we need to empty the buffer after returning it
     *
     * @return Queue of AccData values
     */
    public ConcurrentLinkedQueue<AccData> getDataList() {
        mustClearBufferNow = true;
        //XXX: Returning a reference, which might be modified here and outside, while clear is called
        return localBuffer;
    }

    /**
     * clearBuffer empties the localBuffer
     * Flag indicates data will be collected not fetched
     */
    private void clearBuffer() {
        localBuffer.clear();
        mustClearBufferNow = false;
    }

    /**
     * Defines the current sensor speed used to register the listener
     * <p>Possible ways:
     * <li>int SENSOR_DELAY_FASTEST get sensor data as fast as possible</li>
     * <li>int SENSOR_DELAY_GAME rate suitable for games</li>
     * <li>int SENSOR_DELAY_NORMAL rate (default) suitable for screen orientation changes</li>
     * <li>int SENSOR_DELAY_UI rate suitable for the user interface</li>
     * </p>
     * @return integer
     */
    public int getSensorSpeed() {
        return SensorManager.SENSOR_DELAY_GAME;
    }
}

