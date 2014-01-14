package core;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import views.core.MainActivity;

/**
 * Created by nick on 1/13/14.
 */
@Module(library = true, injects = MainActivity.class)
public class KnodaModule extends Object {
    private final KnodaApplication application;

    public KnodaModule(KnodaApplication application) {
        this.application = application;
    }


    @Provides @Singleton Context provideContext() {
        return application;
    }


}
