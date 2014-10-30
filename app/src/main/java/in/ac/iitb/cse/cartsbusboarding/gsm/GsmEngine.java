package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by chaudhary on 10/23/14.
 */
public class GsmEngine {
    private final Context mContext;
    private GsmService mGsmService;
    private GsmData data;
    private ServiceConnection mServiceConnection;

    public GsmEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, GsmService.class));

        initServiceConnection();
    }

    private void initServiceConnection() {
        mServiceConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    mGsmService = ((GsmService.LocalBinder) service).getService();
                } catch (Throwable t) {
                    Log.e("GsmEngine", "mServiceConnection.onServiceConnected() -> " + t);
                }
            }// onServiceConnected()

            public void onServiceDisconnected(ComponentName className) {
                mGsmService = null;
            }// onServiceDisconnected()
        };

        mContext.bindService(new Intent(mContext, GsmService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public GsmData getData() {
        data = mGsmService.getData();
        return data;
    }

    /**
     * Get distance b/w two points namely source and destination
     * @param src
     * @param dest
     * @return double distance value
     */
    public double getDistance(GsmData src, GsmData dest) {
        Location locationA = new Location("point A");

        locationA.setLatitude(src.getGsmLat());
        locationA.setLongitude(src.getGsmLong());

        Location locationB = new Location("point B");

        locationB.setLatitude(dest.getGsmLat());
        locationB.setLongitude(dest.getGsmLong());

        float distance = locationA.distanceTo(locationB);
        return ((double) distance);
    }

}
