package views.core;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import di.KnodaApplication;
import managers.GcmManager;
import models.KnodaScreen;
import models.LoginRequest;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import unsorted.BadgesUnseenMonitor;
import views.activity.ActivityFragment;
import views.addprediction.AddPredictionFragment;
import views.badge.BadgeFragment;
import views.login.PhotoChooserActivity;
import views.login.WelcomeFragment;
import views.predictionlists.HistoryFragment;
import views.predictionlists.HomeFragment;
import views.profile.MyProfileFragment;
import views.search.SearchFragment;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private GcmManager gcmManager;
    private NavigationDrawerFragment navigationDrawerFragment;

    private HashMap<KnodaScreen, Class<? extends Fragment>> classMap;
    private HashMap<KnodaScreen, Fragment> instanceMap;

    @InjectView(R.id.splash_screen)
    public RelativeLayout splashScreen;

    GoogleCloudMessaging gcm;

    private ArrayList<KnodaScreen> screens;
    private boolean actionBarEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.knodaLightGreen);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getActionBar().hide();

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        bus.register(this);

        instanceMap = new HashMap<KnodaScreen, Fragment>();
        classMap = getClassMap();

        initializeFragmentBackStack();
        setUpNavigation();

        final LoginRequest request = sharedPrefManager.getSavedLoginRequest();

        if (request == null) {
            showLogin();
            hideSplash();
        } else {
            userManager.login(request, new NetworkCallback<User>() {
                @Override
                public void completionHandler(User object, ServerError error) {
                    if (error != null)
                        showLogin();
                    else
                        doLogin();
                    hideSplash();
                }
            });
        }
        new ImagePreloader(networkingManager).invoke();
        if (getIntent().getBooleanExtra("showActivity", false)) {
            showActivities();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (navigationDrawerFragment != null && !navigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onBackPressed() {
        if (progressView.getVisibility() == View.VISIBLE)
            return;

        if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (progressView.getVisibility() == View.VISIBLE || !actionBarEnabled)
            return true;

        switch (item.getItemId()) {
            case android.R.id.home: {
                if (navigationDrawerFragment.isDrawerToggleEnabled())
                    break;
                getFragmentManager().popBackStack();
                return true;
            }

            case R.id.action_add_prediction: {
                onAddPrediction();
                break;
            }

            case R.id.action_search: {
                onSearch();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(KnodaScreen screen) {
        Fragment fragment;

        fragment = getFragment(screen);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment).commit();

    }

    public Fragment getFragment(KnodaScreen screen) {
        Fragment fragment = instanceMap.get(screen);
        if (fragment == null) {
            Class<? extends Fragment> fragmentClass = classMap.get(screen);
            try {
                fragment = fragmentClass.newInstance();
            }
            catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            }
            catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            instanceMap.put(screen, fragment);
        }

        return fragment;
    }

    private HashMap<KnodaScreen, Class<? extends Fragment>> getClassMap() {
        HashMap<KnodaScreen, Class<? extends Fragment>> map = new HashMap<KnodaScreen, Class<? extends Fragment>>();

        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.HOME, "Home", getResources().getDrawable(R.drawable.drawer_home)), HomeFragment.class);
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.ACTIVITY, "Activity", getResources().getDrawable(R.drawable.drawer_activity)), ActivityFragment.class);
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.HISTORY, "History", getResources().getDrawable(R.drawable.drawer_history)), HistoryFragment.class);
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.BADGES, "Badges", getResources().getDrawable(R.drawable.drawer_badges)), BadgeFragment.class);
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.PROFILE, "Profile", getResources().getDrawable(R.drawable.drawer_profile)), MyProfileFragment.class);
        return map;
    }

    private void setUpNavigation (){
        navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        screens = new ArrayList<KnodaScreen>(classMap.keySet());
        Collections.sort(screens);

        navigationDrawerFragment.setScreens(screens);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void pushFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null).replace(R.id.container, fragment).commit();
        navigationDrawerFragment.setDrawerToggleEnabled(false);
    }

    public void popFragment() {
        getFragmentManager().popBackStack();
    }

    public void showFrament(KnodaScreen.KnodaScreenOrder position) {
        KnodaScreen screen = findScreen(position);

        if (screen == null)
            return;

        onNavigationDrawerItemSelected(screen);
    }

    private KnodaScreen findScreen(KnodaScreen.KnodaScreenOrder position) {
        KnodaScreen screen = screens.get(position.ordinal());

        if (screen != null && screen.order == position)
            return screen;

        return null;
    }

    private void initializeFragmentBackStack () {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Integer count = getFragmentManager().getBackStackEntryCount();

                if (count <= 0)
                    navigationDrawerFragment.setDrawerToggleEnabled(true);
            }
        });
    }

    private void showLogin () {
       WelcomeFragment welcome = WelcomeFragment.newInstance();
       FragmentManager fragmentManager = getFragmentManager();
       FragmentTransaction transaction = fragmentManager.beginTransaction();
       transaction.replace(R.id.container, welcome).commit();

       navigationDrawerFragment.setDrawerToggleEnabled(false);
       navigationDrawerFragment.setDrawerLockerMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

       invalidateOptionsMenu();
    }

    public void doLogin() {
        registerGcm();
        navigationDrawerFragment.setDrawerToggleEnabled(true);
        navigationDrawerFragment.setDrawerLockerMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        getActionBar().show();
        invalidateOptionsMenu();

        navigationDrawerFragment.selectStartingItem();
        navigationDrawerFragment.refreshUser();
        navigationDrawerFragment.refreshActivity();
        if (userManager.getUser().avatar == null) {
            Intent intent = new Intent(this, PhotoChooserActivity.class);
            startActivity(intent);
        }
    }

    private void registerGcm() {
        if (checkPlayServices()) {
            gcmManager = new GcmManager(networkingManager, sharedPrefManager, GoogleCloudMessaging.getInstance(this));
            String regId = gcmManager.getRegistrationId();
            if (regId.isEmpty()) {
                gcmManager.registerInBackground();
            }
        } else {
            Log.i("MainActivity", "No valid Google Play Services APK found.");
        }
    }

    public void restart() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void checkBadges() {
        new BadgesUnseenMonitor(this, networkingManager).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        KnodaApplication.activityPaused();
        ((KnodaApplication)getApplication()).setCurrentActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KnodaApplication.activityResumed();
        ((KnodaApplication)getApplication()).setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((KnodaApplication)getApplication()).setCurrentActivity(null);
    }

    private void hideSplash() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashScreen.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        splashScreen.setAnimation(fadeOut);
    }

    private void onAddPrediction() {
        AddPredictionFragment fragment = new AddPredictionFragment();
        pushFragment(fragment);
    }

    private void onSearch() {
        SearchFragment fragment = new SearchFragment();
        pushFragment(fragment);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    public void showActivities() {
        navigationDrawerFragment.selectItem(1);
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this, "56TTPBKSC2BJZGSW2W76");
        FlurryAgent.setCaptureUncaughtExceptions(true);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public void setActionBarEnabled(boolean enabled) {
        actionBarEnabled = enabled;
    }
}
