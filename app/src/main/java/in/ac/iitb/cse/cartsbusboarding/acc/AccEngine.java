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
    int queueSize;
    Queue queue[];
    int noOfReadings;

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

    public Queue getDataList(){
            return mAccService.getDataList();

    }

    public double[] mean(){
        Queue queue = getDataList();

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

    public int getQueueSize(){
        return mAccService.getQueueSize();
    }
}
