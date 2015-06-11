/**
 *   CartsBusBoarding - Bus Boarding Event detection project by
 *                      CARTS in IITB & UIET, Panjab University
 *
 *   Copyright (c) 2014 Shubham Chaudhary <me@shubhamchaudhary.in>
 *   Copyright (c) 2014 Tanjot Kaur <tanjot28@gmail.com>
 *
 *   This file is part of CartsBusBoarding.
 *
 *   CartsBusBoarding is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CartsBusBoarding is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CartsBusBoarding.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import in.ac.iitb.cse.cartsbusboarding.common.Engine;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import java.util.Calendar;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGE;

public class GsmEngine implements Engine {
    public static final String TAG = LogUtils.makeLogTag(GsmEngine.class);
    private final Context mContext;
    private GsmService mGsmService;
    private GsmData mData;
    private ServiceConnection mServiceConnection;
    private GsmData mSavedSource, mSavedDestination;
    private long mSavedStartTime, mSavedEndTime;

    public GsmEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, GsmService.class));

        mSavedSource = mSavedDestination = null;
        initServiceConnection();
    }

    private void initServiceConnection() {
        mServiceConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    mGsmService = ((GsmService.LocalBinder) service).getService();
                } catch (Throwable t) {
                    LOGE(TAG, "mServiceConnection.onServiceConnected() -> " + t);
                }
            }// onServiceConnected()

            public void onServiceDisconnected(ComponentName className) {
                mGsmService = null;
            }// onServiceDisconnected()
        };

        mContext.bindService(new Intent(mContext, GsmService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public GsmData getCurrentData() {
        mData = mGsmService.getCurrentData();
        return mData;
    }

    public boolean hasSpeed() {
        return mGsmService.hasSpeed();
    }

    public float getSpeed() {
        return mGsmService.getSpeed();
    }

    /**
     * Get distance b/w two points namely source and destination
     * @param src
     * @param dest
     * @return double distance value
     */
    public double getDistance(GsmData src, GsmData dest) {
        Location locationA = new Location("point A");

        locationA.setLatitude(src.getGsmLat());
        locationA.setLongitude(src.getGsmLong());

        Location locationB = new Location("point B");

        locationB.setLatitude(dest.getGsmLat());
        locationB.setLongitude(dest.getGsmLong());

        float distance = locationA.distanceTo(locationB);
        return ((double) distance);
    }

    /**
     *
     * @param time (in ms)
     * @return
     */
    public double getSpeed(long time) {
        GsmData source = mGsmService.getCurrentData();
        long start_time = Calendar.getInstance().getTimeInMillis();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end_time = Calendar.getInstance().getTimeInMillis();
        GsmData dest = mGsmService.getCurrentData();

        double dist = getDistance(source, dest);

        // Temporary logs
        LOGE(TAG, "Time: " + (end_time - start_time));
        LOGE(TAG, "Source" + mSavedSource.toString());
        LOGE(TAG, "Destination" + mSavedDestination.toString());
        return (dist / time);
    }

    public double myGetSpeed() {
        if (mSavedDestination == null) {
            mSavedSource = mGsmService.getCurrentData();
            mSavedStartTime = Calendar.getInstance().getTimeInMillis();
        } else {
            mSavedSource = mSavedDestination;
            mSavedStartTime = mSavedEndTime;
        }
        mSavedDestination = mGsmService.getCurrentData();
        mSavedEndTime = Calendar.getInstance().getTimeInMillis();
        double dist = getDistance(mSavedSource, mSavedDestination);

        // Temporary logs
        LOGE(TAG, "Source" + mSavedSource.toString());
        LOGE(TAG, "Destination" + mSavedDestination.toString());
        long time = mSavedEndTime - mSavedStartTime;
        return (dist / time);
    }
}
