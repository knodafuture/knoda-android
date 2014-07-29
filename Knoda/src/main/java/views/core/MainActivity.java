package views.core;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v8.renderscript.RenderScript;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;
import com.tapjoy.TapjoyConnect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import di.KnodaApplication;
import helpers.TapjoyPPA;
import helpers.TypefaceSpan;
import helpers.blur.RenderScriptGaussianBlur;
import managers.AppOutdatedManager;
import managers.GcmManager;
import models.ActivityItem;
import models.Group;
import models.Invitation;
import models.Notification;
import models.Prediction;
import models.ServerError;
import models.Setting;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.ActivityNavEvent;
import pubsub.ChangeGroupEvent;
import pubsub.GroupNavEvent;
import pubsub.HomeNavEvent;
import pubsub.LoginFlowDoneEvent;
import pubsub.ProfileNavEvent;
import pubsub.ReloadListsEvent;
import pubsub.ScreenCaptureEvent;
import views.activity.ActivityFragment;
import views.addprediction.AddPredictionFragment;
import views.avatar.UserAvatarChooserFragment;
import views.contests.SocialFragment;
import views.details.CreateCommentFragment;
import views.details.DetailsFragment;
import views.group.AddGroupFragment;
import views.group.GroupFragment;
import views.group.GroupSettingsFragment;
import views.login.WelcomeFragment;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.HomeFragment;
import views.profile.MyProfileFragment;
import views.search.SearchFragment;
import views.settings.SettingsFragment;

public class MainActivity extends BaseActivity {

