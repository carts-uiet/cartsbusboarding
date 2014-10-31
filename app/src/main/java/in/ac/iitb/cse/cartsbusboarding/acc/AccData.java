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

    public AccData() { }

    public AccData(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        String base = "(";
        base += x + ", ";
        base += y + ", ";
        base += z;
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
