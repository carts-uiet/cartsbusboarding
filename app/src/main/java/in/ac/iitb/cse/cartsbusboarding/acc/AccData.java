package in.ac.iitb.cse.cartsbusboarding.acc;

/**
 * Created by chaudhary on 10/23/14.
 */
public class AccData {
    /*
     * Data Encapsulation
     */
    float x,y,z;

    /*Temp adjustment*/
    final static int MEAN = 1;
    final static int MODE = 2;
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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

}
