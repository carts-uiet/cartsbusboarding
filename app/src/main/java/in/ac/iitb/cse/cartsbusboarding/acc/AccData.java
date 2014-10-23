package in.ac.iitb.cse.cartsbusboarding.acc;

/**
 * Created by chaudhary on 10/23/14.
 */
public class AccData {
    /*
     * Data Encapsulation
     */
    float x,y,z;
    @Override
    public String toString() {
        String base = "ACC Data: ";
        base += getX()+",";
        base += getY()+",";
        base += getZ();
        return base;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
