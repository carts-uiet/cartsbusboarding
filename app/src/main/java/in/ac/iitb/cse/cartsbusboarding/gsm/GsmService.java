package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

public class GsmService extends Service {
    LocationManager gsmMgr;
    GsmData dataRead;

    @Override
    public void onCreate() {
        super.onCreate();
        gsmMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GsmListner gsmListener = new GsmListner();

        gsmMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gsmListener);
        dataRead = gsmListener.getData();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
