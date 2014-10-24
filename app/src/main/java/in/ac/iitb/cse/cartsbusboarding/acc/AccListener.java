package in.ac.iitb.cse.cartsbusboarding.acc;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.format.Time;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chaudhary on 10/17/14.
 */
public class AccListener implements SensorEventListener {
    AccData data;
    SensorManager sensorManager;
    Sensor sensor;
    Queue accBuffer;
    int bufferSize;
    int itemsInBuffer;

    AccListener(SensorManager sm,Sensor s){
        sensorManager = sm;
        sensor = s;
        this.sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        accBuffer = new LinkedList();
        bufferSize = 60;//no of readings to be stored in buffer
        itemsInBuffer = 0;
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

        if(itemsInBuffer < bufferSize){
            accBuffer.add(data);
        }else{
            accBuffer.remove();
            accBuffer.add(data);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public AccData getData() {
        return data;
    }

    public Queue getDataList(){
        Log.e("Item",accBuffer.toString());
        return accBuffer;
     }

    public int getQueueSize(){
        return itemsInBuffer;
    }
}
