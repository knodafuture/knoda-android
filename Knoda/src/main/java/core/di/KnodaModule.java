package core.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nick on 1/13/14.
 */
@Module(
    library = true)
public class KnodaModule extends Object {
    private final KnodaApplication application;

    public KnodaModule(KnodaApplication application) {
        this.application = application;
    }


    @Provides @Singleton Context provideContext() {
        return application;
    }
}

