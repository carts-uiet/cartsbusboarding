package in.ac.iitb.cse.cartsbusboarding;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PollingService extends Service {
    public final String _ClassName = this.getClass().getSimpleName();
    public final int POLLING_DELAY = 5000; //TODO: Get from preferences
    Timer pollingTaskTimer;

    public PollingService() {
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
        pollingTaskTimer.schedule(doAsynchronousTask, 0, POLLING_DELAY); //execute in every 5000 ms
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
            if (patternRecognition.getAvg() != 2.0) {
                /* Vibrate */
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
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
