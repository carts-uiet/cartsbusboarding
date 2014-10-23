package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

public class GsmService extends Service {
    LocationManager gsmMgr;
    GsmListener gsmListener;
    GsmData dataRead;

    @Override
    public void onCreate() {
        super.onCreate();
        gsmMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gsmListener = new GsmListener();

        gsmMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gsmListener);
        dataRead = gsmListener.getData();
    }

    /* Getter */
    public GsmData getData() {
        dataRead = gsmListener.getData();
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

        public GsmService getService() {
            return GsmService.this;
        }
    }// LocalBinder

    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

}