    private static final int userRefreshInterval = 30000;
    // Storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";
    private static final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");
    public String currentFragment = "";
    public HashMap<String, ArrayList<Setting>> settings;
    public Menu menu;
    public BitmapDrawable blurredBackground;
    public String mCurrentPhotoPath = null;
    public Uri mCapturedImageURI = null;
    @Inject
    AppOutdatedManager appOutdatedManager;
    @InjectView(R.id.navbar)
    LinearLayout navbar;
    @InjectView(R.id.fragmentContainer)
    FrameLayout container;
    @InjectView(R.id.nav_activity_dot)
    ImageView activityDot;
    private GcmManager gcmManager;
    private boolean actionBarEnabled = true;
    private String title;
    private Group currentGroup;
    private Notification pushNotification;
    private Handler handler = new Handler();
    private boolean userDialogShown = false;
    private Runnable userRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshUser();
        }
    };
    private Runnable activitiesRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshActivities();
        }
    };
    private HomeFragment homeFragment = null;
    private ActivityFragment activityFragment = null;
    private SocialFragment socialFragment = null;
    private MyProfileFragment myProfileFragment = null;
    private SearchFragment searchFragment = null;
    private SettingsFragment settingsFragment = null;
    private RelativeLayout.LayoutParams navbarShown;
    private RelativeLayout.LayoutParams navbarHidden;
    private RelativeLayout.LayoutParams containerFull;
    private RelativeLayout.LayoutParams containerPartial;

    @OnClick(R.id.nav_home)
    void onClickHome() {
        onHome();
    }

    @OnClick(R.id.nav_activity)
    void onClickActivity() {
        onActivity();
    }

    @OnClick(R.id.nav_predict)
    void onClickPredict() {
        onAddPrediction();
    }

    @OnClick(R.id.nav_groups)
    void onClickGroups() {
        onGroups();
    }

    @OnClick(R.id.nav_profile)
    void onClickProfile() {
        onProfile();
    }

    @Subscribe
    public void changeGroup(ChangeGroupEvent event) {
        currentGroup = event.group;
    }

    @Subscribe
    public void onLoginFlowDone(LoginFlowDoneEvent event) {
        if (userManager.getUser() == null) {
            spinner.show();
            sharedPrefManager.clearSession();
            userManager.loginAsGuest(new NetworkCallback<User>() {
                @Override
                public void completionHandler(User object, ServerError error) {
                    doLogin();
                    spinner.hide();
                }
            });
        } else {
            if (!userManager.getUser().guestMode)
                restart();
        }
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
        settings = new HashMap<String, ArrayList<Setting>>();
        pushNotification = new Notification();

        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        navbarShown = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, onedp * 60);
        navbarShown.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        navbarHidden = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);


        containerFull = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerFull.setMargins(0, actionBarHeight, 0, 0);
        containerPartial = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerPartial.setMargins(0, actionBarHeight, 0, onedp * 60);

        createTransparentBackground();

        refreshUser();
        refreshActivities();

        initializeFragmentBackStack();
        //setUpNavigation();

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
                                            onActivity();
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
                                            onActivity();
                                        else {
                                            GroupSettingsFragment fragment = GroupSettingsFragment.newInstance(object.group, pushNotification.id);
                                            pushFragment(fragment);
                                        }
                                    }
                                });
                            } else {
                                onActivity();
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
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "shown", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "hidden", Toast.LENGTH_SHORT).show();
        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "shown", Toast.LENGTH_SHORT).show();
        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "hidden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (spinner.isVisible())
            return;

        if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else if (getFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (spinner.isVisible() || !actionBarEnabled)
            return true;

        switch (item.getItemId()) {
            case android.R.id.home: {
                if (getActionBar().getDisplayOptions() == 15)
                    onBackPressed();
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
            case R.id.action_create_group: {
                onCreateGroup();
                break;
            }
            case R.id.action_profile_guest: {
                showLogin("Giddy Up!", "Now we're talking! Choose an option below to sign-up and start tracking your predictions.");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
        if (mCapturedImageURI != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedImageURI.toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void restoreActionBar() {
        setActionBarTitle(title);
    }

    public void pushFragment(Fragment fragment) {

        if (!checkFragment(fragment))
            return;

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).replace(R.id.fragmentContainer, fragment).commitAllowingStateLoss();
    }

    public boolean checkFragment(Fragment fragment) {
        if (userManager != null && userManager.getUser() != null && !userManager.getUser().guestMode)
            return true;

        if (fragment instanceof AddGroupFragment || fragment instanceof GroupFragment) {
            showLogin("Hey now!", "You need to create an account to access contests and groups.");
            return false;
        } else if (fragment instanceof AddPredictionFragment) {
            showLogin("Oh Snap!", "You need to create an account to make predictions.");
            return false;
        } else if (fragment instanceof CreateCommentFragment) {
            showLogin("Whoa!", "To comment on predictions, you need to create an account.");
            return false;
        }
//        else if (fragment instanceof MyProfileFragment) {
//            showLogin("Whoa there cowboy", "You're just a guest.\nSign up with Knoda to unlock your profile");
//            return false;
//        }

        return true;
    }

    public void popFragment() {
        getFragmentManager().popBackStack();
    }

    public void popToRootFragment() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void initializeFragmentBackStack() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Integer count = getFragmentManager().getBackStackEntryCount();
            }
        });
    }

    public void showLogin(String titleMessage, String detailMessage) {
        captureScreen();
        WelcomeFragment f = WelcomeFragment.newInstance(titleMessage, detailMessage);

        f.show(getFragmentManager().beginTransaction(), "welcome");
    }

    public void launch() {
        registerGcm();
        invalidateOptionsMenu();

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

        if (userManager.getUser() == null || userManager.getUser().guestMode) {
            showLogin(null, null);
            onHome();
        } else if (userManager.getUser().avatar == null) {
            UserAvatarChooserFragment f = new UserAvatarChooserFragment();
            f.show(getFragmentManager(), "avatar");
        } else {
            if (sharedPrefManager.getTwitterAuthScreen().equals("profile")) {
                onProfile();
            } else
                onHome();
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
        bus.unregister(this);
        finish();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
            onActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((KnodaApplication) getApplication()).setCurrentActivity(null);
    }

    public void resetNavIcons() {
        findViewById(R.id.nav_home).setBackgroundResource(R.drawable.nav_home);
        findViewById(R.id.nav_activity).setBackgroundResource(R.drawable.nav_activity);
        findViewById(R.id.nav_profile).setBackgroundResource(R.drawable.nav_me);
        findViewById(R.id.nav_groups).setBackgroundResource(R.drawable.nav_groups);

        ((TextView) findViewById(R.id.nav_home_text)).setTextColor(Color.parseColor("#5C5D5C"));
        ((TextView) findViewById(R.id.nav_activity_text)).setTextColor(Color.parseColor("#5C5D5C"));
        ((TextView) findViewById(R.id.nav_profile_text)).setTextColor(Color.parseColor("#5C5D5C"));
        ((TextView) findViewById(R.id.nav_groups_text)).setTextColor(Color.parseColor("#5C5D5C"));
    }

    public void onHome() {
        if (getCurrentFragment().equals("HomeFragment")) {
            bus.post(new HomeNavEvent());
            return;
        }
        if (homeFragment == null)
            homeFragment = HomeFragment.newInstance();
        pushFragment(homeFragment);
    }

    public void onActivity() {
        if (getCurrentFragment().equals("ActivityFragment")) {
            bus.post(new ActivityNavEvent());
            return;
        }
        if (activityFragment == null)
            activityFragment = ActivityFragment.newInstance();
        pushFragment(activityFragment);
    }

    public void onProfile() {
        if (getCurrentFragment().equals("MyProfileFragment")) {
            bus.post(new ProfileNavEvent());
            return;
        }
        if (myProfileFragment == null)
            myProfileFragment = MyProfileFragment.newInstance();
        pushFragment(myProfileFragment);
    }

    public void onGroups() {
        if (getCurrentFragment().equals("SocialFragment")) {
            bus.post(new GroupNavEvent());
            return;
        }
        if (socialFragment == null)
            socialFragment = SocialFragment.newInstance();
        if (checkFragment(socialFragment))
            pushFragment(socialFragment);
    }

    private void onSettings() {
        if (settingsFragment == null)
            settingsFragment = new SettingsFragment();
        pushFragment(settingsFragment);
    }

    private void onCreateGroup() {
        pushFragment(AddGroupFragment.newInstance());
    }

    private void onAddPrediction() {
        resetNavIcons();
        AddPredictionFragment fragment = AddPredictionFragment.newInstance(currentGroup);
        pushFragment(fragment);
    }

    private void onSearch() {
        if (searchFragment == null)
            searchFragment = new SearchFragment();
        pushFragment(searchFragment);
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
            MyProfileFragment fragment = MyProfileFragment.newInstance();
            pushFragment(fragment);
        } else {
            AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(id);
            pushFragment(fragment);
        }
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

    public void doLogin() {
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

    public void refreshUser() {

        if (connectivityManager == null)
            return;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            userManager.refreshUser(new NetworkCallback<User>() {
                @Override
                public void completionHandler(User object, ServerError error) {
                    handler.postDelayed(userRefreshRunnable, userRefreshInterval);
                }
            });
        } else {
            if (!userDialogShown) {
                userDialogShown = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Network Connectivity")
                        .setMessage("You have lost internet connectivity. Retry connecting?")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userDialogShown = false;
                                handler.postDelayed(userRefreshRunnable, userRefreshInterval);
                                handler.postDelayed(activitiesRefreshRunnable, userRefreshInterval);
                            }
                        })
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userDialogShown = false;
                                refreshUser();
                                refreshActivities();
                            }
                        })
                        .create().show();
            }

        }

    }

    public void refreshActivities() {
        if (connectivityManager == null)
            return;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            networkingManager.getUnseenActivityItems(null, new NetworkListCallback<ActivityItem>() {
                @Override
                public void completionHandler(ArrayList<ActivityItem> object, ServerError error) {
                    if (error != null) {

                    } else {
                        if (object != null && object.size() > 0) {
                            setActivitiesDot(false);
                        }
                        handler.postDelayed(activitiesRefreshRunnable, userRefreshInterval);
                    }

                }
            });
        }

    }

    public void setActivitiesDot(boolean seen) {
        if (seen)
            activityDot.setVisibility(View.INVISIBLE);
        else
            activityDot.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getFragmentManager().getBackStackEntryCount() <= 1) {
            System.out.println("Stack: " + getFragmentManager().getBackStackEntryCount());
            finish();
            return true;
        } else {
            System.out.println("Key Down");
            return super.onKeyDown(keyCode, event);
        }
    }

    public void hideNavbar() {
        navbar.setLayoutParams(navbarHidden);
        container.setLayoutParams(containerFull);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showNavbar() {
        navbar.setLayoutParams(navbarShown);
        container.setLayoutParams(containerPartial);
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private String getCurrentFragment() {
        if (getFragmentManager().getBackStackEntryCount() == 0)
            return "";
        else {
            return getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
        }

    }

    private void createTransparentBackground() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point size = new Point();
        display.getSize(size);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(getResources().getColor(R.color.knodaLightGreenTransparent));

        RenderScriptGaussianBlur blur = new RenderScriptGaussianBlur(RenderScript.create(this));
        bitmap = blur.blur(15, bitmap);
        blurredBackground = new BitmapDrawable(getResources(), Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()));

    }

    public boolean isDebuggable(Context ctx) {
        boolean debuggable = false;

        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (int i = 0; i < signatures.length; i++) {
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            //debuggable variable will remain false
        } catch (CertificateException e) {
            //debuggable variable will remain false
        }
        return debuggable;
    }


}
