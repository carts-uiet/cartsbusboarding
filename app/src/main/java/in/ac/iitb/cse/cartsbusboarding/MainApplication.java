package in.ac.iitb.cse.cartsbusboarding;

import android.app.Application;

public class MainApplication extends Application {

    private MainApplicationComponent component;

    @Override public void onCreate() {
        super.onCreate();
        component = DaggerMainApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
        component().inject(this);
    }

    public MainApplicationComponent component() {
        return component;
    }
}
