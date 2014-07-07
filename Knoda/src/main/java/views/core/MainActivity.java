package views.core;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;
import com.tapjoy.TapjoyConnect;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import di.KnodaApplication;
import helpers.TapjoyPPA;
import helpers.TypefaceSpan;
import managers.AppOutdatedManager;
import managers.GcmManager;
import models.Group;
import models.Invitation;
import models.KnodaScreen;
import models.Notification;
import models.Prediction;
import models.ServerError;
import models.Setting;
import models.User;
import networking.NetworkCallback;
import pubsub.ChangeGroupEvent;
import pubsub.ReloadListsEvent;
import pubsub.ScreenCaptureEvent;
import unsorted.BadgesUnseenMonitor;
import views.activity.ActivityFragment;
import views.addprediction.AddPredictionFragment;
import views.avatar.UserAvatarChooserFragment;
import views.badge.BadgeFragment;
import views.details.CreateCommentFragment;
import views.details.DetailsFragment;
import views.group.AddGroupFragment;
import views.group.GroupFragment;
import views.group.GroupSettingsFragment;
import views.login.WelcomeFragment;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.HistoryFragment;
import views.predictionlists.HomeFragment;
import views.profile.MyProfileFragment;
import views.search.SearchFragment;
import views.settings.SettingsFragment;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static KnodaScreen.KnodaScreenOrder startupScreen;
    public String currentFragment = "";
    public HashMap<String, ArrayList<Setting>> settings;
    GoogleCloudMessaging gcm;
    @Inject
    AppOutdatedManager appOutdatedManager;
    private GcmManager gcmManager;
    private NavigationDrawerFragment navigationDrawerFragment;
    private HashMap<KnodaScreen, Class<? extends Fragment>> classMap;
    private HashMap<KnodaScreen, Fragment> instanceMap;
    private ArrayList<KnodaScreen> screens;
    private boolean actionBarEnabled = true;
    private String title;
    private Group currentGroup;
    private Notification pushNotification;

    @Subscribe
    public void changeGroup(ChangeGroupEvent event) {
        currentGroup = event.group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.knodaLightGreen);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        bus.register(this);
        appOutdatedManager.setBus(bus);
        instanceMap = new HashMap<KnodaScreen, Fragment>();
        classMap = getClassMap();
        settings = new HashMap<String, ArrayList<Setting>>();
        pushNotification = new Notification();

        initializeFragmentBackStack();
        setUpNavigation();

        if (getIntent().getData() != null)
            twitterManager.checkIntentData(getIntent());


        if (getIntent().getStringExtra("type") != null) {
            pushNotification.type = getIntent().getStringExtra("type");
        }
        if (getIntent().getStringExtra("id") != null) {
            pushNotification.id = getIntent().getStringExtra("id");
        }

        launch();

        if (getIntent().getStringExtra("type") != null) {
            if (userManager.isLoggedIn()) {
                spinner.show();
                userManager.refreshUser(new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        if (error != null) {
                            spinner.hide();
                            return;
                        } else {
                            if (pushNotification.type.equals("p")) {
                                networkingManager.getPrediction(Integer.parseInt(pushNotification.id), new NetworkCallback<Prediction>() {
                                    @Override
                                    public void completionHandler(Prediction object, ServerError error) {
                                        spinner.hide();
                                        if (error != null)
                                            showActivities();
                                        else {
                                            DetailsFragment fragment = DetailsFragment.newInstance(object);
                                            pushFragment(fragment);
                                        }
                                    }
                                });
                            } else if (pushNotification.type.equals("gic")) {
                                networkingManager.getInvitationByCode(pushNotification.id, new NetworkCallback<Invitation>() {
                                    @Override
                                    public void completionHandler(Invitation object, ServerError error) {
                                        spinner.hide();
                                        if (error != null)
                                            showActivities();
                                        else {
                                            GroupSettingsFragment fragment = GroupSettingsFragment.newInstance(object.group, pushNotification.id);
                                            pushFragment(fragment);
                                        }
                                    }
                                });
                            } else {
                                showActivities();
                                spinner.hide();
                            }

                        }
                    }
                });
            } else {
                userManager.loginAsGuest(new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        showLogin("Whoa there cowboy!", "You're just a guest.\nSign up with Knoda.");
                    }
                });
            }

        } else {
            //launch();
        }
        new ImagePreloader(networkingManager).invoke();
        TapjoyConnect.requestTapjoyConnect(this, TapjoyPPA.TJC_APP_ID, TapjoyPPA.TJC_APP_SECRET);
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
        if (spinner.isVisible())
            return;

        if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (spinner.isVisible() || !actionBarEnabled)
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
            case R.id.action_settings: {
                onSettings();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(KnodaScreen screen) {
        Fragment fragment = getFragment(screen);
        if (!checkFragment(fragment))
            return;

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment).commitAllowingStateLoss();

    }

    public Fragment getFragment(KnodaScreen screen) {
        Fragment fragment = instanceMap.get(screen);
        if (fragment == null) {
            Class<? extends Fragment> fragmentClass = classMap.get(screen);
            try {
                fragment = fragmentClass.newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
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
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.GROUP, "Groups", getResources().getDrawable(R.drawable.drawer_groups)), GroupFragment.class);
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.HISTORY, "History", getResources().getDrawable(R.drawable.drawer_history)), HistoryFragment.class);
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.PROFILE, "Profile", getResources().getDrawable(R.drawable.drawer_profile)), MyProfileFragment.class);
        map.put(new KnodaScreen(KnodaScreen.KnodaScreenOrder.BADGES, "Badges", getResources().getDrawable(R.drawable.drawer_badges)), BadgeFragment.class);
        return map;
    }

    private void setUpNavigation() {
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
        setActionBarTitle(title);
    }

    public void pushFragment(Fragment fragment) {

        if (!checkFragment(fragment))
            return;

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null).replace(R.id.container, fragment).commitAllowingStateLoss();
        navigationDrawerFragment.setDrawerToggleEnabled(false);
    }

    public boolean checkFragment(Fragment fragment) {
        if (userManager != null && userManager.getUser() != null && !userManager.getUser().guestMode)
            return true;

        if (fragment instanceof AddGroupFragment) {
            showLogin("Hey now!", "You need to create an account to join and create groups.");
            return false;
        } else if (fragment instanceof AddPredictionFragment) {
            showLogin("Oh Snap!", "You need to create an account to make predictions.");
            return false;
        } else if (fragment instanceof CreateCommentFragment) {
            showLogin("Whoa!", "To comment on predictions, you need to create an account.");
            return false;
        } else if (fragment instanceof MyProfileFragment) {
            showLogin("Whoa there cowboy", "You're just a guest.\nSign up with Knoda to unlock your profile");
            return false;
        }

        return true;
    }

    public void popFragment() {
        getFragmentManager().popBackStack();
    }

    public void popToRootFragment() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void showFrament(KnodaScreen.KnodaScreenOrder position) {
        KnodaScreen screen = findScreen(position);

        if (screen == null)
            return;

        navigationDrawerFragment.selectItem(position.ordinal());
    }

    private KnodaScreen findScreen(KnodaScreen.KnodaScreenOrder position) {
        KnodaScreen screen = screens.get(position.ordinal());

        if (screen != null && screen.order == position)
            return screen;

        return null;
    }

    private void initializeFragmentBackStack() {
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

    public void showLogin(String titleMessage, String detailMessage) {
        captureScreen();
        WelcomeFragment f = WelcomeFragment.newInstance(titleMessage, detailMessage);

        f.show(getFragmentManager().beginTransaction(), "welcome");
        navigationDrawerFragment.resetDrawerUISelection();

    }

    public void launch() {
        registerGcm();
        navigationDrawerFragment.setDrawerToggleEnabled(true);
        navigationDrawerFragment.setDrawerLockerMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        invalidateOptionsMenu();

        if (startupScreen == null)
            navigationDrawerFragment.selectStartingItem();
        else {
            navigationDrawerFragment.selectItem(startupScreen.ordinal());
            startupScreen = null;
        }
        navigationDrawerFragment.refreshUser();
        navigationDrawerFragment.refreshActivity();

        if (getIntent().getExtras() != null) {
            String launchInfo = getIntent().getExtras().getString("launchInfo");
            if (launchInfo != null) {
                Uri uri = Uri.parse(launchInfo);

                List<String> parts = uri.getPathSegments();
                if (parts.get(0).equals("predictions")) {
                    spinner.show();
                    networkingManager.getPrediction(Integer.parseInt(parts.get(1)), new NetworkCallback<Prediction>() {
                        @Override
                        public void completionHandler(Prediction object, ServerError error) {
                            spinner.hide();

                            if (error == null) {
                                DetailsFragment fragment = DetailsFragment.newInstance(object);
                                pushFragment(fragment);
                            }
                        }
                    });
                }
            }
        }

        Prediction p = sharedPrefManager.getPredictionInProgress();

        if (p != null)
            onAddPrediction();

        if (userManager.getUser().guestMode) {
            showLogin(null, null);
        } else if (userManager.getUser().avatar == null) {
            UserAvatarChooserFragment f = new UserAvatarChooserFragment();
            f.show(getFragmentManager(), "avatar");
        }
    }

    private void registerGcm() {
        if (checkPlayServices()) {
            gcmManager = new GcmManager(networkingManager, sharedPrefManager, GoogleCloudMessaging.getInstance(this));
            gcmManager.registerInBackground();
            System.out.println("GCM ID: " + gcmManager.getRegistrationId());
        } else {
            Log.i("MainActivity", "No valid Google Play Services APK found.");
        }
    }

    public void restart() {
        finish();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
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
        ((KnodaApplication) getApplication()).setCurrentActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KnodaApplication.activityResumed();
        ((KnodaApplication) getApplication()).setCurrentActivity(this);
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), "455514421245892");
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        this.setIntent(newIntent);
        if (getIntent().getBooleanExtra("showActivity", false)) {
            showActivities();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((KnodaApplication) getApplication()).setCurrentActivity(null);
    }

    private void onSettings() {
        SettingsFragment fragment = new SettingsFragment();
        pushFragment(fragment);
    }

    private void onAddPrediction() {
        AddPredictionFragment fragment = AddPredictionFragment.newInstance(currentGroup);
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
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        9000).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    public void profileClick(View v) {
        Integer id = (Integer) v.getTag();
        if (id == null)
            return;
        else if (id.equals(userManager.getUser().id)) {
            showFrament(KnodaScreen.KnodaScreenOrder.PROFILE);
        } else {
            AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(id);
            pushFragment(fragment);
        }
    }

    public void showActivities() {
        navigationDrawerFragment.selectItem(1);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "56TTPBKSC2BJZGSW2W76");
        FlurryAgent.setCaptureUncaughtExceptions(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public void setActionBarEnabled(boolean enabled) {
        actionBarEnabled = enabled;
    }

    public void setActionBarTitle(String title) {
        if (title == "" || title == null) {
            title = "KNODA";
        }

        SpannableString s = new SpannableString(title);
        s.setSpan(new TypefaceSpan(this, "KronaOne-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        if (title != "KNODA")
            this.title = title;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    public void requestStartupScreen(KnodaScreen.KnodaScreenOrder screen) {
        startupScreen = screen;
    }

    public void doLogin() {
        navigationDrawerFragment.refreshUser();
        bus.post(new ReloadListsEvent());
    }

    private void captureScreen() {
        final View v = getWindow().getDecorView();

        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setDrawingCacheEnabled(true);

                Bitmap bmap = v.getDrawingCache();

                int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
                Bitmap b = Bitmap.createBitmap(bmap, 0, contentViewTop, bmap.getWidth(), bmap.getHeight() - contentViewTop, null, true);

                v.setDrawingCacheEnabled(false);

                saveImage(b);
            }
        }, 500);

    }

    protected void saveImage(Bitmap b) {

        final Context context = this;

        AsyncTask<Bitmap, Void, File> t = new AsyncTask<Bitmap, Void, File>() {
            @Override
            protected File doInBackground(Bitmap... bitmaps) {

                Bitmap b = bitmaps[0];

                if (b == null)
                    return null;

//                RenderScriptGaussianBlur blur = new RenderScriptGaussianBlur(RenderScript.create(context));
//                b = blur.blur(15, b);
//                if (b == null)
//                    return null;

                File saved_image_file = new File(
                        Environment.getExternalStorageDirectory()
                                + "/blur_background.png"
                );
                if (saved_image_file.exists())
                    saved_image_file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(saved_image_file);
                    b.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    out.flush();
                    out.close();
                    return saved_image_file;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File file) {
                bus.post(new ScreenCaptureEvent(file));
            }
        };

        t.execute(b);
    }

    public void invalidateBackgroundImage() {
        if (getFragmentManager().findFragmentByTag("welcome") != null)
            captureScreen();
    }

}
