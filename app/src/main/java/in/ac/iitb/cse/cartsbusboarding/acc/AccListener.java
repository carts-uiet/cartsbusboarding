package in.ac.iitb.cse.cartsbusboarding.acc;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chaudhary on 10/17/14.
 */
public class AccListener implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor sensor;
    /**
     * data: Most recent acceleration value
     */
    private AccData data;
    /**
     * localBuffer contains acceleration values and is cleared externally
     */
    private Queue<AccData> localBuffer;
    /**
     * Flag indicates that we need to empty the buffer after returning it
     */
    private boolean mustClearBufferNow;
    private Context mContext;

    AccListener(Context context) {
//        TODO: look out for a way to create it's object in listner itself
        mContext = context;
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        localBuffer = new LinkedList();
        clearBuffer();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
        data = new AccData();
        data.x = sensorEvent.values[0];
        data.y = sensorEvent.values[1];
        data.z = sensorEvent.values[2];

        //If update is called before returning buffer, it will send an empty buffer to calling method
        if (mustClearBufferNow) {
            clearBuffer();
        }
        localBuffer.add(data);
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
    public Queue<AccData> getDataList() {
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
}

