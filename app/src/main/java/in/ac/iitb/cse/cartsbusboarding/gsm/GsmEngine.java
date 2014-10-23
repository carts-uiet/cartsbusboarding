package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.content.Context;
import android.content.Intent;

/**
 * Created by chaudhary on 10/23/14.
 */
public class GsmEngine {
    Context mContext;

    public GsmEngine(Context context) {
        mContext = context;
        mContext.startService(new Intent(mContext, GsmService.class));
    }
}
