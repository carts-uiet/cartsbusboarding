package in.ac.iitb.cse.cartsbusboarding.acc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

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
                  /*Started from here bcoz it will work only after service has started*/
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

        /**
         * Calculates mean of data in mainBuffer
         * @return
         */
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
}
