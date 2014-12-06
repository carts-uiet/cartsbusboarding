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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

public class GsmService extends Service {
    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    private LocationManager gsmMgr;
    private GsmListener gsmListener;
    private GsmData dataRead;

    @Override
    public void onCreate() {
        super.onCreate();
        gsmMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gsmListener = new GsmListener();

        gsmMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gsmListener);
        dataRead = gsmListener.getData();
    }

    /* Getter */
    public GsmData getData() {
        dataRead = gsmListener.getData();
        return dataRead;
    }

    public boolean hasSpeed(){
        return gsmListener.hasSpeed();
    }
    public float getSpeed(){
        return gsmListener.getSpeed();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        return mBinder;
    }

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public GsmService getService() {
            return GsmService.this;
        }
    }// LocalBinder

}
