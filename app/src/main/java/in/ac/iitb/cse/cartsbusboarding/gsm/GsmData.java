package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.location.Location;

/**
 * Created by chaudhary on 10/23/14.
 */
public class GsmData {
    /*
     * Data Encapsulation
     */
    public Location location;
    /** Get the latitude, in degrees. */
    public double gsmLat;
    /** Get the longitude, in degrees. */
    public double gsmLong;
    /** Get the estimated accuracy of this location, in meters. */
    public float gsmAccuracy;

    @Override
    public String toString() {
        String base = "GSM Data: (";
        base += gsmLat + ", ";
        base += gsmLong + ", ";
        base += gsmAccuracy;
        base += ")";
        return base;
    }

    /* Getters */
    public double getGsmLat() {
        return gsmLat;
    }

    public double getGsmLong() {
        return gsmLong;
    }

    public float getGsmAccuracy() {
        return gsmAccuracy;
    }

}
