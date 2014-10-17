package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by chaudhary on 10/17/14.
 */
public class GsmListner implements LocationListener {
    double GSMLat, GSMLong;
    float GSMAccuracy;

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
        // TODO Auto-generated method stub
        GSMLat = location.getLatitude();
        GSMLong = location.getLongitude();
        GSMAccuracy = location.getAccuracy();
    }
}
