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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import in.ac.iitb.cse.cartsbusboarding.acc.AccData;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.acc.FeatureCalculator;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmEngine;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * No need to pass Engine to classes outside acc/gsm packages
     * Still leaving the classes(acc/gsm module) where it's passed as it is,
     * to maintain abstraction from MainActivity
     */
    public static AccEngine accEngine;
    public static GsmEngine gsmEngine;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /* Our Stuff */
        init_gsm();
        init_acc();
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
        new GsmDisplayTask().execute();
        new AccDisplayTask().execute();
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //This is called once the fragments have been loaded
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private class GsmDisplayTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final GsmData gsmData = gsmEngine.getCurrentData();
            Log.i(TAG, "Received: " + gsmData);
            if (gsmData != null) {
                Log.i(TAG, "Data- " + gsmData.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView twData = (TextView) findViewById(R.id.section_data_gsm);
                        twData.setText(Html.fromHtml(
                                        "Lat/Long: " + gsmData.toString()
                                                + "<br/>"
                                                + "GSM Speed: " + gsmEngine.getSpeed()
                                                + "<br/>"
                                                + "My get Speed: " + (int) gsmEngine.myGetSpeed()
                                                + "<br/>"
//                                            +"speed(getDisT): "+ String.format(format, gsmEngine.getSpeed(2000)) )
                                )
                        );
                    }
                });
            }
            return null;
        }
    }

    private class AccDisplayTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            AccData accData = accEngine.getCurrentData();
            if (accData != null) {
                Log.i(TAG, "Data- " + accData);
                FeatureCalculator featureCalculator = new FeatureCalculator(accEngine);
                final double mean = featureCalculator.getMean();
                final double std = featureCalculator.getStd();
                final double dcComp = featureCalculator.getDCComponent();
                final double energy = featureCalculator.getEnergy();
                final double entropy = featureCalculator.getEntropy();

//            featureCalculator.getMean(20);
//            featureCalculator.getStd(20);
//            featureCalculator.getDCComponent(20);
//            featureCalculator.getEnergy(20);
                //XXX: PR uses its own featureCalc
//            PatternRecognition patternRecognition = new PatternRecognition(accEngine);
//            boolean hasIt = patternRecognition.hasBoardedBus();
//            Log.i(TAG, "HasBoardedBus: "+hasIt);

                final PatternRecognition patternRecognition = new PatternRecognition(accEngine);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String format = "%.5f";
                        TextView twData = (TextView) findViewById(R.id.section_data_acc);
                        twData.setText(Html.fromHtml(
//                                        "HasBoarded: " + hasIt +
                                        "<br/>"
//                                                + "hasBoarded: " + patternRecognition.hasBoardedBus()
                                                + "Average IDX: " + patternRecognition.getAvg()
                                                + "<br/>"
                                                + "TIME DOMAIN FEATURES:"
                                                + "<br/>"
                                                + "Mean: " + String.format(format, mean)
                                                + " m/s<sup><small> 2 </small></sup>"
                                                + "<br/>"
                                                + "Std: " + String.format(format, std)
                                                + " m/s<sup><small> 2 </small></sup>"
                                                + "<br/><br/>"
                                                + "FREQUENCY DOMAIN FEATURES:"
                                                + "<br/>"
                                                + "DC Comp: " + String.format(format, dcComp)
                                                + "<br/>"
                                                + "Energy: " + String.format(format, energy)
                                                + "<br/>"
                                                + "Entropy: " + String.format(format, entropy))
                        );
                    }
                });
            }
            return null;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
