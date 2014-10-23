package in.ac.iitb.cse.cartsbusboarding.acc;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by chaudhary on 10/17/14.
 */
public class AccListener implements SensorEventListener {
    AccData data;
    SensorManager sensorManager;
    Sensor sensor;

    AccListener(SensorManager sm,Sensor s){
        sensorManager = sm;
        sensor = s;
        this.sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public AccData getData() {
        return data;
    }
}
