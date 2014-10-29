package in.ac.iitb.cse.cartsbusboarding.acc;

import android.app.Service;
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
    private AccListener accListener;
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    public void onCreate() {
        super.onCreate();
        /** Context needed to create sensor manager in listener */
        accListener = new AccListener(this.getApplicationContext());
        Log.e("Service", "Acc");
    }

    /* Getter */
    public AccData getCurrentData() {
        return accListener.getCurrentData();
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
