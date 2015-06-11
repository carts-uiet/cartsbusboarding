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

package in.ac.iitb.cse.cartsbusboarding;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.ui.MainApplication;
import in.ac.iitb.cse.cartsbusboarding.ui.SettingsActivity;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGI;

public class PollingService extends Service {
    private static final String TAG = LogUtils.makeLogTag(PollingService.class);
    public int mPrefPollingDelay;
    @Inject AccEngine mAccEngine;
    Context mContext;
    Timer mPollingTaskTimer;
    private boolean mPrefVibrate;
    private double mPrefAccuracy;
    private Handler mHandler;

    @Override
    public void onDestroy() {
        mPollingTaskTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initializeDaggerGraphToInjectDependency();

        mHandler = new Handler();
        mContext = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Display a notification about us starting.
        mPrefVibrate = preferences.getBoolean(SettingsActivity.KEY_ENABLE_VIBE,
                SettingsActivity.KEY_ENABLE_VIBE_DEFAULT);
        String prefStringAccuracy = preferences.getString(SettingsActivity.KEY_ACCURACY,
                "" + SettingsActivity.KEY_ACCURACY_DEFAULT);
        mPrefAccuracy = Float.parseFloat(prefStringAccuracy);
        String prefStringPollingDelay = preferences.getString(SettingsActivity.KEY_SYNC_FREQ,
                "" + SettingsActivity.KEY_SYNC_FREQ_DEFAULT);
        mPrefPollingDelay = Integer.parseInt(prefStringPollingDelay);
        LOGI(TAG, "Prefs: " + mPrefVibrate + mPrefPollingDelay + mPrefAccuracy);
        startPollingTimer();
    }

    private void initializeDaggerGraphToInjectDependency() {
        ((MainApplication) getApplication()).component().inject(this);
    }

    private void startPollingTimer() {
        mPollingTaskTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            boolean showedStartToast = false;

            @Override
            public void run() {
                if (!showedStartToast) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Polling Started", Toast.LENGTH_SHORT).show();
                        }
                    });
                    showedStartToast = true;
                }
                LOGI(TAG, "pollingTaskTimed started");
                PollingTask pollingTaskTimed = new PollingTask(mAccEngine, mContext);
                pollingTaskTimed.execute();
                // PerformBackgroundTask this class is the class that extends AsynchTask
            }
        };
        mPollingTaskTimer.schedule(doAsynchronousTask, 0, mPrefPollingDelay); //execute in every 5000 ms
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class PollingTask extends AsyncTask<Void, Void, Void> {

        @Inject AccEngine accEngine;
        @Inject Context context;

        public PollingTask(AccEngine accEngine, Context context) {
            this.accEngine = accEngine;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //XXX: PR uses its own featureCalc
//            PatternRecognition patternRecognition = new PatternRecognition(mAccEngine);
//            boolean hasIt = patternRecognition.hasBoardedBus();
//            Log.i(TAG, "HasBoardedBus: "+hasIt);

            PatternRecognition patternRecognition = new PatternRecognition(accEngine, context);
            final double avg = patternRecognition.getAvg();
            if (avg <= mPrefAccuracy) {
                /* Vibrate */
                if (mPrefVibrate) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Average: " + avg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }
}
