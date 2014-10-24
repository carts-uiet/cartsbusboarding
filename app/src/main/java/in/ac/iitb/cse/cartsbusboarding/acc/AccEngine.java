package in.ac.iitb.cse.cartsbusboarding.acc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.AvoidXfermode;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import in.ac.iitb.cse.cartsbusboarding.MainActivity;

/**
 * Created by chaudhary on 10/23/14.
 */
public class AccEngine{
    private ServiceConnection mServiceConnection;
    AccService mAccService;
    Context mContext;
    AccData data;
    Queue accReadings;

    public AccEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, AccService.class));
        Log.e("Engine","Acc");
        initServiceConnection();
        accReadings = new LinkedList();
    }

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

    public AccData getData() {
        data = mAccService.getData();
        Log.e("data","x"+data.getX()+",y"+data.getY()+",z"+data.getZ());
        return data;
    }

    public Queue getDataList(){
            return mAccService.getDataList();
    }

    public Queue getDataList(long specReadingTime, long listnerPollingTime){
        /*new GetDataThread(specReadingTime,listnerPollingTime).run(); would run the functions called from thread in main thread itself*/
        Thread t = new Thread(new GetDataThread(specReadingTime,listnerPollingTime));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("Acc Readings ",""+accReadings);

        return accReadings;
    }

    public double[] mean(Queue queue){

        queue = getDataList();

        if(queue == null) return(null);

        double sum[] = new double[3];

        while(!queue.isEmpty()){ //assumed that size of x, y, z are same
            data = (AccData) queue.remove();
            Log.e("data","x"+data.getX()+",y"+data.getY()+",z"+data.getZ()+"sum"+sum[0]);

            sum[0] += data.getX();
            sum[1] += data.getY();
            sum[2] += data.getZ();
        }

        double mean[] = new double[3];

        int size = getQueueSize();
        mean[0] = sum[0]/size;
        mean[1] = sum[1]/size;
        mean[2] = sum[2]/size;
        Log.e("mean","x:"+mean[0]+",y:"+mean[1]+",z:"+mean[2]);
        return  mean;
    }

    public  ArrayList separateXYZ(int windowSize){
        if(accReadings.isEmpty()) return null;

        int windowCount = 0;
        int dataArrayIndex = 0;
        /*double bcoz MEAN and Median use double data type*/
        double dataArrayX[] = new double[windowSize];
        double dataArrayY[] = new double[windowSize];
        double dataArrayZ[] = new double[windowSize];

        while( (windowCount < windowSize) && !accReadings.isEmpty()){
            AccData data = (AccData) accReadings.remove();
            dataArrayX[dataArrayIndex] = data.getX();
            dataArrayY[dataArrayIndex] = data.getY();
            dataArrayZ[dataArrayIndex] = data.getZ();
            windowCount++;
            dataArrayIndex++;
        }
        ArrayList al = new ArrayList(3);
        al.add(dataArrayX);
        al.add(dataArrayY);
        al.add(dataArrayZ);

        return al;
    }
    public ArrayList operations(int windowSize, ArrayList operation_list){//windowSize is no of values to be taken in a window

        Queue temp_accReadings = accReadings;
        boolean findMean = false;
        ArrayList meanList = null;
        boolean findMedian = false;
        ArrayList medianList = null;

        while(!accReadings.isEmpty()){
            ArrayList xyz = separateXYZ(windowSize);

            for(int j=0; j<operation_list.size(); j++){

                int operation = Integer.parseInt(""+operation_list.get(j));

                switch (operation){
                    case AccData.MEAN:
                        if (!findMean){
                            findMean = true;
                            meanList = new ArrayList();
                        }

                        Mean mean = new Mean();
                        AccData meanData = new AccData();
                        meanData.setX((float) mean.evaluate((double[]) xyz.get(0)));
                        meanData.setY((float) mean.evaluate((double[]) xyz.get(1)));
                        meanData.setZ((float) mean.evaluate((double[]) xyz.get(2)));

                        meanList.add(meanData);
                        Log.e("MEAN",""+"x"+meanData.getX()+",y"+meanData.getY()+",z"+meanData.getZ());
                        break;

                    case AccData.MODE:
                        if (!findMedian){
                            findMedian = true;
                            medianList = new ArrayList();
                        }

                        Median median = new Median();
                        AccData medianData = new AccData();
                        medianData.setX((float) median .evaluate((double[]) xyz.get(0)));
                        medianData.setY((float) median .evaluate((double[]) xyz.get(1)));
                        medianData.setZ((float) median .evaluate((double[]) xyz.get(2)));

                        medianList.add(medianData);

                        Log.e("MEDIAN",""+"x"+medianData.getX()+",y"+medianData.getY()+",z"+medianData.getZ());
                        break;
                }
            }
        }

        ArrayList retList = new ArrayList();
        if(findMean){
            retList.add(meanList);
        }
        if (findMedian){
            retList.add(meanList);
        }
        accReadings = temp_accReadings;
        return retList;
    }




    public int getQueueSize(){
        return mAccService.getQueueSize();
    }

    class GetDataThread implements Runnable{
        long startTime;
        long specReadingTime;//Time in ms for which to read data
        long listnerPollingTime;//Time in ms to sleep

        GetDataThread(long specReadingTime, long listnerPollingTime){
            this.specReadingTime = specReadingTime;
            this.listnerPollingTime = listnerPollingTime;

        }

        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            long endtime = startTime + specReadingTime;

            while( System.currentTimeMillis() < endtime ){
                Queue queue = mAccService.getDataList();

                if(!queue.isEmpty()){
                    accReadings.addAll(queue);
//                    Log.e("her","here");
//                    Log.e("Acc Readings in thread",""+queue);
//                    Log.e("Size in thread",""+mAccService.getQueueSize());

                }

                try {
                    Thread.sleep(listnerPollingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            Log.e("in thread","End");

        }


    }
}
