package in.ac.iitb.cse.cartsbusboarding.tasks;

import android.os.AsyncTask;
import android.util.Log;
import in.ac.iitb.cse.cartsbusboarding.controllers.AccDisplayController;
import in.ac.iitb.cse.cartsbusboarding.data.GsmDisplayData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmEngine;

public class GsmDisplayTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = GsmDisplayTask.class.getSimpleName();
    private GsmEngine mGsmEngine;
    private AccDisplayController mController;
    private GsmDisplayData mGsmDisplayData;

    public GsmDisplayTask(GsmEngine gsmEngine, AccDisplayController controller) {
        this.mGsmEngine = gsmEngine;
        this.mController = controller;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final GsmData gsmData = mGsmEngine.getCurrentData();
        Log.i(TAG, "Received: " + gsmData);
        if (gsmData != null) {
            Log.i(TAG, "Data- " + gsmData.toString());
            mGsmDisplayData = new GsmDisplayData(
                    mGsmEngine.getSpeed(),
                    mGsmEngine.myGetSpeed(),
                    gsmData
            );
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (mGsmDisplayData != null) {
            mController.displayGsm(mGsmDisplayData);
        }
    }

}
