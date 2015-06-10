package in.ac.iitb.cse.cartsbusboarding.tasks;

import android.os.AsyncTask;
import in.ac.iitb.cse.cartsbusboarding.controllers.AccDisplayController;
import in.ac.iitb.cse.cartsbusboarding.data.GsmDisplayData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmData;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmEngine;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import javax.inject.Inject;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGI;

public class GsmDisplayTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = LogUtils.makeLogTag(GsmDisplayTask.class);
    @Inject GsmEngine mGsmEngine;
    @Inject AccDisplayController mController;
    private GsmDisplayData mGsmDisplayData;

    @Inject
    public GsmDisplayTask(GsmEngine gsmEngine, AccDisplayController controller) {
        this.mGsmEngine = gsmEngine;
        this.mController = controller;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final GsmData gsmData = mGsmEngine.getCurrentData();
        LOGI(TAG, "Received: " + gsmData);
        if (gsmData != null) {
            LOGI(TAG, "Data- " + gsmData.toString());
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
        mController.displayGsm(mGsmDisplayData);
    }

}
