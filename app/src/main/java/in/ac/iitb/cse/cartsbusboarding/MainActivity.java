package in.ac.iitb.cse.cartsbusboarding;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import in.ac.iitb.cse.cartsbusboarding.acc.AccData;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.acc.FeatureCalculator;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmEngine;

public class MainActivity extends ActionBarActivity {

    public static final String _ClassName = MainActivity.class.getSimpleName();
    public AccEngine accEngine;
    public GsmEngine gsmEngine;
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

    private void init_gsm() {
        gsmEngine = new GsmEngine(this.getApplicationContext());
    }

    private void init_acc() {
        accEngine = new AccEngine(this.getApplicationContext());
    }

    public void textViewClicked(View v) {
        new GsmDisplayTask().execute();
        new AccDisplayTask().execute();
    }
    private class GsmDisplayTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final GsmData gsmData = gsmEngine.getData();
            Log.i(_ClassName, "Received: " + gsmData);
            if (gsmData != null) {
                Log.i(_ClassName, "Data- " + gsmData.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView twData = (TextView) findViewById(R.id.section_data_gsm);
                        twData.setText(Html.fromHtml(
                                        "Lat/Long: "+gsmData.toString()
                                                +"<br/>"
                                                +"Has Speed: "+gsmEngine.hasSpeed()
                                                +"<br/>"
                                                +"Speed: "+gsmEngine.getSpeed()
                                                +"<br/>"
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
                Log.i(_ClassName, "Data- " + accData);
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
//            Log.i(_ClassName, "HasBoardedBus: "+hasIt);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String format = "%.5f";
                        TextView twData = (TextView) findViewById(R.id.section_data_acc);
                        twData.setText(Html.fromHtml(
//                                        "HasBoarded: " + hasIt +
                                        "<br/>"
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
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
            return 3;
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
