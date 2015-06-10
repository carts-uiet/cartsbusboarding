package in.ac.iitb.cse.cartsbusboarding.tasks;

import android.content.Context;
import android.os.AsyncTask;
import in.ac.iitb.cse.cartsbusboarding.PatternRecognition;
import in.ac.iitb.cse.cartsbusboarding.acc.AccData;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.acc.FeatureCalculator;
import in.ac.iitb.cse.cartsbusboarding.controllers.AccDisplayController;
import in.ac.iitb.cse.cartsbusboarding.data.AccDisplayData;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import javax.inject.Inject;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.LOGI;

public class AccDisplayTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = LogUtils.makeLogTag(AccDisplayTask.class);
    @Inject AccEngine mAccEngine;
    @Inject AccDisplayController mController;
    private AccDisplayData mAccDisplayData;
    private Context mContext;

    @Inject
    public AccDisplayTask(AccEngine accEngine, AccDisplayController controller, Context context) {
        this.mAccEngine = accEngine;
        this.mController = controller;
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AccData accData = mAccEngine.getCurrentData();
        if (accData != null) {
            LOGI(TAG, "Data- " + accData);
            FeatureCalculator featureCalculator = new FeatureCalculator(mAccEngine);
            PatternRecognition patternRecognition = new PatternRecognition(mAccEngine, mContext);

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
        mController.displayAcc(mAccDisplayData);
    }

}
