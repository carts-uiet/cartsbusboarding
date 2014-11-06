package in.ac.iitb.cse.cartsbusboarding.acc;

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
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
     * Calculates mean
     * @param data whose mean is to be calculated
     * @return mean of data provided
     */
    public double getMean(ArrayList<AccData> data) {
        return calculateMean(bufferArrayAbsAcc(data));
    }

    /**
     * Calculates mean on windows of currentBuffer
     * @param windowSize
     * @return array of means on currentBuffer data divided into windows
     */
    public double[] getMean(int windowSize) {
        ArrayList data = new ArrayList(currentBuffer);
        int noOfWindows = data.size()/windowSize;
        double[] windowMeans = new double[noOfWindows+1];
        int index = 0;
        int startIndex = 0;
        int endIndex = 0;

        /*Loop will only read upto windows in multiples of windowSize(Last window with values less
          than windowSize is delt in next if condition)
        */
        Log.d("MEAN ARRAY: ","");
        while(endIndex < (noOfWindows*windowSize) ) {
            startIndex = endIndex;
            endIndex += windowSize;
            ArrayList<AccData> temp = new ArrayList(data.subList(startIndex, endIndex));
            windowMeans[index++] = getMean(temp);
            Log.d("mean"+(index-1),""+windowMeans[index-1]);
        }
        if (endIndex < data.size()){
            startIndex = endIndex;
            ArrayList<AccData> temp= new ArrayList(data.subList(startIndex, data.size()));
            windowMeans[index] = getMean(temp);
            Log.d("mean"+(index),""+windowMeans[index]);
        }

        return windowMeans;
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
     * Calculates Standard Deviation
     * @param data whose std is to be calculated
     * @return std of data provided
     */
    public double getStd(ArrayList<AccData> data) {
        return calculateStd(bufferArrayAbsAcc(data));
    }


    /**
     *Calculates Standard Deviation on windows of currentBuffer
     * @param windowSize
     * @return array of std on currentBuffer data divided into windows
     */
    public double[] getStd(int windowSize) {
        ArrayList data = new ArrayList(currentBuffer);
        int noOfWindows = data.size()/windowSize;
        double[] windowStd = new double[noOfWindows+1];
        int index = 0;
        int startIndex = 0;
        int endIndex = 0;

        /*Loop will only read upto windows in multiples of windowSize(Last window with values less
          than windowSize is delt in next if condition)
        */
        Log.d("STD ARRAY: ","");
        while(endIndex < (noOfWindows*windowSize) ) {
            startIndex = endIndex;
            endIndex += windowSize;
            ArrayList<AccData> temp = new ArrayList(data.subList(startIndex, endIndex));
            windowStd[index++] = getStd(temp);
            Log.d("std"+(index-1),""+windowStd[index-1]);
        }
        if (endIndex < data.size()){
            startIndex = endIndex;
            ArrayList<AccData> temp= new ArrayList(data.subList(startIndex, data.size()));
            windowStd[index] = getStd(temp);
            Log.d("std"+(index),""+windowStd[index]);
        }

        return windowStd;
    }

    /**
     * Get the DC component of data in mainBuffer
     *
     * @return dc comp.from mainBuffer
     */
    public double getDCComponent() {
        return calculateDCComponent(applyFFT(bufferArrayAbsAcc()));
    }

    /**
     * Calculates dc comp.
     * @param data whose dc comp. is to be calculated
     * @return dc comp of data provided
     */
    public double getDCComponent(ArrayList<AccData> data) {
        return calculateDCComponent(applyFFT(bufferArrayAbsAcc(data)));
    }

    /**
     * finds dcComponent for windows of currentBuffer
     * @param windowSize
     * @return array of dcComp on currentBuffer data divided into windows
     */
    public double[] getDCComponent(int windowSize) {
        ArrayList data = new ArrayList(currentBuffer);
        int noOfWindows = data.size()/windowSize;
        double[] windowdc = new double[noOfWindows+1];
        int index = 0;
        int startIndex = 0;
        int endIndex = 0;

        /*Loop will only read upto windows in multiples of windowSize(Last window with values less
          than windowSize is delt in next if condition)
        */
        Log.d("DC Comp. ARRAY: ","");
        while(endIndex < (noOfWindows*windowSize) ) {
            startIndex = endIndex;
            endIndex += windowSize;
            ArrayList<AccData> temp = new ArrayList(data.subList(startIndex, endIndex));
            windowdc[index++] = getDCComponent(temp);
            Log.d("dc comp"+(index-1),""+windowdc[index-1]);
        }
        if (endIndex < data.size()){
            startIndex = endIndex;
            ArrayList<AccData> temp= new ArrayList(data.subList(startIndex, data.size()));
            windowdc[index] = getDCComponent(temp);
            Log.d("dc comp"+(index),""+windowdc[index]);
        }

        return windowdc;
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

    /**
     * Overloaded function on which operations are to be performed
     * @param data is array on which o
     * @return
     */
    public synchronized double[] bufferArrayAbsAcc(ArrayList<AccData> data){
        double bufferAbsAcc[] = new double[data.size()];

        int index = 0;
        for (AccData currData : data) {
            bufferAbsAcc[index++] = Math.sqrt(
                    Math.pow(currData.getX(), 2)
                            + Math.pow(currData.getY(), 2)
                            + Math.pow(currData.getZ(), 2)
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
        //First element is the DC component
        input[0] = 0;
        for ( double input_value : input){
            sum += input_value*input_value;
        }
        //TODO: returns NaN if input has only one element
        return (sum/(input.length-1));
    }

    /**
     * Calculates entropy i.e. mean of difference in result of fft
     * @param input
     * @return entropy value
     */
    private double calculateEntropy(double input[]){
        double diff = 0;

        //Here,if there is no or only one value in input, difference returned is zero
        if(input.length == 0){
            return 0;
        }
        if(input.length == 1){
            return input[0];
        }
        //TODO: not sure what to return
        if(input.length == 2){
            return input[1];
        }

        //Ignored first element because it is the DC component
        for (int i = 1; i < input.length-1; i++) {
            diff += (input[i+1] - input[i]);
            Log.e("diff",""+diff);
        }
        return diff/(input.length-1);
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
