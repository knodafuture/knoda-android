package di;

import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import managers.SharedPrefManager;

/**
 * Created by nick on 1/13/14.
 */
@Module(
        injects = {
                KnodaApplication.class
        },
        library = true)
public class KnodaModule extends Object {
    private final KnodaApplication application;

    public KnodaModule(KnodaApplication application) {
        this.application = application;
    }

    @Provides @Singleton Context provideContext() {
        return application;
    }

    @Provides @Singleton SharedPrefManager provideSharedPrefManager() {return new SharedPrefManager(application);}

    @Provides @Singleton Bus provideBus() {return new Bus();}

}

