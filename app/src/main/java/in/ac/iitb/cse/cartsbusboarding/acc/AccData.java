package in.ac.iitb.cse.cartsbusboarding.acc;

/**
 * Created by chaudhary on 10/23/14.
 */
public class AccData {
    /*
     * Data Encapsulation
     */
    public float x;
    public float y;
    public float z;

    @Override
    public String toString() {
        String base = "(";
        base += getX() + ", ";
        base += getY() + ", ";
        base += getZ();
        base += ")";
        return base;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

}
