package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

public class GsmService extends Service {
    LocationManager gpsMgr, gsmMgr;
    double gsmLat, gsmLong;
    float gsmAccuracy;
    Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //GPSmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gsmMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GsmListner gsmListener = new GsmListner();

        gsmMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gsmListener);
        GsmData dataRead = gsmListener.getData();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
