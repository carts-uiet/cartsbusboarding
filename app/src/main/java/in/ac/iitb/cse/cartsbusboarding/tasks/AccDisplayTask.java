package in.ac.iitb.cse.cartsbusboarding.tasks;

import android.os.AsyncTask;
import android.util.Log;
import in.ac.iitb.cse.cartsbusboarding.PatternRecognition;
import in.ac.iitb.cse.cartsbusboarding.acc.AccData;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.acc.FeatureCalculator;
import in.ac.iitb.cse.cartsbusboarding.controllers.AccDisplayController;
import in.ac.iitb.cse.cartsbusboarding.data.AccDisplayData;

public class AccDisplayTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = AccDisplayTask.class.getSimpleName();
    private AccEngine mAccEngine;
    private AccDisplayController mController;
    private AccDisplayData mAccDisplayData;

    public AccDisplayTask(AccEngine accEngine, AccDisplayController controller) {
        this.mAccEngine = accEngine;
        this.mController = controller;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AccData accData = mAccEngine.getCurrentData();
        if (accData != null) {
            Log.i(TAG, "Data- " + accData);
            FeatureCalculator featureCalculator = new FeatureCalculator(mAccEngine);
            PatternRecognition patternRecognition = new PatternRecognition(mAccEngine);

            // boolean hasIt = patternRecognition.hasBoardedBus();
            // Log.i(TAG, "HasBoardedBus: "+hasIt);
            //XXX: PR uses its own featureCalc
            double avg = patternRecognition.getAvg();

            mAccDisplayData = new AccDisplayData(
                    featureCalculator.getMean(),
                    featureCalculator.getStd(),
                    featureCalculator.getDCComponent(),
                    featureCalculator.getEnergy(),
                    featureCalculator.getEntropy(),
                    avg);
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (mAccDisplayData != null) {
            mController.displayAcc(mAccDisplayData);
        }
    }

}
