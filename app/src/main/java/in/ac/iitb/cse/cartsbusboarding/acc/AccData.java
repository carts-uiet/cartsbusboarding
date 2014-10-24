package in.ac.iitb.cse.cartsbusboarding.acc;

/**
 * Created by chaudhary on 10/23/14.
 */
public class AccData {
    /*
     * Data Encapsulation
     */
    float x;
    float y;
    float z;

    @Override
    public String toString() {
        String base = "(";
        base += getX()+", ";
        base += getY()+", ";
        base += getZ();
        base += ")";
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
