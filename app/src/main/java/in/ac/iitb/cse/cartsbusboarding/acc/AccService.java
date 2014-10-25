package in.ac.iitb.cse.cartsbusboarding.acc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Queue;

public class AccService extends Service {
    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    AccListener accListener;
    AccData dataRead;
    SensorManager sensorManager;
    Sensor sensor;

    @Override
    public void onCreate() {
        super.onCreate();
//        TODO: look out for a way to create it's object in listner itself
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        accListener = new AccListener();
        accListener = new AccListener(sensorManager, sensor);
        Log.e("Service", "Acc");
        dataRead = accListener.getData();
    }

    /* Getter */
    public AccData getData() {
        dataRead = accListener.getData();
        return dataRead;
    }

    public Queue getDataList() {
        return accListener.getDataList();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        return mBinder;
    }

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public AccService getService() {
            return AccService.this;
        }
    }// LocalBinder

}
