package com.knoda.knoda.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import managers.SharedPrefManager;
import views.core.MainActivity;

public class SharedPrefManagerTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private SharedPrefManager sharedPrefManager;
    private Solo solo;

    public SharedPrefManagerTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        sharedPrefManager = mainActivity.sharedPrefManager;
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        mainActivity.networkingManager.stopCalls();
        solo.finishOpenedActivities();
    }

    public void testPreconditions() {
        assertNotNull(sharedPrefManager);
    }

    public void testAddRemoveKey() {
        sharedPrefManager.saveObjectString("", "test");
        assertNotNull(sharedPrefManager.getObjectString("test"));

        sharedPrefManager.deleteKey("test");
        assertNull(sharedPrefManager.getObjectString("test"));
    }

    public void testFirstLaunch() {
        sharedPrefManager.deleteKey(SharedPrefManager.FIRST_LAUNCH_KEY);
        assertTrue(sharedPrefManager.getFirstLaunch());
    }


}