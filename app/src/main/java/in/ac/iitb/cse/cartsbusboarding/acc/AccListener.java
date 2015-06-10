/**
 * CartsBusBoarding - Bus Boarding Event detection project by
 * CARTS in IITB & UIET, Panjab University
 * <p/>
 * Copyright (c) 2014 Shubham Chaudhary <me@shubhamchaudhary.in>
 * Copyright (c) 2014 Tanjot Kaur <tanjot28@gmail.com>
 * <p/>
 * This file is part of CartsBusBoarding.
 * <p/>
 * CartsBusBoarding is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * CartsBusBoarding is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with CartsBusBoarding.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.ac.iitb.cse.cartsbusboarding.acc;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;
import in.ac.iitb.cse.cartsbusboarding.R;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGV;

/**
 * Main Listener that receives the accelerometer data
 */
public class AccListener implements SensorEventListener {
    private static final String TAG = LogUtils.makeLogTag(AccListener.class);
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
        LOGV(TAG, "SensorList: " + sensorList.toString());
        LOGV(TAG, "Sensor MinDelay: " + sensor.getMinDelay() + " microseconds");
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
     *
     * @return integer
     */
    public int getSensorSpeed() {
        return SensorManager.SENSOR_DELAY_GAME;
    }
}

