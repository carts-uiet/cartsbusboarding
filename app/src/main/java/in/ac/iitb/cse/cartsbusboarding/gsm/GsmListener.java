package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by chaudhary on 10/17/14.
 */
public class GsmListener implements LocationListener {
    private GsmData data;

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {
        data = new GsmData();
        data.location = location;
        data.gsmLat = location.getLatitude();
        data.gsmLong = location.getLongitude();
        data.gsmAccuracy = location.getAccuracy();
    }

    public GsmData getData() {
        return data;
    }
}
