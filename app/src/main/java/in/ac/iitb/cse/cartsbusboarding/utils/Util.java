package in.ac.iitb.cse.cartsbusboarding.utils;

import android.app.ActivityManager;
import android.content.Context;

public class Util {
    /**
     * You can use this generic function to check whether or not a service is
     * available in ActivityManager's RunningServiceInfo list
     *
     * @param serviceClass Any service_name.class that you need to check
     * @return boolean depending on whether service in ActivityManager or not
     */
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
