package in.ac.iitb.cse.cartsbusboarding.ui;

import dagger.Component;
import in.ac.iitb.cse.cartsbusboarding.PollingService;
import in.ac.iitb.cse.cartsbusboarding.acc.AccService;

import javax.inject.Singleton;

@Singleton
@Component(modules = AndroidModule.class)
public interface MainApplicationComponent {
    /* These are all the places where dagger is injecting stuff */
    void inject(MainApplication application);
    void inject(MainActivity demoActivity);
    void inject(PollingService pollingService);
    void inject(AccService service);
}
