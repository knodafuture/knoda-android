package core;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import views.core.MainActivity;
import views.core.Spinner;
import views.login.ForgotPasswordFragment;
import views.login.LoginFragment;
import views.login.SignUpFragment;
import views.login.WelcomeFragment;

/**
 * Created by nick on 1/17/14.
 */

@Module(
        injects = {
                MainActivity.class,
                WelcomeFragment.class,
                LoginFragment.class,
                SignUpFragment.class,
                ForgotPasswordFragment.class
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
