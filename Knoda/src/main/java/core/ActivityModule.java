package core;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import views.core.BaseFragment;
import views.core.MainActivity;
import views.core.Spinner;

/**
 * Created by nick on 1/17/14.
 */

@Module(
        injects = {
                MainActivity.class,
                BaseFragment.class
        },
        addsTo = KnodaModule.class,
        library = true
)
public class ActivityModule {

    private final MainActivity mActivity;

    public ActivityModule(MainActivity activity) {
        this.mActivity = activity;
    }

    @Provides @Singleton Spinner provideSpinner() {
        return new Spinner(mActivity);
    }

    @Provides @Singleton ErrorReporter provideReporter() {
        return new ErrorReporter(mActivity);
    }
}
