package in.ac.iitb.cse.cartsbusboarding;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PollingService extends Service {
    public final String _ClassName = this.getClass().getSimpleName();
    public int prefPollingDelay; //TODO: Get from preferences
    private boolean prefVibrate;
    private double prefAccuracy;
    Timer pollingTaskTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Display a notification about us starting.
        prefVibrate = preferences.getBoolean(SettingsActivity.KEY_ENABLE_VIBE,
                                                SettingsActivity.KEY_ENABLE_VIBE_DEFAULT);
        String prefStringAccuracy = preferences.getString(SettingsActivity.KEY_ACCURACY,
                                                    ""+SettingsActivity.KEY_ACCURACY_DEFAULT);
        prefAccuracy = Float.parseFloat(prefStringAccuracy);
        String prefStringPollingDelay = preferences.getString(SettingsActivity.KEY_SYNC_FREQ,
                                                    ""+SettingsActivity.KEY_SYNC_FREQ_DEFAULT);
        prefPollingDelay = Integer.parseInt(prefStringPollingDelay);
        Log.i(_ClassName, "Prefs: " + prefVibrate + prefPollingDelay + prefAccuracy);
        startPollingTimer();
    }

    private void startPollingTimer() {
        pollingTaskTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            boolean showedStartToast = false;
            @Override
            public void run() {
                if (!showedStartToast) {
//                    Toast.makeText(getApplicationContext(), "Polling Started", Toast.LENGTH_SHORT).show();
                    showedStartToast = true;
                }
                Log.i(_ClassName, "pollingTaskTimed started");
                PollingTask pollingTaskTimed = new PollingTask();
                pollingTaskTimed.execute();
                // PerformBackgroundTask this class is the class that extends AsynchTask
            }
        };
        pollingTaskTimer.schedule(doAsynchronousTask, 0, prefPollingDelay); //execute in every 5000 ms
    }

    public class PollingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //XXX: PR uses its own featureCalc
//            PatternRecognition patternRecognition = new PatternRecognition(accEngine);
//            boolean hasIt = patternRecognition.hasBoardedBus();
//            Log.i(_ClassName, "HasBoardedBus: "+hasIt);

            PatternRecognition patternRecognition = new PatternRecognition(MainActivity.accEngine);
            double avg = patternRecognition.getAvg();
            if (avg <= prefAccuracy) {
                /* Vibrate */
                if (prefVibrate) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);
                }
            }
            return null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
