package di;

import javax.inject.Singleton;

import unsorted.ErrorReporter;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.UserManager;
import dagger.Module;
import dagger.Provides;
import views.activity.ActivityFragment;
import views.core.MainActivity;
import views.core.Spinner;
import views.login.ForgotPasswordFragment;
import views.login.LoginFragment;
import views.login.SignUpFragment;
import views.login.WelcomeFragment;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.HistoryFragment;
import views.predictionlists.HomeFragment;

/**
 * Created by nick on 1/17/14.
 */

@Module(
        injects = {
                UserManager.class,
                NetworkingManager.class,
                MainActivity.class,
                WelcomeFragment.class,
                LoginFragment.class,
                SignUpFragment.class,
                ForgotPasswordFragment.class,
                HomeFragment.class,
                ActivityFragment.class,
                HistoryFragment.class,
                AnotherUsersProfileFragment.class
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

    @Provides @Singleton UserManager provideUserManager() {
        return new UserManager();
    }

    @Provides @Singleton SharedPrefManager provideSharedPrefManager() {
        return new SharedPrefManager(mActivity);
    }
}