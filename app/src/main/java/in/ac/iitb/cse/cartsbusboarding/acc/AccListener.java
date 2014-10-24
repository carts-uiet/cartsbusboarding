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
    Queue localBuffer;

    int bufferSize;
    int itemsInBuffer;
    boolean getDataList;

    AccListener(SensorManager sm,Sensor s){
        sensorManager = sm;
        sensor = s;
        this.sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        bufferSize = 30;//no of readings to be stored in buffer
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

        //TODO: Explaination
        if(getDataList){
            updateQueue();
        }
        if(itemsInBuffer < bufferSize){
            localBuffer.add(data);
            itemsInBuffer++;
        }else{
            localBuffer.remove();
            localBuffer.add(data);
        }

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
     * TODO: Documentation
     * @return
     */
    public Queue getDataList(){
//        Log.e("Item",localBuffer.toString());
        getDataList = true;
        return localBuffer;
     }

    /**
     * updateQueue: TODO: Documentation
     */
    public void updateQueue(){
        localBuffer = new LinkedList();
        getDataList = false;
        itemsInBuffer = 0;
    }
}

