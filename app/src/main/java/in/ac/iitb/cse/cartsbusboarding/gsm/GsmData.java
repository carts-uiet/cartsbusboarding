package in.ac.iitb.cse.cartsbusboarding.gsm;

/**
 * Created by chaudhary on 10/23/14.
 */
public class GsmData {
    /*
     * Data Encapsulation
     */
    public double gsmLat;
    public double gsmLong;
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
