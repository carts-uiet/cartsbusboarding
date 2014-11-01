package in.ac.iitb.cse.cartsbusboarding.acc;

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.Arrays;
import java.util.Queue;

/**
 * Created by chaudhary on 10/30/14.
 */
public class FeatureCalculator {
    Queue<AccData> currentBuffer;

    public FeatureCalculator(AccEngine accEngine) {
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
     * Get the DC component of data in mainBuffer
     *
     * @return DC component i.e. mean of resulting values of fft transform
     */
    public double getDCComponent() {
        return calculateDCComponent(applyFFT(bufferArrayAbsAcc()));
    }

    /**
     * Get the energy value of data in mainBuffer
     *
     * @return energy value
     */
    public double getEnergy() {
        return calculateEnergy(applyFFT(bufferArrayAbsAcc()));
    }


    /**
     * Get the entropy value of data in mainBuffer
     *
     * @return entropy value
     */
    public double getEntropy() {
        return calculateEntropy(applyFFT(bufferArrayAbsAcc()));
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

    /**
     * @param input
     * @return DC component i.e. the first component of fft transform output
     */
    private double calculateDCComponent(double input[]){
        return input[0];
    }

    /**
     * Calculates energy of data array
     * @param input
     * @return energy value
     */
    private double calculateEnergy(double input[]){
        double sum = 0;
//        TODO: why consider first element it is only mean of other elements
//        input[0] = 0;
        for ( double input_value : input){
            sum += input_value*input_value;
        }
        return (sum/(input.length));
    }

    /**
     * Calculates entropy i.e. mean of difference in result of fft
     * @param input
     * @return entropy value
     */
    private double calculateEntropy(double input[]){
        double diff = 0;
        for (int i = 0; i < input.length-1; i++) {
            diff += (input[i] - input[i+1]);
        }
        return diff/(input.length);
    }

    /**
     * Applies fft on data given
     * @param input
     * @return absolute values after applying fft
     */
    private double[] applyFFT(double input[]){

        //fft works on data length = some power of two
        int fftLength;
        int length = input.length;  //initialized with input's length
        int power = 0;
        while (true){
            int powOfTwo = (int)Math.pow(2,power);  //maximum no. of values to be applied fft on

            if( powOfTwo == length){
                fftLength = powOfTwo;
                break;
            }
            if(powOfTwo > length ){
                fftLength = (int) Math.pow(2, (power-1));
                break;
            }
            power++;
        }

        double[] tempInput = Arrays.copyOf(input, fftLength);
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        //apply fft on input
        Complex[] complexTransInput = fft.transform(tempInput, TransformType.FORWARD);

        for (int i = 0; i < complexTransInput.length; i++) {
            double real = (complexTransInput[i].getReal());
            double img = (complexTransInput[i].getImaginary());

            tempInput[i] = Math.sqrt((real * real) + (img * img));
        }

        return tempInput;
    }
}
