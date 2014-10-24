package in.ac.iitb.cse.cartsbusboarding.acc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chaudhary on 10/23/14.
 */
public class AccEngine{
    private ServiceConnection mServiceConnection;
    AccService mAccService;
    Context mContext;
    AccData data;
    Queue mainBuffer;
    int bufferSize = 60;
    long listenerPollingTime = 1000;
    EngineFillerThread engineFillerThread;

    /**
     * Default constructor
     * @param context needed to start the service
     */
    public AccEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, AccService.class));
        Log.e("Engine", "Acc");
        initServiceConnection();
        mainBuffer = new LinkedList();

    }

    /**
     * Creates a initial connection of engine with service
     * so that the service is accessible via mAccService
     */
    private void initServiceConnection() {
        mServiceConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    mAccService = ((AccService.LocalBinder) service).getService();
                  /*Start here bcoz it will work only after service has started*/
                    startEngineFiller();
                } catch (Throwable t) {
                    Log.e("AccEngine", "mServiceConnection.onServiceConnected() -> " + t);
                }
            }// onServiceConnected()

            public void onServiceDisconnected(ComponentName className) {
                mAccService = null;
            }// onServiceDisconnected()
        };

        mContext.bindService(new Intent(mContext, AccService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Starts thread for collecting data
     */
    public void startEngineFiller(){
        engineFillerThread = new EngineFillerThread(listenerPollingTime);
        new Thread(engineFillerThread).start();
    }

    /**
     * EngineFillerThread fills data in mainBuffer
     */
    class EngineFillerThread implements Runnable{
        long startTime;
        long specReadingTime;//Time in ms for which to read data
        long listnerPollingTime;//Time in ms to sleep

        /**
         * EngineFillerThread fills data in mainBuffer
         * @param listenerPollingTime Sleep duration of listener read
         */
        EngineFillerThread(long listenerPollingTime){
            this.listnerPollingTime = listenerPollingTime;
        }

        @Override
        public void run() {

            //Keeps running till the time app runs
            while( true ){
                Queue queue = mAccService.getDataList();    //Clears localBuffer of Listener
                if(!queue.isEmpty()){
                    while( (mainBuffer.size() < bufferSize) && !(queue.isEmpty())){
                        Log.e("peek",""+queue.peek());

                        mainBuffer.add(queue.remove());

                    }
                    while (!queue.isEmpty()){
                        mainBuffer.remove();
                        mainBuffer.add(queue.remove());
                    }

                }
                Log.e("in thread mean: ",""+calculateMean());
                Log.e("in thread",""+mainBuffer);
                try {
                    Thread.sleep(listnerPollingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private synchronized double calculateMean(){

            //temp Queue to store buffer values because we will have to remove these values
            Queue temp = new LinkedList();
            temp.addAll(mainBuffer);

                    /*Applying mean on data in buffer*/
            int size = mainBuffer.size();
            double bufferValues[] = new double[size];
            int index = 0;
            while (index < size){
                AccData accData = new AccData();
                accData = (AccData) temp.remove();
                bufferValues[index++] = Math.sqrt(Math.pow(accData.getX(), 2) + Math.pow(accData.getY(), 2) + Math.pow(accData.getZ(), 2));
            }
            Mean mean = new Mean();
            double meanBuffer = mean.evaluate(bufferValues);

            return meanBuffer;
        }
        public double getMean(){
            double meanBuffer = calculateMean();
            return meanBuffer;
        }
    }

    /**
     * Return most recent acceleration value
     * @return most recent acceleration value as AccData
     */
    public AccData getData() {
        data = mAccService.getData();
        Log.e("data","x"+data.getX()+",y"+data.getY()+",z"+data.getZ());
        return data;
    }

    /**
     * Returns mean of data in mainBuffer
     */
    public double getMean(){
        return engineFillerThread.getMean();
    }
//    /**
//     * TODO: Documentation
//     * @return
//     */
//    public Queue getDataList(){
//            return mAccService.getDataList();
//    }
////
////    public Queue getDataList(long specReadingTime, long listnerPollingTime){
////        /*new EngineFillerThread(specReadingTime,listnerPollingTime).run(); would run the functions called from thread in main thread itself*/
////        Thread t = new Thread(new EngineFillerThread(specReadingTime,listnerPollingTime));
////        t.start();
////        try {
////            t.join();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        Log.e("Acc Readings ",""+ mainBuffer);
////
////        return mainBuffer;
////    }
//
//    /**
//     * Separates the mainBuffer data into array list from accdata
//     * @return
//     */
//    public  ArrayList separateXYZ(){
//        int dataArrayIndex = 0;
//        int size = mainBuffer.size();
//        Queue temp = mainBuffer;
//        /*double bcoz MEAN and Median use double data type*/
//        double dataArrayX[] = new double[size];
//        double dataArrayY[] = new double[size];
//        double dataArrayZ[] = new double[size];
//
//        while( !mainBuffer.isEmpty()){
//            AccData data = (AccData) temp.remove();   //One value at a time
//            dataArrayX[dataArrayIndex] = data.getX();
//            dataArrayY[dataArrayIndex] = data.getY();
//            dataArrayZ[dataArrayIndex] = data.getZ();
//            dataArrayIndex++;
//        }
//        ArrayList al = new ArrayList(3);
//        al.add(dataArrayX);
//        al.add(dataArrayY);
//        al.add(dataArrayZ);
//        return al;
//    }
//
//    /**
//     * Mean for main
//     * @param windowSize
//     * @return
//     */
//    public ArrayList getMean(int windowSize){
//        if (mainBuffer == null) return null;
//
//        Queue temp_accReadings = mainBuffer;
//        ArrayList meanList = null;
//
//        meanList = new ArrayList();
//        Mean mean = new Mean();
//
//        while(!mainBuffer.isEmpty()){
//            ArrayList xyz = separateXYZ(windowSize);
//
//            AccData meanData = new AccData();
//            meanData.setX((float) mean.evaluate((double[]) xyz.get(0)));
//            meanData.setY((float) mean.evaluate((double[]) xyz.get(1)));
//            meanData.setZ((float) mean.evaluate((double[]) xyz.get(2)));
//
//            meanList.add(meanData);
//            Log.e("MEAN",""+"x"+meanData.getX()+",y"+meanData.getY()+",z"+meanData.getZ());
//
//        }
//
//        mainBuffer = temp_accReadings;
//        return meanList;
//    }
//
//    /**
//     * FIXME: Probably will be deleted
//     * @param windowSize
//     * @param operation_list
//     * @return
//     */
//    public ArrayList operations(int windowSize, ArrayList operation_list){//windowSize is no of values to be taken in a window
//
//        Queue temp_accReadings = mainBuffer;
//        boolean findMean = false;
//        ArrayList meanList = null;
//        boolean findMedian = false;
//        ArrayList medianList = null;
//
//        while(!mainBuffer.isEmpty()){
//            ArrayList xyz = separateXYZ(windowSize);
//
//            for(int j=0; j<operation_list.size(); j++){
//
//                int operation = Integer.parseInt(""+operation_list.get(j));
//
//                switch (operation){
//                    case AccData.MEAN:
//                        if (!findMean){
//                            findMean = true;
//                            meanList = new ArrayList();
//                        }
//
//                        Mean mean = new Mean();
//                        AccData meanData = new AccData();
//                        meanData.setX((float) mean.evaluate((double[]) xyz.get(0)));
//                        meanData.setY((float) mean.evaluate((double[]) xyz.get(1)));
//                        meanData.setZ((float) mean.evaluate((double[]) xyz.get(2)));
//
//                        meanList.add(meanData);
//                        Log.e("MEAN",""+"x"+meanData.getX()+",y"+meanData.getY()+",z"+meanData.getZ());
//                        break;
//
//                    case AccData.MODE:
//                        if (!findMedian){
//                            findMedian = true;
//                            medianList = new ArrayList();
//                        }
//
//                        Median median = new Median();
//                        AccData medianData = new AccData();
//                        medianData.setX((float) median .evaluate((double[]) xyz.get(0)));
//                        medianData.setY((float) median .evaluate((double[]) xyz.get(1)));
//                        medianData.setZ((float) median .evaluate((double[]) xyz.get(2)));
//
//                        medianList.add(medianData);
//
//                        Log.e("MEDIAN",""+"x"+medianData.getX()+",y"+medianData.getY()+",z"+medianData.getZ());
//                        break;
//                }
//            }
//        }
//
//        ArrayList retList = new ArrayList();
//        if(findMean){
//            retList.add(meanList);
//        }
//        if (findMedian){
//            retList.add(meanList);
//        }
//        mainBuffer = temp_accReadings;
//        return retList;
//    }
}
