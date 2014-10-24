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
    private ServiceConnection mServiceConnection;
    AccService mAccService;
    Context mContext;
    AccData data;
    int queueSize;

    public AccEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, AccService.class));
        Log.e("Engine","Acc");
        initServiceConnection();
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
    public Queue[] getData(int noOfReadings){
        Queue queueX = null;
        Queue queueY = null;
        Queue queueZ = null;

        queueSize = 0;
        queueX = new LinkedList();
        queueY = new LinkedList();
        queueZ = new LinkedList();

//        int outOfLoop = 100 + noOfReadings;

        for(int i=0; i<noOfReadings; i++){
            if(getData() != null){
                queueX.add(data.getX());
                queueY.add(data.getY());
                queueZ.add(data.getZ());
                queueSize++;
            }else{
                i--;
            }

            int sleepTime = 10000;//unit is milliseconds
            try{
                Log.e("sleep","zzzzzz...");
                Thread.sleep(sleepTime);
            }catch (Exception e){
                Log.e("Interrupted error: ",""+e);
            }

//            TODO:check for infinte loop i.e. if all values obtained are null
        }
        Queue queue[] = null;

        if(queueSize > 0){
            queue = new Queue[3];
            queue[0] = queueX;
            queue[1] = queueY;
            queue[2] = queueZ;
        }
        return  queue;
    }

    public float[] mean(int noOfReadings){
        Queue queue[] = getData(noOfReadings);

        if(queue == null) return(null);

        float sum[] = new float[3];

        while(!queue[0].isEmpty()){ //assumed that size of x, y, z are same
            sum[0] += Float.parseFloat((queue[0].remove()).toString());
            sum[1] += Float.parseFloat((queue[1].remove()).toString());
            sum[2] += Float.parseFloat((queue[2].remove()).toString());
        }

        float mean[] = new float[3];
        mean[0] = sum[0]/queueSize;
        mean[1] = sum[1]/queueSize;
        mean[2] = sum[2]/queueSize;
        Log.e("mean","x:"+mean[0]+",y:"+mean[1]+",z:"+mean[2]);
        return  mean;
    }
}
