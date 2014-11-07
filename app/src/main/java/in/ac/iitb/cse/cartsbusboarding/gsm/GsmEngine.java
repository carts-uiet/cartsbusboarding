package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

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

    public boolean hasSpeed(){
        return mGsmService.hasSpeed();
    }
    public float getSpeed(){
        return mGsmService.getSpeed();
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

    /**
     *
     * @param time (in ms)
     * @return
     */
    public double getSpeed(long time){
        GsmData source = mGsmService.getData();
        long start_time = Calendar.getInstance().getTimeInMillis();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end_time = Calendar.getInstance().getTimeInMillis();
        GsmData dest = mGsmService.getData();
        //Temporary logs
        Log.e("Time: ",""+(end_time-start_time));
        Log.e("Source",""+source.toString());
        Log.e("Destination",""+dest.toString());
        double dist= getDistance(source, dest);
        return (dist/time);
    }

}
