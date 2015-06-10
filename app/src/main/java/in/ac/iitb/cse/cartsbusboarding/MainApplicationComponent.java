package in.ac.iitb.cse.cartsbusboarding;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AndroidModule.class)
public interface MainApplicationComponent {
    void inject(MainApplication application);
    void inject(MainActivity demoActivity);
    void inject(PollingService pollingService);
}
