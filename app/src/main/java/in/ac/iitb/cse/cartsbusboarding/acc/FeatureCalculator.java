package in.ac.iitb.cse.cartsbusboarding.acc;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.Queue;

/**
 * Created by chaudhary on 10/30/14.
 */
public class FeatureCalculator {
    private AccEngine accEngine;
    Queue<AccData> currentBuffer;

    public FeatureCalculator(AccEngine accEngine) {
        this.accEngine = accEngine;
        currentBuffer = accEngine.getMainBuffer();
    }


    /**
     * Get the mean of data in mainBuffer
     *
     * @return mean from mainBuffer
     */
    public double getMean() {
        return calculateMean(bufferArrayAbsAcc());
    }

    /**
     * Get the Standard Deviation of data in mainBuffer
     *
     * @return StD from mainBuffer
     */
    public double getStd() {
        return calculateStd(bufferArrayAbsAcc());
    }


    /**
     * Stores the absolute acceleration(x,y,z) of current buffer
     * values in a double array
     * synchronized to sync threads(both access mainBuffer)
     * @return double array of absolute acc of data in buffer
     */
    public synchronized double[] bufferArrayAbsAcc(){
        double bufferAbsAcc[] = new double[currentBuffer.size()];

        int index = 0;
        for (AccData data : currentBuffer) {
            bufferAbsAcc[index++] = Math.sqrt(
                    Math.pow(data.getX(), 2)
                            + Math.pow(data.getY(), 2)
                            + Math.pow(data.getZ(), 2)
            );
        }
        return bufferAbsAcc;
    }


/* Generic Functions */

    /**
     * Calculates mean of whatever data is given to this function
     * @return
     */
    private double calculateMean(double input[]) {
        return ((new Mean()).evaluate(input));
    }

    /**
     * Calculates StDev of whatever data is given to this function
     * @return
     */
    private double calculateStd(double input[]) {
        return (new StandardDeviation()).evaluate(input);
    }


}
