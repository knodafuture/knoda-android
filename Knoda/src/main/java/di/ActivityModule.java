package di;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import adapters.PredictionAdapter;
import dagger.Module;
import dagger.Provides;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.UserManager;
import unsorted.ErrorReporter;
import views.activity.ActivityFragment;
import views.addprediction.AddPredictionFragment;
import views.badge.BadgeFragment;
import views.core.BaseActivity;
import views.core.MainActivity;
import views.core.NavigationDrawerFragment;
import views.core.Spinner;
import views.details.CreateCommentFragment;
import views.details.DetailsFragment;
import views.login.ForgotPasswordFragment;
import views.login.LoginFragment;
import views.login.PhotoChooserActivity;
import views.login.SignUpFragment;
import views.login.WelcomeFragment;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.CategoryFragment;
import views.predictionlists.HistoryFragment;
import views.predictionlists.HomeFragment;
import views.profile.MyProfileFragment;
import views.search.SearchFragment;

/**
 * Created by nick on 1/17/14.
 */

@Module(
        injects = {
                UserManager.class,
                NetworkingManager.class,
                MainActivity.class,
                PhotoChooserActivity.class,
                WelcomeFragment.class,
                LoginFragment.class,
                SignUpFragment.class,
                ForgotPasswordFragment.class,
                HomeFragment.class,
                ActivityFragment.class,
                HistoryFragment.class,
                AnotherUsersProfileFragment.class,
                MyProfileFragment.class,
                AddPredictionFragment.class,
                SearchFragment.class,
                CategoryFragment.class,
                BadgeFragment.class,
                DetailsFragment.class,
                NavigationDrawerFragment.class,
                CreateCommentFragment.class,
                Bus.class,
                PredictionAdapter.class
        },
        addsTo = KnodaModule.class,
        library = true
)
public class ActivityModule {

    private final BaseActivity mActivity;

    public ActivityModule(BaseActivity activity) {
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

    @Provides @Singleton Bus provideBus() {
        return new Bus();
    }
}
