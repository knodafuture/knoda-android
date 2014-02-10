package views.core;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.knoda.knoda.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import models.KnodaScreen;
import unsorted.Logger;
import di.ActivityModule;
import di.KnodaApplication;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.UserManager;
import networking.NetworkCallback;
import dagger.ObjectGraph;
import models.LoginRequest;
import models.ServerError;
import models.User;
import views.activity.ActivityFragment;
import views.login.PhotoChooserActivity;
import views.login.WelcomeFragment;
import views.predictionlists.HistoryFragment;
import views.predictionlists.HomeFragment;
import views.profile.MyProfileFragment;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private ObjectGraph activityGraph;

    private NavigationDrawerFragment navigationDrawerFragment;

    private HashMap<KnodaScreen, Class<? extends Fragment>> classMap;
    private HashMap<KnodaScreen, Fragment> instanceMap;

    @Inject NetworkingManager networkingManager;

    @Inject UserManager userManager;

    @Inject SharedPrefManager sharedPrefManager;

    @InjectView(R.id.splash_screen)
    public FrameLayout splashScreen;
    @InjectView(R.id.progress_view)
    public FrameLayout progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.knodaLightGreen);
        KnodaApplication application = (KnodaApplication) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
        activityGraph.inject(userManager);
        activityGraph.inject(networkingManager);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getActionBar().hide();

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

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
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new ActivityModule(this));
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }

    @Override protected void onDestroy() {
        activityGraph = null;
        System.gc();
        super.onDestroy();
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
            super.onBackPressed();

        getFragmentManager().popBackStack();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (progressView.getVisibility() == View.VISIBLE)
            return true;

        switch (item.getItemId()) {
            case android.R.id.home: {
                if (navigationDrawerFragment.isDrawerToggleEnabled())
                    break;
                getFragmentManager().popBackStack();
                return true;
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
        transaction.addToBackStack(null).replace(R.id.container, fragment).commit();

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

        map.put(new KnodaScreen(0, "Home", getResources().getDrawable(R.drawable.side_nav_home_icon)), HomeFragment.class);
        map.put(new KnodaScreen(1, "Activity", getResources().getDrawable(R.drawable.side_nav_activity_icon)), ActivityFragment.class);
        map.put(new KnodaScreen(2, "History", getResources().getDrawable(R.drawable.side_nav_history_icon)), HistoryFragment.class);
        map.put(new KnodaScreen(3, "Profile", getResources().getDrawable(R.drawable.side_nav_profile_icon)), MyProfileFragment.class);
        return map;
    }

    private void setUpNavigation (){
        navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        ArrayList<KnodaScreen> screens = new ArrayList<KnodaScreen>(classMap.keySet());
        Collections.sort(screens);

        navigationDrawerFragment.setScreens(screens);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void pushFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null).replace(R.id.container, fragment).commit();
        navigationDrawerFragment.setDrawerToggleEnabled(false);
    }

    private void initializeFragmentBackStack () {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Integer count = getFragmentManager().getBackStackEntryCount();

                if (count <= 1)
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

        Logger.log(new Gson().toJson(userManager.getUser()));
        navigationDrawerFragment.setDrawerToggleEnabled(true);
        navigationDrawerFragment.setDrawerLockerMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        getActionBar().show();
        invalidateOptionsMenu();

        navigationDrawerFragment.selectStartingItem();

        if (userManager.getUser().avatar == null) {
            Intent intent = new Intent(this, PhotoChooserActivity.class);
            startActivity(intent);
        }
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
}
