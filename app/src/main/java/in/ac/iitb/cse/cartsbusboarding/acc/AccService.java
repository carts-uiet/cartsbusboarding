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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import in.ac.iitb.cse.cartsbusboarding.ui.MainApplication;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import javax.inject.Inject;
import java.util.Queue;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGI;
import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGV;

public class AccService extends Service {
    private static final String TAG = LogUtils.makeLogTag(AccService.class);
    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    @Inject AccListener mAccListener;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDaggerGraphToInjectDependency();
        LOGV(TAG, "Started ! ! !");
        LOGI(TAG, "SensorSpeed: " + mAccListener.getSensorSpeed());
    }

    private void initializeDaggerGraphToInjectDependency() {
        ((MainApplication) getApplication()).component().inject(this);
    }

    /* Getter */
    public AccData getCurrentData() {
        return mAccListener.getCurrentData();
    }

    public Queue getDataList() {
        return mAccListener.getDataList();
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
        public AccService getService() {
            return AccService.this;
        }
    }// LocalBinder

}
