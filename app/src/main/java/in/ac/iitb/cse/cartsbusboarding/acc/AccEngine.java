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
public class AccEngine {
    private static final String _ClassName = AccEngine.class.getSimpleName();
    private static final int bufferSize = 60;
    private static final long listenerPollingTime = 500;
    private final Context mContext;
    private AccData data;
    private AccService mAccService;
    private EngineFillerThread engineFillerThread;
    private Queue<AccData> mainBuffer;
    private ServiceConnection mServiceConnection;

    /**
     * Default constructor
     *
     * @param context needed to start the service
     */
    public AccEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, AccService.class));
        Log.e("Engine", "Acc");
        initServiceConnection();
        /** Started from here bcoz it will work only after service has started */
        startEngineFiller();
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
    private void startEngineFiller() {
        engineFillerThread = new EngineFillerThread();
        new Thread(engineFillerThread).start();
    }

    /**
     * Stores the absolute(x,y,z) of current buffer values in a double array
     * @return double array corresponding to data in buffer
     */
    public double[] bufferToArray(){
        double bufferArray[] = new double[mainBuffer.size()];

        int index = 0;
        for (AccData data : mainBuffer) {
            bufferArray[index++] = Math.sqrt(
                    Math.pow(data.getX(), 2)
                            + Math.pow(data.getY(), 2)
                            + Math.pow(data.getZ(), 2)
            );
        }
        return bufferArray;
    }

    /**
     * Get the mean of data in mainBuffer
     *
     * @return mean from mainBuffer
     */
    public double getMean() {
        return calculateMean();
    }

    /**
     * Calculates mean of data in mainBuffer
     * synchronized to sync threads(both access mainBuffer)
     * @return
     */
    private synchronized double calculateMean() {
        return (new Mean()).evaluate(bufferToArray());
    }



    /**
     * Return most recent acceleration value
     *
     * @return most recent acceleration value as AccData
     */
    public AccData getCurrentData() {
        data = mAccService.getCurrentData();
        Log.e("data", "x" + data.getX() + ",y" + data.getY() + ",z" + data.getZ());
        return data;
    }

    /**
     * EngineFillerThread fills data in mainBuffer
     */
    private class EngineFillerThread implements Runnable {
        @Override
        public void run() {

            //Keeps running till the time app runs
            while (true) {
                if (mAccService == null)
                    continue;
                Queue<AccData> localDataQueue = mAccService.getDataList();    //Clears localBuffer of Listener
                // If mainBuffer is not of the desired size
                while (!(localDataQueue.isEmpty())) {
                    if (mainBuffer.size() < bufferSize) {
                        Log.i(_ClassName, "New Value: " + localDataQueue.peek());
                        mainBuffer.add(localDataQueue.remove());
                    } else {
                        // If mainBuffer is full
                        mainBuffer.remove();
                        mainBuffer.add(localDataQueue.remove());
                    }
                }//end while
                Log.d(_ClassName, "Thread Data: " + mainBuffer.size());
                Log.d(_ClassName, "Thread Mean: " + calculateMean());
                try {
                    Thread.sleep(listenerPollingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
