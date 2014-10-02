package di;

import android.content.Context;
import android.net.ConnectivityManager;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import adapters.PredictionAdapter;
import dagger.Module;
import dagger.Provides;
import managers.AppOutdatedManager;
import managers.NetworkingManager;
import managers.TwitterManager;
import managers.UserManager;
import unsorted.ErrorReporter;
import views.activity.ActivityFragment;
import views.activity.ActivityTypeFragment;
import views.addprediction.AddPredictionFragment;
import views.avatar.GroupAvatarChooserActivity;
import views.avatar.UserAvatarChooserActivity;
import views.avatar.UserAvatarChooserFragment;
import views.contacts.FindFriendsActivity;
import views.contacts.FindFriendsContactsFragment;
import views.contacts.FindFriendsFacebookTwitterFragment;
import views.contests.ContestDetailFragment;
import views.contests.ContestFragment;
import views.contests.ContestLeaderboardFeedFragment;
import views.contests.ContestLeaderboardFragment;
import views.contests.SocialFragment;
import views.core.BaseActivity;
import views.core.BaseWebFragment;
import views.core.MainActivity;
import views.core.Spinner;
import views.core.SplashActivity;
import views.details.CreateCommentFragment;
import views.details.DetailsFragment;
import views.group.AddGroupFragment;
import views.group.EditGroupFragment;
import views.group.GroupFragment;
import views.group.GroupLeaderboardFragment;
import views.group.GroupLeaderboardsFragment;
import views.group.GroupSettingsFragment;
import views.group.InvitationsFragment;
import views.login.ForgotPasswordFragment;
import views.login.LoginFragment;
import views.login.SignUpFragment;
import views.login.SignupConfirmFragment;
import views.login.WelcomeFragment;
import views.other.TagActivity;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.CategoryFragment;
import views.predictionlists.GroupPredictionListFragment;
import views.predictionlists.HomeFragment;
import views.profile.FollowFeedFragment;
import views.profile.FollowFragment;
import views.profile.HeadToHeadFragment;
import views.profile.MyProfileFeedFragment;
import views.profile.MyProfileFragment;
import views.profile.PhotoFragment;
import views.search.SearchFragment;
import views.settings.SettingsDeveloperFragment;
import views.settings.SettingsFragment;

/**
 * Created by nick on 1/17/14.
 */

@Module(
        injects = {
                UserManager.class,
                NetworkingManager.class,
                MainActivity.class,
                UserAvatarChooserActivity.class,
                GroupAvatarChooserActivity.class,
                WelcomeFragment.class,
                LoginFragment.class,
                SignUpFragment.class,
                ForgotPasswordFragment.class,
                HomeFragment.class,
                ActivityFragment.class,
                ActivityTypeFragment.class,
                AnotherUsersProfileFragment.class,
                GroupFragment.class,
                AddPredictionFragment.class,
                SearchFragment.class,
                CategoryFragment.class,
                DetailsFragment.class,
                CreateCommentFragment.class,
                AddGroupFragment.class,
                GroupPredictionListFragment.class,
                GroupLeaderboardFragment.class,
                GroupLeaderboardsFragment.class,
                Bus.class,
                PredictionAdapter.class,
                SplashActivity.class,
                PhotoFragment.class,
                GroupSettingsFragment.class,
                InvitationsFragment.class,
                EditGroupFragment.class,
                UserAvatarChooserFragment.class,
                SignupConfirmFragment.class,
                MyProfileFragment.class,
                MyProfileFeedFragment.class,
                SettingsFragment.class,
                BaseWebFragment.class,
                SocialFragment.class,
                ContestFragment.class,
                ContestDetailFragment.class,
                ContestLeaderboardFragment.class,
                ContestLeaderboardFeedFragment.class,
                FindFriendsContactsFragment.class,
                FindFriendsActivity.class,
                FindFriendsFacebookTwitterFragment.class,
                FollowFragment.class,
                FollowFeedFragment.class,
                HeadToHeadFragment.class,
                TagActivity.class,
                SettingsDeveloperFragment.class
        },
        addsTo = KnodaModule.class,
        library = true
)
public class ActivityModule {

    private final BaseActivity mActivity;

    public ActivityModule(BaseActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @Singleton
    Spinner provideSpinner() {
        return new Spinner(mActivity);
    }

    @Provides
    @Singleton
    ErrorReporter provideReporter() {
        return new ErrorReporter(mActivity);
    }

    @Provides
    @Singleton
    TwitterManager provideTwitterManager() {
        return new TwitterManager();
    }

    @Provides
    @Singleton
    AppOutdatedManager provideAppOutdatedManager() {
        return new AppOutdatedManager(mActivity);
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
