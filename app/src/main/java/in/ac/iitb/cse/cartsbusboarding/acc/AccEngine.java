/**
 * CartsBusBoarding - Bus Boarding Event detection project by
 * CARTS in IITB & UIET, Panjab University
 * <p/>
 * Copyright (c) 2014 Shubham Chaudhary <me@shubhamchaudhary.in>
 * Copyright (c) 2014 Tanjot Kaur <tanjot28@gmail.com>
 * <p/>
 * This file is part of CartsBusBoarding.
 * <p/>
 * CartsBusBoarding is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * CartsBusBoarding is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with CartsBusBoarding.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.ac.iitb.cse.cartsbusboarding.acc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import in.ac.iitb.cse.cartsbusboarding.common.Engine;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGE;
import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGV;

public class AccEngine implements Engine {
    private static final String TAG = LogUtils.makeLogTag(AccEngine.class);
    private static final int BUFFER_SIZE = 1000;
    private static final long LISTENER_POLLING_TIME = 500;
    @Inject Context mContext;
    private AccData mData;
    private AccService mAccService;
    private EngineFillerThread engineFillerThread;
    private ConcurrentLinkedQueue<AccData> mMainBuffer;
    private ServiceConnection mServiceConnection;

    /**
     * Default constructor
     *
     * @param context needed to start the service
     */
    @Inject
    public AccEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, AccService.class));
        LOGE(TAG, "AccEngine starting");
        initServiceConnection();
        /** Started from here bcoz it will work only after service has started */
        startEngineFiller();
        mMainBuffer = new ConcurrentLinkedQueue<AccData>();
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
                    LOGE(TAG, "mServiceConnection.onServiceConnected() -> " + t);
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
     * Return most recent acceleration value
     *
     * @return most recent acceleration value as AccData
     */
    @Override
    public AccData getCurrentData() {
        mData = mAccService.getCurrentData();
        return mData;
    }


    public Queue<AccData> getMainBuffer() {
        Queue<AccData> output = new LinkedList<AccData>();
        for (AccData data : mMainBuffer)
            output.add(data);
        return output;
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
//                mAccService.getDataList();    //Clears localBuffer of Listener
                ConcurrentLinkedQueue<AccData> localDataQueue = new ConcurrentLinkedQueue<AccData>(mAccService.getDataList());
                // If mainBuffer is not of the desired size
                while (!(localDataQueue.isEmpty())) {
                    if (mMainBuffer.size() < BUFFER_SIZE) {
                        LOGV(TAG, "New Value: " + localDataQueue.peek());
                        //mMainBuffer.add(localDataQueue.remove());
                        mMainBuffer.offer(localDataQueue.remove());
                    } else {
                        // If mainBuffer is full
                        mMainBuffer.remove();
                        //mMainBuffer.add(localDataQueue.remove());
                        mMainBuffer.offer(localDataQueue.remove());
                    }
                }//end while
                //Log.d(TAG, "Thread Data: " + mMainBuffer.size());
                try {
                    Thread.sleep(LISTENER_POLLING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
