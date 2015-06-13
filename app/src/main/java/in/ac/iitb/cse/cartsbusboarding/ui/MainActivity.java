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

package in.ac.iitb.cse.cartsbusboarding.ui;

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
import in.ac.iitb.cse.cartsbusboarding.PollingService;
import in.ac.iitb.cse.cartsbusboarding.R;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.controllers.AccDisplayController;
import in.ac.iitb.cse.cartsbusboarding.datacollection.DataSyncIntentService;
import in.ac.iitb.cse.cartsbusboarding.models.AccDisplayData;
import in.ac.iitb.cse.cartsbusboarding.models.GsmDisplayData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmEngine;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmListener;
import in.ac.iitb.cse.cartsbusboarding.tasks.AccDisplayTask;
import in.ac.iitb.cse.cartsbusboarding.tasks.GsmDisplayTask;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import javax.inject.Inject;

import static in.ac.iitb.cse.cartsbusboarding.utils.Util.isMyServiceRunning;

public class MainActivity extends AppCompatActivity implements AccDisplayController {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    @Inject AccEngine mAccEngine;
    @Inject GsmEngine mGsmEngine;
    private SwipeRefreshLayout mAccRefreshLayout;
    private SwipeRefreshLayout mGsmRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainFragment fragment = new MainFragment();
        supportFragmentManager.beginTransaction().add(android.R.id.content, fragment).commit();

        initializeDaggerGraphToInjectDependency();
    }

    private void initializeDaggerGraphToInjectDependency() {
        ((MainApplication) getApplication()).component().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setRefreshLayoutListeners();
    }

    private void setRefreshLayoutListeners() {
        mAccRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.acc_swipe_refresh_layout);
        mGsmRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.gsm_swipe_refresh_layout);
        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                textViewClicked(null);
            }
        };
        mAccRefreshLayout.setOnRefreshListener(refreshListener);
        mGsmRefreshLayout.setOnRefreshListener(refreshListener);
    }

    void setupPollingButton() {
        Button pollingButton = (Button) findViewById(R.id.button_polling);
        if (isMyServiceRunning(this, PollingService.class)) {
            pollingButton.setText(getString(R.string.btn_stop_polling));
        } else {
            pollingButton.setText(getString(R.string.btn_start_polling));
        }
    }

    private void driveDemo() {
        DataSyncIntentService.startActionDriveDemo(this);
    }

    public void pollingButtonClicked(View v) {
        if (true) {
            Button pollingButton = (Button) findViewById(R.id.button_polling);
            pollingButton.setText("Drive Demo Started");
            driveDemo();
        } else {
            Button pollingButton = (Button) findViewById(R.id.button_polling);
            if (isMyServiceRunning(this, PollingService.class)) {
                pollingButton.setText(getString(R.string.btn_start_polling));
                stopService(new Intent(this, PollingService.class));
            } else {
                pollingButton.setText(getString(R.string.btn_stop_polling));
                Intent serviceIntent = new Intent(this, PollingService.class);
                startService(serviceIntent);
            }
            //Maybe call setupPollingButton here!
        }
        //Maybe call setupPollingButton here!
    }

    public void textViewClicked(View v) {
        mGsmRefreshLayout.setRefreshing(true);
        new GsmDisplayTask(mGsmEngine, MainActivity.this).execute();
        mAccRefreshLayout.setRefreshing(true);
        new AccDisplayTask(mAccEngine, MainActivity.this, this.getApplicationContext()).execute();
        setupPollingButton();

        appendGpsData();
    }

    private void appendGpsData() {
        LocationManager gpsMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GsmListener gpsListener = new GsmListener();
        gpsMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        final GsmData gpsData = gpsListener.getCurrentData();
        if (gpsData != null && gpsData.getLocation() != null) {
            TextView twData = (TextView) findViewById(R.id.section_data_gsm);
            twData.setText(twData.getText() + "GPS Speed:" + gpsData.getLocation().getSpeed());
        }
    }

    @Override
    public void displayGsm(GsmDisplayData data) {
        TextView twData = (TextView) findViewById(R.id.section_data_gsm);
        if (data == null) {
            twData.setText(getString(R.string.no_gsm_data));
        } else {
            twData.setText(Html.fromHtml(
                            "Lat/Long: " + data.getGsmData().toString()
                                    + "<br/>"
                                    + "GSM Speed: " + data.getSpeed()
                                    + "<br/>"
                                    + "My get Speed: " + (int) data.getMySpeed()
                                    + "<br/>"
//                                  +"speed(getDisT): "+ String.format(format, mGsmEngine.getSpeed(2000)) )
                    )
            );
        }
        mGsmRefreshLayout.setRefreshing(false);
    }

    @Override
    public void displayAcc(AccDisplayData data) {
        TextView twData = (TextView) findViewById(R.id.section_data_acc);
        if (data == null) {
            twData.setText(getString(R.string.no_acc_data));
        } else {
            String format = "%.5f";

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
        }
        mAccRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
