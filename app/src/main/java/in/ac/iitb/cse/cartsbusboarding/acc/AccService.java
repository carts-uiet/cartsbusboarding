package in.ac.iitb.cse.cartsbusboarding.acc;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AccService extends Service {
    AccListener accListener;
    AccData dataRead;

    @Override
    public void onCreate() {
        super.onCreate();
        accListener = new AccListener();

        dataRead = accListener.getData();
    }

    /* Getter */
    public AccData getData() {
        dataRead = accListener.getData();
        return dataRead;
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

    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

}
