package in.ac.iitb.cse.cartsbusboarding.ui;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.gsm.GsmEngine;
import in.ac.iitb.cse.cartsbusboarding.utils.ForApplication;

import javax.inject.Singleton;

@Module
public class AndroidModule {
    /**
     * A module for Android-specific dependencies which require a {@link Context} or
     * {@link android.app.Application} to create.
     */
    private final MainApplication application;

    public AndroidModule(MainApplication application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    GsmEngine provideGsmEngine() {
        return new GsmEngine(application);
    }

    @Provides
    @Singleton
    AccEngine provideAccEngine() {
        return new AccEngine(application);
    }

}
