package views.core;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.knoda.knoda.R;

import java.util.HashMap;

import javax.inject.Inject;

import core.KnodaApplication;
import core.KnodaScreen;
import models.Topics;
import networking.NetworkCallback;
import networking.NetworkingManager;
import views.login.WelcomeFragment;
import views.predictionlists.HomeFragment;

import static core.Logger.log;

;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private HashMap<KnodaScreen, Class<? extends Fragment>> mClassMap;
    private HashMap<KnodaScreen, Fragment> mInstanceMap;


    private boolean mShowingLogin;

    @Inject
    NetworkingManager mNetworkingManager;


    FrameLayout splashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((KnodaApplication) getApplication()).inject(this);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getActionBar().hide();

        setContentView(R.layout.activity_main);
        splashScreen = (FrameLayout) findViewById(R.id.splash_screen);

        initializeFragmentBackStack();
        setUpNavigation();

        mInstanceMap = new HashMap<KnodaScreen, Fragment>();
        mClassMap = getClassMap();

        boolean loggedIn = false;

        if (loggedIn) {
            doLogin("saved", "saved");
        } else {
            showLogin();
        }

        hideSplash();


        mNetworkingManager.getTopics(new NetworkCallback<Topics>() {
            @Override
            public void completionHandler(Topics object, VolleyError error) {
                Gson gson = new Gson();
                log(gson.toJson(object));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment!= null && !mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mShowingLogin) {
            menu.removeGroup(R.id.default_menu_group);
            return true;
        }

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_example:
                Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home: {
                if (mNavigationDrawerFragment.isDrawerToggleEnabled())
                    break;
                getFragmentManager().popBackStack();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        Fragment fragment;

        KnodaScreen screen = KnodaScreen.get(position);

        fragment = getFragment(screen);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null).replace(R.id.container, fragment).commit();

    }

    public Fragment getFragment(KnodaScreen screen) {
        Fragment fragment = mInstanceMap.get(screen);
        if (fragment == null) {
            Class<? extends Fragment> fragmentClass = mClassMap.get(screen);
            try {
                fragment = fragmentClass.newInstance();
            }
            catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            }
            catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            mInstanceMap.put(screen, fragment);
        }

        return fragment;
    }

    private HashMap<KnodaScreen, Class<? extends Fragment>> getClassMap() {
        HashMap<KnodaScreen, Class<? extends Fragment>> map = new HashMap<KnodaScreen, Class<? extends Fragment>>();

        map.put(KnodaScreen.HOME, HomeFragment.class);

        return map;
    }

    private void setUpNavigation (){
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void pushFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null).replace(R.id.container, fragment).commit();
        mNavigationDrawerFragment.setDrawerToggleEnabled(false);
    }

    private void initializeFragmentBackStack () {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Integer count = getFragmentManager().getBackStackEntryCount();

                if (count <= 0)
                    mNavigationDrawerFragment.setDrawerToggleEnabled(true);
            }
        });
    }

    private void showLogin () {

       mShowingLogin = true;

       WelcomeFragment welcome = WelcomeFragment.newInstance();
       FragmentManager fragmentManager = getFragmentManager();
       FragmentTransaction transaction = fragmentManager.beginTransaction();
       transaction.replace(R.id.container, welcome).commit();

       mNavigationDrawerFragment.setDrawerToggleEnabled(false);
       mNavigationDrawerFragment.setDrawerLockerMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

       invalidateOptionsMenu();
    }

    public void doLogin(String username, String password) {

        mShowingLogin = false;

        mNavigationDrawerFragment.setDrawerToggleEnabled(true);
        mNavigationDrawerFragment.setDrawerLockerMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        invalidateOptionsMenu();

        mNavigationDrawerFragment.selectStartingItem();

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
