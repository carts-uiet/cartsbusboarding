package in.ac.iitb.cse.cartsbusboarding.ui;

import dagger.Component;
import in.ac.iitb.cse.cartsbusboarding.PollingService;

import javax.inject.Singleton;

@Singleton
@Component(modules = AndroidModule.class)
public interface MainApplicationComponent {
    void inject(MainApplication application);
    void inject(MainActivity demoActivity);
    void inject(PollingService pollingService);
}
