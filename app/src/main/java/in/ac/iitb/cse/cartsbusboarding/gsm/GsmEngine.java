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
import android.util.Log;

import java.util.Calendar;

/**
 * Created by chaudhary on 10/23/14.
 */
public class GsmEngine {
    private final Context mContext;
    private GsmService mGsmService;
    private GsmData data;
    private ServiceConnection mServiceConnection;
    private GsmData saved_source, saved_destination;
    private long saved_startTime, saved_endTime;

    public GsmEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, GsmService.class));

        saved_source = saved_destination = null;
        initServiceConnection();
    }

    private void initServiceConnection() {
        mServiceConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    mGsmService = ((GsmService.LocalBinder) service).getService();
                } catch (Throwable t) {
                    Log.e("GsmEngine", "mServiceConnection.onServiceConnected() -> " + t);
                }
            }// onServiceConnected()

            public void onServiceDisconnected(ComponentName className) {
                mGsmService = null;
            }// onServiceDisconnected()
        };

        mContext.bindService(new Intent(mContext, GsmService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public GsmData getData() {
        data = mGsmService.getData();
        return data;
    }

    public boolean hasSpeed(){
        return mGsmService.hasSpeed();
    }
    public float getSpeed(){
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
    public double getSpeed(long time){
        GsmData source = mGsmService.getData();
        long start_time = Calendar.getInstance().getTimeInMillis();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end_time = Calendar.getInstance().getTimeInMillis();
        GsmData dest = mGsmService.getData();

        double dist= getDistance(source, dest);

        // Temporary logs
        Log.e("Time: ",""+(end_time-start_time));
        Log.e("Source",""+saved_source.toString());
        Log.e("Destination",""+saved_destination.toString());
        return (dist/time);
    }

    public double myGetSpeed(){
        if(saved_destination == null){
            saved_source = mGsmService.getData();
            saved_startTime = Calendar.getInstance().getTimeInMillis();
        }else{
            saved_source = saved_destination;
            saved_startTime = saved_endTime;
        }
        saved_destination = mGsmService.getData();
        saved_endTime = Calendar.getInstance().getTimeInMillis();
        double dist= getDistance(saved_source, saved_destination);

        // Temporary logs
        Log.e("Source",""+saved_source.toString());
        Log.e("Destination",""+saved_destination.toString());
        long time = saved_endTime - saved_startTime;
        return (dist/time);
    }
}
