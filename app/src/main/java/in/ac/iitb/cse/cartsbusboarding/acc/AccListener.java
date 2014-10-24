package in.ac.iitb.cse.cartsbusboarding.acc;

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
    SensorManager sensorManager;
    Sensor sensor;
    /** data: Most recent acceleration value */
    AccData data;
    /** localBuffer contains acceleration values and is cleared externally */
    Queue<AccData> localBuffer;
    /** Flag indicates that we need to empty the buffer after returning it */
    boolean getDataList;

    AccListener(SensorManager sm,Sensor s){
        sensorManager = sm;
        sensor = s;
        this.sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        localBuffer = new LinkedList();
        updateQueue();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
        float curX = sensorEvent.values[0];
        float curY = sensorEvent.values[1];
        float curZ = sensorEvent.values[2];
        data = new AccData();
        data.x = curX;
        data.y = curY;
        data.z = curZ;

        //If update is called before returning buffer, it will send an empty buffer to calling method
        if(getDataList){
            updateQueue();
        }
        localBuffer.add(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Returns the most recent acceleration value
     * @return most recent acc value
     */
    public AccData getData() {
        return data;
    }

    /**
     * getDataList returns localBufferData
     * Flag indicates that we need to empty the buffer after returning it
     * @return Queue of AccData values
     */
    public Queue<AccData> getDataList(){
        getDataList = true;
        return localBuffer;
     }

    /**
     * updateQueue empties the localBuffer
     * Flag indicates data will be collected not fetched
     */
    public void updateQueue(){
        localBuffer.removeAll(localBuffer);
        getDataList = false;
    }
}

