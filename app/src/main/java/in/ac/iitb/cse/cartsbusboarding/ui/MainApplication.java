package in.ac.iitb.cse.cartsbusboarding.ui;

import android.app.Application;
import in.ac.iitb.cse.cartsbusboarding.DaggerMainApplicationComponent;

public class MainApplication extends Application {

    private MainApplicationComponent mComponent;

    @Override public void onCreate() {
        super.onCreate();
        mComponent = DaggerMainApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
        component().inject(this);
    }

    public MainApplicationComponent component() {
        return mComponent;
    }
}
