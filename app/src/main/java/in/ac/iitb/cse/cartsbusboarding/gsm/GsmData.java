package in.ac.iitb.cse.cartsbusboarding.gsm;

/**
 * Created by chaudhary on 10/23/14.
 */
public class GsmData {
    /*
     * Data Encapsulation
     */
    double gsmLat;
    double gsmLong;
    float gsmAccuracy;

    @Override
    public String toString() {
        String base = "GSM Data: ";
        base += getGsmLat();
        base += getGsmLong();
        base += getGsmAccuracy();
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
