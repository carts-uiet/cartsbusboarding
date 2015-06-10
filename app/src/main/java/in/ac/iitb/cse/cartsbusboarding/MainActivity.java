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

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.controllers.AccDisplayController;
import in.ac.iitb.cse.cartsbusboarding.data.AccDisplayData;
import in.ac.iitb.cse.cartsbusboarding.data.GsmDisplayData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmEngine;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmListener;
import in.ac.iitb.cse.cartsbusboarding.tasks.AccDisplayTask;
import in.ac.iitb.cse.cartsbusboarding.tasks.GsmDisplayTask;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;
import in.ac.iitb.cse.cartsbusboarding.utils.MainFragment;

public class MainActivity extends AppCompatActivity implements AccDisplayController {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    /**
     * No need to pass Engine to classes outside acc/gsm packages
     * Still leaving the classes(acc/gsm module) where it's passed as it is,
     * to maintain abstraction from MainActivity
     */
    public static AccEngine accEngine;
    public static GsmEngine gsmEngine;
    private SwipeRefreshLayout accRefreshLayout;
    private SwipeRefreshLayout gsmRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainFragment fragment = new MainFragment();
        supportFragmentManager.beginTransaction().add(android.R.id.content, fragment).commit();

        /* Our Stuff */
        init_gsm();
        init_acc();

    }

    @Override
    protected void onStart() {
        super.onStart();

        setRefreshLayoutListeners();
    }

    private void setRefreshLayoutListeners() {
        accRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.acc_swipe_refresh_layout);
        gsmRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.gsm_swipe_refresh_layout);
        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                textViewClicked(null);
            }
        };
        accRefreshLayout.setOnRefreshListener(refreshListener);
        gsmRefreshLayout.setOnRefreshListener(refreshListener);
    }

    /**
     * Setup different activity elements to show correct values onCreate
     */
    void setup_display() {
        Button pollingButton = (Button) findViewById(R.id.button_polling);
        if (isMyServiceRunning(PollingService.class)) {
            pollingButton.setText("Stop Polling");
        } else {
            pollingButton.setText("Start Polling");
        }
    }

    private void init_gsm() {
        gsmEngine = new GsmEngine(this.getApplicationContext());
    }

    private void init_acc() {
        accEngine = new AccEngine(this.getApplicationContext());
    }

    /**
     * You can use this generic function to check whether or not a service is
     * available in ActivityManager's RunningServiceInfo list
     *
     * @param serviceClass Any service_name.class that you need to check
     * @return boolean depending on whether service in ActivityManager or not
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void pollingButtonClicked(View v) {
        Button pollingButton = (Button) findViewById(R.id.button_polling);
        if (isMyServiceRunning(PollingService.class)) {
            pollingButton.setText("Start Polling");
            stopService(new Intent(this, PollingService.class));
        } else {
            pollingButton.setText("Stop Polling");
            Intent serviceIntent = new Intent(this, PollingService.class);
            startService(serviceIntent);
        }
        //Maybe call setup_display here!
    }

    public void textViewClicked(View v) {
        gsmRefreshLayout.setRefreshing(true);
        new GsmDisplayTask(gsmEngine, MainActivity.this).execute();
        accRefreshLayout.setRefreshing(true);
        new AccDisplayTask(accEngine, MainActivity.this).execute();
        /* Setup Display called here to make sure that the button text is right */
        setup_display();
        /* Hack begins */
        LocationManager gpsMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GsmListener gpsListener = new GsmListener();
        gpsMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        final GsmData gpsData = gpsListener.getCurrentData();
        float gpsSpeed = -1;
        if (gpsData != null && gpsData.getLocation() != null) {
            TextView twData = (TextView) findViewById(R.id.section_data_gsm);
            gpsSpeed = gpsData.getLocation().getSpeed();
            twData.setText(twData.getText() + "GPS Speed:" + gpsSpeed);
        }
        /* Hack ends */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayGsm(GsmDisplayData data) {
        TextView twData = (TextView) findViewById(R.id.section_data_gsm);
        twData.setText(Html.fromHtml(
                        "Lat/Long: " + data.getGsmData().toString()
                                + "<br/>"
                                + "GSM Speed: " + data.getSpeed()
                                + "<br/>"
                                + "My get Speed: " + (int) data.getMySpeed()
                                + "<br/>"
//                                            +"speed(getDisT): "+ String.format(format, gsmEngine.getSpeed(2000)) )
                )
        );
        gsmRefreshLayout.setRefreshing(false);
    }

    @Override
    public void displayAcc(AccDisplayData data) {
        String format = "%.5f";
        TextView twData = (TextView) findViewById(R.id.section_data_acc);

        twData.setText(Html.fromHtml("<br/>"
                        + "Average IDX: " + data.getAvg()
                        + "<br/>"
                        + "TIME DOMAIN FEATURES:"
                        + "<br/>"
                        + "Mean: " + String.format(format, data.getMean())
                        + " m/s<sup><small> 2 </small></sup>"
                        + "<br/>"
                        + "Std: " + String.format(format, data.getStd())
                        + " m/s<sup><small> 2 </small></sup>"
                        + "<br/><br/>"
                        + "FREQUENCY DOMAIN FEATURES:"
                        + "<br/>"
                        + "DC Comp: " + String.format(format, data.getDcComp())
                        + "<br/>"
                        + "Energy: " + String.format(format, data.getEnergy())
                        + "<br/>"
                        + "Entropy: " + String.format(format, data.getEntropy()))
        );
        accRefreshLayout.setRefreshing(false);
    }

}
